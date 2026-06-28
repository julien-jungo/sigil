package dev.sigil.user.web.dto;

import java.util.Optional;

public record UpdateUserRequest(Optional<String> role, Optional<Boolean> enabled) {}
