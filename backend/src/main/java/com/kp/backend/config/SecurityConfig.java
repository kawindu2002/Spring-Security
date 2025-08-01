package com.kp.backend.config;

import com.kp.backend.util.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main security configuration class for Spring Security setup.
 * Configures authentication, authorization, and JWT filter integration.
 */
@Configuration  // Marks this class as a Spring configuration class
@EnableMethodSecurity  // Enables method-level security with @PreAuthorize, @PostAuthorize etc.
@RequiredArgsConstructor  // Lombok: Generates constructor for final fields (dependency injection)
public class SecurityConfig {
     
     // Core service for loading user details during authentication
     private final UserDetailsService userDetailsService;
     
     // Custom JWT authentication filter for processing JWT tokens
     private final JwtAuthFilter jwtAuthFilter;
     
     // Password encoder bean (typically BCrypt) for password operations
     private final PasswordEncoder passwordEncoder;
     
     /**
      * Configures the security filter chain - the core of Spring Security setup.
      * Defines:
      * - Request authorization rules
      * - CSRF protection settings
      * - Session management policy
      * - Authentication provider
      * - JWT filter integration
      *
      * @param http HttpSecurity configuration builder
      * @return Configured SecurityFilterChain
      * @throws Exception if configuration fails
      */
     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http
               // Disable CSRF protection (typically safe for stateless JWT APIs)
               .csrf(AbstractHttpConfigurer::disable)
               
               // Configure request authorization rules
               .authorizeHttpRequests(auth -> auth
                    // Allow unauthenticated access to authentication endpoints
                    .requestMatchers("/auth/**").permitAll()
                    // All other requests require authentication
                    .anyRequest().authenticated()
               )
               
               // Configure stateless session management (no session created)
               .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               )
               
               // Set our custom authentication provider
               .authenticationProvider(authenticationProvider())
               
               // Add our JWT filter before the default username/password filter
               .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
          
          return http.build(); // Build and return the security configuration
     }
     
     /**
      * Creates and configures the authentication provider.
      * Uses DaoAuthenticationProvider which:
      * - Loads user details via UserDetailsService
      * - Validates passwords using PasswordEncoder
      *
      * @return Configured AuthenticationProvider
      */
     private AuthenticationProvider authenticationProvider() {
          // Create DAO-based authentication provider
          DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
          
          // Set the user details service for loading user data
          provider.setUserDetailsService(userDetailsService);
          
          // Set the password encoder for credential validation
          provider.setPasswordEncoder(passwordEncoder);
          
          return provider;
     }
}
