package mz.co.moseguros.vehicle.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.vehicle.domain.DriverRules.DriverData;

/** Os campos de {@link CreateDriverRequest}, com as mesmas regras, mais {@code version}. */
public record UpdateDriverRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 120,
                example = "Ana Maria Mussa")
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1990-05-14")
        LocalDate birthDate,
        @Schema(description = "Omitido ou null mantém o actual", example = "MAP-1234567")
        String licenceNumber,
        @ArraySchema(schema = @Schema(example = "B"), minItems = 1, maxItems = 12, uniqueItems = true,
                arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
        List<String> licenceCategories,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2010-03-01")
        LocalDate licenceIssueDate,
        Boolean isOwner,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    DriverData toData() {
        return new DriverData(fullName, birthDate, licenceNumber, licenceCategories, licenceIssueDate, isOwner);
    }

    /** Sem o número da carta, para nunca ir para um log. */
    @Override
    public String toString() {
        return "UpdateDriverRequest[licenceCategories=" + licenceCategories + ", isOwner=" + isOwner + ", version="
                + version + "]";
    }
}
