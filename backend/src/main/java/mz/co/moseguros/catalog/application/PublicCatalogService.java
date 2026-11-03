package mz.co.moseguros.catalog.application;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.application.CatalogSystemLookups.CatalogEntry;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Catálogo público, sem login (S2-04 §6): versões em vigor de produtos não retirados de seguradoras
 * activas. Lê por {@link CatalogSystemLookups}, que só devolve versões publicadas.
 *
 * <p>A ordem (nome da seguradora, depois do produto) usa o nome da seguradora, que está noutro
 * módulo: a lista filtra-se na BD e ordena-se e pagina-se em memória (o catálogo tem dezenas de
 * produtos, não milhares).
 */
@Service
public class PublicCatalogService {

    private final CatalogSystemLookups lookups;

    public PublicCatalogService(CatalogSystemLookups lookups) {
        this.lookups = lookups;
    }

    public Page<CatalogEntry> list(VehicleCategory category, VehicleUsage usage, ProductType type, Pageable pageable) {
        List<CatalogEntry> all = lookups.publishedVersions(category, usage, type);
        int from = (int) Math.min(pageable.getOffset(), all.size());
        int to = Math.min(from + pageable.getPageSize(), all.size());
        return new PageImpl<>(all.subList(from, to), pageable, all.size());
    }

    /**
     * Versão que foi publicada (também substituída ou retirada, para quem tem uma apólice antiga),
     * de uma seguradora activa.
     *
     * @throws DomainException {@code PRODUCT_VERSION_NOT_FOUND} (404) nos outros casos
     */
    public CatalogEntry get(UUID versionId) {
        return lookups.publishedVersion(versionId)
                .filter(CatalogEntry::insurerActive)
                .orElseThrow(() -> DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_FOUND,
                        "Product version not found"));
    }
}
