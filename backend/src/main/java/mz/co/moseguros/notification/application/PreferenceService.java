package mz.co.moseguros.notification.application;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.NotificationRecipientResolver;
import mz.co.moseguros.notification.domain.DeviceRepository;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationPreference;
import mz.co.moseguros.notification.domain.NotificationPreferenceRepository;
import mz.co.moseguros.notification.infrastructure.NotificationProperties;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Preferências do cliente (S6-04 §8; {@code self:profile}): todas as categorias, pela ordem do enum, com os
 * canais pela {@code channel-order}; {@code available} usa só os contactos e os dispositivos. Numa categoria
 * obrigatória não se desligam ao mesmo tempo SMS, WhatsApp e e-mail ({@code MANDATORY_CATEGORY_NEEDS_CHANNEL};
 * o push não conta).
 */
@Service
public class PreferenceService {

    private final NotificationPreferenceRepository preferences;
    private final DeviceRepository devices;
    private final NotificationRecipientResolver recipients;
    private final CurrentUser currentUser;
    private final List<MessageChannel> order;

    public PreferenceService(NotificationPreferenceRepository preferences, DeviceRepository devices,
                             NotificationRecipientResolver recipients, CurrentUser currentUser,
                             NotificationProperties properties) {
        this.preferences = preferences;
        this.devices = devices;
        this.recipients = recipients;
        this.currentUser = currentUser;
        this.order = properties.channelOrder();
    }

    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional(readOnly = true)
    public List<CategoryPreferences> mine() {
        return view(currentUser.id());
    }

    /**
     * Grava só o que muda.
     *
     * @param changes categoria → canal → activo
     */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public List<CategoryPreferences> update(List<CategoryChange> changes) {
        UUID userId = currentUser.id();
        Map<String, NotificationPreference> stored = new HashMap<>();
        for (NotificationPreference p : preferences.findByUserId(userId)) {
            stored.put(p.getCategory() + "/" + p.getChannel(), p);
        }
        for (int i = 0; i < changes.size(); i++) {
            CategoryChange change = changes.get(i);
            Map<MessageChannel, Boolean> result = new EnumMap<>(MessageChannel.class);
            for (MessageChannel channel : order) {
                NotificationPreference p = stored.get(change.category().name() + "/" + channel);
                result.put(channel, p == null || p.isEnabled());
            }
            result.putAll(change.channels());
            if (change.category().mandatory() && !result.get(MessageChannel.SMS) && !result.get(MessageChannel.WHATSAPP)
                    && !result.get(MessageChannel.EMAIL)) {
                throw new FieldValidationException("categories[" + i + "].channels",
                        FieldErrorCodes.MANDATORY_CATEGORY_NEEDS_CHANNEL,
                        "A mandatory category needs SMS, WhatsApp or e-mail");
            }
            for (Map.Entry<MessageChannel, Boolean> entry : change.channels().entrySet()) {
                String key = change.category().name() + "/" + entry.getKey();
                NotificationPreference p = stored.get(key);
                if (p == null) {
                    p = preferences.save(new NotificationPreference(userId, change.category().name(), entry.getKey(),
                            entry.getValue()));
                    stored.put(key, p);
                } else {
                    p.setEnabled(entry.getValue());
                }
            }
        }
        preferences.flush();
        return view(userId);
    }

    private List<CategoryPreferences> view(UUID userId) {
        NotificationRecipient recipient = recipients.resolve(userId).orElse(null);
        boolean hasDevices = devices.existsByUserIdAndActiveTrue(userId);
        Map<String, Boolean> stored = new HashMap<>();
        for (NotificationPreference p : preferences.findByUserId(userId)) {
            stored.put(p.getCategory() + "/" + p.getChannel(), p.isEnabled());
        }
        List<CategoryPreferences> out = new ArrayList<>();
        for (NotificationCategory category : NotificationCategory.values()) {
            List<ChannelPreference> channels = new ArrayList<>();
            for (MessageChannel channel : order) {
                boolean enabled = stored.getOrDefault(category.name() + "/" + channel, Boolean.TRUE);
                boolean available = recipient != null && ChannelPlanner.contactAvailable(channel, recipient, hasDevices);
                channels.add(new ChannelPreference(channel, enabled, available));
            }
            out.add(new CategoryPreferences(category, category.mandatory(), channels));
        }
        return out;
    }

    /** Pedido de mudança de uma categoria. */
    public record CategoryChange(NotificationCategory category, Map<MessageChannel, Boolean> channels) {
    }

    public record CategoryPreferences(NotificationCategory category, boolean mandatory,
                                      List<ChannelPreference> channels) {
    }

    public record ChannelPreference(MessageChannel channel, boolean enabled, boolean available) {
    }
}
