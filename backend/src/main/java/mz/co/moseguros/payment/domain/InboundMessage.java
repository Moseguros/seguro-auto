package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Uma mensagem RabbitMQ recebida do {@code payment-file-worker} ({@code payment.inbound_message}, S5-10 §1):
 * a idempotência do consumo. A linha nasce com {@code insert … on conflict (message_id) do nothing}
 * ({@link InboundMessageRepository#insertIfAbsent}); uma repetição não insere e não faz mais nada.
 */
@Entity
@Table(schema = "payment", name = "inbound_message")
public class InboundMessage extends BaseEntity {

    /** Valores de {@code message_type}: o nome simples da classe da mensagem ({@code MessageHeaders.typeOf}). */
    public static final String REFERENCE_PAYMENT_RECEIVED = "ReferencePaymentReceived";
    public static final String PAYMENT_FILE_PROCESSED = "PaymentFileProcessed";
    public static final String PAYMENT_FILE_REJECTED = "PaymentFileRejected";
    public static final String PAYMENT_SOURCE_POLLED = "PaymentSourcePolled";

    /** Resultado do tratamento da mensagem. */
    public enum Outcome {
        /** Pagamento confirmado (intenção {@code SUCCEEDED}). */
        CONFIRMED,
        /** Pagamento que gerou uma excepção para o financeiro. */
        EXCEPTION,
        /** Ficheiro ou leitura registados. */
        RECORDED,
        /** Mensagem válida mas sem efeito (leitura fora de ordem, fonte de outra seguradora). */
        IGNORED
    }

    @Column(name = "message_id", nullable = false, updatable = false, length = 64)
    private String messageId;

    @Column(name = "message_type", nullable = false, updatable = false, length = 40)
    private String messageType;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "source_id", nullable = false, updatable = false)
    private UUID sourceId;

    @Column(name = "file_sha256", length = 64)
    private String fileSha256;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false, length = 12)
    private Outcome outcome;

    @Column(name = "intent_id")
    private UUID intentId;

    @Column(name = "exception_id")
    private UUID exceptionId;

    protected InboundMessage() {
    }

    /** Resultado final da mensagem. */
    public void complete(Outcome outcome, UUID intentId, UUID exceptionId) {
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.intentId = intentId;
        this.exceptionId = exceptionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public String getFileSha256() {
        return fileSha256;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public UUID getIntentId() {
        return intentId;
    }

    public UUID getExceptionId() {
        return exceptionId;
    }
}
