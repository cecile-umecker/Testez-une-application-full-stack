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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void findById_shouldReturnUserFromDatabase() {
        // Arrange - Create and save a real user
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
        // Arrange - Create and save a user
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

        // Verify user exists
        assertThat(userRepository.findById(userId)).isPresent();

        // Act
        userService.delete(userId);

        // Assert
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @Transactional
    void delete_shouldThrowException_whenUserDoesNotExist() {
        // Act & Assert - Should throw EmptyResultDataAccessException
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userService.delete(999L);
        });
    }

    @Test
    @Transactional
    void findById_shouldReturnAdminUser() {
        // Arrange - Create admin user
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
        // Arrange - User with special characters
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
        // Arrange - Create multiple users
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

        // Verify both exist
        assertThat(userRepository.findById(user1.getId())).isPresent();
        assertThat(userRepository.findById(user2.getId())).isPresent();

        // Act - Delete both
        userService.delete(user1.getId());
        userService.delete(user2.getId());

        // Assert - Both deleted
        assertThat(userRepository.findById(user1.getId())).isEmpty();
        assertThat(userRepository.findById(user2.getId())).isEmpty();
    }

    @Test
    @Transactional
    void findById_shouldReturnUserWithAllFields() {
        // Arrange - User with all fields populated
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

        // Assert - Verify all fields
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

        // Act - Delete then try to find
        userService.delete(userId);
        User result = userService.findById(userId);

        // Assert
        assertThat(result).isNull();
    }
}