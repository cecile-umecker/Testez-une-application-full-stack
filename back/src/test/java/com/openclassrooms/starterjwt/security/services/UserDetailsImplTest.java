package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserDetailsImpl Unit Test Suite
 * 
 * This test file contains unit tests for the UserDetailsImpl class.
 * The UserDetailsImpl is a custom implementation of Spring Security's UserDetails interface,
 * representing authenticated user information with additional application-specific fields.
 * 
 * Test Coverage:
 * 
 * Builder Pattern Tests:
 * - testBuilderAndGetters: Tests builder with all fields set,
 *   verifies getters return correct values (id, username, firstName, lastName, password, admin)
 * 
 * - testBuilderIndividualSetters: Tests builder setters called individually,
 *   verifies each setter works correctly before build() is called
 * 
 * - testBuilderPartialFields: Tests builder with only some fields set,
 *   verifies unset fields are null after build
 * 
 * - testBuilderWithNullValues: Tests builder explicitly set with null values,
 *   verifies null values are preserved
 * 
 * - testBuilderEmptyBuild: Tests builder without any fields set,
 *   verifies all fields are null in resulting object
 * 
 * - testBuilderToString: Tests builder's toString() method contains class name
 * 
 * - testBuilderChaining: Tests fluent API chaining of builder methods,
 *   verifies all fields can be set in single chain
 * 
 * - testMultipleBuildersIndependence: Tests multiple builders are independent,
 *   verifies changes to one builder don't affect another
 * 
 * Constructor Tests:
 * - testAllArgsConstructor: Tests 6-parameter constructor with all values,
 *   verifies all fields are correctly initialized (id, username, firstName, lastName, admin, password)
 * 
 * - testAllArgsConstructorWithNulls: Tests constructor accepts null values for all parameters,
 *   verifies null handling in constructor
 * 
 * Getter Tests:
 * - testGettersReturnCorrectValues: Tests all getters return values set via builder,
 *   verifies getter methods work correctly for each field
 * 
 * Spring Security UserDetails Methods Tests:
 * - testAuthoritiesAndAccountStatus: Tests Spring Security interface methods,
 *   verifies getAuthorities() returns empty collection,
 *   confirms all account status methods return true (non-expired, non-locked, enabled)
 * 
 * - testAuthoritiesAlwaysEmpty: Tests authorities collection is always empty for all users
 * 
 * - testAccountStatusAlwaysTrue: Tests all account status flags are always true
 * 
 * Equals Tests:
 * - testEquals: Tests equals() based on ID field only,
 *   verifies users with same ID are equal regardless of other fields,
 *   confirms users with different IDs are not equal,
 *   validates null and different class comparisons return false
 * 
 * - testEqualsSameInstance: Tests reflexive property (object equals itself)
 * 
 * - testEqualsWithNullIds: Tests users with null IDs are considered equal
 * 
 * - testEqualsNullIdVsNonNullId: Tests user with null ID not equal to user with non-null ID
 * 
 * - testEqualsDifferentClass: Tests equals() returns false for different types
 * 
 * - testEqualsWithDifferentFieldsSameId: Tests equality depends only on ID,
 *   verifies other fields (username, firstName, lastName, password, admin) don't affect equality
 * 
 * UserDetailsImpl Properties:
 * - id: User's unique identifier (Long)
 * - username: User's email/username (String) - implements UserDetails.getUsername()
 * - firstName: User's first name (String)
 * - lastName: User's last name (String)
 * - password: User's encrypted password (String) - implements UserDetails.getPassword()
 * - admin: Admin privilege flag (Boolean)
 * 
 * Spring Security UserDetails Interface Methods:
 * - getAuthorities(): Returns empty collection (no role-based authorities)
 * - isAccountNonExpired(): Always returns true
 * - isAccountNonLocked(): Always returns true
 * - isCredentialsNonExpired(): Always returns true
 * - isEnabled(): Always returns true
 * 
 * Test Configuration:
 * - Uses AssertJ for fluent assertions
 * - Tests Lombok-generated builder pattern and all-args constructor
 * - Validates equals() contract based on ID field only
 * - Tests Spring Security UserDetails interface implementation
 */

class UserDetailsImplTest {

    @Test
    void testBuilderAndGetters() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("secret")
                .admin(true)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("john@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getAdmin()).isTrue();
    }

    @Test
    void testBuilderIndividualSetters() {
        UserDetailsImpl.UserDetailsImplBuilder builder = UserDetailsImpl.builder();
        
        builder.id(10L);
        builder.username("builder@test.com");
        builder.firstName("Builder");
        builder.lastName("Test");
        builder.password("builderPass");
        builder.admin(false);
        
        UserDetailsImpl user = builder.build();
        
        assertThat(user.getId()).isEqualTo(10L);
        assertThat(user.getUsername()).isEqualTo("builder@test.com");
        assertThat(user.getFirstName()).isEqualTo("Builder");
        assertThat(user.getLastName()).isEqualTo("Test");
        assertThat(user.getPassword()).isEqualTo("builderPass");
        assertThat(user.getAdmin()).isFalse();
    }

