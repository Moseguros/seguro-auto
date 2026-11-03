package mz.co.moseguros.catalog.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.application.ProductService.ProductDetails;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductStatus;
import mz.co.moseguros.catalog.domain.ProductVersionStatus;

/**
 * Produto com as suas versões (da mais recente para a mais antiga).
 *
 * @param currentVersion versão em vigor ({@code PUBLISHED}), se houver
 * @param draftVersion   versão em preparação ({@code DRAFT}), se houver
 * @param version        bloqueio optimista do produto
 */
public record ProductResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AUTO-COMPLETO")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Completo")
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType type,
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductStatus status,
        Instant retiredAt,
        ProductVersionRefResponse currentVersion,
        ProductVersionRefResponse draftVersion,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProductVersionRefResponse> versions,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static ProductResponse from(ProductDetails details) {
        Product p = details.product();
        List<ProductVersionRefResponse> versions = details.versions().stream()
                .map(ProductVersionRefResponse::from).toList();
        return new ProductResponse(p.getId(), p.getCode(), p.getName(), p.getType(), p.getDescription(),
                p.getStatus(), p.getRetiredAt(), find(versions, ProductVersionStatus.PUBLISHED),
                find(versions, ProductVersionStatus.DRAFT), versions, p.getVersion(), p.getCreatedAt(),
                p.getUpdatedAt());
    }

    private static ProductVersionRefResponse find(List<ProductVersionRefResponse> versions,
                                                  ProductVersionStatus status) {
        return versions.stream().filter(v -> v.status() == status).findFirst().orElse(null);
    }
}
