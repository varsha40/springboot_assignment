package com.spring.ims.security.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.ims.IConstants.IConstants;
import com.spring.ims.models.ERole;
import com.spring.ims.models.Role;
import com.spring.ims.models.User;
import com.spring.ims.payload.request.LoginRequest;
import com.spring.ims.payload.request.SignupRequest;
import com.spring.ims.payload.response.JwtResponse;
import com.spring.ims.payload.response.MessageResponse;
import com.spring.ims.repository.RoleRepository;
import com.spring.ims.repository.UserRepository;
import com.spring.ims.security.jwt.JwtUtils;

@Service
public class AuthenticationService {
    
    @Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtUtils jwtUtils;
	
	/**
	 * This method authenticates the user and generates the JWT Token
	 * 
	 * @param loginRequest
	 * 
	 * @return {@link  ResponseEntity<?>}
	 */
	public Map<String, Object> authenticateUser(LoginRequest loginRequest) {
		
		// Calls internally the authentication provider to authenticate the request
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		// Sets security context details
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// Generates JWT token
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		// Gets the list of roles for the current user
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		// Set values to map
		Map<String, Object> map = new HashMap<>();
		map.put("jwt", jwt);
		map.put("id", userDetails.getId());
		map.put("name", userDetails.getUsername());
		map.put("email", userDetails.getEmail());
		map.put("roles", roles);

		return map;

	}
	
	/**
	 * This method is used to create new User
	 * 
	 * @param signupRequest
	 * 
	 * @return {@link ResponseEntity<?> }
	 */
	public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
		
		// Checks if username already exists
		if (userRepository.existsByUsername(signupRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		
		// Checks if email already exists
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signupRequest.getUsername(), signupRequest.getEmail(),
				encoder.encode(signupRequest.getPassword()));

		Set<String> strRoles = signupRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			
			// Fetches USER role from DB
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(IConstants.ROLE_NOT_FOUND));
			roles.add(userRole);
		} else {
			
			// Sets role for the new user
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException(IConstants.ROLE_NOT_FOUND));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException(IConstants.ROLE_NOT_FOUND));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		
		// Saves the new user data in DB
		userRepository.save(user);
		
		return ResponseEntity.ok(new MessageResponse(IConstants.USER_REGISTERED_SUCCESSFULLY));
	}
}
