package mz.co.moseguros.vehicle;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Cópia dos dados de um veículo num instante ({@link VehicleApi#snapshot}), para as cotações e
 * propostas guardarem o que foi usado.
 *
 * @param habitualDriverId condutor habitual activo; {@code null} sem condutores
 * @param takenAt          quando a cópia foi tirada
 */
public record VehicleSnapshot(UUID vehicleId, UUID ownerUserId, String plate, String chassisNumber,
                              String make, String model, int year, Integer engineCc, VehicleCategory category,
                              int seats, Integer grossWeightKg, VehicleUsage usage, Province provinceOfCirculation,
                              Money commercialValue, boolean hasTracker, boolean inatroVerified,
                              UUID habitualDriverId, Instant takenAt) {
}
