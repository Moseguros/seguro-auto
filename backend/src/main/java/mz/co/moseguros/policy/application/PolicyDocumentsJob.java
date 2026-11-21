package mz.co.moseguros.policy.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.policy.domain.PolicyRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Gera os PDF que faltam (S6-02 §1): a cada minuto ({@code Africa/Maputo}, lock {@value #LOCK}) procura até
 * {@value #BATCH} apólices com certificado {@code VALID} sem PDF ou sem as condições particulares, emitidas
 * há mais de {@value #GRACE_MINUTES} minutos (o {@link PolicyDocumentsListener} trata das recentes), as mais
 * antigas primeiro, e chama {@link PolicyDocumentService#generate} para cada uma. Uma falha não pára as
 * outras: log {@code ERROR} com o {@code policyId} e o contador {@value #FAILURES} (Prometheus
 * {@code moseguros_policy_documents_failures_total}). Serve também para os certificados novos do S7.
 */
@Component
public class PolicyDocumentsJob {

    public static final String LOCK = "policy.documents";
    public static final String FAILURES = "moseguros.policy.documents.failures";
    static final int BATCH = 50;
    static final long GRACE_MINUTES = 2;
    private static final Logger log = LoggerFactory.getLogger(PolicyDocumentsJob.class);

    private final PolicyRepository policies;
    private final PolicyDocumentService documents;
    private final ObjectProvider<MeterRegistry> meters;
    private final TransactionTemplate readOnlyTx;
    private final Clock clock;

    public PolicyDocumentsJob(PolicyRepository policies, PolicyDocumentService documents,
                              ObjectProvider<MeterRegistry> meters, PlatformTransactionManager transactionManager,
                              Clock clock) {
        this.policies = policies;
        this.documents = documents;
        this.meters = meters;
        this.readOnlyTx = new TransactionTemplate(transactionManager);
        this.readOnlyTx.setReadOnly(true);
        this.clock = clock;
    }

    /** @return o número de apólices com documentos gerados nesta execução */
    @Scheduled(cron = "0 * * * * *", zone = "Africa/Maputo")
    @SchedulerLock(name = LOCK, lockAtMostFor = "PT5M")
    @RunAsSystem
    public int run() {
        Instant startedAt = clock.instant();
        Instant issuedBefore = startedAt.minus(Duration.ofMinutes(GRACE_MINUTES));
        List<UUID> candidates = readOnlyTx.execute(
                status -> policies.findMissingDocuments(issuedBefore, PageRequest.of(0, BATCH)));
        if (candidates == null || candidates.isEmpty()) {
            return 0;
        }
        log.info("Job started job={} candidates={}", LOCK, candidates.size());
        int processed = 0;
        int failed = 0;
        for (UUID policyId : candidates) {
            try {
                if (documents.generate(policyId) == PolicyDocumentService.Outcome.GENERATED) {
                    processed++;
                }
            } catch (RuntimeException e) {
                failed++;
                failure();
                log.error("Job job={} failed for policyId={} ({})", LOCK, policyId, e.getClass().getSimpleName(), e);
            }
        }
        log.info("Job finished job={} processed={} failed={} durationMs={}", LOCK, processed, failed,
                Duration.between(startedAt, clock.instant()).toMillis());
        return processed;
    }

    private void failure() {
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            Counter.builder(FAILURES).register(registry).increment();
        }
    }
}
