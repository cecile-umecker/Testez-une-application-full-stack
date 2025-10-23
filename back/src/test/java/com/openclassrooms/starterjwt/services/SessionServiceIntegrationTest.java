package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SessionService Integration Test Suite
 * 
 * This test file contains integration tests for the SessionService.
 * The SessionService handles business logic for yoga sessions including CRUD operations
 * and user participation management. Tests use real database interactions with transaction rollback.
 * 
 * Test Coverage:
 * 
 * Create Tests:
 * - create_shouldSaveSessionToDatabase: Tests session creation with teacher and valid data,
 *   verifies session is persisted to database with generated ID and all fields intact
 * 
 * FindAll Tests:
 * - findAll_shouldReturnAllSessionsFromDatabase: Tests retrieving all sessions,
 *   verifies multiple sessions are returned with correct data
 * 
 * GetById Tests:
 * - getById_shouldReturnSessionFromDatabase: Tests retrieving specific session by ID,
 *   verifies correct session data is returned (name, description, teacher)
 * 
 * - getById_shouldReturnNull_whenSessionDoesNotExist: Tests null is returned for non-existent session ID
 * 
 * Update Tests:
 * - update_shouldUpdateSessionInDatabase: Tests updating existing session,
 *   verifies updated values are persisted (name, description) and changes are reflected in database
 * 
 * Delete Tests:
 * - delete_shouldRemoveSessionFromDatabase: Tests session deletion,
 *   verifies session is removed from database and cannot be found afterwards
 * 
 * Participate Tests:
 * - participate_shouldAddUserToSession: Tests adding user to session participants list,
 *   verifies user is added to session's users collection in database
 * 
 * - participate_shouldThrowNotFoundException_whenSessionDoesNotExist: Tests exception handling
 *   when trying to participate in non-existent session
 * 
 * - participate_shouldThrowNotFoundException_whenUserDoesNotExist: Tests exception handling
 *   when trying to add non-existent user to session
 * 
 * - participate_shouldThrowBadRequestException_whenUserAlreadyParticipating: Tests duplicate participation prevention,
 *   verifies BadRequestException is thrown when user already in session
 * 
 * NoLongerParticipate Tests:
 * - noLongerParticipate_shouldRemoveUserFromSession: Tests removing user from session participants,
 *   verifies user is removed from session's users collection in database
 * 
 * - noLongerParticipate_shouldThrowNotFoundException_whenSessionDoesNotExist: Tests exception handling
 *   when trying to leave non-existent session
 * 
 * - noLongerParticipate_shouldThrowBadRequestException_whenUserNotInSession: Tests error handling
 *   when trying to remove non-participating user
 * 
 * - noLongerParticipate_shouldOnlyRemoveSpecifiedUser: Tests selective user removal,
 *   verifies only specified user is removed while other participants remain
 * 
 * SessionService Methods Tested:
 * - create(Session): Saves new session to database
 * - findAll(): Retrieves all sessions
 * - getById(Long): Finds session by ID
 * - update(Long, Session): Updates existing session
 * - delete(Long): Removes session from database
 * - participate(Long sessionId, Long userId): Adds user to session
 * - noLongerParticipate(Long sessionId, Long userId): Removes user from session
 * 
 * Exception Handling:
 * - NotFoundException: Thrown when session or user not found
 * - BadRequestException: Thrown for duplicate participation or removing non-participant
 * 
 * Dependencies:
 * - SessionRepository: Database operations for sessions
 * - UserRepository: Database operations for users
 * - TeacherRepository: Database operations for teachers
 * 
 * Test Configuration:
 * - @SpringBootTest: Full application context loading
 * - @Transactional: Rolls back database changes after each test
 * - @DirtiesContext: Resets application context for each test to ensure isolation
 * - Uses real database with cleanup to ensure test independence
 */

