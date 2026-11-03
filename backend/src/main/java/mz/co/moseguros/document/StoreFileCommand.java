package mz.co.moseguros.document;

import java.util.Set;
import java.util.UUID;

/**
 * Pedido para guardar um ficheiro ({@link DocumentApi#store}).
 *
 * @param ownerType           obrigatório; tem de ter uma {@link FileAccessPolicy} registada
 * @param ownerId             obrigatório; id da entidade dona, noutro módulo
 * @param purpose             obrigatório; finalidade dentro do dono ({@code REGISTRATION_DOC}…)
 * @param classification      obrigatório
 * @param insurerId           opcional; seguradora a que o ficheiro pertence
 * @param originalFilename    opcional; {@code null} → {@code ficheiro}
 * @param declaredContentType tipo indicado pelo cliente HTTP (pode ser {@code application/octet-stream}
 *                            ou {@code null}); se indicado, tem de ser igual ao tipo real
 * @param allowedContentTypes opcional; {@code null} = os quatro tipos aceites; restringe (ex.: só
 *                            {@code application/pdf})
 * @param content             obrigatório
 */
public record StoreFileCommand(
        String ownerType,
        UUID ownerId,
        String purpose,
        FileClassification classification,
        UUID insurerId,
        String originalFilename,
        String declaredContentType,
        Set<String> allowedContentTypes,
        byte[] content) {
}
