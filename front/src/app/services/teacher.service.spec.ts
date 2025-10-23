import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';
import { expect } from '@jest/globals';

/**
 * TeacherService Test Suite
 * 
 * This test file contains unit tests for the TeacherService.
 * The TeacherService handles HTTP operations related to yoga teachers.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Service creation validation
 * - all(): Tests GET request to retrieve all teachers from 'api/teacher' endpoint
 * - detail(id): Tests GET request to retrieve specific teacher details from 'api/teacher/:id'
 * 
 * Methods Tested:
 * - all(): Observable<Teacher[]> - Retrieves list of all available teachers
 * - detail(id: string): Observable<Teacher> - Gets specific teacher details by ID
 * 
 * Mock HTTP:
 * - HttpClientTestingModule: Provides mock HTTP client for testing
 * - HttpTestingController: Verifies HTTP requests and provides mock responses
 * 
 * Mock Data:
 * - mockTeachers: Array of Teacher objects with sample teacher data
 * - mockTeacher: Single Teacher object for detail endpoint testing
 */

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeachers: Teacher[] = [
    { id: 1, firstName: 'John', lastName: 'Doe', createdAt: new Date(), updatedAt: new Date() },
    { id: 2, firstName: 'Jane', lastName: 'Smith', createdAt: new Date(), updatedAt: new Date() }
  ];

  const mockTeacher: Teacher = { id: 1, firstName: 'John', lastName: 'Doe', createdAt: new Date(), updatedAt: new Date() };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
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

  it('should fetch all teachers', () => {
    service.all().subscribe(teachers => {
      expect(teachers.length).toBe(2);
      expect(teachers).toEqual(mockTeachers);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeachers);
  });

  it('should fetch a teacher detail by id', () => {
    service.detail('1').subscribe(teacher => {
      expect(teacher).toEqual(mockTeacher);
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeacher);
  });
});
