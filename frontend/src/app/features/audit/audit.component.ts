import { Component, inject, signal, OnInit } from '@angular/core';
import { SlicePipe } from '@angular/common';
import { AuditEntry } from '../../core/models/audit.model';
import { AuditService } from './audit.service';

@Component({
  selector: 'app-audit',
  imports: [SlicePipe],
  templateUrl: './audit.component.html',
})
export class AuditComponent implements OnInit {
  protected entries = signal<AuditEntry[]>([]);
  protected nextCursor = signal<string | null>(null);

  private svc = inject(AuditService);

  ngOnInit() {
    this.load();
  }

  private load(cursor?: string) {
    this.svc.list(cursor).subscribe((page) => {
      this.entries.update((prev) => (cursor ? [...prev, ...page.entries] : page.entries));
      this.nextCursor.set(page.next_cursor);
    });
  }

  loadMore() {
    const cursor = this.nextCursor();
    if (cursor) this.load(cursor);
  }
}