    @Test
    void testBuilderPartialFields() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(2L)
                .username("partial@test.com")
                .build();

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getUsername()).isEqualTo("partial@test.com");
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getAdmin()).isNull();
    }

    @Test
    void testBuilderWithNullValues() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(null)
                .username(null)
                .firstName(null)
                .lastName(null)
                .password(null)
                .admin(null)
                .build();

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getAdmin()).isNull();
    }

    @Test
    void testBuilderEmptyBuild() {
        UserDetailsImpl user = UserDetailsImpl.builder().build();

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getAdmin()).isNull();
    }

    @Test
    void testBuilderToString() {
        UserDetailsImpl.UserDetailsImplBuilder builder = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com");
        
        String builderString = builder.toString();
        assertThat(builderString).contains("UserDetailsImplBuilder");
    }

    @Test
    void testAllArgsConstructor() {
        UserDetailsImpl user = new UserDetailsImpl(
                5L,
                "constructor@test.com",
                "Constructor",
                "Test",
                true,
                "constructorPass"
        );

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getUsername()).isEqualTo("constructor@test.com");
        assertThat(user.getFirstName()).isEqualTo("Constructor");
        assertThat(user.getLastName()).isEqualTo("Test");
        assertThat(user.getAdmin()).isTrue();
        assertThat(user.getPassword()).isEqualTo("constructorPass");
    }

    @Test
    void testAllArgsConstructorWithNulls() {
        UserDetailsImpl user = new UserDetailsImpl(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getAdmin()).isNull();
        assertThat(user.getPassword()).isNull();
    }

    @Test
    void testAuthoritiesAndAccountStatus() {
        UserDetailsImpl user = UserDetailsImpl.builder().build();

        Collection<?> authorities = user.getAuthorities();
        assertThat(authorities).isNotNull();
        assertThat(authorities).isEmpty();

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testEquals() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(1L)
                .username("jane@example.com")
                .firstName("Jane")
                .build();

        UserDetailsImpl user3 = UserDetailsImpl.builder()
                .id(2L)
                .username("bob@example.com")
                .build();

        assertThat(user1).isEqualTo(user2); 
        assertThat(user1).isNotEqualTo(user3); 
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo("string");
    }

    @Test
    void testEqualsSameInstance() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(10L)
                .username("test@example.com")
                .build();
        assertThat(user).isEqualTo(user); 
    }

    @Test
    void testEqualsWithNullIds() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(null)
                .username("user1@example.com")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(null)
                .username("user2@example.com")
                .build();

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testEqualsNullIdVsNonNullId() {
        UserDetailsImpl userWithNullId = UserDetailsImpl.builder()
                .id(null)
                .username("null@example.com")
                .build();

        UserDetailsImpl userWithId = UserDetailsImpl.builder()
                .id(1L)
                .username("notnull@example.com")
                .build();

        assertThat(userWithNullId).isNotEqualTo(userWithId);
    }

    @Test
    void testEqualsDifferentClass() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        assertThat(user).isNotEqualTo("string");
        assertThat(user).isNotEqualTo(new Object());
    }

    @Test
    void testGettersReturnCorrectValues() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(5L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password123")
                .admin(false)
                .build();

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getUsername()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getAdmin()).isFalse();
    }

    @Test
    void testAuthoritiesAlwaysEmpty() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).admin(true).build();

        assertThat(user1.getAuthorities()).isEmpty();
        assertThat(user2.getAuthorities()).isEmpty();
    }

    @Test
    void testAccountStatusAlwaysTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testEqualsWithDifferentFieldsSameId() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(5L)
                .username("user1@example.com")
                .firstName("First1")
                .lastName("Last1")
                .password("pass1")
                .admin(true)
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(5L)
                .username("user2@example.com")
                .firstName("First2")
                .lastName("Last2")
                .password("pass2")
                .admin(false)
                .build();

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testBuilderChaining() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(99L)
                .username("chain@test.com")
                .firstName("Chain")
                .lastName("Test")
                .password("chainPass")
                .admin(true)
                .build();

        assertThat(user.getId()).isEqualTo(99L);
        assertThat(user.getUsername()).isEqualTo("chain@test.com");
        assertThat(user.getFirstName()).isEqualTo("Chain");
        assertThat(user.getLastName()).isEqualTo("Test");
        assertThat(user.getPassword()).isEqualTo("chainPass");
        assertThat(user.getAdmin()).isTrue();
    }

    @Test
    void testMultipleBuildersIndependence() {
        UserDetailsImpl.UserDetailsImplBuilder builder1 = UserDetailsImpl.builder()
                .id(1L)
                .username("user1@test.com");

        UserDetailsImpl.UserDetailsImplBuilder builder2 = UserDetailsImpl.builder()
                .id(2L)
                .username("user2@test.com");

        UserDetailsImpl user1 = builder1.build();
        UserDetailsImpl user2 = builder2.build();

        assertThat(user1.getId()).isEqualTo(1L);
        assertThat(user1.getUsername()).isEqualTo("user1@test.com");
        assertThat(user2.getId()).isEqualTo(2L);
        assertThat(user2.getUsername()).isEqualTo("user2@test.com");
    }
}