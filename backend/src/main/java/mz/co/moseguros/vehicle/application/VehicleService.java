package mz.co.moseguros.vehicle.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.vehicle.VehicleRemovalGuard;
import mz.co.moseguros.vehicle.application.VehicleDetails.DriverLink;
import mz.co.moseguros.vehicle.domain.Driver;
import mz.co.moseguros.vehicle.domain.DriverRepository;
import mz.co.moseguros.vehicle.domain.RecordStatus;
import mz.co.moseguros.vehicle.domain.Vehicle;
import mz.co.moseguros.vehicle.domain.VehicleDriver;
import mz.co.moseguros.vehicle.domain.VehicleDriverRepository;
import mz.co.moseguros.vehicle.domain.VehicleRepository;
import mz.co.moseguros.vehicle.domain.VehicleRules;
import mz.co.moseguros.vehicle.domain.VehicleRules.DriverAssignment;
import mz.co.moseguros.vehicle.domain.VehicleRules.ValidVehicle;
import mz.co.moseguros.vehicle.domain.VehicleRules.VehicleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Veículos do cliente autenticado ({@code /me/vehicles}, S2-03 §2–3). Corre em âmbito
 * {@code CUSTOMER}: o RLS só mostra os veículos do próprio cliente, e um id de outro cliente dá 404
 * {@code VEHICLE_NOT_FOUND}.
 */
