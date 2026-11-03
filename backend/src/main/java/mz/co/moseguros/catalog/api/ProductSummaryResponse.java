package mz.co.moseguros.catalog.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.application.ProductService.ProductSummary;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductStatus;

/**
 * Linha da lista de produtos da seguradora.
 *
 * @param currentVersionNo número da versão em vigor; ausente sem versão publicada
 * @param hasDraft         há uma versão em preparação
 */
public record ProductSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AUTO-COMPLETO")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Completo")
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductStatus status,
        Integer currentVersionNo,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasDraft,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static ProductSummaryResponse from(ProductSummary summary) {
        Product p = summary.product();
        return new ProductSummaryResponse(p.getId(), p.getCode(), p.getName(), p.getType(), p.getStatus(),
                summary.currentVersionNo(), summary.hasDraft(), p.getUpdatedAt());
    }
}
