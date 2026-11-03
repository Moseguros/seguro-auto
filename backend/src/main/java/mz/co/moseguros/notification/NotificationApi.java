package mz.co.moseguros.notification;

import java.util.UUID;

import mz.co.moseguros.notification.application.EmailMessageService;
import mz.co.moseguros.notification.application.OtpMessageService;
import mz.co.moseguros.shared.domain.Channel;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Notificações. Nesta fase envia códigos OTP (S1-03) e e-mails
 * de modelos simples, como o convite de staff (S1-04); as notificações gerais chegam no Sprint 6.
 *
 * <p>Pode ser chamada de qualquer âmbito, dentro ou fora de uma transacção: os serviços em que
 * delega correm em {@code SYSTEM} ({@code outbound_message} só é visível nesse âmbito).
 */
@Service
public class NotificationApi {

    private final OtpMessageService otpMessages;
    private final EmailMessageService emailMessages;

    public NotificationApi(OtpMessageService otpMessages, EmailMessageService emailMessages) {
        this.otpMessages = otpMessages;
        this.emailMessages = emailMessages;
    }

    /**
     * Envia já, de forma síncrona (timeout de 5 s), um código OTP pelo canal indicado. Nunca
     * lança por falha do fornecedor: devolve {@code FAILED} com um motivo curto.
     *
     * @param destination     telemóvel em E.164 ({@code +258…}) para SMS e WhatsApp; e-mail normalizado
     * @param validityMinutes validade do código, mostrada na mensagem
     * @param language        {@code pt} ou {@code en} (outro valor → {@code pt})
     * @param correlationId   id do desafio OTP; volta no evento {@link OtpDeliveryFailed}
     */
    public DeliveryResult sendOtp(Channel channel, String destination, String code, int validityMinutes,
                                  String language, UUID correlationId) {
        return otpMessages.sendOtp(channel, destination, code, validityMinutes, language, correlationId);
    }

    /**
     * Envia já, de forma síncrona, um e-mail a partir do modelo {@code templateKey}
     * ({@code <templateKey>.subject} e {@code <templateKey>.body} em
     * {@code messages/notifications_*.properties}) e regista-o em {@code outbound_message}. Nunca
     * lança por falha do fornecedor: devolve {@code FAILED} com um motivo curto. O texto não é
     * gravado nem registado.
     *
     * @param to            e-mail normalizado
     * @param args          argumentos do modelo ({@code {0}}, {@code {1}}…); um
     *                      {@code MessageSourceResolvable} é traduzido na língua do e-mail
     * @param language      {@code pt} ou {@code en} (outro valor → {@code pt})
     * @param purpose       finalidade gravada em {@code outbound_message.purpose} (ex.: {@code INVITATION})
     * @param correlationId id do objecto que originou o envio (ex.: o convite)
     */
    public DeliveryResult sendEmail(String to, String templateKey, Object[] args, String language, String purpose,
                                    UUID correlationId) {
        return emailMessages.sendEmail(to, templateKey, args, language, purpose, correlationId);
    }
}
