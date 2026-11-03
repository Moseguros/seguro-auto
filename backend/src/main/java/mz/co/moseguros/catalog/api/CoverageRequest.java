package mz.co.moseguros.catalog.api;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.domain.CoverageSpec;
import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura de uma versão (S2-04 §3, tabela {@code CoverageRequest}). As regras são validadas no
 * serviço ({@code CoverageRules}); os erros vêm em {@code errors[]} com
 * {@code field = "coverages[i].<campo>"}.
 */
public record CoverageRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Sem repetidos na lista (DUPLICATED)")
        CoverageCode code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "O cliente não pode retirar esta cobertura")
        Boolean mandatory,
        @Schema(description = "Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o "
                + "próprio código (INVALID_REQUIREMENT)")
        List<CoverageCode> requiresCodes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        CapitalType capitalType,
        @Schema(description = "Obrigatório em FIXED e PER_PERSON (por pessoa), proibido nos outros; > 0")
        Money capital,
        @Schema(description = "Obrigatório em OPTIONS, proibido nos outros; 2 a 10 valores > 0 em ordem estritamente "
                + "crescente (CAPITAL_OPTIONS_INVALID)")
        List<Money> capitalOptions,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        DeductibleType deductibleType,
        @Schema(description = "Obrigatório em FIXED, proibido nos outros; > 0")
        Money deductibleAmount,
        @Schema(description = "Obrigatório em PERCENT, proibido nos outros; > 0 e <= 100, até 2 casas decimais",
                example = "10.00")
        BigDecimal deductiblePercent,
        @Schema(description = "Opcional em PERCENT (mínimo da franquia), proibido nos outros; > 0")
        Money deductibleMinAmount,
        @ArraySchema(arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "1 a 20 códigos ISO 3166-1 alfa-2 sem repetidos; inclui MZ"),
                schema = @Schema(example = "MZ"))
        List<String> territorialScope,
        @Schema(maxLength = 1000)
        String description,
        @Schema(maxLength = 4000, description = "Exclusões resumidas; o texto legal está nas condições gerais")
        String exclusions) {

    CoverageSpec toSpec() {
        return new CoverageSpec(code, mandatory, requiresCodes, capitalType, capital, capitalOptions, deductibleType,
                deductibleAmount, deductiblePercent, deductibleMinAmount, territorialScope, description, exclusions);
    }
}
