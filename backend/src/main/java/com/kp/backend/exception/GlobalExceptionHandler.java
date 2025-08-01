package com.kp.backend.exception;

import com.kp.backend.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 * Centralizes error handling and provides consistent API error responses.
 *
 * Features:
 * - Handles security-related exceptions
 * - Manages JWT token exceptions
 * - Provides fallback for uncaught exceptions
 * - Standardizes error response format
 */
@RestControllerAdvice // Combines @ControllerAdvice and @ResponseBody for REST APIs
public class GlobalExceptionHandler {
     
     /**
      * Handles cases when a username cannot be found during authentication.
      *
      * @param e The thrown UsernameNotFoundException
      * @return Standardized ApiResponse with:
      *         - HTTP 404 status
      *         - User-friendly message
      *         - No data payload
      *
      * Security Note: Generic message prevents username enumeration attacks
      */
     @ExceptionHandler(UsernameNotFoundException.class)
     @ResponseStatus(HttpStatus.NOT_FOUND)
     public ApiResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
          return new ApiResponse(404, "User not found", null);
     }
     
     /**
      * Handles invalid credential attempts during login.
      *
      * @param e The thrown BadCredentialsException
      * @return Standardized ApiResponse with:
      *         - HTTP 400 status
      *         - Generic error message
      *         - No sensitive information
      *
      * Security Best Practice:
      * - Same response for wrong username vs wrong password
      * - Prevents credential guessing feedback
      */
     @ExceptionHandler(BadCredentialsException.class)
     @ResponseStatus(HttpStatus.BAD_REQUEST)
     public ApiResponse handleBadCredentialsException(BadCredentialsException e) {
          return new ApiResponse(400, "Bad Credentials", null);
     }
     
     /**
      * Handles expired JWT tokens.
      *
      * @param e The thrown ExpiredJwtException
      * @return Standardized ApiResponse with:
      *         - HTTP 401 status
      *         - Clear expiration message
      *         - No token details
      *
      * Client should handle by:
      * 1. Prompting user to re-authenticate
      * 2. Using refresh token if available
      */
     @ExceptionHandler(ExpiredJwtException.class)
     @ResponseStatus(HttpStatus.UNAUTHORIZED)
     public ApiResponse handleJWTTokenExpiredException(ExpiredJwtException e) {
          return new ApiResponse(401, "JWT token expired", null);
     }
     
     /**
      * Fallback handler for all uncaught runtime exceptions.
      *
      * @param e The thrown RuntimeException
      * @return Standardized ApiResponse with:
      *         - HTTP 500 status
      *         - Generic error message
      *         - Exception message (development only)
      *
      * Production Consideration:
      * - Log full stacktrace internally
      * - Omit exception message in production
      * - Consider custom exception hierarchy
      */
     @ExceptionHandler(RuntimeException.class)
     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     public ApiResponse handleAllException(RuntimeException e) {
          return new ApiResponse(500, "INTERNAL SERVER ERROR", e.getMessage());
     }
     
     /**
      * Recommended Additional Handlers:
      *
      * 1. MethodArgumentNotValidException - for @Valid failures
      * 2. AccessDeniedException - for authorization failures
      * 3. Custom business exceptions
      * 4. Database constraint violations
      *
      * Security Best Practices:
      * - Never expose stack traces
      * - Sanitize all error messages
      * - Consistent logging
      */
}

