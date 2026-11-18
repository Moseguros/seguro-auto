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
 * Caso para o financeiro da seguradora ({@code payment.payment_exception}, S5-02 §2): um pagamento
 * recebido que a plataforma não pode associar ou aceitar sozinha. O nome Java evita a confusão com
 * {@code java.lang.Exception}. A resolução (S5-10) nunca movimenta dinheiro: uma devolução faz-se fora
 * da plataforma.
 */
@Entity
@Table(schema = "payment", name = "payment_exception")
public class PaymentExceptionCase extends BaseEntity {

    public static final int MAX_DETAIL = 60;

    /** Estado de um caso. */
    public enum Status { OPEN, RESOLVED }

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 25)
    private PaymentExceptionType type;

    @Column(name = "intent_id", updatable = false)
    private UUID intentId;

    @Column(name = "entity", length = 5)
    private String entity;

    @Column(name = "reference", length = 11)
    private String reference;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "detail", length = MAX_DETAIL)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status = Status.OPEN;

    protected PaymentExceptionCase() {
    }

    /** {@code TARGET_NOT_PAYABLE} de uma intenção confirmada (S5-02 §9). */
    public static PaymentExceptionCase targetNotPayable(PaymentIntent intent, ReferencePayment reference,
                                                        String detail) {
        PaymentExceptionCase c = new PaymentExceptionCase();
        c.insurerId = intent.getInsurerId();
        c.type = PaymentExceptionType.TARGET_NOT_PAYABLE;
        c.intentId = intent.getId();
        c.amount = intent.getAmount();
        c.paidAt = intent.getSucceededAt();
        String d = Objects.requireNonNull(detail, "detail");
        c.detail = d.length() > MAX_DETAIL ? d.substring(0, MAX_DETAIL) : d;
        if (reference != null) {
            c.entity = reference.getEntity();
            c.reference = reference.getReference();
        }
        return c;
    }

    /**
     * {@code UNRESOLVED_STATUS} (S5-04 §4): intenção M-Pesa {@code UNKNOWN} sem resposta final passado o
     * prazo das consultas; o financeiro resolve (S5-10).
     */
    public static PaymentExceptionCase unresolvedStatus(PaymentIntent intent) {
        PaymentExceptionCase c = new PaymentExceptionCase();
        c.insurerId = intent.getInsurerId();
        c.type = PaymentExceptionType.UNRESOLVED_STATUS;
        c.intentId = intent.getId();
        c.amount = intent.getAmount();
        c.detail = intent.getMethod().name() + " status unknown";
        return c;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public PaymentExceptionType getType() {
        return type;
    }

    public UUID getIntentId() {
        return intentId;
    }

    public String getEntity() {
        return entity;
    }

    public String getReference() {
        return reference;
    }

    public Long getAmount() {
        return amount;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public String getDetail() {
        return detail;
    }

    public Status getStatus() {
        return status;
    }
}
