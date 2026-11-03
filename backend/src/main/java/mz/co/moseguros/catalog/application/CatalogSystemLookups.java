package mz.co.moseguros.catalog.application;

import java.text.Collator;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.catalog.domain.Coverage;
import mz.co.moseguros.catalog.domain.CoverageRepository;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductDocument;
import mz.co.moseguros.catalog.domain.ProductDocumentRepository;
import mz.co.moseguros.catalog.domain.ProductRepository;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras do catálogo entre seguradoras (S2-04 §6), em {@code SYSTEM}: o catálogo público, o
 * {@code CatalogApi} e a {@code ProductVersionFileAccessPolicy}. É o único caminho de leitura que
 * vê produtos de várias seguradoras, por isso filtra no código e devolve <b>só versões com
 * {@code published_at} preenchido</b> (nunca rascunhos). Só leitura.
 */
@Component
public class CatalogSystemLookups {

    private final ProductVersionRepository versions;
    private final ProductRepository products;
    private final CoverageRepository coverages;
    private final ProductDocumentRepository documents;
    private final OrganizationApi organizations;

    public CatalogSystemLookups(ProductVersionRepository versions, ProductRepository products,
                                CoverageRepository coverages, ProductDocumentRepository documents,
                                OrganizationApi organizations) {
        this.versions = versions;
        this.products = products;
        this.coverages = coverages;
        this.documents = documents;
        this.organizations = organizations;
    }

    /**
     * Versão publicada com a descrição do produto e o resumo da seguradora.
     *
     * @param insurer {@code null} se a organização não existir (não acontece com dados íntegros)
     */
    public record CatalogEntry(ProductVersionView version, String productDescription, OrganizationSummary insurer) {

        public boolean insurerActive() {
            return insurer != null && insurer.isActive();
        }
    }

    /** Seguradora e data de publicação de uma versão ({@code publishedAt} nulo = rascunho). */
    public record VersionAccess(UUID insurerId, Instant publishedAt) {
    }

    /**
     * Versões {@code PUBLISHED} de produtos não retirados, de seguradoras {@code ACTIVE}, com os
     * filtros opcionais ({@code null} = todos), por nome da seguradora e depois do produto.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public List<CatalogEntry> publishedVersions(VehicleCategory category, VehicleUsage usage, ProductType type) {
        List<ProductVersion> found = versions.findPublished(name(category), name(usage), name(type));
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-MZ"));
        return entries(found).stream()
                .filter(CatalogEntry::insurerActive)
                .sorted(Comparator.<CatalogEntry, String>comparing(e -> e.insurer().displayName(), collator)
                        .thenComparing(e -> e.version().productName(), collator)
                        .thenComparing(e -> e.version().id()))
                .toList();
    }

    /** Versão com {@code published_at} preenchido (qualquer estado depois de publicada), qualquer seguradora. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<CatalogEntry> publishedVersion(UUID versionId) {
        if (versionId == null) {
            return Optional.empty();
        }
        return versions.findById(versionId)
                .filter(v -> v.getPublishedAt() != null)
                .map(v -> entries(List.of(v)).get(0));
    }

    /** Seguradora e data de publicação da versão, para a {@code ProductVersionFileAccessPolicy}. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<VersionAccess> versionAccess(UUID versionId) {
        if (versionId == null) {
            return Optional.empty();
        }
        return versions.findById(versionId).map(v -> new VersionAccess(v.getInsurerId(), v.getPublishedAt()));
    }

    private List<CatalogEntry> entries(List<ProductVersion> found) {
        if (found.isEmpty()) {
            return List.of();
        }
        Set<UUID> versionIds = found.stream().map(ProductVersion::getId).collect(Collectors.toSet());
        Map<UUID, Product> productById = products.findByIdIn(found.stream().map(ProductVersion::getProductId)
                .collect(Collectors.toSet())).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        Map<UUID, List<Coverage>> coveragesByVersion = coverages.findByProductVersionIdInOrderBySortOrderAsc(versionIds)
                .stream().collect(Collectors.groupingBy(Coverage::getProductVersionId));
        Map<UUID, List<ProductDocument>> documentsByVersion = documents
                .findByProductVersionIdInOrderByCreatedAtAscIdAsc(versionIds)
                .stream().collect(Collectors.groupingBy(ProductDocument::getProductVersionId));
        Map<UUID, OrganizationSummary> insurers = organizations.findSummaries(found.stream()
                .map(ProductVersion::getInsurerId).collect(Collectors.toSet()));
        return found.stream().map(v -> {
            Product p = productById.get(v.getProductId());
            ProductVersionView view = CatalogViews.version(v, p, coveragesByVersion.getOrDefault(v.getId(), List.of()),
                    documentsByVersion.getOrDefault(v.getId(), List.of()));
            return new CatalogEntry(view, p.getDescription(), insurers.get(v.getInsurerId()));
        }).toList();
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
