package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Lista pública e completa (não paginada) dos valores activos de um domínio. */
public record PublicReferenceValuesResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<PublicReferenceValueResponse> values) {
}
