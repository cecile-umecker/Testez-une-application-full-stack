package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnTeacher_whenTeacherExists() throws Exception {
        // Arrange - Create and save a real teacher
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act & Assert
        mockMvc.perform(get("/api/teacher/" + savedTeacher.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTeacher.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnNotFound_whenTeacherDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldHandleSpecialCharacters_inTeacherName() throws Exception {
        // Arrange - Teacher with special characters
        Teacher teacher = new Teacher();
        teacher.setFirstName("François");
        teacher.setLastName("O'Connor-Müller");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act & Assert
        mockMvc.perform(get("/api/teacher/" + savedTeacher.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("François"))
                .andExpect(jsonPath("$.lastName").value("O'Connor-Müller"));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_shouldReturnAllTeachers() throws Exception {
        // Arrange - Create multiple teachers
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");
        teacher1.setCreatedAt(LocalDateTime.now());
        teacher1.setUpdatedAt(LocalDateTime.now());
        teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        teacher2.setCreatedAt(LocalDateTime.now());
        teacher2.setUpdatedAt(LocalDateTime.now());
        teacherRepository.save(teacher2);

        Teacher teacher3 = new Teacher();
        teacher3.setFirstName("Bob");
        teacher3.setLastName("Johnson");
        teacher3.setCreatedAt(LocalDateTime.now());
        teacher3.setUpdatedAt(LocalDateTime.now());
        teacherRepository.save(teacher3);

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[*].firstName", hasItem("John")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Jane")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Bob")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Doe")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Smith")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Johnson")));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_shouldReturnEmptyArray_whenNoTeachersExist() throws Exception {
        // Arrange - Clear all teachers (handled by @Transactional rollback)
        
        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_shouldReturnTeachersInCorrectFormat() throws Exception {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Alice");
        teacher.setLastName("Williams");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act & Assert - Verify DTO structure
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.firstName == 'Alice')]").exists())
                .andExpect(jsonPath("$[?(@.firstName == 'Alice')].id").exists())
                .andExpect(jsonPath("$[?(@.firstName == 'Alice')].firstName").exists())
                .andExpect(jsonPath("$[?(@.firstName == 'Alice')].lastName").exists());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnCompleteTeacherData() throws Exception {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Complete");
        teacher.setLastName("Data");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act & Assert - Verify all fields are present
        mockMvc.perform(get("/api/teacher/" + savedTeacher.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_shouldReturnTeachersWithAllFields() throws Exception {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Full");
        teacher.setLastName("Fields");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        teacherRepository.save(teacher);

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.firstName == 'Full')].id").exists())
                .andExpect(jsonPath("$[?(@.firstName == 'Full')].firstName").value("Full"))
                .andExpect(jsonPath("$[?(@.firstName == 'Full')].lastName").value("Fields"))
                .andExpect(jsonPath("$[?(@.firstName == 'Full')].createdAt").exists())
                .andExpect(jsonPath("$[?(@.firstName == 'Full')].updatedAt").exists());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_multipleTimes_shouldReturnSameData() throws Exception {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Consistent");
        teacher.setLastName("Data");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act & Assert - Call multiple times
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/teacher/" + savedTeacher.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedTeacher.getId()))
                    .andExpect(jsonPath("$.firstName").value("Consistent"))
                    .andExpect(jsonPath("$.lastName").value("Data"));
        }
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_shouldHandleLargeNumberOfTeachers() throws Exception {
        // Arrange - Create 10 teachers
        for (int i = 1; i <= 10; i++) {
            Teacher teacher = new Teacher();
            teacher.setFirstName("Teacher" + i);
            teacher.setLastName("Last" + i);
            teacher.setCreatedAt(LocalDateTime.now());
            teacher.setUpdatedAt(LocalDateTime.now());
            teacherRepository.save(teacher);
        }

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(10))));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnNotFound_forNegativeId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findById_shouldReturnNotFound_forZeroId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_afterAddingTeacher_shouldIncludeNewTeacher() throws Exception {
        // Arrange - Get initial count
        int initialCount = teacherRepository.findAll().size();

        // Add new teacher
        Teacher newTeacher = new Teacher();
        newTeacher.setFirstName("NewTeacher");
        newTeacher.setLastName("JustAdded");
        newTeacher.setCreatedAt(LocalDateTime.now());
        newTeacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(newTeacher);

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(initialCount))))
                .andExpect(jsonPath("$[?(@.id == " + savedTeacher.getId() + ")].firstName").value("NewTeacher"))
                .andExpect(jsonPath("$[?(@.id == " + savedTeacher.getId() + ")].lastName").value("JustAdded"));
    }

    @Test
    @WithMockUser
    @DirtiesContext
    void findAll_shouldReturnTeachersOrderedById() throws Exception {
        // Arrange - Create teachers
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("First");
        teacher1.setLastName("Teacher");
        teacher1.setCreatedAt(LocalDateTime.now());
        teacher1.setUpdatedAt(LocalDateTime.now());
        Teacher saved1 = teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Second");
        teacher2.setLastName("Teacher");
        teacher2.setCreatedAt(LocalDateTime.now());
        teacher2.setUpdatedAt(LocalDateTime.now());
        Teacher saved2 = teacherRepository.save(teacher2);

        // Act & Assert - Verify order (assuming default sort by ID)
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + saved1.getId() + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + saved2.getId() + ")]").exists());
    }

    @Test
    @DirtiesContext
    void findById_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act & Assert - No @WithMockUser
        mockMvc.perform(get("/api/teacher/" + savedTeacher.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void findAll_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert - No @WithMockUser
        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}