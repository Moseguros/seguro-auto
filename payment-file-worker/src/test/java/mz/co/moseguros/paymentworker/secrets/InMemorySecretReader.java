package mz.co.moseguros.paymentworker.secrets;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Cofre em memória, só para os testes. */
public class InMemorySecretReader implements SecretReader {

    private final Map<String, String> values = new ConcurrentHashMap<>();

    public void put(String path, String value) {
        values.put(path, value);
    }

    public void remove(String path) {
        values.remove(path);
    }

    public void clear() {
        values.clear();
    }

    @Override
    public Optional<String> read(String path) {
        return Optional.ofNullable(values.get(path));
    }

    @Override
    public String toString() {
        return "InMemorySecretReader[" + values.size() + " secrets]";
    }
}
