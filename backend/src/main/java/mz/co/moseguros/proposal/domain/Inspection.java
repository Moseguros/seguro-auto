package mz.co.moseguros.proposal.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Vistoria de danos próprios ({@code proposal.inspection}, S4-03 §1, §3 e §4): uma por proposta com
 * {@code inspection_required}. As fotografias são linhas próprias ({@link InspectionPhoto}); os métodos
 * que precisam delas recebem as activas. As transições validam-se aqui; as regras da proposta
 * (editável, {@code UNDER_REVIEW}) verifica-as o serviço. Não se apaga.
 */
@Entity
@Table(schema = "proposal", name = "inspection")
public class Inspection extends BaseEntity {

    public static final int ODOMETER_MAX = 2_000_000;
    public static final int CUSTOMER_NOTES_MIN = 10;
    public static final int CUSTOMER_NOTES_MAX = 500;
    public static final int REVIEW_NOTES_MAX = 1000;
    public static final int REJECTION_REASON_MIN = 10;
    public static final int REJECTION_REASON_MAX = 1000;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "proposal_id", nullable = false, updatable = false)
    private UUID proposalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 12)
    private InspectionMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private InspectionStatus status;

    @Column(name = "odometer_km")
    private Integer odometerKm;

    @Column(name = "customer_notes", length = CUSTOMER_NOTES_MAX)
    private String customerNotes;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_notes", length = REVIEW_NOTES_MAX)
    private String reviewNotes;

    @Column(name = "rejection_reason", length = REJECTION_REASON_MAX)
    private String rejectionReason;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "retake_slots", columnDefinition = "text[]")
    private List<String> retakeSlots;

    protected Inspection() {
    }

    /** Vistoria {@code PENDING}/{@code SELF_PHOTOS} da proposta (regra 1). */
    public static Inspection pending(Proposal proposal) {
        if (!proposal.isInspectionRequired()) {
            throw new IllegalArgumentException("The proposal does not require an inspection");
        }
        Inspection i = new Inspection();
        i.insurerId = proposal.getInsurerId();
        i.proposalId = proposal.getId();
        i.mode = InspectionMode.SELF_PHOTOS;
        i.status = InspectionStatus.PENDING;
        return i;
    }

    // --- Regras ---

    /** @throws DomainException 409 {@code INSPECTION_NOT_EDITABLE} fora de {@code PENDING}/{@code REJECTED} */
    public void requireEditable() {
        if (!status.isEditable()) {
            throw DomainException.of(ErrorCodes.INSPECTION_NOT_EDITABLE,
                    "The inspection can no longer be changed (status " + status + ")");
        }
    }

    /** @throws DomainException 409 {@code CONCURRENT_MODIFICATION} se a versão lida não for a gravada */
    public void requireVersion(long expected) {
        if (getVersion() != expected) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The inspection was changed by another request (version " + getVersion() + ")");
        }
    }

    /** Ângulos obrigatórios sem fotografia activa, pela ordem de {@link PhotoSlot}; vazio em {@code ON_SITE}. */
    public List<PhotoSlot> missingSlots(Collection<InspectionPhoto> active) {
        return mode == InspectionMode.ON_SITE ? List.of() : missingRequiredSlots(active);
    }

    /** Ângulos obrigatórios sem fotografia activa, seja qual for o modo. */
    public static List<PhotoSlot> missingRequiredSlots(Collection<InspectionPhoto> active) {
        Set<PhotoSlot> present = EnumSet.noneOf(PhotoSlot.class);
        active.stream().filter(InspectionPhoto::isActive).forEach(p -> present.add(p.getSlot()));
        return PhotoSlot.REQUIRED.stream().filter(s -> !present.contains(s)).toList();
    }

    /**
     * Pode receber uma fotografia neste ângulo: editável (409 {@code INSPECTION_NOT_EDITABLE}) e, em
     * {@code EXTRA}, menos de {@code maxExtras} activas (400, campo {@code slot}, {@code EXTRA_PHOTOS_LIMIT}).
     */
    public void requireCanAdd(Collection<InspectionPhoto> active, PhotoSlot slot, int maxExtras) {
        requireEditable();
        if (slot == PhotoSlot.EXTRA) {
            long extras = active.stream().filter(p -> p.isActive() && p.getSlot() == PhotoSlot.EXTRA).count();
            if (extras >= maxExtras) {
                throw new FieldValidationException("slot", FieldErrorCodes.EXTRA_PHOTOS_LIMIT,
                        "At most " + maxExtras + " extra photos");
            }
        }
    }

    /**
     * Fotografia nova e a que ela substitui (o mesmo ângulo fixo), já marcada com {@code removed_at}.
     * Quem grava escreve primeiro a substituída (o índice {@code ux_photo_slot_active} só admite uma
     * activa por ângulo) e só depois insere a nova.
     */
    public record PhotoAddition(InspectionPhoto added, Optional<InspectionPhoto> replaced) {
    }

    /**
     * Acrescenta uma fotografia (regras 2, 4 e 5): numa vistoria {@code REJECTED} passa a
     * {@code PENDING}; num ângulo fixo com fotografia, a anterior fica retirada.
     */
    public PhotoAddition addPhoto(Collection<InspectionPhoto> active, InspectionPhoto.NewPhoto photo, Instant now,
                                  int maxExtras) {
        requireCanAdd(active, photo.slot(), maxExtras);
        Optional<InspectionPhoto> replaced = Optional.empty();
        if (photo.slot().isFixed()) {
            replaced = active.stream().filter(p -> p.isActive() && p.getSlot() == photo.slot()).findFirst();
            replaced.ifPresent(p -> p.retire(now));
        }
        if (status == InspectionStatus.REJECTED) {
            moveTo(InspectionStatus.PENDING);
            // Só REJECTED tem motivo (check da tabela): o motivo fica na auditoria; a data da revisão e
            // os ângulos a repetir ficam até ao envio seguinte, para o cliente saber o que refazer.
            rejectionReason = null;
        }
        return new PhotoAddition(InspectionPhoto.of(this, photo), replaced);
    }

    /** Retira uma fotografia activa desta vistoria (editável). */
    public void removePhoto(InspectionPhoto photo, Instant now) {
        requireEditable();
        if (!photo.getInspectionId().equals(getId()) || !photo.isActive()) {
            throw DomainException.of(ErrorCodes.PHOTO_NOT_FOUND, "Photo not found");
        }
        photo.retire(now);
    }

    /**
     * {@code PENDING → SUBMITTED} com a quilometragem; faltam ângulos → 409
     * {@code INSPECTION_PHOTOS_MISSING} ({@code missingSlots}).
     */
    public void submit(Collection<InspectionPhoto> active, int odometerKm, Instant now) {
        requireEditable();
        if (odometerKm < 0 || odometerKm > ODOMETER_MAX) {
            throw new IllegalArgumentException("odometerKm out of range");
        }
        if (status != InspectionStatus.PENDING) {
            throw invalidTransition(InspectionStatus.SUBMITTED);
        }
        List<PhotoSlot> missing = missingRequiredSlots(active);
        if (!missing.isEmpty()) {
            throw photosMissing(missing);
        }
        moveTo(InspectionStatus.SUBMITTED);
        this.mode = InspectionMode.SELF_PHOTOS;
        this.odometerKm = odometerKm;
        this.submittedAt = now;
        clearReview();
    }

    /** {@code PENDING}/{@code REJECTED} → {@code SUBMITTED} em {@code ON_SITE}, com o local e as horas preferidas. */
    public void requestOnSite(String notes, Instant now) {
        requireEditable();
        Objects.requireNonNull(notes, "notes");
        moveTo(InspectionStatus.SUBMITTED);
        this.mode = InspectionMode.ON_SITE;
        this.customerNotes = notes;
        this.submittedAt = now;
        clearReview();
    }

    /**
     * {@code SUBMITTED → APPROVED} pelo subscritor. {@code mode} é o modo efectivo da aprovação: em
     * {@code SELF_PHOTOS} exige os seis ângulos (409 {@code INSPECTION_PHOTOS_MISSING}).
     */
    public void approve(Collection<InspectionPhoto> active, InspectionMode mode, String notes, UUID reviewer,
                        Instant now) {
        requireTransition(InspectionStatus.APPROVED);
        Objects.requireNonNull(mode, "mode");
        if (mode == InspectionMode.SELF_PHOTOS) {
            List<PhotoSlot> missing = missingRequiredSlots(active);
            if (!missing.isEmpty()) {
                throw photosMissing(missing);
            }
        }
        moveTo(InspectionStatus.APPROVED);
        this.mode = mode;
        review(reviewer, now);
        this.reviewNotes = notes;
        this.rejectionReason = null;
        this.retakeSlots = null;
    }

    /** {@code SUBMITTED → REJECTED} pelo subscritor, com o motivo e os ângulos a repetir. */
    public void reject(String reason, List<PhotoSlot> retake, UUID reviewer, Instant now) {
        requireTransition(InspectionStatus.REJECTED);
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A rejection needs a reason");
        }
        moveTo(InspectionStatus.REJECTED);
        review(reviewer, now);
        this.reviewNotes = null;
        this.rejectionReason = reason;
        this.retakeSlots = retake == null || retake.isEmpty() ? null
                : new ArrayList<>(retake.stream().map(Enum::name).toList());
    }

    /**
     * {@code SUBMITTED}/{@code APPROVED} → {@code PENDING}: a seguradora pede informação com o item
     * {@code INSPECTION} ({@code requestProposalInfo}, S4-05). Limpa a revisão anterior (fica na auditoria).
     */
    public void reopen() {
        if (status != InspectionStatus.SUBMITTED && status != InspectionStatus.APPROVED) {
            throw invalidTransition(InspectionStatus.PENDING);
        }
        moveTo(InspectionStatus.PENDING);
        clearReview();
    }

    /** Um envio novo espera por uma revisão nova: a anterior fica só na auditoria. */
    private void clearReview() {
        this.reviewedBy = null;
        this.reviewedAt = null;
        this.reviewNotes = null;
        this.rejectionReason = null;
        this.retakeSlots = null;
    }

    private void review(UUID reviewer, Instant now) {
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer");
        this.reviewedAt = Objects.requireNonNull(now, "now");
    }

    private void requireTransition(InspectionStatus target) {
        if (status != InspectionStatus.SUBMITTED) {
            throw invalidTransition(target);
        }
    }

    private void moveTo(InspectionStatus target) {
        if (!status.canTransitionTo(target)) {
            throw invalidTransition(target);
        }
        this.status = target;
    }

    private DomainException invalidTransition(InspectionStatus target) {
        return DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                "Cannot change an inspection from " + status + " to " + target);
    }

    private static DomainException photosMissing(List<PhotoSlot> missing) {
        return DomainException.of(ErrorCodes.INSPECTION_PHOTOS_MISSING, "Required photos are missing: " + missing)
                .withProperty("missingSlots", missing.stream().map(Enum::name).toList());
    }

    // --- Leitura ---

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getProposalId() {
        return proposalId;
    }

    public InspectionMode getMode() {
        return mode;
    }

    public InspectionStatus getStatus() {
        return status;
    }

    public Integer getOdometerKm() {
        return odometerKm;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    /** Ângulos a repetir pedidos na última recusa; vazio sem recusa. */
    public List<PhotoSlot> getRetakeSlots() {
        return retakeSlots == null ? List.of() : retakeSlots.stream().map(PhotoSlot::valueOf).toList();
    }
}
