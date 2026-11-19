package mz.co.moseguros.notification.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.MustacheParseException;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.springframework.stereotype.Component;

/**
 * Renderização e validação dos modelos (S6-04 §3): jmustache com {@code escapeHTML(false)} (os e-mails são
 * texto simples) e {@code strictSections(true)}; uma variável desconhecida é um erro.
 */
@Component
public class TemplateRenderer {

    private static final Mustache.Compiler COMPILER = Mustache.compiler().escapeHTML(false).strictSections(true);

    private final boolean asciiFold;

    @org.springframework.beans.factory.annotation.Autowired
    public TemplateRenderer(mz.co.moseguros.notification.infrastructure.NotificationProperties properties) {
        this.asciiFold = properties.sms().asciiFold();
    }

    /** Para os testes unitários. */
    public TemplateRenderer(boolean asciiFold) {
        this.asciiFold = asciiFold;
    }

    public boolean asciiFold() {
        return asciiFold;
    }

    /**
     * @throws TemplateException {@code TEMPLATE_SYNTAX} ou {@code TEMPLATE_UNKNOWN_VARIABLE}
     */
    public String render(String template, Map<String, String> values) {
        if (template == null) {
            return null;
        }
        com.samskivert.mustache.Template compiled;
        try {
            compiled = COMPILER.compile(template);
        } catch (MustacheParseException e) {
            throw new TemplateException(FieldErrorCodes.TEMPLATE_SYNTAX, "Invalid template syntax");
        } catch (MustacheException e) {
            throw new TemplateException(FieldErrorCodes.TEMPLATE_SYNTAX, "Invalid template syntax");
        }
        try {
            return compiled.execute(values);
        } catch (MustacheException.Context e) {
            throw new TemplateException(FieldErrorCodes.TEMPLATE_UNKNOWN_VARIABLE, "Unknown variable: " + e.key);
        } catch (MustacheException e) {
            throw new TemplateException(FieldErrorCodes.TEMPLATE_SYNTAX, "Invalid template");
        }
    }

    /**
     * Regras da secção 3 para um texto (ao gravar no admin, ao pré-visualizar e no {@code TemplateSeedIT}).
     * Vazia = válido.
     */
    public List<FieldError> validate(MessageChannel channel, String subject, String body, String whatsappTemplateName,
                                     Collection<String> variables, Map<String, String> sampleParams) {
        List<FieldError> errors = new ArrayList<>();
        Map<String, String> allowed = new LinkedHashMap<>();
        for (String variable : variables) {
            allowed.put(variable, sampleParams.getOrDefault(variable, variable));
        }
        boolean needsSubject = channel == MessageChannel.EMAIL || channel == MessageChannel.PUSH;
        if (needsSubject) {
            if (subject == null || subject.isBlank()) {
                errors.add(new FieldError("subject", FieldErrorCodes.REQUIRED, "The subject is required"));
            } else {
                int max = channel == MessageChannel.PUSH ? NotificationTemplate.MAX_PUSH_TITLE
                        : NotificationTemplate.MAX_SUBJECT;
                String rendered = check("subject", subject, allowed, errors);
                if (subject.length() > NotificationTemplate.MAX_SUBJECT || (rendered != null && rendered.length() > max)) {
                    errors.add(new FieldError("subject", FieldErrorCodes.TOO_LONG,
                            "The subject must have at most " + max + " characters"));
                }
            }
        }
        if (body == null || body.isBlank()) {
            errors.add(new FieldError("body", FieldErrorCodes.REQUIRED, "The body is required"));
        } else if (body.length() > NotificationTemplate.MAX_BODY) {
            errors.add(new FieldError("body", FieldErrorCodes.TOO_LONG,
                    "The body must have at most " + NotificationTemplate.MAX_BODY + " characters"));
        } else {
            String rendered = check("body", body, allowed, errors);
            if (rendered != null && channel == MessageChannel.PUSH
                    && rendered.length() > NotificationTemplate.MAX_PUSH_BODY) {
                errors.add(new FieldError("body", FieldErrorCodes.TOO_LONG,
                        "The push text must have at most " + NotificationTemplate.MAX_PUSH_BODY + " characters"));
            }
            if (rendered != null && channel == MessageChannel.SMS && !SmsText.measure(rendered, asciiFold).fits()) {
                errors.add(new FieldError("body", FieldErrorCodes.SMS_TOO_LONG, "The SMS is too long"));
            }
        }
        if (channel == MessageChannel.WHATSAPP && (whatsappTemplateName == null || whatsappTemplateName.isBlank())) {
            errors.add(new FieldError("whatsappTemplateName", FieldErrorCodes.REQUIRED,
                    "The WhatsApp template name is required"));
        }
        return errors;
    }

    private String check(String field, String template, Map<String, String> allowed, List<FieldError> errors) {
        try {
            return render(template, allowed);
        } catch (TemplateException e) {
            errors.add(new FieldError(field, e.code(), e.getMessage()));
            return null;
        }
    }

    /** Erro de um modelo: {@code TEMPLATE_SYNTAX} ou {@code TEMPLATE_UNKNOWN_VARIABLE}. */
    public static final class TemplateException extends IllegalArgumentException {

        private final String code;

        TemplateException(String code, String message) {
            super(message);
            this.code = code;
        }

        public String code() {
            return code;
        }
    }
}
