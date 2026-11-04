package mz.co.moseguros.vehicle.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.crypto.BlindIndex;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import mz.co.moseguros.vehicle.DriverSnapshot;
import mz.co.moseguros.vehicle.NewDriver;
import mz.co.moseguros.vehicle.NewVehicle;
import mz.co.moseguros.vehicle.RegisteredVehicle;
import mz.co.moseguros.vehicle.VehicleSnapshot;
import mz.co.moseguros.vehicle.domain.Driver;
import mz.co.moseguros.vehicle.domain.DriverRepository;
import mz.co.moseguros.vehicle.domain.DriverRules;
import mz.co.moseguros.vehicle.domain.DriverRules.DriverData;
import mz.co.moseguros.vehicle.domain.DriverRules.ValidDriver;
import mz.co.moseguros.vehicle.domain.RecordStatus;
import mz.co.moseguros.vehicle.domain.Vehicle;
import mz.co.moseguros.vehicle.domain.VehicleDriver;
import mz.co.moseguros.vehicle.domain.VehicleDriverRepository;
import mz.co.moseguros.vehicle.domain.VehicleRepository;
import mz.co.moseguros.vehicle.domain.VehicleRules;
import mz.co.moseguros.vehicle.domain.VehicleRules.ValidVehicle;
import mz.co.moseguros.vehicle.domain.VehicleRules.VehicleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Escritas do {@code vehicle} pedidas por outro módulo em nome de um cliente, em {@code SYSTEM}
 * (S4-06 §3): o mediador, em âmbito {@code ORG}, não vê nem escreve nas tabelas do cliente. Filtra
 * sempre pelo dono indicado.
 *
 * <p>Corre na transacção de quem chama: um erro posterior desfaz o veículo e o condutor. Tudo o que é
 * escrito é gravado ({@code flush}) e retirado do contexto de persistência antes de voltar ao âmbito de
 * quem chama, para nada destas tabelas ser gravado de novo fora de {@code SYSTEM}. Os logs têm os ids;
 * nunca a matrícula completa, o chassis nem o número da carta.
 */
@Component
public class VehicleSystemLookups {

    private static final Logger log = LoggerFactory.getLogger(VehicleSystemLookups.class);

    private final VehicleRepository vehicles;
    private final DriverRepository drivers;
    private final VehicleDriverRepository vehicleDrivers;
    private final PlateParser plates;
    private final ReferenceValueLookup references;
    private final BlindIndex blindIndex;
    private final RegistryVerification registryVerification;
    private final VehicleSnapshots snapshots;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final Clock clock;
    private final EntityManager entityManager;

    public VehicleSystemLookups(VehicleRepository vehicles, DriverRepository drivers,
                                VehicleDriverRepository vehicleDrivers, PlateParser plates,
                                ReferenceValueLookup references, BlindIndex blindIndex,
                                RegistryVerification registryVerification, VehicleSnapshots snapshots,
                                AuditService audit, CurrentUser currentUser, Clock clock,
                                EntityManager entityManager) {
        this.vehicles = vehicles;
        this.drivers = drivers;
        this.vehicleDrivers = vehicleDrivers;
        this.plates = plates;
        this.references = references;
        this.blindIndex = blindIndex;
        this.registryVerification = registryVerification;
        this.snapshots = snapshots;
        this.audit = audit;
        this.currentUser = currentUser;
        this.clock = clock;
        this.entityManager = entityManager;
    }

