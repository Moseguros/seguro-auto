package mz.co.moseguros.notification.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.notification.NotificationCategory;

/** Só o que muda (S6-04 §8). */
public record UpdateNotificationPreferencesRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotNull @Size(max = 20) @Valid
        List<CategoryChange> categories) {

    @Schema(name = "NotificationCategoryPreferencesChange")
    public record CategoryChange(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotNull NotificationCategory category,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotNull @Size(max = 4) @Valid
            List<ChannelChange> channels) {
    }

    @Schema(name = "NotificationChannelPreferenceChange")
    public record ChannelChange(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotNull NotificationChannel channel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotNull Boolean enabled) {
    }
}
