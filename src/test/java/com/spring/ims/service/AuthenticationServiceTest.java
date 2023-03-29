package com.spring.ims.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.spring.ims.IConstants.IConstants;
import com.spring.ims.exception.EmailAlreadyExistsException;
import com.spring.ims.exception.UserNameAlreadyExistsException;
import com.spring.ims.models.ERole;
import com.spring.ims.models.Role;
import com.spring.ims.models.User;
import com.spring.ims.payload.request.LoginRequest;
import com.spring.ims.payload.request.SignupRequest;
import com.spring.ims.repository.RoleRepository;
import com.spring.ims.repository.UserRepository;
import com.spring.ims.security.jwt.JwtUtils;
import com.spring.ims.security.services.AuthenticationService;
import com.spring.ims.security.services.UserDetailsImpl;

class AuthenticationServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private AuthenticationService authenticationService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private PasswordEncoder encoder;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testAuthenticateUser() {

        List<SimpleGrantedAuthority>  simpleGrantedAuthority = new ArrayList<>();
        simpleGrantedAuthority.add(new SimpleGrantedAuthority("ROLE_USER"));

		// Mocking the authentication response
		Authentication authentication = Mockito.mock(Authentication.class);

        // Mocking the authentication manager response
		Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

		// Mocking the JWT token generation response
		Mockito.when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn("testjwttoken");

		Mockito.when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(1L, "user1", "email@test.com", "pass", simpleGrantedAuthority));

		// Mocking the expected output
        List<String> myList = new ArrayList<>();
        myList.add("ROLE_USER");
		Map<String, Object> expectedOutput = new HashMap<>();
        expectedOutput.put("jwt", "testjwttoken");
        expectedOutput.put("id", 1L);
        expectedOutput.put("name", "user1");
        expectedOutput.put("email", "email@test.com");
        expectedOutput.put("roles", myList);
	
		LoginRequest loginRequest = new LoginRequest("user1", "pass1");

        // Calling the method to be tested
		Map<String, Object> actualOutput = authenticationService.authenticateUser(loginRequest);

		// Assert that the output matches the expected output
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test 
	public void registerUserTest() {
		
		// Creating a new SignupRequest object
		SignupRequest signupRequest = getSignupData();
		Set<String> roles = new HashSet<>();
		roles.add("admin");
		roles.add("user");
		roles.add("mod");
		signupRequest.setRole(roles);
		Set<Role> userRoles = new HashSet<>();

		// Creating a new User object 
		User user = getUserData();
		Role adminRole = new Role(ERole.ROLE_ADMIN);
		userRoles.add(adminRole);
		
		Role modRole = new Role(ERole.ROLE_MODERATOR);
		userRoles.add(modRole);
		
		Role userRole = new Role(ERole.ROLE_USER);
		userRoles.add(userRole);
		user.setRoles(userRoles);
		
		// Success case
		// Mock the userRepository and roleRepository
		when(userRepository.existsByUsername("testuser")).thenReturn(false);
		when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
		when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
		when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(Optional.of(modRole));
		when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// Calling the registerUser method
		User savedUser = authenticationService.registerUser(signupRequest);
		
		assertEquals(user.getEmail(), savedUser.getEmail());
        
        
	}
	
    @Test
    public void testRegisterUserEmailAlreadyExists() {
    	
    	SignupRequest signupRequest = getSignupData();

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
        
        // test that the service method throws the expected exception
        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
        	authenticationService.registerUser(signupRequest);
        });
        assertEquals(IConstants.EMAIL_ALREADY_EXISTS, exception.getMessage());
    }
    
    @Test
    public void testRegisterUserUserNameAlreadyExists() {
    	
        SignupRequest signupRequest = getSignupData();

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        
        // test that the service method throws the expected exception
        Exception exception = assertThrows(UserNameAlreadyExistsException.class, () -> {
        	authenticationService.registerUser(signupRequest);
        });
        assertEquals(IConstants.USERNAME_ALREADY_EXISTS, exception.getMessage());
        
    }
    
    public SignupRequest getSignupData() {
    	
    	 SignupRequest signupRequest = new SignupRequest();
         signupRequest.setUsername("testuser");
         signupRequest.setEmail("test@gmail.com");
         signupRequest.setPassword("testpassword");
         signupRequest.setRole(Collections.singleton("user"));
         
         return signupRequest;
    }
    
    public User getUserData() {
    	
    	 User existingUser = new User();
         existingUser.setUsername("testuser");
         existingUser.setEmail("test@gmail.com");
         existingUser.setPassword("testpassword");
         return existingUser;
    }
    
}


