package mz.co.moseguros.notification.api;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.notification.application.PreferenceService;
import mz.co.moseguros.notification.domain.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Preferências de notificação do cliente (S6-04 §8; {@code self:profile}). */
@RestController
@RequestMapping("/api/v1/me/notification-preferences")
@Tag(name = NotificationTag.NAME, description = NotificationTag.DESCRIPTION)
public class MyPreferenceController {

    private final PreferenceService preferences;

    public MyPreferenceController(PreferenceService preferences) {
        this.preferences = preferences;
    }

    @GetMapping
    @Operation(operationId = "getMyNotificationPreferences", summary = "Consulta as preferências de notificação",
            description = "Permissão self:profile. Todas as categorias, pela ordem do enum; canais pela ordem de "
                    + "envio (PUSH, WHATSAPP, SMS, EMAIL). available usa só os contactos verificados e os "
                    + "dispositivos activos.")
    public NotificationPreferencesResponse getMyNotificationPreferences() {
        return NotificationPreferencesResponse.from(preferences.mine());
    }

    @PutMapping
    @Operation(operationId = "updateMyNotificationPreferences", summary = "Altera as preferências de notificação",
            description = "Permissão self:profile. Só o que muda. Numa categoria obrigatória (PAYMENT, POLICY, "
                    + "LEGAL) não se desligam SMS, WhatsApp e e-mail ao mesmo tempo → 400 VALIDATION_ERROR com "
                    + "errors[0] = { field: categories[i].channels, code: MANDATORY_CATEGORY_NEEDS_CHANNEL } (o "
                    + "push não conta).")
    public NotificationPreferencesResponse updateMyNotificationPreferences(
            @Valid @RequestBody UpdateNotificationPreferencesRequest request) {
        List<PreferenceService.CategoryChange> changes = request.categories().stream().map(c -> {
            Map<MessageChannel, Boolean> channels = new EnumMap<>(MessageChannel.class);
            c.channels().forEach(ch -> channels.put(ch.channel().toDomain(), ch.enabled()));
            return new PreferenceService.CategoryChange(c.category(), channels);
        }).toList();
        return NotificationPreferencesResponse.from(preferences.update(changes));
    }
}
