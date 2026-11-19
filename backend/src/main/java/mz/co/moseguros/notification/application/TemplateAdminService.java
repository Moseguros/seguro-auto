package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.notification.domain.Destinations;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.notification.domain.NotificationTemplate.TemplateText;
import mz.co.moseguros.notification.domain.NotificationTemplateRepository;
import mz.co.moseguros.notification.domain.OutboundMessage;
import mz.co.moseguros.notification.domain.OutboundMessageRepository;
import mz.co.moseguros.notification.infrastructure.ChannelSender;
import mz.co.moseguros.notification.infrastructure.OutboundContent;
import mz.co.moseguros.notification.infrastructure.ProviderErrors;
import mz.co.moseguros.notification.infrastructure.ProviderResult;
import mz.co.moseguros.notification.infrastructure.PushSender;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Administração dos modelos (S6-04 §8; {@code notification-template:manage}, âmbito {@code PLATFORM}): listar,
 * consultar (com o texto por defeito do ficheiro), editar (validação da secção 3, {@code version}), repor,
 * pré-visualizar e testar. Editar e repor são auditados ({@code NOTIFICATION_TEMPLATE.UPDATE}/{@code RESET}).
 */
@Service
public class TemplateAdminService {

    public static final String PERMISSION = "hasAuthority('notification-template:manage')";
    private static final Logger log = LoggerFactory.getLogger(TemplateAdminService.class);

    private final NotificationTemplateRepository templates;
    private final OutboundMessageRepository outbound;
    private final TemplateRenderer renderer;
    private final List<ChannelSender> senders;
    private final ObjectProvider<PushSender> push;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final TransactionTemplate tx;
    private final Clock clock;
    private volatile TemplateCatalog catalog;

    public TemplateAdminService(NotificationTemplateRepository templates, OutboundMessageRepository outbound,
                                TemplateRenderer renderer, List<ChannelSender> senders, ObjectProvider<PushSender> push,
                                AuditService audit, CurrentUser currentUser, TransactionTemplate tx, Clock clock) {
        this.templates = templates;
        this.outbound = outbound;
        this.renderer = renderer;
        this.senders = senders;
        this.push = push;
        this.audit = audit;
        this.currentUser = currentUser;
        this.tx = tx;
        this.clock = clock;
    }

    @PreAuthorize(PERMISSION)
    @Transactional(readOnly = true)
    public Page<NotificationTemplate> list(String codePrefix, MessageChannel channel, String locale,
                                           Boolean customized, Pageable pageable) {
        return templates.search(codePrefix == null || codePrefix.isBlank() ? null : codePrefix.strip(), channel,
                locale == null || locale.isBlank() ? null : locale, customized, pageable);
    }

    @PreAuthorize(PERMISSION)
    @Transactional(readOnly = true)
    public Details get(UUID id) {
        return details(find(id));
    }

    /** Grava o texto do administrador ({@code customized = true}); {@code version} diferente → 409. */
    @PreAuthorize(PERMISSION)
    @Transactional
    public Details update(UUID id, TemplateText text, boolean active, Long version) {
        NotificationTemplate t = find(id);
        if (version == null) {
            throw new FieldValidationException("version", FieldErrorCodes.REQUIRED, "The version is required");
        }
        if (t.getVersion() != version) {
            throw new DomainException(ErrorCodes.CONCURRENT_MODIFICATION, 409,
                    "The template was changed by another request");
        }
        TemplateText normalized = normalize(t.getChannel(), text);
        requireValid(t, normalized.subject(), normalized.body(), normalized.whatsappTemplateName());
        Snapshot before = Snapshot.of(t);
        t.customize(normalized, active);
        templates.flush();
        record("NOTIFICATION_TEMPLATE.UPDATE", t, before);
        return details(t);
    }

    /** Repõe o texto do ficheiro ({@code customized = false}). */
    @PreAuthorize(PERMISSION)
    @Transactional
    public Details reset(UUID id) {
        NotificationTemplate t = find(id);
        TemplateText defaults = defaults(t);
        if (defaults == null) {
            throw new DomainException(ErrorCodes.TEMPLATE_NOT_FOUND, 404, "The template is no longer in the file");
        }
        Snapshot before = Snapshot.of(t);
        t.resetTo(defaults);
        templates.flush();
        record("NOTIFICATION_TEMPLATE.RESET", t, before);
        return details(t);
    }

