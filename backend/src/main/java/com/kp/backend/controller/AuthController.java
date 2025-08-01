package com.kp.backend.controller;

import com.kp.backend.dto.ApiResponse;
import com.kp.backend.dto.AuthDTO;
import com.kp.backend.dto.RegisterDTO;
import com.kp.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - Handles user registration and authentication endpoints.
 * Exposes REST APIs for user management and authentication flows.
 */
@RestController // Marks this class as a Spring MVC controller with request mapping methods
@RequestMapping("/auth") // Base path for all endpoints in this controller
@CrossOrigin("*") // Allows cross-origin requests from any domain (adjust for production)
@RequiredArgsConstructor // Lombok: Generates constructor for final fields (dependency injection)
public class AuthController {
     
     // Service layer dependency for handling authentication business logic
     private final AuthService authService;
     
     /**
      * Handles user registration requests.
      *
      * @param registerDTO Data Transfer Object containing user registration details
      * @return ResponseEntity containing:
      *         - HTTP status code
      *         - Success message
      *         - Registered user data or authentication token
      *
      * Endpoint: POST /auth/register
      * Request Body: RegisterDTO (username, password, email, etc.)
      * Response: Standardized ApiResponse format
      */
     @PostMapping("/register")
     public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterDTO registerDTO) {
          return ResponseEntity.ok(new ApiResponse(
               200, // HTTP status code equivalent (OK)
               "User registered Successfully",
               authService.register(registerDTO) // Delegates to service layer
          );
     }
     
     /**
      * Handles user authentication (login) requests.
      *
      * @param authDTO Data Transfer Object containing login credentials
      * @return ResponseEntity containing:
      *         - HTTP status code
      *         - Success message
      *         - Authentication token or user details
      *
      * Endpoint: POST /auth/login
      * Request Body: AuthDTO (typically username/email and password)
      * Response: Standardized ApiResponse format with JWT token
      */
     @PostMapping("/login")
     public ResponseEntity<ApiResponse> login(@RequestBody AuthDTO authDTO) {
          return ResponseEntity.ok(new ApiResponse(
               200, // HTTP status code equivalent (OK)
               "User logged in Successfully",
               authService.authenticate(authDTO) // Delegates to service layer
          ));
     }
}