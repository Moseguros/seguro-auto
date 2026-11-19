package mz.co.moseguros.paymentworker.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Um ficheiro lido de uma fonte ({@code payment_worker.processed_file}, S5-09 §3 e §6).
 *
 * <pre>
 * RECEIVED → PARSED → PUBLISHED → DONE
 *     └──────────┴───→ ERROR (erro de conteúdo; nunca se tenta de novo com o mesmo SHA-256)
 * </pre>
 */
@Table(schema = "payment_worker", value = "processed_file")
public class ProcessedFile {

    public enum Status {
        RECEIVED,
        PARSED,
        PUBLISHED,
        DONE,
        ERROR
    }

    /** {@code created_by}/{@code updated_by} de todas as linhas do worker. */
    public static final String ACTOR = "payment-file-worker";
    public static final int MAX_ERROR_MESSAGE = 500;

    @Id
    private UUID id;
    @Version
    private Long version;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private UUID sourceId;
    private UUID insurerId;
    private String fileName;
    private String sha256;
    private long sizeBytes;
    private Instant remoteMtime;
    private String fileFormat;
    private String formatFileId;
    private String previousFormatFileId;
    private Integer recordCount;
    private Long totalAmount;
    private Status status;
    private String errorCode;
    private String errorMessage;
    private int attempts;
    private Instant receivedAt;
    private Instant completedAt;

    protected ProcessedFile() {
    }

    /** Ficheiro acabado de descarregar ({@code RECEIVED}). */
    public static ProcessedFile received(UUID id, UUID sourceId, UUID insurerId, String fileName, String sha256,
                                         long sizeBytes, Instant remoteMtime, Instant now) {
        ProcessedFile f = new ProcessedFile();
        f.id = Objects.requireNonNull(id, "id");
        f.createdAt = now;
        f.createdBy = ACTOR;
        f.updatedAt = now;
        f.updatedBy = ACTOR;
        f.sourceId = Objects.requireNonNull(sourceId, "sourceId");
        f.insurerId = Objects.requireNonNull(insurerId, "insurerId");
        f.fileName = Objects.requireNonNull(fileName, "fileName");
        f.sha256 = Objects.requireNonNull(sha256, "sha256");
        f.sizeBytes = sizeBytes;
        f.remoteMtime = remoteMtime;
        f.status = Status.RECEIVED;
        f.attempts = 1;
        f.receivedAt = now;
        return f;
    }

    /** Mais uma tentativa de um ficheiro que ficou a meio ({@code RECEIVED}, {@code PARSED}, {@code PUBLISHED}). */
    public void retried(String fileName, Instant remoteMtime, Instant now) {
        this.attempts++;
        this.fileName = fileName;
        this.remoteMtime = remoteMtime;
        touch(now);
    }

    /** Identificadores do ficheiro no formato, depois de lido. */
    public void identified(String fileFormat, String formatFileId, String previousFormatFileId, Instant now) {
        this.fileFormat = fileFormat;
        this.formatFileId = formatFileId;
        this.previousFormatFileId = previousFormatFileId;
        touch(now);
    }

    /** Registos gravados ({@code PARSED}). */
    public void parsed(int recordCount, long totalAmount, Instant now) {
        requireStatus(Status.RECEIVED);
        this.recordCount = recordCount;
        this.totalAmount = totalAmount;
        this.status = Status.PARSED;
        touch(now);
    }

    /** Todos os registos confirmados pelo RabbitMQ. */
    public void published(Instant now) {
        requireStatus(Status.PARSED);
        this.status = Status.PUBLISHED;
        touch(now);
    }

    /** {@code PaymentFileProcessed} publicado: fim. */
    public void done(Instant now) {
        requireStatus(Status.PUBLISHED);
        this.status = Status.DONE;
        this.completedAt = now;
        touch(now);
    }

    /** Erro de conteúdo: {@code message} sem conteúdo do ficheiro além do número da linha. */
    public void error(String code, String message, Instant now) {
        if (status == Status.DONE || status == Status.PUBLISHED) {
            throw new IllegalStateException("A published file cannot be rejected: " + id);
        }
        this.status = Status.ERROR;
        this.errorCode = code;
        this.errorMessage = message == null ? null
                : message.length() > MAX_ERROR_MESSAGE ? message.substring(0, MAX_ERROR_MESSAGE) : message;
        this.completedAt = now;
        touch(now);
    }

    private void requireStatus(Status expected) {
        if (status != expected) {
            throw new IllegalStateException("File " + id + " is " + status + ", expected " + expected);
        }
    }

    private void touch(Instant now) {
        this.updatedAt = now;
        this.updatedBy = ACTOR;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSha256() {
        return sha256;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public Instant getRemoteMtime() {
        return remoteMtime;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public String getFormatFileId() {
        return formatFileId;
    }

    public String getPreviousFormatFileId() {
        return previousFormatFileId;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    /** Primeiros 12 caracteres do SHA-256 (logs). */
    public String shortSha() {
        return sha256.length() > 12 ? sha256.substring(0, 12) : sha256;
    }

    @Override
    public String toString() {
        return "ProcessedFile[id=" + id + ", sourceId=" + sourceId + ", fileName=" + fileName + ", sha256="
                + shortSha() + ", status=" + status + "]";
    }
}
