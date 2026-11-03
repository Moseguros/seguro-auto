package mz.co.moseguros.organization.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import mz.co.moseguros.shared.persistence.converters.NuitConverter;

/**
 * Organização: seguradora, mediador, oficina, perito, assistência, provedor, regulador,
 * polícia ou cliente empresarial ({@code organization.organization}).
 *
 * <p>O NUIT de uma pessoa colectiva é público e fica em claro (AGENTS.md §6).
 */
@Entity
@Table(schema = "organization", name = "organization")
public class Organization extends BaseEntity {

    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{9,15}$");
    private static final Pattern SPACES = Pattern.compile("\\s");

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 20)
    private OrganizationType type;

    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Column(name = "trade_name", length = 120)
    private String tradeName;

    @Convert(converter = NuitConverter.class)
    @Column(name = "nuit", nullable = false, length = 9)
    private Nuit nuit;

    @Column(name = "license_number", length = 40)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationStatus status;

    @Column(name = "contact_email", nullable = false, length = 254)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "address", length = 300)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "province", length = 20)
    private Province province;

    protected Organization() {
    }

    /**
     * Nova organização {@code ACTIVE}. O número de autorização da ASFPM é obrigatório para
     * {@code INSURER}, {@code BROKER} e {@code AGENT} ({@code LICENSE_NUMBER_REQUIRED}, 400).
     */
    public static Organization create(OrganizationType type, String legalName, String tradeName, Nuit nuit,
                                      String licenseNumber, String contactEmail, String contactPhone,
                                      String address, Province province) {
        Organization org = new Organization();
        org.type = Objects.requireNonNull(type, "type");
        org.nuit = Objects.requireNonNull(nuit, "nuit");
        org.licenseNumber = blankToNull(licenseNumber);
        if (type.requiresLicense() && org.licenseNumber == null) {
            throw DomainException.badRequest(ErrorCodes.LICENSE_NUMBER_REQUIRED,
                    "License number is required for organizations of type " + type);
        }
        org.legalName = requireText(legalName, "legalName");
        org.tradeName = blankToNull(tradeName);
        org.contactEmail = Emails.normalize(contactEmail);
        org.contactPhone = normalizePhone(contactPhone);
        org.address = blankToNull(address);
        org.province = province;
        org.status = OrganizationStatus.ACTIVE;
        return org;
    }

    /**
     * Altera os dados da organização (o tipo não muda), com as mesmas regras de
     * {@link #create}.
     */
    public void update(String legalName, String tradeName, Nuit nuit, String licenseNumber, String contactEmail,
                       String contactPhone, String address, Province province) {
        String license = blankToNull(licenseNumber);
        if (type.requiresLicense() && license == null) {
            throw DomainException.badRequest(ErrorCodes.LICENSE_NUMBER_REQUIRED,
                    "License number is required for organizations of type " + type);
        }
        String legal = requireText(legalName, "legalName");
        String email = Emails.normalize(contactEmail);
        String phone = normalizePhone(contactPhone);
        this.nuit = Objects.requireNonNull(nuit, "nuit");
        this.licenseNumber = license;
        this.legalName = legal;
        this.tradeName = blankToNull(tradeName);
        this.contactEmail = email;
        this.contactPhone = phone;
        this.address = blankToNull(address);
        this.province = province;
    }

    /** {@code ACTIVE → SUSPENDED}; repetir dá {@code INVALID_STATE_TRANSITION} (409). */
    public void suspend() {
        transition(OrganizationStatus.ACTIVE, OrganizationStatus.SUSPENDED);
    }

    /** {@code SUSPENDED → ACTIVE}; repetir dá {@code INVALID_STATE_TRANSITION} (409). */
    public void reactivate() {
        transition(OrganizationStatus.SUSPENDED, OrganizationStatus.ACTIVE);
    }

    private void transition(OrganizationStatus from, OrganizationStatus to) {
        if (status != from) {
            throw new DomainException(ErrorCodes.INVALID_STATE_TRANSITION, 409,
                    "Organization cannot go from " + status + " to " + to);
        }
        status = to;
    }

    /** Nome a mostrar: o comercial, se existir, senão a denominação social. */
    public String displayName() {
        return tradeName != null ? tradeName : legalName;
    }

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE;
    }

    public OrganizationType getType() {
        return type;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public Nuit getNuit() {
        return nuit;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public Province getProvince() {
        return province;
    }

    static String normalizePhone(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String phone = SPACES.matcher(raw).replaceAll("");
        if (!PHONE.matcher(phone).matches()) {
            throw DomainException.badRequest(ErrorCodes.INVALID_PHONE, "Invalid phone number");
        }
        return phone;
    }

    private static String requireText(String value, String field) {
        String text = blankToNull(value);
        if (text == null) {
            throw DomainException.badRequest(ErrorCodes.VALIDATION_ERROR, field + " is required");
        }
        return text;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
