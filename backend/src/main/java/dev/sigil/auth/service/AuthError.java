package dev.sigil.auth.service;

public sealed interface AuthError permits AuthError.InvalidCredentials, AuthError.InvalidToken {

    record InvalidCredentials() implements AuthError {}

    record InvalidToken(String reason) implements AuthError {}
}
