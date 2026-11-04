package mz.co.moseguros.proposal.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionPhoto;
import mz.co.moseguros.proposal.domain.InspectionPhotoRepository;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.PhotoSlot;
import mz.co.moseguros.proposal.domain.PhotoSource;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Vistoria da proposta pelo cliente dono ({@code self:proposals}) ou pelo mediador da proposta
 * ({@code proposal:create-for-client}, S4-06): fotografias por ângulo, submissão e pedido de vistoria
 * presencial (S4-03 §3 a §5). Corre no âmbito de quem pede; o acesso à proposta é o do
 * {@link ProposalQueryService#accessible}. Cada escrita conta como alteração de conteúdo da proposta
 * ({@code content_revision + 1}, {@code expires_at} renovado).
 *
 * <p>Ordem das verificações: proposta (404), vistoria exigida (409 {@code INSPECTION_NOT_REQUIRED}),
 * proposta editável (409 {@code PROPOSAL_NOT_EDITABLE}), vistoria editável (409
 * {@code INSPECTION_NOT_EDITABLE}), {@code version} (409), corpo (400) e por fim as regras do estado.
 */
@Service
public class InspectionService {

    /** Tipo de dono dos ficheiros no {@code document} ({@link InspectionFileAccessPolicy}). */
    public static final String OWNER_TYPE = "INSPECTION";
    /** Fotografias: nunca PDF. */
    public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/heic");
    static final String PURPOSE_PREFIX = "INSPECTION_";

    private static final Logger log = LoggerFactory.getLogger(InspectionService.class);

    private final ProposalQueryService queries;
    private final ProposalRepository proposals;
    private final InspectionRepository inspections;
    private final InspectionPhotoRepository photos;
    private final DocumentApi documents;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties proposalProperties;
    private final InspectionProperties properties;
    private final Clock clock;

    public InspectionService(ProposalQueryService queries, ProposalRepository proposals,
                             InspectionRepository inspections, InspectionPhotoRepository photos,
                             DocumentApi documents, AuditService audit, CurrentUser currentUser,
                             ProposalProperties proposalProperties, InspectionProperties properties, Clock clock) {
        this.queries = queries;
        this.proposals = proposals;
        this.inspections = inspections;
        this.photos = photos;
        this.documents = documents;
        this.audit = audit;
        this.currentUser = currentUser;
        this.proposalProperties = proposalProperties;
        this.properties = properties;
        this.clock = clock;
    }

    /** Ficheiro da parte {@code file}; {@code null} se faltar. */
    public record Upload(byte[] content, String originalFilename, String contentType) {
    }

    /** As outras partes do pedido, em texto (as que faltam vêm a {@code null}). */
    public record PhotoInput(String slot, String clientPhotoId, String source, String takenAt, String latitude,
                             String longitude) {
    }

    /**
     * Fotografia gravada.
     *
     * @param created {@code false} se o {@code clientPhotoId} já existia (repetição: 200 e nada gravado)
     */
    public record PhotoResult(InspectionPhoto photo, boolean created) {
    }

    /**
     * {@code uploadInspectionPhoto}: guarda o ficheiro ({@code DocumentApi.store}, dono
     * {@code INSPECTION}) e a fotografia, e liga o ficheiro na mesma transacção. Uma repetição com o
     * mesmo {@code clientPhotoId} devolve a fotografia já gravada, sem guardar nada (a app repete o
     * envio quando perde a resposta). Auditoria {@code INSPECTION.PHOTO_ADD}.
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public PhotoResult upload(UUID proposalId, Upload file, PhotoInput input) {
        Proposal proposal = queries.accessible(proposalId);
        Inspection inspection = inspectionOf(proposal);
        UUID clientPhotoId = parseUuid(input.clientPhotoId());
        if (clientPhotoId != null) {
            Optional<InspectionPhoto> existing = photos.findByInspectionIdAndClientPhotoId(inspection.getId(),
                    clientPhotoId);
            if (existing.isPresent()) {
                return new PhotoResult(existing.get(), false);
            }
        }
        proposal.requireEditable();
        inspection.requireEditable();
        ValidPhoto valid = validate(file, input, clientPhotoId);

        Instant now = clock.instant();
        List<InspectionPhoto> active = photos.findActive(inspection.getId());
        inspection.requireCanAdd(active, valid.slot(), properties.maxExtraPhotos());
        FileRef ref = documents.store(new StoreFileCommand(OWNER_TYPE, inspection.getId(),
                PURPOSE_PREFIX + valid.slot().name(), FileClassification.PERSONAL, inspection.getInsurerId(),
                file.originalFilename(), file.contentType(), ALLOWED_CONTENT_TYPES, file.content()));
        Inspection.PhotoAddition addition = inspection.addPhoto(active, new InspectionPhoto.NewPhoto(valid.slot(),
                ref.id(), clientPhotoId, valid.source(), valid.takenAt(), valid.latitude(), valid.longitude()), now,
                properties.maxExtraPhotos());
        try {
            // A substituída primeiro: ux_photo_slot_active só admite uma activa por ângulo fixo.
            addition.replaced().ifPresent(photos::saveAndFlush);
            photos.saveAndFlush(addition.added());
        } catch (DataIntegrityViolationException e) {
            // Dois envios em simultâneo para o mesmo ângulo fixo ou com o mesmo clientPhotoId.
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The inspection was changed by another request");
        }
        documents.markLinked(ref.id());
        inspections.saveAndFlush(inspection);
        touch(proposal, now);

        InspectionPhoto photo = addition.added();
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("photoId", photo.getId());
        after.put("slot", photo.getSlot().name());
        after.put("fileId", photo.getFileId());
        after.put("source", photo.getSource().name());
        addition.replaced().ifPresent(r -> after.put("replacedPhotoId", r.getId()));
        after.put("status", inspection.getStatus().name());
        record(proposal, inspection, "INSPECTION.PHOTO_ADD", null, after);
        return new PhotoResult(photo, true);
    }

    /** {@code removeInspectionPhoto}: marca {@code removed_at}. Auditoria {@code INSPECTION.PHOTO_REMOVE}. */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public void remove(UUID proposalId, UUID photoId) {
        Proposal proposal = queries.accessible(proposalId);
        Inspection inspection = inspectionOf(proposal);
        proposal.requireEditable();
        inspection.requireEditable();
        InspectionPhoto photo = photoId == null ? null : photos.findById(photoId)
                .filter(p -> p.getInspectionId().equals(inspection.getId()) && p.isActive()).orElse(null);
        if (photo == null) {
            throw DomainException.of(ErrorCodes.PHOTO_NOT_FOUND, "Photo not found");
        }
        Instant now = clock.instant();
        inspection.removePhoto(photo, now);
        photos.saveAndFlush(photo);
        touch(proposal, now);
        record(proposal, inspection, "INSPECTION.PHOTO_REMOVE",
                Map.of("photoId", photo.getId(), "slot", photo.getSlot().name(), "fileId", photo.getFileId()),
                Map.of("removedAt", photo.getRemovedAt()));
    }

    /**
     * {@code submitInspection}: {@code PENDING → SUBMITTED} com a quilometragem; faltam ângulos → 409
     * {@code INSPECTION_PHOTOS_MISSING}. Auditoria {@code INSPECTION.SUBMIT}.
     *
     * @param version versão da proposta
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails submit(UUID proposalId, Integer odometerKm, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        Inspection inspection = inspectionOf(proposal);
        proposal.requireEditable();
        inspection.requireEditable();
        DeclarationService.requireVersion(proposal, version);
        FieldErrors errors = new FieldErrors();
        if (odometerKm == null) {
            errors.add("odometerKm", FieldErrorCodes.REQUIRED, "The odometer reading is required");
        } else if (odometerKm < 0 || odometerKm > Inspection.ODOMETER_MAX) {
            errors.add("odometerKm", FieldErrorCodes.OUT_OF_RANGE, "Between 0 and " + Inspection.ODOMETER_MAX);
        }
        errors.throwIfAny();
        Map<String, Object> before = state(inspection);
        Instant now = clock.instant();
        inspection.submit(photos.findActive(inspection.getId()), odometerKm, now);
        inspections.saveAndFlush(inspection);
        touch(proposal, now);
        record(proposal, inspection, "INSPECTION.SUBMIT", before, state(inspection));
        log.info("Inspection {} of proposal {} submitted", inspection.getId(), proposal.getNumber());
        return queries.details(proposal);
    }

    /**
     * {@code requestOnSiteInspection}: {@code PENDING}/{@code REJECTED} → {@code SUBMITTED} em
     * {@code ON_SITE}; as fotografias já enviadas ficam. Auditoria {@code INSPECTION.REQUEST_ON_SITE}.
     *
     * @param notes   local e horas preferidas (10–500 caracteres)
     * @param version versão da proposta
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails requestOnSite(UUID proposalId, String notes, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        Inspection inspection = inspectionOf(proposal);
        proposal.requireEditable();
        inspection.requireEditable();
        DeclarationService.requireVersion(proposal, version);
        String text = notes == null ? null : notes.strip();
        FieldErrors errors = new FieldErrors();
        if (text == null || text.isEmpty()) {
            errors.add("notes", FieldErrorCodes.REQUIRED, "Say where and when the inspection can be done");
        } else if (text.length() < Inspection.CUSTOMER_NOTES_MIN) {
            errors.add("notes", FieldErrorCodes.TOO_SHORT, "At least " + Inspection.CUSTOMER_NOTES_MIN + " characters");
        } else if (text.length() > Inspection.CUSTOMER_NOTES_MAX) {
            errors.add("notes", FieldErrorCodes.TOO_LONG, "At most " + Inspection.CUSTOMER_NOTES_MAX + " characters");
        }
        errors.throwIfAny();
        Map<String, Object> before = state(inspection);
        Instant now = clock.instant();
        inspection.requestOnSite(text, now);
        inspections.saveAndFlush(inspection);
        touch(proposal, now);
        record(proposal, inspection, "INSPECTION.REQUEST_ON_SITE", before, state(inspection));
        return queries.details(proposal);
    }

    /** @throws DomainException 409 {@code INSPECTION_NOT_REQUIRED} se a proposta não tiver vistoria */
    Inspection inspectionOf(Proposal proposal) {
        if (!proposal.isInspectionRequired()) {
            throw notRequired();
        }
        return inspections.findByProposalId(proposal.getId()).orElseThrow(InspectionService::notRequired);
    }

    private void touch(Proposal proposal, Instant now) {
        proposal.touchContent(now, proposalProperties.expiry());
        proposals.saveAndFlush(proposal);
    }

    private record ValidPhoto(PhotoSlot slot, PhotoSource source, Instant takenAt, BigDecimal latitude,
                              BigDecimal longitude) {
    }

    /** Partes do pedido (S4-03 §5); erros todos juntos em 400 {@code VALIDATION_ERROR}. */
    private ValidPhoto validate(Upload file, PhotoInput in, UUID clientPhotoId) {
        FieldErrors errors = new FieldErrors();
        if (file == null) {
            errors.add("file", FieldErrorCodes.REQUIRED, "A file is required");
        }
        PhotoSlot slot = null;
        if (blank(in.slot())) {
            errors.add("slot", FieldErrorCodes.REQUIRED, "The angle is required");
        } else {
            slot = PhotoSlot.parse(in.slot()).orElse(null);
            if (slot == null) {
                errors.add("slot", FieldErrorCodes.INVALID_SLOT, "Unknown angle");
            }
        }
        if (!blank(in.clientPhotoId()) && clientPhotoId == null) {
            errors.add("clientPhotoId", FieldErrorCodes.INVALID_VALUE, "Not a UUID");
        }
        PhotoSource source = PhotoSource.UPLOAD;
        if (!blank(in.source())) {
            source = parseSource(in.source());
            if (source == null) {
                errors.add("source", FieldErrorCodes.INVALID_VALUE, "CAMERA or UPLOAD");
            }
        }
        Instant takenAt = null;
        if (!blank(in.takenAt())) {
            takenAt = parseInstant(in.takenAt());
            Instant now = clock.instant();
            if (takenAt == null) {
                errors.add("takenAt", FieldErrorCodes.INVALID_DATE, "ISO 8601 date and time");
            } else if (takenAt.isAfter(now.plus(InspectionProperties.FUTURE_TOLERANCE))) {
                errors.add("takenAt", FieldErrorCodes.PHOTO_IN_FUTURE, "The photo date is in the future");
            } else if (takenAt.isBefore(now.minus(properties.maxPhotoAge()))) {
                errors.add("takenAt", FieldErrorCodes.PHOTO_TOO_OLD,
                        "The photo is older than " + properties.maxPhotoAgeDays() + " days");
            }
        }
        BigDecimal latitude = null;
        BigDecimal longitude = null;
        boolean hasLat = !blank(in.latitude());
        boolean hasLon = !blank(in.longitude());
        if (hasLat != hasLon) {
            errors.add(hasLat ? "longitude" : "latitude", FieldErrorCodes.INVALID_COORDINATES,
                    "Send both coordinates or none");
        } else if (hasLat) {
            latitude = parseDecimal(in.latitude());
            longitude = parseDecimal(in.longitude());
            if (latitude == null || latitude.abs().compareTo(BigDecimal.valueOf(90)) > 0) {
                errors.add("latitude", FieldErrorCodes.INVALID_COORDINATES, "Between -90 and 90");
            }
            if (longitude == null || longitude.abs().compareTo(BigDecimal.valueOf(180)) > 0) {
                errors.add("longitude", FieldErrorCodes.INVALID_COORDINATES, "Between -180 and 180");
            }
        }
        errors.throwIfAny();
        return new ValidPhoto(slot, source, takenAt, latitude, longitude);
    }

    private static Map<String, Object> state(Inspection i) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("status", i.getStatus().name());
        state.put("mode", i.getMode().name());
        state.put("odometerKm", i.getOdometerKm());
        state.put("submittedAt", i.getSubmittedAt());
        return state;
    }

    private void record(Proposal proposal, Inspection inspection, String action, Object before, Object after) {
        ActorType actor = currentUser.isCustomer() ? ActorType.CUSTOMER : ActorType.STAFF;
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("proposalId", proposal.getId());
        state.put("contentRevision", proposal.getContentRevision());
        if (after instanceof Map<?, ?> map) {
            map.forEach((k, v) -> state.put(String.valueOf(k), v));
        }
        audit.record(new AuditRecord(actor, currentUser.id().toString(), inspection.getInsurerId(), action,
                "Inspection", inspection.getId().toString(), before, state));
    }

    private static DomainException notRequired() {
        return DomainException.of(ErrorCodes.INSPECTION_NOT_REQUIRED, "This proposal does not need an inspection");
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static UUID parseUuid(String value) {
        if (blank(value)) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static PhotoSource parseSource(String value) {
        try {
            return PhotoSource.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Instant parseInstant(String value) {
        try {
            return OffsetDateTime.parse(value.trim()).toInstant();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
