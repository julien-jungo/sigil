package dev.sigil.user.web;

import dev.sigil.user.domain.UserError;
import dev.sigil.user.service.UserService;
import dev.sigil.user.web.dto.CreateUserRequest;
import dev.sigil.user.web.dto.UpdateUserRequest;
import dev.sigil.user.web.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<UserResponse> list() {
    return userService.findAll().stream().map(UserResponse::from).toList();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest request) {
    return switch (userService.create(request)) {
      case dev.sigil.common.Result.Ok<?, ?> ok ->
          ResponseEntity.status(HttpStatus.CREATED)
              .body(UserResponse.from((dev.sigil.user.domain.User) ok.value()));
      case dev.sigil.common.Result.Err<?, ?> err ->
          switch ((UserError) err.error()) {
            case UserError.UsernameConflict c ->
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                        ProblemDetail.forStatusAndDetail(
                            HttpStatus.CONFLICT, "Username already exists: " + c.username()));
            case UserError.NotFound n -> throw new IllegalStateException("unreachable");
          };
    };
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
    return switch (userService.update(id, request)) {
      case dev.sigil.common.Result.Ok<?, ?> ok ->
          ResponseEntity.ok(UserResponse.from((dev.sigil.user.domain.User) ok.value()));
      case dev.sigil.common.Result.Err<?, ?> err ->
          switch ((UserError) err.error()) {
            case UserError.NotFound n ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ProblemDetail.forStatusAndDetail(
                            HttpStatus.NOT_FOUND, "User not found: " + n.username()));
            case UserError.UsernameConflict c -> throw new IllegalStateException("unreachable");
          };
    };
  }
}
