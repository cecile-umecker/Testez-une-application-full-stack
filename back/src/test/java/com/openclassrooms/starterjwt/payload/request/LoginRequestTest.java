package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
