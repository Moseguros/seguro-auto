package mz.co.moseguros.catalog.api;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.application.ProductService;
import mz.co.moseguros.catalog.application.ProductVersionService;
import mz.co.moseguros.catalog.domain.ProductStatus;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Produtos da seguradora do utilizador (S2-04 §3): leitura {@code product:read}, escrita
 * {@code product:manage}. Um produto de outra seguradora dá 404 {@code PRODUCT_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/insurer/products")
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class InsurerProductController {

    static final Set<String> SORTS = Set.of("name", "code", "createdAt");

    private final ProductService products;
    private final ProductVersionService versions;

    public InsurerProductController(ProductService products, ProductVersionService versions) {
        this.products = products;
        this.versions = versions;
    }

    @GetMapping
    @Operation(operationId = "listInsurerProducts", summary = "Lista os produtos da seguradora",
            description = "Permissão product:read. q procura no nome ou no código (sem diferenciar maiúsculas). "
                    + "Ordenação por name (defeito, asc), code ou createdAt.")
    public PageResponse<ProductSummaryResponse> list(
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "name, code ou createdAt, com ,asc ou ,desc", example = "name,asc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(products.list(status, type, q,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.ASC, "name"))),
                ProductSummaryResponse::from);
    }

    @PostMapping
    @Operation(operationId = "createInsurerProduct", summary = "Cria um produto",
            description = "Permissão product:manage. Cria o produto em DRAFT e a versão 1 DRAFT vazia. Código "
                    + "repetido na seguradora → 409 PRODUCT_CODE_ALREADY_EXISTS.")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse body = ProductResponse.from(products.create(request.code(), request.name(), request.type(),
                request.description()));
        return ResponseEntity.created(URI.create("/api/v1/insurer/products/" + body.id())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getInsurerProduct", summary = "Consulta um produto",
            description = "Permissão product:read. Inclui as versões, da mais recente para a mais antiga.")
    public ProductResponse get(@PathVariable UUID id) {
        return ProductResponse.from(products.get(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateInsurerProduct", summary = "Altera um produto",
            description = "Permissão product:manage. Mudar type depois de o produto ter sido publicado → 409 "
                    + "PRODUCT_TYPE_LOCKED; produto retirado → 409 PRODUCT_RETIRED; version diferente → 409 "
                    + "CONCURRENT_MODIFICATION.")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        return ProductResponse.from(products.update(id, request.name(), request.type(), request.description(),
                request.version()));
    }

    @PostMapping("/{id}/retire")
    @Operation(operationId = "retireInsurerProduct", summary = "Retira um produto",
            description = "Permissão product:manage. Irreversível: o produto passa a RETIRED e a versão em vigor a "
                    + "RETIRED (valid_to = hoje). Já retirado → 409 PRODUCT_RETIRED.")
    public ProductResponse retire(@PathVariable UUID id) {
        return ProductResponse.from(products.retire(id));
    }

    @PostMapping("/{id}/versions")
    @Operation(operationId = "createProductVersion", summary = "Cria uma nova versão do produto",
            description = "Permissão product:manage. Copia a última versão não-DRAFT (categorias, usos, regras, "
                    + "coberturas e documentos). Já existe uma DRAFT → 409 PRODUCT_DRAFT_EXISTS; produto retirado → "
                    + "409 PRODUCT_RETIRED.")
    public ResponseEntity<ProductVersionResponse> createVersion(@PathVariable UUID id) {
        ProductVersionResponse body = ProductVersionResponse.from(versions.create(id));
        return ResponseEntity.created(URI.create("/api/v1/insurer/product-versions/" + body.id())).body(body);
    }
}
