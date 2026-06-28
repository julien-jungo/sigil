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
        var pageSize = Math.min(limit, DEFAULT_PAGE_SIZE);
        var entries = repository.findPage(cursorTs, cursorId, pageSize);
        var nextCursor = entries.size() == pageSize
                ? Optional.of(AuditCursor.from(entries.getLast()).encode())
                : Optional.<String>empty();
        return new AuditPage(entries, nextCursor);
    }

    public record AuditPage(List<AuditEntry> entries, Optional<String> nextCursor) {}
}
