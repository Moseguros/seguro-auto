package mz.co.moseguros.catalog;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Versão de um produto, para outros módulos ({@link CatalogApi}).
 *
 * @param status                  {@code PUBLISHED}, {@code SUPERSEDED} ou {@code RETIRED} (o
 *                                {@link CatalogApi} só devolve versões que foram publicadas)
 * @param generalConditionsFileId ficheiro das condições gerais em pt (atalho sobre {@code documents})
 * @param preContractInfoFileId   ficheiro da informação pré-contratual em pt
 * @param rules                   regras da versão ({@code rating}, {@code underwriting}, {@code bonusMalus})
 * @param version                 bloqueio optimista da linha
 */
public record ProductVersionView(
        UUID id,
        UUID productId,
        String productCode,
        String productName,
        ProductType productType,
        UUID insurerId,
        int versionNo,
        String status,
        LocalDate validFrom,
        LocalDate validTo,
        List<VehicleCategory> vehicleCategories,
        List<VehicleUsage> usages,
        List<CoverageView> coverages,
        List<ProductDocumentView> documents,
        UUID generalConditionsFileId,
        UUID preContractInfoFileId,
        JsonNode rules,
        Instant publishedAt,
        long version) {
}
