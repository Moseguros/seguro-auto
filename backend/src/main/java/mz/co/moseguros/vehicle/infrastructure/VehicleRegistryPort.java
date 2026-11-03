package mz.co.moseguros.vehicle.infrastructure;

import java.util.Optional;

import mz.co.moseguros.shared.domain.Matricula;

/**
 * Consulta ao registo automóvel (INATRO) por matrícula (S2-03 §5; modelo transversal §9). A
 * implementação escolhe-se por {@code moseguros.vehicle.registry.provider}; até haver acesso só
 * existe {@code mock} ({@link MockVehicleRegistry}).
 */
public interface VehicleRegistryPort {

    /**
     * Dados registados da matrícula; vazio se não houver registo.
     *
     * @throws VehicleRegistryUnavailableException se o registo não responder
     */
    Optional<RegistryVehicleData> lookup(Matricula plate);
}
