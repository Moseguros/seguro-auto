package mz.co.moseguros.organization.application;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.organization.domain.InsurerCharges;
import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.InsurerProfileRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encargos (impostos e taxas) da seguradora do utilizador (S3-01 §4). Só em âmbito {@code ORG} de
 * uma seguradora (outro âmbito → 403 {@code FORBIDDEN}); o RLS de {@code insurer_profile} mostra
 * só a própria. O modo de tarifação não se altera aqui (é da plataforma, S3-02).
 */
@Service
public class InsurerRatingSettingsService {

    private final InsurerProfileRepository profiles;
    private final AuditService audit;
    private final CurrentUser currentUser;

    public InsurerRatingSettingsService(InsurerProfileRepository profiles, AuditService audit,
                                        CurrentUser currentUser) {
        this.profiles = profiles;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    /** Modo de tarifação, encargos (pela ordem) e {@code version} do perfil. */
    public record RatingSettings(RatingMode ratingMode, List<ChargeRule> charges, long version) {
    }

    @PreAuthorize("hasAnyAuthority('rating:manage','product:read')")
    @Transactional(readOnly = true)
    public RatingSettings get() {
        return settings(load());
    }

    /**
     * Substitui os encargos.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} com {@code errors[]} ({@code field} =
     *                         ponteiro JSON dentro de {@code charges}), ou 409
     *                         {@code CONCURRENT_MODIFICATION}
     */
    @PreAuthorize("hasAuthority('rating:manage')")
    @Transactional
    public RatingSettings update(JsonNode charges, long expectedVersion) {
        InsurerProfile profile = load();
        List<FieldError> errors = InsurerCharges.validate(charges);
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        if (profile.getVersion() != expectedVersion) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The insurer profile was changed by another request");
        }
        JsonNode before = profile.getCharges();
        profile.replaceCharges(charges);
        profiles.flush();
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), profile.getOrganizationId(),
                "INSURER_CHARGES.UPDATE", "InsurerProfile", profile.getId().toString(), before,
                profile.getCharges()));
        return settings(profile);
    }

    private InsurerProfile load() {
        TenantSnapshot tenant = TenantContext.current();
        UUID insurerId = tenant.insurerId();
        if (tenant.scope() != Scope.ORG || insurerId == null) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only insurer staff can manage rating settings");
        }
        return profiles.findByOrganizationId(insurerId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.FORBIDDEN, "Insurer profile not visible"));
    }

    private static RatingSettings settings(InsurerProfile profile) {
        return new RatingSettings(profile.getRatingMode(), InsurerCharges.parse(profile.getCharges()),
                profile.getVersion());
    }
}
