package com.spring.ims.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.ims.controllers.AuthController;
import com.spring.ims.models.ERole;
import com.spring.ims.models.Role;
import com.spring.ims.models.User;
import com.spring.ims.payload.request.LoginRequest;
import com.spring.ims.payload.request.SignupRequest;
import com.spring.ims.payload.response.JwtResponse;
import com.spring.ims.security.services.AuthenticationService;


@WebMvcTest(AuthController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private static ObjectMapper objectMapper;

    
    @InjectMocks
	private AuthController authController;
    
    @BeforeEach
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}
    
    @Test
    public void testRegisterUser() throws Exception {
        
        // Create a new SignupRequest object with valid data
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("testpassword");
        signupRequest.setRole(Collections.singleton("user"));
        System.out.println(signupRequest.getRole());

       
        User user = new User();
        user.setId(1L);
        user.setEmail(signupRequest.getEmail());
        user.setUsername(signupRequest.getUsername());
        Role role = new Role();
        role.setId(2);
        role.setName(ERole.ROLE_USER);
        Set<Role> set = new HashSet<>();
        set.add(role);
        user.setRoles(set);

        // Converting the signUp Request to JSON format
        String json = new ObjectMapper().writeValueAsString(signupRequest);

        when(authenticationService.registerUser(any(SignupRequest.class))).thenReturn(user);

        // Making a POST request to the "/api/auth/signup" endpoint 
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        
         // Checking the response body
         String responseBody = mvcResult.getResponse().getContentAsString();
         User addedUser = new ObjectMapper().readValue(responseBody, User.class);

         assertEquals(signupRequest.getUsername(), addedUser.getUsername());

    }
    
    @Test
    public void authenticateUserTest() throws Exception {
    	
    	LoginRequest loginRequest = new LoginRequest("testuser", "12345678");
    	
    	 Map<String, Object> expectedOutput = new HashMap<>();
         expectedOutput.put("jwt", "testjwttoken");
         expectedOutput.put("id", 1L);
         expectedOutput.put("name", "testuser");
         expectedOutput.put("email", "test@gmail.com");
         List<String> myList = new ArrayList<>();
         myList.add("ROLE_USER");
         expectedOutput.put("roles", myList);
    	
    	// Converting the issue to JSON format
        String json = new ObjectMapper().writeValueAsString(loginRequest);
        
        when(authenticationService.authenticateUser(any(LoginRequest.class))).thenReturn(expectedOutput);
        
        
        // Making a POST request to the "/api/auth/signin" endpoint 
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        
        // Checking the response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        JwtResponse jwtResponse = new ObjectMapper().readValue(responseBody, JwtResponse.class);

        assertEquals(loginRequest.getUsername(), jwtResponse.getUsername());

    	
    }

}
