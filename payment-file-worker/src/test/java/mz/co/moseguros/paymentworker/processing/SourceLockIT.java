package mz.co.moseguros.paymentworker.processing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentworker.WorkerITSupport;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import mz.co.moseguros.paymentworker.domain.ProcessedFileRepository;
import mz.co.moseguros.paymentworker.metrics.WorkerMetrics;
import mz.co.moseguros.paymentworker.parser.ParserRegistry;
import mz.co.moseguros.paymentworker.secrets.SecretReader;
import mz.co.moseguros.paymentworker.sftp.SftpSessions;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Duas instâncias do worker (dois {@link SourcePoller} com o mesmo {@link LockProvider}) disparadas ao mesmo
 * tempo para a mesma fonte: o ficheiro é descarregado e publicado uma só vez (S5-09 §4).
 */
class SourceLockIT extends WorkerITSupport {

    @Autowired
    LockProvider lockProvider;

    @Autowired
    ParserRegistry parsers;

    @Autowired
    SecretReader secretReader;

    @Autowired
    SftpSessions sftpSessions;

    @Autowired
    FileProcessor processor;

    @Autowired
    RecordPublisher publisher;

    @Autowired
    ProcessedFileRepository files;

    @Autowired
    WorkerMetrics metrics;

    @Autowired
    WorkerProperties properties;

    SourcePoller instance(WorkerProperties props) {
        return new SourcePoller(parsers, secretReader, sftpSessions, processor, publisher, files,
                new DefaultLockingTaskExecutor(lockProvider), metrics, props, Clock.systemUTC());
    }

    @Test
    void twoInstancesNeverReadTheSameSourceAtTheSameTime() throws Exception {
        // o trinco fica pelo menos 5 s: a segunda instância não o consegue nem se a primeira acabar antes
        WorkerProperties props = new WorkerProperties(properties.backendBaseUrl(), properties.clientId(),
                properties.clientSecret(), properties.sourcesRefreshInterval(), properties.pollInterval(),
                properties.workDir(), properties.maxFileSize(), properties.maxFilesPerRun(),
                properties.lockAtMostFor(), Duration.ofSeconds(5), properties.publishConfirmTimeout(),
                properties.allowUnknownHostKeys());
        SourcePoller a = instance(props);
        SourcePoller b = instance(props);
        String dir = "lock" + UUID.randomUUID().toString().substring(0, 8);
        // sem mudança de pasta: só o trinco impede a segunda leitura de descarregar o ficheiro
        FileSourceDescriptor source = source(dir, false);
        put(dir + "/inbound/a.txt", mepsFile("202611181", null, 100_000, 200_000));

        CountDownLatch start = new CountDownLatch(1);
        CompletableFuture<Optional<PollResult>> first = CompletableFuture.supplyAsync(() -> {
            await(start);
            return a.pollWithLock(source);
        });
        CompletableFuture<Optional<PollResult>> second = CompletableFuture.supplyAsync(() -> {
            await(start);
            return b.pollWithLock(source);
        });
        start.countDown();
        List<Optional<PollResult>> results = List.of(first.get(60, TimeUnit.SECONDS), second.get(60, TimeUnit.SECONDS));

        assertThat(results).filteredOn(Optional::isPresent).hasSize(1);
        assertThat(ofType(drain(), "ReferencePaymentReceived")).hasSize(2);
        assertThat(serverDownloads(dir + "/inbound/a.txt")).isEqualTo(1);
        assertThat(metrics.downloads(source.sourceId())).isEqualTo(1.0);
        assertThat(jdbc.queryForObject("select count(*) from payment_worker.shedlock where name = ?", Integer.class,
                SourcePoller.LOCK_PREFIX + source.sourceId())).isEqualTo(1);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
