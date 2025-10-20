/// <reference types="cypress" />

describe('404 page spec', () => {
  it('shows 404 page when navigating to a non-existent route', () => {
    cy.visit('/this-page-does-not-exist', { failOnStatusCode: false });

    cy.contains('Page not found').should('exist');
    
    cy.url().should('include', '/404');
  });
});
