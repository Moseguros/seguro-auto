package mz.co.moseguros.vehicle.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.vehicle.application.DriverDetails;
import mz.co.moseguros.vehicle.domain.Driver;

/** Condutor do cliente. O número da carta só sai mascarado. */
public record DriverResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1990-05-14")
        LocalDate birthDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Quatro * fixos e os últimos 4 caracteres", example = "****4567")
        String licenceNumberMasked,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> licenceCategories,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2010-03-01")
        LocalDate licenceIssueDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isOwner,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Veículos activos a que está associado")
        List<UUID> vehicleIds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static DriverResponse from(DriverDetails details) {
        Driver d = details.driver();
        return new DriverResponse(d.getId(), d.getFullName(), d.getBirthDate(), d.licenceNumberMasked(),
                List.copyOf(d.getLicenceCategories()), d.getLicenceIssueDate(), d.isOwner(),
                List.copyOf(details.vehicleIds()), d.getVersion(), d.getCreatedAt(), d.getUpdatedAt());
    }
}
