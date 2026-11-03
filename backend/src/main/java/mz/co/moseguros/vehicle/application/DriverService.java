package mz.co.moseguros.vehicle.application;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.crypto.BlindIndex;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.vehicle.domain.Driver;
import mz.co.moseguros.vehicle.domain.DriverRepository;
import mz.co.moseguros.vehicle.domain.DriverRules;
import mz.co.moseguros.vehicle.domain.DriverRules.DriverData;
import mz.co.moseguros.vehicle.domain.DriverRules.ValidDriver;
import mz.co.moseguros.vehicle.domain.RecordStatus;
import mz.co.moseguros.vehicle.domain.VehicleDriver;
import mz.co.moseguros.vehicle.domain.VehicleDriverRepository;
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
 * Condutores do cliente autenticado ({@code /me/drivers}, S2-03 §2–3). Corre em âmbito
 * {@code CUSTOMER} (RLS). O número da carta nunca sai em claro: nem na resposta, nem nos logs, nem
 * na auditoria (só {@code licenceNumberMasked}).
 */
@Service
public class DriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverService.class);
    private static final String UNIQUE_LICENCE_INDEX = "ux_driver_owner_licence";
    private static final String UNIQUE_OWNER_INDEX = "ux_driver_owner_self";

    private final CurrentUser currentUser;
    private final DriverRepository drivers;
    private final VehicleDriverRepository vehicleDrivers;
    private final ReferenceValueLookup references;
    private final BlindIndex blindIndex;
    private final AuditService audit;
    private final Clock clock;

    public DriverService(CurrentUser currentUser, DriverRepository drivers, VehicleDriverRepository vehicleDrivers,
                         ReferenceValueLookup references, BlindIndex blindIndex, AuditService audit, Clock clock) {
        this.currentUser = currentUser;
        this.drivers = drivers;
        this.vehicleDrivers = vehicleDrivers;
        this.references = references;
        this.blindIndex = blindIndex;
        this.audit = audit;
        this.clock = clock;
    }

    /** Condutores {@code ACTIVE} do cliente. */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional(readOnly = true)
    public Page<DriverDetails> listMine(Pageable pageable) {
        Page<Driver> page = drivers.findByOwnerUserIdAndStatus(currentUser.id(), RecordStatus.ACTIVE, pageable);
        Map<UUID, List<UUID>> vehicles = activeVehicles(page.getContent().stream().map(Driver::getId).toList());
        return page.map(d -> new DriverDetails(d, vehicles.getOrDefault(d.getId(), List.of())));
    }

    /** @throws DomainException {@code DRIVER_NOT_FOUND} se não existir, for de outro cliente ou estiver retirado */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional(readOnly = true)
    public DriverDetails getMine(UUID driverId) {
        return details(loadActive(driverId));
    }

    /**
     * Cria o condutor. Auditoria {@code DRIVER.CREATE}.
     *
     * @throws DomainException {@code VALIDATION_ERROR}, {@code DRIVER_LIMIT_REACHED},
     *                         {@code DRIVER_ALREADY_REGISTERED} ou {@code OWNER_DRIVER_ALREADY_EXISTS}
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public DriverDetails create(DriverData data) {
        UUID ownerId = currentUser.id();
        ValidDriver valid = DriverRules.validate(data, true, references, today());
        if (drivers.countByOwnerUserIdAndStatus(ownerId, RecordStatus.ACTIVE) >= DriverRules.MAX_ACTIVE_DRIVERS) {
            throw DomainException.of(ErrorCodes.DRIVER_LIMIT_REACHED,
                    "A customer can have at most " + DriverRules.MAX_ACTIVE_DRIVERS + " active drivers");
        }
        String hash = hash(valid.licenceNumber());
        if (drivers.existsByOwnerUserIdAndLicenceNumberHashAndStatus(ownerId, hash, RecordStatus.ACTIVE)) {
            throw alreadyRegistered();
        }
        if (valid.isOwner() && drivers.existsByOwnerUserIdAndOwnerTrueAndStatus(ownerId, RecordStatus.ACTIVE)) {
            throw ownerAlreadyExists();
        }
        Driver driver = Driver.create(ownerId, valid, hash);
        saveAndFlush(driver);
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "DRIVER.CREATE", "Driver",
                driver.getId().toString(), null, AuditState.of(driver)));
        log.info("Customer {} registered driver {}", ownerId, driver.getId());
        return details(driver);
    }

    /**
     * Altera o condutor; sem número da carta mantém o actual. Auditoria {@code DRIVER.UPDATE}.
     *
     * @param version versão lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public DriverDetails update(UUID driverId, DriverData data, long version) {
        UUID ownerId = currentUser.id();
        Driver driver = loadActive(driverId);
        if (driver.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The driver was changed by another request (version " + driver.getVersion() + ")");
        }
        ValidDriver valid = DriverRules.validate(data, false, references, today());
        String hash = valid.licenceNumber() == null ? null : hash(valid.licenceNumber());
        if (hash != null && drivers.existsByOwnerUserIdAndLicenceNumberHashAndStatusAndIdNot(ownerId, hash,
                RecordStatus.ACTIVE, driverId)) {
            throw alreadyRegistered();
        }
        if (valid.isOwner() && drivers.existsByOwnerUserIdAndOwnerTrueAndStatusAndIdNot(ownerId, RecordStatus.ACTIVE,
                driverId)) {
            throw ownerAlreadyExists();
        }
        AuditState before = AuditState.of(driver);
        driver.update(valid, hash);
        saveAndFlush(driver);
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "DRIVER.UPDATE", "Driver",
                driver.getId().toString(), before, AuditState.of(driver)));
        return details(driver);
    }

    /**
     * Retira o condutor e apaga as suas ligações a veículos {@code ACTIVE}. Auditoria
     * {@code DRIVER.DELETE}.
     *
     * @throws DomainException {@code DRIVER_IS_HABITUAL} (409) se for o habitual de um veículo activo
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public void delete(UUID driverId) {
        UUID ownerId = currentUser.id();
        Driver driver = loadActive(driverId);
        boolean habitual = vehicleDrivers.findByDriverIdsAndVehicleStatus(List.of(driverId), RecordStatus.ACTIVE)
                .stream().anyMatch(VehicleDriver::isHabitual);
        if (habitual) {
            throw DomainException.of(ErrorCodes.DRIVER_IS_HABITUAL,
                    "The driver is the habitual driver of an active vehicle");
        }
        AuditState before = AuditState.of(driver);
        vehicleDrivers.deleteByDriverAndVehicleStatus(driverId, RecordStatus.ACTIVE);
        driver.remove(clock.instant());
        drivers.saveAndFlush(driver);
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "DRIVER.DELETE", "Driver",
                driver.getId().toString(), before, null));
        log.info("Customer {} removed driver {}", ownerId, driverId);
    }

    private Driver loadActive(UUID driverId) {
        return drivers.findByIdAndOwnerUserIdAndStatus(driverId, currentUser.id(), RecordStatus.ACTIVE)
                .orElseThrow(() -> DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found"));
    }

    private DriverDetails details(Driver driver) {
        return new DriverDetails(driver, activeVehicles(List.of(driver.getId())).getOrDefault(driver.getId(), List.of()));
    }

    /** Veículos {@code ACTIVE} de cada condutor. */
    private Map<UUID, List<UUID>> activeVehicles(Collection<UUID> driverIds) {
        if (driverIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, List<UUID>> result = new HashMap<>();
        for (VehicleDriver link : vehicleDrivers.findByDriverIdsAndVehicleStatus(driverIds, RecordStatus.ACTIVE)) {
            result.computeIfAbsent(link.getDriverId(), id -> new ArrayList<>()).add(link.getVehicleId());
        }
        result.values().forEach(list -> list.sort(null));
        return result;
    }

    private String hash(String normalizedLicenceNumber) {
        return blindIndex.hash(DriverRules.LICENCE_HASH_PURPOSE, normalizedLicenceNumber);
    }

    private LocalDate today() {
        return LocalDate.ofInstant(clock.instant(), DriverRules.ZONE);
    }

    private void saveAndFlush(Driver driver) {
        try {
            drivers.saveAndFlush(driver);
        } catch (DataIntegrityViolationException e) {
            String cause = String.valueOf(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
            if (cause.contains(UNIQUE_LICENCE_INDEX)) {
                throw alreadyRegistered();
            }
            if (cause.contains(UNIQUE_OWNER_INDEX)) {
                throw ownerAlreadyExists();
            }
            throw e;
        }
    }

    private static DomainException alreadyRegistered() {
        return DomainException.of(ErrorCodes.DRIVER_ALREADY_REGISTERED,
                "The customer already has an active driver with this licence");
    }

    private static DomainException ownerAlreadyExists() {
        return DomainException.of(ErrorCodes.OWNER_DRIVER_ALREADY_EXISTS,
                "The customer is already registered as a driver");
    }

    /** Estado gravado na auditoria: o número da carta só mascarado. */
    public record AuditState(String fullName, LocalDate birthDate, String licenceNumberMasked,
                             List<String> licenceCategories, LocalDate licenceIssueDate, boolean isOwner,
                             String status) {

        static AuditState of(Driver d) {
            return new AuditState(d.getFullName(), d.getBirthDate(), d.licenceNumberMasked(),
                    List.copyOf(d.getLicenceCategories()), d.getLicenceIssueDate(), d.isOwner(), d.getStatus().name());
        }
    }
}
