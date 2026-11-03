package mz.co.moseguros.vehicle.infrastructure;

import mz.co.moseguros.shared.domain.VehicleCategory;

/** Dados de um veículo no registo automóvel ({@link VehicleRegistryPort}). */
public record RegistryVehicleData(String plate, String chassisNumber, String make, String model, int year,
                                  Integer engineCc, VehicleCategory category, int seats, Integer grossWeightKg) {
}
