package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherTest {

    @Test
    void testBuilderAndGettersSetters() {
        LocalDateTime now = LocalDateTime.now();

        // Création via builder
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Vérification des getters
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);

        // Test des setters avec chaining
        teacher.setFirstName("Jane").setLastName("Smith");
        assertThat(teacher.getFirstName()).isEqualTo("Jane");
        assertThat(teacher.getLastName()).isEqualTo("Smith");

        // Test equals/hashCode basés sur id
        Teacher sameId = Teacher.builder().id(1L).build();
        Teacher diffId = Teacher.builder().id(2L).build();
        assertThat(teacher).isEqualTo(sameId);
        assertThat(teacher).isNotEqualTo(diffId);
        assertThat(teacher.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(teacher.hashCode()).isNotEqualTo(diffId.hashCode());

        // Test toString
        assertThat(teacher.toString()).contains("id=1", "firstName=Jane", "lastName=Smith");
    }

    @Test
    void testSettersAndGettersAndToStringEqualsHashCode() {
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Doe");
        teacher.setFirstName("John");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);

        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);

        // equals / hashCode - cas avec même id
        Teacher same = new Teacher();
        same.setId(1L);
        Teacher diff = new Teacher();
        diff.setId(2L);

        assertThat(teacher).isEqualTo(same);
        assertThat(teacher).isNotEqualTo(diff);
        assertThat(teacher.hashCode()).isEqualTo(same.hashCode());
        assertThat(teacher.hashCode()).isNotEqualTo(diff.hashCode());

        // toString coverage
        String toString = teacher.toString();
        assertThat(toString).contains("John", "Doe");

        // test des setters en chainage si Lombok Accessors(chain = true)
        teacher.setFirstName("Jane").setLastName("Smith");
        assertThat(teacher.getFirstName()).isEqualTo("Jane");
        assertThat(teacher.getLastName()).isEqualTo("Smith");
    }

    @Test
    void testHashCodeAndEqualsWithNullId() {
        // Test avec id = null
        Teacher nullId1 = Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        Teacher nullId2 = Teacher.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        // Deux objets avec id=null doivent être égaux
        assertThat(nullId1).isEqualTo(nullId2);
        // Et avoir le même hashCode (contrat equals/hashCode)
        assertThat(nullId1.hashCode()).isEqualTo(nullId2.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        Teacher teacher = Teacher.builder().id(1L).firstName("John").build();

        // Test equals avec null
        assertThat(teacher.equals(null)).isFalse();

        // Test equals avec un type différent
        assertThat(teacher.equals("string")).isFalse();

        // Test equals avec lui-même
        assertThat(teacher.equals(teacher)).isTrue();
    }

    @Test
    void testHashCodeConsistency() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        // Le hashCode doit rester constant pour le même objet
        int hash1 = teacher.hashCode();
        int hash2 = teacher.hashCode();
        assertThat(hash1).isEqualTo(hash2);

        // Même après modification d'autres champs (si equals/hashCode basés sur id uniquement)
        teacher.setFirstName("Jane");
        int hash3 = teacher.hashCode();
        assertThat(hash1).isEqualTo(hash3);
    }

    @Test
    void testHashCodeDifferentForDifferentIds() {
        Teacher teacher1 = Teacher.builder().id(1L).build();
        Teacher teacher2 = Teacher.builder().id(2L).build();
        Teacher teacher3 = Teacher.builder().id(3L).build();

        // Des ids différents doivent produire des hashCodes différents
        assertThat(teacher1.hashCode()).isNotEqualTo(teacher2.hashCode());
        assertThat(teacher1.hashCode()).isNotEqualTo(teacher3.hashCode());
        assertThat(teacher2.hashCode()).isNotEqualTo(teacher3.hashCode());
    }
}