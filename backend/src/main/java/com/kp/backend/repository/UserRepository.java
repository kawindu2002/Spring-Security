package com.kp.backend.repository;

import com.kp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA Repository interface for User entity operations.
 * Provides CRUD operations and custom query methods for User management.
 *
 * Extends JpaRepository which includes:
 * - Standard CRUD operations (save, findById, findAll, delete, etc.)
 * - Pagination and sorting support
 * - Batch operations
 *
 * @param<User> The entity type this repository manages
 * @param<Long> The type of the entity's primary key
 */
public interface UserRepository extends JpaRepository<User, Long> {
     
     /**
      * Custom query method to find a user by username.
      *
      * @param username The username to search for (case-sensitive)
      * @return Optional<User> containing:
      *         - User object if found
      *         - Empty Optional if no user found
      *
      * Method Naming Convention:
      * - Spring Data JPA automatically implements this method
      * - Query derived from method name (findBy + field name)
      *
      * Usage Example:
      * Optional<User> user = userRepository.findByUsername("john_doe");
      *
      * Security Considerations:
      * - Used during authentication to load user details
      * - Should be combined with proper access control
      */
     Optional<User> findByUsername(String username);
     
     // Additional query methods can be added following the same pattern:
     // Optional<User> findByEmail(String email);
     // List<User> findByRole(Role role);
     // boolean existsByUsername(String username);
}


//Optional<User> is used for safety:

//If user is found → You get Optional.of(user)
//If user is not found → You get Optional.empty() (instead of null, which can crash your app if not handled)
//So you’re forced to check if the value exists before using it.


// Where to Use It?

//Login/authentication logic (Spring Security UserDetailsService)
//Validating unique usernames before registration
//Admin searching for users


