package mz.co.moseguros.shared.secrets;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cofre em memória (S5-01 §2), só para testes e desenvolvimento ({@code moseguros.secrets.store=
 * in-memory}; fora dos perfis {@code local} e {@code test} a aplicação não arranca com ele, ver
 * {@link SecretStoreConfig}). Perde tudo ao reiniciar.
 */
public class InMemorySecretStore implements SecretStore {

    private final Map<String, String> secrets = new ConcurrentHashMap<>();

    @Override
    public void put(String path, String value) {
        secrets.put(new SecretRef(path).path(), Objects.requireNonNull(value, "value"));
    }

    @Override
    public Optional<String> get(String path) {
        return Optional.ofNullable(secrets.get(new SecretRef(path).path()));
    }

    @Override
    public void delete(String path) {
        secrets.remove(new SecretRef(path).path());
    }

    @Override
    public boolean exists(String path) {
        return secrets.containsKey(new SecretRef(path).path());
    }

    @Override
    public String toString() {
        return "InMemorySecretStore[" + secrets.size() + " secrets]";
    }
}
