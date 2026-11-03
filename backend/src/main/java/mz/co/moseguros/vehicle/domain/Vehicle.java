package mz.co.moseguros.vehicle.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import mz.co.moseguros.vehicle.domain.VehicleRules.ValidVehicle;

/**
 * Veículo de um cliente ({@code vehicle.vehicle}, S2-03 §1). A matrícula é gravada em duas colunas
 * simples ({@code plate} e {@code plate_format_code}) e reconstruída com {@link Matricula#restore}: o
 * {@code MatriculaConverter} só grava o valor.
 */
@Entity
@Table(schema = "vehicle", name = "vehicle")
public class Vehicle extends BaseEntity {

    @Column(name = "owner_user_id", nullable = false, updatable = false)
    private UUID ownerUserId;

    @Column(name = "plate", nullable = false, length = 15)
    private String plate;

    @Column(name = "plate_format_code", nullable = false, length = 20)
    private String plateFormatCode;

    @Column(name = "chassis_number", nullable = false, length = 30)
    private String chassisNumber;

    @Column(name = "make", nullable = false, length = 60)
    private String make;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "engine_cc")
    private Integer engineCc;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private VehicleCategory category;

    @Column(name = "seats", nullable = false)
    private int seats;

    @Column(name = "gross_weight_kg")
    private Integer grossWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage", nullable = false, length = 25)
    private VehicleUsage usage;

    @Enumerated(EnumType.STRING)
    @Column(name = "province_of_circulation", nullable = false, length = 20)
    private Province provinceOfCirculation;

    @Column(name = "registration_doc_file_id")
    private UUID registrationDocFileId;

    @Column(name = "commercial_value_amount")
    private Long commercialValueAmount;

    @Column(name = "has_tracker", nullable = false)
    private boolean hasTracker;

    @Column(name = "inatro_verified_at")
    private Instant inatroVerifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "removed_at")
    private Instant removedAt;

    protected Vehicle() {
    }

    /** Veículo novo, {@code ACTIVE}, do cliente indicado. */
    public static Vehicle create(UUID ownerUserId, ValidVehicle data, Instant inatroVerifiedAt) {
        Vehicle vehicle = new Vehicle();
        vehicle.ownerUserId = ownerUserId;
        vehicle.apply(data, inatroVerifiedAt);
        return vehicle;
    }

    /** Substitui os dados do livrete e o resultado da consulta ao registo automóvel. */
    public void update(ValidVehicle data, Instant inatroVerifiedAt) {
        requireActive();
        apply(data, inatroVerifiedAt);
    }

    /** {@code true} se a matrícula ou o chassis são diferentes dos gravados (pede nova consulta ao registo). */
    public boolean identityChanges(ValidVehicle data) {
        return !plate.equals(data.plate().value()) || !chassisNumber.equals(data.chassisNumber());
    }

    /** Novo livrete actual (o anterior continua guardado e ligado). */
    public void attachRegistrationDoc(UUID fileId) {
        requireActive();
        this.registrationDocFileId = fileId;
    }

    /** Retira o veículo (sem apagar a linha). */
    public void remove(Instant now) {
        requireActive();
        this.status = RecordStatus.REMOVED;
        this.removedAt = now;
    }

    public boolean isActive() {
        return status == RecordStatus.ACTIVE;
    }

    private void apply(ValidVehicle data, Instant inatroVerifiedAt) {
        this.plate = data.plate().value();
        this.plateFormatCode = data.plate().formatCode();
        this.chassisNumber = data.chassisNumber();
        this.make = data.make();
        this.model = data.model();
        this.year = data.year();
        this.engineCc = data.engineCc();
        this.category = data.category();
        this.seats = data.seats();
        this.grossWeightKg = data.grossWeightKg();
        this.usage = data.usage();
        this.provinceOfCirculation = data.provinceOfCirculation();
        this.commercialValueAmount = data.commercialValue() == null ? null : data.commercialValue().amount();
        this.hasTracker = data.hasTracker();
        this.inatroVerifiedAt = inatroVerifiedAt;
    }

    private void requireActive() {
        if (!isActive()) {
            throw DomainException.of(ErrorCodes.VEHICLE_NOT_FOUND, "Vehicle not found");
        }
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public Matricula getPlate() {
        return Matricula.restore(plate, plateFormatCode);
    }

    public String getPlateFormatCode() {
        return plateFormatCode;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public Integer getEngineCc() {
        return engineCc;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    public int getSeats() {
        return seats;
    }

    public Integer getGrossWeightKg() {
        return grossWeightKg;
    }

    public VehicleUsage getUsage() {
        return usage;
    }

    public Province getProvinceOfCirculation() {
        return provinceOfCirculation;
    }

    public UUID getRegistrationDocFileId() {
        return registrationDocFileId;
    }

    public Money getCommercialValue() {
        return commercialValueAmount == null ? null : Money.ofCentavos(commercialValueAmount);
    }

    public boolean hasTracker() {
        return hasTracker;
    }

    public Instant getInatroVerifiedAt() {
        return inatroVerifiedAt;
    }

    public boolean isInatroVerified() {
        return inatroVerifiedAt != null;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public Instant getRemovedAt() {
        return removedAt;
    }
}
