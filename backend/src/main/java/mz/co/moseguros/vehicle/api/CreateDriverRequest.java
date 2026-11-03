package mz.co.moseguros.vehicle.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.vehicle.domain.DriverRules.DriverData;

/**
 * Condutor novo. Os erros de validação vêm todos juntos em {@code errors[]} ({@code REQUIRED},
 * {@code INVALID_NAME}, {@code INVALID_DATE}, {@code INVALID_LICENCE_NUMBER},
 * {@code LICENCE_CATEGORY_UNKNOWN}, {@code DUPLICATED}, {@code OUT_OF_RANGE},
 * {@code LICENCE_ISSUE_DATE_INVALID}).
 */
public record CreateDriverRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 120,
                description = "Letras, espaços, apóstrofo e hífen; pelo menos duas palavras",
                example = "Ana Maria Mussa")
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "No passado; idade até 100 anos",
                example = "1990-05-14")
        LocalDate birthDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "5–20 letras, dígitos, / ou - (espaços ignorados). Nunca é devolvido.",
                example = "MAP-1234567")
        String licenceNumber,
        @ArraySchema(schema = @Schema(description = "Código activo em LICENCE_CATEGORY", example = "B"),
                minItems = 1, maxItems = 12, uniqueItems = true,
                arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
        List<String> licenceCategories,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Entre o nascimento e hoje",
                example = "2010-03-01")
        LocalDate licenceIssueDate,
        @Schema(description = "O condutor é o próprio cliente (defeito false); só um por cliente")
        Boolean isOwner) {

    DriverData toData() {
        return new DriverData(fullName, birthDate, licenceNumber, licenceCategories, licenceIssueDate, isOwner);
    }

    /** Sem o número da carta, para nunca ir para um log. */
    @Override
    public String toString() {
        return "CreateDriverRequest[licenceCategories=" + licenceCategories + ", isOwner=" + isOwner + "]";
    }
}
