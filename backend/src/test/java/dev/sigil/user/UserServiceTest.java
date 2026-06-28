package dev.sigil.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.sigil.common.Result;
import dev.sigil.user.domain.Role;
import dev.sigil.user.domain.User;
import dev.sigil.user.domain.UserError;
import dev.sigil.user.domain.UserRepository;
import dev.sigil.user.service.PasswordService;
import dev.sigil.user.service.UserService;
import dev.sigil.user.web.dto.CreateUserRequest;
import dev.sigil.user.web.dto.UpdateUserRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class UserServiceTest {

  private UserRepository repository;
  private UserService userService;

  @BeforeEach
  void setUp() {
    repository = mock(UserRepository.class);
    var passwordService = mock(PasswordService.class);
    when(passwordService.encode(any())).thenReturn("hashed");
    userService =
        new UserService(repository, passwordService, mock(ApplicationEventPublisher.class));
  }

  @Test
  void createSucceeds() {
    when(repository.existsByUsername("alice")).thenReturn(false);
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = userService.create(new CreateUserRequest("alice", "pw", Role.VIEWER));

    assertThat(result).isInstanceOf(Result.Ok.class);
    var user = ((Result.Ok<User, ?>) result).value();
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getRole()).isEqualTo(Role.VIEWER);
  }

  @Test
  void createFailsOnDuplicateUsername() {
    when(repository.existsByUsername("alice")).thenReturn(true);

    var result = userService.create(new CreateUserRequest("alice", "pw", Role.VIEWER));

    assertThat(result).isInstanceOf(Result.Err.class);
    assertThat(((Result.Err<?, UserError>) result).error())
        .isInstanceOf(UserError.UsernameConflict.class);
    verify(repository, never()).save(any());
  }

  @Test
  void findByIDReturnsNotFoundForMissingUser() {
    var id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());

    var result = userService.findByID(id);

    assertThat(result).isInstanceOf(Result.Err.class);
    assertThat(((Result.Err<?, UserError>) result).error()).isInstanceOf(UserError.NotFound.class);
  }

  @Test
  void updateAppliesRoleChange() {
    var id = UUID.randomUUID();
    var user = new User("alice", "hash", Role.VIEWER);
    when(repository.findById(id)).thenReturn(Optional.of(user));
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result =
        userService.update(id, new UpdateUserRequest(Optional.of(Role.ADMIN), Optional.empty()));

    assertThat(result).isInstanceOf(Result.Ok.class);
    assertThat(((Result.Ok<User, ?>) result).value().getRole()).isEqualTo(Role.ADMIN);
  }

  @Test
  void updateReturnsNotFoundForMissingUser() {
    var id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());

    var result = userService.update(id, new UpdateUserRequest(Optional.empty(), Optional.empty()));

    assertThat(result).isInstanceOf(Result.Err.class);
    assertThat(((Result.Err<?, UserError>) result).error()).isInstanceOf(UserError.NotFound.class);
  }
}
