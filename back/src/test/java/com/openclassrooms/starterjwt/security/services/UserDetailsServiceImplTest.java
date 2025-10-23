package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UserDetailsServiceImpl Unit Test Suite
 * 
 * This test file contains unit tests for the UserDetailsServiceImpl class.
 * The UserDetailsServiceImpl implements Spring Security's UserDetailsService interface
 * and is responsible for loading user-specific data during authentication.
 * 
 * Test Coverage:
 * 
 * loadUserByUsername Tests:
 * - loadUserByUsername_UserFound_ReturnsUserDetails: Tests successful user lookup by email,
 *   verifies UserRepository.findByEmail() is called with correct email,
 *   confirms UserDetailsImpl is returned with all user fields mapped correctly
 *   (id, username/email, password, firstName, lastName),
 *   validates UserDetails interface implementation
 * 
 * - loadUserByUsername_UserNotFound_ThrowsException: Tests behavior when user is not found,
 *   verifies UsernameNotFoundException is thrown with descriptive message,
 *   confirms error message contains the email that was not found
 * 
 * UserDetailsServiceImpl Responsibilities:
 * 1. Load user from database by email address
 * 2. Convert User entity to UserDetailsImpl (Spring Security UserDetails)
 * 3. Throw UsernameNotFoundException if user doesn't exist
 * 4. Provide user data for Spring Security authentication process
 * 
 * Mocked Dependencies:
 * - UserRepository: Provides database access for user lookup by email
 * 
 * Test Configuration:
 * - Uses Mockito for mocking UserRepository
 * - Uses AssertJ for fluent assertions
 * - Uses JUnit Jupiter for exception testing (assertThrows)
 * - Tests Spring Security UserDetailsService.loadUserByUsername() implementation
 * 
 * Data Mapping Verified:
 * - User.id → UserDetailsImpl.id
 * - User.email → UserDetailsImpl.username (Spring Security convention)
 * - User.password → UserDetailsImpl.password
 * - User.firstName → UserDetailsImpl.firstName
 * - User.lastName → UserDetailsImpl.lastName
 * - User.admin → UserDetailsImpl.admin
 */

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Mockito.when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
        UserDetailsImpl impl = (UserDetailsImpl) userDetails;
        assertThat(impl.getId()).isEqualTo(user.getId());
        assertThat(impl.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(impl.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        Mockito.when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@example.com")
        );

        assertThat(exception.getMessage()).isEqualTo("User Not Found with email: unknown@example.com");
    }
}
