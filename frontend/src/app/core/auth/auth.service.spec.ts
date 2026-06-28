import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideZonelessChangeDetection } from '@angular/core';
import { AuthService } from './auth.service';

function makeJwt(payloadObj: object): string {
  const header = btoa(JSON.stringify({ alg: 'RS256' })).replace(/=/g, '');
  const payloadJson = JSON.stringify(payloadObj);
  const payloadB64Url = btoa(payloadJson).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
  return `${header}.${payloadB64Url}.mock_sig`;
}

function createService(): AuthService {
  TestBed.configureTestingModule({
    providers: [
      AuthService,
      provideZonelessChangeDetection(),
      provideHttpClient(),
      provideRouter([]),
    ],
  });
  return TestBed.inject(AuthService);
}

describe('AuthService', () => {
  afterEach(() => {
    localStorage.clear();
    TestBed.resetTestingModule();
  });

  it('decodes a standard Base64-encoded JWT payload', () => {
    const exp = Math.floor(Date.now() / 1000) + 3600;
    localStorage.setItem(
      'sigil_token',
      makeJwt({ sub: 'uid', username: 'admin', role: 'ADMIN', exp }),
    );
    const service = createService();

    expect(service.isAuthenticated()).toBeTrue();
    expect(service.role()).toBe('ADMIN');
  });

  it('decodes a Base64URL-encoded JWT payload containing - characters', () => {
    // '>' at position 0 of the sub value causes the standard Base64 of the payload
    // to contain '+', which Nimbus serialises as '-' in Base64URL per RFC 7515.
    // atob() throws "Invalid character" on '-', so the fix is to replace -/_ before decoding.
    const exp = Math.floor(Date.now() / 1000) + 3600;
    const token = makeJwt({ sub: '>test-id', username: 'alice', role: 'VIEWER', exp });
    const payloadPart = token.split('.')[1];

    expect(payloadPart).withContext('payload must contain - to exercise the fix').toContain('-');

    localStorage.setItem('sigil_token', token);
    const service = createService();

    expect(service.isAuthenticated()).toBeTrue();
    expect(service.role()).toBe('VIEWER');
  });

  it('returns false for isAuthenticated when token is expired', () => {
    const exp = Math.floor(Date.now() / 1000) - 1;
    localStorage.setItem(
      'sigil_token',
      makeJwt({ sub: 'uid', username: 'admin', role: 'ADMIN', exp }),
    );
    const service = createService();

    expect(service.isAuthenticated()).toBeFalse();
  });
});
