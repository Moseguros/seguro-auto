package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Motivos de um resultado {@code WARNED} (S4-02 §8), pela ordem em que se avaliam e se mostram. Os
 * avisos (provisórios, a validar juridicamente) estão nos frontends e no PDF.
 */
@Schema(enumAsRef = true)
public enum SuitabilityReason {
    NEED_NOT_COVERED_OWN_DAMAGE,
    NEED_NOT_COVERED_THEFT_FIRE,
    COVER_ABOVE_NEED,
    NEED_UNSURE,
    FINANCED_WITHOUT_OWN_DAMAGE,
    CANNOT_AFFORD_REPAIR,
    CROSS_BORDER_NOT_COVERED,
    DEDUCTIBLE_NOT_WANTED
}
