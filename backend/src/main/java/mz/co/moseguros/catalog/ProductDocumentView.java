package mz.co.moseguros.catalog;

import java.util.UUID;

/**
 * Documento de uma versão de produto; o conteúdo descarrega-se em {@code GET /api/v1/files/{fileId}}.
 *
 * @param language {@code pt} ou {@code en}
 * @param title    {@code null}: os ecrãs mostram o nome do tipo
 */
public record ProductDocumentView(UUID id, ProductDocumentType docType, String language, String title, UUID fileId) {
}
