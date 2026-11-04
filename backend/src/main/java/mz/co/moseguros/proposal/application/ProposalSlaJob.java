package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.StaffContact;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Alerta de SLA (S4-05 §10): de hora a hora, ao minuto 10 de Maputo, cada proposta
 * {@code UNDER_REVIEW} com {@code sla_alert_at <= agora} e sem alerta enviado recebe um e-mail
 * {@code proposal.sla-alert} para cada subscritor da seguradora ({@code proposal:decide}) e fica com
 * {@code sla_alerted_at}. Sem destinatários grava na mesma e regista {@code WARN} com o id da
 * seguradora.
 *
 * <p><strong>Não muda o estado</strong>: depois de {@code sla_due_at} a proposta continua em análise
 * ({@code slaStatus = OVERDUE}); não há aceitação tácita. Idempotente: uma proposta alertada não volta a
 * ser escolhida. Cada proposta na sua transacção; os e-mails saem depois do commit. Sem ShedLock até ao
 * S6: uma só instância.
 */
@Component
public class ProposalSlaJob {

    static final int BATCH_SIZE = 100;
    static final String ACTOR = "ProposalSlaJob";
    static final String PERMISSION = "proposal:decide";
    private static final Logger log = LoggerFactory.getLogger(ProposalSlaJob.class);

    private final ProposalRepository proposals;
    private final IdentityApi identity;
    private final ProposalNotifier notifier;
    private final AuditService audit;
    private final TransactionTemplate tx;
    private final Clock clock;

    public ProposalSlaJob(ProposalRepository proposals, IdentityApi identity, ProposalNotifier notifier,
                          AuditService audit, PlatformTransactionManager transactionManager, Clock clock) {
        this.proposals = proposals;
        this.identity = identity;
        this.notifier = notifier;
        this.audit = audit;
        this.tx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /** Proposta alertada e os destinatários. */
    private record Alert(Proposal proposal, List<StaffContact> recipients, Instant reviewStartedAt, Instant dueAt) {
    }

    /** @return quantas propostas alertou */
    @RunAsSystem
    @Scheduled(cron = "0 10 * * * *", zone = "Africa/Maputo")
    public int alert() {
        Instant startedAt = clock.instant();
        int alerted = 0;
        int failed = 0;
        while (true) {
            List<UUID> batch = tx.execute(status ->
                    proposals.findSlaAlertDueIds(startedAt, PageRequest.of(0, BATCH_SIZE)));
            if (batch == null || batch.isEmpty()) {
                break;
            }
            int alertedInBatch = 0;
            for (UUID id : batch) {
                try {
                    Alert alert = tx.execute(status -> alertOne(id, startedAt));
                    if (alert != null) {
                        alerted++;
                        alertedInBatch++;
                        for (StaffContact staff : alert.recipients()) {
                            notifier.slaAlert(alert.proposal(), staff, alert.reviewStartedAt(), alert.dueAt());
                        }
                    }
                } catch (RuntimeException e) {
                    failed++;
                    log.warn("Proposal SLA alert failed proposalId={} ({})", id, e.getClass().getSimpleName());
                }
            }
            // Sem progresso (todas falharam ou já estavam alertadas): pára para não repetir o lote.
            if (alertedInBatch == 0 || batch.size() < BATCH_SIZE) {
                break;
            }
        }
        if (alerted > 0 || failed > 0) {
            log.info("Proposal SLA alerts finished alerted={} failed={}", alerted, failed);
        }
        return alerted;
    }

    private Alert alertOne(UUID id, Instant cutoff) {
        Proposal proposal = proposals.findById(id).orElse(null);
        if (proposal == null || proposal.getStatus() != ProposalStatus.UNDER_REVIEW
                || proposal.getSlaAlertAt() == null || proposal.getSlaAlertAt().isAfter(cutoff)
                || proposal.getSlaAlertedAt() != null) {
            return null;
        }
        List<StaffContact> recipients = identity.staffWithPermission(proposal.getInsurerId(), PERMISSION);
        if (recipients.isEmpty()) {
            log.warn("Proposal {}: no staff with {} at insurer {} to alert", proposal.getId(), PERMISSION,
                    proposal.getInsurerId());
        }
        Instant now = clock.instant();
        proposal.markSlaAlerted(now);
        proposals.saveAndFlush(proposal);
        audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, proposal.getInsurerId(), "PROPOSAL.SLA_ALERT",
                "Proposal", proposal.getId().toString(), Map.of("slaAlertAt", proposal.getSlaAlertAt().toString()),
                Map.of("slaAlertedAt", now.toString(), "recipients", recipients.size())));
        return new Alert(proposal, recipients, proposal.getReviewStartedAt(), proposal.getSlaDueAt());
    }
}
