import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

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
  // Tests unitaires formulaires
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


    // -----------------------
  // Tests unitaires submit
  // -----------------------

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
  // Tests d’intégration DOM
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
