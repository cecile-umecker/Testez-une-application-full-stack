package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SessionController Unit Test Suite
 * 
 * This test file contains unit tests for the SessionController.
 * The SessionController handles CRUD operations for yoga sessions and user participation management.
 * Tests use mocked dependencies to isolate controller logic.
 * 
 * Test Coverage:
 * 
 * findById Tests:
 * - testFindByIdSuccess: Tests retrieving a session by valid ID,
 *   verifies session details are returned with 200 OK status
 * 
 * - testFindByIdNotFound: Tests retrieving non-existent session returns 404 Not Found
 * 
 * - testFindByIdBadRequest: Tests retrieving with invalid ID format returns 400 Bad Request
 * 
 * findAll Tests:
 * - testFindAll: Tests retrieving all sessions,
 *   verifies list of sessions is returned with correct data
 * 
 * create Tests:
 * - testCreateSuccess: Tests creating a new session with valid data,
 *   verifies session is created and returned with 200 OK status
 * 
 * update Tests:
 * - testUpdateSuccess: Tests updating an existing session with valid data,
 *   verifies updated session is returned with 200 OK status
 * 
 * - testUpdateBadRequest: Tests updating with invalid ID format returns 400 Bad Request
 * 
 * delete Tests:
 * - testDeleteSuccess: Tests deleting an existing session,
 *   verifies deletion succeeds with 200 OK status
 * 
 * - testDeleteNotFound: Tests deleting non-existent session returns 404 Not Found
 * 
 * - testDeleteBadRequest: Tests deleting with invalid ID format returns 400 Bad Request
 * 
 * participate Tests:
 * - testParticipateSuccess: Tests adding a user to a session,
 *   verifies participation succeeds with 200 OK status
 * 
 * - testParticipateBadRequest: Tests participation with invalid ID format returns 400 Bad Request
 * 
 * noLongerParticipate Tests:
 * - testNoLongerParticipateSuccess: Tests removing a user from a session,
 *   verifies removal succeeds with 200 OK status
 * 
 * - testNoLongerParticipateBadRequest: Tests removal with invalid ID format returns 400 Bad Request
 * 
 * Mocked Dependencies:
 * - SessionService: Handles business logic for session operations
 * - SessionMapper: Converts between Session entities and SessionDto objects
 * 
 * Test Configuration:
 * - @SpringBootTest: Loads full application context
 * - @AutoConfigureMockMvc: Auto-configures MockMvc
 * - @MockBean: Creates mock instances of dependencies
 * - @WithMockUser: Provides authenticated context for all tests
 */

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(1L);
        session.setName("Yoga");

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("Cours de yoga pour débutants");
    }

    // ================= findById =================
    @Test
    @WithMockUser
    void testFindByIdSuccess() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yoga"));

        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    @WithMockUser
    void testFindByIdNotFound() throws Exception {
        when(sessionService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/session/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(sessionService, times(1)).getById(99L);
        verifyNoInteractions(sessionMapper);
    }

    @Test
    @WithMockUser
    void testFindByIdBadRequest() throws Exception {
        mockMvc.perform(get("/api/session/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(sessionService);
        verifyNoInteractions(sessionMapper);
    }

    // ================= findAll =================
    @Test
    @WithMockUser
    void testFindAll() throws Exception {
        List<Session> sessions = List.of(session);
        List<SessionDto> sessionDtos = List.of(sessionDto);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Yoga"));

        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(sessions);
    }

    // ================= create =================
    @Test
    @WithMockUser
    void testCreateSuccess() throws Exception {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yoga"));

        verify(sessionMapper, times(1)).toEntity(sessionDto);
        verify(sessionService, times(1)).create(session);
        verify(sessionMapper, times(1)).toDto(session);
    }

    // ================= update =================
    @Test
    @WithMockUser
    void testUpdateSuccess() throws Exception {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(put("/api/session/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yoga"));

        verify(sessionMapper, times(1)).toEntity(sessionDto);
        verify(sessionService, times(1)).update(1L, session);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    @WithMockUser
    void testUpdateBadRequest() throws Exception {
        mockMvc.perform(put("/api/session/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(sessionService);
        verifyNoInteractions(sessionMapper);
    }

    // ================= delete =================
    @Test
    @WithMockUser
    void testDeleteSuccess() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);

        mockMvc.perform(delete("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser
    void testDeleteNotFound() throws Exception {
        when(sessionService.getById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser
    void testDeleteBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).delete(anyLong());
    }

    // ================= participate =================
    @Test
    @WithMockUser
    void testParticipateSuccess() throws Exception {
        doNothing().when(sessionService).participate(1L, 2L);

        mockMvc.perform(post("/api/session/1/participate/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).participate(1L, 2L);
    }

    @Test
    @WithMockUser
    void testParticipateBadRequest() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/def").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).participate(anyLong(), anyLong());
    }

    // ================= noLongerParticipate =================
    @Test
    @WithMockUser
    void testNoLongerParticipateSuccess() throws Exception {
        doNothing().when(sessionService).noLongerParticipate(1L, 2L);

        mockMvc.perform(delete("/api/session/1/participate/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
    }

    @Test
    @WithMockUser
    void testNoLongerParticipateBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/abc/participate/def").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}
