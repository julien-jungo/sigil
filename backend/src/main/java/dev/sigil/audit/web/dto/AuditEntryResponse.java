package dev.sigil.audit.web.dto;

import dev.sigil.audit.domain.AuditEntry;
import java.time.Instant;
import java.util.UUID;

public record AuditEntryResponse(
    UUID id, String eventType, UUID actorID, UUID targetID, Instant occurredAt) {

  public static AuditEntryResponse from(AuditEntry entry) {
    return new AuditEntryResponse(
        entry.getID(),
        entry.getEventType().name(),
        entry.getActorID(),
        entry.getTargetID(),
        entry.getOccurredAt());
  }
}
