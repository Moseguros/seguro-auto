package mz.co.moseguros.catalog.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import mz.co.moseguros.catalog.domain.PlateFormatEntity;
import mz.co.moseguros.catalog.domain.PlateFormatRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Formatos de matrícula (S2-02 §4 e §5): lista pública dos activos e gestão pelo administrador da
 * plataforma ({@code reference:manage}). Cada escrita publica {@link PlateFormatsChanged} para o
 * {@link DbPlateFormatProvider} recarregar depois do commit.
 */
@Service
public class PlateFormatService {

    private static final Logger log = LoggerFactory.getLogger(PlateFormatService.class);

    private final PlateFormatRepository formats;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ApplicationEventPublisher events;

    public PlateFormatService(PlateFormatRepository formats, AuditService audit, CurrentUser currentUser,
                              ApplicationEventPublisher events) {
        this.formats = formats;
        this.audit = audit;
        this.currentUser = currentUser;
        this.events = events;
    }

    /** Formatos activos, por ordem de teste (público). */
    @Transactional(readOnly = true)
    public List<PlateFormatEntity> listActive() {
        return formats.findByActiveTrueOrderBySortOrderAscCodeAsc();
    }

    /** Todos os formatos, por ordem de teste. */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional(readOnly = true)
    public List<PlateFormatEntity> list() {
        return formats.findAllByOrderBySortOrderAscCodeAsc();
    }

    /**
     * Cria um formato activo.
     *
     * @throws DomainException {@code VALIDATION_ERROR} (regras do {@link PlateFormatValidator}) ou
     *                         {@code PLATE_FORMAT_CODE_ALREADY_EXISTS} (409)
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public PlateFormatEntity create(Data data) {
        PlateFormatValidator.validate(data.code(), data.label(), data.pattern(), data.displayTemplate(),
                data.example(), data.sortOrder());
        if (formats.existsByCode(data.code())) {
            throw DomainException.of(ErrorCodes.PLATE_FORMAT_CODE_ALREADY_EXISTS, "Plate format code already exists");
        }
        PlateFormatEntity format = formats.save(PlateFormatEntity.create(data.code(), data.label().trim(),
                data.pattern(), data.displayTemplate(), data.example(), data.sortOrder()));
        formats.flush();
        changed("PLATE_FORMAT.CREATE", format, null);
        log.info("Plate format {} created", format.getCode());
        return format;
    }

    /**
     * Altera um formato (o código não muda; o de {@code data} é ignorado).
     *
     * @param version versão lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public PlateFormatEntity update(UUID id, Data data, long version) {
        PlateFormatEntity format = find(id);
        if (format.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION, "The plate format was changed by another request");
        }
        PlateFormatValidator.validate(null, data.label(), data.pattern(), data.displayTemplate(), data.example(),
                data.sortOrder());
        Snapshot before = Snapshot.of(format);
        format.update(data.label().trim(), data.pattern(), data.displayTemplate(), data.example(), data.sortOrder());
        formats.flush();
        changed("PLATE_FORMAT.UPDATE", format, before);
        return format;
    }

    /** @throws DomainException {@code INVALID_STATE_TRANSITION} (409) se já estiver activo */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public PlateFormatEntity activate(UUID id) {
        PlateFormatEntity format = find(id);
        Snapshot before = Snapshot.of(format);
        format.activate();
        formats.flush();
        changed("PLATE_FORMAT.ACTIVATE", format, before);
        return format;
    }

    /**
     * @throws DomainException {@code INVALID_STATE_TRANSITION} (409) se já estiver inactivo, ou
     *                         {@code LAST_ACTIVE_PLATE_FORMAT} (409) se for o último activo
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public PlateFormatEntity deactivate(UUID id) {
        List<PlateFormatEntity> active = formats.lockActive();
        PlateFormatEntity format = find(id);
        Snapshot before = Snapshot.of(format);
        format.deactivate();
        if (active.size() <= 1) {
            throw DomainException.of(ErrorCodes.LAST_ACTIVE_PLATE_FORMAT,
                    "At least one plate format must stay active");
        }
        formats.flush();
        changed("PLATE_FORMAT.DEACTIVATE", format, before);
        return format;
    }

    /**
     * Experimenta um padrão e um modelo com um valor, sem gravar. As regras de {@code pattern} e
     * {@code displayTemplate} aplicam-se ({@code VALIDATION_ERROR}).
     *
     * @return o valor formatado, ou vazio se não corresponder
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    public Optional<String> test(String pattern, String displayTemplate, String value) {
        Pattern compiled = PlateFormatValidator.validateExpression(pattern, displayTemplate);
        return PlateFormatValidator.render(compiled, displayTemplate, value == null ? "" : value);
    }

    private PlateFormatEntity find(UUID id) {
        return formats.findById(id).orElseThrow(() -> DomainException.of(ErrorCodes.PLATE_FORMAT_NOT_FOUND,
                "Plate format not found"));
    }

    private void changed(String action, PlateFormatEntity format, Snapshot before) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), null, action, "PlateFormat",
                format.getId().toString(), before, Snapshot.of(format)));
        events.publishEvent(new PlateFormatsChanged(format.getCode()));
    }

    /** Campos de criação e alteração. */
    public record Data(String code, String label, String pattern, String displayTemplate, String example,
                       Integer sortOrder) {
    }

    /** Estado auditado (antes e depois). */
    public record Snapshot(String code, String label, String pattern, String displayTemplate, String example,
                           int sortOrder, boolean active, long version, Instant updatedAt) {

        static Snapshot of(PlateFormatEntity f) {
            return new Snapshot(f.getCode(), f.getLabel(), f.getPattern(), f.getDisplayTemplate(), f.getExample(),
                    f.getSortOrder(), f.isActive(), f.getVersion(), f.getUpdatedAt());
        }
    }
}
