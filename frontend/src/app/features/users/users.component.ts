import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SlicePipe } from '@angular/common';
import { User } from '../../core/models/user.model';
import { UserService } from './user.service';

@Component({
  selector: 'app-users',
  imports: [FormsModule, SlicePipe],
  templateUrl: './users.component.html',
})
export class UsersComponent implements OnInit {
  protected users = signal<User[]>([]);
  protected showForm = signal(false);
  protected newUsername = '';
  protected newPassword = '';
  protected newRole = 'VIEWER';

  private svc = inject(UserService);

  ngOnInit() {
    this.load();
  }

  private load() {
    this.svc.list().subscribe((users) => this.users.set(users));
  }

  create() {
    this.svc
      .create({ username: this.newUsername, password: this.newPassword, role: this.newRole })
      .subscribe(() => {
        this.showForm.set(false);
        this.newUsername = '';
        this.newPassword = '';
        this.load();
      });
  }

  toggleEnabled(user: User) {
    this.svc.update(user.id, { enabled: !user.enabled }).subscribe(() => this.load());
  }
}
