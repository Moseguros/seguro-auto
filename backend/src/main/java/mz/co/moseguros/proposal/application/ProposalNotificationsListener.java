package mz.co.moseguros.proposal.application;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.proposal.ProposalAccepted;
import mz.co.moseguros.proposal.ProposalExpired;
import mz.co.moseguros.proposal.ProposalInfoRequested;
import mz.co.moseguros.proposal.ProposalRejected;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Avisos ao cliente das decisões sobre a proposta (S6-04 §9; modelo transversal §1.1): cada evento → um
 * {@code NotificationApi.notify} para o {@code customerUserId} do evento, com o número e o nome congelado da
 * seguradora lidos de {@code proposal.proposal} pelo id do evento. Depois do commit, numa transacção nova, em
 * {@code SYSTEM}. Substitui os e-mails {@code proposal.accepted}/{@code rejected}/{@code info-requested} do
 * S4-05 ao cliente.
 */
@Component
public class ProposalNotificationsListener {

    static final String ACCEPTED = "PROPOSAL_ACCEPTED";
    static final String REJECTED = "PROPOSAL_REJECTED";
    static final String INFO_REQUESTED = "PROPOSAL_INFO_REQUESTED";
    static final String EXPIRED = "PROPOSAL_EXPIRED";

    private static final Logger log = LoggerFactory.getLogger(ProposalNotificationsListener.class);

    private final ProposalRepository proposals;
    private final NotificationApi notifications;

    public ProposalNotificationsListener(ProposalRepository proposals, NotificationApi notifications) {
        this.proposals = proposals;
        this.notifications = notifications;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(ProposalAccepted event) {
        notify(event.proposalId(), event.customerUserId(), ACCEPTED, true);
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(ProposalRejected event) {
        notify(event.proposalId(), event.customerUserId(), REJECTED, false);
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(ProposalInfoRequested event) {
        notify(event.proposalId(), event.customerUserId(), INFO_REQUESTED, false);
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(ProposalExpired event) {
        notify(event.proposalId(), event.customerUserId(), EXPIRED, false);
    }

    private void notify(UUID proposalId, UUID customerUserId, String templateCode, boolean withAmount) {
        Proposal proposal = proposals.findById(proposalId).orElse(null);
        if (proposal == null || !proposal.getCustomerUserId().equals(customerUserId)) {
            log.warn("Proposal {} not found for the {} notification", proposalId, templateCode);
            return;
        }
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("proposalNumber", proposal.getNumber());
        if (!EXPIRED.equals(templateCode)) {
            params.put("insurerName", proposal.getInsurerName());
        }
        if (withAmount) {
            params.put("amount", Money.ofCentavos(proposal.getTotalAmount()));
        }
        params.put("webPath", "/cliente/propostas/" + proposalId);
        params.put("deepLink", "moseguros://proposal/" + proposalId);
        notifications.notify(customerUserId, templateCode, params, NotificationCategory.PROPOSAL);
    }
}
