package mz.co.moseguros.notification;

import java.util.UUID;

import mz.co.moseguros.notification.application.OtpMessageService;
import mz.co.moseguros.shared.domain.Channel;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Notificações. Nesta fase só envia códigos OTP (S1-03); as
 * notificações gerais chegam no Sprint 6.
 *
 * <p>Pode ser chamada de qualquer âmbito, dentro ou fora de uma transacção: os serviços em que
 * delega correm em {@code SYSTEM} ({@code outbound_message} só é visível nesse âmbito).
 */
@Service
public class NotificationApi {

    private final OtpMessageService otpMessages;

    public NotificationApi(OtpMessageService otpMessages) {
        this.otpMessages = otpMessages;
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
}
