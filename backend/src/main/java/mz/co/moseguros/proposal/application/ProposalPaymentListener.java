package mz.co.moseguros.proposal.application;

import java.util.Map;

import mz.co.moseguros.payment.PaymentApi;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Pagamento de uma proposta confirmado ({@link PaymentSucceeded} com alvo {@code PROPOSAL}; S5-02 §9),
 * depois do commit, numa transacção nova, em {@code SYSTEM}, com a proposta bloqueada:
 * <ul>
 *   <li>{@code ACCEPTED} → {@code PAID}, auditoria {@code PROPOSAL.PAID}, {@link ProposalPaid} (o
 *       {@code policy} emite a apólice, S6-01) e o aviso aos actores ({@link ProposalPaymentNotifier});</li>
 *   <li>já paga com a mesma intenção → nada (evento repetido);</li>
 *   <li>paga com outra intenção, ou noutro estado → {@code PaymentApi.raiseTargetNotPayable} (excepção para
 *       o financeiro; nunca emite nada sozinha).</li>
 * </ul>
 */
@Component
public class ProposalPaymentListener {

    static final String ACTOR = "ProposalPaymentListener";
    private static final Logger log = LoggerFactory.getLogger(ProposalPaymentListener.class);

    private final ProposalRepository proposals;
    private final PaymentApi payments;
    private final ProposalPaymentNotifier notifier;
    private final AuditService audit;
    private final ApplicationEventPublisher events;

    public ProposalPaymentListener(ProposalRepository proposals, PaymentApi payments, ProposalPaymentNotifier notifier,
                                   AuditService audit, ApplicationEventPublisher events) {
        this.proposals = proposals;
        this.payments = payments;
        this.notifier = notifier;
        this.audit = audit;
        this.events = events;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(PaymentSucceeded event) {
        if (event.targetType() != PaymentTargetType.PROPOSAL) {
            return;
        }
        Proposal proposal = proposals.lock(event.targetId()).orElse(null);
        if (proposal == null) {
            log.warn("Payment for an unknown proposal intentId={} proposalId={}", event.intentId(), event.targetId());
            payments.raiseTargetNotPayable(event.intentId(), "PROPOSAL_NOT_FOUND");
            return;
        }
        ProposalStatus status = proposal.getStatus();
        if (status == ProposalStatus.ACCEPTED) {
            proposal.markPaid(event.intentId(), event.paidAt());
            proposals.saveAndFlush(proposal);
            audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, proposal.getInsurerId(), "PROPOSAL.PAID",
                    "Proposal", proposal.getId().toString(), Map.of("status", status.name()),
                    Map.of("status", proposal.getStatus().name(), "paymentIntentId", event.intentId().toString(),
                            "paidAt", event.paidAt().toString())));
            events.publishEvent(new ProposalPaid(Ids.newId(), event.occurredAt(), proposal.getId(),
                    proposal.getInsurerId(), proposal.getCustomerUserId(), event.intentId(),
                    Money.ofCentavos(proposal.getTotalAmount()), event.paidAt()));
            log.info("Proposal paid proposalId={} intentId={} method={}", proposal.getId(), event.intentId(),
                    event.method());
            notifier.paid(proposal, event);
            return;
        }
        if ((status == ProposalStatus.PAID || status == ProposalStatus.CONVERTED)
                && event.intentId().equals(proposal.getPaymentIntentId())) {
            return; // evento repetido
        }
        String detail = status == ProposalStatus.PAID || status == ProposalStatus.CONVERTED ? "PROPOSAL_ALREADY_PAID"
                : "PROPOSAL_" + status.name();
        log.warn("Payment received for a proposal that cannot accept it proposalId={} intentId={} status={}",
                proposal.getId(), event.intentId(), status);
        payments.raiseTargetNotPayable(event.intentId(), detail);
    }
}
