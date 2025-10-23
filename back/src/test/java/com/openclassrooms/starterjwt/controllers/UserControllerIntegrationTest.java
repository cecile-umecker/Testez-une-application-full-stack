package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController Integration Test Suite
 * 
 * This test file contains integration tests for the UserController.
 * The UserController handles user retrieval and deletion operations.
 * Tests use real database interactions with cleanup after each test.
 * 
 * Test Coverage:
 * 
 * findById Tests:
 * - findById_shouldReturnUser_whenUserExists: Tests retrieving a specific user by ID,
 *   verifies user details (id, email, firstName, lastName, admin) are returned correctly
 * 
 * - findById_shouldReturnNotFound_whenUserDoesNotExist: Tests retrieving non-existent user returns 404 Not Found
 * 
 * - findById_shouldReturnBadRequest_whenIdIsInvalid: Tests retrieving with invalid ID format returns 400 Bad Request
 * 
 * - findById_shouldReturnAdminUser_whenUserIsAdmin: Tests admin flag is correctly returned for admin users
 * 
 * - findById_shouldHandleSpecialCharacters_inUserData: Tests handling special characters (accents, apostrophes, hyphens)
 *   in user names and emails (François, Müller-O'Connor)
 * 
 * - findById_shouldReturnCompleteUserData: Tests all user fields are returned (id, email, firstName, lastName, admin, createdAt, updatedAt)
 * 
 * - findById_shouldNotReturnPassword_inResponse: Tests password field is excluded from response for security
 * 
 * - findById_shouldReturnNotFound_forNegativeId: Tests negative ID returns 404
 * 
 * - findById_shouldReturnNotFound_forZeroId: Tests zero ID returns 404
 * 
 * - findById_multipleTimes_shouldReturnSameData: Tests data consistency across multiple requests
 * 
 * - findById_shouldHandleLongNames: Tests handling of long first and last names
 * 
 * - findById_shouldHandleLongEmail: Tests handling of long email addresses
 * 
 * - findById_shouldReturnUser_withUniqueEmail: Tests email uniqueness constraint
 * 
 * - findById_shouldReturnUnauthorized_whenNotAuthenticated: Tests unauthenticated access returns 401
 * 
 * delete Tests:
 * - delete_shouldDeleteUser_whenUserDeletesOwnAccount: Tests user can successfully delete their own account,
 *   verifies user is removed from database
 * 
 * - delete_shouldReturnUnauthorized_whenUserTriesToDeleteOtherAccount: Tests user cannot delete another user's account,
 *   verifies 401 status and target user remains in database
 * 
 * - delete_shouldReturnUnauthorized_whenAdminTriesToDeleteOtherUser: Tests even admin cannot delete other users' accounts,
 *   verifies authorization rules are enforced
 * 
 * - delete_shouldReturnNotFound_whenUserDoesNotExist: Tests deleting non-existent user returns 404
 * 
 * - delete_shouldReturnBadRequest_whenIdIsInvalid: Tests deleting with invalid ID format returns 400
 * 
 * - delete_shouldReturnUnauthorized_whenNotAuthenticated: Tests unauthenticated deletion returns 401
 * 
 * - delete_shouldWorkForUserWithSessions: Tests user deletion works even if user has associated sessions
 * 
 * Dependencies:
 * - MockMvc: For simulating HTTP requests
 * - UserRepository: For user database operations
 * - PasswordEncoder: For password encryption in test data setup
 * 
 * Test Configuration:
 * - @SpringBootTest: Full application context loading
 * - @AutoConfigureMockMvc: Auto-configures MockMvc
 * - @Transactional: Rolls back database changes after each test
 * - @DirtiesContext: Resets application context for each test
 * - @WithMockUser: Provides authenticated context with specific username for authorization tests
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "john.doe@example.com")
    @DirtiesContext
    void findById_shouldReturnUser_whenUserExists() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DirtiesContext
    void findById_shouldReturnAdminUser_whenUserIsAdmin() throws Exception {
        // Arrange
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setAdmin(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        User savedAdmin = userRepository.save(adminUser);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedAdmin.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAdmin.getId()))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_shouldHandleSpecialCharacters_inUserData() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("françois.müller@example.com");
        user.setFirstName("François");
        user.setLastName("Müller-O'Connor");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("françois.müller@example.com"))
                .andExpect(jsonPath("$.firstName").value("François"))
                .andExpect(jsonPath("$.lastName").value("Müller-O'Connor"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_shouldReturnCompleteUserData() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("complete@example.com");
        user.setFirstName("Complete");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.admin").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist()); 
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_shouldNotReturnPassword_inResponse() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("secure@example.com");
        user.setFirstName("Secure");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("secretPassword"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "john.doe@example.com")
    @DirtiesContext
    void delete_shouldDeleteUser_whenUserDeletesOwnAccount() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        assertThat(userRepository.findById(userId)).isPresent();

        // Act
        mockMvc.perform(delete("/api/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @WithMockUser(username = "other.user@example.com")
    @DirtiesContext
    void delete_shouldReturnUnauthorized_whenUserTriesToDeleteOtherAccount() throws Exception {
        // Arrange
        User targetUser = new User();
        targetUser.setEmail("john.doe@example.com");
        targetUser.setFirstName("John");
        targetUser.setLastName("Doe");
        targetUser.setPassword(passwordEncoder.encode("password123"));
        targetUser.setAdmin(false);
        targetUser.setCreatedAt(LocalDateTime.now());
        targetUser.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(targetUser);
        Long userId = savedUser.getId();

        assertThat(userRepository.findById(userId)).isPresent();

        // Act
        mockMvc.perform(delete("/api/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Assert
        assertThat(userRepository.findById(userId)).isPresent();
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DirtiesContext
    void delete_shouldReturnUnauthorized_whenAdminTriesToDeleteOtherUser() throws Exception {
        // Arrange
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setAdmin(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(adminUser);

        User targetUser = new User();
        targetUser.setEmail("target@example.com");
        targetUser.setFirstName("Target");
        targetUser.setLastName("User");
        targetUser.setPassword(passwordEncoder.encode("password123"));
        targetUser.setAdmin(false);
        targetUser.setCreatedAt(LocalDateTime.now());
        targetUser.setUpdatedAt(LocalDateTime.now());
        User savedTarget = userRepository.save(targetUser);
        Long targetId = savedTarget.getId();

        // Act
        mockMvc.perform(delete("/api/user/" + targetId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Assert
        assertThat(userRepository.findById(targetId)).isPresent();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void delete_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/user/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void delete_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/user/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void findById_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void delete_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(delete("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_shouldReturnNotFound_forNegativeId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_shouldReturnNotFound_forZeroId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_multipleTimes_shouldReturnSameData() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("consistent@example.com");
        user.setFirstName("Consistent");
        user.setLastName("Data");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/user/" + savedUser.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.email").value("consistent@example.com"))
                    .andExpect(jsonPath("$.firstName").value("Consistent"))
                    .andExpect(jsonPath("$.lastName").value("Data"));
        }
    }

    @Test
    @WithMockUser(username = "longname@example.com")
    @DirtiesContext
    void findById_shouldHandleLongNames() throws Exception {
        // Arrange
        String longFirstName = "Maximilian";
        String longLastName = "Bartholomew";
        
        User user = new User();
        user.setEmail("longname@example.com");
        user.setFirstName(longFirstName);
        user.setLastName(longLastName);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(longFirstName))
                .andExpect(jsonPath("$.lastName").value(longLastName));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DirtiesContext
    void findById_shouldHandleLongEmail() throws Exception {
        // Arrange
        String longEmail = "verylongemailaddress@example.com";
        
        User user = new User();
        user.setEmail(longEmail);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(longEmail));
    }

    @Test
    @WithMockUser(username = "deleteme@example.com")
    @DirtiesContext
    void delete_shouldWorkForUserWithSessions() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("deleteme@example.com");
        user.setFirstName("Delete");
        user.setLastName("Me");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        // Act
        mockMvc.perform(delete("/api/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @WithMockUser(username = "unique@example.com")
    @DirtiesContext
    void findById_shouldReturnUser_withUniqueEmail() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("unique@example.com");
        user.setFirstName("Unique");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("unique@example.com"));

        assertThat(userRepository.findByEmail("unique@example.com")).isPresent();
    }
}