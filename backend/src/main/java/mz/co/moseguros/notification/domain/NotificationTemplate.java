package mz.co.moseguros.notification.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Modelo de uma mensagem por código, canal e língua ({@code notification.template}, S6-04 §2 e §3). O texto
 * por defeito vem de {@code messages/notifications_{pt,en}.properties} (o {@code TemplateSeeder} carrega-o
 * no arranque); o administrador pode sobrepô-lo ({@code customized = true}). Só visível em
 * {@code SYSTEM}/{@code PLATFORM}.
 */
@Entity
@Table(schema = "notification", name = "template")
public class NotificationTemplate extends BaseEntity {

    public static final int MAX_BODY = 4000;
    public static final int MAX_SUBJECT = 200;
    public static final int MAX_PUSH_TITLE = 65;
    public static final int MAX_PUSH_BODY = 240;

    @Column(name = "code", nullable = false, updatable = false, length = 60)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private MessageChannel channel;

    @Column(name = "locale", nullable = false, updatable = false, length = 5)
    private String locale;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "whatsapp_template_name", length = 100)
    private String whatsappTemplateName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "whatsapp_params", columnDefinition = "text[]")
    private List<String> whatsappParams;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "variables", nullable = false, columnDefinition = "text[]")
    private List<String> variables;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sample_params", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> sampleParams;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "customized", nullable = false)
    private boolean customized;

    @Column(name = "default_hash", nullable = false, length = 64)
    private String defaultHash;

    protected NotificationTemplate() {
    }

    /** Modelo novo, com o texto do ficheiro. */
    public static NotificationTemplate fromDefaults(String code, MessageChannel channel, String locale,
                                                    TemplateText text, List<String> variables,
                                                    Map<String, String> sampleParams, String defaultHash) {
        NotificationTemplate t = new NotificationTemplate();
        t.code = Objects.requireNonNull(code, "code");
        if (!channel.external()) {
            throw new IllegalArgumentException("A template needs an external channel: " + channel);
        }
        t.channel = channel;
        t.locale = Objects.requireNonNull(locale, "locale");
        t.apply(text);
        t.variables = List.copyOf(variables);
        t.sampleParams = Map.copyOf(sampleParams);
        t.defaultHash = Objects.requireNonNull(defaultHash, "defaultHash");
        return t;
    }

    /** O ficheiro mudou e o administrador não personalizou: o texto novo substitui o actual. */
    public void refreshDefaults(TemplateText text, List<String> variables, Map<String, String> sampleParams,
                                String defaultHash) {
        if (!customized) {
            apply(text);
            this.active = true;
        }
        refreshMetadata(variables, sampleParams, defaultHash);
    }

    /** Uma linha não personalizada que tinha saído do ficheiro e voltou. */
    public void reactivateDefault() {
        if (!customized) {
            this.active = true;
        }
    }

    /** Só as variáveis, os exemplos e o hash (modelo personalizado, ou ficheiro igual). */
    public void refreshMetadata(List<String> variables, Map<String, String> sampleParams, String defaultHash) {
        this.variables = List.copyOf(variables);
        this.sampleParams = Map.copyOf(sampleParams);
        this.defaultHash = defaultHash;
    }

    /** A linha deixou de estar no ficheiro. */
    public void deactivateRemoved() {
        this.active = false;
    }

    /** Edição do administrador ({@code PUT}). */
    public void customize(TemplateText text, boolean active) {
        apply(text);
        this.active = active;
        this.customized = true;
    }

    /** Repõe o texto do ficheiro ({@code reset}). */
    public void resetTo(TemplateText text) {
        apply(text);
        this.customized = false;
        this.active = true;
    }

    private void apply(TemplateText text) {
        this.subject = text.subject();
        this.body = Objects.requireNonNull(text.body(), "body");
        this.whatsappTemplateName = text.whatsappTemplateName();
        this.whatsappParams = text.whatsappParams() == null ? null : List.copyOf(text.whatsappParams());
    }

    /** O texto editável. */
    public TemplateText text() {
        return new TemplateText(subject, body, whatsappTemplateName, whatsappParams);
    }

    public String getCode() {
        return code;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public String getLocale() {
        return locale;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getWhatsappTemplateName() {
        return whatsappTemplateName;
    }

    public List<String> getWhatsappParams() {
        return whatsappParams == null ? List.of() : whatsappParams;
    }

    public List<String> getVariables() {
        return variables;
    }

    public Map<String, String> getSampleParams() {
        return sampleParams;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCustomized() {
        return customized;
    }

    public String getDefaultHash() {
        return defaultHash;
    }

    /**
     * Texto editável de um modelo.
     *
     * @param subject              assunto do e-mail ou título do push; {@code null} nos outros
     * @param whatsappTemplateName modelo aprovado pela Meta (só WhatsApp)
     * @param whatsappParams       variáveis, por ordem, enviadas como parâmetros do modelo WhatsApp
     */
    public record TemplateText(String subject, String body, String whatsappTemplateName,
                               List<String> whatsappParams) {
    }
}
