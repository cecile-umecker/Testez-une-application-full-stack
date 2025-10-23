/// <reference types="cypress" />

/**
 * 404 Page E2E Test Suite
 * 
 * This test file contains end-to-end tests for the 404 Not Found page.
 * Tests verify that users are properly redirected to the 404 page when navigating to non-existent routes.
 * 
 * Test Coverage:
 * 
 * E2E Tests:
 * - Shows 404 page when navigating to non-existent route: Tests that accessing an invalid URL
 *   displays the "Page not found" message and redirects to the /404 route
 * 
 * Test Configuration:
 * - failOnStatusCode: false - Allows Cypress to continue test execution even when receiving 404 status codes
 * 
 * Validations:
 * - Verifies "Page not found" text is displayed
 * - Confirms URL is redirected to /404 route
 */

describe('404 page spec', () => {
  it('shows 404 page when navigating to a non-existent route', () => {
    cy.visit('/this-page-does-not-exist', { failOnStatusCode: false });

    cy.contains('Page not found').should('exist');
    
    cy.url().should('include', '/404');
  });
});
