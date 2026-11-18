package mz.co.moseguros.paymentcontracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

/**
 * Um ficheiro de movimentos rejeitado por inteiro: nenhum dos seus registos é publicado (S5-08,
 * S5-09). Chave {@link PaymentsTopology#fileRejectedKey(UUID)}, fila {@link
 * PaymentsTopology#PAYMENT_FILE_EVENTS_QUEUE}. Schema: {@code
 * schemas/payment-file-rejected.v1.json}.
 *
 * @param messageId {@link MessageIds#paymentFileRejected(UUID, String, Instant)}
 * @param formatFileId identificador do ficheiro no formato; {@code null} se o ficheiro for
 *     ilegível
 * @param detail explicação curta (até {@value #DETAIL_MAX_LENGTH} caracteres), sem dados pessoais
 * @param lineNo linha do problema; {@code null} se não for de uma linha
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentFileRejected(
        String messageId,
        int schemaVersion,
        UUID insurerId,
        UUID sourceId,
        String fileFormat,
        String fileName,
        String sha256,
        String formatFileId,
        FileRejectionReason reason,
        String detail,
        Integer lineNo,
        Instant rejectedAt) {

    public static final int DETAIL_MAX_LENGTH = 200;
}
