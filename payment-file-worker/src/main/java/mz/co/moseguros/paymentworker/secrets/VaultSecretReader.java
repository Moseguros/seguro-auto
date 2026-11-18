package mz.co.moseguros.paymentworker.secrets;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.client.RestClientException;

/**
 * Segredos no HashiCorp Vault em KV v2 (S5-01 §2): o caminho {@code secret/<resto>} lê-se em
 * {@code secret/data/<resto>}, campo {@value #VALUE}. Regista só o caminho, nunca o valor nem o token.
 */
public class VaultSecretReader implements SecretReader {

    public static final String DEFAULT_MOUNT = "secret";
    static final String VALUE = "value";

    private static final Logger log = LoggerFactory.getLogger(VaultSecretReader.class);

    private final String mount;
    private final VaultKeyValueOperations kv;

    public VaultSecretReader(VaultOperations vault, String mount) {
        this.mount = Objects.requireNonNull(mount, "mount");
        this.kv = Objects.requireNonNull(vault, "vault").opsForKeyValue(mount, KeyValueBackend.KV_2);
    }

    @Override
    public Optional<String> read(String path) {
        String key = key(path);
        try {
            VaultResponse response = kv.get(key);
            Object value = response == null || response.getData() == null ? null : response.getData().get(VALUE);
            return Optional.ofNullable(value == null ? null : value.toString());
        } catch (VaultException | RestClientException e) {
            log.warn("Secret store unavailable (read {}): {}", path, e.getClass().getSimpleName());
            throw new SecretStoreUnavailableException("The secret store is unavailable");
        }
    }

    /** {@code secret/insurers/x} → {@code insurers/x}. */
    private String key(String path) {
        Objects.requireNonNull(path, "path");
        String prefix = mount + "/";
        if (!path.startsWith(prefix) || path.length() == prefix.length() || path.contains("..")) {
            throw new IllegalArgumentException("Secret path outside the mount '" + mount + "': " + path);
        }
        return path.substring(prefix.length());
    }

    @Override
    public String toString() {
        return "VaultSecretReader[mount=" + mount + "]";
    }
}
