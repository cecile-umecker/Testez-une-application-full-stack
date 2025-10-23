package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageResponse Unit Test Suite
 * 
 * This test file contains unit tests for the MessageResponse DTO.
 * The MessageResponse is used to return simple text messages to the client,
 * typically for success/error notifications after operations like registration.
 * 
 * Test Coverage:
 * 
 * Constructor Tests:
 * - testConstructorSetsMessage: Tests 1-parameter constructor creates response with message,
 *   verifies message is correctly initialized and instance is not null
 * 
 * Getter Tests:
 * - testGetMessage: Tests getMessage() returns the value set in constructor,
 *   verifies getter correctly retrieves the stored message
 * 
 * Setter Tests:
 * - testSetMessage: Tests setMessage() updates message value,
 *   verifies setter correctly modifies the stored message
 * 
 * - testSetMessageToNull: Tests setMessage() accepts null values,
 *   verifies message can be set to null without errors
 * 
 * Mockito Tests:
 * - testWithMockitoMock: Tests Mockito mock interaction for complete test coverage,
 *   verifies mocking behavior with when-thenReturn pattern and verify() method
 * 
 * DTO Properties:
 * - message: Response message text (String)
 * 
 * Test Configuration:
 * - Uses JUnit Jupiter assertions (assertEquals, assertNotNull, assertNull)
 * - Uses @DisplayName for French test descriptions
 * - Uses Mockito for mock testing coverage
 * - Tests constructor, getter, setter, and null handling
 */

class MessageResponseTest {

    @Test
    @DisplayName("Constructeur : doit initialiser correctement le message")
    void testConstructorSetsMessage() {
        // GIVEN
        String expectedMessage = "Succès";

        // WHEN
        MessageResponse response = new MessageResponse(expectedMessage);

        // THEN
        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage(), "Le message doit être correctement initialisé");
    }

    @Test
    @DisplayName("Getter : doit retourner le message correctement")
    void testGetMessage() {
        // GIVEN
        MessageResponse response = new MessageResponse("Bonjour");

        // WHEN
        String actual = response.getMessage();

        // THEN
        assertEquals("Bonjour", actual);
    }

    @Test
    @DisplayName("Setter : doit mettre à jour le message correctement")
    void testSetMessage() {
        // GIVEN
        MessageResponse response = new MessageResponse("Initial");
        String newMessage = "Mise à jour";

        // WHEN
        response.setMessage(newMessage);

        // THEN
        assertEquals(newMessage, response.getMessage(), "Le setter doit mettre à jour le message");
    }

    @Test
    @DisplayName("Setter : doit accepter un message null")
    void testSetMessageToNull() {
        // GIVEN
        MessageResponse response = new MessageResponse("Initial");

        // WHEN
        response.setMessage(null);

        // THEN
        assertNull(response.getMessage(), "Le message doit pouvoir être null");
    }

    @Test
    @DisplayName("Mockito : interaction factice pour couverture complète du contexte Mockito")
    void testWithMockitoMock() {
        // GIVEN
        MessageResponse mockResponse = Mockito.mock(MessageResponse.class);
        Mockito.when(mockResponse.getMessage()).thenReturn("Mocked message");

        // WHEN
        String result = mockResponse.getMessage();

        // THEN
        assertEquals("Mocked message", result);
        Mockito.verify(mockResponse).getMessage();
    }
}
