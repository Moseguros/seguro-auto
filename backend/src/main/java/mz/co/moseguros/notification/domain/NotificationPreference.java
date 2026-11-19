package mz.co.moseguros.notification.domain;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Preferência do cliente por categoria e canal ({@code notification.preference}, S6-04 §2). Sem linha, o
 * canal está activo.
 */
@Entity
@Table(schema = "notification", name = "preference")
public class NotificationPreference extends BaseEntity {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "category", nullable = false, updatable = false, length = 20)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private MessageChannel channel;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    protected NotificationPreference() {
    }

    public NotificationPreference(UUID userId, String category, MessageChannel channel, boolean enabled) {
        if (channel == MessageChannel.INBOX) {
            throw new IllegalArgumentException("The inbox has no preference");
        }
        this.userId = Objects.requireNonNull(userId, "userId");
        this.category = Objects.requireNonNull(category, "category");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
