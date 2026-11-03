package mz.co.moseguros.organization.domain;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    /** Ficheiro do logótipo ({@code document.file}, sem FK); enviado pelo {@code catalog} (S2-04 §7.1). */
    @Column(name = "logo_file_id")
    private UUID logoFileId;

    @Column(name = "complaints_email", nullable = false, length = 254)
    private String complaintsEmail;

    @Column(name = "ombudsman_org_id")
    private UUID ombudsmanOrgId;

    /** Tarifação interna ou externa (S3-01 §2); muda só pela plataforma (S3-02). */
    @Enumerated(EnumType.STRING)
    @Column(name = "rating_mode", nullable = false, length = 10)
    private RatingMode ratingMode = RatingMode.INTERNAL;

    /** Serviço de tarifação externo (S3-02); muda só pela plataforma ({@code updateRatingIntegration}). */
    @Column(name = "rating_endpoint_url", length = 500)
    private String ratingEndpointUrl;

    /** Referência do segredo no cofre (S3-02), nunca o segredo; muda só pela plataforma. */
    @Column(name = "rating_secret_ref", length = 200)
    private String ratingSecretRef;

    /** Encargos ({@code schemas/charges-v1.json}), pela ordem em que se calculam. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "charges", nullable = false, columnDefinition = "jsonb")
    private JsonNode charges = JsonNodeFactory.instance.arrayNode();

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

    /** Altera o código curto, o e-mail de reclamações e o provedor do cliente. */
    public void update(String shortCode, String complaintsEmail, Organization ombudsman) {
        String code = validShortCode(shortCode);
        String email = Emails.normalize(complaintsEmail);
        setOmbudsman(ombudsman);
        this.shortCode = code;
        this.complaintsEmail = email;
    }

    /** Novo logótipo ({@code null} retira-o). */
    public void changeLogo(UUID fileIdOrNull) {
        this.logoFileId = fileIdOrNull;
    }

    /**
     * Substitui os encargos. {@code charges} já foi validado ({@link InsurerCharges#validate});
     * aqui só se confirma que é uma lista.
     */
    public void replaceCharges(JsonNode newCharges) {
        if (newCharges == null || !newCharges.isArray()) {
            throw new IllegalArgumentException("charges must be a JSON array");
        }
        this.charges = newCharges.deepCopy();
    }

    /**
     * Ligação à tarifação da seguradora (S3-02 §9), já validada pelo serviço. Em {@code EXTERNAL}
     * o endereço e a referência do segredo são obrigatórios (a BD também o garante); em
     * {@code INTERNAL} ficam gravados para voltar a ligar, mas não se usam.
     */
    public void changeRatingIntegration(RatingMode mode, String endpointUrl, String secretRef) {
        Objects.requireNonNull(mode, "mode");
        if (mode == RatingMode.EXTERNAL && (endpointUrl == null || secretRef == null)) {
            throw new IllegalArgumentException("EXTERNAL rating needs the endpoint and the secret reference");
        }
        this.ratingMode = mode;
        this.ratingEndpointUrl = endpointUrl;
        this.ratingSecretRef = secretRef;
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

    public RatingMode getRatingMode() {
        return ratingMode;
    }

    public String getRatingEndpointUrl() {
        return ratingEndpointUrl;
    }

    public String getRatingSecretRef() {
        return ratingSecretRef;
    }

    /** Cópia da lista de encargos (JSON de {@code charges-v1.json}). */
    public JsonNode getCharges() {
        return charges.deepCopy();
    }
}
