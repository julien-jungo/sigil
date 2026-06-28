package dev.sigil.auth.web.dto;

import java.time.Instant;

public record IntrospectResponse(boolean active, String sub, String role, Instant exp) {}
