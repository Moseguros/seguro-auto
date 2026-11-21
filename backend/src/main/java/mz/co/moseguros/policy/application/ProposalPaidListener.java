package mz.co.moseguros.policy.application;

import java.time.Clock;
import java.time.Duration;

import mz.co.moseguros.policy.application.PolicyIssuanceService.Outcome;
import mz.co.moseguros.policy.application.PolicyIssuanceService.Result;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Proposta paga ({@link ProposalPaid}, S5-02 §9) → emissão automática da apólice (S6-01 §5; decisão
 * D36), depois do commit, numa transacção nova, em {@code SYSTEM}. Filtra sempre pelos ids do evento.
 *
 * <p>Uma segunda entrega em simultâneo falha no índice {@code ux_policy_proposal} dentro da transacção
 * própria do {@link PolicyIssuanceService} ({@code REQUIRES_NEW}): é apanhada aqui ("já emitida") e o
 * evento fica concluído. Qualquer outra excepção propaga-se: a publicação fica incompleta e o
 * {@code IncompletePublicationsJob} reenvia-a.
 */
@Component
public class ProposalPaidListener {

    static final String UNIQUE_PROPOSAL_INDEX = "ux_policy_proposal";
    private static final Logger log = LoggerFactory.getLogger(ProposalPaidListener.class);

    private final PolicyIssuanceService issuance;
    private final PolicyMetrics metrics;
    private final Clock clock;

    public ProposalPaidListener(PolicyIssuanceService issuance, PolicyMetrics metrics, Clock clock) {
        this.issuance = issuance;
        this.metrics = metrics;
        this.clock = clock;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(ProposalPaid event) {
        Result result;
        try {
            result = issuance.issue(event.proposalId(), event.paymentIntentId());
        } catch (DataIntegrityViolationException e) {
            if (!isDuplicateProposal(e)) {
                throw e;
            }
            log.info("Policy already issued (concurrent delivery) proposalId={}", event.proposalId());
            return;
        }
        if (result.outcome() == Outcome.ISSUED) {
            metrics.issued(result.productType(), Duration.between(event.occurredAt(), clock.instant()));
        }
    }

    /** A violação é a do índice {@code ux_policy_proposal} (uma apólice por proposta). */
    static boolean isDuplicateProposal(DataIntegrityViolationException e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ConstraintViolationException c && c.getConstraintName() != null) {
                return c.getConstraintName().contains(UNIQUE_PROPOSAL_INDEX);
            }
            if (t.getCause() == t) {
                break;
            }
        }
        String message = e.getMostSpecificCause() == null ? null : e.getMostSpecificCause().getMessage();
        return message != null && message.contains(UNIQUE_PROPOSAL_INDEX);
    }
}
