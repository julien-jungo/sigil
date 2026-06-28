package dev.sigil.auth.keypair;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

public class ConfiguredKeypairProvider implements KeypairProvider {

    private final RSAKey rsaKey;

    public ConfiguredKeypairProvider(String pemPrivateKey) {
        try {
            this.rsaKey = RSAKey.parseFromPEMEncodedObjects(pemPrivateKey).toRSAKey();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse PEM private key", e);
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
