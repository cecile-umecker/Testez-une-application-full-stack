import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';

import { ListComponent } from './list.component';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';

/**
 * ListComponent Test Suite
 * 
 * This test file contains unit and integration tests for the ListComponent.
 * The ListComponent displays a list of available yoga sessions and provides admin functionality to create new sessions.
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Component creation validation
 * - user getter: Verifies user information is retrieved from SessionService
 * 
 * Integration Tests:
 * - Display page title: Verifies "Rentals available" title is rendered
 * - Display "Create" button for admin: Checks create button is visible when user is admin
 * - Render session cards: Validates all sessions are displayed with correct data (name, date, description)
 * 
 * Mock Services:
 * - SessionService: Manages user session information and admin status
 * - SessionApiService: Retrieves list of all available sessions
 * 
 * Mock Data:
 * - mockSessions: Array of Session objects with sample yoga sessions
 */

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessions: Session[] = [
    { id: 1, name: 'Yoga Basics', description: 'Intro session', date: new Date(), teacher_id: 1, users: [] },
    { id: 2, name: 'Advanced Yoga', description: 'Advanced session', date: new Date(), teacher_id: 2, users: [1, 2] }
  ];

  const mockSessionService = {
    sessionInformation: { admin: true }
  };

  const mockSessionApiService = {
    all: jest.fn(() => of(mockSessions))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // -----------------------
  // Unit tests
  // -----------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return user from sessionService', () => {
    expect(component.user).toEqual(mockSessionService.sessionInformation);
  });

  // -----------------------
 // Integration tests
  // -----------------------
  it('should display page title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const title = compiled.querySelector('mat-card-title')?.textContent?.trim();
    expect(title).toBe('Rentals available');
  });

  it('should display "Create" button if user is admin', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const createButton = compiled.querySelector('button');
    expect(createButton?.textContent).toContain('Create');
  });

  it('should render session cards with correct data', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const sessionCards = compiled.querySelectorAll('mat-card.item');
    expect(sessionCards.length).toBe(mockSessions.length);

    mockSessions.forEach((session, index) => {
      const card = sessionCards[index];
      const cardTitle = card.querySelector('mat-card-title')?.textContent;
      const cardSubtitle = card.querySelector('mat-card-subtitle')?.textContent;
      const cardContent = card.querySelector('mat-card-content p')?.textContent;

      expect(cardTitle).toBe(session.name);
      expect(cardSubtitle).toContain('Session on');
      expect(cardContent).toContain(session.description);
    });
  });
});
