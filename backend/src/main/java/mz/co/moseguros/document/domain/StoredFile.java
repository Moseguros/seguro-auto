package mz.co.moseguros.document.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Metadados de um ficheiro guardado ({@code document.file}). O conteúdo está no
 * {@code FileStorage}, na {@link #getStorageKey() chave} (nunca com o nome original nem dados
 * pessoais). A linha nunca é apagada: a limpeza de órfãos marca {@code purged_at}.
 */
@Entity
@Table(schema = "document", name = "file")
public class StoredFile extends BaseEntity {

    /** 10 MB (ADR-005). */
    public static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;

    @Column(name = "owner_type", nullable = false, updatable = false, length = 30)
    private String ownerType;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;

    @Column(name = "purpose", nullable = false, updatable = false, length = 40)
    private String purpose;

    @Column(name = "insurer_id", updatable = false)
    private UUID insurerId;

    @Column(name = "uploaded_by_user_id", updatable = false)
    private UUID uploadedByUserId;

    @Column(name = "uploaded_by_org_id", updatable = false)
    private UUID uploadedByOrgId;

    @Column(name = "original_filename", nullable = false, updatable = false, length = 200)
    private String originalFilename;

    @Column(name = "content_type", nullable = false, updatable = false, length = 40)
    private String contentType;

    @Column(name = "size_bytes", nullable = false, updatable = false)
    private long sizeBytes;

    @Column(name = "sha256", nullable = false, updatable = false, length = 64)
    private String sha256;

    @Column(name = "storage_key", nullable = false, updatable = false, length = 300)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification", nullable = false, updatable = false, length = 10)
    private FileClassification classification;

    @Column(name = "linked_at")
    private Instant linkedAt;

    @Column(name = "purged_at")
    private Instant purgedAt;

    protected StoredFile() {
    }

    /**
     * Ficheiro novo, ainda por ligar ao dono. O {@link #getId() id} já existe (UUID v7): quem
     * cria usa-o para formar a chave de armazenamento antes de gravar a linha.
     */
    public static StoredFile create(String ownerType, UUID ownerId, String purpose, UUID insurerId,
                                    UUID uploadedByUserId, UUID uploadedByOrgId, String originalFilename,
                                    AllowedContentType contentType, long sizeBytes, String sha256,
                                    FileClassification classification) {
        StoredFile file = new StoredFile();
        file.ownerType = Objects.requireNonNull(ownerType, "ownerType");
        file.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        file.purpose = Objects.requireNonNull(purpose, "purpose");
        file.insurerId = insurerId;
        file.uploadedByUserId = uploadedByUserId;
        file.uploadedByOrgId = uploadedByOrgId;
        file.originalFilename = Objects.requireNonNull(originalFilename, "originalFilename");
        file.contentType = Objects.requireNonNull(contentType, "contentType").mimeType();
        if (sizeBytes < 1 || sizeBytes > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("sizeBytes out of range: " + sizeBytes);
        }
        file.sizeBytes = sizeBytes;
        file.sha256 = Objects.requireNonNull(sha256, "sha256");
        file.classification = Objects.requireNonNull(classification, "classification");
        return file;
    }

    /** Define a chave de armazenamento; só antes de gravar a linha. */
    public void assignStorageKey(String storageKey) {
        if (this.storageKey != null) {
            throw new IllegalStateException("storage key already assigned");
        }
        this.storageKey = Objects.requireNonNull(storageKey, "storageKey");
    }

    /** Liga o ficheiro ao dono; idempotente (mantém a primeira data). */
    public void markLinked(Instant now) {
        if (linkedAt == null) {
            linkedAt = Objects.requireNonNull(now, "now");
        }
    }

    /** Conteúdo apagado pela limpeza de órfãos. */
    public void markPurged(Instant now) {
        if (purgedAt == null) {
            purgedAt = Objects.requireNonNull(now, "now");
        }
    }

    public boolean isPurged() {
        return purgedAt != null;
    }

    public boolean isLinked() {
        return linkedAt != null;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getPurpose() {
        return purpose;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getUploadedByUserId() {
        return uploadedByUserId;
    }

    public UUID getUploadedByOrgId() {
        return uploadedByOrgId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public FileClassification getClassification() {
        return classification;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public Instant getPurgedAt() {
        return purgedAt;
    }
}
