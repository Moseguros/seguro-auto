package mz.co.moseguros.notification.application;

import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.MessageStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Métricas das notificações (S6-04 §4 e §5; Prometheus):
 * {@code moseguros_notification_messages_total{channel, status}},
 * {@code moseguros_notification_fallback_total{from, to}},
 * {@code moseguros_notification_undeliverable_total{category}} e
 * {@code moseguros_notification_sms_truncated_total{templateCode}}.
 */
@Component
public class NotificationMetrics {

    public static final String MESSAGES = "moseguros.notification.messages";
    public static final String FALLBACK = "moseguros.notification.fallback";
    public static final String UNDELIVERABLE = "moseguros.notification.undeliverable";
    public static final String SMS_TRUNCATED = "moseguros.notification.sms.truncated";

    private final MeterRegistry registry;

    public NotificationMetrics(ObjectProvider<MeterRegistry> registry) {
        this.registry = registry.getIfAvailable();
    }

    /** Estado final de uma linha ({@code SENT}, {@code DELIVERED}, {@code FAILED}, {@code SKIPPED}). */
    public void message(MessageChannel channel, MessageStatus status) {
        if (registry != null) {
            registry.counter(MESSAGES, "channel", channel.name(), "status", status.name()).increment();
        }
    }

    public void fallback(MessageChannel from, MessageChannel to) {
        if (registry != null) {
            registry.counter(FALLBACK, "from", from.name(), "to", to.name()).increment();
        }
    }

    public void undeliverable(String category) {
        if (registry != null) {
            registry.counter(UNDELIVERABLE, "category", category).increment();
        }
    }

    public void smsTruncated(String templateCode) {
        if (registry != null) {
            registry.counter(SMS_TRUNCATED, "templateCode", templateCode).increment();
        }
    }
}
