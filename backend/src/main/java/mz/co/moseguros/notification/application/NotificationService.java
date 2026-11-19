package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.NotificationRecipientResolver;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationMessage;
import mz.co.moseguros.notification.domain.NotificationMessageRepository;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.notification.domain.NotificationTemplateRepository;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@code NotificationApi.notify} (S6-04 §4): grava a linha {@code INBOX} e as linhas {@code QUEUED} dos canais
 * escolhidos pelo {@link ChannelPlanner}, na transacção de quem chama, e entrega-as ao executor depois do
 * commit. Um rollback de quem chama desfaz tudo e não envia nada.
 */
@Service
public class NotificationService {

    static final String DEFAULT_WEB_PATH = "/cliente/notificacoes";
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationTemplateRepository templates;
    private final NotificationMessageRepository messages;
    private final NotificationRecipientResolver recipients;
    private final PlanningContexts contexts;
    private final TemplateRenderer renderer;
    private final MessageDispatcher dispatcher;
    private final NotificationMetrics metrics;
    private final Clock clock;
    private final String webBaseUrl;

    public NotificationService(NotificationTemplateRepository templates, NotificationMessageRepository messages,
                               NotificationRecipientResolver recipients, PlanningContexts contexts,
                               TemplateRenderer renderer, MessageDispatcher dispatcher, NotificationMetrics metrics,
                               Clock clock, @Value("${moseguros.web.base-url:http://localhost:5173}") String webBaseUrl) {
        this.templates = templates;
        this.messages = messages;
        this.recipients = recipients;
        this.contexts = contexts;
        this.renderer = renderer;
        this.dispatcher = dispatcher;
        this.metrics = metrics;
        this.clock = clock;
        this.webBaseUrl = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
    }

    /** Ver {@code NotificationApi.notify}. */
    @RunAsSystem
    @Transactional(propagation = Propagation.REQUIRED)
    public UUID notify(UUID userId, String templateCode, Map<String, Object> params, NotificationCategory category) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(templateCode, "templateCode");
        Objects.requireNonNull(category, "category");
        Map<String, Object> input = params == null ? Map.of() : params;
        List<NotificationTemplate> codeTemplates = templates.findByCode(templateCode);
        if (codeTemplates.isEmpty()) {
            throw new IllegalArgumentException("Unknown notification template: " + templateCode);
        }
        List<String> missing = codeTemplates.get(0).getVariables().stream()
                .filter(v -> !ParamFormatter.AUTOMATIC.contains(v) && input.get(v) == null)
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing parameters for " + templateCode + ": " + missing);
        }
        UUID notificationId = Ids.newId();
        Optional<NotificationRecipient> found = recipients.resolve(userId);
        if (found.isEmpty() || !found.get().active()) {
            log.info("Notification {} ({}) for user {} not recorded: no active recipient", notificationId,
                    templateCode, userId);
            return notificationId;
        }
        NotificationRecipient recipient = found.get();
        String locale = "en".equals(recipient.language()) ? "en" : "pt";

        Map<String, String> values = new LinkedHashMap<>(ParamFormatter.format(input, locale));
        Object webPath = input.get(ParamFormatter.WEB_PATH);
        Object deepLink = input.get(ParamFormatter.DEEP_LINK);
        values.put(ParamFormatter.LINK, webBaseUrl + (webPath == null ? DEFAULT_WEB_PATH : webPath.toString()));
        values.put(ParamFormatter.SITE_URL, webBaseUrl);
        Map<String, String> data = new LinkedHashMap<>();
        if (deepLink != null) {
            data.put(NotificationMessage.DATA_DEEP_LINK, deepLink.toString());
        }
        if (webPath != null) {
            data.put(NotificationMessage.DATA_WEB_PATH, webPath.toString());
        }

        NotificationTemplate push = pushTemplate(codeTemplates, locale, templateCode);
        Instant now = clock.instant();
        messages.save(NotificationMessage.inbox(notificationId, userId, templateCode, category.name(), locale, values,
                renderer.render(push.getSubject(), values), renderer.render(push.getBody(), values), data, now));

        ChannelPlanner.Context context = contexts.build(category, recipient, codeTemplates, locale);
        ChannelPlanner.Plan plan = contexts.planner().plan(context);
        if (plan.undeliverable()) {
            metrics.undeliverable(category.name());
            log.warn("Notification {} ({}, {}) for user {}: no external channel available", notificationId,
                    templateCode, category, userId);
        }
        List<UUID> queued = new ArrayList<>();
        for (ChannelPlanner.Planned row : plan.rows()) {
            queued.add(messages.save(NotificationMessage.queued(notificationId, userId, templateCode, category.name(),
                    row.channel(), locale, values, data, row.deviceId(), now)).getId());
        }
        log.info("Notification {} ({}, {}) for user {}: inbox + {}", notificationId, templateCode, category, userId,
                plan.rows().stream().map(r -> r.channel().name()).toList());
        dispatcher.afterCommit(queued);
        return notificationId;
    }

    private static NotificationTemplate pushTemplate(List<NotificationTemplate> codeTemplates, String locale,
                                                     String code) {
        return codeTemplates.stream()
                .filter(t -> t.getChannel() == MessageChannel.PUSH && t.getLocale().equals(locale)).findFirst()
                .or(() -> codeTemplates.stream()
                        .filter(t -> t.getChannel() == MessageChannel.PUSH && t.getLocale().equals("pt")).findFirst())
                .orElseThrow(() -> new IllegalArgumentException("No push template (inbox text) for " + code));
    }
}
