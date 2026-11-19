package mz.co.moseguros.paymentworker.config;

import java.net.URI;

import mz.co.moseguros.paymentworker.secrets.SecretReader;
import mz.co.moseguros.paymentworker.secrets.VaultSecretReader;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

/**
 * Cofre de segredos (S5-01 §2): o worker só <b>lê</b> as credenciais SFTP das fontes, no momento de
 * cada leitura, pelos caminhos que o backend lhe dá ({@code secretRefs}). Os testes substituem o
 * {@link SecretReader} por um em memória.
 */
@Configuration(proxyBeanMethods = false)
public class VaultConfig {

    /**
     * {@code moseguros.vault}.
     *
     * @param uri   endereço do Vault
     * @param token token de leitura; vazio só em {@code local} e {@code test} ({@link StartupChecks})
     */
    @ConfigurationProperties(prefix = "moseguros.vault")
    public record VaultProperties(String uri, String token) {

        @Override
        public String toString() {
            return "VaultProperties[uri=" + uri + ", token=***]";
        }
    }

    /** Os testes substituem-no por um {@code InMemorySecretReader} {@code @Primary}. */
    @Bean
    SecretReader vaultSecretReader(VaultProperties properties) {
        String token = properties.token() == null || properties.token().isBlank() ? "missing-token"
                : properties.token();
        VaultTemplate template = new VaultTemplate(VaultEndpoint.from(URI.create(properties.uri())),
                new TokenAuthentication(token));
        return new VaultSecretReader(template, VaultSecretReader.DEFAULT_MOUNT);
    }
}
