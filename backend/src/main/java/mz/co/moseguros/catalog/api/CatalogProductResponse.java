package mz.co.moseguros.catalog.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.catalog.application.CatalogSystemLookups.CatalogEntry;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Produto no catálogo público: uma versão publicada, com a seguradora, as coberturas e os
 * documentos (descarregam-se sem login em {@code GET /api/v1/files/{fileId}}).
 *
 * @param status {@code PUBLISHED} na lista; na consulta por id também {@code SUPERSEDED} ou {@code RETIRED}
 */
public record CatalogProductResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID productVersionId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID productId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
        int versionNo,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PUBLISHED", "SUPERSEDED", "RETIRED"})
        String status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        CatalogInsurerResponse insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AUTO-COMPLETO")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Completo")
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType type,
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate validFrom,
        LocalDate validTo,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<VehicleCategory> vehicleCategories,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<VehicleUsage> usages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<CoverageResponse> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProductDocumentResponse> documents) {

    /** Seguradora do produto. */
    public record CatalogInsurerResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Seguradora Demo")
            String displayName) {
    }

    /** Coberturas só com capital e franquia (lista). */
    static CatalogProductResponse summary(CatalogEntry entry) {
        return of(entry, CoverageResponse::summary);
    }

    /** Coberturas completas (consulta por id). */
    static CatalogProductResponse full(CatalogEntry entry) {
        return of(entry, CoverageResponse::full);
    }

    private static CatalogProductResponse of(CatalogEntry entry, Function<CoverageView, CoverageResponse> coverage) {
        ProductVersionView v = entry.version();
        return new CatalogProductResponse(v.id(), v.productId(), v.versionNo(), v.status(),
                new CatalogInsurerResponse(entry.insurer().id(), entry.insurer().displayName()), v.productCode(),
                v.productName(), v.productType(), entry.productDescription(), v.validFrom(), v.validTo(),
                v.vehicleCategories(), v.usages(), v.coverages().stream().map(coverage).toList(),
                v.documents().stream().map(ProductDocumentResponse::from).toList());
    }
}
