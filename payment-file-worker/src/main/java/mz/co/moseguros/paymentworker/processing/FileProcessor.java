package mz.co.moseguros.paymentworker.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;
import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentcontracts.MoneyAmount;
import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import mz.co.moseguros.paymentworker.domain.FileRecord;
import mz.co.moseguros.paymentworker.domain.FileRecordRepository;
import mz.co.moseguros.paymentworker.domain.ProcessedFile;
import mz.co.moseguros.paymentworker.domain.ProcessedFileRepository;
import mz.co.moseguros.paymentworker.metrics.WorkerMetrics;
import mz.co.moseguros.paymentworker.parser.FileHeader;
import mz.co.moseguros.paymentworker.parser.FileValidator;
import mz.co.moseguros.paymentworker.parser.ParseContext;
import mz.co.moseguros.paymentworker.parser.ParsedFile;
import mz.co.moseguros.paymentworker.parser.ParsedRecord;
import mz.co.moseguros.paymentworker.parser.PaymentFileParseException;
import mz.co.moseguros.paymentworker.parser.PaymentFileParser;
import mz.co.moseguros.paymentworker.processing.RecordPublisher.PublishException;
import mz.co.moseguros.paymentworker.sftp.SftpSessions.Connection;
import mz.co.moseguros.paymentworker.sftp.SftpSessions.RemoteFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Um ficheiro de uma fonte (S5-09 §6). Um ficheiro nunca gera {@code ReferencePaymentReceived} duas
 * vezes, seja reposto com o mesmo nome, com outro nome ou deixado em {@code inboundDir}: pelo SHA-256
 * (passos a0 e c) e pelo identificador do ficheiro no formato (passo e2).
 *
 * <p>Uma rejeição publica primeiro o {@code PaymentFileRejected} e só depois grava {@code ERROR} e move
 * o ficheiro: se a publicação falhar, a leitura seguinte trata o ficheiro de novo (o erro de conteúdo
 * repete-se) e a rejeição não se perde; depois de {@code ERROR}, o mesmo SHA-256 nunca volta a ser
 * publicado.
 */
@Component
public class FileProcessor {

    /** O que aconteceu ao ficheiro nesta leitura. */
    public enum Outcome {
        /** Todos os registos publicados e {@code PaymentFileProcessed} enviado. */
        DONE,
        /** Rejeitado por conteúdo ({@code PaymentFileRejected}). */
        REJECTED,
        /** Já tratado: nada publicado. */
        SKIPPED,
        /** Uma publicação falhou: retoma na leitura seguinte. */
        PUBLISH_FAILED,
        /** Publicado mas ainda por mudar de pasta: retoma na leitura seguinte. */
        PENDING
    }

    static final int PUBLISHED_BATCH = 100;
    static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");
    static final DateTimeFormatter SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(MAPUTO);

    private static final Logger log = LoggerFactory.getLogger(FileProcessor.class);

    private final ProcessedFileRepository files;
    private final FileRecordRepository records;
    private final FileValidator validator;
    private final RecordPublisher publisher;
    private final WorkerMetrics metrics;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final long maxFileSize;

    public FileProcessor(ProcessedFileRepository files, FileRecordRepository records, FileValidator validator,
                         RecordPublisher publisher, WorkerMetrics metrics, TransactionTemplate tx, Clock clock,
                         WorkerProperties properties) {
        this.files = files;
        this.records = records;
        this.validator = validator;
        this.publisher = publisher;
        this.metrics = metrics;
        this.tx = tx;
        this.clock = clock;
        this.maxFileSize = properties.maxFileSize().toBytes();
    }

