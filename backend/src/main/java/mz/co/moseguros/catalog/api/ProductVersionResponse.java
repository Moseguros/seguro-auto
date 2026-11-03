package mz.co.moseguros.catalog.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.application.CatalogViews;
import mz.co.moseguros.catalog.application.ProductVersionService.VersionDetails;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionStatus;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Versão de um produto, com as coberturas e os documentos.
 *
 * @param editable {@code DRAFT} e produto não retirado
 * @param version  bloqueio optimista da versão (enviar nos pedidos de alteração)
 */
public record ProductVersionResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID productId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AUTO-COMPLETO")
        String productCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Completo")
        String productName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType productType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
        int versionNo,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductVersionStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<VehicleCategory> vehicleCategories,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<VehicleUsage> usages,
        String changeNotes,
        UUID basedOnVersionId,
        LocalDate validFrom,
        LocalDate validTo,
        Instant publishedAt,
        String publishedBy,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<CoverageResponse> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProductDocumentResponse> documents,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean editable,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static ProductVersionResponse from(VersionDetails details) {
        ProductVersion v = details.version();
        Product p = details.product();
        return new ProductVersionResponse(v.getId(), p.getId(), p.getCode(), p.getName(), p.getType(),
                v.getVersionNo(), v.getStatus(), v.getVehicleCategories(), v.getUsages(), v.getChangeNotes(),
                v.getBasedOnVersionId(), v.getValidFrom(), v.getValidTo(), v.getPublishedAt(), v.getPublishedBy(),
                details.coverages().stream().map(CatalogViews::coverage).map(CoverageResponse::full).toList(),
                details.documents().stream().map(CatalogViews::document).map(ProductDocumentResponse::from).toList(),
                details.editable(), v.getVersion(), v.getCreatedAt(), v.getUpdatedAt());
    }
}
