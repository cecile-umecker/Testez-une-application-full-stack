package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtResponseTest {

    @Test
    void testConstructor() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                "jwt-token-123",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Assert
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("user@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getAdmin()).isTrue();
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    void testConstructorWithNullValues() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Assert
        assertThat(response.getToken()).isNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getFirstName()).isNull();
        assertThat(response.getLastName()).isNull();
        assertThat(response.getAdmin()).isNull();
        assertThat(response.getType()).isEqualTo("Bearer"); 
    }

    @Test
    void testConstructorWithAdminFalse() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                "token",
                2L,
                "regular@example.com",
                "Jane",
                "Smith",
                false
        );

        // Assert
        assertThat(response.getAdmin()).isFalse();
    }

    @Test
    void testDefaultTypeValue() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Assert
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    void testSetToken() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "initial-token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setToken("new-token");

        // Assert
        assertThat(response.getToken()).isEqualTo("new-token");
    }

    @Test
    void testSetType() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setType("CustomType");

        // Assert
        assertThat(response.getType()).isEqualTo("CustomType");
    }

    @Test
    void testSetId() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setId(99L);

        // Assert
        assertThat(response.getId()).isEqualTo(99L);
    }

    @Test
    void testSetUsername() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setUsername("newuser@example.com");

        // Assert
        assertThat(response.getUsername()).isEqualTo("newuser@example.com");
    }

    @Test
    void testSetFirstName() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setFirstName("Jane");

        // Assert
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testSetLastName() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setLastName("Smith");

        // Assert
        assertThat(response.getLastName()).isEqualTo("Smith");
    }

    @Test
    void testSetAdmin() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setAdmin(false);

        // Assert
        assertThat(response.getAdmin()).isFalse();
    }

    @Test
    void testSettersWithNullValues() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setToken(null);
        response.setId(null);
        response.setUsername(null);
        response.setFirstName(null);
        response.setLastName(null);
        response.setAdmin(null);
        response.setType(null);

        // Assert
        assertThat(response.getToken()).isNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getFirstName()).isNull();
        assertThat(response.getLastName()).isNull();
        assertThat(response.getAdmin()).isNull();
        assertThat(response.getType()).isNull();
    }

    @Test
    void testGettersReturnCorrectValues() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "test-token",
                5L,
                "test@example.com",
                "Test",
                "User",
                false
        );

        // Act & Assert
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getUsername()).isEqualTo("test@example.com");
        assertThat(response.getFirstName()).isEqualTo("Test");
        assertThat(response.getLastName()).isEqualTo("User");
        assertThat(response.getAdmin()).isFalse();
    }

    @Test
    void testMultipleSettersChained() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act
        response.setToken("new-token");
        response.setId(10L);
        response.setUsername("new@example.com");
        response.setFirstName("Jane");
        response.setLastName("Smith");
        response.setAdmin(false);
        response.setType("Custom");

        // Assert
        assertThat(response.getToken()).isEqualTo("new-token");
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getUsername()).isEqualTo("new@example.com");
        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getAdmin()).isFalse();
        assertThat(response.getType()).isEqualTo("Custom");
    }

    @Test
    void testConstructorWithEmptyStrings() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                "",
                0L,
                "",
                "",
                "",
                false
        );

        // Assert
        assertThat(response.getToken()).isEqualTo("");
        assertThat(response.getId()).isEqualTo(0L);
        assertThat(response.getUsername()).isEqualTo("");
        assertThat(response.getFirstName()).isEqualTo("");
        assertThat(response.getLastName()).isEqualTo("");
        assertThat(response.getAdmin()).isFalse();
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    void testTypeDefaultValueNotOverriddenByConstructor() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Assert
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    void testConstructorWithLongToken() {
        // Arrange
        String longToken = "a".repeat(500);

        // Act
        JwtResponse response = new JwtResponse(
                longToken,
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Assert
        assertThat(response.getToken()).isEqualTo(longToken);
        assertThat(response.getToken()).hasSize(500);
    }

    @Test
    void testConstructorWithSpecialCharacters() {
        // Arrange & Act
        JwtResponse response = new JwtResponse(
                "token!@#$%^&*()",
                1L,
                "user+test@example.com",
                "Jöhn",
                "Döe-Smith",
                true
        );

        // Assert
        assertThat(response.getToken()).isEqualTo("token!@#$%^&*()");
        assertThat(response.getUsername()).isEqualTo("user+test@example.com");
        assertThat(response.getFirstName()).isEqualTo("Jöhn");
        assertThat(response.getLastName()).isEqualTo("Döe-Smith");
    }

    @Test
    void testSettersOverwriteConstructorValues() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "initial-token",
                1L,
                "initial@example.com",
                "Initial",
                "Name",
                true
        );

        // Act
        response.setToken("updated-token");
        response.setId(999L);
        response.setUsername("updated@example.com");
        response.setFirstName("Updated");
        response.setLastName("LastName");
        response.setAdmin(false);

        // Assert
        assertThat(response.getToken()).isEqualTo("updated-token");
        assertThat(response.getId()).isEqualTo(999L);
        assertThat(response.getUsername()).isEqualTo("updated@example.com");
        assertThat(response.getFirstName()).isEqualTo("Updated");
        assertThat(response.getLastName()).isEqualTo("LastName");
        assertThat(response.getAdmin()).isFalse();
    }

    @Test
    void testGettersDoNotModifyState() {
        // Arrange
        JwtResponse response = new JwtResponse(
                "token",
                1L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        // Act 
        response.getToken();
        response.getId();
        response.getUsername();
        response.getFirstName();
        response.getLastName();
        response.getAdmin();
        response.getType();

        // Assert
        assertThat(response.getToken()).isEqualTo("token");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("user@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getAdmin()).isTrue();
        assertThat(response.getType()).isEqualTo("Bearer");
    }
}