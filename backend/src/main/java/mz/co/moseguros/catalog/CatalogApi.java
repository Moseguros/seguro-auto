package mz.co.moseguros.catalog;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.application.CatalogSystemLookups;
import mz.co.moseguros.catalog.application.CatalogSystemLookups.CatalogEntry;
import mz.co.moseguros.catalog.application.InsurerVersionService;
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
    private final InsurerVersionService insurerVersions;

    public CatalogApi(PlatformSettingService settings, CatalogSystemLookups lookups,
                      InsurerVersionService insurerVersions) {
        this.settings = settings;
        this.lookups = lookups;
        this.insurerVersions = insurerVersions;
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

    /**
     * Versão em qualquer estado ({@code DRAFT} incluído), no âmbito de quem chama: o RLS mostra só
     * as versões da seguradora do {@code TenantContext} (S3-01 §5). Não usa o
     * {@link CatalogSystemLookups}.
     *
     * @throws DomainException {@code PRODUCT_VERSION_NOT_FOUND} (404) se não for visível
     */
    public ProductVersionView insurerVersion(UUID productVersionId) {
        return insurerVersions.view(productVersionId);
    }

    /**
     * Grava {@code rules.rating} de uma versão {@code DRAFT} da seguradora de quem chama
     * ({@code rules = rules || {"rating": …}}) e incrementa a {@code version}. Não valida o
     * conteúdo da tarifa: isso é do módulo {@code rating}.
     *
     * @return a nova {@code version}
     * @throws DomainException {@code PRODUCT_VERSION_NOT_FOUND} (404), {@code PRODUCT_VERSION_NOT_EDITABLE}
     *                         ou {@code CONCURRENT_MODIFICATION} (409)
     */
    public long updateRatingRules(UUID productVersionId, JsonNode rating, long expectedVersion) {
        return insurerVersions.updateRatingRules(productVersionId, rating, expectedVersion);
    }
}
