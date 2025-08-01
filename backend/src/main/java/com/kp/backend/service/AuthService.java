package com.kp.backend.service;

import com.kp.backend.dto.AuthDTO;
import com.kp.backend.dto.AuthResponseDTO;
import com.kp.backend.dto.RegisterDTO;
import com.kp.backend.entity.User;
import com.kp.backend.enums.Role;
import com.kp.backend.repository.UserRepository;
import com.kp.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Core authentication service handling user registration and login processes.
 * Manages:
 * - User credential validation
 * - Password encoding
 * - JWT token generation
 * - User account creation
 */

@Service // Marks this class as a Spring service component
@RequiredArgsConstructor // Lombok: Generates constructor for final fields (dependency injection)
public class AuthService {
     
     // Repository for database operations on User entities
     private final UserRepository userRepository;
     
     // Password encoder for secure password hashing and verification
     private final PasswordEncoder passwordEncoder;
     
     // Utility for JWT token generation and validation
     private final JwtUtil jwtUtil;
     
     /**
      * Authenticates a user and generates JWT token upon successful login.
      *
      * @param authDTO Contains username and password credentials
      * @return AuthResponseDTO containing JWT token
      * @throws RuntimeException if username not found
      * @throws BadCredentialsException if password doesn't match
      
      * Flow:
      * 1. Find user by username
      * 2. Verify password matches stored hash
      * 3. Generate and return JWT token
      */
     
     public AuthResponseDTO authenticate(AuthDTO authDTO) {
          // Find user or throw exception
          User user = userRepository.findByUsername(authDTO.getUsername())
               .orElseThrow(() -> new RuntimeException("Username not found"));
          
          // Verify password against stored hash
          if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
               throw new BadCredentialsException("Invalid credentials");
          }
          
          // Generate JWT token for authenticated user
          String token = jwtUtil.generateToken(user.getUsername());
          return new AuthResponseDTO(token);
     }
     
     /**
      * Registers a new user account with encoded password.
      *
      * @param registerDTO Contains registration details (username, password, role)
      * @return Success message
      * @throws RuntimeException if username already exists
      *
      * Flow:
      * 1. Check username availability
      * 2. Encode password
      * 3. Create and save user entity
      */
     public String register(RegisterDTO registerDTO) {
          // Check for existing username
          if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
               throw new RuntimeException("Username already in use");
          }
          
          // Build new user entity with encoded password
          User user = User.builder()
               .username(registerDTO.getUsername())
               .password(passwordEncoder.encode(registerDTO.getPassword())) // 🔒 Secure hashing
               .role(Role.valueOf(registerDTO.getRole())) // Convert string to enum
               .build();
          
          // Persist new user
          userRepository.save(user);
          return "User registered successfully";
     }
     
     /**
      * Production Considerations:
      * - Add email verification
      * - Implement password strength validation
      * - Add account locking after failed attempts
      * - Include user activity logging
      * - Add CAPTCHA for registration
      *
      * Security Enhancements:
      * - Store password version for hash upgrades
      * - Add salt to password encoding
      * - Implement token invalidation
      */
}