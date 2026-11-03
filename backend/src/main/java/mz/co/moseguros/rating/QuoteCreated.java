package mz.co.moseguros.rating;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: uma linha de {@code rating.quote} foi gravada (S3-02 §13). Um por cotação,
 * publicado na gravação (outbox do Spring Modulith). Ninguém o ouve no S3.
 *
 * @param origin      {@code CUSTOMER} ou {@code BROKER}
 * @param status      {@code OFFERED} ou {@code NOT_AVAILABLE}
 * @param totalAmount centavos; {@code null} em {@code NOT_AVAILABLE}
 */
public record QuoteCreated(UUID eventId, Instant occurredAt, UUID quoteId, UUID quoteRequestId, UUID insurerId,
                           UUID productVersionId, String origin, String status, Long totalAmount) {
}
