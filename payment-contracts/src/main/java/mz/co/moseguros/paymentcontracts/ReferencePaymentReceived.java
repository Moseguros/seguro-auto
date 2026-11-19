package mz.co.moseguros.paymentcontracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

/**
 * Um pagamento por entidade e referência lido de um registo de detalhe de um ficheiro de
 * movimentos MEPS ou BMEPS (ADR-007, {@code docs/specs/interbancos/}). Publicado pelo worker com a
 * chave {@link PaymentsTopology#referencePaidKey(UUID)}; consumido pelo backend na fila {@link
 * PaymentsTopology#REFERENCE_PAYMENTS_QUEUE} (S5-10). Schema: {@code
 * schemas/reference-payment-received.v1.json}.
 *
 * @param messageId {@link MessageIds#referencePaymentReceived(UUID, String, int)}
 * @param schemaVersion sempre {@link MessageHeaders#SCHEMA_VERSION}
 * @param insurerId seguradora dona da fonte
 * @param sourceId fonte (configuração {@code BANK_REFERENCE} da seguradora, S5-01)
 * @param fileFormat {@link FileSourceDescriptor#FORMAT_MEPS} ou {@link
 *     FileSourceDescriptor#FORMAT_BMEPS}
 * @param entity entidade (5 dígitos): MEPS A033; BMEPS a da configuração da fonte
 * @param reference referência (11 dígitos): MEPS A034; BMEPS "Referência"
 * @param amount montante pago (MEPS A011; BMEPS "Valor")
 * @param fee tarifa (MEPS A032) ou comissão (BMEPS); {@code null} se o ficheiro não a trouxer
 * @param paidAt data e hora da transacção do cliente (hora de Maputo convertida para UTC)
 * @param channel tipo de terminal do MEPS (A015); {@code null} se vazio ou no BMEPS
 * @param bankTransactionId identificador do banco se vier preenchido (não só zeros); senão {@code
 *     null}
 * @param fileName nome do ficheiro no SFTP
 * @param fileSha256 SHA-256 do conteúdo do ficheiro (hexadecimal minúsculo)
 * @param formatFileId identificador do ficheiro no formato: MEPS A005 ({@code AAAAMMDDS}); BMEPS
 *     {@code <empresa>-<perfil>-<ID do ficheiro>}
 * @param lineNo número da linha no ficheiro (1 = cabeçalho)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReferencePaymentReceived(
        String messageId,
        int schemaVersion,
        UUID insurerId,
        UUID sourceId,
        String fileFormat,
        String entity,
        String reference,
        MoneyAmount amount,
        MoneyAmount fee,
        Instant paidAt,
        String channel,
        String bankTransactionId,
        String fileName,
        String fileSha256,
        String formatFileId,
        int lineNo) {}
