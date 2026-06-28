package dev.sigil.user.web.dto;

import dev.sigil.user.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String username, @NotBlank String password, @NotNull Role role) {}
