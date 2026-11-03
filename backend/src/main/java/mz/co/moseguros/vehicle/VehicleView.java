package mz.co.moseguros.vehicle;

import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Veículo de um cliente, como o vêem os outros módulos ({@link VehicleApi#findOwned}).
 *
 * @param habitualDriverId condutor habitual activo; {@code null} sem condutores
 * @param status           {@code ACTIVE} ou {@code REMOVED}
 */
public record VehicleView(UUID id, UUID ownerUserId, String plate, String plateFormatCode, String chassisNumber,
                          String make, String model, int year, Integer engineCc, VehicleCategory category, int seats,
                          Integer grossWeightKg, VehicleUsage usage, Province provinceOfCirculation,
                          Money commercialValue, boolean hasTracker, boolean inatroVerified,
                          UUID registrationDocFileId, UUID habitualDriverId, String status, long version) {
}
