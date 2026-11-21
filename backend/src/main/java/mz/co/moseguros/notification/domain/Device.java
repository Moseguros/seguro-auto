package mz.co.moseguros.notification.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Dispositivo do cliente para push ({@code notification.device}, S6-04 §2). O token FCM nunca aparece em
 * logs nem em respostas ({@link #toString()} não o inclui).
 */
@Entity
@Table(schema = "notification", name = "device")
public class Device extends BaseEntity {

    public static final int MAX_TOKEN = 512;
    public static final int MAX_APP_VERSION = 30;

    /** Plataforma da app. */
    @io.swagger.v3.oas.annotations.media.Schema(name = "DevicePlatform", enumAsRef = true)
    public enum Platform { ANDROID, IOS }

    /** Porque deixou de estar activo. */
    public enum DeactivationReason { UNREGISTERED, INVALID_TOKEN, REASSIGNED }

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 10)
    private Platform platform;

    @Column(name = "token", nullable = false, updatable = false, length = MAX_TOKEN)
    private String token;

    @Column(name = "app_version", length = MAX_APP_VERSION)
    private String appVersion;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivated_reason", length = 20)
    private DeactivationReason deactivatedReason;

    protected Device() {
    }

    public Device(UUID userId, Platform platform, String token, String appVersion, Instant now) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.platform = Objects.requireNonNull(platform, "platform");
        this.token = Objects.requireNonNull(token, "token");
        this.appVersion = appVersion;
        this.lastSeenAt = now;
    }

    /** Registado de novo pelo mesmo cliente: actualiza e reactiva. */
    public void seen(Platform platform, String appVersion, Instant now) {
        this.platform = platform;
        this.appVersion = appVersion;
        this.lastSeenAt = now;
        this.active = true;
        this.deactivatedAt = null;
        this.deactivatedReason = null;
    }

    public void deactivate(DeactivationReason reason, Instant now) {
        if (!active) {
            return;
        }
        this.active = false;
        this.deactivatedAt = now;
        this.deactivatedReason = Objects.requireNonNull(reason, "reason");
    }

    public UUID getUserId() {
        return userId;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getToken() {
        return token;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public DeactivationReason getDeactivatedReason() {
        return deactivatedReason;
    }
}
