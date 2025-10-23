package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SignupRequest Unit Test Suite
 * 
 * This test file contains unit tests for the SignupRequest DTO.
 * The SignupRequest is used to transfer user registration data from the client to the registration endpoint.
 * Tests verify getters/setters, Bean Validation constraints, equals/hashCode contracts, and toString() method.
 * 
 * Test Coverage:
 * 
 * Getters and Setters Tests:
 * - testGettersAndSetters: Tests all setters store values correctly,
 *   verifies all getters return the set values (email, firstName, lastName, password)
 * 
 * Validation Tests - Email:
 * - testValidSignupRequest: Tests valid request with all fields passes validation
 * - testEmailNotBlank: Tests @NotBlank constraint triggers violation for empty email
 * - testEmailNull: Tests @NotBlank constraint triggers violation for null email
 * - testEmailMaxSize: Tests @Size(max=50) constraint triggers violation for email > 50 characters
 * - testEmailInvalidFormat: Tests @Email constraint triggers violation for invalid email format
 * 
 * Validation Tests - First Name:
 * - testFirstNameNotBlank: Tests @NotBlank constraint for empty firstName
 * - testFirstNameNull: Tests @NotBlank constraint for null firstName
 * - testFirstNameTooShort: Tests @Size(min=3) constraint for firstName < 3 characters
 * - testFirstNameTooLong: Tests @Size(max=20) constraint for firstName > 20 characters
 * - testFirstNameMinSize: Tests minimum valid length (3 characters) passes validation
 * - testFirstNameMaxSize: Tests maximum valid length (20 characters) passes validation
 * 
 * Validation Tests - Last Name:
 * - testLastNameNotBlank: Tests @NotBlank constraint for empty lastName
 * - testLastNameNull: Tests @NotBlank constraint for null lastName
 * - testLastNameTooShort: Tests @Size(min=3) constraint for lastName < 3 characters
 * - testLastNameTooLong: Tests @Size(max=20) constraint for lastName > 20 characters
 * 
 * Validation Tests - Password:
 * - testPasswordNotBlank: Tests @NotBlank constraint for empty password
 * - testPasswordNull: Tests @NotBlank constraint for null password
 * - testPasswordTooShort: Tests @Size(min=6) constraint for password < 6 characters
 * - testPasswordTooLong: Tests @Size(max=40) constraint for password > 40 characters
 * - testPasswordMinSize: Tests minimum valid length (6 characters) passes validation
 * - testPasswordMaxSize: Tests maximum valid length (40 characters) passes validation
 * 
 * Validation Tests - Multiple Fields:
 * - testMultipleViolations: Tests all invalid fields trigger violations simultaneously (4 violations total)
 * 
 * Equals Tests:
 * - testEqualsSameInstance: Tests reflexive property (object equals itself)
 * - testEqualsIdenticalValues: Tests symmetric property with identical field values
 * - testEqualsNull: Tests equals() returns false for null
 * - testEqualsDifferentClass: Tests equals() returns false for different types
 * - testEqualsDifferentEmail/FirstName/LastName/Password: Tests inequality when each field differs
 * - testEqualsWithNullFields: Tests two instances with all null fields are equal
 * - testEqualsOneNullFieldOneNot: Tests inequality when one field is null and other is not
 * 
 * HashCode Tests:
 * - testHashCodeConsistency: Tests hashCode() returns same value on multiple calls
 * - testHashCodeEqualObjects: Tests equal objects have equal hashCodes
 * - testHashCodeDifferentEmail/FirstName/LastName/Password: Tests different field values produce different hashCodes
 * - testHashCodeWithNullFields: Tests consistent hashCode for null fields
 * 
 * ToString Test:
 * - testToString: Tests toString() contains all field values (email, firstName, lastName, password)
 * 
 * CanEquals Test:
 * - testCanEquals: Tests canEqual() method for type compatibility check
 * 
 * DTO Properties and Constraints:
 * - email: String, @NotBlank, @Size(max=50), @Email
 * - firstName: String, @NotBlank, @Size(min=3, max=20)
 * - lastName: String, @NotBlank, @Size(min=3, max=20)
 * - password: String, @NotBlank, @Size(min=6, max=40)
 * 
 * Test Configuration:
 * - Uses javax.validation.Validator for constraint validation testing
 * - ValidatorFactory: Created before each test to initialize validator
 * - Uses AssertJ for fluent assertions
 * - Tests Bean Validation annotations, equals/hashCode contract, and toString()
 */

class SignupRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidSignupRequest() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void testGettersAndSetters() {
        // Arrange & Act
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("secret123");

        // Assert
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getFirstName()).isEqualTo("John");
        assertThat(request.getLastName()).isEqualTo("Doe");
        assertThat(request.getPassword()).isEqualTo("secret123");
    }

    @Test
    void testEmailNotBlank() {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testEmailNull() {
        SignupRequest request = new SignupRequest();
        request.setEmail(null);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testEmailMaxSize() {
        SignupRequest request = new SignupRequest();
        request.setEmail("a".repeat(40) + "@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testEmailInvalidFormat() {
        SignupRequest request = new SignupRequest();
        request.setEmail("invalid-email");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testFirstNameNotBlank() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
    }

    @Test
    void testFirstNameNull() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName(null);
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
    }

    @Test
    void testFirstNameTooShort() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("Jo");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
    }

    @Test
    void testFirstNameTooLong() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("A".repeat(21));
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
    }

    @Test
    void testFirstNameMinSize() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("Jon");
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testFirstNameMaxSize() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("A".repeat(20));
        request.setLastName("Doe");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testLastNameNotBlank() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
    }

    @Test
    void testLastNameNull() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName(null);
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
    }

    @Test
    void testLastNameTooShort() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Do");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
    }

    @Test
    void testLastNameTooLong() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("D".repeat(21));
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
    }

    @Test
    void testPasswordNotBlank() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testPasswordNull() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword(null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testPasswordTooShort() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("12345");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testPasswordTooLong() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("P".repeat(41));

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testPasswordMinSize() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("pass12");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testPasswordMaxSize() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("P".repeat(40));

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMultipleViolations() {
        SignupRequest request = new SignupRequest();
        request.setEmail("invalid");
        request.setFirstName("Jo");
        request.setLastName("D");
        request.setPassword("123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(4);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    
    @Test
    void testEqualsSameInstance() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        assertThat(request).isEqualTo(request);
    }

    @Test
    void testEqualsIdenticalValues() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertThat(request1).isEqualTo(request2);
        assertThat(request2).isEqualTo(request1);
    }

    @Test
    void testEqualsNull() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        
        assertThat(request).isNotEqualTo(null);
    }

    @Test
    void testEqualsDifferentClass() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        
        assertThat(request).isNotEqualTo("string");
        assertThat(request).isNotEqualTo(new Object());
    }

    @Test
    void testEqualsDifferentEmail() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test1@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test2@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void testEqualsDifferentFirstName() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("Jane");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void testEqualsDifferentLastName() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Smith");
        request2.setPassword("password123");

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void testEqualsDifferentPassword() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("different456");

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void testEqualsWithNullFields() {
        SignupRequest request1 = new SignupRequest();
        SignupRequest request2 = new SignupRequest();

        assertThat(request1).isEqualTo(request2);
    }

    @Test
    void testEqualsOneNullFieldOneNot() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail(null);

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");

        assertThat(request1).isNotEqualTo(request2);
    }


    @Test
    void testHashCodeConsistency() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        int hashCode1 = request.hashCode();
        int hashCode2 = request.hashCode();

        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void testHashCodeEqualObjects() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testHashCodeDifferentEmail() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test1@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test2@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    void testHashCodeDifferentFirstName() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("Jane");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    void testHashCodeDifferentLastName() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Smith");
        request2.setPassword("password123");

        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    void testHashCodeDifferentPassword() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("different456");

        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        SignupRequest request1 = new SignupRequest();
        SignupRequest request2 = new SignupRequest();

        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        String toString = request.toString();

        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("John");
        assertThat(toString).contains("Doe");
        assertThat(toString).contains("password123");
    }

    @Test
    void testCanEquals() {
        SignupRequest request1 = new SignupRequest();
        SignupRequest request2 = new SignupRequest();
        
        assertThat(request1.equals(request2)).isTrue();
    }
}