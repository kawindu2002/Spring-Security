package com.kp.backend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT Utility Class - Handles all JSON Web Token operations.
 *
 * Responsibilities:
 * - Token generation
 * - Token parsing and validation
 * - Claims extraction
 *
 * Security Features:
 * - HMAC-SHA256 signing
 * - Configurable expiration
 * - Secure key handling
 */
@Component // Marks as Spring-managed bean for dependency injection
public class JwtUtil {
     
     // Token expiration time in milliseconds (e.g., 86400000 = 24 hours)
     @Value("${jwt.expiration}")
     private long expiration;
     
     // Secret key for signing/verifying tokens (minimum 256-bit recommended)
     @Value("${jwt.secretKey}")
     private String secretKey;
     
     /* ==============================================
      * TOKEN GENERATION
      * ============================================== */
     
     /**
      * Generates a signed JWT token for authenticated users.
      *
      * @param username Subject claim (user identifier)
      * @return Signed JWT string
      *
      * Token Contents:
      * - Subject: Username
      * - IssuedAt: Creation timestamp
      * - Expiration: Calculated from configured duration
      * - Signature: HMAC-SHA256
      *
      * Security Considerations:
      * - Never include sensitive data in tokens
      * - Keep expiration times reasonable
      * - Use strong secret keys
      */
     public String generateToken(String username) {
          return Jwts.builder()
               .setSubject(username) // Identifies the principal
               .setIssuedAt(new Date()) // Token creation time
               .setExpiration(new Date(System.currentTimeMillis() + expiration))
               .signWith(
                    Keys.hmacShaKeyFor(secretKey.getBytes()), // Converts string to secure key
                    SignatureAlgorithm.HS256 // Recommended symmetric algorithm
               )
               .compact(); // Generates final token string
     }
     
     /* ==============================================
      * TOKEN PARSING
      * ============================================== */
     
     /**
      * Extracts the username (subject) from a valid JWT.
      *
      * @param token JWT string
      * @return Username from subject claim
      * @throws io.jsonwebtoken.JwtException if token is invalid
      */
     public String extractUsername(String token) {
          return Jwts.parserBuilder()
               .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())) // Same key as signing
               .build()
               .parseClaimsJws(token) // Verifies signature
               .getBody()
               .getSubject(); // Extracts the username
     }
     
     /* ==============================================
      * TOKEN VALIDATION
      * ============================================== */
     
     /**
      * Validates a JWT's integrity and expiration.
      *
      * @param token JWT string to validate
      * @return true if valid, false if expired or tampered
      *
      * Checks Performed:
      * 1. Correct signature
      * 2. Not expired
      * 3. Proper structure
      */
     public boolean validateToken(String token) {
          try {
               Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token); // Throws exceptions if invalid
               return true;
          } catch (Exception e) {
               // Log validation failures in production
               return false;
          }
     }
     
     /* ==============================================
      * SECURITY BEST PRACTICES
      * ============================================== */
     
     /**
      * Production Recommendations:
      *
      * 1. Key Rotation:
      *    - Implement periodic key rotation
      *    - Maintain old keys during transition
      *
      * 2. Enhanced Validation:
      *    - Check token against revocation list
      *    - Validate issuer/audience claims
      *
      * 3. Monitoring:
      *    - Log validation failures
      *    - Alert on suspicious patterns
      *
      * 4. Algorithm Security:
      *    - Consider RS256 for asymmetric signing
      *    - Keep JJWT library updated
      */
}

