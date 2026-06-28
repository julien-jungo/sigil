package dev.sigil.user.service;

import dev.sigil.common.AuditEvent;
import dev.sigil.common.Result;
import dev.sigil.user.domain.Role;
import dev.sigil.user.domain.User;
import dev.sigil.user.domain.UserError;
import dev.sigil.user.domain.UserRepository;
import dev.sigil.user.web.dto.CreateUserRequest;
import dev.sigil.user.web.dto.UpdateUserRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;
    private final PasswordService passwordService;
    private final ApplicationEventPublisher events;

    public UserService(
            UserRepository repository,
            PasswordService passwordService,
            ApplicationEventPublisher events) {
        this.repository = repository;
        this.passwordService = passwordService;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Result<User, UserError> findByID(UUID id) {
        return repository
                .findById(id)
                .map(Result::<User, UserError>ok)
                .orElse(Result.err(new UserError.NotFound(id.toString())));
    }

    @Transactional(readOnly = true)
    public Result<User, UserError> findByUsername(String username) {
        return repository
                .findByUsername(username)
                .map(Result::<User, UserError>ok)
                .orElse(Result.err(new UserError.NotFound(username)));
    }

    public Result<User, UserError> create(CreateUserRequest request) {
        if (repository.existsByUsername(request.username())) {
            return Result.err(new UserError.UsernameConflict(request.username()));
        }
        var user = new User(
                request.username(),
                passwordService.encode(request.password()),
                request.role());
        var saved = repository.save(user);
        events.publishEvent(new AuditEvent(AuditEvent.EventType.USER_CREATED, saved.getID(), null));
        return Result.ok(saved);
    }

    public Result<User, UserError> update(UUID id, UpdateUserRequest request) {
        return repository
                .findById(id)
                .map(user -> {
                    request.role().ifPresent(user::setRole);
                    request.enabled().ifPresent(user::setEnabled);
                    var saved = repository.save(user);
                    events.publishEvent(
                            new AuditEvent(AuditEvent.EventType.USER_UPDATED, saved.getID(), null));
                    return Result.<User, UserError>ok(saved);
                })
                .orElse(Result.err(new UserError.NotFound(id.toString())));
    }

    public void createAdminIfAbsent(String username, String password) {
        if (!repository.existsByUsername(username)) {
            repository.save(new User(username, passwordService.encode(password), Role.ADMIN));
        }
    }
}
