package mz.co.moseguros.policy;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipo de cobertura da apólice (S6-01 regra 5): {@code TEMPORARY} e {@code BORDER} pelo tipo de produto;
 * {@code COMPREHENSIVE} com alguma cobertura {@code OD_*}; senão {@code MTPL_ONLY}.
 */
@Schema(enumAsRef = true)
public enum CoverageType {
    MTPL_ONLY,
    COMPREHENSIVE,
    TEMPORARY,
    BORDER
}
