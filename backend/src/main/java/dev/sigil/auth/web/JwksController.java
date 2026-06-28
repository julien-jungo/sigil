package dev.sigil.auth.web;

import dev.sigil.auth.keypair.KeypairProvider;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksController {

    private final KeypairProvider keypairProvider;

    public JwksController(KeypairProvider keypairProvider) {
        this.keypairProvider = keypairProvider;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return keypairProvider.jwkSet().toJSONObject();
    }
}
