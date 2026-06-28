export interface AuditEntry {
  id: string;
  event_type: string;
  actor_id: string | null;
  target_id: string | null;
  occurred_at: string;
}

export interface AuditPage {
  entries: AuditEntry[];
  next_cursor: string | null;
}
