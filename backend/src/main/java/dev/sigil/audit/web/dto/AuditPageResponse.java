package dev.sigil.audit.web.dto;

import java.util.List;
import java.util.Optional;

public record AuditPageResponse(List<AuditEntryResponse> entries, Optional<String> nextCursor) {}
