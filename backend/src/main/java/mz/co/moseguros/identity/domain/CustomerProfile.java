package mz.co.moseguros.identity.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.crypto.EncryptedStringConverter;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Dados pessoais do cliente ({@code identity.customer_profile}), criado vazio junto com a
 * conta. O número do documento e o NUIT (pessoa singular) estão em claro no Java e
 * cifrados nas colunas ({@code *_enc}, AGENTS.md §6).
 */
@Entity
@Table(schema = "identity", name = "customer_profile")
public class CustomerProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "full_name", length = 120)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_doc_type", length = 10)
    private IdDocType idDocType;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "id_doc_number_enc")
    private String idDocNumber;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "nuit_enc")
    private String nuit;

    @Column(name = "address", length = 300)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "province", length = 20)
    private Province province;

    protected CustomerProfile() {
    }

    /** Perfil vazio de uma conta de cliente. */
    public static CustomerProfile emptyFor(UserAccount account) {
        if (!account.isCustomer()) {
            throw new IllegalArgumentException("Customer profile requires a CUSTOMER account");
        }
        CustomerProfile profile = new CustomerProfile();
        profile.userId = account.getId();
        return profile;
    }

    /**
     * Substitui os dados pessoais. O NUIT é validado com {@link Nuit} ({@code INVALID_NUIT})
     * e guardado normalizado; {@code null} apaga o valor.
     */
    public void update(String fullName, LocalDate birthDate, IdDocType idDocType, String idDocNumber,
                       String nuit, String address, Province province) {
        this.fullName = blankToNull(fullName);
        this.birthDate = birthDate;
        this.idDocType = idDocType;
        this.idDocNumber = blankToNull(idDocNumber);
        this.nuit = nuit == null || nuit.isBlank() ? null : Nuit.of(nuit).value();
        this.address = blankToNull(address);
        this.province = province;
    }

    /**
     * Alteração pelo cliente (S1-06): valida com {@link CustomerProfileRules} e grava. O número do
     * documento e o NUIT {@code null} mantêm os actuais; um valor substitui-os.
     *
     * @param today data de hoje em {@link CustomerProfileRules#ZONE}
     * @throws DomainException 400 {@code VALIDATION_ERROR} com um erro por campo em {@code errors}
     */
    public void apply(CustomerProfileRules.Changes changes, LocalDate today) {
        List<FieldError> errors = CustomerProfileRules.validate(this, changes, today);
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        this.fullName = CustomerProfileRules.normalizeName(changes.fullName());
        this.birthDate = changes.birthDate();
        this.idDocType = changes.idDocType();
        String documentNumber = CustomerProfileRules.normalizeDocumentNumber(changes.idDocNumber());
        if (documentNumber != null) {
            this.idDocNumber = documentNumber;
        }
        if (changes.nuit() != null && !changes.nuit().isBlank()) {
            this.nuit = Nuit.of(changes.nuit()).value();
        }
        this.address = blankToNull(changes.address());
        this.province = changes.province();
    }

    /** {@code *********345A}, ou {@code null} sem documento. */
    public String idDocNumberMasked() {
        return CustomerProfileRules.maskDocumentNumber(idDocNumber);
    }

    /** {@code *****6789}, ou {@code null} sem NUIT. */
    public String nuitMasked() {
        return nuit == null ? null : Nuit.of(nuit).masked();
    }

    /** Completo quando tem nome, data de nascimento, documento, NUIT e província. */
    public boolean isComplete() {
        return fullName != null && birthDate != null && idDocType != null && idDocNumber != null
                && nuit != null && province != null;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public IdDocType getIdDocType() {
        return idDocType;
    }

    public String getIdDocNumber() {
        return idDocNumber;
    }

    public String getNuit() {
        return nuit;
    }

    public String getAddress() {
        return address;
    }

    public Province getProvince() {
        return province;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
