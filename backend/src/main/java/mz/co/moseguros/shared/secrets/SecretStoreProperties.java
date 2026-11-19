package mz.co.moseguros.shared.secrets;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuração do cofre de segredos ({@code moseguros.secrets}, S5-01 §2).
 *
 * @param store {@code vault} (defeito) ou {@code in-memory} (só nos perfis {@code local} e {@code test})
 * @param vault ligação ao Vault; o token é obrigatório com {@code store = vault}
 */
@ConfigurationProperties(prefix = "moseguros.secrets")
public record SecretStoreProperties(@DefaultValue("vault") Store store, @DefaultValue Vault vault) {

    public enum Store {
        VAULT,
        IN_MEMORY
    }

    /**
     * @param uri   endereço do Vault ({@code VAULT_URI})
     * @param token token do Vault ({@code VAULT_TOKEN}); nunca vai para logs nem para {@code toString()}
     * @param mount <i>mount</i> KV v2 (primeiro segmento dos caminhos)
     */
    public record Vault(@DefaultValue("http://localhost:8200") URI uri, String token,
                        @DefaultValue("secret") String mount) {

        public boolean hasToken() {
            return token != null && !token.isBlank();
        }

        @Override
        public String toString() {
            return "Vault[uri=" + uri + ", mount=" + mount + ", token=" + (hasToken() ? "****" : "<empty>") + "]";
        }
    }

    /**
     * Confirma que a configuração serve para os perfis activos.
     *
     * @param localOrTest perfil {@code local} ou {@code test} activo
     * @throws IllegalStateException com a mensagem do que falta
     */
    public void validate(boolean localOrTest) {
        if (store == Store.IN_MEMORY && !localOrTest) {
            throw new IllegalStateException("moseguros.secrets.store=in-memory (MOSEGUROS_SECRETS_STORE) is only "
                    + "allowed with the local or test profile; use vault with VAULT_URI and VAULT_TOKEN");
        }
        if (store == Store.VAULT && (vault == null || !vault.hasToken())) {
            throw new IllegalStateException("moseguros.secrets.vault.token (VAULT_TOKEN) is required when "
                    + "moseguros.secrets.store=vault");
        }
        if (store == Store.VAULT && (vault.mount() == null || vault.mount().isBlank() || vault.mount().contains("/"))) {
            throw new IllegalStateException("moseguros.secrets.vault.mount must be a single path segment (ex.: secret)");
        }
    }
}
