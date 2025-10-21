package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

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

        // Vérification des getters
        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Yoga Class");
        assertThat(session.getDate()).isEqualTo(nowDate);
        assertThat(session.getDescription()).isEqualTo("Morning Yoga");
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(user1, user2);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getUpdatedAt()).isEqualTo(now);

        // Test setter et chaining
        session.setName("Pilates")
                .setDescription("Evening Class")
                .setUsers(List.of(user1));
        assertThat(session.getName()).isEqualTo("Pilates");
        assertThat(session.getDescription()).isEqualTo("Evening Class");
        assertThat(session.getUsers()).containsExactly(user1);

        // Test equals/hashCode (id seulement)
        Session sameId = Session.builder().id(1L).build();
        Session diffId = Session.builder().id(2L).build();
        assertThat(session).isEqualTo(sameId);
        assertThat(session).isNotEqualTo(diffId);
        assertThat(session.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(session.hashCode()).isNotEqualTo(diffId.hashCode());

        // Test toString contient des infos utiles
        assertThat(session.toString()).contains("id=1", "name=Pilates", "description=Evening Class");
    }

    @Test
    void testSetDate() {
        Session session = new Session();
        Date date1 = new Date();
        Date date2 = new Date(System.currentTimeMillis() + 100000);

        // Test setDate et vérification
        session.setDate(date1);
        assertThat(session.getDate()).isEqualTo(date1);

        // Changement de date
        session.setDate(date2);
        assertThat(session.getDate()).isEqualTo(date2);

        // Test avec null
        session.setDate(null);
        assertThat(session.getDate()).isNull();
    }

    @Test
    void testSetTeacher() {
        Session session = new Session();
        Teacher teacher1 = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        Teacher teacher2 = Teacher.builder().id(2L).firstName("Jane").lastName("Smith").build();

        // Test setTeacher et vérification
        session.setTeacher(teacher1);
        assertThat(session.getTeacher()).isEqualTo(teacher1);

        // Changement de teacher
        session.setTeacher(teacher2);
        assertThat(session.getTeacher()).isEqualTo(teacher2);

        // Test avec null
        session.setTeacher(null);
        assertThat(session.getTeacher()).isNull();
    }

    @Test
    void testSetCreatedAt() {
        Session session = new Session();
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusDays(1);

        // Test setCreatedAt et vérification
        session.setCreatedAt(time1);
        assertThat(session.getCreatedAt()).isEqualTo(time1);

        // Changement de createdAt
        session.setCreatedAt(time2);
        assertThat(session.getCreatedAt()).isEqualTo(time2);

        // Test avec null
        session.setCreatedAt(null);
        assertThat(session.getCreatedAt()).isNull();
    }

    @Test
    void testSetUpdatedAt() {
        Session session = new Session();
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusDays(1);

        // Test setUpdatedAt et vérification
        session.setUpdatedAt(time1);
        assertThat(session.getUpdatedAt()).isEqualTo(time1);

        // Changement de updatedAt
        session.setUpdatedAt(time2);
        assertThat(session.getUpdatedAt()).isEqualTo(time2);

        // Test avec null
        session.setUpdatedAt(null);
        assertThat(session.getUpdatedAt()).isNull();
    }

    @Test
    void testHashCodeAndEqualsWithNullId() {
        // Test avec id = null
        Session nullId1 = Session.builder()
                .name("Session 1")
                .description("Description 1")
                .build();

        Session nullId2 = Session.builder()
                .name("Session 2")
                .description("Description 2")
                .build();

        // Deux objets avec id=null doivent être égaux
        assertThat(nullId1).isEqualTo(nullId2);
        // Et avoir le même hashCode (contrat equals/hashCode)
        assertThat(nullId1.hashCode()).isEqualTo(nullId2.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        Session session = Session.builder()
                .id(1L)
                .name("Yoga")
                .build();

        // Test equals avec null
        assertThat(session.equals(null)).isFalse();

        // Test equals avec un type différent
        assertThat(session.equals("string")).isFalse();

        // Test equals avec lui-même
        assertThat(session.equals(session)).isTrue();
    }

    @Test
    void testHashCodeConsistency() {
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Class")
                .description("Morning Yoga")
                .build();

        // Le hashCode doit rester constant pour le même objet
        int hash1 = session.hashCode();
        int hash2 = session.hashCode();
        assertThat(hash1).isEqualTo(hash2);

        // Même après modification d'autres champs (si equals/hashCode basés sur id uniquement)
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

        // Des ids différents doivent produire des hashCodes différents
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

        // Vérifier que les setters retournent l'instance (chaining)
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