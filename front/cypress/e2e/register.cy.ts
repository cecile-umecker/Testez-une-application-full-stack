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