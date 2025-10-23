import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MeComponent } from './me.component';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from '../../services/user.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { HttpClientModule } from '@angular/common/http';

/**
 * MeComponent Test Suite
 * 
 * This test file contains unit and integration tests for the MeComponent.
 * The MeComponent displays user profile information and allows users to delete their account.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Component creation validation
 * - ngOnInit(): Verifies user data is fetched using UserService.getById()
 * - back(): Tests navigation to previous page using browser history
 * - delete(): Tests account deletion flow including service calls, snackbar notification, logout, and navigation
 * 
 * Integration Tests:
 * - Display title: Verifies "User information" heading is rendered
 * - Display name: Checks user's full name (John DOE) is displayed
 * - Display email: Validates email address is shown
 * - Display delete button: Ensures delete button is visible for non-admin users
 * - Display dates: Confirms creation and update dates are rendered
 * 
 * Mock Services:
 * - SessionService: Manages user session and logout
 * - UserService: Handles user data retrieval and deletion
 * - MatSnackBar: Displays notification messages
 * - Router: Handles navigation
 */

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: { admin: false, id: 1 },
    logOut: jest.fn()
  };

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of({
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      admin: false,
      createdAt: new Date(),
      updatedAt: new Date()
    })),
    delete: jest.fn().mockReturnValue(of({}))
  };

  const mockSnackBar = { open: jest.fn() };
  const mockRouter = { navigate: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // -----------------------
  // Unit tests
  // -----------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call userService.getById on ngOnInit', () => {
    component.ngOnInit();
    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toBeDefined();
    expect(component.user?.firstName).toBe('John');
  });

  it('should call window.history.back() on back', () => {
    const spy = jest.spyOn(window.history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
  });

  it('should delete user and navigate on delete', () => {
    component.delete();
    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockSnackBar.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  // -----------------------
 // Integration tests
  // -----------------------
  it('should display title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('User information');
  });

  it('should display name', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('John DOE');
  });

  it('should display email', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('john.doe@example.com');
  });

  it('should display delete button for non-admin', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button[color="warn"]');
    expect(button).toBeTruthy();
  });

  it('should display createdAt and updatedAt', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Create at:');
    expect(compiled.textContent).toContain('Last update:');
  });
});
