package dev.sigil.user.web.dto;

import dev.sigil.user.domain.Role;
import java.util.Optional;

public record UpdateUserRequest(Optional<Role> role, Optional<Boolean> enabled) {}
