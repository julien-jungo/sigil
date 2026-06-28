import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

const MOCK_TOKEN =
  'eyJhbGciOiJSUzI1NiJ9' +
  '.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzUxMTEwNTIxLCJleHAiOjk5OTk5OTk5OTl9' +
  '.mock';

const MOCK_USERS = [
  {
    id: '00000000-0000-0000-0000-000000000001',
    username: 'admin',
    role: 'ADMIN',
    enabled: true,
    created_at: '2026-06-28T13:00:00Z',
  },
  {
    id: '00000000-0000-0000-0000-000000000002',
    username: 'alice',
    role: 'VIEWER',
    enabled: true,
    created_at: '2026-06-28T13:05:00Z',
  },
  {
    id: '00000000-0000-0000-0000-000000000003',
    username: 'bob',
    role: 'VIEWER',
    enabled: false,
    created_at: '2026-06-27T10:00:00Z',
  },
];

const MOCK_AUDIT = {
  entries: [
    {
      id: 'a0000000-0000-0000-0000-000000000001',
      event_type: 'LOGIN_SUCCESS',
      actor_id: '00000000-0000-0000-0000-000000000001',
      target_id: null,
      occurred_at: '2026-06-28T14:20:00Z',
    },
    {
      id: 'a0000000-0000-0000-0000-000000000002',
      event_type: 'USER_CREATED',
      actor_id: '00000000-0000-0000-0000-000000000001',
      target_id: '00000000-0000-0000-0000-000000000002',
      occurred_at: '2026-06-28T14:18:00Z',
    },
    {
      id: 'a0000000-0000-0000-0000-000000000003',
      event_type: 'LOGIN_FAILURE',
      actor_id: null,
      target_id: null,
      occurred_at: '2026-06-28T14:15:00Z',
    },
  ],
  next_cursor: null,
};

function respond<T>(body: T) {
  return of(new HttpResponse({ status: 200, body }));
}

export const mockInterceptor: HttpInterceptorFn = (req, next) => {
  const { method, url } = req;

  if (url.includes('/api/v1/auth/login')) {
    return respond({ token: MOCK_TOKEN, expires_at: '2099-01-01T00:00:00Z' });
  }

  if (url.includes('/api/v1/auth/introspect')) {
    const token = req.body as { token?: string };
    return respond({
      active: true,
      sub: '00000000-0000-0000-0000-000000000001',
      role: 'ADMIN',
      exp: '2099-01-01T00:00:00Z',
    });
  }

  if (url.includes('/.well-known/jwks.json')) {
    return respond({ keys: [{ kty: 'RSA', kid: 'sigil-key', use: 'sig', alg: 'RS256' }] });
  }

  if (url.includes('/api/v1/audit')) {
    return respond(MOCK_AUDIT);
  }

  if (url.includes('/api/v1/users')) {
    if (method === 'GET') return respond(MOCK_USERS);
    if (method === 'POST') {
      const body = req.body as { username: string; role: string };
      return respond({
        id: crypto.randomUUID(),
        username: body.username,
        role: body.role,
        enabled: true,
        created_at: new Date().toISOString(),
      });
    }
    if (method === 'PUT') {
      const body = req.body as { role?: string; enabled?: boolean };
      const id = url.split('/').pop()!;
      const existing = MOCK_USERS.find((u) => u.id === id) ?? MOCK_USERS[0];
      return respond({ ...existing, ...body });
    }
  }

  return next(req);
};
