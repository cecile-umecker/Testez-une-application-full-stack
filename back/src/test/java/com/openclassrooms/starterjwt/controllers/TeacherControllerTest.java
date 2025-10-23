package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

/**
 * TeacherController Unit Test Suite
 * 
 * This test file contains unit tests for the TeacherController.
 * The TeacherController handles retrieval operations for yoga teachers.
 * Tests use mocked dependencies to isolate controller logic.
 * 
 * Test Coverage:
 * 
 * findById Tests:
 * - testFindByIdSuccess: Tests retrieving a teacher by valid ID,
 *   verifies teacher details (id, firstName, lastName) are returned with 200 OK status
 * 
 * - testFindByIdNotFound: Tests retrieving non-existent teacher returns 404 Not Found
 * 
 * - testFindByIdBadRequest: Tests retrieving with invalid ID format (non-numeric) returns 400 Bad Request
 * 
 * findAll Tests:
 * - testFindAll: Tests retrieving all teachers,
 *   verifies list of teachers is returned with correct data and 200 OK status
 * 
 * Mocked Dependencies:
 * - TeacherService: Handles business logic for teacher operations
 * - TeacherMapper: Converts between Teacher entities and TeacherDto objects
 * 
 * Test Configuration:
 * - @SpringBootTest: Loads full application context
 * - @AutoConfigureMockMvc: Auto-configures MockMvc
 * - @MockBean: Creates mock instances of dependencies
 * - @WithMockUser: Provides authenticated context for all tests with USER role
 * 
 * Test Data:
 * - teacher: Mock Teacher entity with id=1, firstName="John", lastName="Doe"
 * - teacherDto: Mock TeacherDto with matching data
 */

@SpringBootTest
@AutoConfigureMockMvc
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testFindByIdSuccess() throws Exception {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        mockMvc.perform(get("/api/teacher/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(1)).toDto(teacher);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testFindByIdNotFound() throws Exception {
        when(teacherService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(teacherService, times(1)).findById(99L);
        verifyNoInteractions(teacherMapper);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testFindByIdBadRequest() throws Exception {
        mockMvc.perform(get("/api/teacher/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(teacherService);
        verifyNoInteractions(teacherMapper);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testFindAll() throws Exception {
        List<Teacher> teachers = List.of(teacher);
        List<TeacherDto> teacherDtos = List.of(teacherDto);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(teachers);
    }
}
