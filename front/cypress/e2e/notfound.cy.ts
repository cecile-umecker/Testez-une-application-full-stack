/// <reference types="cypress" />

describe('404 page spec', () => {
  it('shows 404 page when navigating to a non-existent route', () => {
    // On tente d'accéder à une route qui n'existe pas
    cy.visit('/this-page-does-not-exist', { failOnStatusCode: false });

    // Vérifie que le texte de la 404 apparaît
    cy.contains('Page not found').should('exist');
    
    // Optionnel : vérifier que l'URL correspond toujours
    cy.url().should('include', '/404');
  });
});
