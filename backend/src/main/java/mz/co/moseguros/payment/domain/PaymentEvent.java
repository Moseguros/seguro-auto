package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Histórico técnico de uma intenção ({@code payment.payment_event}, S5-02 §2); só se acrescentam
 * linhas. {@code rawPayload} chega já mascarado (sem tokens nem segredos, telemóvel mascarado) e nunca
 * sai na API.
 */
@Entity
@Table(schema = "payment", name = "payment_event")
public class PaymentEvent extends BaseEntity {

    @Column(name = "intent_id", nullable = false, updatable = false)
    private UUID intentId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 30)
    private PaymentEventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", updatable = false, length = 12)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", updatable = false, length = 12)
    private PaymentStatus toStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", updatable = false, columnDefinition = "jsonb")
    private JsonNode rawPayload;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    protected PaymentEvent() {
    }

    public static PaymentEvent of(PaymentIntent intent, PaymentEventType type, PaymentStatus from, PaymentStatus to,
                                  JsonNode maskedPayload, Instant at) {
        PaymentEvent e = new PaymentEvent();
        e.intentId = intent.getId();
        e.insurerId = intent.getInsurerId();
        e.type = Objects.requireNonNull(type, "type");
        e.fromStatus = from;
        e.toStatus = to;
        e.rawPayload = maskedPayload == null ? null : maskedPayload.deepCopy();
        e.receivedAt = Objects.requireNonNull(at, "at");
        return e;
    }

    public UUID getIntentId() {
        return intentId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public PaymentEventType getType() {
        return type;
    }

    public PaymentStatus getFromStatus() {
        return fromStatus;
    }

    public PaymentStatus getToStatus() {
        return toStatus;
    }

    public JsonNode getRawPayload() {
        return rawPayload == null ? null : rawPayload.deepCopy();
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
