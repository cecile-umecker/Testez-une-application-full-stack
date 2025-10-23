package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TeacherService Unit Test Suite
 * 
 * This test file contains unit tests for the TeacherService.
 * The TeacherService handles business logic for yoga teachers including retrieval operations.
 * Tests use mocked dependencies to isolate service logic.
 * 
 * Test Coverage:
 * 
 * FindAll Tests:
 * - findAll_shouldReturnAllTeachers: Tests retrieving all teachers,
 *   verifies TeacherRepository.findAll() is called,
 *   confirms correct number of teachers is returned (2 teachers)
 * 
 * FindById Tests:
 * - findById_shouldReturnTeacher_whenExists: Tests retrieving specific teacher by ID,
 *   verifies TeacherRepository.findById() is called with correct ID (1L),
 *   confirms correct teacher data is returned (id, firstName)
 * 
 * - findById_shouldReturnNull_whenNotExists: Tests null is returned for non-existent teacher ID,
 *   verifies TeacherRepository.findById() is called with ID (99L),
 *   confirms null is returned when Optional.empty()
 * 
 * TeacherService Methods Tested:
 * - findAll(): Retrieves all teachers from repository
 * - findById(Long): Finds specific teacher by ID, returns null if not found
 * 
 * Mocked Dependencies:
 * - TeacherRepository: Database operations for teachers
 * 
 * Test Configuration:
 * - Uses Mockito for mocking repository (@Mock, @InjectMocks)
 * - MockitoAnnotations.openMocks() called in @BeforeEach to initialize mocks
 * - Uses JUnit Jupiter assertions (assertNotNull, assertEquals, assertNull)
 * - Verifies repository method calls using Mockito.verify()
 */

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnAllTeachers() {
        // Arrange
        Teacher teacher1 = new Teacher();
        Teacher teacher2 = new Teacher();
        when(teacherRepository.findAll()).thenReturn(List.of(teacher1, teacher2));

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnTeacher_whenExists() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenNotExists() {
        // Arrange
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Teacher result = teacherService.findById(99L);

        // Assert
        assertNull(result);
        verify(teacherRepository, times(1)).findById(99L);
    }
}
