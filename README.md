# Yoga-app
## Installation Requirements

- Java 11 
- Spring Boot
- Maven
- MySQL
- Node.js 16 and npm
- Angular 14 and CLI

## Backend Setup

1. Configure MySQL:

Download [MySQL](https://dev.mysql.com/downloads/mysql/)

```bash
  CREATE DATABASE test;
  USE test;
  SOURCE absolutePathFile/.../.../ressource/sql/script.sql;
  CREATE USER 'user'@'localhost' IDENTIFIED BY '123456';
  GRANT ALL PRIVILEGES ON test.* TO 'user'@'localhost';
  FLUSH PRIVILEGES;
  ```

2. Install Spring dependencies :
    ```bash
    cd back
    mvn clean install
    ```

3. Start Spring Boot API:
    ```bash
    mvn spring-boot:run
    ```
    Server runs on `http://localhost:8080`

## Frontend Setup

1. Install Angular dependencies:
    ```bash
    cd front
    npm install
    ```

2. Start Angular server:
    ```bash
    npm run start
    ```
    Application runs on `http://localhost:4200`

## Development

Both servers must be running:
- Backend API on port 8080
- Frontend on port 4200

Access the application at `http://localhost:4200`

## Tests
This project includes three levels of testing:

  - Unit and integration tests for the backend using JUnit 5,
  - Unit and intégration tests for the frontend using Jest,
  - end-to-end (E2E) tests using Cypress.

### Backend

To run tests :

  ```bash
  cd back
  mvn clean test
  ```

To generate coverage report :

  ```bash
  mvn jacoco:report
  ```
  The coverage report file is available on `.../back/target/site/jacoco/index.html`

### Frontend

To run tests :
  ```bash
  cd front
  npm run test
  ```

To generate coverage report :
  ```bash
  npm test -- --coverage
  ```

The coverage report file is available on `.../front/coverage/jest/lcov-report/index.html`

### e2e

To run live tests and open the Cypress graphical interface : 
  ```bash
  cd front
  npm run cypress:open
  ```
This command opens the Cypress GUI. 

The front server must be running.

In the interface :

  1. Select ***E2E*** ***testing***
  2. Choose a browser
  3. Then select a test file from the list to execute it

To generate coverage report :
  ```bash
  ng run yoga:e2e-ci
  npm run e2e:coverage
  ```

The coverage report file is available on `.../front/coverage/lcov-report/index.html`

## Additional Resources

### Postman Collection
- Import collection from `.../ressources/postman/yoga.postman_collection.json`
- Follow [Postman import guide](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman)