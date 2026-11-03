package mz.co.moseguros.document;

import java.time.Instant;
import java.util.UUID;

/**
 * Metadados de um ficheiro guardado. Nunca inclui a chave de armazenamento.
 *
 * @param originalFilename nome já limpo, com a extensão do tipo real
 * @param linked           {@code true} depois de {@link DocumentApi#markLinked}; os ficheiros por
 *                         ligar são limpos ao fim de 30 dias
 */
public record FileRef(
        UUID id,
        String ownerType,
        UUID ownerId,
        String purpose,
        String contentType,
        long sizeBytes,
        String sha256,
        String originalFilename,
        FileClassification classification,
        UUID insurerId,
        Instant createdAt,
        boolean linked) {
}
