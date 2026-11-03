package mz.co.moseguros.rating.infrastructure;

/**
 * Segredo do HMAC de uma seguradora externa a partir da referência gravada em
 * {@code insurer_profile.rating_secret_ref} (S3-02 §8). Até ao cofre de segredos (S5) só há
 * {@link ConfigRatingSecretResolver} ({@code config:<chave>}); o S5 acrescenta {@code secret/…}.
 */
public interface RatingSecretResolver {

    /**
     * @throws UnknownSecretRefException se a referência não tiver formato conhecido ou não existir
     */
    String resolve(String ref);

    /** Referência desconhecida: a seguradora fica indisponível ({@code INSURER_UNAVAILABLE}). */
    class UnknownSecretRefException extends RuntimeException {

        public UnknownSecretRefException(String ref) {
            super("Unknown rating secret reference: " + ref);
        }
    }
}
