import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from './services/session.service';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { NgZone, NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatToolbarModule } from '@angular/material/toolbar';

/**
 * AppComponent Test Suite
 * 
 * This test file contains unit and integration tests for the AppComponent.
 * The AppComponent is the root component of the application that manages the main navigation toolbar
 * and displays different navigation links based on user authentication status.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Component creation validation
 * - $isLogged(): Tests observable returns authentication status from SessionService
 * - logout(): Verifies SessionService.logOut() is called and navigation to root path
 * 
 * Integration Tests (Not Logged In):
 * - Display toolbar title: Verifies "Yoga app" title is rendered in mat-toolbar
 * - Display login/register links: Checks "Login" and "Register" links are visible when user is not authenticated
 * - Hide authenticated links: Ensures "Sessions", "Account", and "Logout" are not displayed
 * 
 * Integration Tests (Logged In):
 * - Display account links: Verifies "Sessions", "Account", and "Logout" links are visible when user is authenticated
 * - Hide guest links: Ensures "Login" and "Register" links are not displayed
 * 
 * Methods Tested:
 * - $isLogged(): Observable<boolean> - Returns authentication status observable from SessionService
 * - logout(): void - Logs out user and navigates to home page
 * 
 * Mock Services:
 * - SessionService: Manages authentication state and logout functionality
 * - Router: Handles navigation after logout
 */

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionServiceMock: any;
  let ngZoneMock: NgZone

  const mockRouter = { navigate: jest.fn() };

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(false)),
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatToolbarModule,
      ],
      declarations: [AppComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: mockRouter }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    ngZoneMock = TestBed.inject(NgZone);
  });

  // -----------------------
  // Unit tests
  // -----------------------
  it('should create the app', () => {
    const fixture: ComponentFixture<AppComponent> = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should call $isLogged and return observable', (done) => {
    sessionServiceMock.$isLogged = jest.fn().mockReturnValue(of(true));
    component.$isLogged().subscribe(value => {
      expect(sessionServiceMock.$isLogged).toHaveBeenCalled();
      expect(value).toBe(true);
      done();
    });
  });

  it('should log out and navigate to root', () => {
    sessionServiceMock.logOut = jest.fn();
    mockRouter.navigate = jest.fn();
    ngZoneMock.run(() => {
      component.logout();
    });
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
  });
});


  // -----------------------
 // Integration tests
  // -----------------------
describe('AppComponent DOM tests', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let sessionServiceMock: any;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(false)),
      logOut: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatToolbarModule,
      ],
      declarations: [AppComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should display toolbar title', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-toolbar')?.textContent).toContain('Yoga app');
  });

  it('should display login/register links when not logged in', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Login');
    expect(compiled.textContent).toContain('Register');
    expect(compiled.textContent).not.toContain('Sessions');
    expect(compiled.textContent).not.toContain('Account');
    expect(compiled.textContent).not.toContain('Logout');
  });
});

describe('AppComponent DOM tests when logged in', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let sessionServiceMock: any;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatToolbarModule,
      ],
      declarations: [AppComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
    
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should display account links when logged in', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Sessions');
    expect(compiled.textContent).toContain('Account');
    expect(compiled.textContent).toContain('Logout');
    expect(compiled.textContent).not.toContain('Login');
    expect(compiled.textContent).not.toContain('Register');
  });
});
