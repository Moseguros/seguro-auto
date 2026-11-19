package mz.co.moseguros.payment.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/** Resposta de {@code createReferenceDocument}: o PDF descarrega-se em {@code GET /api/v1/files/{fileId}}. */
public record ReferenceDocumentResponse(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID fileId) {
}
