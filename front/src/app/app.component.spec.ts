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
