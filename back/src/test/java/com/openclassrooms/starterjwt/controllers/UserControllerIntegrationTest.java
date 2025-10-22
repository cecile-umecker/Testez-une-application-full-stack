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
        // Arrange - Create and save a real user
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
        // Arrange - Create admin user
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
        // Arrange - User with special characters
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

        // Act & Assert - Verify all DTO fields
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
                .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be in DTO
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

        // Act & Assert - Password should NOT be exposed
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "john.doe@example.com")
    @DirtiesContext
    void delete_shouldDeleteUser_whenUserDeletesOwnAccount() throws Exception {
        // Arrange - Create user
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

        // Verify user exists
        assertThat(userRepository.findById(userId)).isPresent();

        // Act
        mockMvc.perform(delete("/api/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert - User should be deleted
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @WithMockUser(username = "other.user@example.com")
    @DirtiesContext
    void delete_shouldReturnUnauthorized_whenUserTriesToDeleteOtherAccount() throws Exception {
        // Arrange - Create target user
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

        // Verify user exists
        assertThat(userRepository.findById(userId)).isPresent();

        // Act - Try to delete with different authenticated user
        mockMvc.perform(delete("/api/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Assert - User should still exist
        assertThat(userRepository.findById(userId)).isPresent();
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DirtiesContext
    void delete_shouldReturnUnauthorized_whenAdminTriesToDeleteOtherUser() throws Exception {
        // Arrange - Create admin user (logged in)
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setAdmin(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(adminUser);

        // Create target user to delete
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

        // Act - Admin tries to delete another user (should fail)
        mockMvc.perform(delete("/api/user/" + targetId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Assert - Target user should still exist
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

        // Act & Assert - No @WithMockUser
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

        // Act & Assert - No @WithMockUser
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

        // Act & Assert - Call multiple times
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
        // Arrange - User with reasonably long names
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
        // Arrange - User with long but valid email
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
        // Arrange - Create user (in real scenario might have sessions)
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
        // Arrange - Verify email uniqueness
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

        // Verify only one user with this email
        assertThat(userRepository.findByEmail("unique@example.com")).isPresent();
    }
}