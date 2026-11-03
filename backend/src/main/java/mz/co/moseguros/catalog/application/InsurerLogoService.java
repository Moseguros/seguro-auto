package mz.co.moseguros.catalog.application;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logótipo da seguradora do utilizador (S2-04 §7.1, decisão D27). O ficheiro guarda-se no
 * {@code document} (PNG ou JPEG até 1 MB, público) e o {@code organization} só guarda o id
 * ({@code insurer_profile.logo_file_id}). O ficheiro anterior fica guardado.
 */
@Service
public class InsurerLogoService {

    public static final String OWNER_TYPE = "INSURER_LOGO";
    public static final long MAX_SIZE_BYTES = 1_048_576;
    static final Set<String> CONTENT_TYPES = Set.of("image/png", "image/jpeg");

    private final OrganizationApi organizations;
    private final DocumentApi documents;
    private final AuditService audit;
    private final CurrentUser currentUser;

    public InsurerLogoService(OrganizationApi organizations, DocumentApi documents, AuditService audit,
                              CurrentUser currentUser) {
        this.organizations = organizations;
        this.documents = documents;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    /** Ficheiro do logótipo actual, ou {@code null}. */
    @PreAuthorize("hasAuthority('product:read')")
    @Transactional(readOnly = true)
    public UUID logoFileId() {
        return organizations.insurerLogoFileId(TenantContext.insurerId()).orElse(null);
    }

    /**
     * Guarda o novo logótipo.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} ({@code file}/{@code OUT_OF_RANGE}) acima de
     *                         1 MB, e os erros de ficheiro do {@code DocumentApi} ({@code FILE_*})
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public UUID upload(byte[] content, String originalFilename, String contentType) {
        UUID insurerId = TenantContext.insurerId();
        if (content.length > MAX_SIZE_BYTES) {
            throw new FieldValidationException("file", FieldErrorCodes.OUT_OF_RANGE, "The logo is larger than 1 MB");
        }
        UUID before = organizations.insurerLogoFileId(insurerId).orElse(null);
        FileRef ref = documents.store(new StoreFileCommand(OWNER_TYPE, insurerId, "LOGO", FileClassification.PUBLIC,
                insurerId, originalFilename, contentType, CONTENT_TYPES, content));
        organizations.setInsurerLogo(insurerId, ref.id());
        documents.markLinked(ref.id());
        record(insurerId, before, ref.id());
        return ref.id();
    }

    /** Retira o logótipo (idempotente). */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public void remove() {
        UUID insurerId = TenantContext.insurerId();
        UUID before = organizations.insurerLogoFileId(insurerId).orElse(null);
        if (before == null) {
            return;
        }
        organizations.setInsurerLogo(insurerId, null);
        record(insurerId, before, null);
    }

    private void record(UUID insurerId, UUID before, UUID after) {
        if (Objects.equals(before, after)) {
            return;
        }
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), insurerId,
                "INSURER_PROFILE.LOGO_UPDATE", "InsurerProfile", insurerId.toString(), new LogoSnapshot(before),
                new LogoSnapshot(after)));
    }

    /** Estado auditado ({@code logoFileId} vai sempre, também a {@code null}). */
    public record LogoSnapshot(@JsonInclude(JsonInclude.Include.ALWAYS) UUID logoFileId) {
    }
}
