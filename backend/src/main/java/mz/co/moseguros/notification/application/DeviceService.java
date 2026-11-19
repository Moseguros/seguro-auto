package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.notification.domain.Device;
import mz.co.moseguros.notification.domain.DeviceRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dispositivos do cliente para push (S6-04 §8; {@code self:profile}). O registo corre em {@code SYSTEM}
 * (filtrado pelo token e pelo id do cliente actual), porque o mesmo token pode estar noutro cliente: esse fica
 * inactivo ({@code REASSIGNED}). O token nunca vai para logs nem para respostas.
 */
@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository devices;
    private final CurrentUser currentUser;
    private final Clock clock;

    public DeviceService(DeviceRepository devices, CurrentUser currentUser, Clock clock) {
        this.devices = devices;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    @PreAuthorize("hasAuthority('self:profile')")
    @RunAsSystem
    @Transactional
    public Device register(String token, Device.Platform platform, String appVersion) {
        UUID userId = currentUser.id();
        Instant now = clock.instant();
        List<Device> sameToken = devices.findByToken(token);
        for (Device other : sameToken) {
            if (other.isActive() && !other.getUserId().equals(userId)) {
                other.deactivate(Device.DeactivationReason.REASSIGNED, now);
                log.info("Push device {} reassigned from user {} to user {}", other.getId(), other.getUserId(), userId);
            }
        }
        devices.flush();
        Device mine = sameToken.stream().filter(d -> d.getUserId().equals(userId))
                .max(Comparator.comparing(Device::getLastSeenAt)).orElse(null);
        if (mine != null) {
            mine.seen(platform, appVersion, now);
            return mine;
        }
        Device created = devices.save(new Device(userId, platform, token, appVersion, now));
        log.info("Push device {} ({}) registered for user {}", created.getId(), platform, userId);
        return created;
    }

    /** {@code active = false} ({@code UNREGISTERED}); de outro cliente → 404 {@code DEVICE_NOT_FOUND}. */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public void unregister(UUID id) {
        Device device = devices.findByIdAndUserId(id, currentUser.id())
                .orElseThrow(() -> new DomainException(ErrorCodes.DEVICE_NOT_FOUND, 404, "Device not found"));
        device.deactivate(Device.DeactivationReason.UNREGISTERED, clock.instant());
    }
}
