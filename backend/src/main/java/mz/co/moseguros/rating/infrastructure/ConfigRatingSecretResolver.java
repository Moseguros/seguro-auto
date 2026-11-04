package mz.co.moseguros.rating.infrastructure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mz.co.moseguros.rating.application.RatingProperties;
import org.springframework.stereotype.Component;

/**
 * Segredos na configuração ({@code moseguros.rating.secrets.<chave>}; em staging e produção, uma
 * variável de ambiente): a referência é {@code config:<chave>}, com {@code <chave>}
 * {@code ^[a-z0-9-]{1,40}$} (S3-02 §8). Única implementação até ao S5.
 */
@Component
public class ConfigRatingSecretResolver implements RatingSecretResolver {

    static final Pattern REF = Pattern.compile("^config:([a-z0-9-]{1,40})$");

    private final RatingProperties properties;

    public ConfigRatingSecretResolver(RatingProperties properties) {
        this.properties = properties;
    }

    @Override
    public String resolve(String ref) {
        Matcher m = ref == null ? null : REF.matcher(ref);
        if (m == null || !m.matches()) {
            throw new UnknownSecretRefException(ref);
        }
        String secret = properties.getSecrets().get(m.group(1));
        if (secret == null || secret.isEmpty()) {
            throw new UnknownSecretRefException(ref);
        }
        return secret;
    }
}
