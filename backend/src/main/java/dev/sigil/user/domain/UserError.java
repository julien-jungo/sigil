package dev.sigil.user.domain;

public sealed interface UserError permits UserError.NotFound, UserError.UsernameConflict {

    record NotFound(String username) implements UserError {}

    record UsernameConflict(String username) implements UserError {}
}