    /** {@code VehicleApi.registerForCustomer} (S4-06 §3). */
    @RunAsSystem
    @Transactional
    public RegisteredVehicle registerForCustomer(UUID ownerUserId, NewVehicle vehicle, NewDriver driver) {
        Objects.requireNonNull(ownerUserId, "ownerUserId");
        Instant now = clock.instant();
        Validated valid = validate(vehicle, driver, now);
        String actor = currentUser.id().toString();

        // Condutor: o activo com a mesma carta, sem o alterar; senão um novo.
        String hash = blindIndex.hash(DriverRules.LICENCE_HASH_PURPOSE, valid.driver().licenceNumber());
        Optional<Driver> existingDriver = drivers.findFirstByOwnerUserIdAndLicenceNumberHashAndStatus(ownerUserId,
                hash, RecordStatus.ACTIVE);
        Driver d;
        if (existingDriver.isPresent()) {
            d = existingDriver.get();
        } else {
            if (drivers.countByOwnerUserIdAndStatus(ownerUserId, RecordStatus.ACTIVE) >= DriverRules.MAX_ACTIVE_DRIVERS) {
                throw DomainException.of(ErrorCodes.DRIVER_LIMIT_REACHED,
                        "A customer can have at most " + DriverRules.MAX_ACTIVE_DRIVERS + " active drivers");
            }
            if (valid.driver().isOwner()
                    && drivers.existsByOwnerUserIdAndOwnerTrueAndStatus(ownerUserId, RecordStatus.ACTIVE)) {
                throw DomainException.of(ErrorCodes.OWNER_DRIVER_ALREADY_EXISTS,
                        "The customer is already registered as a driver");
            }
            d = Driver.create(ownerUserId, valid.driver(), hash);
            drivers.saveAndFlush(d);
            audit.record(new AuditRecord(ActorType.STAFF, actor, null, "DRIVER.CREATE", "Driver",
                    d.getId().toString(), null, DriverService.AuditState.of(d)));
        }

        // Veículo: o activo com a mesma matrícula e o mesmo chassis, sem o alterar; outro chassis → 409.
        Optional<Vehicle> existingVehicle = vehicles.findFirstByOwnerUserIdAndPlateAndStatus(ownerUserId,
                valid.vehicle().plate().value(), RecordStatus.ACTIVE);
        Vehicle v;
        if (existingVehicle.isPresent()) {
            v = existingVehicle.get();
            if (!valid.vehicle().chassisNumber().equals(VehicleRules.normalizeChassis(v.getChassisNumber()))) {
                throw DomainException.of(ErrorCodes.VEHICLE_ALREADY_REGISTERED,
                        "The customer already has an active vehicle with this plate");
            }
        } else {
            if (vehicles.countByOwnerUserIdAndStatus(ownerUserId, RecordStatus.ACTIVE)
                    >= VehicleRules.MAX_ACTIVE_VEHICLES) {
                throw DomainException.of(ErrorCodes.VEHICLE_LIMIT_REACHED,
                        "A customer can have at most " + VehicleRules.MAX_ACTIVE_VEHICLES + " active vehicles");
            }
            v = Vehicle.create(ownerUserId, valid.vehicle(), registryVerification.verify(valid.vehicle()));
            vehicles.saveAndFlush(v);
            audit.record(new AuditRecord(ActorType.STAFF, actor, null, "VEHICLE.CREATE", "Vehicle",
                    v.getId().toString(), null, VehicleService.AuditState.of(v)));
        }

        // Ligação: habitual se o veículo não tiver habitual; senão não habitual, se ainda não estiver ligado.
        link(v, d, actor);

        entityManager.flush();
        VehicleSnapshot vehicleSnapshot = snapshots.vehicleSnapshot(v, now);
        DriverSnapshot driverSnapshot = snapshots.driverSnapshot(d, now);
        entityManager.detach(v);
        entityManager.detach(d);
        log.info("Staff {} registered vehicle {} (new: {}) and driver {} (new: {}) for customer {}", actor, v.getId(),
                existingVehicle.isEmpty(), d.getId(), existingDriver.isEmpty(), ownerUserId);
        return new RegisteredVehicle(vehicleSnapshot, driverSnapshot, existingVehicle.isEmpty(),
                existingDriver.isEmpty());
    }

