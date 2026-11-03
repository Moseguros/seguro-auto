package mz.co.moseguros.document.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.document.domain.StoredFile;
import mz.co.moseguros.document.domain.StoredFileRepository;
import mz.co.moseguros.document.infrastructure.storage.FileStorage;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Limpa os ficheiros nunca ligados a um dono ({@code linked_at} nulo) criados há mais de 30 dias:
 * apaga o conteúdo do armazenamento e marca {@code purged_at} (a linha fica). Todos os dias às
 * 03:30 de Maputo, em lotes de {@value #BATCH_SIZE}, cada lote na sua transacção.
 *
 * <p>Uma falha num ficheiro não pára o lote: fica para a execução seguinte. Até ao ShedLock (S6)
 * corre numa só instância.
 */
@Component
public class OrphanFileCleanupJob {

    static final int BATCH_SIZE = 500;
    static final Duration MAX_ORPHAN_AGE = Duration.ofDays(30);
    private static final UUID MIN_UUID = new UUID(0L, 0L);
    private static final Logger log = LoggerFactory.getLogger(OrphanFileCleanupJob.class);

    private final StoredFileRepository files;
    private final FileStorage storage;
    private final TransactionTemplate tx;
    private final Clock clock;

    public OrphanFileCleanupJob(StoredFileRepository files, FileStorage storage,
                                PlatformTransactionManager transactionManager, Clock clock) {
        this.files = files;
        this.storage = storage;
        this.tx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    @RunAsSystem
    @Scheduled(cron = "0 30 3 * * *", zone = "Africa/Maputo")
    public Result purgeOrphans() {
        Instant startedAt = clock.instant();
        Instant cutoff = startedAt.minus(MAX_ORPHAN_AGE);
        log.info("Orphan file cleanup started cutoff={}", cutoff);
        int purged = 0;
        int failed = 0;
        Instant afterCreatedAt = Instant.EPOCH;
        UUID afterId = MIN_UUID;
        while (true) {
            Instant fromCreatedAt = afterCreatedAt;
            UUID fromId = afterId;
            BatchResult batch = tx.execute(status -> purgeBatch(cutoff, fromCreatedAt, fromId));
            if (batch == null || batch.size() == 0) {
                break;
            }
            purged += batch.purged();
            failed += batch.failed();
            if (batch.size() < BATCH_SIZE) {
                break;
            }
            afterCreatedAt = batch.lastCreatedAt();
            afterId = batch.lastId();
        }
        log.info("Orphan file cleanup finished purged={} failed={} durationMs={}", purged, failed,
                Duration.between(startedAt, clock.instant()).toMillis());
        return new Result(purged, failed);
    }

    private BatchResult purgeBatch(Instant cutoff, Instant afterCreatedAt, UUID afterId) {
        List<StoredFile> batch = files.findOrphansAfter(cutoff, afterCreatedAt, afterId, PageRequest.of(0, BATCH_SIZE));
        int purged = 0;
        int failed = 0;
        Instant now = clock.instant();
        for (StoredFile file : batch) {
            try {
                storage.delete(file.getStorageKey());
                file.markPurged(now);
                purged++;
            } catch (RuntimeException e) {
                failed++;
                log.warn("Orphan file cleanup failed fileId={} ({})", file.getId(), e.getClass().getSimpleName());
            }
        }
        StoredFile last = batch.isEmpty() ? null : batch.get(batch.size() - 1);
        return new BatchResult(batch.size(), purged, failed,
                last == null ? null : last.getCreatedAt(), last == null ? null : last.getId());
    }

    /** Contagens de uma execução. */
    public record Result(int purged, int failed) {
    }

    private record BatchResult(int size, int purged, int failed, Instant lastCreatedAt, UUID lastId) {
    }
}
