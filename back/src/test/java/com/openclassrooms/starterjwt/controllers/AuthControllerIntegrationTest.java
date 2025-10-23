package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DirtiesContext
    void login_shouldReturnJwtToken_whenCredentialsAreValid() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @DirtiesContext
    void login_shouldReturnUnauthorized_whenPasswordIsInvalid() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void login_shouldReturnUnauthorized_whenUserDoesNotExist() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void login_shouldReturnAdminTrue_whenUserIsAdmin() throws Exception {
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

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true))
                .andExpect(jsonPath("$.username").value("admin@example.com"));
    }

    @Test
    @DirtiesContext
    void login_shouldHandleSpecialCharactersInCredentials() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("françois.o'connor@example.com");
        user.setFirstName("François");
        user.setLastName("O'Connor");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("françois.o'connor@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("François"))
                .andExpect(jsonPath("$.lastName").value("O'Connor"));
    }

    @Test
    @DirtiesContext
    void register_shouldCreateNewUser_whenEmailIsNotTaken() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("securePassword123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        User createdUser = userRepository.findByEmail("newuser@example.com").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("newuser@example.com");
        assertThat(createdUser.getFirstName()).isEqualTo("New");
        assertThat(createdUser.getLastName()).isEqualTo("User");
        assertThat(createdUser.isAdmin()).isFalse();
        assertThat(createdUser.getPassword()).isNotEqualTo("securePassword123");
    }

    @Test
    @DirtiesContext
    void register_shouldReturnBadRequest_whenEmailAlreadyExists() throws Exception {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setAdmin(false);
        existingUser.setCreatedAt(LocalDateTime.now());
        existingUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(existingUser);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

        assertThat(userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("existing@example.com"))
                .count()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void register_shouldEncodePassword_whenCreatingUser() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("secure@example.com");
        signupRequest.setFirstName("Secure");
        signupRequest.setLastName("User");
        signupRequest.setPassword("plainTextPassword");

        // Act
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Assert
        User createdUser = userRepository.findByEmail("secure@example.com").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getPassword()).isNotEqualTo("plainTextPassword");
        assertThat(createdUser.getPassword()).startsWith("$2a$"); // BCrypt prefix
        assertThat(createdUser.getPassword().length()).isGreaterThan(50); // BCrypt hashes are long
    }

    @Test
    @DirtiesContext
    void register_shouldSetAdminToFalse_byDefault() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("normaluser@example.com");
        signupRequest.setFirstName("Normal");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password");

        // Act
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Assert
        User createdUser = userRepository.findByEmail("normaluser@example.com").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.isAdmin()).isFalse();
    }

    @Test
    @DirtiesContext
    void register_shouldHandleSpecialCharactersInUserData() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("françois.müller@example.com");
        signupRequest.setFirstName("François");
        signupRequest.setLastName("Müller-O'Connor");
        signupRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        User createdUser = userRepository.findByEmail("françois.müller@example.com").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getFirstName()).isEqualTo("François");
        assertThat(createdUser.getLastName()).isEqualTo("Müller-O'Connor");
    }

    @Test
    @DirtiesContext
    void register_shouldSetCreatedAndUpdatedTimestamps() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("timestamps@example.com");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password");

        // Act
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Assert
        User createdUser = userRepository.findByEmail("timestamps@example.com").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getCreatedAt()).isNotNull();
        assertThat(createdUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DirtiesContext
    void register_andThenLogin_shouldWorkCorrectly() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("registerlogin@example.com");
        signupRequest.setFirstName("Register");
        signupRequest.setLastName("Login");
        signupRequest.setPassword("mySecurePassword");

        // Act
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Act
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("registerlogin@example.com");
        loginRequest.setPassword("mySecurePassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("registerlogin@example.com"))
                .andExpect(jsonPath("$.firstName").value("Register"))
                .andExpect(jsonPath("$.lastName").value("Login"));
    }

    @Test
    @DirtiesContext
    void register_shouldFailWithBadRequest_whenEmailIsInvalid() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid-email"); // Invalid email format
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void login_shouldFailWithBadRequest_whenEmailIsEmpty() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(""); // Empty email
        loginRequest.setPassword("password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void login_shouldFailWithBadRequest_whenPasswordIsEmpty() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword(""); // Empty password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void register_shouldFailWithBadRequest_whenFieldsAreMissing() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("incomplete@example.com");
        // Missing firstName, lastName, password

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void login_multipleTimes_shouldReturnDifferentTokens() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("multilogin@example.com");
        user.setFirstName("Multi");
        user.setLastName("Login");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("multilogin@example.com");
        loginRequest.setPassword("password123");

        // Act
        String token1 = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token2 = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert
        assertThat(token1).isNotEmpty();
        assertThat(token2).isNotEmpty();
    }
}