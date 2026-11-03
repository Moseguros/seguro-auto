package mz.co.moseguros.catalog.application;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.catalog.application.ProductVersionService.Editing;
import mz.co.moseguros.catalog.application.ProductVersionService.VersionDetails;
import mz.co.moseguros.catalog.domain.ProductRepository;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação de {@code CatalogApi.insurerVersion} e {@code CatalogApi.updateRatingRules}
 * (S3-01 §5): versões de qualquer estado, no âmbito de quem chama. Não usa o
 * {@link CatalogSystemLookups}: lê pelo repositório normal e o RLS mostra só as versões da
 * seguradora do {@code TenantContext}. As permissões são verificadas por quem chama (o
 * {@code rating}).
 */
@Service
public class InsurerVersionService {

    private final ProductVersionRepository versions;
    private final ProductRepository products;
    private final ProductVersionService versionService;

    public InsurerVersionService(ProductVersionRepository versions, ProductRepository products,
                                 ProductVersionService versionService) {
        this.versions = versions;
        this.products = products;
        this.versionService = versionService;
    }

    /** @throws DomainException {@code PRODUCT_VERSION_NOT_FOUND} (404) se não for visível */
    @Transactional(readOnly = true)
    public ProductVersionView view(UUID versionId) {
        ProductVersion version = visible(versionId);
        VersionDetails details = versionService.details(version, products.findById(version.getProductId())
                .orElseThrow(InsurerVersionService::notFound));
        return CatalogViews.version(details.version(), details.product(), details.coverages(), details.documents());
    }

    /**
     * Grava {@code rules.rating} de uma versão {@code DRAFT} e incrementa a {@code version}.
     *
     * @return a nova {@code version}
     * @throws DomainException {@code PRODUCT_VERSION_NOT_FOUND} (404), {@code PRODUCT_VERSION_NOT_EDITABLE},
     *                         {@code PRODUCT_RETIRED} ou {@code CONCURRENT_MODIFICATION} (409)
     */
    @Transactional
    public long updateRatingRules(UUID versionId, JsonNode rating, long expectedVersion) {
        Objects.requireNonNull(rating, "rating");
        visible(versionId);
        Editing editing = versionService.beginEdit(versionId, expectedVersion, false);
        ProductVersion version = editing.version();
        version.replaceRatingRules(rating);
        versions.flush();
        if (version.getVersion() == expectedVersion) {
            versionService.forceIncrement(version); // tarifa igual: a escrita conta na mesma
        }
        return version.getVersion();
    }

    private ProductVersion visible(UUID versionId) {
        UUID insurerId = TenantContext.current().insurerId();
        if (versionId == null || insurerId == null) {
            throw notFound();
        }
        return versions.findById(versionId)
                .filter(v -> v.getInsurerId().equals(insurerId))
                .orElseThrow(InsurerVersionService::notFound);
    }

    private static DomainException notFound() {
        return DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_FOUND, "Product version not found");
    }
}
