package dev.sigil.user.web.dto;

import dev.sigil.user.domain.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id, String username, String role, boolean enabled, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getID(),
                user.getUsername(),
                user.getRole().name(),
                user.isEnabled(),
                user.getCreatedAt());
    }
}
