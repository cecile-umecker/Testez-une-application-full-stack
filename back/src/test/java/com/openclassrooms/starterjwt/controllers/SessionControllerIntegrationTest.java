package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User user;
    private Session session;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user = userRepository.save(user);

        session = new Session();
        session.setName("Yoga Session");
        session.setDate(new Date());
        session.setDescription("Integration test session");
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>()); 
        session = sessionRepository.save(session);
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void testFindByIdIntegration() throws Exception {
        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("Integration test session"));
    }

    @Test
    @WithMockUser
    void testFindAllIntegration() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.name == 'Yoga Session')]").exists());
    }

    @Test
    @WithMockUser
    void testCreateIntegration() throws Exception {
        SessionDto newSessionDto = new SessionDto();
        newSessionDto.setName("New Integration Session");
        newSessionDto.setDate(new Date());
        newSessionDto.setTeacher_id(teacher.getId());
        newSessionDto.setDescription("New session description");

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Integration Session"))
                .andExpect(jsonPath("$.description").value("New session description"));
    }

    @Test
    @WithMockUser
    void testUpdateIntegration() throws Exception {
        SessionDto updatedDto = new SessionDto();
        updatedDto.setName("Updated Yoga Session");
        updatedDto.setDate(new Date());
        updatedDto.setTeacher_id(teacher.getId());
        updatedDto.setDescription("Updated description");

        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser
    void testDeleteIntegration() throws Exception {
        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testParticipateIntegration() throws Exception {
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testNoLongerParticipateIntegration() throws Exception {
        if (session.getUsers() == null) {
            session.setUsers(new ArrayList<>());
        }
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(0)));
    }

    @Test
    @WithMockUser
    void testCreateWithInvalidDataIntegration() throws Exception {
        SessionDto invalidDto = new SessionDto();
        invalidDto.setName("");
        
        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testFindByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/session/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testParticipateSessionNotFound() throws Exception {
        mockMvc.perform(post("/api/session/999999/participate/" + user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testParticipateUserNotFound() throws Exception {
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testParticipateAlreadyParticipating() throws Exception {
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testNoLongerParticipateSessionNotFound() throws Exception {
        mockMvc.perform(delete("/api/session/999999/participate/" + user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testNoLongerParticipateUserNotParticipating() throws Exception {
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testDeleteInvalidId() throws Exception {
        mockMvc.perform(delete("/api/session/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testFindByIdInvalidId() throws Exception {
        mockMvc.perform(get("/api/session/invalid"))
                .andExpect(status().isBadRequest());
    }
}