@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);
    private static final String UNIQUE_PLATE_INDEX = "ux_vehicle_owner_plate";

    private final CurrentUser currentUser;
    private final VehicleRepository vehicles;
    private final DriverRepository drivers;
    private final VehicleDriverRepository vehicleDrivers;
    private final RegistryVerification registryVerification;
    private final PlateParser plates;
    private final DocumentApi documents;
    private final List<VehicleRemovalGuard> removalGuards;
    private final AuditService audit;
    private final Clock clock;
    private final EntityManager entityManager;

    public VehicleService(CurrentUser currentUser, VehicleRepository vehicles, DriverRepository drivers,
                          VehicleDriverRepository vehicleDrivers, RegistryVerification registryVerification,
                          PlateParser plates,
                          DocumentApi documents, List<VehicleRemovalGuard> removalGuards, AuditService audit,
                          Clock clock, EntityManager entityManager) {
        this.currentUser = currentUser;
        this.vehicles = vehicles;
        this.drivers = drivers;
        this.vehicleDrivers = vehicleDrivers;
        this.registryVerification = registryVerification;
        this.plates = plates;
        this.documents = documents;
        this.removalGuards = List.copyOf(removalGuards);
        this.audit = audit;
        this.clock = clock;
        this.entityManager = entityManager;
    }

    /** Veículos {@code ACTIVE} do cliente. */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional(readOnly = true)
    public Page<VehicleDetails> listMine(Pageable pageable) {
        Page<Vehicle> page = vehicles.findByOwnerUserIdAndStatus(currentUser.id(), RecordStatus.ACTIVE, pageable);
        Map<UUID, List<DriverLink>> links = driverLinks(page.getContent().stream().map(Vehicle::getId).toList());
        return page.map(v -> new VehicleDetails(v, registrationDoc(v), links.getOrDefault(v.getId(), List.of())));
    }

    /** @throws DomainException {@code VEHICLE_NOT_FOUND} se não existir, for de outro cliente ou estiver retirado */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional(readOnly = true)
    public VehicleDetails getMine(UUID vehicleId) {
        return details(loadActive(vehicleId));
    }

    /**
     * Cria o veículo e consulta o registo automóvel (§3.3). Auditoria {@code VEHICLE.CREATE}.
     *
     * @throws DomainException {@code VALIDATION_ERROR}, {@code VEHICLE_LIMIT_REACHED} ou
     *                         {@code VEHICLE_ALREADY_REGISTERED}
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public VehicleDetails create(VehicleData data) {
        UUID ownerId = currentUser.id();
        ValidVehicle valid = VehicleRules.validate(data, plates, clock.instant());
        if (vehicles.countByOwnerUserIdAndStatus(ownerId, RecordStatus.ACTIVE) >= VehicleRules.MAX_ACTIVE_VEHICLES) {
            throw DomainException.of(ErrorCodes.VEHICLE_LIMIT_REACHED,
                    "A customer can have at most " + VehicleRules.MAX_ACTIVE_VEHICLES + " active vehicles");
        }
        if (vehicles.existsByOwnerUserIdAndPlateAndStatus(ownerId, valid.plate().value(), RecordStatus.ACTIVE)) {
            throw alreadyRegistered();
        }
        Vehicle vehicle = Vehicle.create(ownerId, valid, registryVerification(valid));
        saveAndFlush(vehicle);
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "VEHICLE.CREATE", "Vehicle",
                vehicle.getId().toString(), null, AuditState.of(vehicle)));
        log.info("Customer {} registered vehicle {} (registry verified: {})", ownerId, vehicle.getId(),
                vehicle.isInatroVerified());
        return details(vehicle);
    }

    /**
     * Altera o veículo; consulta de novo o registo se mudar a matrícula ou o chassis. Auditoria
     * {@code VEHICLE.UPDATE}.
     *
     * @param version versão lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public VehicleDetails update(UUID vehicleId, VehicleData data, long version) {
        UUID ownerId = currentUser.id();
        Vehicle vehicle = loadActive(vehicleId);
        requireVersion(vehicle, version);
        ValidVehicle valid = VehicleRules.validate(data, plates, clock.instant());
        if (vehicles.existsByOwnerUserIdAndPlateAndStatusAndIdNot(ownerId, valid.plate().value(), RecordStatus.ACTIVE,
                vehicleId)) {
            throw alreadyRegistered();
        }
        AuditState before = AuditState.of(vehicle);
        Instant verifiedAt = vehicle.identityChanges(valid) ? registryVerification(valid) : vehicle.getInatroVerifiedAt();
        vehicle.update(valid, verifiedAt);
        saveAndFlush(vehicle);
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "VEHICLE.UPDATE", "Vehicle",
                vehicle.getId().toString(), before, AuditState.of(vehicle)));
        return details(vehicle);
    }

    /**
     * Retira o veículo (sem apagar a linha nem as suas ligações a condutores) se nenhum
     * {@link VehicleRemovalGuard} o impedir. Auditoria {@code VEHICLE.DELETE}.
     *
     * @throws DomainException {@code VEHICLE_REMOVAL_BLOCKED} (409)
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public void delete(UUID vehicleId) {
        UUID ownerId = currentUser.id();
        Vehicle vehicle = loadActive(vehicleId);
        for (VehicleRemovalGuard guard : removalGuards) {
            if (guard.blocksRemoval(vehicleId)) {
                throw DomainException.of(ErrorCodes.VEHICLE_REMOVAL_BLOCKED, "The vehicle cannot be removed now");
            }
        }
        AuditState before = AuditState.of(vehicle);
        vehicle.remove(clock.instant());
        vehicles.saveAndFlush(vehicle);
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "VEHICLE.DELETE", "Vehicle",
                vehicle.getId().toString(), before, null));
        log.info("Customer {} removed vehicle {}", ownerId, vehicleId);
    }

    /**
     * Substitui a lista de condutores do veículo (§3.5). A versão do veículo aumenta. Auditoria
     * {@code VEHICLE.DRIVERS_UPDATE}.
     *
     * @param version versão do veículo lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     * @throws DomainException {@code VALIDATION_ERROR} ou {@code DRIVER_NOT_FOUND} (404)
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public VehicleDetails updateDrivers(UUID vehicleId, List<DriverAssignment> assignments, long version) {
        UUID ownerId = currentUser.id();
        Vehicle vehicle = loadActive(vehicleId);
        requireVersion(vehicle, version);
        List<DriverAssignment> valid = VehicleRules.validateDrivers(assignments);
        List<UUID> driverIds = valid.stream().map(DriverAssignment::driverId).toList();
        long found = driverIds.isEmpty() ? 0 : drivers.findByIdInAndStatus(driverIds, RecordStatus.ACTIVE).stream()
                .filter(d -> d.getOwnerUserId().equals(ownerId))
                .count();
        if (found != driverIds.size()) {
            throw DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found");
        }

        List<AssignmentState> before = driverLinks(List.of(vehicleId)).getOrDefault(vehicleId, List.of()).stream()
                .map(l -> new AssignmentState(l.driverId(), l.habitual()))
                .toList();
        entityManager.lock(vehicle, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        vehicleDrivers.deleteByVehicle(vehicleId);
        vehicleDrivers.saveAllAndFlush(valid.stream()
                .map(a -> new VehicleDriver(vehicleId, a.driverId(), a.habitual()))
                .toList());
        List<AssignmentState> after = valid.stream().map(a -> new AssignmentState(a.driverId(), a.habitual())).toList();
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "VEHICLE.DRIVERS_UPDATE",
                "Vehicle", vehicleId.toString(), new DriversState(before), new DriversState(after)));
        return details(vehicle);
    }

    /** Veículo {@code ACTIVE} do cliente actual; senão {@code VEHICLE_NOT_FOUND} (404). */
    Vehicle loadActive(UUID vehicleId) {
        return vehicles.findByIdAndOwnerUserIdAndStatus(vehicleId, currentUser.id(), RecordStatus.ACTIVE)
                .orElseThrow(VehicleService::notFound);
    }

    /** O livrete actual e os condutores do veículo. */
    VehicleDetails details(Vehicle vehicle) {
        return new VehicleDetails(vehicle, registrationDoc(vehicle),
                driverLinks(List.of(vehicle.getId())).getOrDefault(vehicle.getId(), List.of()));
    }

    static DomainException notFound() {
        return DomainException.of(ErrorCodes.VEHICLE_NOT_FOUND, "Vehicle not found");
    }

    /** Condutores {@code ACTIVE} de cada veículo, o habitual primeiro e depois por nome. */
    private Map<UUID, List<DriverLink>> driverLinks(Collection<UUID> vehicleIds) {
        if (vehicleIds.isEmpty()) {
            return Map.of();
        }
        List<VehicleDriver> links = vehicleDrivers.findByVehicleIdIn(vehicleIds);
        if (links.isEmpty()) {
            return Map.of();
        }
        Map<UUID, Driver> activeDrivers = drivers.findByIdInAndStatus(
                        links.stream().map(VehicleDriver::getDriverId).collect(Collectors.toSet()), RecordStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(Driver::getId, Function.identity()));
        Map<UUID, List<DriverLink>> result = new HashMap<>();
        for (VehicleDriver link : links) {
            Driver driver = activeDrivers.get(link.getDriverId());
            if (driver != null) {
                result.computeIfAbsent(link.getVehicleId(), id -> new ArrayList<>())
                        .add(new DriverLink(driver.getId(), driver.getFullName(), link.isHabitual()));
            }
        }
        Comparator<DriverLink> order = Comparator.comparing(DriverLink::habitual).reversed()
                .thenComparing(DriverLink::fullName);
        result.values().forEach(list -> list.sort(order));
        return result;
    }

    private FileRef registrationDoc(Vehicle vehicle) {
        if (vehicle.getRegistrationDocFileId() == null) {
            return null;
        }
        try {
            return documents.metadata(vehicle.getRegistrationDocFileId());
        } catch (DomainException e) {
            log.warn("Registration document {} of vehicle {} is not readable ({})",
                    vehicle.getRegistrationDocFileId(), vehicle.getId(), e.getCode());
            return null;
        }
    }

    private Instant registryVerification(ValidVehicle valid) {
        return registryVerification.verify(valid);
    }

    private void saveAndFlush(Vehicle vehicle) {
        try {
            vehicles.saveAndFlush(vehicle);
        } catch (DataIntegrityViolationException e) {
            String cause = String.valueOf(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
            if (cause.contains(UNIQUE_PLATE_INDEX)) {
                throw alreadyRegistered();
            }
            throw e;
        }
    }

    private static void requireVersion(Vehicle vehicle, long version) {
        if (vehicle.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The vehicle was changed by another request (version " + vehicle.getVersion() + ")");
        }
    }

    private static DomainException alreadyRegistered() {
        return DomainException.of(ErrorCodes.VEHICLE_ALREADY_REGISTERED,
                "The customer already has an active vehicle with this plate");
    }

    /** Estado gravado na auditoria. */
    public record AuditState(String plate, String chassisNumber, String make, String model, int year,
                             Integer engineCc, VehicleCategory category, int seats, Integer grossWeightKg,
                             VehicleUsage usage, Province provinceOfCirculation, Money commercialValue,
                             boolean hasTracker, boolean inatroVerified, String status) {

        static AuditState of(Vehicle v) {
            Matricula plate = v.getPlate();
            return new AuditState(plate.value(), v.getChassisNumber(), v.getMake(), v.getModel(), v.getYear(),
                    v.getEngineCc(), v.getCategory(), v.getSeats(), v.getGrossWeightKg(), v.getUsage(),
                    v.getProvinceOfCirculation(), v.getCommercialValue(), v.hasTracker(), v.isInatroVerified(),
                    v.getStatus().name());
        }
    }

    /** Lista de condutores na auditoria. */
    public record DriversState(List<AssignmentState> drivers) {
    }

    public record AssignmentState(UUID driverId, boolean habitual) {
    }
}