    /** Renderiza sem gravar (sem {@code params}, os {@code sample_params}). */
    @PreAuthorize(PERMISSION)
    @Transactional(readOnly = true)
    public Preview preview(UUID id, String subject, String body, Map<String, String> params) {
        NotificationTemplate t = find(id);
        String effectiveSubject = t.getChannel() == MessageChannel.EMAIL || t.getChannel() == MessageChannel.PUSH
                ? subject : null;
        requireValid(t, effectiveSubject, body, t.getChannel() == MessageChannel.WHATSAPP
                ? nonBlankOr(t.getWhatsappTemplateName(), "x") : null);
        Map<String, String> values = new LinkedHashMap<>(t.getSampleParams());
        if (params != null) {
            values.putAll(params);
        }
        String renderedSubject;
        String renderedBody;
        try {
            renderedSubject = renderer.render(effectiveSubject, values);
            renderedBody = renderer.render(body, values);
        } catch (TemplateRenderer.TemplateException e) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", List.of(new FieldError("params", e.code(), e.getMessage())));
        }
        if (t.getChannel() == MessageChannel.SMS) {
            SmsText.Measured sms = SmsText.measure(renderedBody, renderer.asciiFold());
            return new Preview(null, renderedBody, sms.text(), sms.length(), sms.limit(), sms.fits());
        }
        return new Preview(renderedSubject, renderedBody, null, null, null, null);
    }

    /**
     * Envia já, com os {@code sample_params} e o texto gravado, sem alternativa. SMS, WhatsApp e e-mail ficam em
     * {@code outbound_message} ({@code NOTIFICATION_TEST}); o push só em log (sem token).
     */
    @PreAuthorize(PERMISSION)
    @RunAsSystem
    public TestResult test(UUID id, String rawDestination) {
        NotificationTemplate t = tx.execute(status -> find(id));
        String destination = destination(t.getChannel(), rawDestination);
        Map<String, String> values = t.getSampleParams();
        String subject = renderer.render(t.getSubject(), values);
        String text = renderer.render(t.getBody(), values);
        if (t.getChannel() == MessageChannel.PUSH) {
            PushSender sender = push.getIfAvailable();
            ProviderResult result;
            if (sender == null) {
                result = ProviderResult.temporary("none", "push not configured");
            } else {
                try {
                    result = sender.send(destination, subject, text, Map.of("notificationId", "test-" + t.getId()));
                } catch (RuntimeException e) {
                    result = ProviderErrors.classify(sender.provider(), e);
                }
            }
            log.info("Test push of template {}/{}/{} via {}: {}", t.getCode(), t.getChannel(), t.getLocale(),
                    result.provider(), result.success() ? "SENT" : "FAILED (" + result.reason() + ")");
            return TestResult.of(result);
        }
        if (t.getChannel() == MessageChannel.SMS) {
            text = SmsText.fit(text, renderer.asciiFold()).text();
        }
        Channel channel = t.getChannel().toDeliveryChannel();
        ChannelSender sender = senders.stream().filter(s -> s.channel() == channel).findFirst().orElse(null);
        String provider = sender == null ? "none" : sender.provider();
        UUID messageId = tx.execute(status -> outbound.save(OutboundMessage.pending(channel,
                OutboundMessage.Purpose.NOTIFICATION_TEST, Destinations.mask(destination),
                Destinations.hash(destination), t.getId(), provider)).getId());
        List<String> whatsappParams = new ArrayList<>();
        for (String name : t.getWhatsappParams()) {
            whatsappParams.add(values.getOrDefault(name, ""));
        }
        ProviderResult result;
        if (sender == null) {
            result = ProviderResult.temporary("none", "channel not configured");
        } else {
            try {
                result = sender.send(new OutboundContent(destination, subject, text, t.getWhatsappTemplateName(),
                        whatsappParams));
            } catch (RuntimeException e) {
                result = ProviderErrors.classify(sender.provider(), e);
            }
        }
        ProviderResult finalResult = result;
        tx.executeWithoutResult(status -> {
            OutboundMessage message = outbound.findById(messageId).orElseThrow();
            if (finalResult.success()) {
                message.markSent(finalResult.providerMessageId(), clock.instant());
            } else {
                message.markFailed(finalResult.reason());
            }
        });
        log.info("Test message {} of template {}/{}/{} to {}: {}", messageId, t.getCode(), t.getChannel(),
                t.getLocale(), Destinations.mask(destination), result.success() ? "SENT" : "FAILED");
        return TestResult.of(result);
    }

    private static String destination(MessageChannel channel, String raw) {
        if (raw == null || raw.isBlank()) {
            throw new FieldValidationException("destination", FieldErrorCodes.REQUIRED, "The destination is required");
        }
        try {
            return switch (channel) {
                case SMS, WHATSAPP -> Msisdn.of(raw).e164();
                case EMAIL -> Emails.normalize(raw);
                default -> {
                    if (raw.length() > mz.co.moseguros.notification.domain.Device.MAX_TOKEN) {
                        throw new FieldValidationException("destination", FieldErrorCodes.TOO_LONG,
                                "The token is too long");
                    }
                    yield raw.strip();
                }
            };
        } catch (FieldValidationException e) {
            throw e;
        } catch (DomainException e) {
            throw FieldValidationException.of("destination", e);
        }
    }

    private NotificationTemplate find(UUID id) {
        return templates.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCodes.TEMPLATE_NOT_FOUND, 404, "Template not found"));
    }

    private void requireValid(NotificationTemplate t, String subject, String body, String whatsappTemplateName) {
        List<FieldError> errors = renderer.validate(t.getChannel(), subject, body, whatsappTemplateName,
                t.getVariables(), t.getSampleParams());
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
    }

    /** O assunto só existe em e-mail e push; o WhatsApp só nos modelos WhatsApp. */
    private static TemplateText normalize(MessageChannel channel, TemplateText text) {
        boolean hasSubject = channel == MessageChannel.EMAIL || channel == MessageChannel.PUSH;
        boolean whatsapp = channel == MessageChannel.WHATSAPP;
        return new TemplateText(hasSubject ? text.subject() : null, text.body(),
                whatsapp ? blankToNull(text.whatsappTemplateName()) : null,
                whatsapp ? (text.whatsappParams() == null ? List.of() : text.whatsappParams()) : null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private static String nonBlankOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private TemplateText defaults(NotificationTemplate t) {
        TemplateCatalog current = catalog;
        if (current == null) {
            current = TemplateCatalog.load();
            catalog = current;
        }
        return current.text(t.getCode(), t.getChannel(), t.getLocale()).orElse(null);
    }

    private Details details(NotificationTemplate t) {
        TemplateText defaults = defaults(t);
        return new Details(t, defaults == null ? null : defaults.subject(), defaults == null ? null : defaults.body());
    }

    private void record(String action, NotificationTemplate t, Snapshot before) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), null, action, "NotificationTemplate",
                t.getId().toString(), before, Snapshot.of(t)));
    }

    /** Modelo e texto por defeito do ficheiro. */
    public record Details(NotificationTemplate template, String defaultSubject, String defaultBody) {
    }

    /** Pré-visualização ({@code smsText}, {@code length}, {@code limit}, {@code fits} só em SMS). */
    public record Preview(String subject, String body, String smsText, Integer length, Integer limit, Boolean fits) {
    }

    /** Resultado do envio de teste. */
    public record TestResult(String status, String reason) {
        static TestResult of(ProviderResult result) {
            return new TestResult(result.success() ? "SENT" : "FAILED", result.success() ? null : result.reason());
        }
    }

    /** Estado auditado. */
    public record Snapshot(String subject, String body, String whatsappTemplateName, boolean active) {
        static Snapshot of(NotificationTemplate t) {
            return new Snapshot(t.getSubject(), t.getBody(), t.getWhatsappTemplateName(), t.isActive());
        }
    }
}
