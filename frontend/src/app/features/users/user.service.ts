import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../../core/models/user.model';

export interface CreateUserRequest {
  username: string;
  password: string;
  role: string;
}

export interface UpdateUserRequest {
  role?: string;
  enabled?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);

  list() {
    return this.http.get<User[]>('/api/v1/users');
  }

  create(req: CreateUserRequest) {
    return this.http.post<User>('/api/v1/users', req);
  }

  update(id: string, req: UpdateUserRequest) {
    return this.http.put<User>(`/api/v1/users/${id}`, req);
  }
}
