import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

interface LoginResponse {
  token: string;
  expires_at: string;
}

interface TokenPayload {
  sub: string;
  username: string;
  role: string;
  exp: number;
}

const TOKEN_KEY = 'sigil_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly isAuthenticated = computed(() => {
    const p = this.payload();
    return p !== null && p.exp * 1000 > Date.now();
  });
  readonly token = this._token.asReadonly();
  readonly role = computed(() => this.payload()?.role ?? null);
  readonly isAdmin = computed(() => this.role() === 'ADMIN');

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(username: string, password: string) {
    return this.http.post<LoginResponse>('/api/v1/auth/login', { username, password }).pipe(
      tap((res) => {
        localStorage.setItem(TOKEN_KEY, res.token);
        this._token.set(res.token);
      }),
    );
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    this._token.set(null);
    this.router.navigate(['/login']);
  }

  private payload(): TokenPayload | null {
    const token = this._token();
    if (!token) return null;
    try {
      const b64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(b64)) as TokenPayload;
    } catch {
      return null;
    }
  }
}
