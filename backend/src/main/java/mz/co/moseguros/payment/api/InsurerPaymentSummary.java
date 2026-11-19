package mz.co.moseguros.payment.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.shared.domain.Money;

/** Linha de {@code listInsurerPayments} (S5-02 §7). */
public record InsurerPaymentSummary(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PG-2027-0001234") String reference,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentMethod method,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123") String targetLabel,
        String providerTxId,
        Instant succeededAt,
        FailureCode failureCode) {

    static InsurerPaymentSummary from(PaymentIntent i) {
        return new InsurerPaymentSummary(i.getId(), i.getReference(), i.getCreatedAt(), i.getMethod(), i.getStatus(),
                i.amount(), i.getTargetLabel(), i.getProviderTxId(), i.getSucceededAt(), i.getFailureCode());
    }
}
