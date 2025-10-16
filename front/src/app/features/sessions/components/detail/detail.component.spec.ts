import { ComponentFixture, TestBed, fakeAsync, flushMicrotasks, tick, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { expect, jest } from '@jest/globals';
import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule, NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: Router;

  // plus aucune const partagée ici
  let sessionApiServiceMock: any;
  let teacherServiceMock: any;
  let sessionServiceMock: any;
  let mockSession: Session;
  let mockTeacher: Teacher;

  beforeEach(() => {
    // on redéfinit les données de base à chaque test
    mockSession = {
      id: 1,
      name: 'Intro session',
      description: 'Session description',
      date: new Date('2025-01-01'),
      teacher_id: 10,
      users: [1, 2],
      createdAt: new Date('2024-12-01'),
      updatedAt: new Date('2024-12-10')
    };

    mockTeacher = {
      id: 10,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    // mocks recréés proprement à chaque beforeEach
    sessionApiServiceMock = {
      detail: jest.fn(() => of(mockSession)),
      delete: jest.fn(() => of({})),
      participate: jest.fn(() => of({})),
      unParticipate: jest.fn(() => of({}))
    };

    teacherServiceMock = {
      detail: jest.fn(() => of(mockTeacher))
    };

    sessionServiceMock = {
      sessionInformation: {
        token: 'token',
        type: 'type',
        id: 1,
        username: 'user',
        firstName: 'First',
        lastName: 'Last',
        admin: true
      }
    };

    TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [
        RouterTestingModule, 
        MatSnackBarModule, 
        BrowserAnimationsModule, // requis pour Angular Material
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        NoopAnimationsModule // <-- désactive toutes les animations
      ],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    });

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  // -----------------------
  // Tests unitaires TS
  // -----------------------

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('back() should call window.history.back()', () => {
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });

  it('delete() should call api.delete and navigate', waitForAsync(() => {
    const navSpy = jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));
    component.delete();
    fixture.whenStable().then(() => {
      expect(sessionApiServiceMock.delete).toHaveBeenCalledWith('1');
      expect(navSpy).toHaveBeenCalledWith(['sessions']);
    });
  }));

  it('participate() should call api.participate and fetch session', fakeAsync(() => {
    component.participate();
    tick();
    expect(sessionApiServiceMock.participate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiServiceMock.detail).toHaveBeenCalled();
  }));

  it('unParticipate() should call api.unParticipate and fetch session', fakeAsync(() => {
    component.unParticipate();
    tick();
    expect(sessionApiServiceMock.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiServiceMock.detail).toHaveBeenCalled();
  }));

  it('ngOnInit() should fetch session and teacher correctly', fakeAsync(() => {
    component.ngOnInit();
    tick();
    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('10');
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
    expect(component.isParticipate).toBe(true);
  }));

  // -----------------------
  // Tests d’intégration DOM
  // -----------------------

  it('should render h1 title with session name', () => {
    fixture.detectChanges();
    const h1 = fixture.nativeElement.querySelector('h1') as HTMLElement;
    expect(h1).toBeTruthy();
    expect(h1.textContent?.trim()).toBe('Intro Session');
  });

  it('should render Delete button for admin', () => {
    fixture.detectChanges();
    const deleteBtn = fixture.nativeElement.querySelector('button[color="warn"]') as HTMLButtonElement;
    expect(deleteBtn).toBeTruthy();
    expect(deleteBtn.textContent).toContain('Delete');
  });

  it('should render teacher name in uppercase', () => {
    fixture.detectChanges();
    const subtitleSpan = fixture.nativeElement.querySelector('mat-card-subtitle span') as HTMLElement;
    expect(subtitleSpan).toBeTruthy();
    expect(subtitleSpan.textContent?.trim()).toContain('John DOE');
  });

  it('should render attendees count', () => {
    fixture.detectChanges();
    const spans = Array.from(fixture.nativeElement.querySelectorAll('span')) as HTMLElement[];
    const attendeesSpan = spans.find(s => s.textContent?.includes('attendees'));
    expect(attendeesSpan).toBeTruthy();
    expect(attendeesSpan!.textContent).toContain('2 attendees');
  });


  it('should render description', () => {
    fixture.detectChanges();
    const desc = fixture.nativeElement.querySelector('.description') as HTMLElement;
    expect(desc).toBeTruthy();
    expect(desc.textContent).toContain('Session description');
  });

  it('should render createdAt and updatedAt', () => {
    fixture.detectChanges();
    const created = fixture.nativeElement.querySelector('.created') as HTMLElement;
    const updated = fixture.nativeElement.querySelector('.updated') as HTMLElement;
    expect(created.textContent).toContain('Create at:');
    expect(updated.textContent).toContain('Last update:');
  });
});
