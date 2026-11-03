package mz.co.moseguros.organization.application;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.InsurerProfileRepository;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ligação da plataforma à tarifação de uma seguradora (S3-02 §9): modo ({@code INTERNAL} ou
 * {@code EXTERNAL}), endereço do serviço externo e referência do segredo do HMAC (nunca o segredo).
 * Só o administrador da plataforma ({@code organization:manage}); o ecrã fica para mais tarde.
 */
@Service
public class RatingIntegrationService {

    static final int URL_MAX = 500;
    /** {@code config:<chave>} (S3-02 §8); o S5 acrescenta {@code secret/…}. */
    static final Pattern SECRET_REF = Pattern.compile("^config:[a-z0-9-]{1,40}$");

    private final OrganizationRepository organizations;
    private final InsurerProfileRepository profiles;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final boolean allowHttp;

    public RatingIntegrationService(OrganizationRepository organizations, InsurerProfileRepository profiles,
                                    AuditService audit, CurrentUser currentUser,
                                    @Value("${moseguros.rating.external.allow-http:false}") boolean allowHttp) {
        this.organizations = organizations;
        this.profiles = profiles;
        this.audit = audit;
        this.currentUser = currentUser;
        this.allowHttp = allowHttp;
    }

    /** Ligação gravada e {@code version} do perfil da seguradora. */
    public record RatingIntegration(RatingMode ratingMode, String ratingEndpointUrl, String ratingSecretRef,
                                    long version) {
    }

    /** @throws DomainException 404 {@code ORGANIZATION_NOT_FOUND} ou 400 {@code ORGANIZATION_NOT_INSURER} */
    @PreAuthorize("hasAuthority('organization:manage')")
    @Transactional(readOnly = true)
    public RatingIntegration get(UUID organizationId) {
        return view(load(organizationId));
    }

    /**
     * Altera a ligação. Em {@code EXTERNAL} o endereço e a referência são obrigatórios; em
     * {@code INTERNAL} os que faltarem mantêm o valor gravado.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} ({@code REQUIRED}, {@code INVALID_URL},
     *                         {@code INVALID_SECRET_REF}), 404 {@code ORGANIZATION_NOT_FOUND}, 400
     *                         {@code ORGANIZATION_NOT_INSURER} ou 409 {@code CONCURRENT_MODIFICATION}
     */
    @PreAuthorize("hasAuthority('organization:manage')")
    @Transactional
    public RatingIntegration update(UUID organizationId, RatingMode mode, String endpointUrl, String secretRef,
                                    long expectedVersion) {
        InsurerProfile profile = load(organizationId);
        String url = blankToNull(endpointUrl);
        String ref = blankToNull(secretRef);
        List<FieldError> errors = new ArrayList<>();
        if (mode == null) {
            errors.add(new FieldError("ratingMode", FieldErrorCodes.REQUIRED, "ratingMode is required"));
        }
        if (url == null) {
            if (mode == RatingMode.EXTERNAL) {
                errors.add(new FieldError("ratingEndpointUrl", FieldErrorCodes.REQUIRED,
                        "EXTERNAL rating needs the endpoint URL"));
            }
        } else if (!validUrl(url)) {
            errors.add(new FieldError("ratingEndpointUrl", FieldErrorCodes.INVALID_URL,
                    allowHttp ? "Must be an absolute http(s) URL of at most 500 characters"
                            : "Must be an absolute https URL of at most 500 characters"));
        }
        if (ref == null) {
            if (mode == RatingMode.EXTERNAL) {
                errors.add(new FieldError("ratingSecretRef", FieldErrorCodes.REQUIRED,
                        "EXTERNAL rating needs the secret reference"));
            }
        } else if (!SECRET_REF.matcher(ref).matches()) {
            errors.add(new FieldError("ratingSecretRef", FieldErrorCodes.INVALID_SECRET_REF,
                    "Must be config:<key> with 1-40 lower-case letters, digits or -"));
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        if (profile.getVersion() != expectedVersion) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The insurer profile was changed by another request");
        }
        RatingIntegration before = view(profile);
        profile.changeRatingIntegration(mode, url != null ? url : profile.getRatingEndpointUrl(),
                ref != null ? ref : profile.getRatingSecretRef());
        profiles.flush();
        RatingIntegration after = view(profile);
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), organizationId,
                "RATING_INTEGRATION.UPDATE", "InsurerProfile", profile.getId().toString(),
                AuditState.of(before), AuditState.of(after)));
        return after;
    }

    /** Estado auditado (sem segredos: só a referência). */
    record AuditState(String ratingMode, String ratingEndpointUrl, String ratingSecretRef) {

        static AuditState of(RatingIntegration i) {
            return new AuditState(i.ratingMode().name(), i.ratingEndpointUrl(), i.ratingSecretRef());
        }
    }

    private InsurerProfile load(UUID organizationId) {
        Organization organization = organizations.findById(organizationId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.ORGANIZATION_NOT_FOUND, "Organization not found"));
        if (organization.getType() != OrganizationType.INSURER) {
            throw DomainException.of(ErrorCodes.ORGANIZATION_NOT_INSURER, "The organization is not an insurer");
        }
        return profiles.findByOrganizationId(organizationId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.ORGANIZATION_NOT_FOUND, "Insurer profile not found"));
    }

    /** URL absoluta com servidor, até 500 caracteres, {@code https} (ou {@code http} com {@code allow-http}). */
    boolean validUrl(String url) {
        if (url.length() > URL_MAX) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            boolean schemeAllowed = scheme.equals("https") || (allowHttp && scheme.equals("http"));
            return uri.isAbsolute() && schemeAllowed && uri.getHost() != null && !uri.getHost().isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static RatingIntegration view(InsurerProfile p) {
        return new RatingIntegration(p.getRatingMode(), p.getRatingEndpointUrl(), p.getRatingSecretRef(),
                p.getVersion());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
