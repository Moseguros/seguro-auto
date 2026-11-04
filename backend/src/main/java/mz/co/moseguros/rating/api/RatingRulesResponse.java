package mz.co.moseguros.rating.api;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.application.RatingRulesService.RatingRulesDetails;
import mz.co.moseguros.shared.domain.Money;

/**
 * Tarifa interna de uma versão de produto (S3-01 §5).
 *
 * @param editable   {@code status == DRAFT}
 * @param configured a versão tem tarifa ({@code rules.rating})
 * @param rating     a tarifa ({@code schemas/rating-v1.json}); {@code null} sem tarifa
 * @param coverages  coberturas da versão, pela ordem (o editor mostra uma tabela por cobertura)
 * @param version    bloqueio optimista da versão
 */
public record RatingRulesResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID productVersionId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType productType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean editable,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean configured,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(ref = RatingRulesSchema.REF, nullable = true)
        JsonNode rating,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<RatingCoverage> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version) {

    static RatingRulesResponse from(RatingRulesDetails details) {
        return new RatingRulesResponse(details.version().id(), details.version().productType(), details.editable(),
                details.configured(), details.rating(),
                details.version().coverages().stream().map(RatingCoverage::from).toList(),
                details.version().version());
    }

    /**
     * Cobertura da versão.
     *
     * @param capital        {@code FIXED} e {@code PER_PERSON}; senão {@code null}
     * @param capitalOptions {@code OPTIONS}; senão {@code null}
     */
    public record RatingCoverage(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            CoverageCode code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean mandatory,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            CapitalType capitalType,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Money capital,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            List<Money> capitalOptions) {

        static RatingCoverage from(CoverageView c) {
            return new RatingCoverage(c.code(), c.mandatory(), c.capitalType(), c.capital(), c.capitalOptions());
        }
    }
}
