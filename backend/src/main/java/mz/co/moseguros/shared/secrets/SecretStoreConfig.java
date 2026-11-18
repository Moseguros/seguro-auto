package mz.co.moseguros.shared.secrets;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.vault.authentication.SimpleSessionManager;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

/**
 * Escolhe o {@link SecretStore} (S5-01 §2): Vault KV v2 ou, só nos perfis {@code local} e
 * {@code test}, em memória. Fora desses perfis, {@code in-memory} ou token vazio impedem o
 * arranque. A autenticação no Vault de staging e produção (AppRole ou equivalente) decide-se com o
 * alojamento (S6-08); por agora só há token.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecretStoreProperties.class)
public class SecretStoreConfig {

    static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Bean
    SecretStore secretStore(SecretStoreProperties properties, Environment environment) {
        properties.validate(environment.acceptsProfiles(Profiles.of("local", "test")));
        if (properties.store() == SecretStoreProperties.Store.IN_MEMORY) {
            return new InMemorySecretStore();
        }
        SecretStoreProperties.Vault vault = properties.vault();
        SimpleClientHttpRequestFactory http = new SimpleClientHttpRequestFactory();
        http.setConnectTimeout(TIMEOUT);
        http.setReadTimeout(TIMEOUT);
        VaultTemplate template = new VaultTemplate(VaultEndpoint.from(vault.uri()), http,
                new SimpleSessionManager(new TokenAuthentication(vault.token())));
        return new VaultSecretStore(template, vault.mount());
    }
}
