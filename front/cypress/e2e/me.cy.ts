/// <reference types="cypress" />

/**
 * Account / User Info E2E Test Suite
 * 
 * This test file contains end-to-end tests for the user account page (/me).
 * Tests cover user information display for both admin and non-admin users, account deletion, and navigation.
 * 
 * Test Coverage:
 * 
 * E2E Tests:
 * - Admin sees correct user info: Tests that admin users see their complete profile information
 *   including name, email, admin badge, and timestamps. Verifies "Delete my account" button is NOT shown.
 * 
 * - Non-admin sees correct user info and can delete account: Tests that non-admin users see their profile
 *   with "Delete my account" button visible. Tests account deletion flow and redirection to home page.
 * 
 * - Account page back button navigates correctly: Tests that clicking the back button on the account page
 *   navigates users back to the sessions list page.
 * 
 * Helper Functions:
 * - loginAs(user): Performs login flow with provided user credentials and waits for successful authentication
 * 
 * Intercepted API Endpoints:
 * - POST /api/auth/login: Mocked for user authentication
 * - GET /api/user/:id: Mocked to return user profile information
 * - DELETE /api/user/:id: Mocked for account deletion (non-admin only)
 * 
 * User Roles Tested:
 * - Admin user: Can view profile but cannot delete account
 * - Non-admin user: Can view profile and delete their own account
 */

describe('Account / User info spec', () => {
  const userAdmin = {
    id: 1,
    username: 'userName',
    firstName: 'firstName',
    lastName: 'lastName',
    email: 'user@admin.com',
    admin: true,
    createdAt: '2025-10-01',
    updatedAt: '2025-10-10'
  };

  const userNonAdmin = {
    id: 2,
    username: 'userName2',
    firstName: 'firstName2',
    lastName: 'lastName2',
    email: 'user@nonadmin.com',
    admin: false,
    createdAt: '2025-09-01',
    updatedAt: '2025-09-10'
  };

  const loginAs = (user: any) => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', { body: user }).as('login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');
    cy.url().should('include', '/sessions');
  };

  it('Admin sees correct user info', () => {
    loginAs(userAdmin);

    cy.intercept('GET', `/api/user/${userAdmin.id}`, userAdmin).as('getUser');
    cy.contains('span.link', 'Account').click();
    cy.wait('@getUser');

    cy.contains(`${userAdmin.firstName} ${userAdmin.lastName.toUpperCase()}`).should('exist');
    cy.contains(userAdmin.email).should('exist');
    cy.contains('You are admin').should('exist');
    cy.contains('Delete my account').should('not.exist');
    cy.contains('Create at:').should('exist');
    cy.contains('Last update:').should('exist');
  });

  it('Non-admin sees correct user info and can delete account', () => {
    loginAs(userNonAdmin);

    cy.intercept('GET', `/api/user/${userNonAdmin.id}`, userNonAdmin).as('getUser');
    cy.contains('span.link', 'Account').click();
    cy.wait('@getUser');

    cy.contains(`${userNonAdmin.firstName} ${userNonAdmin.lastName.toUpperCase()}`).should('exist');
    cy.contains(userNonAdmin.email).should('exist');
    cy.contains('You are admin').should('not.exist');
    cy.contains('Delete my account').should('exist');
    cy.contains('Create at:').should('exist');
    cy.contains('Last update:').should('exist');
    cy.intercept('DELETE', `/api/user/${userNonAdmin.id}`, { statusCode: 200 }).as('deleteUser');

    cy.contains('button', 'Detail').click(); 
    cy.wait('@deleteUser');

    cy.url().should('include', '/');
  });

  it('Account page back button navigates correctly', () => {
    loginAs(userNonAdmin);

    cy.contains('span', 'Account').click();
    cy.url().should('include', '/me');

    cy.get('button[mat-icon-button]').click();

    cy.url().should('include', '/sessions');
  });
});
