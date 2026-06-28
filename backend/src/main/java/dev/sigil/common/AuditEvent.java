package dev.sigil.common;

import java.util.UUID;

public record AuditEvent(EventType type, UUID actorID, UUID targetID) {

    public enum EventType {
        USER_CREATED,
        USER_UPDATED,
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        TOKEN_INTROSPECTED
    }
}
