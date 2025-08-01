package com.kp.backend.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - Core security component that processes JWT tokens.
 *
 * Responsibilities:
 * - Intercepts every HTTP request
 * - Extracts and validates JWT tokens
 * - Sets Spring Security authentication context
 * - Protects API endpoints
 *
 * Security Features:
 * - Stateless authentication
 * - Token validation
 * - Context propagation
 */
@Component // Marks as Spring-managed component
@RequiredArgsConstructor // Lombok: Generates constructor for final dependencies
public class JwtAuthFilter extends OncePerRequestFilter {
     
     // Service for JWT operations (token creation/validation)
     private final JwtUtil jwtUtil;
     
     // Service for loading user details from database
     private final UserDetailsService userDetailsService;
     
     /**
      * Core filter method executed for every request.
      *
      * Flow:
      * 1. Check for Authorization header
      * 2. Extract and validate JWT
      * 3. Load user details
      * 4. Set security context
      * 5. Continue filter chain
      *
      * @param request HTTP request
      * @param response HTTP response
      * @param filterChain Remaining filter chain
      */
     @Override
     protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain) throws ServletException, IOException {
          
          // Extract Authorization header
          final String authHeader = request.getHeader("Authorization");
          final String jwtToken;
          final String username;
          
          // Skip JWT processing if no Bearer token present
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               filterChain.doFilter(request, response);
               return;
          }
          
          // Extract token (remove "Bearer " prefix)
          jwtToken = authHeader.substring(7);
          
          // Extract username from token claims
          username = jwtUtil.extractUsername(jwtToken);
          
          // Proceed only if username exists and no existing authentication
          if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               
               // Load complete user details from database
               UserDetails userDetails = userDetailsService.loadUserByUsername(username);
               
               // Validate token against user details
               if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                         userDetails, // Principal
                         null, // Credentials (null since using JWT)
                         userDetails.getAuthorities() // Granted authorities
                    );
                    
                    // Add request details (IP, session ID etc.)
                    authToken.setDetails(
                         new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
               }
          }
          
          // Continue processing the request
          filterChain.doFilter(request, response);
     }
     
     /**
      * Security Considerations:
      * - Token validation includes expiration check
      * - No sensitive information in tokens
      * - Context cleared after request
      *
      * Production Enhancements:
      * - Token blacklisting for logout
      * - Rate limiting
      * - Token refresh mechanism
      * - Detailed audit logging
      */
}

