package mz.co.moseguros.catalog;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.catalog.application.CatalogSystemLookups;
import mz.co.moseguros.catalog.application.CatalogSystemLookups.CatalogEntry;
import mz.co.moseguros.catalog.application.PlatformSettingService;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Catálogo (modelo transversal §3): parâmetros da plataforma (S2-02) e
 * versões publicadas de produtos (S2-04).
 *
 * <p>Funciona em qualquer âmbito, dentro ou fora de uma transacção: as tabelas de referência não
 * têm RLS, e as versões publicadas lêem-se em {@code SYSTEM} ({@link CatalogSystemLookups}).
 */
@Service
public class CatalogApi {

    private final PlatformSettingService settings;
    private final CatalogSystemLookups lookups;

    public CatalogApi(PlatformSettingService settings, CatalogSystemLookups lookups) {
        this.settings = settings;
        this.lookups = lookups;
    }

    /**
     * Valor actual de um parâmetro da plataforma, em texto ({@code MONEY} em centavos). Lê sempre
     * a base de dados: uma alteração vê-se logo a seguir.
     *
     * @throws IllegalArgumentException se a chave não existir (as chaves só entram por migração:
     *                                  é um erro de programação)
     */
    public String platformSetting(String key) {
        return settings.value(key)
                .orElseThrow(() -> new IllegalArgumentException("Unknown platform setting: " + key));
    }

    /**
     * Versões em vigor ({@code PUBLISHED}) de produtos não retirados, de seguradoras activas, com os
     * mesmos filtros do catálogo público ({@code null} = todos), por nome da seguradora e do produto.
     */
    public List<ProductVersionView> findPublishedVersions(VehicleCategory category, VehicleUsage usage,
                                                          ProductType type) {
        return lookups.publishedVersions(category, usage, type).stream().map(CatalogEntry::version).toList();
    }

    /**
     * Versão que foi publicada (em vigor, substituída ou retirada), de qualquer seguradora.
     *
     * @throws DomainException {@code PRODUCT_VERSION_NOT_FOUND} (404) se não existir ou nunca tiver
     *                         sido publicada
     */
    public ProductVersionView version(UUID productVersionId) {
        return lookups.publishedVersion(productVersionId)
                .map(CatalogEntry::version)
                .orElseThrow(() -> DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_FOUND,
                        "Product version not found"));
    }
}
