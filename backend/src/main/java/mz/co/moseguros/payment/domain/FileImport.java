package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Um ficheiro de movimentos MEPS ou BMEPS processado ou rejeitado pelo {@code payment-file-worker}
 * ({@code payment.file_import}, S5-10 §1). Só se acrescentam linhas.
 */
@Entity
@Table(schema = "payment", name = "file_import")
public class FileImport extends BaseEntity {

    public static final int MAX_DETAIL = 200;

    /** Estado do ficheiro. */
    public enum Status { PROCESSED, REJECTED }

    @Column(name = "message_id", nullable = false, updatable = false, length = 64)
    private String messageId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "source_id", nullable = false, updatable = false)
    private UUID sourceId;

    @Column(name = "file_name", nullable = false, updatable = false, length = 255)
    private String fileName;

    @Column(name = "sha256", nullable = false, updatable = false, length = 64)
    private String sha256;

    @Column(name = "file_format", nullable = false, updatable = false, length = 10)
    private String fileFormat;

    @Column(name = "format_file_id", updatable = false, length = 30)
    private String formatFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false, length = 10)
    private Status status;

    @Column(name = "record_count", updatable = false)
    private Integer recordCount;

    @Column(name = "total_amount", updatable = false)
    private Long totalAmount;

    @Column(name = "total_fees", updatable = false)
    private Long totalFees;

    @Column(name = "reason", updatable = false, length = 30)
    private String reason;

    @Column(name = "detail", updatable = false, length = MAX_DETAIL)
    private String detail;

    @Column(name = "line_no", updatable = false)
    private Integer lineNo;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    protected FileImport() {
    }

    public static FileImport processed(PaymentFileProcessed m) {
        FileImport f = base(m.messageId(), m.insurerId(), m.sourceId(), m.fileName(), m.sha256(), m.fileFormat(),
                m.formatFileId());
        f.status = Status.PROCESSED;
        f.recordCount = m.recordCount();
        f.totalAmount = Objects.requireNonNull(m.totalAmount(), "totalAmount").amount();
        f.totalFees = m.totalFees() == null ? null : m.totalFees().amount();
        f.occurredAt = Objects.requireNonNull(m.processedAt(), "processedAt");
        return f;
    }

    public static FileImport rejected(PaymentFileRejected m) {
        FileImport f = base(m.messageId(), m.insurerId(), m.sourceId(), m.fileName(), m.sha256(), m.fileFormat(),
                m.formatFileId());
        f.status = Status.REJECTED;
        f.reason = Objects.requireNonNull(m.reason(), "reason").name();
        f.detail = m.detail() == null || m.detail().length() <= MAX_DETAIL ? m.detail()
                : m.detail().substring(0, MAX_DETAIL);
        f.lineNo = m.lineNo();
        f.occurredAt = Objects.requireNonNull(m.rejectedAt(), "rejectedAt");
        return f;
    }

    private static FileImport base(String messageId, UUID insurerId, UUID sourceId, String fileName, String sha256,
                                   String fileFormat, String formatFileId) {
        FileImport f = new FileImport();
        f.messageId = Objects.requireNonNull(messageId, "messageId");
        f.insurerId = Objects.requireNonNull(insurerId, "insurerId");
        f.sourceId = Objects.requireNonNull(sourceId, "sourceId");
        f.fileName = Objects.requireNonNull(fileName, "fileName");
        f.sha256 = Objects.requireNonNull(sha256, "sha256");
        f.fileFormat = Objects.requireNonNull(fileFormat, "fileFormat");
        f.formatFileId = formatFileId;
        return f;
    }

    public String getMessageId() {
        return messageId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSha256() {
        return sha256;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public String getFormatFileId() {
        return formatFileId;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Long getTotalFees() {
        return totalFees;
    }

    public String getReason() {
        return reason;
    }

    public String getDetail() {
        return detail;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
