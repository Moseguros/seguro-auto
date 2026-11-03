package mz.co.moseguros.vehicle.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.vehicle.application.VehicleDetails;
import mz.co.moseguros.vehicle.domain.Vehicle;

/**
 * Veículo do cliente. {@code engineCc}, {@code grossWeightKg}, {@code commercialValue} e
 * {@code registrationDoc} são omitidos quando não existem.
 */
public record VehicleResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AAA-100-MC")
        String plate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "JTDBR32E720012345")
        String chassisNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Toyota")
        String make,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Corolla")
        String model,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2018")
        int year,
        @Schema(example = "1598")
        Integer engineCc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleCategory category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
        int seats,
        Integer grossWeightKg,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleUsage usage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province provinceOfCirculation,
        Money commercialValue,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasTracker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "true se o registo automóvel confirmou a matrícula e o chassis")
        boolean inatroVerified,
        RegistrationDocResponse registrationDoc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Condutores activos, o habitual primeiro")
        List<VehicleDriverResponse> drivers,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static VehicleResponse from(VehicleDetails details) {
        Vehicle v = details.vehicle();
        return new VehicleResponse(v.getId(), v.getPlate().value(), v.getChassisNumber(), v.getMake(), v.getModel(),
                v.getYear(), v.getEngineCc(), v.getCategory(), v.getSeats(), v.getGrossWeightKg(), v.getUsage(),
                v.getProvinceOfCirculation(), v.getCommercialValue(), v.hasTracker(), v.isInatroVerified(),
                RegistrationDocResponse.from(details.registrationDoc()),
                details.drivers().stream().map(VehicleDriverResponse::from).toList(),
                v.getVersion(), v.getCreatedAt(), v.getUpdatedAt());
    }

    /** Livrete actual. */
    public record RegistrationDocResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Descarregar com GET /api/v1/files/{fileId} (downloadFile)")
            UUID fileId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "image/jpeg")
            String contentType,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "812345")
            long sizeBytes,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant uploadedAt) {

        static RegistrationDocResponse from(FileRef file) {
            return file == null ? null
                    : new RegistrationDocResponse(file.id(), file.contentType(), file.sizeBytes(), file.createdAt());
        }
    }

    /** Condutor associado ao veículo. */
    public record VehicleDriverResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID driverId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
            String fullName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean habitual) {

        static VehicleDriverResponse from(VehicleDetails.DriverLink link) {
            return new VehicleDriverResponse(link.driverId(), link.fullName(), link.habitual());
        }
    }
}
