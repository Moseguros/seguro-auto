package mz.co.moseguros.organization.domain;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Dados próprios de uma seguradora ({@code organization.insurer_profile}); uma linha por
 * organização do tipo {@code INSURER}.
 */
@Entity
@Table(schema = "organization", name = "insurer_profile")
public class InsurerProfile extends BaseEntity {

    private static final Pattern SHORT_CODE = Pattern.compile("^[A-Z0-9]{2,10}$");

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "short_code", nullable = false, length = 10)
    private String shortCode;

    /** Preenchido quando existir o módulo {@code document} (Sprint 3). */
    @Column(name = "logo_file_id")
    private UUID logoFileId;

    @Column(name = "complaints_email", nullable = false, length = 254)
    private String complaintsEmail;

    @Column(name = "ombudsman_org_id")
    private UUID ombudsmanOrgId;

    protected InsurerProfile() {
    }

    /**
     * @param insurer   organização do tipo {@code INSURER}
     * @param ombudsman provedor do cliente (tipo {@code OMBUDSMAN}) ou {@code null}
     */
    public static InsurerProfile create(Organization insurer, String shortCode, String complaintsEmail,
                                        Organization ombudsman) {
        Objects.requireNonNull(insurer, "insurer");
        if (insurer.getType() != OrganizationType.INSURER) {
            throw new IllegalArgumentException("Insurer profile requires an INSURER organization");
        }
        InsurerProfile profile = new InsurerProfile();
        profile.organizationId = insurer.getId();
        profile.shortCode = validShortCode(shortCode);
        profile.complaintsEmail = Emails.normalize(complaintsEmail);
        profile.setOmbudsman(ombudsman);
        return profile;
    }

    public void setOmbudsman(Organization ombudsman) {
        if (ombudsman != null && ombudsman.getType() != OrganizationType.OMBUDSMAN) {
            throw new IllegalArgumentException("Ombudsman must be an OMBUDSMAN organization");
        }
        this.ombudsmanOrgId = ombudsman == null ? null : ombudsman.getId();
    }

    private static String validShortCode(String raw) {
        if (raw == null || !SHORT_CODE.matcher(raw).matches()) {
            throw DomainException.badRequest(ErrorCodes.VALIDATION_ERROR,
                    "Short code must have 2 to 10 upper-case letters or digits");
        }
        return raw;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getShortCode() {
        return shortCode;
    }

    public UUID getLogoFileId() {
        return logoFileId;
    }

    public String getComplaintsEmail() {
        return complaintsEmail;
    }

    public UUID getOmbudsmanOrgId() {
        return ombudsmanOrgId;
    }
}
