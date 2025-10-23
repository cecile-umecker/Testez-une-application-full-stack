package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Session Model Unit Test Suite
 * 
 * This test file contains unit tests for the Session entity model.
 * The Session model represents a yoga session with associated teacher and participating users.
 * Tests verify builder pattern, getters/setters, equals/hashCode contracts, and method chaining.
 * 
 * Test Coverage:
 * 
 * Builder and Getters/Setters Tests:
 * - testBuilderAndGettersSetters: Tests Session.builder() creates entity with all fields,
 *   verifies getters return correct values, tests setter method chaining,
 *   validates equals/hashCode based on ID, and checks toString() output
 * 
 * Individual Setter Tests:
 * - testSetDate: Tests setDate() with valid dates and null value
 * 
 * - testSetTeacher: Tests setTeacher() with different teachers and null value
 * 
 * - testSetCreatedAt: Tests setCreatedAt() with different timestamps and null value
 * 
 * - testSetUpdatedAt: Tests setUpdatedAt() with different timestamps and null value
 * 
 * Equals and HashCode Tests:
 * - testHashCodeAndEqualsWithNullId: Tests sessions with null IDs are considered equal,
 *   verifies consistent hashCode for null IDs
 * 
 * - testEqualsWithNullAndDifferentType: Tests equals() returns false for null and different types,
 *   verifies reflexive property (session.equals(session) is true)
 * 
 * - testHashCodeConsistency: Tests hashCode() returns same value on multiple calls,
 *   verifies hashCode remains consistent when non-ID fields change
 * 
 * - testHashCodeDifferentForDifferentIds: Tests sessions with different IDs have different hashCodes
 * 
 * Setter Chaining Test:
 * - testSettersChaining: Tests all setters return the Session instance for method chaining,
 *   verifies fluent API pattern works correctly
 * 
 * Entity Properties Tested:
 * - id: Unique identifier (Long)
 * - name: Session name (String)
 * - date: Session date (Date)
 * - description: Session description (String)
 * - teacher: Associated teacher (Teacher entity)
 * - users: List of participating users (List<User>)
 * - createdAt: Creation timestamp (LocalDateTime)
 * - updatedAt: Last update timestamp (LocalDateTime)
 * 
 * Test Configuration:
 * - Uses AssertJ for fluent assertions
 * - Tests builder pattern, setters, equals/hashCode contract, and toString()
 */

class SessionTest {

    @Test
    void testBuilderAndGettersSetters() {
        Date nowDate = new Date();
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
        User user1 = User.builder().id(1L).email("a@ex.com").firstName("A").lastName("B").password("pass").admin(false).build();
        User user2 = User.builder().id(2L).email("b@ex.com").firstName("C").lastName("D").password("pass").admin(false).build();

        Session session = Session.builder()
                .id(1L)
                .name("Yoga Class")
                .date(nowDate)
                .description("Morning Yoga")
                .teacher(teacher)
                .users(List.of(user1, user2))
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Yoga Class");
        assertThat(session.getDate()).isEqualTo(nowDate);
        assertThat(session.getDescription()).isEqualTo("Morning Yoga");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(user1, user2);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getUpdatedAt()).isEqualTo(now);

        session.setName("Pilates")
                .setDescription("Evening Class")
                .setUsers(List.of(user1));
        assertThat(session.getName()).isEqualTo("Pilates");
        assertThat(session.getDescription()).isEqualTo("Evening Class");
        assertThat(session.getUsers()).containsExactly(user1);

        Session sameId = Session.builder().id(1L).build();
        Session diffId = Session.builder().id(2L).build();
        assertThat(session).isEqualTo(sameId);
        assertThat(session).isNotEqualTo(diffId);
        assertThat(session.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(session.hashCode()).isNotEqualTo(diffId.hashCode());

        assertThat(session.toString()).contains("id=1", "name=Pilates", "description=Evening Class");
    }

