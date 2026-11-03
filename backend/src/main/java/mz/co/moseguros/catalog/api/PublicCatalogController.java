package mz.co.moseguros.catalog.api;

import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.application.PublicCatalogService;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Catálogo público de produtos (S2-04 §6), sem login. */
@RestController
@RequestMapping("/api/v1/catalog")
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class PublicCatalogController {

    private final PublicCatalogService catalog;

    public PublicCatalogController(PublicCatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/products")
    @SecurityRequirements
    @Operation(operationId = "listCatalogProducts", summary = "Lista os produtos publicados",
            description = "Público. Versões em vigor de produtos não retirados de seguradoras activas, por nome da "
                    + "seguradora e depois do produto (ordem fixa). Com vehicleCategory ou usage, só as versões que os "
                    + "incluem. As coberturas levam só capital e franquia.")
    public PageResponse<CatalogProductResponse> list(
            @RequestParam(required = false) VehicleCategory vehicleCategory,
            @RequestParam(required = false) VehicleUsage usage,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return PageResponse.of(catalog.list(vehicleCategory, usage, type,
                        PageRequests.of(page, size, null, Set.of(), Sort.unsorted())),
                CatalogProductResponse::summary);
    }

    @GetMapping("/product-versions/{id}")
    @SecurityRequirements
    @Operation(operationId = "getCatalogProductVersion", summary = "Consulta uma versão publicada",
            description = "Público. Qualquer versão que foi publicada (também SUPERSEDED e RETIRED, para quem tem "
                    + "uma apólice antiga), de uma seguradora activa, com as coberturas completas; senão 404 "
                    + "PRODUCT_VERSION_NOT_FOUND.")
    public CatalogProductResponse get(@PathVariable UUID id) {
        return CatalogProductResponse.full(catalog.get(id));
    }
}
