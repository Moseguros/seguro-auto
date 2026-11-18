package mz.co.moseguros.payment;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.Money;

/**
 * Uma intenção de pagamento (S5-02 §1), no âmbito de quem a lê. É também o corpo das respostas HTTP
 * de intenção (schema {@code IntentView} do contrato) e o que fica gravado na idempotência.
 *
 * @param reference         número do pagamento ({@code PG-…})
 * @param payerMsisdnMasked {@code +258*****4567}; nunca o número completo
 * @param succeededAt       momento da confirmação ({@code paidAt} do {@link PaymentSucceeded}); {@code null} antes
 */
@Schema(name = "IntentView")
public record IntentView(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PG-2027-0001234") String reference,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentMethod method,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentPurpose purpose,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) TargetRef target,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) InsurerRef insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID payerUserId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
        String payerMsisdnMasked,
        FailureCode failureCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
        Instant succeededAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) NextAction nextAction) {

    /** O alvo: tipo, id e o número de negócio ({@code PR-…}). */
    @Schema(name = "PaymentTargetRef")
    public record TargetRef(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentTargetType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123") String label) {
    }

    /** A seguradora que recebe o dinheiro (directamente; a MoSeguros não recebe fundos). */
    @Schema(name = "PaymentInsurerRef")
    public record InsurerRef(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            String displayName) {
    }

    @JsonIgnore
    public UUID insurerId() {
        return insurer.id();
    }

    @JsonIgnore
    public PaymentTargetType targetType() {
        return target.type();
    }

    @JsonIgnore
    public UUID targetId() {
        return target.id();
    }
}
