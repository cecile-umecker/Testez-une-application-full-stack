package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * User Model Unit Test Suite
 * 
 * This test file contains unit tests for the User entity model.
 * The User model represents an application user with authentication credentials and profile information.
 * Tests verify constructors with @NonNull validation, builder pattern, getters/setters with null checks,
 * equals/hashCode contracts, and toString() method.
 * 
 * Test Coverage:
 * 
 * Constructor Tests (with @NonNull validation):
 * - testFullCoverage: Comprehensive test covering multiple scenarios:
 *   • Tests 5-parameter constructor (email, lastName, firstName, password, admin)
 *     verifies all fields are set correctly
 *   • Tests @NonNull validation on 5-parameter constructor throws NullPointerException
 *     for null email, lastName, firstName, or password
 *   
 *   • Tests 8-parameter constructor (id, email, lastName, firstName, password, admin, createdAt, updatedAt)
 *     verifies all fields including timestamps are set correctly
 *   • Tests @NonNull validation on 8-parameter constructor throws NullPointerException
 *     for null email, lastName, firstName, or password
 * 
 * Builder Pattern Tests:
 * - Tests User.builder() creates entity with all fields (id, email, lastName, firstName, password, admin, timestamps)
 *   verifies builder pattern works correctly
 * 
 * Setter Tests with @NonNull Validation:
 * - Tests setEmail(), setLastName(), setFirstName(), setPassword(), setAdmin()
 *   verifies setters update values correctly
 * - Tests @NonNull validation on setters throws NullPointerException
 *   for null email, lastName, firstName, or password
 * 
 * Equals and HashCode Tests:
 * - Tests equals() based on ID: users with same ID are equal regardless of other fields
 * - Tests equals() with different IDs: users with different IDs are not equal
 * - Tests hashCode() consistency: same ID produces same hashCode
 * - Tests hashCode() for different IDs: different IDs produce different hashCodes
 * - Tests users with null IDs are considered equal
 * - Tests hashCode() for null IDs: consistent hashCode regardless of other fields
 * - Tests equals() returns false for null and different types
 * 
 * ToString Test:
 * - Tests toString() contains key fields: email, lastName, firstName
 * 
 * Entity Properties Tested:
 * - id: Unique identifier (Long)
 * - email: User's email address (String, @NonNull)
 * - lastName: User's last name (String, @NonNull)
 * - firstName: User's first name (String, @NonNull)
 * - password: User's encrypted password (String, @NonNull)
 * - admin: Admin flag (boolean)
 * - createdAt: Creation timestamp (LocalDateTime)
 * - updatedAt: Last update timestamp (LocalDateTime)
 * 
 * Test Configuration:
 * - Uses AssertJ for fluent assertions
 * - Uses JUnit Jupiter for exception testing
 * - Tests @NonNull constraint enforcement on constructors and setters
 * - Tests builder pattern, equals/hashCode contract, and toString()
 */

class UserTest {

    @Test
    void testFullCoverage() {
        LocalDateTime now = LocalDateTime.now();

        User userReq = new User("john@example.com", "Doe", "John", "secret", true);
        assertThat(userReq.getEmail()).isEqualTo("john@example.com");
        assertThat(userReq.getLastName()).isEqualTo("Doe");
        assertThat(userReq.getFirstName()).isEqualTo("John");
        assertThat(userReq.getPassword()).isEqualTo("secret");
        assertThat(userReq.isAdmin()).isTrue();

        assertThrows(NullPointerException.class, () -> new User(null, "Doe", "John", "secret", true));
        assertThrows(NullPointerException.class, () -> new User("john@example.com", null, "John", "secret", true));
        assertThrows(NullPointerException.class, () -> new User("john@example.com", "Doe", null, "secret", true));
        assertThrows(NullPointerException.class, () -> new User("john@example.com", "Doe", "John", null, true));

        User userAll = new User(1L, "mail@test.com", "Last", "First", "pwd", false, now, now);
        assertThat(userAll.getId()).isEqualTo(1L);
        assertThat(userAll.getCreatedAt()).isEqualTo(now);
        assertThat(userAll.getUpdatedAt()).isEqualTo(now);

        assertThrows(NullPointerException.class, () -> new User(1L, null, "Last", "First", "pwd", false, now, now));
        assertThrows(NullPointerException.class, () -> new User(1L, "mail@test.com", null, "First", "pwd", false, now, now));
        assertThrows(NullPointerException.class, () -> new User(1L, "mail@test.com", "Last", null, "pwd", false, now, now));
        assertThrows(NullPointerException.class, () -> new User(1L, "mail@test.com", "Last", "First", null, false, now, now));

        User user = User.builder()
                .id(2L)
                .email("builder@test.com")
                .lastName("Builder")
                .firstName("Bob")
                .password("1234")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        user.setEmail("changed@mail.com");
        user.setLastName("Changed");
        user.setFirstName("User");
        user.setPassword("newpass");
        user.setAdmin(true);

        assertThat(user.getEmail()).isEqualTo("changed@mail.com");
        assertThat(user.getLastName()).isEqualTo("Changed");
        assertThat(user.getFirstName()).isEqualTo("User");
        assertThat(user.getPassword()).isEqualTo("newpass");
        assertThat(user.isAdmin()).isTrue();

        assertThrows(NullPointerException.class, () -> user.setEmail(null));
        assertThrows(NullPointerException.class, () -> user.setLastName(null));
        assertThrows(NullPointerException.class, () -> user.setFirstName(null));
        assertThrows(NullPointerException.class, () -> user.setPassword(null));

        User sameId = User.builder()
                .id(2L)
                .email("same@example.com")
                .firstName("Same")
                .lastName("User")
                .password("pwd")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User diffId = User.builder()
                .id(3L)
                .email("diff@example.com")
                .firstName("Diff")
                .lastName("User")
                .password("pwd")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(user).isEqualTo(sameId);
        assertThat(user).isNotEqualTo(diffId);
        assertThat(user.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(user.hashCode()).isNotEqualTo(diffId.hashCode());

        User nullId1 = User.builder()
                .email("n1@example.com")
                .firstName("A")
                .lastName("B")
                .password("p")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User nullId2 = User.builder()
                .email("n2@example.com")
                .firstName("C")
                .lastName("D")
                .password("p")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(nullId1.equals(nullId2)).isTrue();
        assertThat(nullId1.hashCode()).isEqualTo(nullId2.hashCode());

        assertThat(user.equals(null)).isFalse();
        assertThat(user.equals("string")).isFalse();

        String toString = user.toString();
        assertThat(toString).contains("changed@mail.com", "Changed", "User");
    }

}
