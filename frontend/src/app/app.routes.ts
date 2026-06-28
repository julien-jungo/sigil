import { Routes } from '@angular/router';
import { authGuard, adminGuard, guestGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'tokens', pathMatch: 'full' },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'users',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/users/users.component').then((m) => m.UsersComponent),
  },
  {
    path: 'tokens',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/tokens/tokens.component').then((m) => m.TokensComponent),
  },
  {
    path: 'audit',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/audit/audit.component').then((m) => m.AuditComponent),
  },
  { path: '**', redirectTo: 'tokens' },
];
