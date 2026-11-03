package mz.co.moseguros.vehicle;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Cópia dos dados de um condutor num instante ({@link VehicleApi#driverSnapshot}). Nunca leva o
 * número da carta em claro: quem o precisar de comparar usa {@code licenceNumberHash} (índice cego,
 * finalidade {@code licence}).
 *
 * @param licenceNumberMasked {@code ****1234}
 * @param takenAt             quando a cópia foi tirada
 */
public record DriverSnapshot(UUID driverId, UUID ownerUserId, String fullName, LocalDate birthDate,
                             String licenceNumberMasked, String licenceNumberHash, List<String> licenceCategories,
                             LocalDate licenceIssueDate, boolean isOwner, Instant takenAt) {
}
