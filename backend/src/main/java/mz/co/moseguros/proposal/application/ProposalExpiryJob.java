package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.payment.PaymentApi;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.proposal.ProposalExpired;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Expiração das propostas sem acção (S4-02 §13): de hora a hora, ao minuto 5 de Maputo, as propostas
 * {@code DRAFT}, {@code INFO_REQUESTED} ou {@code ACCEPTED} com {@code expires_at <= agora} passam a
 * {@code EXPIRED}, por lotes de {@value #BATCH_SIZE}, cada proposta na sua transacção (com o evento
 * {@link ProposalExpired} e a auditoria {@code PROPOSAL.EXPIRE}).
 *
 * <p>É idempotente: uma proposta já expirada deixa de ter prazo e não volta a ser escolhida. Sem
 * ShedLock até ao S6: uma só instância. Desde o S5-02 não expira propostas {@code ACCEPTED} com um
 * pagamento M-Pesa em curso ({@code PaymentApi.hasBlockingIntent}): tenta de novo no ciclo seguinte, sem
 * mudar {@code expires_at}. As referências bancárias não bloqueiam (a intenção expira com a proposta).
 */
@Component
public class ProposalExpiryJob {

    static final int BATCH_SIZE = 100;
    static final String ACTOR = "ProposalExpiryJob";
    private static final Set<ProposalStatus> EXPIRING = EnumSet.of(ProposalStatus.DRAFT,
            ProposalStatus.INFO_REQUESTED, ProposalStatus.ACCEPTED);
    private static final Logger log = LoggerFactory.getLogger(ProposalExpiryJob.class);

    private final ProposalRepository proposals;
    private final AuditService audit;
    private final ApplicationEventPublisher events;
    private final TransactionTemplate tx;
    private final PaymentApi payments;
    private final Clock clock;

    public ProposalExpiryJob(ProposalRepository proposals, AuditService audit, ApplicationEventPublisher events,
                             PlatformTransactionManager transactionManager, PaymentApi payments, Clock clock) {
        this.proposals = proposals;
        this.audit = audit;
        this.events = events;
        this.tx = new TransactionTemplate(transactionManager);
        this.payments = payments;
        this.clock = clock;
    }

    /** @return quantas propostas expirou */
    @RunAsSystem
    @Scheduled(cron = "0 5 * * * *", zone = "Africa/Maputo")
    public int expire() {
        Instant startedAt = clock.instant();
        log.info("Proposal expiry started at={}", startedAt);
        int expired = 0;
        int failed = 0;
        while (true) {
            List<UUID> batch = tx.execute(status ->
                    proposals.findExpiredIds(EXPIRING, startedAt, PageRequest.of(0, BATCH_SIZE)));
            if (batch == null || batch.isEmpty()) {
                break;
            }
            int expiredInBatch = 0;
            for (UUID id : batch) {
                try {
                    Boolean done = tx.execute(status -> expireOne(id, startedAt));
                    if (Boolean.TRUE.equals(done)) {
                        expired++;
                        expiredInBatch++;
                    }
                } catch (RuntimeException e) {
                    failed++;
                    log.warn("Proposal expiry failed proposalId={} ({})", id, e.getClass().getSimpleName());
                }
            }
            // Sem progresso (todas falharam ou já não estavam vencidas): pára para não repetir o lote.
            if (expiredInBatch == 0 || batch.size() < BATCH_SIZE) {
                break;
            }
        }
        log.info("Proposal expiry finished expired={} failed={} durationMs={}", expired, failed,
                Duration.between(startedAt, clock.instant()).toMillis());
        return expired;
    }

    private boolean expireOne(UUID id, Instant cutoff) {
        Proposal proposal = proposals.findById(id).orElse(null);
        if (proposal == null || !EXPIRING.contains(proposal.getStatus()) || proposal.getExpiresAt() == null
                || proposal.getExpiresAt().isAfter(cutoff)) {
            return false;
        }
        if (proposal.getStatus() == ProposalStatus.ACCEPTED
                && payments.hasBlockingIntent(PaymentTargetType.PROPOSAL, proposal.getId())) {
            log.info("Proposal expiry postponed: mobile payment in progress proposalId={}", proposal.getId());
            return false; // o dinheiro pode estar a caminho: tenta no ciclo seguinte
        }
        Instant now = clock.instant();
        Instant expiresAt = proposal.getExpiresAt();
        ProposalStatus from = proposal.expire(now);
        proposals.saveAndFlush(proposal);
        events.publishEvent(new ProposalExpired(Ids.newId(), now, proposal.getId(), proposal.getInsurerId(),
                proposal.getCustomerUserId(), proposal.getBrokerOrgId(), from));
        audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, proposal.getInsurerId(), "PROPOSAL.EXPIRE", "Proposal",
                proposal.getId().toString(), Map.of("status", from.name(), "expiresAt", expiresAt.toString()),
                Map.of("status", proposal.getStatus().name())));
        return true;
    }
}
