package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.ReferenceValue;

/**
 * Valor de referência na lista pública.
 *
 * @param labelEn    ausente → os ecrãs mostram {@code labelPt}
 * @param parentCode só nos modelos: código da marca
 */
public record PublicReferenceValueResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "TOYOTA_HILUX")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Hilux")
        String labelPt,
        String labelEn,
        @Schema(example = "TOYOTA")
        String parentCode) {

    static PublicReferenceValueResponse from(ReferenceValue v) {
        return new PublicReferenceValueResponse(v.getCode(), v.getLabelPt(), v.getLabelEn(), v.getParentCode());
    }
}
