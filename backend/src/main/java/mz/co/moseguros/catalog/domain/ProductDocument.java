package mz.co.moseguros.catalog.domain;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Documento legal de uma versão de produto ({@code catalog.product_document}, S2-04). O ficheiro
 * está no módulo {@code document} ({@code document.file}, sem FK); várias versões podem apontar
 * para o mesmo ficheiro (uma versão nova copia as linhas).
 */
@Entity
@Table(schema = "catalog", name = "product_document")
public class ProductDocument extends BaseEntity {

    public static final int MAX_PER_VERSION = 10;

    @Column(name = "product_version_id", nullable = false, updatable = false)
    private UUID productVersionId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, updatable = false, length = 20)
    private ProductDocumentType docType;

    @Column(name = "language", nullable = false, updatable = false, length = 2)
    private String language;

    @Column(name = "title", length = 120)
    private String title;

    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    protected ProductDocument() {
    }

    public static ProductDocument create(ProductVersion version, ProductDocumentType docType, String language,
                                         String title, UUID fileId) {
        Objects.requireNonNull(version, "version");
        ProductDocument document = new ProductDocument();
        document.productVersionId = version.getId();
        document.insurerId = version.getInsurerId();
        document.docType = Objects.requireNonNull(docType, "docType");
        document.language = Objects.requireNonNull(language, "language");
        document.title = Product.blankToNull(title);
        document.fileId = Objects.requireNonNull(fileId, "fileId");
        return document;
    }

    /** Cópia para outra versão, com o mesmo ficheiro. */
    public ProductDocument copyTo(ProductVersion version) {
        return create(version, docType, language, title, fileId);
    }

    /** Novo ficheiro (e título) de um documento principal enviado outra vez. */
    public void replaceFile(UUID newFileId, String newTitle) {
        this.fileId = Objects.requireNonNull(newFileId, "newFileId");
        this.title = Product.blankToNull(newTitle);
    }

    public UUID getProductVersionId() {
        return productVersionId;
    }

    public ProductDocumentType getDocType() {
        return docType;
    }

    public String getLanguage() {
        return language;
    }

    public String getTitle() {
        return title;
    }

    public UUID getFileId() {
        return fileId;
    }
}
