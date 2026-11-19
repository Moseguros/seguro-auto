package mz.co.moseguros.paymentworker.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Um registo de detalhe (pagamento) de um ficheiro ({@code payment_worker.file_record}, S5-09 §3).
 * {@code publishedAt} só se grava com a confirmação ({@code ack}) do RabbitMQ.
 */
@Table(schema = "payment_worker", value = "file_record")
public class FileRecord {

    public static final int MAX_RAW_LINE = 200;

    @Id
    private UUID id;
    @Version
    private Long version;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private UUID fileId;
    private int lineNo;
    private String entity;
    private String reference;
    private long amount;
    private Long fee;
    private Instant paidAt;
    private String channel;
    private String bankTransactionId;
    private String rawLine;
    private String messageId;
    private Instant publishedAt;

    protected FileRecord() {
    }

    public static FileRecord of(UUID id, UUID fileId, int lineNo, String entity, String reference, long amount, Long fee,
                                Instant paidAt, String channel, String bankTransactionId, String rawLine,
                                String messageId, Instant now) {
        FileRecord r = new FileRecord();
        r.id = Objects.requireNonNull(id, "id");
        r.createdAt = now;
        r.createdBy = ProcessedFile.ACTOR;
        r.updatedAt = now;
        r.updatedBy = ProcessedFile.ACTOR;
        r.fileId = Objects.requireNonNull(fileId, "fileId");
        r.lineNo = lineNo;
        r.entity = Objects.requireNonNull(entity, "entity");
        r.reference = Objects.requireNonNull(reference, "reference");
        r.amount = amount;
        r.fee = fee;
        r.paidAt = Objects.requireNonNull(paidAt, "paidAt");
        r.channel = channel;
        r.bankTransactionId = bankTransactionId;
        String raw = Objects.requireNonNull(rawLine, "rawLine");
        r.rawLine = raw.length() > MAX_RAW_LINE ? raw.substring(0, MAX_RAW_LINE) : raw;
        r.messageId = Objects.requireNonNull(messageId, "messageId");
        return r;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFileId() {
        return fileId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getEntity() {
        return entity;
    }

    public String getReference() {
        return reference;
    }

    public long getAmount() {
        return amount;
    }

    public Long getFee() {
        return fee;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public String getChannel() {
        return channel;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getRawLine() {
        return rawLine;
    }

    public String getMessageId() {
        return messageId;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    /** Sem a referência nem a linha (logs). */
    @Override
    public String toString() {
        return "FileRecord[fileId=" + fileId + ", lineNo=" + lineNo + ", published=" + (publishedAt != null) + "]";
    }
}
