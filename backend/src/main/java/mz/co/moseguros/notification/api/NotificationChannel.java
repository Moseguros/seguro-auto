package mz.co.moseguros.notification.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.notification.domain.MessageChannel;

/** Canal externo de uma notificação, no contrato (S6-04 §8). */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum NotificationChannel {
    PUSH, WHATSAPP, SMS, EMAIL;

    static NotificationChannel from(MessageChannel channel) {
        return NotificationChannel.valueOf(channel.name());
    }

    MessageChannel toDomain() {
        return MessageChannel.valueOf(name());
    }
}
