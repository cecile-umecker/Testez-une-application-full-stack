package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void testFullCoverage() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Test du constructeur @RequiredArgsConstructor
        User userReq = new User("john@example.com", "Doe", "John", "secret", true);
        assertThat(userReq.getEmail()).isEqualTo("john@example.com");
        assertThat(userReq.getLastName()).isEqualTo("Doe");
        assertThat(userReq.getFirstName()).isEqualTo("John");
        assertThat(userReq.getPassword()).isEqualTo("secret");
        assertThat(userReq.isAdmin()).isTrue();

        // 1bis. Test du constructeur avec champ null → Lombok @NonNull déclenche NPE
        assertThrows(NullPointerException.class, () -> new User(null, "Doe", "John", "secret", true));
        assertThrows(NullPointerException.class, () -> new User("john@example.com", null, "John", "secret", true));
        assertThrows(NullPointerException.class, () -> new User("john@example.com", "Doe", null, "secret", true));
        assertThrows(NullPointerException.class, () -> new User("john@example.com", "Doe", "John", null, true));

        // 2. Test du constructeur @AllArgsConstructor
        User userAll = new User(1L, "mail@test.com", "Last", "First", "pwd", false, now, now);
        assertThat(userAll.getId()).isEqualTo(1L);
        assertThat(userAll.getCreatedAt()).isEqualTo(now);
        assertThat(userAll.getUpdatedAt()).isEqualTo(now);

        // 2bis. Test AllArgsConstructor avec nulls → NPE sur @NonNull
        assertThrows(NullPointerException.class, () -> new User(1L, null, "Last", "First", "pwd", false, now, now));
        assertThrows(NullPointerException.class, () -> new User(1L, "mail@test.com", null, "First", "pwd", false, now, now));
        assertThrows(NullPointerException.class, () -> new User(1L, "mail@test.com", "Last", null, "pwd", false, now, now));
        assertThrows(NullPointerException.class, () -> new User(1L, "mail@test.com", "Last", "First", null, false, now, now));

        // 3. Test du builder et des setters
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

        // Setters explicites
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

        // Setters avec null → NPE pour @NonNull
        assertThrows(NullPointerException.class, () -> user.setEmail(null));
        assertThrows(NullPointerException.class, () -> user.setLastName(null));
        assertThrows(NullPointerException.class, () -> user.setFirstName(null));
        assertThrows(NullPointerException.class, () -> user.setPassword(null));

        // 4. Test equals/hashCode complet
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

        // id non null
        assertThat(user).isEqualTo(sameId);
        assertThat(user).isNotEqualTo(diffId);
        assertThat(user.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(user.hashCode()).isNotEqualTo(diffId.hashCode());

        // id null → equals true si les deux id=null
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
        // Si les deux objets sont égaux, leurs hashCodes doivent être égaux
        assertThat(nullId1.hashCode()).isEqualTo(nullId2.hashCode());

        // equals avec null et autre type
        assertThat(user.equals(null)).isFalse();
        assertThat(user.equals("string")).isFalse();

        // 5. Test toString
        String toString = user.toString();
        assertThat(toString).contains("changed@mail.com", "Changed", "User");
    }

}
