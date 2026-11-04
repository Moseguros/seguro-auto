package mz.co.moseguros.vehicle.application;

import java.time.Clock;
import java.time.Instant;

import mz.co.moseguros.vehicle.domain.VehicleRules;
import mz.co.moseguros.vehicle.domain.VehicleRules.ValidVehicle;
import mz.co.moseguros.vehicle.infrastructure.VehicleRegistryPort;
import mz.co.moseguros.vehicle.infrastructure.VehicleRegistryUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Consulta ao registo automóvel na criação e na alteração de um veículo (S2-03 §3.3), partilhada pelo
 * {@link VehicleService} e pelo {@link VehicleSystemLookups} (S4-06).
 */
@Component
class RegistryVerification {

    private static final Logger log = LoggerFactory.getLogger(RegistryVerification.class);

    private final VehicleRegistryPort registry;
    private final Clock clock;

    RegistryVerification(VehicleRegistryPort registry, Clock clock) {
        this.registry = registry;
        this.clock = clock;
    }

    /**
     * Instante da verificação se a matrícula existe com o mesmo chassis; {@code null} se não existir, o
     * chassis for diferente ou o registo não responder.
     */
    Instant verify(ValidVehicle valid) {
        try {
            return registry.lookup(valid.plate())
                    .filter(found -> valid.chassisNumber().equals(VehicleRules.normalizeChassis(found.chassisNumber())))
                    .map(found -> clock.instant())
                    .orElse(null);
        } catch (VehicleRegistryUnavailableException e) {
            log.warn("Vehicle registry unavailable; plate {} saved without verification", valid.plate().masked());
            return null;
        }
    }
}
