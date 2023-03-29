package com.spring.ims.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ims.models.User;
import com.spring.ims.payload.request.LoginRequest;
import com.spring.ims.payload.request.SignupRequest;
import com.spring.ims.payload.response.JwtResponse;
import com.spring.ims.security.services.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  @Autowired
  private AuthenticationService authenticateUserService;
  

  /**
   * This API authenticates the user request
   * 
   * @RequestBody loginRequest
   * 
   * @return {@link ResponseEntity<?>}
   */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
	  
    

	Map<String, Object> map =  authenticateUserService.authenticateUser(loginRequest);
	  
	ObjectMapper objectMapper = new ObjectMapper();
    String jwt =  objectMapper.convertValue(map.get("jwt"), String.class);
    Long id =  objectMapper.convertValue(map.get("id"), Long.class);
    String name =  objectMapper.convertValue(map.get("name"), String.class);
    String email =  objectMapper.convertValue(map.get("email"), String.class);
    List<String> roles = new ObjectMapper().convertValue(map.get("roles"), new TypeReference<List<String>>() {});

    // Returns user details with the generated JWT token
		return ResponseEntity.ok(
      new JwtResponse(jwt, id, name, email, roles));
      
  }

  /**
	 * This method is used to create new User
	 * 
	 * @param signupRequest
	 * 
	 * @return {@link ResponseEntity<?> }
	 */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
	  
    // Calling register user service to register new user
    User user = authenticateUserService.registerUser(signUpRequest);
    
    if(user != null){

      return new ResponseEntity<>(user, HttpStatus.OK);
    }
    // If null, returns error response
		return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
   
    
  }
}
