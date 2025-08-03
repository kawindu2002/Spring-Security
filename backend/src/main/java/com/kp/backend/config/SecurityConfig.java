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

// =====================================================================================================================

//Spring Security Configuration Documentation
//SecurityFilterChain with JWT Authentication
//1. Overview
//This document explains the SecurityFilterChain configuration for a Spring Boot application using JWT (JSON Web Token) authentication. The configuration disables CSRF, enforces stateless session management, and integrates a custom JWT filter for authentication.
//2. Configuration Breakdown
//2.1. @Bean Annotation
//Purpose: Declares that the method produces a Spring-managed bean.
//Why Used: Spring Security automatically detects SecurityFilterChain beans to configure HTTP security.
//Default Behavior: If multiple SecurityFilterChain beans exist, Spring combines them.
//2.2. Method Signature
//public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
//SecurityFilterChain: Defines the order and behavior of security filters.
//HttpSecurity: The main builder class for configuring web security.
//throws Exception: Required because security configurations may throw exceptions (e.g., misconfiguration).
//
//3. Security Configurations
//3.1. CSRF Protection
//.csrf(AbstractHttpConfigurer::disable)
//What is CSRF?
//Cross-Site Request Forgery (CSRF) is an attack where a malicious site tricks a user into performing unwanted actions on an authenticated site.
//Why Disabled?
//Stateless APIs (using JWT) typically don’t need CSRF protection because:
//JWTs are sent in the Authorization header (not cookies).
//No session is maintained (stateless).
//Enabled by default in Spring Security for form-based login.
//
//3.2. Request Authorization Rules
//.authorizeHttpRequests(auth -> auth
//.requestMatchers("/auth/**").permitAll()
//.anyRequest().authenticated()
//)
//authorizeHttpRequests (replaces authorizeRequests in Spring Security 6+):
//Defines access control rules for HTTP requests.
//requestMatchers("/auth/**").permitAll()
//Allows unauthenticated access to endpoints under /auth/ (e.g., login, registration).
//anyRequest().authenticated()
//All other requests require authentication.
//If a user is not authenticated, Spring Security returns 401 Unauthorized.
//
//3.3. Session Management (Stateless)
//.sessionManagement(session -> session
//.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//)
//Why Stateless?
//JWTs are self-contained (no server-side session storage needed).
//Each request must include the JWT in the Authorization header.
//Alternate Policies:
//Policy				Behavior
//ALWAYS			Always creates a session
//IF_REQUIRED (default)	Creates a session only when needed
//NEVER			Never creates a session but uses an existing one
//STATELESS			No session is ever created or used
//
//3.4. Custom Authentication Provider
//.authenticationProvider(authenticationProvider())
//What is an AuthenticationProvider?
//Validates user credentials (e.g., username/password against a database).
//When Used?
//Needed if using both JWT and form-based login.
//Optional if only JWT is used (since jwtAuthFilter handles authentication).
//3.5. JWT Filter Integration
//.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//What is jwtAuthFilter?
//A custom filter that:
//Extracts the JWT from the Authorization header.
//Validates the token.
//Sets the SecurityContext if valid.
//Why Before UsernamePasswordAuthenticationFilter?
//Ensures JWT authentication happens before Spring’s default form-login filter.
//If no JWT is found, the request proceeds (may hit 401 if authentication is required).
//4. Flow of a Request
//Request arrives at the security filter chain.
//CSRF check is skipped (disabled).
//JWT Filter (jwtAuthFilter) checks for a valid token:
//✅ Valid JWT: Sets Authentication in SecurityContext.
//❌ No/Invalid JWT: Proceeds without authentication.
//Authorization Check:
//If endpoint is /auth/** → Allows access.
//For other endpoints → Checks SecurityContext for authentication.
//If unauthorized → Returns 401 Unauthorized.
//If authorized → Proceeds to the controller.
//5. When to Use This Configuration?
//✅ Recommended for:
//REST APIs (stateless backends).
//JWT-based authentication (no sessions).
//Single-Page Applications (SPA) or mobile apps.
//❌ Not recommended for:
//Traditional server-rendered apps (use sessions instead).
//Applications needing CSRF protection (e.g., form submissions with cookies).
//6. Summary
//Configuration : csrf().disable()
//Purpose : Disables CSRF protection
//Why Used? : Not needed for stateless JWT APIs
//
//Configuration : authorizeHttpRequests()
//Purpose :  Defines access rules
//Why Used? : Restricts endpoints based on authentication
//
//Configuration : sessionManagement(STATELESS)
//Purpose : No session creation
//Why Used? : JWTs are self-contained
//
//Configuration : authenticationProvider()
//Purpose : Custom auth logic
//Why Used? : Validates credentials (optional for JWT-only)
//
//Configuration : addFilterBefore(jwtAuthFilter)
//Purpose : JWT validation
//Why Used? : Authenticates requests before default login
//
//7. Further Improvements
//Rate Limiting: Add a filter to prevent brute-force attacks.
//Role-Based Access: Use .hasRole("ADMIN") for fine-grained control.
//CORS Configuration: If the API is consumed by a frontend, configure CORS.

