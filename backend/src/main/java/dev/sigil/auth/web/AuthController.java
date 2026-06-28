package dev.sigil.auth.web;

import dev.sigil.auth.service.AuthError;
import dev.sigil.auth.service.TokenService;
import dev.sigil.auth.web.dto.IntrospectRequest;
import dev.sigil.auth.web.dto.IntrospectResponse;
import dev.sigil.auth.web.dto.LoginRequest;
import dev.sigil.auth.web.dto.LoginResponse;
import dev.sigil.common.AuditEvent;
import dev.sigil.common.Result;
import dev.sigil.user.service.PasswordService;
import dev.sigil.user.service.UserService;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher events;

    public AuthController(
            UserService userService,
            PasswordService passwordService,
            TokenService tokenService,
            ApplicationEventPublisher events) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.events = events;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var userResult = userService.findByUsername(request.username());
        if (userResult instanceof Result.Err<?, ?>) {
            events.publishEvent(new AuditEvent(AuditEvent.EventType.LOGIN_FAILURE, null, null));
            return unauthorized();
        }
        var user = ((Result.Ok<dev.sigil.user.domain.User, ?>) userResult).value();
        if (!user.isEnabled() || !passwordService.matches(request.password(), user.getPasswordHash())) {
            events.publishEvent(new AuditEvent(AuditEvent.EventType.LOGIN_FAILURE, user.getID(), null));
            return unauthorized();
        }
        return switch (tokenService.issue(user)) {
            case Result.Ok<String, ?> ok -> {
                events.publishEvent(new AuditEvent(AuditEvent.EventType.LOGIN_SUCCESS, user.getID(), null));
                yield ResponseEntity.ok(new LoginResponse(ok.value(), Instant.now().plusSeconds(3600)));
            }
            case Result.Err<?, AuthError> err -> ResponseEntity.internalServerError()
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Token issuance failed"));
        };
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@Valid @RequestBody IntrospectRequest request) {
        return switch (tokenService.introspect(request.token())) {
            case Result.Ok<com.nimbusds.jwt.JWTClaimsSet, ?> ok -> {
                var claims = ok.value();
                events.publishEvent(new AuditEvent(AuditEvent.EventType.TOKEN_INTROSPECTED, null, null));
                yield ResponseEntity.ok(new IntrospectResponse(
                        true,
                        claims.getSubject(),
                        (String) claims.getClaim("role"),
                        claims.getExpirationTime().toInstant()));
            }
            case Result.Err<?, ?> err -> ResponseEntity.ok(new IntrospectResponse(false, null, null, null));
        };
    }

    private ResponseEntity<ProblemDetail> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }
}
