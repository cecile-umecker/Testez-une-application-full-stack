import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { first } from 'rxjs/operators';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    token: 'abc123',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'Test',
    lastName: 'User',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit false initially via $isLogged', (done) => {
    service.$isLogged().pipe(first()).subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });

  it('should set isLogged to true and sessionInformation on logIn', (done) => {
    service.$isLogged().pipe(first()).subscribe(valueBefore => {
      expect(valueBefore).toBe(false); // check initial state

      service.logIn(mockUser);

      service.$isLogged().pipe(first()).subscribe(valueAfter => {
        expect(valueAfter).toBe(true);
        expect(service.sessionInformation).toEqual(mockUser);
        done();
      });
    });
  });

  it('should set isLogged to false and clear sessionInformation on logOut', (done) => {
    // first log in to set state
    service.logIn(mockUser);

    service.logOut();

    service.$isLogged().pipe(first()).subscribe(value => {
      expect(value).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
      done();
    });
  });
});
