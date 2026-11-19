package mz.co.moseguros.notification.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.application.PreferenceService.CategoryPreferences;

/** Preferências do cliente (S6-04 §8): todas as categorias, com os canais pela ordem de envio. */
public record NotificationPreferencesResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<CategoryResponse> categories) {

    static NotificationPreferencesResponse from(List<CategoryPreferences> categories) {
        return new NotificationPreferencesResponse(categories.stream()
                .map(c -> new CategoryResponse(c.category(), c.mandatory(), c.channels().stream()
                        .map(ch -> new ChannelResponse(NotificationChannel.from(ch.channel()), ch.enabled(),
                                ch.available()))
                        .toList()))
                .toList());
    }

    /**
     * Uma categoria.
     *
     * @param mandatory avisos que chegam sempre por pelo menos SMS, WhatsApp ou e-mail
     */
    @Schema(name = "NotificationCategoryPreferences")
    public record CategoryResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) NotificationCategory category,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean mandatory,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<ChannelResponse> channels) {
    }

    /**
     * Um canal.
     *
     * @param available o cliente tem o contacto verificado ou um dispositivo activo
     */
    @Schema(name = "NotificationChannelPreference")
    public record ChannelResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) NotificationChannel channel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean enabled,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean available) {
    }
}
