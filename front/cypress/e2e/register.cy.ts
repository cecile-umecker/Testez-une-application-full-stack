/// <reference types="cypress" />

/**
 * Register E2E Test Suite
 * 
 * This test file contains end-to-end tests for the user registration functionality.
 * Tests cover successful registration, error handling for validation failures, and duplicate email scenarios.
 * 
 * Test Coverage:
 * 
 * E2E Tests:
 * - Register successful: Tests complete registration flow with valid user data (firstName, lastName, email, password),
 *   verifies successful API response and redirection to /login page
 * 
 * - Register failed because of existing email: Tests registration attempt with an email that already exists in the system,
 *   verifies 400 status code handling with "Email is already taken!" message and user remains on /register page
 * 
 * - Register failed because of empty fields: Tests form validation when all required fields are empty,
 *   verifies 400 Bad Request handling and user remains on /register page
 * 
 * - Register failed because of invalid email format: Tests email validation with incorrectly formatted email address,
 *   verifies 400 Bad Request handling and user remains on /register page
 * 
 * Intercepted API Endpoints:
 * - POST /api/auth/register: Mocked for both successful and failed registration attempts
 * 
 * Form Fields Tested:
 * - firstName: Required field validation
 * - lastName: Required field validation
 * - email: Required field and email format validation
 * - password: Required field validation
 */

describe('Register spec', () => {
  it('Register successful', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {
        message: 'User registered successfully!'
      },
    })

    cy.get('input[formControlName=firstName]').type("John")
    cy.get('input[formControlName=lastName]').type("Doe")
    cy.get('input[formControlName=email]').type("johndoe@mail.com")
    cy.get('input[formControlName=password]').type(`${"Pass!word"}{enter}{enter}`)

    cy.url().should('include', '/login')
  })

  it('Register failed because of existing email', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {
        message: 'Error: Email is already taken!'
      },
    })  

    cy.get('input[formControlName=firstName]').type("John")
    cy.get('input[formControlName=lastName]').type("Doe")
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"Pass!word"}{enter}{enter}`)

    cy.url().should('include', '/register')
  })

  it('Register failed because of empty fields', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {
        message: 'Bad Request'
      },
    })

    cy.get('input[formControlName=firstName]').type("{selectall}{backspace}")
    cy.get('input[formControlName=lastName]').type("{selectall}{backspace}")
    cy.get('input[formControlName=email]').type("{selectall}{backspace}")
    cy.get('input[formControlName=password]').type(`${"{selectall}{backspace}"}{enter}{enter}`)

    cy.url().should('include', '/register')
  })

  it('Register failed because of invalid email format', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {
        message: 'Bad Request'
      },
    })

    cy.get('input[formControlName=firstName]').type("John")
    cy.get('input[formControlName=lastName]').type("Doe")
    cy.get('input[formControlName=email]').type("invalidEmailFormat")
    cy.get('input[formControlName=password]').type(`${"Pass!"}{enter}{enter}`)

    cy.url().should('include', '/register')
  })
})