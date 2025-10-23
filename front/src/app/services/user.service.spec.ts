import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';
import { expect } from '@jest/globals';

/**
 * UserService Test Suite
 * 
 * This test file contains unit tests for the UserService.
 * The UserService handles HTTP operations related to user management.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Service creation validation
 * - getById(id): Tests GET request to retrieve specific user from 'api/user/:id' endpoint
 * - delete(id): Tests DELETE request to remove user from 'api/user/:id' endpoint
 * 
 * Methods Tested:
 * - getById(id: string): Observable<User> - Retrieves specific user details by ID
 * - delete(id: string): Observable<any> - Deletes user account by ID
 * 
 * Mock HTTP:
 * - HttpClientTestingModule: Provides mock HTTP client for testing
 * - HttpTestingController: Verifies HTTP requests and provides mock responses
 * 
 * Mock Data:
 * - mockUser: User object with sample user data including email, name, and timestamps
 */

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    email: 'john@example.com',
    firstName: 'John',
    lastName: 'Doe',
    password: 'secret',
    admin: false,
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
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

  it('should fetch user by id', () => {
    service.getById('1').subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should delete user by id', () => {
    service.delete('1').subscribe(response => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
