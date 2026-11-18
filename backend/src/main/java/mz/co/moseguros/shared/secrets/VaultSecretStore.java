package mz.co.moseguros.shared.secrets;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.client.RestClientException;

/**
 * Cofre HashiCorp Vault em KV v2 (S5-01 §2). Cada segredo fica em
 * {@code <mount>/data/<resto do caminho>} com os dados {@code { "value": "<valor>" }};
 * {@link #delete} apaga os metadados (todas as versões).
 *
 * <p>Erros de ligação ou do Vault → {@code SECRET_STORE_UNAVAILABLE} (503). Regista só o caminho,
 * nunca o valor nem o token.
 */
public class VaultSecretStore implements SecretStore {

    static final String VALUE = "value";

    private static final Logger log = LoggerFactory.getLogger(VaultSecretStore.class);

    private final VaultOperations vault;
    private final String mount;
    private final VaultKeyValueOperations kv;

    public VaultSecretStore(VaultOperations vault, String mount) {
        this.vault = Objects.requireNonNull(vault, "vault");
        this.mount = Objects.requireNonNull(mount, "mount");
        this.kv = vault.opsForKeyValue(mount, KeyValueBackend.KV_2);
    }

    @Override
    public void put(String path, String value) {
        Objects.requireNonNull(value, "value");
        String key = key(path);
        call(path, "put", () -> {
            kv.put(key, Map.of(VALUE, value));
            return null;
        });
    }

    @Override
    public Optional<String> get(String path) {
        String key = key(path);
        return call(path, "get", () -> {
            VaultResponse response = kv.get(key);
            Object value = response == null || response.getData() == null ? null : response.getData().get(VALUE);
            return Optional.ofNullable(value == null ? null : value.toString());
        });
    }

    @Override
    public void delete(String path) {
        String key = key(path);
        call(path, "delete", () -> {
            vault.opsForVersionedKeyValue(mount).opsForKeyValueMetadata().delete(key);
            return null;
        });
    }

    @Override
    public boolean exists(String path) {
        return get(path).isPresent();
    }

    /** Caminho sem o mount: {@code secret/insurers/x} → {@code insurers/x}. */
    private String key(String path) {
        String p = new SecretRef(path).path();
        String prefix = mount + "/";
        if (!p.startsWith(prefix) || p.length() == prefix.length()) {
            throw new IllegalArgumentException("Secret path outside the mount '" + mount + "': " + p);
        }
        return p.substring(prefix.length());
    }

    private <T> T call(String path, String operation, java.util.function.Supplier<T> action) {
        try {
            return action.get();
        } catch (VaultException | RestClientException e) {
            // Sem a mensagem da causa: pode trazer o corpo do pedido. Só o caminho e a operação.
            log.warn("Secret store unavailable ({} {}): {}", operation, path, e.getClass().getSimpleName());
            throw DomainException.of(ErrorCodes.SECRET_STORE_UNAVAILABLE, "The secret store is unavailable");
        }
    }

    @Override
    public String toString() {
        return "VaultSecretStore[mount=" + mount + "]";
    }
}
