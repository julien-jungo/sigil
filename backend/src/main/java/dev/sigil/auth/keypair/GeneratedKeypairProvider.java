package dev.sigil.auth.keypair;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class GeneratedKeypairProvider implements KeypairProvider {

    private final RSAKey rsaKey;

    public GeneratedKeypairProvider() {
        try {
            this.rsaKey = new RSAKeyGenerator(2048).keyID("sigil-key").generate();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA keypair", e);
        }
    }

    @Override
    public PrivateKey privateKey() {
        try {
            return rsaKey.toPrivateKey();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract private key", e);
        }
    }

    @Override
    public PublicKey publicKey() {
        try {
            return rsaKey.toPublicKey();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract public key", e);
        }
    }

    @Override
    public JWKSet jwkSet() {
        return new JWKSet(rsaKey.toPublicJWK());
    }
}
