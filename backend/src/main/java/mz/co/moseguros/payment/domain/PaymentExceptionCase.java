package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Caso para o financeiro da seguradora ({@code payment.payment_exception}, S5-02 §2): um pagamento
 * recebido que a plataforma não pode associar ou aceitar sozinha. O nome Java evita a confusão com
 * {@code java.lang.Exception}. A resolução (S5-10 §6) nunca movimenta dinheiro: uma devolução
 * ({@link PaymentResolution#REFUND}) faz-se fora da plataforma, pela seguradora.
 */
@Entity
@Table(schema = "payment", name = "payment_exception")
public class PaymentExceptionCase extends BaseEntity {

    public static final int MAX_DETAIL = 60;
    public static final int MAX_FILE_NAME = 255;
    public static final int MAX_BANK_TRANSACTION_ID = 64;
    public static final int NOTE_MIN = 5;
    public static final int NOTE_MAX = 500;

    /** Estado de um caso. */
    public enum Status { OPEN, RESOLVED }

    private static final Set<PaymentResolution> LINK_REFUND_IGNORE =
            EnumSet.of(PaymentResolution.LINKED, PaymentResolution.REFUND, PaymentResolution.IGNORED);
    private static final Set<PaymentResolution> REFUND_IGNORE =
            EnumSet.of(PaymentResolution.REFUND, PaymentResolution.IGNORED);
    private static final Set<PaymentResolution> MARK =
            EnumSet.of(PaymentResolution.MARK_SUCCEEDED, PaymentResolution.MARK_FAILED);

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 25)
    private PaymentExceptionType type;

    @Column(name = "intent_id", updatable = false)
    private UUID intentId;

    @Column(name = "message_id", updatable = false, length = 64)
    private String messageId;

    @Column(name = "source_id", updatable = false)
    private UUID sourceId;

    @Column(name = "file_name", updatable = false, length = MAX_FILE_NAME)
    private String fileName;

    @Column(name = "line_no", updatable = false)
    private Integer lineNo;

    @Column(name = "entity", length = 5)
    private String entity;

    @Column(name = "reference", length = 11)
    private String reference;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "bank_transaction_id", updatable = false, length = MAX_BANK_TRANSACTION_ID)
    private String bankTransactionId;

    @Column(name = "detail", length = MAX_DETAIL)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status = Status.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution", length = 15)
    private PaymentResolution resolution;

    @Column(name = "linked_intent_id")
    private UUID linkedIntentId;

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "note", length = NOTE_MAX)
    private String note;

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
        c.detail = truncate(Objects.requireNonNull(detail, "detail"), MAX_DETAIL);
        if (reference != null) {
            c.entity = reference.getEntity();
            c.reference = reference.getReference();
            c.bankTransactionId = reference.getBankTransactionId();
        }
        return c;
    }

    /**
     * Pagamento lido no ficheiro do banco que a plataforma não confirma sozinha (S5-10 §3).
     *
     * @param intentId intenção da referência encontrada, ou {@code null}
     * @param detail   ex.: {@code INVALID_CHECK_DIGITS}, {@code NOT_ISSUED}; pode ser {@code null}
     */
    public static PaymentExceptionCase fromBankPayment(PaymentExceptionType type, ReferencePaymentReceived m,
                                                       UUID intentId, String detail) {
        if (type == PaymentExceptionType.TARGET_NOT_PAYABLE || type == PaymentExceptionType.UNRESOLVED_STATUS) {
            throw new IllegalArgumentException("Not a bank payment exception type: " + type);
        }
        PaymentExceptionCase c = new PaymentExceptionCase();
        c.insurerId = Objects.requireNonNull(m.insurerId(), "insurerId");
        c.type = type;
        c.intentId = intentId;
        c.messageId = m.messageId();
        c.sourceId = m.sourceId();
        c.fileName = truncate(m.fileName(), MAX_FILE_NAME);
        c.lineNo = m.lineNo();
        c.entity = m.entity();
        c.reference = m.reference();
        c.amount = m.amount().amount();
        c.paidAt = m.paidAt();
        c.bankTransactionId = truncate(m.bankTransactionId(), MAX_BANK_TRANSACTION_ID);
        c.detail = truncate(detail, MAX_DETAIL);
        return c;
    }

    /** Resoluções permitidas para o tipo (S5-10 §6); nenhuma depois de resolvido. */
    public List<PaymentResolution> allowedResolutions() {
        if (status == Status.RESOLVED) {
            return List.of();
        }
        return List.copyOf(allowedFor(type));
    }

    /** Resoluções permitidas para um tipo (S5-10 §6), por ordem. */
    public static Set<PaymentResolution> allowedFor(PaymentExceptionType type) {
        return switch (type) {
            case AMOUNT_MISMATCH, UNKNOWN_REFERENCE, EXPIRED_REFERENCE, CANCELLED_REFERENCE -> LINK_REFUND_IGNORE;
            case ALREADY_PAID, TARGET_NOT_PAYABLE -> REFUND_IGNORE;
            case UNRESOLVED_STATUS -> MARK;
        };
    }

    /**
     * Verifica que o caso pode ser resolvido com {@code resolution}.
     *
     * @throws DomainException 409 {@code INVALID_STATE_TRANSITION} se já estiver resolvido; 409
     *                         {@code CONCURRENT_MODIFICATION} se {@code expectedVersion} não for a gravada; 400
     *                         {@code RESOLUTION_NOT_ALLOWED} se o tipo não a permitir
     */
    public void requireResolvable(PaymentResolution resolution, long expectedVersion) {
        if (status == Status.RESOLVED) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION, "The payment exception is already resolved");
        }
        if (getVersion() != expectedVersion) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The payment exception was changed by another request (version " + getVersion() + ")");
        }
        if (!allowedFor(type).contains(Objects.requireNonNull(resolution, "resolution"))) {
            throw DomainException.of(ErrorCodes.RESOLUTION_NOT_ALLOWED,
                    "Resolution " + resolution + " is not allowed for " + type)
                    .withProperty("allowedResolutions", allowedFor(type).stream().map(Enum::name).toList());
        }
    }

    /**
     * Fecha o caso (S5-10 §6). Não movimenta dinheiro.
     *
     * @param linkedIntentId só {@link PaymentResolution#LINKED}
     */
    public void resolve(PaymentResolution resolution, UUID linkedIntentId, UUID by, Instant at, String note,
                        long expectedVersion) {
        requireResolvable(resolution, expectedVersion);
        if (resolution == PaymentResolution.LINKED) {
            this.linkedIntentId = Objects.requireNonNull(linkedIntentId, "linkedIntentId");
        }
        String n = Objects.requireNonNull(note, "note").strip();
        if (n.length() < NOTE_MIN || n.length() > NOTE_MAX) {
            throw new IllegalArgumentException("note must have " + NOTE_MIN + "-" + NOTE_MAX + " characters");
        }
        this.status = Status.RESOLVED;
        this.resolution = resolution;
        this.resolvedBy = Objects.requireNonNull(by, "by");
        this.resolvedAt = Objects.requireNonNull(at, "at");
        this.note = n;
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
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

    public String getMessageId() {
        return messageId;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getLineNo() {
        return lineNo;
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

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getDetail() {
        return detail;
    }

    public Status getStatus() {
        return status;
    }

    public PaymentResolution getResolution() {
        return resolution;
    }

    public UUID getLinkedIntentId() {
        return linkedIntentId;
    }

    public UUID getResolvedBy() {
        return resolvedBy;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public String getNote() {
        return note;
    }
}
