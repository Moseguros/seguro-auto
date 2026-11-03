package mz.co.moseguros.catalog.application;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.application.ProductVersionService.Editing;
import mz.co.moseguros.catalog.domain.ProductDocument;
import mz.co.moseguros.catalog.domain.ProductDocumentRepository;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Documentos legais das versões {@code DRAFT} (S2-04 §3). Os PDFs guardam-se no módulo
 * {@code document} ({@code ownerType = PRODUCT_VERSION}, {@code PUBLIC}); quem os lê decide a
 * {@code ProductVersionFileAccessPolicy}. Apagar um documento só apaga a linha (o ficheiro pode ser
 * de outra versão).
 */
@Service
public class ProductDocumentService {

    public static final String OWNER_TYPE = "PRODUCT_VERSION";
    static final Set<String> LANGUAGES = Set.of("pt", "en");
    static final int TITLE_MAX = 120;

    private final ProductVersionService versionService;
    private final ProductDocumentRepository documents;
    private final DocumentApi documentApi;

    public ProductDocumentService(ProductVersionService versionService, ProductDocumentRepository documents,
                                  DocumentApi documentApi) {
        this.versionService = versionService;
        this.documents = documents;
        this.documentApi = documentApi;
    }

    /** Ficheiro enviado. */
    public record Upload(byte[] content, String originalFilename, String contentType) {
    }

    /**
     * Guarda o PDF e liga-o à versão. {@code GENERAL_CONDITIONS} e {@code PRE_CONTRACT_INFO}
     * substituem o ficheiro do documento com o mesmo tipo e língua.
     *
     * @param language {@code null} = {@code pt}
     * @throws DomainException 409 {@code PRODUCT_VERSION_NOT_EDITABLE}, {@code PRODUCT_RETIRED},
     *                         {@code PRODUCT_DOCUMENT_LIMIT_REACHED}; 400 {@code VALIDATION_ERROR};
     *                         os erros de ficheiro do {@code DocumentApi} ({@code FILE_*})
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public ProductDocument upload(UUID versionId, Upload file, ProductDocumentType docType, String language,
                                  String title) {
        Editing editing = versionService.beginEdit(versionId, null, false);
        ProductVersion version = editing.version();
        if (docType == null) {
            throw new FieldValidationException("docType", FieldErrorCodes.REQUIRED, "docType is required");
        }
        String lang = language == null || language.isBlank() ? "pt" : language.trim();
        if (!LANGUAGES.contains(lang)) {
            throw new FieldValidationException("language", FieldErrorCodes.INVALID_VALUE, "language must be pt or en");
        }
        if (title != null && title.trim().length() > TITLE_MAX) {
            throw new FieldValidationException("title", FieldErrorCodes.SIZE, "title has at most 120 characters");
        }
        Optional<ProductDocument> existing = docType.isMain()
                ? documents.findByProductVersionIdAndDocTypeAndLanguage(versionId, docType, lang)
                : Optional.empty();
        if (existing.isEmpty() && documents.countByProductVersionId(versionId) >= ProductDocument.MAX_PER_VERSION) {
            throw DomainException.of(ErrorCodes.PRODUCT_DOCUMENT_LIMIT_REACHED,
                    "A version has at most " + ProductDocument.MAX_PER_VERSION + " documents");
        }
        versionService.forceIncrement(version);

        FileRef ref = documentApi.store(new StoreFileCommand(OWNER_TYPE, versionId, docType.name(),
                FileClassification.PUBLIC, TenantContext.insurerId(), file.originalFilename(), file.contentType(),
                Set.of("application/pdf"), file.content()));
        ProductDocument document;
        Object before = null;
        if (existing.isPresent()) {
            document = existing.get();
            before = DocumentSnapshot.of(document);
            document.replaceFile(ref.id(), title);
        } else {
            document = documents.save(ProductDocument.create(version, docType, lang, title, ref.id()));
        }
        documentApi.markLinked(ref.id());
        documents.flush();
        versionService.audit(version, "PRODUCT_VERSION.DOCUMENT_ADD", before, DocumentSnapshot.of(document));
        return document;
    }

    /**
     * Retira o documento da versão {@code DRAFT}.
     *
     * @throws DomainException {@code PRODUCT_DOCUMENT_NOT_FOUND} (404), ou 409 como em {@link #upload}
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public void delete(UUID versionId, UUID documentId) {
        Editing editing = versionService.beginEdit(versionId, null, false);
        ProductDocument document = documents.findByIdAndProductVersionId(documentId, versionId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.PRODUCT_DOCUMENT_NOT_FOUND,
                        "Product document not found"));
        versionService.forceIncrement(editing.version());
        DocumentSnapshot before = DocumentSnapshot.of(document);
        documents.delete(document);
        documents.flush();
        versionService.audit(editing.version(), "PRODUCT_VERSION.DOCUMENT_REMOVE", before, null);
    }

    /** Documento auditado. */
    public record DocumentSnapshot(UUID id, ProductDocumentType docType, String language, String title, UUID fileId) {

        static DocumentSnapshot of(ProductDocument d) {
            return new DocumentSnapshot(d.getId(), d.getDocType(), d.getLanguage(), d.getTitle(), d.getFileId());
        }
    }
}
