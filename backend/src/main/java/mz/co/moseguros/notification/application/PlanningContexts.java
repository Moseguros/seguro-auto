package mz.co.moseguros.notification.application;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.domain.Device;
import mz.co.moseguros.notification.domain.DeviceRepository;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationPreference;
import mz.co.moseguros.notification.domain.NotificationPreferenceRepository;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.notification.domain.NotificationTemplateRepository;
import mz.co.moseguros.notification.infrastructure.NotificationProperties;
import org.springframework.stereotype.Component;

/**
 * Lê da base de dados o que o {@link ChannelPlanner} precisa (modelos activos, dispositivos activos,
 * preferências). Quem chama corre em {@code SYSTEM}.
 */
@Component
public class PlanningContexts {

    private final NotificationTemplateRepository templates;
    private final DeviceRepository devices;
    private final NotificationPreferenceRepository preferences;
    private final ChannelPlanner planner;

    public PlanningContexts(NotificationTemplateRepository templates, DeviceRepository devices,
                            NotificationPreferenceRepository preferences, NotificationProperties properties) {
        this.templates = templates;
        this.devices = devices;
        this.preferences = preferences;
        this.planner = new ChannelPlanner(properties.channelOrder());
    }

    public ChannelPlanner planner() {
        return planner;
    }

    public ChannelPlanner.Context build(NotificationCategory category, NotificationRecipient recipient,
                                        String templateCode, String locale) {
        return build(category, recipient, templates.findByCode(templateCode), locale);
    }

    public ChannelPlanner.Context build(NotificationCategory category, NotificationRecipient recipient,
                                        List<NotificationTemplate> codeTemplates, String locale) {
        Set<MessageChannel> channels = EnumSet.noneOf(MessageChannel.class);
        for (NotificationTemplate t : codeTemplates) {
            if (t.isActive() && t.getLocale().equals(locale)
                    && (t.getChannel() != MessageChannel.WHATSAPP
                    || (t.getWhatsappTemplateName() != null && !t.getWhatsappTemplateName().isBlank()))) {
                channels.add(t.getChannel());
            }
        }
        List<UUID> deviceIds = devices.findByUserIdAndActiveTrue(recipient.userId()).stream()
                .map(Device::getId).toList();
        return new ChannelPlanner.Context(category, recipient, channels, deviceIds,
                preferencesOf(recipient.userId(), category.name()));
    }

    public Map<MessageChannel, Boolean> preferencesOf(UUID userId, String category) {
        Map<MessageChannel, Boolean> out = new EnumMap<>(MessageChannel.class);
        for (NotificationPreference p : preferences.findByUserId(userId)) {
            if (p.getCategory().equals(category)) {
                out.put(p.getChannel(), p.isEnabled());
            }
        }
        return out;
    }
}
