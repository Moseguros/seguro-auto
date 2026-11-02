package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.OtpSettingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Administração das definições do OTP do cliente (plataforma). As alterações valem logo no
 * pedido seguinte.
 */
@RestController
@RequestMapping("/api/v1/admin/otp-settings")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class OtpSettingsController {

    private final OtpSettingsService service;

    public OtpSettingsController(OtpSettingsService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "getOtpSettings", summary = "Definições do OTP",
            description = "Permissão otp-settings:read.")
    public OtpSettingsResponse get() {
        return OtpSettingsResponse.from(service.get());
    }

    @PutMapping
    @Operation(operationId = "updateOtpSettings", summary = "Altera as definições do OTP",
            description = "Permissão otp-settings:manage. Todos os campos são obrigatórios; version diferente da "
                    + "gravada → 409 CONCURRENT_MODIFICATION.")
    public OtpSettingsResponse update(@Valid @RequestBody UpdateOtpSettingsRequest request) {
        return OtpSettingsResponse.from(service.update(request.toUpdate()));
    }

    @PostMapping("/test")
    @Operation(operationId = "testOtpDelivery", summary = "Envia um código de teste",
            description = "Permissão otp-settings:manage. Só pelo canal pedido, mesmo que esteja desactivado, e sem "
                    + "alternativa; o código não serve para entrar. Conta para o limite por hora.")
    public OtpTestResponse test(@Valid @RequestBody OtpTestRequest request) {
        return OtpTestResponse.from(service.sendTest(request.identifier(), request.identifierType(),
                request.channel()));
    }
}