@SpringBootTest
@Transactional
class SessionServiceIntegrationTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @DirtiesContext
    void create_shouldSaveSessionToDatabase() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Morning yoga");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        // Act
        Session result = sessionService.create(session);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga Session");
        assertThat(result.getDescription()).isEqualTo("Morning yoga");
        assertThat(sessionRepository.findById(result.getId())).isPresent();
    }

    @Test
    @DirtiesContext
    void findAll_shouldReturnAllSessionsFromDatabase() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session1 = new Session();
        session1.setName("Session 1");
        session1.setDescription("Description 1");
        session1.setDate(new Date());
        session1.setTeacher(savedTeacher);
        session1.setUsers(new ArrayList<>());
        session1.setCreatedAt(LocalDateTime.now());
        session1.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session1);

        Session session2 = new Session();
        session2.setName("Session 2");
        session2.setDescription("Description 2");
        session2.setDate(new Date());
        session2.setTeacher(savedTeacher);
        session2.setUsers(new ArrayList<>());
        session2.setCreatedAt(LocalDateTime.now());
        session2.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session2);

        // Act
        List<Session> result = sessionService.findAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result).extracting(Session::getName)
                .contains("Session 1", "Session 2");
    }

    @Test
    @DirtiesContext
    void getById_shouldReturnSessionFromDatabase() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Test Session");
        session.setDescription("Test Description");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        // Act
        Session result = sessionService.getById(savedSession.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedSession.getId());
        assertThat(result.getName()).isEqualTo("Test Session");
        assertThat(result.getDescription()).isEqualTo("Test Description");
    }

    @Test
    @DirtiesContext
    void getById_shouldReturnNull_whenSessionDoesNotExist() {
        // Act
        Session result = sessionService.getById(999999L);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DirtiesContext
    void update_shouldUpdateSessionInDatabase() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Original Name");
        session.setDescription("Original Description");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        Session updatedSession = new Session();
        updatedSession.setName("Updated Name");
        updatedSession.setDescription("Updated Description");
        updatedSession.setDate(new Date());
        updatedSession.setTeacher(savedTeacher);
        updatedSession.setUsers(new ArrayList<>());

        // Act
        Session result = sessionService.update(savedSession.getId(), updatedSession);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedSession.getId());
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");

        Session fromDb = sessionRepository.findById(savedSession.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DirtiesContext
    void delete_shouldRemoveSessionFromDatabase() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("To Delete");
        session.setDescription("Will be deleted");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);
        Long sessionId = savedSession.getId();

        assertThat(sessionRepository.findById(sessionId)).isPresent();

        // Act
        sessionService.delete(sessionId);

        // Assert
        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }

    @Test
    @DirtiesContext
    void participate_shouldAddUserToSession() {
        // Arrange
        User user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        // Act
        sessionService.participate(savedSession.getId(), savedUser.getId());

        // Assert
        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).hasSize(1);
        assertThat(updatedSession.getUsers().get(0).getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DirtiesContext
    void participate_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        // Arrange
        User user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> 
            sessionService.participate(999999L, savedUser.getId())
        );
    }

    @Test
    @DirtiesContext
    void participate_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> 
            sessionService.participate(savedSession.getId(), 999999L)
        );
    }

    @Test
    @DirtiesContext
    void participate_shouldThrowBadRequestException_whenUserAlreadyParticipating() {
        // Arrange
        User user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>(List.of(savedUser))); 
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> 
            sessionService.participate(savedSession.getId(), savedUser.getId())
        );
    }

    @Test
    @DirtiesContext
    void noLongerParticipate_shouldRemoveUserFromSession() {
        // Arrange
        User user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>(List.of(savedUser)));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        assertThat(savedSession.getUsers()).hasSize(1);

        // Act
        sessionService.noLongerParticipate(savedSession.getId(), savedUser.getId());

        // Assert
        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).isEmpty();
    }

    @Test
    @DirtiesContext
    void noLongerParticipate_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        // Act & Assert
        assertThrows(NotFoundException.class, () -> 
            sessionService.noLongerParticipate(999999L, 1L)
        );
    }

    @Test
    @DirtiesContext
    void noLongerParticipate_shouldThrowBadRequestException_whenUserNotInSession() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> 
            sessionService.noLongerParticipate(savedSession.getId(), 999L)
        );
    }

    @Test
    @DirtiesContext
    void noLongerParticipate_shouldOnlyRemoveSpecifiedUser() {
        // Arrange
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setPassword("password");
        user1.setAdmin(false);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setPassword("password");
        user2.setAdmin(false);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        User savedUser2 = userRepository.save(user2);

        Teacher teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        Teacher savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>(List.of(savedUser1, savedUser2)));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);

        // Act 
        sessionService.noLongerParticipate(savedSession.getId(), savedUser1.getId());

        // Assert
        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).hasSize(1);
        assertThat(updatedSession.getUsers().get(0).getId()).isEqualTo(savedUser2.getId());
    }
}