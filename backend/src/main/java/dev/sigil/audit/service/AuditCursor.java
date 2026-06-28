package dev.sigil.audit.service;

import dev.sigil.audit.domain.AuditEntry;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public record AuditCursor(Instant occurredAt, UUID id) {

    public static AuditCursor from(AuditEntry entry) {
        return new AuditCursor(entry.getOccurredAt(), entry.getID());
    }

    public static AuditCursor decode(String encoded) {
        var raw = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        var parts = raw.split("\\|", 2);
        return new AuditCursor(Instant.parse(parts[0]), UUID.fromString(parts[1]));
    }

    public String encode() {
        var raw = occurredAt + "|" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
