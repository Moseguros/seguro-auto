package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Entidade e referência de uma intenção {@code BANK_REFERENCE} ({@code payment.reference_payment},
 * S5-02 §2; rede Ponto24, {@code docs/specs/interbancos/}). A referência (factura 7 + mês 2 + controlo
 * 2) vem do {@code ReferenceGenerator} (S5-07) e nunca se repete na entidade.
 */
@Entity
@Table(schema = "payment", name = "reference_payment")
public class ReferencePayment extends BaseEntity {

    /** Número máximo de envios da referência por SMS (S5-02 §7). */
    public static final int MAX_SMS = 3;

    @Column(name = "intent_id", nullable = false, updatable = false)
    private UUID intentId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "payer_user_id", nullable = false, updatable = false)
    private UUID payerUserId;

    @Column(name = "entity", nullable = false, updatable = false, length = 5)
    private String entity;

    @Column(name = "reference", nullable = false, updatable = false, length = 11)
    private String reference;

    @Column(name = "invoice_number", nullable = false, updatable = false)
    private int invoiceNumber;

    @Column(name = "reference_month", nullable = false, updatable = false)
    private short referenceMonth;

    @Column(name = "amount", nullable = false, updatable = false)
    private long amount;

    @Column(name = "valid_until", nullable = false, updatable = false)
    private Instant validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ReferenceStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "bank_transaction_id", length = 60)
    private String bankTransactionId;

    @Column(name = "message_id", length = 64)
    private String messageId;

    @Column(name = "document_file_id")
    private UUID documentFileId;

    @Column(name = "sms_sent_count", nullable = false)
    private int smsSentCount;

    protected ReferencePayment() {
    }

    public static ReferencePayment active(PaymentIntent intent, String entity, String reference, int invoiceNumber,
                                          int month) {
        ReferencePayment r = new ReferencePayment();
        r.intentId = intent.getId();
        r.insurerId = intent.getInsurerId();
        r.payerUserId = intent.getPayerUserId();
        r.entity = Objects.requireNonNull(entity, "entity");
        r.reference = Objects.requireNonNull(reference, "reference");
        r.invoiceNumber = invoiceNumber;
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be 1-12");
        }
        r.referenceMonth = (short) month;
        r.amount = intent.getAmount();
        r.validUntil = intent.getExpiresAt();
        r.status = ReferenceStatus.ACTIVE;
        return r;
    }

    public boolean isActive() {
        return status == ReferenceStatus.ACTIVE;
    }

    /** A intenção expirou ({@code PaymentExpiryJob}). */
    public void expire() {
        if (status == ReferenceStatus.ACTIVE) {
            status = ReferenceStatus.EXPIRED;
        }
    }

    /** A intenção foi substituída ({@code SUPERSEDED}). Um pagamento na rede depois disto é uma excepção (S5-10). */
    public void cancel() {
        if (status == ReferenceStatus.ACTIVE) {
            status = ReferenceStatus.CANCELLED;
        }
    }

    /** A intenção foi confirmada; os dados do banco ({@code bank_transaction_id}, {@code message_id}) vêm no S5-10. */
    public void markPaid(Instant at) {
        this.status = ReferenceStatus.PAID;
        if (this.paidAt == null) {
            this.paidAt = at;
        }
    }

    public void smsSent() {
        smsSentCount++;
    }

    public void recordDocument(UUID fileId) {
        this.documentFileId = Objects.requireNonNull(fileId, "fileId");
    }

    public Money amount() {
        return Money.ofCentavos(amount);
    }

    public UUID getIntentId() {
        return intentId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getPayerUserId() {
        return payerUserId;
    }

    public String getEntity() {
        return entity;
    }

    public String getReference() {
        return reference;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public int getReferenceMonth() {
        return referenceMonth;
    }

    public long getAmount() {
        return amount;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public ReferenceStatus getStatus() {
        return status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public UUID getDocumentFileId() {
        return documentFileId;
    }

    public int getSmsSentCount() {
        return smsSentCount;
    }
}
