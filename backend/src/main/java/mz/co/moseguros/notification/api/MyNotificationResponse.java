package mz.co.moseguros.notification.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.notification.domain.NotificationMessage;

/**
 * Uma notificação do centro de notificações (S6-04 §8).
 *
 * @param deepLink URI da app ({@code moseguros://…}); pode faltar
 * @param webPath  caminho na web ({@code /cliente/…}); pode faltar
 */
public record MyNotificationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "POLICY_ISSUED") String templateCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "POLICY") String category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Apólice emitida") String title,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                example = "A apólice AP-2027-000123 (ABC-123-MC) está activa até 10/02/2028 10:31.") String body,
        @Schema(example = "moseguros://policy/0194f7a2-3b1c-7d2e-9f00-1234567890ab") String deepLink,
        @Schema(example = "/cliente/apolices/0194f7a2-3b1c-7d2e-9f00-1234567890ab") String webPath,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
        Instant readAt) {

    static MyNotificationResponse from(NotificationMessage m) {
        return new MyNotificationResponse(m.getId(), m.getTemplateCode(), m.getCategory(), m.getTitle(), m.getBody(),
                m.getData().get(NotificationMessage.DATA_DEEP_LINK), m.getData().get(NotificationMessage.DATA_WEB_PATH),
                m.getCreatedAt(), m.getReadAt());
    }
}
