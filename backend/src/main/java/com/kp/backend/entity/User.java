package com.kp.backend.entity;

import com.kp.backend.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity class representing the application's user accounts.
 * Maps to a database table and serves as the core identity entity for authentication.
 *
 * Lombok annotations are used to reduce boilerplate code while maintaining clarity.
 */
@Entity // Marks this class as a JPA entity (maps to database table)
@Table(name = "user") // Explicit table name (optional - defaults to class name)
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates no-args constructor
@AllArgsConstructor // Lombok: Generates constructor with all arguments
@Builder // Lombok: Provides builder pattern for object creation
public class User {
     
     /**
      * Primary key identifier for the user.
      * Uses database identity column for auto-generation.
      */
     @Id // Marks as primary key
     @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment strategy
     private long id;
     
     /**
      * Unique username for authentication.
      * Should be validated for uniqueness at service layer.
      *
      * Note: Consider adding @Column(unique=true) for database enforcement
      */
     private String username;
     
     /**
      * Hashed password for security.
      * Should always be stored encrypted (handled by AuthService).
      *
      * Security Note:
      * - Never store plain text passwords
      * - Consider adding @Transient for password confirmation fields
      */
     private String password;
     
     /**
      * User role defining authorization level.
      * Stored as String in database via Enum conversion.
      *
      * @see com.kp.backend.enums.Role
      */
     @Enumerated(EnumType.STRING) // Stores enum name rather than ordinal
     private Role role;
     
     // Additional fields that could be considered:
     // private String email;
     // private boolean enabled;
     // private LocalDateTime createdAt;
     // private LocalDateTime lastLogin;
     
     /**
      * Security Best Practices Not Implemented:
      * - Account status flags (enabled/locked/expired)
      * - Credential expiration
      * - Login audit fields
      * - Password reset tokens
      *
      * Production Recommendations:
      * - Add validation constraints (@Size, @Pattern)
      * - Implement proper timestamp auditing
      * - Consider separate credentials entity
      */
}

