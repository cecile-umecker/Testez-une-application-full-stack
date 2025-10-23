import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

/**
 * RegisterComponent Test Suite
 * 
 * This test file contains unit and integration tests for the RegisterComponent.
 * The RegisterComponent handles user registration through a registration form.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Component creation validation
 * - Form validation when empty: Verifies form is invalid without input
 * - Email field validation: Tests email format validation (invalid/valid formats)
 * - First name and last name validation: Tests required field validation
 * - Password field validation: Tests password required validation
 * - submit() with successful registration: Verifies AuthService.register() call and navigation to /login
 * - submit() with failed registration: Tests error handling and onError flag is set to true
 * 
 * Integration Tests:
 * - Display "Register" title: Verifies the page title is rendered
 * - Render form fields: Checks all four input fields (firstName, lastName, email, password) are present
 * - Display error message: Validates error message appears when onError is true
 * 
 * Mock Services:
 * - AuthService: Handles user registration
 * - Router: Handles navigation after successful registration
 */

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: any;
  let router: Router;

  beforeEach(() => {
    mockAuthService = { register: jest.fn(() => of(void 0)) };

    TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));
    fixture.detectChanges();
  });

  // -----------------------
  // Unit tests
  // -----------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.form.valid).toBeFalsy();
  });

  it('should validate email field', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalidEmail');
    expect(emailControl?.valid).toBeFalsy();
    emailControl?.setValue('test@example.com');
    expect(emailControl?.valid).toBeTruthy();
  });

  it('should validate firstName and lastName required', () => {
    const firstName = component.form.get('firstName');
    const lastName = component.form.get('lastName');

    firstName?.setValue('');
    lastName?.setValue('');
    expect(firstName?.valid).toBeFalsy();
    expect(lastName?.valid).toBeFalsy();

    firstName?.setValue('Alice');
    lastName?.setValue('Dupont');
    expect(firstName?.valid).toBeTruthy();
    expect(lastName?.valid).toBeTruthy();
  });

  it('should validate password required', () => {
    const password = component.form.get('password');
    password?.setValue('');
    expect(password?.valid).toBeFalsy();
    password?.setValue('secret');
    expect(password?.valid).toBeTruthy();
  });

  it('should call register and navigate to /login on success', () => {
    mockAuthService.register.mockReturnValue(of({}));
    const routerSpy = jest.spyOn(router, 'navigate');

    component.form.setValue({
      firstName: 'Alice',
      lastName: 'Doe',
      email: 'alice@test.com',
      password: '12345',
    });

    component.submit();

    expect(mockAuthService.register).toHaveBeenCalledWith({
      firstName: 'Alice',
      lastName: 'Doe',
      email: 'alice@test.com',
      password: '12345',
    });
    expect(routerSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should set onError to true when register fails', () => {
    mockAuthService.register.mockReturnValue(throwError(() => new Error('fail')));

    component.form.setValue({
      firstName: 'Alice',
      lastName: 'Doe',
      email: 'alice@test.com',
      password: '12345',
    });

    component.submit();

    expect(component.onError).toBe(true);
  });

  // -----------------------
 // Integration tests
  // -----------------------
  it('should display "Register" title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-card-title')?.textContent).toContain('Register');
  });

  it('should have form fields for first name, last name, email, and password', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const inputs = compiled.querySelectorAll('input');
    expect(inputs.length).toBe(4);
    expect(inputs[0].getAttribute('formControlName')).toBe('firstName');
    expect(inputs[1].getAttribute('formControlName')).toBe('lastName');
    expect(inputs[2].getAttribute('formControlName')).toBe('email');
    expect(inputs[3].getAttribute('formControlName')).toBe('password');
  });

  it('should display error message when onError is true', () => {
    component.onError = true;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.error')).toBeTruthy();
  });
});
