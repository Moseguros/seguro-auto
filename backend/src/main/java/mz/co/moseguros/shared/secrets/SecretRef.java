package mz.co.moseguros.shared.secrets;

import java.util.Objects;

/**
 * Referência de um segredo no cofre (o caminho, nunca o valor), ex.:
 * {@code secret/insurers/<uuid>/payments/MPESA/apiKey}. Quem precisa do valor chama
 * {@link SecretStore#get(String)} no momento em que o usa.
 */
public record SecretRef(String path) {

    public SecretRef {
        Objects.requireNonNull(path, "path");
        if (path.isBlank() || path.startsWith("/") || path.contains("..")) {
            throw new IllegalArgumentException("Invalid secret path: " + path);
        }
    }
}
