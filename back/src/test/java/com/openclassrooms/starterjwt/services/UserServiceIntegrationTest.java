package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UserService Integration Test Suite
 * 
 * This test file contains integration tests for the UserService.
 * The UserService handles business logic for user management including retrieval and deletion operations.
 * Tests use real database interactions with cleanup after each test.
 * 
 * Test Coverage:
 * 
 * FindById Tests:
 * - findById_shouldReturnUserFromDatabase: Tests retrieving specific user by ID,
 *   verifies correct user data is returned (id, email, firstName, lastName, admin)
 * 
 * - findById_shouldReturnNull_whenUserDoesNotExistInDatabase: Tests null is returned
 *   for non-existent user ID (999L)
 * 
 * - findById_shouldReturnAdminUser: Tests retrieving admin user,
 *   verifies admin flag is correctly returned and all fields are intact
 * 
 * - findById_shouldHandleSpecialCharactersInUserData: Tests handling of special characters
 *   (accents, apostrophes, hyphens, plus signs) in user data
 *   (françois.o'connor@test.com, François, O'Connor-Müller)
 * 
 * - findById_shouldReturnUserWithAllFields: Tests all user fields are returned
 *   (id, email, firstName, lastName, password, admin, createdAt, updatedAt)
 * 
 * Delete Tests:
 * - delete_shouldRemoveUserFromDatabase: Tests user deletion,
 *   verifies user is removed from database and cannot be found afterwards
 * 
 * - delete_shouldThrowException_whenUserDoesNotExist: Tests EmptyResultDataAccessException
 *   is thrown when attempting to delete non-existent user (999L)
 * 
 * - delete_shouldRemoveMultipleUsers: Tests deleting multiple users sequentially,
 *   verifies each user is properly removed from database
 * 
 * - delete_andThenFindById_shouldReturnNull: Tests complete deletion workflow,
 *   verifies deleted user cannot be retrieved via findById()
 * 
 * UserService Methods Tested:
 * - findById(Long): Finds specific user by ID, returns null if not found
 * - delete(Long): Removes user from database, throws exception if user doesn't exist
 * 
 * Exception Handling:
 * - EmptyResultDataAccessException: Thrown when deleting non-existent user
 * 
 * Dependencies:
 * - UserRepository: Database operations for users
 * 
 * Test Configuration:
 * - @SpringBootTest: Full application context loading
 * - @Transactional: Rolls back database changes after each test
 * - @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD): Resets application context after each test
 * - @BeforeEach: Clears all users from database before each test
 * - Uses real database with cleanup to ensure test independence
 */

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void findById_shouldReturnUserFromDatabase() {
        // Arrange
        User user = User.builder()
                .email("integration@test.com")
                .firstName("Integration")
                .lastName("Test")
                .password("password123")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        // Act
        User result = userService.findById(savedUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo("integration@test.com");
        assertThat(result.getFirstName()).isEqualTo("Integration");
        assertThat(result.getLastName()).isEqualTo("Test");
        assertThat(result.isAdmin()).isFalse();
    }

    @Test
    @Transactional
    void findById_shouldReturnNull_whenUserDoesNotExistInDatabase() {
        // Act
        User result = userService.findById(999L);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @Transactional
    void delete_shouldRemoveUserFromDatabase() {
        // Arrange
        User user = User.builder()
                .email("delete@test.com")
                .firstName("Delete")
                .lastName("Me")
                .password("password123")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        assertThat(userRepository.findById(userId)).isPresent();

        // Act
        userService.delete(userId);

        // Assert
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @Transactional
    void delete_shouldThrowException_whenUserDoesNotExist() {
        // Act & Assert 
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userService.delete(999L);
        });
    }

    @Test
    @Transactional
    void findById_shouldReturnAdminUser() {
        // Arrange 
        User adminUser = User.builder()
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminpass")
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(adminUser);

        // Act
        User result = userService.findById(savedUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isAdmin()).isTrue();
        assertThat(result.getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    @Transactional
    void findById_shouldHandleSpecialCharactersInUserData() {
        // Arrange 
        User user = User.builder()
                .email("françois.o'connor@test.com")
                .firstName("François")
                .lastName("O'Connor-Müller")
                .password("password123")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        // Act
        User result = userService.findById(savedUser.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("françois.o'connor@test.com");
        assertThat(result.getFirstName()).isEqualTo("François");
        assertThat(result.getLastName()).isEqualTo("O'Connor-Müller");
    }

    @Test
    @Transactional
    void delete_shouldRemoveMultipleUsers() {
        // Arrange
        User user1 = userRepository.save(User.builder()
                .email("user1@test.com")
                .firstName("User")
                .lastName("One")
                .password("pass1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        User user2 = userRepository.save(User.builder()
                .email("user2@test.com")
                .firstName("User")
                .lastName("Two")
                .password("pass2")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        assertThat(userRepository.findById(user1.getId())).isPresent();
        assertThat(userRepository.findById(user2.getId())).isPresent();

        // Act 
        userService.delete(user1.getId());
        userService.delete(user2.getId());

        // Assert
        assertThat(userRepository.findById(user1.getId())).isEmpty();
        assertThat(userRepository.findById(user2.getId())).isEmpty();
    }

    @Test
    @Transactional
    void findById_shouldReturnUserWithAllFields() {
        // Arrange 
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .email("complete@test.com")
                .firstName("Complete")
                .lastName("User")
                .password("encryptedPassword")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        User savedUser = userRepository.save(user);

        // Act
        User result = userService.findById(savedUser.getId());

        // Assert
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo("complete@test.com");
        assertThat(result.getFirstName()).isEqualTo("Complete");
        assertThat(result.getLastName()).isEqualTo("User");
        assertThat(result.getPassword()).isEqualTo("encryptedPassword");
        assertThat(result.isAdmin()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @Transactional
    void delete_andThenFindById_shouldReturnNull() {
        // Arrange
        User user = userRepository.save(User.builder()
                .email("delete-find@test.com")
                .firstName("Delete")
                .lastName("Find")
                .password("password")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        Long userId = user.getId();

        // Act 
        userService.delete(userId);
        User result = userService.findById(userId);

        // Assert
        assertThat(result).isNull();
    }
}