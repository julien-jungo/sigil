package dev.sigil.auth.keypair;

import com.nimbusds.jose.jwk.JWKSet;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeypairProvider {

    PrivateKey privateKey();

    PublicKey publicKey();

    JWKSet jwkSet();
}
