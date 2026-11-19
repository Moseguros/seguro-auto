package mz.co.moseguros.notification.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.notification.application.DeviceService;
import mz.co.moseguros.notification.domain.Device;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Dispositivos do cliente para push (S6-04 §8; {@code self:profile}). A resposta nunca tem o token. */
@RestController
@RequestMapping("/api/v1/me/devices")
@Tag(name = NotificationTag.NAME, description = NotificationTag.DESCRIPTION)
public class MyDeviceController {

    private final DeviceService devices;

    public MyDeviceController(DeviceService devices) {
        this.devices = devices;
    }

    @PostMapping
    @Operation(operationId = "registerMyDevice", summary = "Regista o dispositivo para push",
            description = "Permissão self:profile. Token já registado para este cliente → actualiza e reactiva; "
                    + "registado para outro cliente → esse fica inactivo e cria-se um novo para este.")
    public DeviceResponse registerMyDevice(@Valid @RequestBody RegisterDeviceRequest request) {
        Device device = devices.register(request.token().strip(), request.platform(), request.appVersion());
        return new DeviceResponse(device.getId(), device.getPlatform(), device.isActive());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "unregisterMyDevice", summary = "Remove o dispositivo",
            description = "Permissão self:profile. Remoção lógica (deixa de receber push). De outro cliente → 404 "
                    + "DEVICE_NOT_FOUND.")
    public void unregisterMyDevice(@PathVariable UUID id) {
        devices.unregister(id);
    }

    /**
     * @param token      token FCM
     * @param appVersion versão da app ({@code 1.0.0+12})
     */
    public record RegisterDeviceRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank @Size(max = Device.MAX_TOKEN) String token,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotNull Device.Platform platform,
            @Schema(example = "1.0.0+12") @Size(max = Device.MAX_APP_VERSION) String appVersion) {
    }

    public record DeviceResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Device.Platform platform,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean active) {
    }
}
