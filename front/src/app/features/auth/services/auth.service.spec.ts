import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

/**
 * AuthService Test Suite
 * 
 * This test file contains unit tests for the AuthService.
 * The AuthService handles authentication operations including user registration and login.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Service creation validation
 * - register(): Tests POST request to 'api/auth/register' endpoint with RegisterRequest payload
 * - login(): Tests POST request to 'api/auth/login' endpoint and validates SessionInformation response
 * 
 * Methods Tested:
 * - register(registerRequest: RegisterRequest): Observable<void>
 *   Sends user registration data to the backend API
 * 
 * - login(loginRequest: LoginRequest): Observable<SessionInformation>
 *   Authenticates user credentials and returns session information including token
 * 
 * Mock HTTP:
 * - HttpClientTestingModule: Provides mock HTTP client for testing
 * - HttpTestingController: Verifies HTTP requests and provides mock responses
 */

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // -----------------------
  // Unit tests
  // -----------------------
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send POST request on register', () => {
    const mockRequest: RegisterRequest = {
      email: 'test@mail.com',
      password: '1234',
      firstName: 'Test',
      lastName: 'User'
    };

    service.register(mockRequest).subscribe(response => {
      expect(response).toBeUndefined(); 
    });

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);

    req.flush(null);
  });

  it('should send POST request on login and return SessionInformation', () => {
    const mockLogin: LoginRequest = { email: 'test@mail.com', password: '1234' };
    const mockResponse: SessionInformation = {
      token: 'abc123',
      type: 'Bearer',
      id: 1,
      username: 'TestUser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    service.login(mockLogin).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockLogin);

    req.flush(mockResponse);
  });
});
