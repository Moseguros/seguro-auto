package mz.co.moseguros.catalog.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.ReferenceDomain;
import mz.co.moseguros.catalog.domain.ReferenceValue;

/** Valor de referência para o administrador. */
public record ReferenceValueResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ReferenceDomain domain,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "TOYOTA_HILUX")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Hilux")
        String labelPt,
        String labelEn,
        @Schema(example = "TOYOTA")
        String parentCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
        int sortOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean active,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static ReferenceValueResponse from(ReferenceValue v) {
        return new ReferenceValueResponse(v.getId(), v.getDomain(), v.getCode(), v.getLabelPt(), v.getLabelEn(),
                v.getParentCode(), v.getSortOrder(), v.isActive(), v.getVersion(), v.getUpdatedAt());
    }
}
