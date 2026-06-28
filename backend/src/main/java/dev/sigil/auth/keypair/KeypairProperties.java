package dev.sigil.auth.keypair;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sigil.jwt")
public record KeypairProperties(String keypairSource, String privateKey, long expirySeconds) {}
