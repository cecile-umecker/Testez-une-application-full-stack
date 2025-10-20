/// <reference types="cypress" />

describe('Sessions spec', () => {
  const userAdmin = {
    id: 1,
    username: 'userName',
    firstName: 'firstName',
    lastName: 'lastName',
    admin: true
  };

  const userNonAdmin = {
    id: 2,
    username: 'userName2',
    firstName: 'firstName2',
    lastName: 'lastName2',
    admin: false
  };

  const teacherMock = [
    { id: 1, firstName: 'John', lastName: 'Doe' },
    { id: 2, firstName: 'Jane', lastName: 'Smith' }
  ];

  const sessionsMock = [
    {
      id: 1,
      name: 'Morning Yoga',
      date: '2025-10-20',
      teacher_id: 1,
      description: 'A relaxing session.',
      users: [],
      createdAt: '2025-10-01',
      updatedAt: '2025-10-10'
    }
  ];

  const loginAs = (user: any) => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', { body: user }).as('login');
    cy.intercept('GET', '/api/session', sessionsMock).as('getSessions');
    cy.intercept('GET', '/api/teacher', teacherMock).as('getTeachers');
    cy.intercept('GET', '/api/session/1', sessionsMock[0]).as('getSessionById');
    cy.intercept('POST', '/api/session', { ...sessionsMock[0], id: 99 }).as('postSession');
    cy.intercept('PUT', '/api/session/1', { ...sessionsMock[0], name: 'Updated Yoga' }).as('putSession');
    cy.intercept('POST', `/api/session/1/participate/${userNonAdmin.id}`, {}).as('participate');
    cy.intercept('DELETE', `/api/session/1/participate/${userNonAdmin.id}`, {}).as('unParticipate');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');
    cy.url().should('include', '/sessions');
  };

  it('Admin can view list of sessions', () => {
    loginAs(userAdmin);
    cy.wait('@getSessions');
    cy.get('mat-card').should('have.length.at.least', 1);
    cy.contains('Morning Yoga').should('exist');
  });

  it('Admin can create a session', () => {
    loginAs(userAdmin);
    cy.get('button[routerLink="create"]').click();
    cy.wait('@getTeachers');

    cy.get('input[formControlName="name"]').type('New Session');
    cy.get('input[formControlName="date"]').type('2025-12-25');

    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').should('have.length.at.least', 1).first().click();

    cy.get('textarea[formControlName="description"]').type('Test session description');
    cy.get('button[type="submit"]').click();

    cy.wait('@postSession');
    cy.url().should('include', '/sessions');
  });

  it('Non-admin cannot create a session', () => {
    loginAs(userNonAdmin);
    cy.get('button[routerLink="create"]').should('not.exist');
  });

  it('Admin can edit a session', () => {
    loginAs(userAdmin);
    cy.contains('button', 'Edit').click();
    cy.wait('@getSessionById');
    cy.wait('@getTeachers');

    cy.get('input[formControlName="name"]').clear().type('Updated Yoga');
    cy.get('button[type="submit"]').click();
    cy.wait('@putSession');
  });

  it('Admin sees delete button on session detail and can delete session', () => {
    loginAs(userAdmin);

    cy.contains('button', 'Detail').click();
    cy.wait('@getSessionById');

    cy.contains('button', 'Delete').should('exist');
    cy.contains('Participate').should('not.exist');

    cy.intercept('DELETE', '/api/session/1', {}).as('deleteSession');

    cy.contains('button', 'Delete').click({ force: true });

    cy.wait('@deleteSession');

    cy.url().should('include', '/sessions');
  });

  it('Non-admin sees participate button and can toggle participation', () => {
    loginAs(userNonAdmin);

    cy.contains('button', 'Detail').click();
    cy.wait('@getSessionById');

    cy.intercept('POST', `/api/session/1/participate/${userNonAdmin.id}`, {}).as('participate');
    cy.intercept('GET', '/api/session/1', { ...sessionsMock[0], users: [userNonAdmin.id] }).as('getSessionAfterParticipate');
    cy.intercept('GET', '/api/teacher/1', teacherMock[0]).as('getTeacherAfterParticipate');

    cy.contains('button', 'Participate')
      .should('be.visible')
      .click({ force: true });
    cy.wait('@participate');
    cy.wait('@getSessionAfterParticipate');
    cy.wait('@getTeacherAfterParticipate');

    cy.intercept('DELETE', `/api/session/1/participate/${userNonAdmin.id}`, {}).as('unParticipate');
    cy.intercept('GET', '/api/session/1', { ...sessionsMock[0], users: [] }).as('getSessionAfterUnparticipate');
    cy.intercept('GET', '/api/teacher/1', teacherMock[0]).as('getTeacherAfterUnparticipate');

    cy.contains('button', 'Do not participate')
      .should('be.visible')
      .click({ force: true });

    cy.wait('@unParticipate');
    cy.wait('@getSessionAfterUnparticipate');
    cy.wait('@getTeacherAfterUnparticipate');

    cy.contains('button', 'Participate').should('be.visible');
  });
});
