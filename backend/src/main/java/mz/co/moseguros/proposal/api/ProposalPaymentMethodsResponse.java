package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PaymentMethodOption;
import mz.co.moseguros.proposal.application.ProposalPaymentService;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.shared.domain.Money;

/**
 * Resposta de {@code listProposalPaymentMethods} (S5-02 §7).
 *
 * @param expiresAt    prazo para pagar; {@code null} numa proposta paga
 * @param activeIntent intenção em curso ({@code CREATED}, {@code PENDING} ou {@code UNKNOWN}), para retomar
 */
public record ProposalPaymentMethodsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID proposalId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123") String proposalNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) IntentView.InsurerRef insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean paid,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<PaymentMethodOption> methods,
        IntentView activeIntent) {

    static ProposalPaymentMethodsResponse from(ProposalPaymentService.PaymentMethods m) {
        Proposal p = m.proposal();
        return new ProposalPaymentMethodsResponse(p.getId(), p.getNumber(),
                new IntentView.InsurerRef(p.getInsurerId(), p.getInsurerName()), Money.ofCentavos(p.getTotalAmount()),
                p.getExpiresAt(), m.paid(), m.methods(), m.activeIntent().orElse(null));
    }
}
