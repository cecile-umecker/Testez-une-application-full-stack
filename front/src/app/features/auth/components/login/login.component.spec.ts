import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: any;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(() => {
    mockAuthService = { login: jest.fn(() => of({ token: '123' })) };

    TestBed.configureTestingModule({
      declarations: [LoginComponent],
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
        SessionService,
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // -----------------------
  // Unit tests
  // -----------------------
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

  it('should validate password field', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.valid).toBeFalsy();
    passwordControl?.setValue('123');
    expect(passwordControl?.valid).toBeTruthy();
  });

  it('should call login and navigate on successful login', () => {
    const loginResponse = { token: '123' };
    mockAuthService.login.mockReturnValue(of(loginResponse));
    const sessionSpy = jest.spyOn(sessionService, 'logIn');
    const routerSpy = jest.spyOn(router, 'navigate');

    component.form.setValue({ email: 'test@test.com', password: '123' });
    component.submit();

    expect(mockAuthService.login).toHaveBeenCalledWith({ email: 'test@test.com', password: '123' });
    expect(sessionSpy).toHaveBeenCalledWith(loginResponse);
    expect(routerSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set onError to true when login fails', () => {
    mockAuthService.login.mockReturnValue(throwError(() => new Error('fail')));

    component.form.setValue({ email: 'test@test.com', password: '123' });
    component.submit();

    expect(component.onError).toBe(true);
  });

  it('should call login even if form is invalid (current behavior)', () => {
    component.form.setValue({ email: '', password: '' });
    component.submit();
    expect(mockAuthService.login).toHaveBeenCalledWith({ email: '', password: '' });
  });

  // -----------------------
 // Integration tests
  // -----------------------
  it('should render the page title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-card-title')?.textContent).toContain('Login');
  });

  it('should render email and password fields', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[formControlName="email"]')).toBeTruthy();
    expect(compiled.querySelector('input[formControlName="password"]')).toBeTruthy();
  });

  it('should render the submit button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('button[type="submit"]')).toBeTruthy();
  });

  it('should display error message when onError is true', () => {
    component.onError = true;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.error')).toBeTruthy();
  });
});
