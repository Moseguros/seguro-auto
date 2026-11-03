package mz.co.moseguros.vehicle.domain;

import java.time.Instant;
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
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import mz.co.moseguros.vehicle.domain.DriverRules.ValidDriver;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Condutor registado por um cliente ({@code vehicle.driver}, S2-03 §1). O número da carta está em
 * claro no Java e cifrado na coluna; para procurar repetidos usa-se o índice cego
 * ({@code licence_number_hash}) e para mostrar a máscara ({@code licence_number_last4}).
 */
@Entity
@Table(schema = "vehicle", name = "driver")
public class Driver extends BaseEntity {

    @Column(name = "owner_user_id", nullable = false, updatable = false)
    private UUID ownerUserId;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "licence_number_enc", nullable = false)
    private String licenceNumber;

    @Column(name = "licence_number_hash", nullable = false, length = 64)
    private String licenceNumberHash;

    @Column(name = "licence_number_last4", nullable = false, length = 4)
    private String licenceNumberLast4;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "licence_categories", nullable = false, columnDefinition = "text[]")
    private List<String> licenceCategories;

    @Column(name = "licence_issue_date", nullable = false)
    private LocalDate licenceIssueDate;

    @Column(name = "is_owner", nullable = false)
    private boolean owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "removed_at")
    private Instant removedAt;

    protected Driver() {
    }

    /**
     * Condutor novo, {@code ACTIVE}.
     *
     * @param licenceNumberHash {@code BlindIndex} do número normalizado ({@link DriverRules#LICENCE_HASH_PURPOSE})
     */
    public static Driver create(UUID ownerUserId, ValidDriver data, String licenceNumberHash) {
        if (data.licenceNumber() == null) {
            throw new IllegalArgumentException("A new driver needs the licence number");
        }
        Driver driver = new Driver();
        driver.ownerUserId = ownerUserId;
        driver.apply(data, licenceNumberHash);
        return driver;
    }

    /**
     * Substitui os dados. Sem número da carta ({@code null}) mantém o actual e o seu hash.
     */
    public void update(ValidDriver data, String licenceNumberHash) {
        requireActive();
        apply(data, licenceNumberHash);
    }

    public void remove(Instant now) {
        requireActive();
        this.status = RecordStatus.REMOVED;
        this.removedAt = now;
    }

    public boolean isActive() {
        return status == RecordStatus.ACTIVE;
    }

    /** {@code ****1234}: quatro {@code *} fixos e os últimos 4 caracteres. */
    public String licenceNumberMasked() {
        return DriverRules.mask(licenceNumberLast4);
    }

    private void apply(ValidDriver data, String licenceNumberHash) {
        this.fullName = data.fullName();
        this.birthDate = data.birthDate();
        if (data.licenceNumber() != null) {
            this.licenceNumber = data.licenceNumber();
            this.licenceNumberHash = licenceNumberHash;
            this.licenceNumberLast4 = DriverRules.last4(data.licenceNumber());
        }
        this.licenceCategories = List.copyOf(data.licenceCategories());
        this.licenceIssueDate = data.licenceIssueDate();
        this.owner = data.isOwner();
    }

    private void requireActive() {
        if (!isActive()) {
            throw DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found");
        }
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    /** Número em claro: só para o domínio; nunca sai da API nem vai para logs ou auditoria. */
    public String getLicenceNumber() {
        return licenceNumber;
    }

    public String getLicenceNumberHash() {
        return licenceNumberHash;
    }

    public List<String> getLicenceCategories() {
        return licenceCategories;
    }

    public LocalDate getLicenceIssueDate() {
        return licenceIssueDate;
    }

    public boolean isOwner() {
        return owner;
    }

    public RecordStatus getStatus() {
        return status;
    }
}
