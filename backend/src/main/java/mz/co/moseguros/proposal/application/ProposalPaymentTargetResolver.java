package mz.co.moseguros.proposal.application;

import java.util.UUID;

import mz.co.moseguros.payment.PayableTarget;
import mz.co.moseguros.payment.PaymentPurpose;
import mz.co.moseguros.payment.PaymentTargetResolver;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * As propostas como alvo de pagamento (S5-02 §9; modelo transversal §1.1). Só {@code ACCEPTED} é
 * pagável (decisão D4); o prazo para pagar é o {@code expires_at} gravado na aceitação (S4-05).
 */
@Component
public class ProposalPaymentTargetResolver implements PaymentTargetResolver {

    private final ProposalRepository proposals;

    public ProposalPaymentTargetResolver(ProposalRepository proposals) {
        this.proposals = proposals;
    }

    @Override
    public boolean supports(PaymentTargetType targetType) {
        return targetType == PaymentTargetType.PROPOSAL;
    }

    @Override
    public String notFoundCode() {
        return ErrorCodes.PROPOSAL_NOT_FOUND;
    }

    /**
     * A proposta no âmbito de quem chama (cliente: as suas pelo RLS).
     *
     * @throws DomainException 404 {@code PROPOSAL_NOT_FOUND}; 409 {@code PAYMENT_TARGET_NOT_PAYABLE} fora de
     *                         {@code ACCEPTED}
     */
    @Override
    @Transactional(readOnly = true)
    public PayableTarget resolve(PaymentTargetType targetType, UUID targetId) {
        Proposal p = proposals.findById(targetId).orElseThrow(() -> DomainException.of(ErrorCodes.PROPOSAL_NOT_FOUND,
                "Proposal not found"));
        if (p.getStatus() != ProposalStatus.ACCEPTED || p.getExpiresAt() == null) {
            throw DomainException.of(ErrorCodes.PAYMENT_TARGET_NOT_PAYABLE,
                    "The proposal cannot be paid (status " + p.getStatus() + ")");
        }
        return new PayableTarget(p.getInsurerId(), p.getCustomerUserId(), Money.ofCentavos(p.getTotalAmount()),
                p.getNumber(), PaymentPurpose.NEW_POLICY, p.getExpiresAt());
    }
}
