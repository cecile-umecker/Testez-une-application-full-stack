/// <reference types="cypress" />

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
