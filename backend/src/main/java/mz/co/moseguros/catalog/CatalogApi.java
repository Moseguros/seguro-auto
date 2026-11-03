package mz.co.moseguros.catalog;

import mz.co.moseguros.catalog.application.PlatformSettingService;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Catálogo (modelo transversal §3). Nesta fase só expõe os parâmetros
 * da plataforma; o S2-04 acrescenta {@code findPublishedVersions(...)} e {@code version(...)}.
 *
 * <p>Funciona em qualquer âmbito, dentro ou fora de uma transacção (as tabelas de referência não
 * têm RLS).
 */
@Service
public class CatalogApi {

    private final PlatformSettingService settings;

    public CatalogApi(PlatformSettingService settings) {
        this.settings = settings;
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
}
