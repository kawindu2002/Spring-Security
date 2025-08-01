package com.kp.backend.config;

import com.kp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration class for setting up essential security components.
 * This class defines beans required for authentication and password encoding.
 */
@Configuration  // Indicates this class contains Spring bean definitions
@RequiredArgsConstructor  // Lombok: Generates constructor for final fields (dependency injection)
public class ApplicationConfig {
     
     // Repository for accessing user data from the database
     // Injected automatically by Spring due to @RequiredArgsConstructor
     private final UserRepository userRepo;
     
     /**
      * Defines the UserDetailsService bean which is a core component of Spring Security.
      * This service is responsible for loading user-specific data during authentication.
      *
      * @return UserDetailsService implementation that fetches users from our repository
      *
      * How it works:
      * 1. Spring Security calls this service with the username during login
      * 2. We query our database using UserRepository
      * 3. Map our application's User entity to Spring Security's UserDetails object
      * 4. Convert the role to Spring Security's GrantedAuthority format
      */
     @Bean  // Marks this method as producing a bean managed by Spring
     public UserDetailsService userDetailsService() {
          // Lambda implementation of UserDetailsService
          return username -> userRepo.findByUsername(username)
               // If user exists, map to Spring Security's User object
               .map(user -> new User(
                    user.getUsername(),  // Username for authentication
                    user.getPassword(),  // Encoded password for verification
                    // Convert role to Spring Security authority
                    // Note: Spring expects roles to be prefixed with "ROLE_"
                    List.of(new SimpleGrantedAuthority(
                         "ROLE_"+user.getRole().name()
                    ))
               ))
               // If user not found, throw exception (will be translated to BadCredentials)
               .orElseThrow(
                    () -> new RuntimeException("User not found")
               );
     }
     
     /**
      * Defines the PasswordEncoder bean used for password hashing and verification.
      * BCrypt is currently the recommended password hashing algorithm.
      *
      * Features of BCrypt:
      * - Automatically generates and manages salts
      * - Includes work factor (strength) parameter
      * - Provides constant-time comparison to prevent timing attacks
      *
      * @return BCryptPasswordEncoder instance for password operations
      */
     @Bean
     public PasswordEncoder passwordEncoder() {
          // Create BCrypt encoder with default strength (10)
          return new BCryptPasswordEncoder();
          
          // Note: For production, you might want to:
          // 1. Make the strength configurable via properties
          // 2. Consider using Argon2 if you need stronger security
     }
}

