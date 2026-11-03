package mz.co.moseguros.catalog.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionStatus;

/**
 * Versão na ficha do produto (sem coberturas nem documentos).
 *
 * @param publishedBy {@code sub} de quem publicou
 */
public record ProductVersionRefResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
        int versionNo,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductVersionStatus status,
        String changeNotes,
        LocalDate validFrom,
        LocalDate validTo,
        Instant publishedAt,
        String publishedBy) {

    static ProductVersionRefResponse from(ProductVersion v) {
        return new ProductVersionRefResponse(v.getId(), v.getVersionNo(), v.getStatus(), v.getChangeNotes(),
                v.getValidFrom(), v.getValidTo(), v.getPublishedAt(), v.getPublishedBy());
    }
}
