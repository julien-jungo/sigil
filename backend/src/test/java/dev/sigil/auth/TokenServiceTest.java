package dev.sigil.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.sigil.auth.keypair.GeneratedKeypairProvider;
import dev.sigil.auth.keypair.KeypairProperties;
import dev.sigil.auth.service.TokenService;
import dev.sigil.common.Result;
import dev.sigil.user.domain.Role;
import dev.sigil.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenServiceTest {

  private TokenService tokenService;

  @BeforeEach
  void setUp() {
    var keypairProvider = new GeneratedKeypairProvider();
    var props = mock(KeypairProperties.class);
    when(props.expirySeconds()).thenReturn(3600L);
    tokenService = new TokenService(keypairProvider, props);
  }

  @Test
  void issueAndIntrospectRoundTrip() {
    var user = new User("alice", "hash", Role.ADMIN);

    var issued = tokenService.issue(user);
    assertThat(issued).isInstanceOf(Result.Ok.class);

    var token = ((Result.Ok<String, ?>) issued).value();
    var introspected = tokenService.introspect(token);
    assertThat(introspected).isInstanceOf(Result.Ok.class);

    var claims = ((Result.Ok<com.nimbusds.jwt.JWTClaimsSet, ?>) introspected).value();
    assertThat(claims.getClaim("username")).isEqualTo("alice");
    assertThat(claims.getClaim("role")).isEqualTo("ADMIN");
  }

  @Test
  void introspectRejectsGarbageToken() {
    var result = tokenService.introspect("not.a.jwt");
    assertThat(result).isInstanceOf(Result.Err.class);
  }

  @Test
  void introspectRejectsTokenSignedWithDifferentKey() {
    var otherService = new TokenService(new GeneratedKeypairProvider(), mockProps());
    var user = new User("bob", "hash", Role.VIEWER);
    var token = ((Result.Ok<String, ?>) otherService.issue(user)).value();

    var result = tokenService.introspect(token);
    assertThat(result).isInstanceOf(Result.Err.class);
  }

  private KeypairProperties mockProps() {
    var props = mock(KeypairProperties.class);
    when(props.expirySeconds()).thenReturn(3600L);
    return props;
  }
}
