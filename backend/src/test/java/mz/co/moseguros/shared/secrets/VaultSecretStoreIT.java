package mz.co.moseguros.shared.secrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.Map;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.vault.VaultContainer;

/**
 * {@link VaultSecretStore} contra um Vault real em modo dev (S5-01 §2): {@code put} → {@code get}
 * → {@code exists} → {@code delete}; o segredo fica em KV v2 ({@code secret/data/...}) com
 * {@code { "value": ... }} e o {@code delete} apaga todas as versões.
 */
class VaultSecretStoreIT {

    static final String TOKEN = "it-root-token";

    static VaultContainer<?> vault;
    static VaultTemplate template;
    static VaultSecretStore store;

    @BeforeAll
    static void start() {
        vault = new VaultContainer<>(DockerImageName.parse("hashicorp/vault:1.20.4")).withVaultToken(TOKEN);
        vault.start();
        template = new VaultTemplate(VaultEndpoint.from(URI.create(vault.getHttpHostAddress())),
                new TokenAuthentication(TOKEN));
        store = new VaultSecretStore(template, "secret");
    }

    @AfterAll
    static void stop() {
        if (vault != null) {
            vault.stop();
        }
    }

    @Test
    void putGetExistsDelete() {
        String path = "secret/insurers/0193aaaa/payments/MPESA/apiKey";
        assertThat(store.exists(path)).isFalse();
        assertThat(store.get(path)).isEmpty();

        store.put(path, "valor-1");
        assertThat(store.get(path)).contains("valor-1");
        assertThat(store.exists(path)).isTrue();

        // KV v2: secret/data/<caminho> com { "value": ... }
        VaultResponse raw = template.read("secret/data/insurers/0193aaaa/payments/MPESA/apiKey");
        assertThat(raw).isNotNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) raw.getRequiredData().get("data");
        assertThat(data).containsExactly(Map.entry("value", "valor-1"));

        store.put(path, "valor-2");
        assertThat(store.get(path)).contains("valor-2");
        VaultResponse metadata = template.read("secret/metadata/insurers/0193aaaa/payments/MPESA/apiKey");
        assertThat(metadata).isNotNull();
        assertThat(metadata.getRequiredData().get("current_version")).isEqualTo(2);

        store.delete(path);
        assertThat(store.exists(path)).isFalse();
        // todas as versões e os metadados
        assertThat(template.read("secret/metadata/insurers/0193aaaa/payments/MPESA/apiKey")).isNull();
        store.delete(path);
    }

    @Test
    void fieldNamesWithDotsAreKept() {
        String path = "secret/insurers/0193bbbb/payments/BANK_REFERENCE/sftp.password";
        store.put(path, "sftp-pass");
        assertThat(store.get(path)).contains("sftp-pass");
        assertThat(template.read("secret/data/insurers/0193bbbb/payments/BANK_REFERENCE/sftp.password")).isNotNull();
    }

    @Test
    void rejectsPathsOutsideTheMount() {
        assertThatThrownBy(() -> store.put("other/x", "v")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unavailableVaultIsSecretStoreUnavailable() {
        VaultSecretStore down = new VaultSecretStore(new VaultTemplate(VaultEndpoint.from(URI.create("http://127.0.0.1:1")),
                new TokenAuthentication(TOKEN)), "secret");
        assertThatThrownBy(() -> down.get("secret/insurers/x/payments/MPESA/apiKey"))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> {
                    assertThat(((DomainException) e).getCode()).isEqualTo("SECRET_STORE_UNAVAILABLE");
                    assertThat(((DomainException) e).getHttpStatus()).isEqualTo(503);
                });
        assertThatThrownBy(() -> down.put("secret/insurers/x/payments/MPESA/apiKey", "v"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void wrongTokenIsSecretStoreUnavailable() {
        VaultSecretStore wrong = new VaultSecretStore(new VaultTemplate(
                VaultEndpoint.from(URI.create(vault.getHttpHostAddress())), new TokenAuthentication("errado")), "secret");
        assertThatThrownBy(() -> wrong.put("secret/insurers/x/payments/MPESA/apiKey", "v"))
                .isInstanceOf(DomainException.class)
                .hasMessageNotContaining("errado");
    }
}
