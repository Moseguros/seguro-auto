package mz.co.moseguros.rating.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.CoverageInput;
import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura pedida, além das obrigatórias (a {@code MTPL} pode vir ou não; é sempre incluída).
 *
 * @param capital capital pretendido; só conta nas coberturas com opções de capital ou por pessoa
 *                ({@code null} = o menor oferecido)
 */
public record RequestedCoverageRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        CoverageCode code,
        @Schema(description = "Capital pretendido (> 0); só conta em coberturas OPTIONS e PER_PERSON")
        Money capital) {

    static List<CoverageInput> toInputs(List<RequestedCoverageRequest> coverages) {
        return coverages == null ? List.of()
                : coverages.stream().map(c -> c == null ? null : new CoverageInput(c.code(), c.capital())).toList();
    }
}