    @Test
    void testSetDate() {
        Session session = new Session();
        Date date1 = new Date();
        Date date2 = new Date(System.currentTimeMillis() + 100000);

        session.setDate(date1);
        assertThat(session.getDate()).isEqualTo(date1);

        session.setDate(date2);
        assertThat(session.getDate()).isEqualTo(date2);

        session.setDate(null);
        assertThat(session.getDate()).isNull();
    }

    @Test
    void testSetTeacher() {
        Session session = new Session();
        Teacher teacher1 = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        Teacher teacher2 = Teacher.builder().id(2L).firstName("Jane").lastName("Smith").build();

        session.setTeacher(teacher1);
        assertThat(session.getTeacher()).isEqualTo(teacher1);

        session.setTeacher(teacher2);
        assertThat(session.getTeacher()).isEqualTo(teacher2);

        session.setTeacher(null);
        assertThat(session.getTeacher()).isNull();
    }

    @Test
    void testSetCreatedAt() {
        Session session = new Session();
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusDays(1);

        session.setCreatedAt(time1);
        assertThat(session.getCreatedAt()).isEqualTo(time1);

        session.setCreatedAt(time2);
        assertThat(session.getCreatedAt()).isEqualTo(time2);

        session.setCreatedAt(null);
        assertThat(session.getCreatedAt()).isNull();
    }

    @Test
    void testSetUpdatedAt() {
        Session session = new Session();
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusDays(1);

        session.setUpdatedAt(time1);
        assertThat(session.getUpdatedAt()).isEqualTo(time1);

        session.setUpdatedAt(time2);
        assertThat(session.getUpdatedAt()).isEqualTo(time2);

        session.setUpdatedAt(null);
        assertThat(session.getUpdatedAt()).isNull();
    }

    @Test
    void testHashCodeAndEqualsWithNullId() {
        Session nullId1 = Session.builder()
                .name("Session 1")
                .description("Description 1")
                .build();

        Session nullId2 = Session.builder()
                .name("Session 2")
                .description("Description 2")
                .build();

        assertThat(nullId1).isEqualTo(nullId2);
        assertThat(nullId1.hashCode()).isEqualTo(nullId2.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        Session session = Session.builder()
                .id(1L)
                .name("Yoga")
                .build();

        assertThat(session.equals(null)).isFalse();

        assertThat(session.equals("string")).isFalse();

        assertThat(session.equals(session)).isTrue();
    }

    @Test
    void testHashCodeConsistency() {
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Class")
                .description("Morning Yoga")
                .build();

        int hash1 = session.hashCode();
        int hash2 = session.hashCode();
        assertThat(hash1).isEqualTo(hash2);

        session.setName("Pilates");
        session.setDescription("Evening Pilates");
        int hash3 = session.hashCode();
        assertThat(hash1).isEqualTo(hash3);
    }

    @Test
    void testHashCodeDifferentForDifferentIds() {
        Session session1 = Session.builder().id(1L).name("Session 1").build();
        Session session2 = Session.builder().id(2L).name("Session 2").build();
        Session session3 = Session.builder().id(3L).name("Session 3").build();

        assertThat(session1.hashCode()).isNotEqualTo(session2.hashCode());
        assertThat(session1.hashCode()).isNotEqualTo(session3.hashCode());
        assertThat(session2.hashCode()).isNotEqualTo(session3.hashCode());
    }

    @Test
    void testSettersChaining() {
        Session session = new Session();
        Date date = new Date();
        Teacher teacher = Teacher.builder().id(1L).firstName("John").build();
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().id(1L).email("a@ex.com").firstName("A").lastName("B").password("p").admin(false).build();

        Session result = session
                .setName("Test")
                .setDescription("Description")
                .setDate(date)
                .setTeacher(teacher)
                .setUsers(List.of(user))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        assertThat(result).isSameAs(session);
        assertThat(session.getName()).isEqualTo("Test");
        assertThat(session.getDescription()).isEqualTo("Description");
        assertThat(session.getDate()).isEqualTo(date);
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(user);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getUpdatedAt()).isEqualTo(now);
    }
}