package mz.co.moseguros.paymentcontracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

/**
 * Um ficheiro de movimentos lido por inteiro e com todos os registos publicados (S5-09). Chave
 * {@link PaymentsTopology#fileProcessedKey(UUID)}, fila {@link
 * PaymentsTopology#PAYMENT_FILE_EVENTS_QUEUE}. Schema: {@code
 * schemas/payment-file-processed.v1.json}.
 *
 * @param messageId {@link MessageIds#paymentFileProcessed(UUID, String)}
 * @param sha256 SHA-256 do conteúdo do ficheiro (hexadecimal minúsculo)
 * @param formatFileId identificador do ficheiro no formato (ver {@link ReferencePaymentReceived})
 * @param recordCount número de registos de detalhe
 * @param totalAmount soma dos montantes pagos
 * @param totalFees soma das tarifas/comissões; {@code null} se o formato não as tiver
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentFileProcessed(
        String messageId,
        int schemaVersion,
        UUID insurerId,
        UUID sourceId,
        String fileFormat,
        String fileName,
        String sha256,
        String formatFileId,
        int recordCount,
        MoneyAmount totalAmount,
        MoneyAmount totalFees,
        Instant processedAt) {}
