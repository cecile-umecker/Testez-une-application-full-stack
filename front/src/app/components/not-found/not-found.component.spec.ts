import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { NotFoundComponent } from './not-found.component';

/**
 * NotFoundComponent Test Suite
 * 
 * This test file contains unit tests for the NotFoundComponent.
 * The NotFoundComponent is displayed when a user navigates to a route that doesn't exist (404 error).
 * 
 * Test Coverage:
 * 
 * Unit Tests:
 * - Component creation validation: Verifies the component can be instantiated successfully
 * 
 * The NotFoundComponent is a simple display component that shows a 404 error page.
 * It typically has no business logic or methods to test beyond its creation.
 */

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotFoundComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