    /**
     * Trata um ficheiro da listagem de {@code inboundDir}.
     *
     * @param workDir pasta local dos ficheiros descarregados desta leitura
     */
    public Outcome process(FileSourceDescriptor source, PaymentFileParser parser, Connection conn, RemoteFile file,
                           Path workDir) {
        UUID sourceId = source.sourceId();
        FileSourceDescriptor.Sftp sftp = source.sftp();
        String inboundPath = join(sftp.inboundDir(), file.name());
        Instant mtime = file.modifiedAt() == null ? null : file.modifiedAt().truncatedTo(ChronoUnit.MICROS);

        // a0: já tratado, sem descarregar
        if (mtime != null) {
            Optional<ProcessedFile> finished = files.findFinishedListing(sourceId, file.name(), file.size(), mtime);
            if (finished.isPresent()) {
                log.debug("File {} of source {} already {} (same name, size and mtime): not downloaded", file.name(),
                        sourceId, finished.get().getStatus());
                moveIfConfigured(conn, sftp, inboundPath, file.name(), finished.get().getStatus());
                metrics.file(sourceId, WorkerMetrics.SKIPPED);
                return Outcome.SKIPPED;
            }
        }

        // a: grande demais, sem descarregar
        if (file.size() > maxFileSize) {
            String sha = MessageIds.sha256Hex(file.name() + "|" + file.size() + "|" + mtime);
            Optional<ProcessedFile> existing = files.findBySourceIdAndSha256(sourceId, sha);
            if (existing.isPresent()) {
                moveIfConfigured(conn, sftp, inboundPath, file.name(), ProcessedFile.Status.ERROR);
                metrics.file(sourceId, WorkerMetrics.SKIPPED);
                return Outcome.SKIPPED;
            }
            ProcessedFile pf = ProcessedFile.received(UUID.randomUUID(), sourceId, source.insurerId(), file.name(),
                    sha, file.size(), mtime, now());
            return reject(source, conn, inboundPath, pf, true, FileRejectionReason.FILE_TOO_LARGE,
                    "File larger than the maximum of " + maxFileSize + " bytes", null);
        }

        // b: descarregar e calcular o SHA-256
        Path local = tempFile(workDir);
        conn.download(inboundPath, local);
        metrics.download(sourceId);
        String sha = sha256(local);
        long size = size(local);

        // c: deduplicação por conteúdo
        Optional<ProcessedFile> existing = files.findBySourceIdAndSha256(sourceId, sha);
        ProcessedFile pf;
        if (existing.isPresent()) {
            pf = existing.get();
            switch (pf.getStatus()) {
                case DONE, ERROR -> {
                    log.info("File {} of source {} has the content of an already {} file (sha256 {}): nothing "
                            + "published", file.name(), sourceId, pf.getStatus(), pf.shortSha());
                    moveIfConfigured(conn, sftp, inboundPath, file.name(), pf.getStatus());
                    metrics.file(sourceId, WorkerMetrics.SKIPPED);
                    return Outcome.SKIPPED;
                }
                case PARSED, PUBLISHED -> {
                    pf.retried(file.name(), mtime, now());
                    pf = files.save(pf);
                    log.info("Resuming file {} of source {} (sha256 {}, {})", file.name(), sourceId, pf.shortSha(),
                            pf.getStatus());
                    return resume(source, conn, inboundPath, pf);
                }
                default -> {
                    pf.retried(file.name(), mtime, now());
                    pf = files.save(pf);
                }
            }
        } else {
            pf = files.save(ProcessedFile.received(UUID.randomUUID(), sourceId, source.insurerId(), file.name(), sha,
                    size, mtime, now()));
        }
        log.info("File {} of source {} (insurer {}) received: sha256 {}, {} bytes", file.name(), sourceId,
                source.insurerId(), pf.shortSha(), size);

        // d: ler
        ParseContext ctx = new ParseContext(source.entity(), source.bmepsCompanyId(), source.bmepsProfileId(),
                ParseContext.MAPUTO, file.name());
        ParsedFile parsed;
        try (InputStream in = Files.newInputStream(local)) {
            parsed = parser.parse(in, ctx);
        } catch (PaymentFileParseException e) {
            return reject(source, conn, inboundPath, pf, false, e.code(), lineMessage(e.getMessage(), e.lineNo()),
                    e.lineNo());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        FileHeader header = parsed.header();
        pf.identified(source.fileFormat(), header.formatFileId(), header.previousFormatFileId(), now());

        // e: validação comum
        Optional<FileValidator.Failure> failure = validator.validate(parsed, ctx, source.fileFormat());
        if (failure.isPresent()) {
            FileValidator.Failure f = failure.get();
            return reject(source, conn, inboundPath, pf, false, f.reason(), lineMessage(f.detail(), f.lineNo()),
                    f.lineNo());
        }

        // e2: o mesmo identificador do ficheiro com outro conteúdo
        if (header.formatFileId() != null
                && files.existsOtherWithFormatFileId(sourceId, header.formatFileId(), pf.getId())) {
            log.warn("alert=PAYMENT_FILE_DUPLICATE_ID source={} insurer={} file={} sha256={} formatFileId={}: "
                            + "already processed with other content; not processed", sourceId, source.insurerId(),
                    file.name(), pf.shortSha(), header.formatFileId());
            return reject(source, conn, inboundPath, pf, false, FileRejectionReason.DUPLICATE_FILE_ID,
                    "File id " + header.formatFileId() + " already processed with other content", null);
        }

        // e3: sequência (só aviso)
        checkSequence(source, pf, header);

        // f: gravar os registos
        ProcessedFile toSave = pf;
        pf = tx.execute(status -> {
            Instant now = now();
            List<FileRecord> rows = new ArrayList<>();
            long total = 0;
            for (ParsedRecord r : parsed.records()) {
                rows.add(FileRecord.of(UUID.randomUUID(), toSave.getId(), r.lineNo(), r.entity(), r.reference(),
                        r.amountCentavos(), r.feeCentavos(), r.paidAt(), r.channel(), r.bankTransactionId(),
                        r.rawLine(), MessageIds.referencePaymentReceived(sourceId, sha, r.lineNo()), now));
                total += r.amountCentavos();
            }
            records.saveAll(rows);
            toSave.parsed(rows.size(), total, now);
            return files.save(toSave);
        });
        log.info("File {} of source {} parsed: {} records, total {} (formatFileId {})", file.name(), sourceId,
                pf.getRecordCount(), pf.getTotalAmount(), pf.getFormatFileId());
        return resume(source, conn, inboundPath, pf);
    }

    /** Passos g e h: publicar o que falta e terminar. */
    Outcome resume(FileSourceDescriptor source, Connection conn, String inboundPath, ProcessedFile pf) {
        if (pf.getStatus() == ProcessedFile.Status.PARSED) {
            if (!publishRecords(source, pf)) {
                return Outcome.PUBLISH_FAILED;
            }
            pf.published(now());
            pf = files.save(pf);
        }
        if (source.sftp().moveAfterProcessing()) {
            try {
                move(conn, inboundPath, source.sftp().processedDir(), pf.getFileName());
            } catch (RuntimeException e) {
                log.warn("File {} of source {} published but not moved to {} ({}): retried on the next poll",
                        pf.getFileName(), source.sourceId(), source.sftp().processedDir(), e.getMessage());
                return Outcome.PENDING;
            }
        }
        return complete(source, pf);
    }

    /** Passo h depois da mudança de pasta: {@code PaymentFileProcessed} e {@code DONE}. */
    Outcome complete(FileSourceDescriptor source, ProcessedFile pf) {
        Instant now = now();
        List<FileRecord> rows = records.findByFileId(pf.getId());
        Long fees = null;
        for (FileRecord r : rows) {
            if (r.getFee() != null) {
                fees = (fees == null ? 0 : fees) + r.getFee();
            }
        }
        PaymentFileProcessed message = new PaymentFileProcessed(
                MessageIds.paymentFileProcessed(source.sourceId(), pf.getSha256()), MessageHeaders.SCHEMA_VERSION,
                pf.getInsurerId(), pf.getSourceId(), pf.getFileFormat(), pf.getFileName(), pf.getSha256(),
                pf.getFormatFileId(), pf.getRecordCount() == null ? rows.size() : pf.getRecordCount(),
                MoneyAmount.mzn(pf.getTotalAmount() == null ? 0 : pf.getTotalAmount()),
                fees == null ? null : MoneyAmount.mzn(fees), now);
        try {
            publisher.publish(message, PaymentsTopology.fileProcessedKey(pf.getInsurerId()), message.messageId());
        } catch (PublishException e) {
            log.warn("PaymentFileProcessed of file {} (source {}) not confirmed: {}", pf.getFileName(),
                    source.sourceId(), e.getMessage());
            return Outcome.PUBLISH_FAILED;
        }
        pf.done(now);
        files.save(pf);
        metrics.file(source.sourceId(), WorkerMetrics.DONE);
        log.info("File {} of source {} (insurer {}) done: sha256 {}, {} records", pf.getFileName(), source.sourceId(),
                pf.getInsurerId(), pf.getSha256().substring(0, 12), pf.getRecordCount());
        return Outcome.DONE;
    }

    /** Passo g: por ordem de linha, os que ainda não têm {@code published_at}. */
    private boolean publishRecords(FileSourceDescriptor source, ProcessedFile pf) {
        List<FileRecord> pending = records.findUnpublished(pf.getId());
        List<UUID> confirmed = new ArrayList<>();
        int published = 0;
        try {
            for (FileRecord r : pending) {
                ReferencePaymentReceived message = new ReferencePaymentReceived(r.getMessageId(),
                        MessageHeaders.SCHEMA_VERSION, pf.getInsurerId(), pf.getSourceId(), pf.getFileFormat(),
                        r.getEntity(), r.getReference(), MoneyAmount.mzn(r.getAmount()),
                        r.getFee() == null ? null : MoneyAmount.mzn(r.getFee()), r.getPaidAt(), r.getChannel(),
                        r.getBankTransactionId(), pf.getFileName(), pf.getSha256(), pf.getFormatFileId(),
                        r.getLineNo());
                publisher.publish(message, PaymentsTopology.referencePaidKey(pf.getInsurerId()), r.getMessageId());
                log.debug("Record line {} of file {} published (reference {})", r.getLineNo(), pf.getFileName(),
                        r.getReference());
                confirmed.add(r.getId());
                published++;
                if (confirmed.size() >= PUBLISHED_BATCH) {
                    flush(confirmed);
                }
            }
            return true;
        } catch (PublishException e) {
            log.warn("Publishing file {} of source {} stopped after {} of {} records: {}", pf.getFileName(),
                    source.sourceId(), published, pending.size(), e.getMessage());
            return false;
        } finally {
            flush(confirmed);
            metrics.recordsPublished(source.sourceId(), published);
        }
    }

    private void flush(List<UUID> confirmed) {
        if (!confirmed.isEmpty()) {
            records.markPublished(List.copyOf(confirmed), now());
            confirmed.clear();
        }
    }

    /**
     * Rejeição do ficheiro inteiro: publica {@code PaymentFileRejected}, grava {@code ERROR} e move para
     * {@code errorDir}. Se a publicação falhar, nada é gravado ({@code PUBLISH_FAILED}).
     *
     * @param insert {@code pf} ainda não está na BD ({@code FILE_TOO_LARGE})
     */
    private Outcome reject(FileSourceDescriptor source, Connection conn, String inboundPath, ProcessedFile pf,
                           boolean insert, FileRejectionReason reason, String detail, Integer lineNo) {
        Instant rejectedAt = now();
        String shortDetail = detail == null ? null : detail.length() > PaymentFileRejected.DETAIL_MAX_LENGTH
                ? detail.substring(0, PaymentFileRejected.DETAIL_MAX_LENGTH) : detail;
        PaymentFileRejected message = new PaymentFileRejected(
                MessageIds.paymentFileRejected(source.sourceId(), pf.getSha256(), rejectedAt),
                MessageHeaders.SCHEMA_VERSION, source.insurerId(), source.sourceId(), source.fileFormat(),
                pf.getFileName(), pf.getSha256(), pf.getFormatFileId(), reason, shortDetail, lineNo, rejectedAt);
        try {
            publisher.publish(message, PaymentsTopology.fileRejectedKey(source.insurerId()), message.messageId());
        } catch (PublishException e) {
            log.warn("PaymentFileRejected of file {} (source {}, {}) not confirmed: {}; retried on the next poll",
                    pf.getFileName(), source.sourceId(), reason, e.getMessage());
            return Outcome.PUBLISH_FAILED;
        }
        pf.error(reason.name(), shortDetail, rejectedAt);
        files.save(pf);
        log.warn("File {} of source {} (insurer {}) rejected: {} (sha256 {}{})", pf.getFileName(), source.sourceId(),
                source.insurerId(), reason, pf.shortSha(), lineNo == null ? "" : ", line " + lineNo);
        moveIfConfigured(conn, source.sftp(), inboundPath, pf.getFileName(), ProcessedFile.Status.ERROR);
        metrics.file(source.sourceId(), WorkerMetrics.ERROR);
        return Outcome.REJECTED;
    }

    /** Passo e3: um ficheiro em falta na sequência só gera um aviso. */
    private void checkSequence(FileSourceDescriptor source, ProcessedFile pf, FileHeader header) {
        Optional<ProcessedFile> last = files.findLastDone(source.sourceId());
        if (last.isEmpty()) {
            return;
        }
        String lastId = last.get().getFormatFileId();
        boolean gap;
        if (header.bmepsFileSequence() != null) {
            Integer lastSequence = bmepsSequence(lastId);
            gap = lastSequence == null || header.bmepsFileSequence() != lastSequence + 1;
        } else {
            gap = !Objects.equals(header.previousFormatFileId(), lastId);
        }
        if (gap) {
            log.warn("alert=PAYMENT_FILE_SEQUENCE_GAP source={} insurer={} file={} formatFileId={} previous={} "
                            + "lastDone={}: a file may be missing (ask the bank)", source.sourceId(),
                    source.insurerId(), pf.getFileName(), header.formatFileId(), header.previousFormatFileId(), lastId);
        }
    }

    /** {@code <empresa>-<perfil>-<ID>} → ID. */
    static Integer bmepsSequence(String formatFileId) {
        if (formatFileId == null) {
            return null;
        }
        int dash = formatFileId.lastIndexOf('-');
        try {
            return Integer.parseInt(formatFileId.substring(dash + 1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void moveIfConfigured(Connection conn, FileSourceDescriptor.Sftp sftp, String inboundPath, String name,
                                  ProcessedFile.Status status) {
        if (!sftp.moveAfterProcessing()) {
            return;
        }
        String dir = status == ProcessedFile.Status.ERROR ? sftp.errorDir() : sftp.processedDir();
        try {
            move(conn, inboundPath, dir, name);
        } catch (RuntimeException e) {
            log.warn("File {} not moved to {}: {}", name, dir, e.getMessage());
        }
    }

    /** {@code rename} para {@code dir}; se já existir um com o mesmo nome, acrescenta {@code .<yyyyMMddHHmmss>}. */
    private void move(Connection conn, String from, String dir, String name) {
        String target = join(dir, name);
        if (conn.exists(target)) {
            target = target + "." + SUFFIX.format(now());
            for (int i = 1; conn.exists(target); i++) {
                target = join(dir, name) + "." + SUFFIX.format(now()) + "-" + i;
            }
        }
        conn.rename(from, target);
    }

    static String join(String dir, String name) {
        return dir.endsWith("/") ? dir + name : dir + "/" + name;
    }

    private static String lineMessage(String message, Integer lineNo) {
        String m = message == null ? "Invalid file" : message;
        return lineNo == null ? m : m + " (line " + lineNo + ")";
    }

    private Instant now() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }

    private static Path tempFile(Path dir) {
        try {
            Files.createDirectories(dir);
            return Files.createTempFile(dir, "file-", ".tmp");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static long size(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String sha256(Path file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream in = new DigestInputStream(Files.newInputStream(file), digest)) {
                in.transferTo(java.io.OutputStream.nullOutputStream());
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
