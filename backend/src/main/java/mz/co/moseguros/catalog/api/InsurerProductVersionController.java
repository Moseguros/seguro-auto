package mz.co.moseguros.catalog.api;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.application.CatalogViews;
import mz.co.moseguros.catalog.application.ProductDocumentService;
import mz.co.moseguros.catalog.application.ProductDocumentService.Upload;
import mz.co.moseguros.catalog.application.ProductVersionService;
import mz.co.moseguros.catalog.application.PublicationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Versões dos produtos da seguradora do utilizador (S2-04 §3 e §4). Só a versão {@code DRAFT} se
 * altera (senão 409 {@code PRODUCT_VERSION_NOT_EDITABLE}); toda a escrita incrementa {@code version}.
 */
@RestController
@RequestMapping("/api/v1/insurer/product-versions")
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class InsurerProductVersionController {

    private final ProductVersionService versions;
    private final ProductDocumentService documents;
    private final PublicationService publication;

    public InsurerProductVersionController(ProductVersionService versions, ProductDocumentService documents,
                                           PublicationService publication) {
        this.versions = versions;
        this.documents = documents;
        this.publication = publication;
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getProductVersion", summary = "Consulta uma versão de um produto",
            description = "Permissão product:read. Inclui as coberturas e os documentos.")
    public ProductVersionResponse get(@PathVariable UUID id) {
        return ProductVersionResponse.from(versions.get(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateProductVersion", summary = "Altera a elegibilidade de uma versão",
            description = "Permissão product:manage. Versão que não está DRAFT → 409 PRODUCT_VERSION_NOT_EDITABLE; "
                    + "produto retirado → 409 PRODUCT_RETIRED; version diferente → 409 CONCURRENT_MODIFICATION.")
    public ProductVersionResponse update(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateProductVersionRequest request) {
        return ProductVersionResponse.from(versions.update(id, request.vehicleCategories(), request.usages(),
                request.changeNotes(), request.version()));
    }

    @PutMapping("/{id}/coverages")
    @Operation(operationId = "replaceProductVersionCoverages", summary = "Substitui as coberturas de uma versão",
            description = "Permissão product:manage. Até 12 coberturas; a ordem da lista é a ordem de apresentação. "
                    + "Erros de validação → 400 VALIDATION_ERROR com errors[].field = coverages[i].<campo>. 409 como "
                    + "em updateProductVersion.")
    public ProductVersionResponse replaceCoverages(@PathVariable UUID id,
                                                   @Valid @RequestBody ReplaceCoveragesRequest request) {
        return ProductVersionResponse.from(versions.replaceCoverages(id,
                request.coverages().stream().map(c -> c == null ? null : c.toSpec()).toList(), request.version()));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadProductVersionDocument", summary = "Envia um documento de uma versão",
            description = "Permissão product:manage. Só PDF (senão 415 FILE_TYPE_NOT_ALLOWED), até 10 MB. "
                    + "GENERAL_CONDITIONS e PRE_CONTRACT_INFO substituem o ficheiro do documento com o mesmo tipo e "
                    + "língua. Mais de 10 documentos → 409 PRODUCT_DOCUMENT_LIMIT_REACHED. 409 como em "
                    + "updateProductVersion.")
    public ResponseEntity<ProductDocumentResponse> uploadDocument(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file,
            @RequestParam("docType") ProductDocumentType docType,
            @Parameter(schema = @Schema(allowableValues = {"pt", "en"}, defaultValue = "pt"))
            @RequestParam(value = "language", required = false) String language,
            @Parameter(schema = @Schema(maxLength = 120))
            @RequestParam(value = "title", required = false) String title) throws IOException {
        ProductDocumentResponse body = ProductDocumentResponse.from(CatalogViews.document(documents.upload(id,
                new Upload(file.getBytes(), file.getOriginalFilename(), file.getContentType()), docType, language,
                title)));
        return ResponseEntity.created(URI.create("/api/v1/files/" + body.fileId())).body(body);
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @Operation(operationId = "deleteProductVersionDocument", summary = "Retira um documento de uma versão",
            description = "Permissão product:manage. O ficheiro fica guardado. Documento desconhecido → 404 "
                    + "PRODUCT_DOCUMENT_NOT_FOUND; 409 como em updateProductVersion.")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id, @PathVariable UUID documentId) {
        documents.delete(id, documentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/validate")
    @Operation(operationId = "validateProductVersion", summary = "Verifica as regras de publicação",
            description = "Permissão product:read. As mesmas regras da publicação, sem gravar; todas as falhas "
                    + "em errors[].")
    public ProductVersionValidationResponse validate(@PathVariable UUID id) {
        return ProductVersionValidationResponse.of(publication.validate(id));
    }

    @PostMapping("/{id}/publish")
    @Operation(operationId = "publishProductVersion", summary = "Publica uma versão",
            description = "Permissão product:manage. Entra em vigor hoje (Africa/Maputo); a versão em vigor passa a "
                    + "SUPERSEDED. Regras por cumprir → 400 PRODUCT_VERSION_NOT_PUBLISHABLE com todas as falhas em "
                    + "errors[]; 409 como em updateProductVersion.")
    public ProductVersionResponse publish(@PathVariable UUID id,
                                          @Valid @RequestBody PublishProductVersionRequest request) {
        return ProductVersionResponse.from(publication.publish(id, request.version()));
    }
}
