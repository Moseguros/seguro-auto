package mz.co.moseguros.paymentworker.processing;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.PollErrorCode;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import mz.co.moseguros.paymentworker.domain.ProcessedFile;
import mz.co.moseguros.paymentworker.domain.ProcessedFileRepository;
import mz.co.moseguros.paymentworker.metrics.WorkerMetrics;
import mz.co.moseguros.paymentworker.parser.ParserRegistry;
import mz.co.moseguros.paymentworker.parser.PaymentFileParser;
import mz.co.moseguros.paymentworker.processing.RecordPublisher.PublishException;
import mz.co.moseguros.paymentworker.secrets.SecretReader;
import mz.co.moseguros.paymentworker.sftp.SftpSessions;
import mz.co.moseguros.paymentworker.sftp.SftpSessions.Connection;
import mz.co.moseguros.paymentworker.sftp.SftpSessions.Credentials;
import mz.co.moseguros.paymentworker.sftp.SftpSessions.RemoteFile;
import mz.co.moseguros.paymentworker.sftp.SftpSessions.SftpException;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Uma leitura de uma fonte (S5-09 §5): leitor do formato, segredos do cofre, ligação SFTP, listagem de
 * {@code inboundDir}, cada ficheiro pelo {@link FileProcessor}, limpeza de {@code work-dir} e o resumo
 * {@code PaymentSourcePolled}. {@link #pollWithLock} corre-a com o trinco {@code source:{sourceId}}:
 * duas instâncias nunca leem a mesma fonte ao mesmo tempo.
 */
@Component
public class SourcePoller {

    public static final String LOCK_PREFIX = "source:";
    static final String SECRET_PASSWORD = "sftp.password";
    static final String SECRET_PRIVATE_KEY = "sftp.privateKey";
    static final String SECRET_PASSPHRASE = "sftp.passphrase";

    private static final Logger log = LoggerFactory.getLogger(SourcePoller.class);

    private final ParserRegistry parsers;
    private final SecretReader secrets;
    private final SftpSessions sftpSessions;
    private final FileProcessor processor;
    private final RecordPublisher publisher;
    private final ProcessedFileRepository files;
    private final LockingTaskExecutor locks;
    private final WorkerMetrics metrics;
    private final WorkerProperties properties;
    private final Clock clock;

    public SourcePoller(ParserRegistry parsers, SecretReader secrets, SftpSessions sftpSessions,
                        FileProcessor processor, RecordPublisher publisher, ProcessedFileRepository files,
                        LockingTaskExecutor locks, WorkerMetrics metrics, WorkerProperties properties, Clock clock) {
        this.parsers = parsers;
        this.secrets = secrets;
        this.sftpSessions = sftpSessions;
        this.processor = processor;
        this.publisher = publisher;
        this.files = files;
        this.locks = locks;
        this.metrics = metrics;
        this.properties = properties;
        this.clock = clock;
    }

    /** Lê a fonte se conseguir o trinco; sem ele não faz nada (vazio). */
    public Optional<PollResult> pollWithLock(FileSourceDescriptor source) {
        LockConfiguration lock = new LockConfiguration(clock.instant(), LOCK_PREFIX + source.sourceId(),
                properties.lockAtMostFor(), properties.lockAtLeastFor());
        try {
            LockingTaskExecutor.TaskResult<PollResult> result = locks.executeWithLock(() -> poll(source), lock);
            if (!result.wasExecuted()) {
                log.debug("Source {} is being read elsewhere (lock {} taken)", source.sourceId(), lock.getName());
                return Optional.empty();
            }
            return Optional.ofNullable(result.getResult());
        } catch (Throwable e) {
            log.error("Poll of source {} failed: {}", source.sourceId(), e.getClass().getSimpleName(), e);
            return Optional.empty();
        }
    }

    /** Uma leitura completa da fonte (sem trinco: usar {@link #pollWithLock}). */
    public PollResult poll(FileSourceDescriptor source) {
        Instant startedAt = now();
        Counts counts = new Counts();
        PollErrorCode error = null;
        Path workDir = properties.workDir().resolve(source.sourceId().toString());
        Optional<PaymentFileParser> parser = parsers.find(source.fileFormat());
        try {
            if (parser.isEmpty()) {
                log.warn("Source {} (insurer {}) has the file format {} without a parser", source.sourceId(),
                        source.insurerId(), source.fileFormat());
                error = PollErrorCode.UNSUPPORTED_FORMAT;
            } else {
                error = read(source, parser.get(), workDir, counts);
            }
        } catch (RuntimeException e) {
            log.error("Poll of source {} failed: {}", source.sourceId(), e.getClass().getSimpleName(), e);
            error = PollErrorCode.INTERNAL_ERROR;
        } finally {
            clean(workDir);
        }
        if (error == null && counts.publishFailed) {
            error = PollErrorCode.PUBLISH_FAILED;
        }
        Instant finishedAt = now();
        String outcome = error == null ? PaymentSourcePolled.OUTCOME_OK : PaymentSourcePolled.OUTCOME_ERROR;
        PollResult result = new PollResult(source.sourceId(), source.insurerId(), startedAt, finishedAt, outcome, error,
                counts.found, counts.processed, counts.rejected);
        publishSummary(result);
        metrics.poll(source.sourceId(), outcome, finishedAt);
        if (result.ok()) {
            log.info("Poll of source {} (insurer {}) OK: {} found, {} processed, {} rejected", source.sourceId(),
                    source.insurerId(), counts.found, counts.processed, counts.rejected);
        } else {
            log.warn("Poll of source {} (insurer {}) ERROR {}: {} found, {} processed, {} rejected",
                    source.sourceId(), source.insurerId(), error, counts.found, counts.processed, counts.rejected);
        }
        return result;
    }

    /** Passos 2 a 4. Devolve o erro da fonte, ou {@code null}. */
    private PollErrorCode read(FileSourceDescriptor source, PaymentFileParser parser, Path workDir, Counts counts) {
        Credentials credentials;
        try {
            Optional<Credentials> read = credentials(source);
            if (read.isEmpty()) {
                log.warn("Source {} (insurer {}): SFTP secret not found in the secret store", source.sourceId(),
                        source.insurerId());
                return PollErrorCode.SECRET_NOT_FOUND;
            }
            credentials = read.get();
        } catch (SecretReader.SecretStoreUnavailableException e) {
            return PollErrorCode.INTERNAL_ERROR;
        }
        FileSourceDescriptor.Sftp sftp = source.sftp();
        PollErrorCode error = null;
        try (Connection conn = sftpSessions.open(sftp, credentials)) {
            if (!conn.exists(sftp.inboundDir())) {
                log.warn("Source {}: inbound directory {} not found", source.sourceId(), sftp.inboundDir());
                return PollErrorCode.SFTP_DIRECTORY_NOT_FOUND;
            }
            recoverMovedButNotCompleted(source, conn);
            List<RemoteFile> listing = candidates(conn.listFiles(sftp.inboundDir()), sftp.filePattern());
            counts.found = listing.size();
            for (RemoteFile file : listing.stream().limit(properties.maxFilesPerRun()).toList()) {
                try {
                    switch (processor.process(source, parser, conn, file, workDir)) {
                        case DONE -> counts.processed++;
                        case REJECTED -> counts.rejected++;
                        case PUBLISH_FAILED -> counts.publishFailed = true;
                        default -> {
                            // SKIPPED, PENDING
                        }
                    }
                } catch (SftpException e) {
                    log.warn("File {} of source {}: {} ({})", file.name(), source.sourceId(), e.code(), e.getMessage());
                    error = error == null ? e.code() : error;
                } catch (RuntimeException e) {
                    log.error("File {} of source {} failed: {}", file.name(), source.sourceId(),
                            e.getClass().getSimpleName(), e);
                    error = error == null ? PollErrorCode.INTERNAL_ERROR : error;
                }
            }
        } catch (SftpException e) {
            log.warn("Source {} (insurer {}): {} ({})", source.sourceId(), source.insurerId(), e.code(),
                    e.getMessage());
            return error == null ? e.code() : error;
        }
        return error;
    }

    /**
     * Ficheiros {@code PUBLISHED} desta fonte que já não estão em {@code inboundDir}: a mudança de pasta
     * correu, mas o {@code PaymentFileProcessed} não foi confirmado. Sem isto ficariam a meio para sempre.
     */
    private void recoverMovedButNotCompleted(FileSourceDescriptor source, Connection conn) {
        if (!source.sftp().moveAfterProcessing()) {
            return;
        }
        for (ProcessedFile pf : files.findBySourceIdAndStatus(source.sourceId(), ProcessedFile.Status.PUBLISHED.name())) {
            if (!conn.exists(FileProcessor.join(source.sftp().inboundDir(), pf.getFileName()))) {
                log.info("File {} of source {} already moved: completing it", pf.getFileName(), source.sourceId());
                processor.complete(source, pf);
            }
        }
    }

    /**
     * Só os nomes que correspondem a {@code filePattern} (glob), sem os que acabam em {@code .part} ou
     * começam por {@code .} (a meio de envio), por data de modificação e depois por nome.
     */
    static List<RemoteFile> candidates(List<RemoteFile> files, String filePattern) {
        String pattern = filePattern == null || filePattern.isBlank()
                ? FileSourceDescriptor.Sftp.DEFAULT_FILE_PATTERN : filePattern;
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        return files.stream()
                .filter(f -> !f.name().startsWith(".") && !f.name().endsWith(".part"))
                .filter(f -> matches(matcher, f.name()))
                .sorted(Comparator.comparing((RemoteFile f) -> f.modifiedAt() == null ? Instant.EPOCH : f.modifiedAt())
                        .thenComparing(RemoteFile::name))
                .toList();
    }

    private static boolean matches(PathMatcher matcher, String name) {
        try {
            return matcher.matches(Path.of(name));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /** Segredos do tipo de autenticação da fonte; vazio se faltar algum obrigatório. */
    private Optional<Credentials> credentials(FileSourceDescriptor source) {
        if (FileSourceDescriptor.Sftp.AUTH_KEY.equals(source.sftp().authType())) {
            Optional<String> key = secret(source, SECRET_PRIVATE_KEY);
            if (key.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new Credentials(null, key.get(), secret(source, SECRET_PASSPHRASE).orElse(null)));
        }
        return secret(source, SECRET_PASSWORD).map(password -> new Credentials(password, null, null));
    }

    private Optional<String> secret(FileSourceDescriptor source, String field) {
        String path = source.secretRefs() == null ? null : source.secretRefs().get(field);
        return path == null ? Optional.empty() : secrets.read(path);
    }

    private void publishSummary(PollResult result) {
        PaymentSourcePolled message = new PaymentSourcePolled(MessageIds.paymentSourcePolled(),
                MessageHeaders.SCHEMA_VERSION, result.insurerId(), result.sourceId(), result.startedAt(),
                result.finishedAt(), result.outcome(), result.errorCode(), result.filesFound(), result.filesProcessed(),
                result.filesRejected());
        try {
            publisher.publish(message, PaymentsTopology.sourcePolledKey(result.insurerId()), message.messageId());
        } catch (PublishException e) {
            log.warn("PaymentSourcePolled of source {} not confirmed: {}", result.sourceId(), e.getMessage());
        }
    }

    private static void clean(Path workDir) {
        if (!Files.isDirectory(workDir)) {
            return;
        }
        try (Stream<Path> paths = Files.list(workDir)) {
            paths.forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("Temporary file {} not deleted: {}", p.getFileName(), e.getClass().getSimpleName());
                }
            });
        } catch (IOException e) {
            log.warn("Work directory {} not cleaned: {}", workDir, e.getClass().getSimpleName());
        }
    }

    private Instant now() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }

    private static final class Counts {
        int found;
        int processed;
        int rejected;
        boolean publishFailed;
    }
}
