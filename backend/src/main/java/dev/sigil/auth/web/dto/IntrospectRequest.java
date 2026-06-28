package dev.sigil.auth.web.dto;

import jakarta.validation.constraints.NotBlank;

public record IntrospectRequest(@NotBlank String token) {}
