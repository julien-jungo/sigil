package dev.sigil.auth.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.sigil.auth.keypair.KeypairProperties;
import dev.sigil.auth.keypair.KeypairProvider;
import dev.sigil.common.Result;
import dev.sigil.user.domain.User;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final KeypairProvider keypairProvider;
  private final long expirySeconds;

  public TokenService(KeypairProvider keypairProvider, KeypairProperties props) {
    this.keypairProvider = keypairProvider;
    this.expirySeconds = props.expirySeconds();
  }

  public Result<String, AuthError> issue(User user) {
    try {
      var now = Instant.now();
      var claims =
          new JWTClaimsSet.Builder()
              .subject(user.getID().toString())
              .claim("username", user.getUsername())
              .claim("role", user.getRole().name())
              .issueTime(Date.from(now))
              .expirationTime(Date.from(now.plusSeconds(expirySeconds)))
              .jwtID(UUID.randomUUID().toString())
              .build();

      var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
      jwt.sign(new RSASSASigner(keypairProvider.privateKey()));
      return Result.ok(jwt.serialize());
    } catch (Exception e) {
      return Result.err(new AuthError.InvalidCredentials());
    }
  }

  public Result<JWTClaimsSet, AuthError> introspect(String token) {
    try {
      var jwt = SignedJWT.parse(token);
      var verifier =
          new com.nimbusds.jose.crypto.RSASSAVerifier(
              (java.security.interfaces.RSAPublicKey) keypairProvider.publicKey());
      if (!jwt.verify(verifier)) {
        return Result.err(new AuthError.InvalidToken("Signature verification failed"));
      }
      var claims = jwt.getJWTClaimsSet();
      if (claims.getExpirationTime().before(new Date())) {
        return Result.err(new AuthError.InvalidToken("Token has expired"));
      }
      return Result.ok(claims);
    } catch (Exception e) {
      return Result.err(new AuthError.InvalidToken(e.getMessage()));
    }
  }
}
