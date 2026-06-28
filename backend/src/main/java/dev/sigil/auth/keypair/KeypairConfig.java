package dev.sigil.auth.keypair;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeypairConfig {

    @Bean
    public KeypairProvider keypairProvider(KeypairProperties props) {
        return switch (props.keypairSource()) {
            case "config" -> new ConfiguredKeypairProvider(props.privateKey());
            case "generated" -> new GeneratedKeypairProvider();
            default -> throw new IllegalStateException(
                    "Unsupported JWT_KEYPAIR_SOURCE: '" + props.keypairSource() + "'. Use 'config' or 'generated'.");
        };
    }
}
