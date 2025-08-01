package com.kp.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demonstration controller showcasing role-based endpoint security.
 * All endpoints in this controller require valid JWT authentication.
 * Each endpoint demonstrates different role-based access control.
 */
@RestController // Identifies this class as a Spring MVC controller with request mapping methods
@RequestMapping("/hello") // Base path for all endpoints in this controller
public class HelloController {
     
     /**
      * Example endpoint accessible only to users with USER role.
      * Demonstrates role-based authorization at the method level.
      *
      * @return String greeting for authenticated users with USER role
      *
      * Security:
      * - Requires valid JWT token in Authorization header
      * - Token must contain ROLE_USER authority
      *
      * Endpoint: GET /hello/user
      * Access: ROLE_USER required
      */
     @GetMapping("/user")
     @PreAuthorize("hasRole('USER')") // Spring Security method-level authorization
     public String helloUser() {
          return "Hello user";
     }
     
     /**
      * Example endpoint accessible only to users with ADMIN role.
      * Demonstrates higher privilege role requirement.
      *
      * @return String greeting for authenticated users with ADMIN role
      *
      * Security:
      * - Requires valid JWT token in Authorization header
      * - Token must contain ROLE_ADMIN authority
      *
      * Endpoint: GET /hello/admin
      * Access: ROLE_ADMIN required
      */
     @GetMapping("/admin")
     @PreAuthorize("hasRole('ADMIN')") // Restricts access to ADMIN role only
     public String helloAdmin() {
          return "Hello admin";
     }
}

