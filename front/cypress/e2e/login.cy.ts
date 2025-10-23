/// <reference types="cypress" />

/**
 * Login E2E Test Suite
 * 
 * This test file contains end-to-end tests for the login functionality.
 * Tests cover successful login/logout flows, error handling, and navigation guards.
 * 
 * Test Coverage:
 * 
 * E2E Tests:
 * - Login successful and logout: Tests complete login flow with valid credentials, navigation to /sessions,
 *   logout functionality, and verification that UI updates correctly (authenticated links hidden, guest links shown)
 * 
 * - Login failed: Tests login with invalid credentials (wrong password), verifies 401 response handling
 *   and user remains on login page
 * 
 * - Login failed with empty fields: Tests form validation with empty email/password fields,
 *   verifies 400 Bad Request handling and user remains on login page
 * 
 * - Login failed with invalid email format: Tests email validation with incorrectly formatted email,
 *   verifies 401 response handling and user remains on login page
 * 
 * - Navigation guard - not logged in: Tests that unauthenticated users attempting to access /sessions
 *   are redirected to /login
 * 
 * - Navigation guard - logged in: Tests that authenticated users can successfully access /sessions route
 * 
 * Intercepted API Endpoints:
 * - POST /api/auth/login: Mocked for both successful and failed login attempts
 * - GET /api/session: Mocked to return empty session list after successful login
 */

describe('Login spec', () => {
  it('Login successfull and can logout', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.contains('span.link', 'Logout').click();

    cy.url().should('include', '/');

    cy.contains('span.link', 'Account').should('not.exist');
    cy.contains('span.link', 'Logout').should('not.exist');
    cy.contains('span.link', 'Sessions').should('not.exist');

    cy.contains('span.link', 'Login').should('exist');
    cy.contains('span.link', 'Register').should('exist');
  })

  it('Login failed', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Bad credentials'
      },
    })

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"wrongPassword"}{enter}{enter}`)

    cy.url().should('include', '/login')
  })

  it('Login failed because of empty fields', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 400,
      body: {
        message: 'Bad Request'
      },
    })

    cy.get('input[formControlName=email]').type("{selectall}{backspace}")
    cy.get('input[formControlName=password]').type("{selectall}{backspace}{enter}")

    cy.url().should('include', '/login')
  })

  it('Login failed because of invalid email format', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Bad credentials'
      },
    })

    cy.get('input[formControlName=email]').type("invalidEmailFormat")
    cy.get('input[formControlName=password]').type("test!1234{enter}")

    cy.url().should('include', '/login')
  })

  it('redirects to login when user is not logged in', () => {
    cy.visit('/sessions', { failOnStatusCode: false });

    cy.url().should('include', '/login');
  });

  it('allows access when user is logged in', () => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', { body: { id: 1, admin: true } }).as('login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');
    cy.wait('@login');

    cy.contains('span.link', 'Sessions').click();
    cy.url().should('include', '/sessions');
  });
})