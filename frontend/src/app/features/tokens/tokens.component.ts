import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { JsonPipe, SlicePipe } from '@angular/common';
import { IntrospectResponse, JwkSet } from '../../core/models/token.model';
import { TokenService } from './token.service';

@Component({
  selector: 'app-tokens',
  imports: [FormsModule, JsonPipe, SlicePipe],
  templateUrl: './tokens.component.html',
})
export class TokensComponent {
  protected tab = signal<'introspect' | 'jwks'>('introspect');
  protected tokenInput = '';
  protected result = signal<IntrospectResponse | null>(null);
  protected jwks = signal<JwkSet | null>(null);
  protected error = signal<string | null>(null);

  private svc = inject(TokenService);

  introspect() {
    this.error.set(null);
    this.svc.introspect(this.tokenInput.trim()).subscribe({
      next: (res) => this.result.set(res),
      error: () => this.error.set('Failed to introspect token'),
    });
  }

  loadJwks() {
    this.svc.jwks().subscribe({
      next: (jwks) => this.jwks.set(jwks),
      error: () => this.error.set('Failed to load JWKS'),
    });
  }
}
