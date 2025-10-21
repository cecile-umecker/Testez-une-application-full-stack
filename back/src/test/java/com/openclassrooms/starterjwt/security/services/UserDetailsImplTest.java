package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

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
        // Test que chaque méthode du builder fonctionne
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
        // Builder avec seulement certains champs
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
        // Builder avec des valeurs null explicites
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
        // Builder sans aucun setter appelé
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
        // Le builder a aussi une méthode toString()
        UserDetailsImpl.UserDetailsImplBuilder builder = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com");
        
        String builderString = builder.toString();
        assertThat(builderString).contains("UserDetailsImplBuilder");
    }

    @Test
    void testAllArgsConstructor() {
        // Test du constructeur @AllArgsConstructor
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
        // Test du constructeur avec des nulls
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
        // Deux objets avec le même id sont égaux
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

        // Equals basé sur l'id uniquement
        assertThat(user1).isEqualTo(user2); // même id
        assertThat(user1).isNotEqualTo(user3); // id différent
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo("string");
    }

    @Test
    void testEqualsSameInstance() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(10L)
                .username("test@example.com")
                .build();
        assertThat(user).isEqualTo(user); // same instance
    }

    @Test
    void testEqualsWithNullIds() {
        // Deux objets avec id null
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(null)
                .username("user1@example.com")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(null)
                .username("user2@example.com")
                .build();

        // Avec id null, ils doivent être égaux
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
        // Test le chaînage des appels du builder
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
        // Vérifier que deux builders sont indépendants
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