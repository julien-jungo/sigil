package dev.sigil.audit.web;

import dev.sigil.audit.service.AuditService;
import dev.sigil.audit.web.dto.AuditEntryResponse;
import dev.sigil.audit.web.dto.AuditPageResponse;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public AuditPageResponse list(
            @RequestParam Optional<String> cursor,
            @RequestParam(defaultValue = "50") int limit) {
        var page = auditService.findPage(cursor, limit);
        var entries = page.entries().stream().map(AuditEntryResponse::from).toList();
        return new AuditPageResponse(entries, page.nextCursor());
    }
}
