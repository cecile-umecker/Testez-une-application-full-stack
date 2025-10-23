package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginRequest Unit Test Suite
 * 
 * This test file contains unit tests for the LoginRequest DTO.
 * The LoginRequest is used to transfer login credentials from the client to the authentication endpoint.
 * Tests verify getters/setters functionality and Bean Validation constraints.
 * 
 * Test Coverage:
 * 
 * Getters and Setters Tests:
 * - testGettersAndSetters: Tests setEmail() and setPassword() store values correctly,
 *   verifies getEmail() and getPassword() return the set values
 * 
 * Validation Tests:
 * - testValidation_NotBlank: Tests @NotBlank constraint validation,
 *   verifies empty email string triggers constraint violation,
 *   verifies null password triggers constraint violation,
 *   confirms exactly 2 violations are detected (one for each field)
 * 
 * - testValidation_ValidValues: Tests valid login request passes validation,
 *   verifies no constraint violations when email and password are properly set
 * 
 * DTO Properties Tested:
 * - email: User's email address (String, @NotBlank constraint)
 * - password: User's password (String, @NotBlank constraint)
 * 
 * Validation Constraints:
 * - @NotBlank on email: Email field cannot be null, empty, or whitespace-only
 * - @NotBlank on password: Password field cannot be null, empty, or whitespace-only
 * 
 * Test Configuration:
 * - Uses javax.validation.Validator for constraint validation testing
 * - ValidatorFactory: Created before each test to initialize validator
 * - Uses AssertJ for fluent assertions
 * - Tests Bean Validation annotations compliance
 */

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void testValidation_NotBlank() {
        LoginRequest request = new LoginRequest();
        request.setEmail("");  
        request.setPassword(null); 

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(2);

        boolean emailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        boolean passwordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));

        assertThat(emailViolation).isTrue();
        assertThat(passwordViolation).isTrue();
    }

    @Test
    void testValidation_ValidValues() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}
