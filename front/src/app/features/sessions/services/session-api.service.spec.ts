import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { expect } from '@jest/globals';

/**
 * SessionApiService Test Suite
 * 
 * This test file contains unit tests for the SessionApiService.
 * The SessionApiService handles all HTTP operations related to yoga sessions including CRUD operations and user participation.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Service creation validation
 * - all(): Tests GET request to retrieve all sessions from 'api/session' endpoint
 * - detail(id): Tests GET request to retrieve specific session details from 'api/session/:id'
 * - create(session): Tests POST request to create new session at 'api/session'
 * - update(id, session): Tests PUT request to update existing session at 'api/session/:id'
 * - delete(id): Tests DELETE request to remove session at 'api/session/:id'
 * - participate(sessionId, userId): Tests POST request to add user to session at 'api/session/:id/participate/:userId'
 * - unParticipate(sessionId, userId): Tests DELETE request to remove user from session at 'api/session/:id/participate/:userId'
 * 
 * Methods Tested:
 * - all(): Observable<Session[]> - Retrieves all available sessions
 * - detail(id: string): Observable<Session> - Gets specific session details
 * - create(session: Session): Observable<Session> - Creates new session
 * - update(id: string, session: Session): Observable<Session> - Updates existing session
 * - delete(id: string): Observable<any> - Deletes session
 * - participate(id: string, userId: string): Observable<void> - Adds user to session
 * - unParticipate(id: string, userId: string): Observable<void> - Removes user from session
 * 
 * Mock HTTP:
 * - HttpClientTestingModule: Provides mock HTTP client for testing
 * - HttpTestingController: Verifies HTTP requests and provides mock responses
 */

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });

    service = TestBed.inject(SessionApiService);
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

  it('should retrieve all sessions', () => {
    const mockSessions: Session[] = [
      {
        id: 1,
        name: 'Yoga débutant',
        description: 'Cours pour débutants',
        date: new Date(),
        teacher_id: 101,
        users: []
      },
      {
        id: 2,
        name: 'Yoga avancé',
        description: 'Cours pour confirmés',
        date: new Date(),
        teacher_id: 102,
        users: [1, 2]
      }
    ];

    service.all().subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
      expect(sessions.length).toBe(2);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should get session detail', () => {
    const mockSession: Session = {
      id: 3,
      name: 'Yoga Flow',
      description: 'Cours détente',
      date: new Date(),
      teacher_id: 100,
      users: [1, 2]
    };

    service.detail('3').subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session/3');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('should create a session', () => {
    const newSession: Session = {
      name: 'Yoga Doux',
      description: 'Session pour débutants',
      date: new Date(),
      teacher_id: 105,
      users: []
    };

    const createdSession = { ...newSession, id: 10 };

    service.create(newSession).subscribe(session => {
      expect(session).toEqual(createdSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    req.flush(createdSession);
  });

  it('should update a session', () => {
    const updatedSession: Session = {
      id: 10,
      name: 'Yoga Doux - modifié',
      description: 'Version révisée',
      date: new Date(),
      teacher_id: 105,
      users: [1]
    };

    service.update('10', updatedSession).subscribe(session => {
      expect(session).toEqual(updatedSession);
    });

    const req = httpMock.expectOne('api/session/10');
    expect(req.request.method).toBe('PUT');
    req.flush(updatedSession);
  });

  it('should delete a session', () => {
    service.delete('10').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne('api/session/10');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should participate in a session', () => {
    service.participate('10', '5').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/session/10/participate/5');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('should unParticipate from a session', () => {
    service.unParticipate('10', '5').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/session/10/participate/5');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
