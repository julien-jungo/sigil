package dev.sigil.audit.domain;

import dev.sigil.common.AuditEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEvent.EventType eventType;

    private UUID actorID;

    private UUID targetID;

    @Column(nullable = false, updatable = false)
    private Instant occurredAt = Instant.now();

    protected AuditEntry() {}

    public AuditEntry(AuditEvent.EventType eventType, UUID actorID, UUID targetID) {
        this.eventType = eventType;
        this.actorID = actorID;
        this.targetID = targetID;
    }

    public UUID getID() {
        return id;
    }

    public AuditEvent.EventType getEventType() {
        return eventType;
    }

    public UUID getActorID() {
        return actorID;
    }

    public UUID getTargetID() {
        return targetID;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
