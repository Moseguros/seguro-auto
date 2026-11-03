package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.catalog.application.PlatformSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Parâmetros da plataforma: lista pública (sem token) e administração ({@code reference:manage}).
 */
@RestController
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class PlatformSettingController {

    private final PlatformSettingService service;

    public PlatformSettingController(PlatformSettingService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/platform-settings")
    @Operation(operationId = "listPublicPlatformSettings", summary = "Lista os parâmetros da plataforma",
            description = "Público: nunca há segredos nesta tabela. MONEY em centavos, como texto.")
    public PublicPlatformSettingsResponse listPublic() {
        return new PublicPlatformSettingsResponse(service.listPublic().stream()
                .map(PublicPlatformSettingResponse::from)
                .toList());
    }

    @GetMapping("/api/v1/admin/platform-settings")
    @Operation(operationId = "listPlatformSettings", summary = "Lista os parâmetros da plataforma (administração)",
            description = "Permissão reference:manage. Inclui versão e autor da última alteração.")
    public PlatformSettingsResponse list() {
        return new PlatformSettingsResponse(service.list().stream().map(PlatformSettingResponse::from).toList());
    }

    @PutMapping("/api/v1/admin/platform-settings/{key}")
    @Operation(operationId = "updatePlatformSetting", summary = "Altera o valor de um parâmetro",
            description = "Permissão reference:manage. Só o valor muda (as chaves entram por migração). Valor "
                    + "inválido para o tipo → VALIDATION_ERROR com INVALID_VALUE em value; chave desconhecida → 404 "
                    + "PLATFORM_SETTING_NOT_FOUND; version diferente → 409 CONCURRENT_MODIFICATION.")
    public PlatformSettingResponse update(@PathVariable String key,
                                          @Valid @RequestBody UpdatePlatformSettingRequest request) {
        return PlatformSettingResponse.from(service.update(key, request.value(), request.version()));
    }
}
