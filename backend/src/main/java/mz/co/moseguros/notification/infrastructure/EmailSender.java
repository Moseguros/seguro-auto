package mz.co.moseguros.notification.infrastructure;

import java.util.Map;

import mz.co.moseguros.shared.domain.Channel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * E-mail em texto simples por SMTP ({@code spring.mail.*}; localmente o Mailpit). Sem servidor
 * SMTP configurado, o envio falha ({@code FAILED}) mas a aplicação arranca.
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.email.provider", havingValue = "smtp", matchIfMissing = true)
public class EmailSender implements OtpDeliveryChannel {

    public static final String PROVIDER = "smtp";

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String from;

    public EmailSender(ObjectProvider<JavaMailSender> mailSender, NotificationProperties properties) {
        this.mailSender = mailSender;
        this.from = properties.email().from();
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public ProviderResult send(String destination, String text, Map<String, String> templateParams) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            return ProviderResult.failed("smtp not configured");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(destination);
        message.setSubject(templateParams.get(PARAM_SUBJECT));
        message.setText(text);
        sender.send(message);
        return ProviderResult.accepted(null);
    }
}
