package mz.co.moseguros.rating.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.DriverInput;

/**
 * Condutor escrito à mão (mediador e simulador, S3-02 §6 e §12): só o que a tarifa usa. Sem nome
 * nem número da carta.
 */
public record RiskDriverRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "No passado; idade até 100 anos",
                example = "1991-09-09")
        LocalDate birthDate,
        @Schema(description = "Opcional; entre o nascimento e hoje", example = "2011-04-04")
        LocalDate licenceIssueDate,
        @ArraySchema(arraySchema = @Schema(description = "Opcional (0–12): códigos activos em LICENCE_CATEGORY"),
                maxItems = 12)
        List<String> licenceCategories) {

    DriverInput toInput() {
        return new DriverInput(birthDate, licenceIssueDate, licenceCategories);
    }
}
