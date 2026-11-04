package mz.co.moseguros.document;

import java.util.Objects;
import java.util.UUID;

/**
 * Dono e metadados do PDF gerado por {@link DocumentApi#renderPdf} (S4-04 §1, decisão D5): o ficheiro é
 * guardado e ligado a este dono numa só chamada.
 *
 * @param ownerType      tem de ter uma {@link FileAccessPolicy} registada (ex.: {@code PROPOSAL})
 * @param ownerId        entidade dona, noutro módulo
 * @param purpose        finalidade dentro do dono (ex.: {@code PROPOSAL_DOCUMENT})
 * @param insurerId      seguradora a que o ficheiro pertence (opcional)
 * @param classification sensibilidade do conteúdo
 * @param fileName       nome do ficheiro (ex.: {@code PR-2027-000123-r3.pdf})
 */
public record PdfTarget(String ownerType, UUID ownerId, String purpose, UUID insurerId,
                        FileClassification classification, String fileName) {

    public PdfTarget {
        Objects.requireNonNull(ownerType, "ownerType");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(purpose, "purpose");
        Objects.requireNonNull(classification, "classification");
    }
}
