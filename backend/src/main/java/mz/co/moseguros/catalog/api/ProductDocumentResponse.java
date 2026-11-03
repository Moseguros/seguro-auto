package mz.co.moseguros.catalog.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.ProductDocumentView;

/**
 * Documento de uma versão; descarrega-se em {@code GET /api/v1/files/{fileId}}.
 *
 * @param title ausente: os ecrãs mostram o nome do tipo
 */
public record ProductDocumentResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductDocumentType docType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"pt", "en"}, example = "pt")
        String language,
        String title,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID fileId) {

    static ProductDocumentResponse from(ProductDocumentView d) {
        return new ProductDocumentResponse(d.id(), d.docType(), d.language(), d.title(), d.fileId());
    }
}
