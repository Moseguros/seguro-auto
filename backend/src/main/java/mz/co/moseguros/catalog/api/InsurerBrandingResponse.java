package mz.co.moseguros.catalog.api;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Imagem da seguradora.
 *
 * @param logoFileId ficheiro do logótipo ({@code GET /api/v1/files/{id}}, público); {@code null} sem logótipo
 */
public record InsurerBrandingResponse(
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        UUID logoFileId) {
}
