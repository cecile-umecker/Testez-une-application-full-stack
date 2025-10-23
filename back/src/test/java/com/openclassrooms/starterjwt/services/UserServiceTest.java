package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserService Unit Test Suite
 * 
 * This test file contains unit tests for the UserService.
 * The UserService handles business logic for user management including retrieval and deletion operations.
 * Tests use mocked dependencies to isolate service logic.
 * 
 * Test Coverage:
 * 
 * FindById Tests:
 * - findById_shouldReturnUser_whenExists: Tests retrieving specific user by ID,
 *   verifies UserRepository.findById() is called with correct ID (1L),
 *   confirms correct user data is returned (id, email)
 * 
 * - findById_shouldReturnNull_whenNotExists: Tests null is returned for non-existent user ID,
 *   verifies UserRepository.findById() is called with ID (99L),
 *   confirms null is returned when Optional.empty()
 * 
 * Delete Tests:
 * - delete_shouldCallRepositoryDeleteById: Tests user deletion,
 *   verifies UserRepository.deleteById() is called with correct ID (1L)
 * 
 * UserService Methods Tested:
 * - findById(Long): Finds specific user by ID, returns null if not found
 * - delete(Long): Removes user from database by calling repository deleteById
 * 
 * Mocked Dependencies:
 * - UserRepository: Database operations for users
 * 
 * Test Configuration:
 * - Uses Mockito for mocking repository (@Mock, @InjectMocks)
 * - MockitoAnnotations.openMocks() called in @BeforeEach to initialize mocks
 * - Uses JUnit Jupiter assertions (assertNotNull, assertEquals, assertNull)
 * - Verifies repository method calls using Mockito.verify()
 */

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("user@test.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenNotExists() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        User result = userService.findById(99L);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }
}
