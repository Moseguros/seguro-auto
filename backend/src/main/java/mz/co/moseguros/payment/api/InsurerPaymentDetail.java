package mz.co.moseguros.payment.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentPurpose;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.PaymentQueryService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.payment.domain.PaymentEvent;
import mz.co.moseguros.payment.domain.PaymentEventType;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferenceStatus;
import mz.co.moseguros.shared.domain.Money;

/**
 * Detalhe de {@code getInsurerPayment} (S5-02 §7): a linha da lista mais o alvo, o telemóvel mascarado,
 * a referência bancária, o histórico (sem as respostas do fornecedor) e as excepções.
 */
public record InsurerPaymentDetail(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PG-2027-0001234") String reference,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentMethod method,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String targetLabel,
        String providerTxId,
        Instant succeededAt,
        FailureCode failureCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentPurpose purpose,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) IntentView.TargetRef target,
        String payerMsisdnMasked,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant expiresAt,
        ConfirmedBy confirmedBy,
        BankReference bankReference,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<Event> events,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<UUID> exceptionIds) {

    /** Referência bancária da intenção ({@code BANK_REFERENCE}). */
    @Schema(name = "InsurerPaymentBankReference")
    public record BankReference(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "90002") String entity,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "00001230126") String reference,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) ReferenceStatus status,
            Instant paidAt,
            String bankTransactionId) {
    }

    /** Linha do histórico técnico. */
    @Schema(name = "InsurerPaymentEvent")
    public record Event(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentEventType type,
            PaymentStatus fromStatus,
            PaymentStatus toStatus,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant receivedAt) {
    }

    static InsurerPaymentDetail from(PaymentQueryService.InsurerDetail d) {
        PaymentIntent i = d.intent();
        ReferencePayment r = d.reference();
        return new InsurerPaymentDetail(i.getId(), i.getReference(), i.getCreatedAt(), i.getMethod(), i.getStatus(),
                i.amount(), i.getTargetLabel(), i.getProviderTxId(), i.getSucceededAt(), i.getFailureCode(),
                i.getPurpose(), new IntentView.TargetRef(i.getTargetType(), i.getTargetId(), i.getTargetLabel()),
                i.getPayerMsisdn() == null ? null : i.getPayerMsisdn().masked(), i.getExpiresAt(), i.getConfirmedBy(),
                r == null ? null : new BankReference(r.getEntity(), r.getReference(), r.getStatus(), r.getPaidAt(),
                        r.getBankTransactionId()),
                d.events().stream().map(InsurerPaymentDetail::event).toList(), d.exceptionIds());
    }

    private static Event event(PaymentEvent e) {
        return new Event(e.getType(), e.getFromStatus(), e.getToStatus(), e.getReceivedAt());
    }
}
