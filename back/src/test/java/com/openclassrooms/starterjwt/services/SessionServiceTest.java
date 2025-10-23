package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SessionService Unit Test Suite
 * 
 * This test file contains unit tests for the SessionService.
 * The SessionService handles business logic for yoga sessions including CRUD operations
 * and user participation management. Tests use mocked dependencies to isolate service logic.
 * 
 * Test Coverage:
 * 
 * Create Tests:
 * - testCreate: Tests session creation,
 *   verifies SessionRepository.save() is called with correct session object,
 *   confirms created session is returned
 * 
 * FindAll Tests:
 * - testFindAll: Tests retrieving all sessions,
 *   verifies SessionRepository.findAll() is called,
 *   confirms correct number of sessions is returned
 * 
 * GetById Tests:
 * - testGetById: Tests retrieving specific session by ID,
 *   verifies SessionRepository.findById() is called with correct ID,
 *   confirms correct session is returned
 * 
 * Update Tests:
 * - testUpdate: Tests updating existing session,
 *   verifies session ID is set correctly,
 *   confirms SessionRepository.save() is called and updated session is returned
 * 
 * Delete Tests:
 * - testDelete: Tests session deletion,
 *   verifies SessionRepository.deleteById() is called with correct ID
 * 
 * Participate Tests:
 * - testParticipateSuccess: Tests successfully adding user to session,
 *   verifies user is added to session's users list,
 *   confirms SessionRepository.save() is called to persist change
 * 
 * - testParticipateThrowsNotFoundIfSessionMissing: Tests NotFoundException is thrown
 *   when session ID doesn't exist in database
 * 
 * - testParticipateThrowsNotFoundIfUserMissing: Tests NotFoundException is thrown
 *   when user ID doesn't exist in database
 * 
 * - testParticipateThrowsBadRequestIfAlreadyParticipating: Tests BadRequestException is thrown
 *   when user is already participating in session
 * 
 * NoLongerParticipate Tests:
 * - testNoLongerParticipateSuccess: Tests successfully removing user from session,
 *   verifies user is removed from session's users list,
 *   confirms SessionRepository.save() is called to persist change
 * 
 * - testNoLongerParticipateRemovesOnlyMatchingUser: Tests selective user removal,
 *   verifies only the specified user is removed from session,
 *   confirms other users remain in session
 * 
 * - testNoLongerParticipateThrowsNotFound: Tests NotFoundException is thrown
 *   when session ID doesn't exist
 * 
 * - testNoLongerParticipateThrowsBadRequestIfUserNotInSession: Tests BadRequestException is thrown
 *   when trying to remove user who is not participating in session
 * 
 * SessionService Methods Tested:
 * - create(Session): Creates and saves new session
 * - findAll(): Retrieves all sessions
 * - getById(Long): Finds session by ID
 * - update(Long, Session): Updates existing session with new data
 * - delete(Long): Removes session from database
 * - participate(Long sessionId, Long userId): Adds user to session participants
 * - noLongerParticipate(Long sessionId, Long userId): Removes user from session participants
 * 
 * Exception Handling:
 * - NotFoundException: Thrown when session or user not found
 * - BadRequestException: Thrown for duplicate participation or removing non-participant
 * 
 * Mocked Dependencies:
 * - SessionRepository: Database operations for sessions
 * - UserRepository: Database operations for users
 * 
 * Test Configuration:
 * - Uses Mockito for mocking repositories
 * - Uses JUnit Jupiter assertions (assertEquals, assertTrue, assertThrows)
 * - Manual mock creation in @BeforeEach setup method
 * - Verifies method calls using Mockito.verify()
 */

class SessionServiceTest {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @Test
    void testCreate() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertEquals(session, result);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testFindAll() {
        List<Session> sessions = List.of(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertEquals(2, result.size());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        Session session = new Session();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertEquals(session, result);
    }

    @Test
    void testUpdate() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.update(1L, session);

        assertEquals(session, result);
        assertEquals(1L, session.getId());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testDelete() {
        sessionService.delete(5L);
        verify(sessionRepository, times(1)).deleteById(5L);
    }

    @Test
    void testParticipateSuccess() {
        User user = new User();
        user.setId(2L);
        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        sessionService.participate(1L, 2L);

        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipateThrowsNotFoundIfSessionMissing() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testParticipateThrowsNotFoundIfUserMissing() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(new Session()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testParticipateThrowsBadRequestIfAlreadyParticipating() {
        User user = new User();
        user.setId(2L);
        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>(List.of(user)));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testNoLongerParticipateSuccess() {
        User user = new User();
        user.setId(3L);
        Session session = new Session();
        session.setUsers(new ArrayList<>(List.of(user)));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 3L);

        assertTrue(session.getUsers().isEmpty());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipateRemovesOnlyMatchingUser() {
        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);
        List<User> users = new ArrayList<>(List.of(user1, user2));

        Session session = new Session();
        session.setUsers(users);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 1L);

        assertEquals(1, session.getUsers().size());
        assertEquals(2L, session.getUsers().get(0).getId());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipateThrowsNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 3L));
    }

    @Test
    void testNoLongerParticipateThrowsBadRequestIfUserNotInSession() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 3L));
    }
}
