package dev.sigil.audit.service;

import dev.sigil.audit.domain.AuditEntry;
import dev.sigil.audit.domain.AuditRepository;
import dev.sigil.common.AuditEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditService {

    static final int DEFAULT_PAGE_SIZE = 50;

    private final AuditRepository repository;

    public AuditService(AuditRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void onAuditEvent(AuditEvent event) {
        repository.save(new AuditEntry(event.type(), event.actorID(), event.targetID()));
    }

    @Transactional(readOnly = true)
    public AuditPage findPage(Optional<String> cursor, int limit) {
        var parsed = cursor.map(AuditCursor::decode);
        var cursorTs = parsed.map(AuditCursor::occurredAt).orElse(null);
        var cursorId = parsed.map(AuditCursor::id).orElse(null);
        var entries = repository.findPage(cursorTs, cursorId, Math.min(limit, DEFAULT_PAGE_SIZE));
        var nextCursor = entries.isEmpty()
                ? Optional.<String>empty()
                : Optional.of(AuditCursor.from(entries.getLast()).encode());
        return new AuditPage(entries, nextCursor);
    }

    public record AuditPage(List<AuditEntry> entries, Optional<String> nextCursor) {}
}
