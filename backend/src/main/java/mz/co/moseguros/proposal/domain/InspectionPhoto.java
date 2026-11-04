package mz.co.moseguros.proposal.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Fotografia de uma vistoria ({@code proposal.inspection_photo}, S4-03 §1). Nunca se apaga: uma
 * fotografia substituída (mesmo ângulo fixo) ou retirada fica com {@code removed_at}. O ficheiro está
 * no módulo {@code document} (dono {@code INSPECTION}, a vistoria).
 */
@Entity
@Table(schema = "proposal", name = "inspection_photo")
public class InspectionPhoto extends BaseEntity {

    /** Casas decimais das coordenadas ({@code numeric(9,6)}). */
    public static final int COORDINATE_SCALE = 6;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "inspection_id", nullable = false, updatable = false)
    private UUID inspectionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false, updatable = false, length = 10)
    private PhotoSlot slot;

    @Column(name = "file_id", nullable = false, updatable = false)
    private UUID fileId;

    @Column(name = "client_photo_id", updatable = false)
    private UUID clientPhotoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, updatable = false, length = 10)
    private PhotoSource source;

    @Column(name = "taken_at", updatable = false)
    private Instant takenAt;

    @Column(name = "latitude", updatable = false, precision = 9, scale = COORDINATE_SCALE)
    private BigDecimal latitude;

    @Column(name = "longitude", updatable = false, precision = 9, scale = COORDINATE_SCALE)
    private BigDecimal longitude;

    @Column(name = "removed_at")
    private Instant removedAt;

    protected InspectionPhoto() {
    }

    /**
     * Dados de uma fotografia nova, já validados.
     *
     * @param clientPhotoId opcional; torna o envio idempotente
     * @param takenAt       opcional; hora indicada pelo cliente
     * @param latitude      opcional, com {@code longitude}
     */
    public record NewPhoto(PhotoSlot slot, UUID fileId, UUID clientPhotoId, PhotoSource source, Instant takenAt,
                           BigDecimal latitude, BigDecimal longitude) {

        public NewPhoto {
            Objects.requireNonNull(slot, "slot");
            Objects.requireNonNull(fileId, "fileId");
            Objects.requireNonNull(source, "source");
            if ((latitude == null) != (longitude == null)) {
                throw new IllegalArgumentException("latitude and longitude go together");
            }
        }
    }

    static InspectionPhoto of(Inspection inspection, NewPhoto p) {
        InspectionPhoto photo = new InspectionPhoto();
        photo.insurerId = inspection.getInsurerId();
        photo.inspectionId = inspection.getId();
        photo.slot = p.slot();
        photo.fileId = p.fileId();
        photo.clientPhotoId = p.clientPhotoId();
        photo.source = p.source();
        photo.takenAt = p.takenAt();
        photo.latitude = scale(p.latitude());
        photo.longitude = scale(p.longitude());
        return photo;
    }

    private static BigDecimal scale(BigDecimal value) {
        return value == null ? null : value.setScale(COORDINATE_SCALE, RoundingMode.HALF_UP);
    }

    /** Substituída ou retirada: deixa de contar (fica guardada). */
    void retire(Instant now) {
        if (removedAt == null) {
            removedAt = Objects.requireNonNull(now, "now");
        }
    }

    public boolean isActive() {
        return removedAt == null;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getInspectionId() {
        return inspectionId;
    }

    public PhotoSlot getSlot() {
        return slot;
    }

    public UUID getFileId() {
        return fileId;
    }

    public UUID getClientPhotoId() {
        return clientPhotoId;
    }

    public PhotoSource getSource() {
        return source;
    }

    public Instant getTakenAt() {
        return takenAt;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Instant getRemovedAt() {
        return removedAt;
    }

    /** Hora do envio ({@code created_at}). */
    public Instant getUploadedAt() {
        return getCreatedAt();
    }
}
