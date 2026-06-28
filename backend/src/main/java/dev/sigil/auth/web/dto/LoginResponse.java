package dev.sigil.auth.web.dto;

import java.time.Instant;

public record LoginResponse(String token, Instant expiresAt) {}
