import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { FormComponent } from './form.component';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { ActivatedRoute, Router } from '@angular/router';

/**
 * FormComponent Test Suite
 * 
 * This test file contains unit and integration tests for the FormComponent.
 * The FormComponent handles creation and updating of yoga sessions with a reactive form.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Component creation validation
 * - ngOnInit() with creation mode: Verifies form is initialized empty
 * - ngOnInit() with non-admin user: Tests redirect to /sessions if user is not admin
 * - ngOnInit() with update mode: Validates form is pre-filled with existing session data
 * - submit() in create mode: Tests SessionApiService.create() is called with form data
 * - submit() in update mode: Tests SessionApiService.update() is called with form data and session ID
 * - exitPage(): Verifies snackbar notification is displayed and navigation to /sessions
 * 
 * Integration Tests:
 * - Display "Create session" title: Verifies title when creating new session
 * - Display "Update session" title: Verifies title when updating existing session
 * - Render name input: Checks name input field is present
 * - Render date input: Validates date input field is rendered
 * - Render teacher select: Ensures teacher dropdown is displayed
 * - Render description textarea: Confirms description textarea is present
 * - Render submit button: Validates submit button is rendered
 * 
 * Mock Services:
 * - SessionService: Manages user session and admin status
 * - SessionApiService: Handles session CRUD operations
 * - TeacherService: Retrieves list of teachers
 * - MatSnackBar: Displays notification messages
 * - Router: Handles navigation and provides current URL
 * - ActivatedRoute: Provides route parameters (session ID for updates)
 */

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let fb: FormBuilder;

  const mockSessionService = {
    sessionInformation: { admin: true }
  };

  const mockSessionApiService = {
    create: jest.fn().mockReturnValue(of({ id: '1' })),
    update: jest.fn().mockReturnValue(of({ id: '1' })),
    detail: jest.fn().mockReturnValue(of({
      id: '1',
      name: 'Math',
      date: '2025-01-01',
      teacher_id: 't1',
      description: 'desc'
    }))
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([
      { id: 't1', firstName: 'John', lastName: 'Doe' }
    ]))
  };

  const mockSnackBar = { open: jest.fn() };
  const mockRouter = { url: '/sessions/create', navigate: jest.fn() };
  const mockActivatedRoute = { snapshot: { paramMap: { get: jest.fn().mockReturnValue('1') } } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatSnackBarModule,
        BrowserAnimationsModule
      ],
      declarations: [FormComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    fb = TestBed.inject(FormBuilder);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // -----------------------
  // Unit tests
  // -----------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init form empty when creating', () => {
    component.ngOnInit();
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.get('name')?.value).toBe('');
  });

  it('should navigate to /sessions if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    component.ngOnInit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should init form with data when updating', () => {
    mockRouter.url = '/sessions/update/1';
    component.ngOnInit();
    expect(component.onUpdate).toBe(true);
    expect(component.sessionForm?.get('name')?.value).toBe('Math');
  });

  it('should call create on submit when not updating', () => {
    component.sessionForm = fb.group({
      name: 'Test',
      date: '2025-01-01',
      teacher_id: 't1',
      description: 'desc'
    });
    component.onUpdate = false;
    component.submit();
    expect(mockSessionApiService.create).toHaveBeenCalledWith(expect.objectContaining({
      name: 'Test'
    }));
  });

  it('should call update on submit when updating', () => {
    component.sessionForm = fb.group({
      name: 'Test',
      date: '2025-01-01',
      teacher_id: 't1',
      description: 'desc'
    });
    component.onUpdate = true;
    component['id'] = '1';
    component.submit();
    expect(mockSessionApiService.update).toHaveBeenCalledWith('1', expect.objectContaining({
      name: 'Test'
    }));
  });

  it('should call create on submit when not updating', () => {
    component.sessionForm = fb.group({
      name: ['Test'],
      date: ['2025-01-01'],
      teacher_id: ['t1'],
      description: ['desc']
    });
    component.onUpdate = false;
    component.submit();
    expect(mockSessionApiService.create).toHaveBeenCalledWith(expect.objectContaining({
      name: 'Test'
    }));
  });

  it('should open snackBar and navigate on exitPage', () => {
    component['exitPage']('message');
    expect(mockSnackBar.open).toHaveBeenCalledWith('message', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  // -----------------------
 // Integration tests
  // -----------------------
  it('should display Create session title when not updating', () => {
    component.onUpdate = false;
    fixture.detectChanges();
    const title = fixture.nativeElement.querySelector('h1')?.textContent;
    expect(title).toContain('Create session');
  });

  it('should display Update session title when updating', () => {
    component.onUpdate = true;
    fixture.detectChanges();
    const title = fixture.nativeElement.querySelector('h1')?.textContent;
    expect(title).toContain('Update session');
  });

  it('should render name input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[formControlName="name"]')).toBeTruthy();
  });

  it('should render date input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[formControlName="date"]')).toBeTruthy();
  });

  it('should render teacher select', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-select[formControlName="teacher_id"]')).toBeTruthy();
  });

  it('should render description textarea', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('textarea[formControlName="description"]')).toBeTruthy();
  });

  it('should render submit button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('button[type="submit"]')).toBeTruthy();
  });

});
