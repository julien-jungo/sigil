package dev.sigil.auth.keypair;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeypairConfig {

    @Bean
    public KeypairProvider keypairProvider(KeypairProperties props) {
        return switch (props.keypairSource()) {
            case "config" -> new ConfiguredKeypairProvider(props.privateKey());
            default -> new GeneratedKeypairProvider();
        };
    }
}
