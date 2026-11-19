package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.InitiatedVia;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentPurpose;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import mz.co.moseguros.shared.persistence.converters.MsisdnConverter;

/**
 * Intenção de pagamento ({@code payment.payment_intent}, S5-02 §2 e §3). O dinheiro vai directamente
 * para a conta da seguradora; a intenção só regista o pedido ao fornecedor e a confirmação.
 *
 * <p>As transições estão aqui e seguem a tabela do §3; outra → 409 {@code INVALID_STATE_TRANSITION}.
 * Quem muda o estado é sempre o {@code IntentStateService} (bloqueio, histórico, auditoria e eventos).
 */
@Entity
@Table(schema = "payment", name = "payment_intent")
public class PaymentIntent extends BaseEntity {

    public static final int MAX_TARGET_LABEL = 40;

    private static final Set<PaymentStatus> SUCCEEDS_FROM = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.UNKNOWN,
            PaymentStatus.EXPIRED, PaymentStatus.CANCELLED);
    private static final Set<PaymentStatus> REFERENCE_OPEN = EnumSet.of(PaymentStatus.CREATED, PaymentStatus.PENDING);

    @Column(name = "reference", nullable = false, updatable = false, length = 16)
    private String reference;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "payer_user_id", nullable = false, updatable = false)
    private UUID payerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, updatable = false, length = 20)
    private PaymentPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, updatable = false, length = 20)
    private PaymentTargetType targetType;

    @Column(name = "target_id", nullable = false, updatable = false)
    private UUID targetId;

    @Column(name = "target_label", nullable = false, updatable = false, length = MAX_TARGET_LABEL)
    private String targetLabel;

    @Column(name = "amount", nullable = false, updatable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, updatable = false, length = 20)
    private PaymentMethod method;

    @Column(name = "method_config_id", nullable = false, updatable = false)
    private UUID methodConfigId;

    @Convert(converter = MsisdnConverter.class)
    @Column(name = "payer_msisdn", updatable = false, length = 12)
    private Msisdn payerMsisdn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_code", length = 40)
    private FailureCode failureCode;

    @Column(name = "provider_tx_id", length = 100)
    private String providerTxId;

    @Column(name = "provider_conversation_id", length = 100)
    private String providerConversationId;

    @Column(name = "third_party_reference", length = 40)
    private String thirdPartyReference;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private UUID idempotencyKey;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "initiated_by_user_id", nullable = false, updatable = false)
    private UUID initiatedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "initiated_via", nullable = false, updatable = false, length = 12)
    private InitiatedVia initiatedVia;

    @Column(name = "dispatched_at")
    private Instant dispatchedAt;

    @Column(name = "unknown_since")
    private Instant unknownSince;

    @Column(name = "next_query_at")
    private Instant nextQueryAt;

    @Column(name = "query_attempts", nullable = false)
    private int queryAttempts;

    @Column(name = "succeeded_at")
    private Instant succeededAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmed_by", length = 20)
    private ConfirmedBy confirmedBy;

    protected PaymentIntent() {
    }

    /** Dados de uma intenção nova ({@code CREATED}). */
    public record New(String reference, UUID insurerId, UUID payerUserId, PaymentPurpose purpose,
                      PaymentTargetType targetType, UUID targetId, String targetLabel, Money amount,
                      PaymentMethod method, UUID methodConfigId, Msisdn payerMsisdn, UUID idempotencyKey,
                      Instant expiresAt, UUID initiatedByUserId, InitiatedVia initiatedVia) {
    }

    public static PaymentIntent create(New n) {
        PaymentIntent p = new PaymentIntent();
        p.reference = Objects.requireNonNull(n.reference(), "reference");
        p.insurerId = Objects.requireNonNull(n.insurerId(), "insurerId");
        p.payerUserId = Objects.requireNonNull(n.payerUserId(), "payerUserId");
        p.purpose = Objects.requireNonNull(n.purpose(), "purpose");
        p.targetType = Objects.requireNonNull(n.targetType(), "targetType");
        p.targetId = Objects.requireNonNull(n.targetId(), "targetId");
        String label = Objects.requireNonNull(n.targetLabel(), "targetLabel");
        p.targetLabel = label.length() > MAX_TARGET_LABEL ? label.substring(0, MAX_TARGET_LABEL) : label;
        if (n.amount() == null || n.amount().amount() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        p.amount = n.amount().amount();
        p.method = Objects.requireNonNull(n.method(), "method");
        p.methodConfigId = Objects.requireNonNull(n.methodConfigId(), "methodConfigId");
        if (n.method() == PaymentMethod.MPESA && n.payerMsisdn() == null) {
            throw new IllegalArgumentException("payerMsisdn is required for MPESA");
        }
        p.payerMsisdn = n.method() == PaymentMethod.MPESA ? n.payerMsisdn() : null;
        p.idempotencyKey = Objects.requireNonNull(n.idempotencyKey(), "idempotencyKey");
        p.expiresAt = Objects.requireNonNull(n.expiresAt(), "expiresAt");
        p.initiatedByUserId = Objects.requireNonNull(n.initiatedByUserId(), "initiatedByUserId");
        p.initiatedVia = Objects.requireNonNull(n.initiatedVia(), "initiatedVia");
        p.status = PaymentStatus.CREATED;
        return p;
    }

    // --- Transições (S5-02 §3) ---

    /**
     * {@code CREATED → PENDING}: envio aceite (referência gerada) ou envio assíncrono começado (M-Pesa;
     * {@code dispatched_at}).
     */
    public void markPending(Instant now, String providerTxId, String providerConversationId,
                            String thirdPartyReference) {
        require(status == PaymentStatus.CREATED, PaymentStatus.PENDING);
        this.status = PaymentStatus.PENDING;
        if (method == PaymentMethod.MPESA && dispatchedAt == null) {
            this.dispatchedAt = Objects.requireNonNull(now, "now");
        }
        provider(providerTxId, providerConversationId, thirdPartyReference);
    }

    /**
     * Confirmação do pagamento (fornecedor, consulta, ficheiro do banco ou resolução manual), a partir de
     * {@code PENDING}, {@code UNKNOWN}, {@code EXPIRED} ou {@code CANCELLED}: o dinheiro foi cobrado,
     * confirma-se sempre (o dono do alvo decide se ainda o aceita).
     */
    public void markSucceeded(Instant paidAt, ConfirmedBy by, String providerTxId) {
        require(SUCCEEDS_FROM.contains(status), PaymentStatus.SUCCEEDED);
        Objects.requireNonNull(by, "by");
        this.status = PaymentStatus.SUCCEEDED;
        this.succeededAt = Objects.requireNonNull(paidAt, "paidAt");
        this.confirmedBy = by;
        this.failureCode = null;
        this.unknownSince = null;
        this.nextQueryAt = null;
        if (providerTxId != null) {
            this.providerTxId = providerTxId;
        }
    }

    /** Recusa do fornecedor ({@code PENDING}) ou consulta com resultado final ({@code UNKNOWN}). */
    public void markFailed(FailureCode code, Instant now) {
        require(status == PaymentStatus.PENDING || status == PaymentStatus.UNKNOWN, PaymentStatus.FAILED);
        this.status = PaymentStatus.FAILED;
        this.failureCode = Objects.requireNonNull(code, "code");
        this.failedAt = Objects.requireNonNull(now, "now");
        this.nextQueryAt = null;
    }

    /** Timeout, erro de rede ou resposta ambígua ({@code PENDING → UNKNOWN}, S5-04). */
    public void markUnknown(Instant now, Instant nextQueryAt) {
        require(status == PaymentStatus.PENDING, PaymentStatus.UNKNOWN);
        this.status = PaymentStatus.UNKNOWN;
        this.unknownSince = Objects.requireNonNull(now, "now");
        this.nextQueryAt = Objects.requireNonNull(nextQueryAt, "nextQueryAt");
    }

    /**
     * Consulta de estado sem resposta final (S5-04 §4): conta a tentativa e marca a seguinte
     * ({@code null} = deixa de consultar, {@code UNRESOLVED_STATUS}).
     */
    public void recordStatusQuery(Instant next) {
        if (status != PaymentStatus.UNKNOWN) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "Only an UNKNOWN payment intent is queried (status " + status + ")");
        }
        this.queryAttempts++;
        this.nextQueryAt = next;
    }

    /** Conta uma consulta que terminou com resultado final (a transição vem a seguir). */
    public void countStatusQuery() {
        this.queryAttempts++;
    }

    /**
     * {@code expires_at} passou: referência em {@code CREATED}/{@code PENDING}, ou M-Pesa em
     * {@code CREATED} (nunca enviada).
     */
    public void expire(Instant now) {
        boolean allowed = method == PaymentMethod.BANK_REFERENCE ? REFERENCE_OPEN.contains(status)
                : status == PaymentStatus.CREATED;
        require(allowed, PaymentStatus.EXPIRED);
        this.status = PaymentStatus.EXPIRED;
        this.failureCode = FailureCode.EXPIRED;
        this.failedAt = Objects.requireNonNull(now, "now");
    }

    /**
     * Referência substituída ({@code SUPERSEDED}): outra intenção do alvo foi paga, ou o montante do
     * alvo mudou. Só {@code BANK_REFERENCE} em {@code CREATED}/{@code PENDING} (as de M-Pesa podem estar
     * a cobrar).
     */
    public void cancel(FailureCode code, Instant now) {
        require(method == PaymentMethod.BANK_REFERENCE && REFERENCE_OPEN.contains(status), PaymentStatus.CANCELLED);
        this.status = PaymentStatus.CANCELLED;
        this.failureCode = Objects.requireNonNull(code, "code");
        this.failedAt = Objects.requireNonNull(now, "now");
    }

    /** Reembolso ({@code SUCCEEDED → REFUNDED}; S7, fora da plataforma). */
    public void markRefunded() {
        require(status == PaymentStatus.SUCCEEDED, PaymentStatus.REFUNDED);
        this.status = PaymentStatus.REFUNDED;
    }

    /** Ids devolvidos pelo fornecedor sem mudança de estado (ex.: resposta do envio já em {@code PENDING}). */
    public void recordProvider(String txId, String conversationId, String thirdParty) {
        provider(txId, conversationId, thirdParty);
    }

    private void provider(String txId, String conversationId, String thirdParty) {
        if (txId != null) {
            this.providerTxId = txId;
        }
        if (conversationId != null) {
            this.providerConversationId = conversationId;
        }
        if (thirdParty != null) {
            this.thirdPartyReference = thirdParty;
        }
    }

    private void require(boolean allowed, PaymentStatus target) {
        if (!allowed) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "Cannot change a " + method + " payment intent from " + status + " to " + target);
        }
    }

    public Money amount() {
        return Money.ofCentavos(amount);
    }

    public boolean isMpesa() {
        return method == PaymentMethod.MPESA;
    }

    // --- Leitura ---

    public String getReference() {
        return reference;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getPayerUserId() {
        return payerUserId;
    }

    public PaymentPurpose getPurpose() {
        return purpose;
    }

    public PaymentTargetType getTargetType() {
        return targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public long getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public UUID getMethodConfigId() {
        return methodConfigId;
    }

    public Msisdn getPayerMsisdn() {
        return payerMsisdn;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public FailureCode getFailureCode() {
        return failureCode;
    }

    public String getProviderTxId() {
        return providerTxId;
    }

    public String getProviderConversationId() {
        return providerConversationId;
    }

    public String getThirdPartyReference() {
        return thirdPartyReference;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UUID getInitiatedByUserId() {
        return initiatedByUserId;
    }

    public InitiatedVia getInitiatedVia() {
        return initiatedVia;
    }

    public Instant getDispatchedAt() {
        return dispatchedAt;
    }

    public Instant getUnknownSince() {
        return unknownSince;
    }

    public Instant getNextQueryAt() {
        return nextQueryAt;
    }

    public int getQueryAttempts() {
        return queryAttempts;
    }

    public Instant getSucceededAt() {
        return succeededAt;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public ConfirmedBy getConfirmedBy() {
        return confirmedBy;
    }
}
