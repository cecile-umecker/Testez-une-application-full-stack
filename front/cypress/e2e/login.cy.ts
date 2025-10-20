/// <reference types="cypress" />

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