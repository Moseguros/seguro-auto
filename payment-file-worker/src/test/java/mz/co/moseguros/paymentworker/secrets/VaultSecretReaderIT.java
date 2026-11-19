package mz.co.moseguros.paymentworker.secrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.vault.VaultContainer;

/** {@link VaultSecretReader} contra um Vault real (KV v2), com o formato do S5-01 ({@code { "value": … }}). */
class VaultSecretReaderIT {

    static final String TOKEN = "test-root-token";
    static final VaultContainer<?> VAULT = new VaultContainer<>(DockerImageName.parse("hashicorp/vault:1.20.4"))
            .withVaultToken(TOKEN);

    static VaultTemplate template;

    @BeforeAll
    static void start() {
        VAULT.start();
        template = new VaultTemplate(VaultEndpoint.from(URI.create(VAULT.getHttpHostAddress())),
                new TokenAuthentication(TOKEN));
    }

    @AfterAll
    static void stop() {
        VAULT.stop();
    }

    @Test
    void readsASecretWrittenInKvV2WithTheS501Format() {
        UUID insurer = UUID.randomUUID();
        String key = "insurers/" + insurer + "/payments/BANK_REFERENCE/sftp.password";
        template.opsForKeyValue("secret", KeyValueBackend.KV_2).put(key, Map.of("value", "palavra-passe-do-sftp"));

        VaultSecretReader reader = new VaultSecretReader(template, VaultSecretReader.DEFAULT_MOUNT);

        assertThat(reader.read("secret/" + key)).contains("palavra-passe-do-sftp");
        assertThat(reader.read("secret/insurers/" + UUID.randomUUID() + "/payments/BANK_REFERENCE/sftp.password"))
                .isEmpty();
    }

    @Test
    void pathsOutsideTheMountAreRefused() {
        VaultSecretReader reader = new VaultSecretReader(template, VaultSecretReader.DEFAULT_MOUNT);
        assertThatThrownBy(() -> reader.read("outro/insurers/x")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reader.read("secret/../sys/x")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unavailableVaultIsReported() {
        VaultTemplate down = new VaultTemplate(VaultEndpoint.from(URI.create("http://127.0.0.1:1")),
                new TokenAuthentication(TOKEN));
        VaultSecretReader reader = new VaultSecretReader(down, VaultSecretReader.DEFAULT_MOUNT);
        assertThatThrownBy(() -> reader.read("secret/insurers/x/payments/BANK_REFERENCE/sftp.password"))
                .isInstanceOf(SecretReader.SecretStoreUnavailableException.class)
                .hasMessageNotContaining(TOKEN);
    }
}
