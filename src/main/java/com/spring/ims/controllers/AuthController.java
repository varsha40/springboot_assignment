package com.spring.ims.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ims.payload.request.LoginRequest;
import com.spring.ims.payload.request.SignupRequest;
import com.spring.ims.repository.RoleRepository;
import com.spring.ims.repository.UserRepository;
import com.spring.ims.security.jwt.JwtUtils;
import com.spring.ims.security.services.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;
  
  @Autowired
  private AuthenticationService authenticateUserService;

  @Autowired
  JwtUtils jwtUtils;

  /**
   * This API authenticates the user request
   * 
   * @RequestBody loginRequest
   * 
   * @return {@link ResponseEntity<?>}
   */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
	  
	  return authenticateUserService.authenticateUser(loginRequest);
      
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
	  
	  return authenticateUserService.registerUser(signUpRequest);
  }
}
