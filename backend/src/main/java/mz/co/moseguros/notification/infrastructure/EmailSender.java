package mz.co.moseguros.notification.infrastructure;

import mz.co.moseguros.shared.domain.Channel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * E-mail em texto simples por SMTP ({@code spring.mail.*}; localmente o Mailpit), com o {@code subject} e o
 * {@code text} do conteúdo. Sem servidor SMTP configurado, o envio falha ({@code TEMPORARY}) mas a
 * aplicação arranca.
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.email.provider", havingValue = "smtp", matchIfMissing = true)
public class EmailSender implements ChannelSender {

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
    public ProviderResult send(OutboundContent content) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            return ProviderResult.temporary(PROVIDER, "smtp not configured");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(content.destination());
        message.setSubject(content.subject());
        message.setText(content.text());
        sender.send(message);
        return ProviderResult.accepted(PROVIDER, null);
    }
}