    private void link(Vehicle v, Driver d, String actor) {
        List<VehicleDriver> links = vehicleDrivers.findByVehicleIdIn(List.of(v.getId()));
        if (links.stream().anyMatch(l -> l.getDriverId().equals(d.getId()))) {
            links.forEach(entityManager::detach);
            return;
        }
        List<UUID> linkedIds = links.stream().map(VehicleDriver::getDriverId).toList();
        List<Driver> activeLinked = linkedIds.isEmpty() ? List.of()
                : drivers.findByIdInAndStatus(linkedIds, RecordStatus.ACTIVE);
        List<VehicleService.AssignmentState> before = links.stream()
                .filter(l -> activeLinked.stream().anyMatch(a -> a.getId().equals(l.getDriverId())))
                .map(l -> new VehicleService.AssignmentState(l.getDriverId(), l.isHabitual()))
                .toList();
        if (before.size() >= VehicleRules.MAX_DRIVERS_PER_VEHICLE) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", List.of(new FieldError("vehicle.drivers", FieldErrorCodes.OUT_OF_RANGE,
                            "A vehicle can have at most " + VehicleRules.MAX_DRIVERS_PER_VEHICLE + " drivers")));
        }
        boolean habitual = before.stream().noneMatch(VehicleService.AssignmentState::habitual);
        // A versão do veículo aumenta já (em SYSTEM), como no PUT /me/vehicles/{id}/drivers.
        entityManager.lock(v, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        VehicleDriver created = new VehicleDriver(v.getId(), d.getId(), habitual);
        vehicleDrivers.saveAndFlush(created);
        List<VehicleService.AssignmentState> after = new ArrayList<>(before);
        after.add(new VehicleService.AssignmentState(d.getId(), habitual));
        audit.record(new AuditRecord(ActorType.STAFF, actor, null, "VEHICLE.DRIVERS_UPDATE", "Vehicle",
                v.getId().toString(), new VehicleService.DriversState(before), new VehicleService.DriversState(after)));
        links.forEach(entityManager::detach);
        entityManager.detach(created);
    }

    private record Validated(ValidVehicle vehicle, ValidDriver driver) {
    }

    /** As regras do S2-03 para os dois, com os erros juntos e os campos prefixados. */
    private Validated validate(NewVehicle vehicle, NewDriver driver, Instant now) {
        List<FieldError> errors = new ArrayList<>();
        if (vehicle == null) {
            errors.add(new FieldError("vehicle", FieldErrorCodes.REQUIRED, "Required"));
        }
        if (driver == null) {
            errors.add(new FieldError("driver", FieldErrorCodes.REQUIRED, "Required"));
        }
        ValidVehicle validVehicle = null;
        if (vehicle != null) {
            try {
                validVehicle = VehicleRules.validate(new VehicleData(vehicle.plate(), vehicle.chassisNumber(),
                        vehicle.make(), vehicle.model(), vehicle.year(), vehicle.engineCc(), vehicle.category(),
                        vehicle.seats(), vehicle.grossWeightKg(), vehicle.usage(), vehicle.provinceOfCirculation(),
                        vehicle.commercialValue(), vehicle.hasTracker()), plates, now);
            } catch (DomainException e) {
                errors.addAll(prefixed("vehicle.", e));
            }
        }
        ValidDriver validDriver = null;
        if (driver != null) {
            try {
                validDriver = DriverRules.validate(new DriverData(driver.fullName(), driver.birthDate(),
                        driver.licenceNumber(), driver.licenceCategories(), driver.licenceIssueDate(),
                        driver.isOwner()), true, references, LocalDate.ofInstant(now, DriverRules.ZONE));
            } catch (DomainException e) {
                errors.addAll(prefixed("driver.", e));
            }
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        return new Validated(validVehicle, validDriver);
    }

    @SuppressWarnings("unchecked")
    private static List<FieldError> prefixed(String prefix, DomainException e) {
        Object list = e.getProperties().get("errors");
        if (!ErrorCodes.VALIDATION_ERROR.equals(e.getCode()) || !(list instanceof List<?>)) {
            throw e;
        }
        return ((List<FieldError>) list).stream()
                .map(f -> new FieldError(prefix + f.field(), f.code(), f.message()))
                .toList();
    }
}
