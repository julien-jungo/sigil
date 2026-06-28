import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IntrospectResponse, JwkSet } from '../../core/models/token.model';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private http = inject(HttpClient);

  introspect(token: string) {
    return this.http.post<IntrospectResponse>('/api/v1/auth/introspect', { token });
  }

  jwks() {
    return this.http.get<JwkSet>('/.well-known/jwks.json');
  }
}
