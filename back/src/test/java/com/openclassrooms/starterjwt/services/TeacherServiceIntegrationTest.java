package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TeacherServiceIntegrationTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @DirtiesContext
    void findAll_shouldReturnAllTeachersFromDatabase() {
        // Arrange
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");
        teacher1.setCreatedAt(LocalDateTime.now());
        teacher1.setUpdatedAt(LocalDateTime.now());

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        teacher2.setCreatedAt(LocalDateTime.now());
        teacher2.setUpdatedAt(LocalDateTime.now());

        Teacher teacher3 = new Teacher();
        teacher3.setFirstName("Bob");
        teacher3.setLastName("Johnson");
        teacher3.setCreatedAt(LocalDateTime.now());
        teacher3.setUpdatedAt(LocalDateTime.now());

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);
        teacherRepository.save(teacher3);

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        assertThat(result).extracting(Teacher::getFirstName)
                .contains("John", "Jane", "Bob");
    }

    @Test
    @DirtiesContext
    void findAll_shouldReturnListFromDatabase() {
        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DirtiesContext
    void findById_shouldReturnTeacherFromDatabase() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Alice");
        teacher.setLastName("Williams");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act
        Teacher result = teacherService.findById(savedTeacher.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedTeacher.getId());
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Williams");
    }

    @Test
    @DirtiesContext
    void findById_shouldReturnNull_whenTeacherDoesNotExistInDatabase() {
        // Act
        Teacher result = teacherService.findById(999999L);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DirtiesContext
    void findById_shouldReturnTeacherWithAllFields() {
        // Arrange 
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = new Teacher();
        teacher.setFirstName("Complete");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);
        
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act
        Teacher result = teacherService.findById(savedTeacher.getId());

        // Assert 
        assertThat(result.getId()).isEqualTo(savedTeacher.getId());
        assertThat(result.getFirstName()).isEqualTo("Complete");
        assertThat(result.getLastName()).isEqualTo("Teacher");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DirtiesContext
    void findById_shouldHandleSpecialCharactersInTeacherData() {
        // Arrange 
        Teacher teacher = new Teacher();
        teacher.setFirstName("François");
        teacher.setLastName("O'Connor-Müller");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act
        Teacher result = teacherService.findById(savedTeacher.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("François");
        assertThat(result.getLastName()).isEqualTo("O'Connor-Müller");
    }

    @Test
    @DirtiesContext
    void findAll_shouldReturnTeachersInCorrectOrder() {
        // Arrange 
        int initialCount = teacherService.findAll().size();
        
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("Adam");
        teacher1.setLastName("Anderson");
        teacher1.setCreatedAt(LocalDateTime.now());
        teacher1.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher1 = teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Barbara");
        teacher2.setLastName("Brown");
        teacher2.setCreatedAt(LocalDateTime.now());
        teacher2.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher2 = teacherRepository.save(teacher2);

        Teacher teacher3 = new Teacher();
        teacher3.setFirstName("Charles");
        teacher3.setLastName("Clark");
        teacher3.setCreatedAt(LocalDateTime.now());
        teacher3.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher3 = teacherRepository.save(teacher3);

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertThat(result).hasSizeGreaterThanOrEqualTo(initialCount + 3);
        
        assertThat(result).anyMatch(t -> t.getId().equals(savedTeacher1.getId()));
        assertThat(result).anyMatch(t -> t.getId().equals(savedTeacher2.getId()));
        assertThat(result).anyMatch(t -> t.getId().equals(savedTeacher3.getId()));
    }

    @Test
    @DirtiesContext
    void findAll_shouldReturnMultipleTeachersWithDifferentData() {
        // Arrange 
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("Emily");
        teacher1.setLastName("Davis");
        teacher1.setCreatedAt(LocalDateTime.now().minusDays(10));
        teacher1.setUpdatedAt(LocalDateTime.now().minusDays(5));
        teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Frank");
        teacher2.setLastName("Evans");
        teacher2.setCreatedAt(LocalDateTime.now().minusDays(20));
        teacher2.setUpdatedAt(LocalDateTime.now().minusDays(1));
        teacherRepository.save(teacher2);

        Teacher teacher3 = new Teacher();
        teacher3.setFirstName("Grace");
        teacher3.setLastName("Foster");
        teacher3.setCreatedAt(LocalDateTime.now().minusDays(30));
        teacher3.setUpdatedAt(LocalDateTime.now());
        teacherRepository.save(teacher3);

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        assertThat(result).allMatch(teacher -> teacher.getFirstName() != null);
        assertThat(result).allMatch(teacher -> teacher.getLastName() != null);
        assertThat(result).allMatch(teacher -> teacher.getCreatedAt() != null);
        assertThat(result).allMatch(teacher -> teacher.getUpdatedAt() != null);
    }

    @Test
    @DirtiesContext
    void findById_multipleTimesForSameTeacher_shouldReturnSameData() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Consistency");
        teacher.setLastName("Test");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);
        
        Long teacherId = savedTeacher.getId();

        // Act
        Teacher result1 = teacherService.findById(teacherId);
        Teacher result2 = teacherService.findById(teacherId);
        Teacher result3 = teacherService.findById(teacherId);

        // Assert
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result3).isNotNull();
        
        assertThat(result1.getId()).isEqualTo(result2.getId()).isEqualTo(result3.getId());
        assertThat(result1.getFirstName()).isEqualTo(result2.getFirstName()).isEqualTo(result3.getFirstName());
        assertThat(result1.getLastName()).isEqualTo(result2.getLastName()).isEqualTo(result3.getLastName());
    }

    @Test
    @DirtiesContext
    void findAll_afterAddingNewTeacher_shouldIncludeNewTeacher() {
        // Arrange
        int initialCount = teacherService.findAll().size();

        // Act
        Teacher newTeacher = new Teacher();
        newTeacher.setFirstName("NewTeacher");
        newTeacher.setLastName("AddedNow");
        newTeacher.setCreatedAt(LocalDateTime.now());
        newTeacher.setUpdatedAt(LocalDateTime.now());
        Teacher saved = teacherRepository.save(newTeacher);

        List<Teacher> updatedTeachers = teacherService.findAll();

        // Assert
        assertThat(updatedTeachers).hasSizeGreaterThan(initialCount);
        assertThat(updatedTeachers).anyMatch(t -> 
            t.getId().equals(saved.getId()) &&
            t.getFirstName().equals("NewTeacher") &&
            t.getLastName().equals("AddedNow")
        );
    }
}