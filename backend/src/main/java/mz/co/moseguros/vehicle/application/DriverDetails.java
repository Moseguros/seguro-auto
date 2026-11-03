package mz.co.moseguros.vehicle.application;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.vehicle.domain.Driver;

/** Condutor como é mostrado ao dono, com os veículos {@code ACTIVE} a que está associado. */
public record DriverDetails(Driver driver, List<UUID> vehicleIds) {
}
