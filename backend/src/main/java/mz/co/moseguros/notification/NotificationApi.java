package mz.co.moseguros.notification;

import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.notification.application.EmailMessageService;
import mz.co.moseguros.notification.application.NotificationService;
import mz.co.moseguros.notification.application.OtpMessageService;
import mz.co.moseguros.notification.application.SmsMessageService;
import mz.co.moseguros.shared.domain.Channel;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Notificações: códigos OTP (S1-03), códigos de assinatura de propostas (S4-04),
 * e-mails e SMS de modelos simples (convite de staff, S1-04; pagamentos, S5-02) e, desde o S6-04, as
 * notificações ao cliente ({@link #notify}: centro de notificações, push, WhatsApp, SMS e e-mail com
 * alternativa). O {@code notification} nunca ouve eventos de outros módulos: quem quer notificar chama
 * {@link #notify} (modelo transversal §1.1).
 *
 * <p>Pode ser chamada de qualquer âmbito, dentro ou fora de uma transacção: os serviços em que
 * delega correm em {@code SYSTEM} ({@code outbound_message} só é visível nesse âmbito).
 */
@Service
public class NotificationApi {

    private final OtpMessageService otpMessages;
    private final EmailMessageService emailMessages;
    private final SmsMessageService smsMessages;
    private final NotificationService notifications;

    public NotificationApi(OtpMessageService otpMessages, EmailMessageService emailMessages,
                           SmsMessageService smsMessages, NotificationService notifications) {
        this.otpMessages = otpMessages;
        this.emailMessages = emailMessages;
        this.smsMessages = smsMessages;
        this.notifications = notifications;
    }

    /**
     * Regista uma notificação ao cliente e agenda o envio (S6-04 §1 e §4). Junta-se à transacção de quem chama
     * ({@code REQUIRED}): um rollback desfaz-a; o envio só começa depois do commit. Grava a linha do centro de
     * notificações e envia pelo primeiro canal disponível e permitido ({@code PUSH, WHATSAPP, SMS, EMAIL}), com
     * 3 tentativas e alternativa; nas categorias obrigatórias, push e um canal externo, mesmo com as
     * preferências desligadas. Conta inexistente ou não activa → nada é gravado (devolve um id novo). Nunca
     * lança por falha de um fornecedor.
     *
     * @param templateCode código do modelo ({@code POLICY_ISSUED}…)
     * @param params       variáveis do modelo ({@code Money}, {@code Instant}, {@code LocalDate} formatados na
     *                     língua do cliente; o resto por {@code toString()}) e, opcionais, {@code webPath}
     *                     ({@code /cliente/…}, vai para {@code link}) e {@code deepLink} ({@code moseguros://…}).
     *                     Nunca documentos, NUIT, códigos OTP, tokens nem segredos
     * @return o {@code notificationId} (igual em todas as mensagens desta chamada)
     * @throws IllegalArgumentException código de modelo desconhecido ou parâmetro em falta
     */
    public UUID notify(UUID userId, String templateCode, Map<String, Object> params, NotificationCategory category) {
        return notifications.notify(userId, templateCode, params, category);
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
     * Envia já o código de assinatura de uma proposta (S4-04 §4), com o mesmo comportamento de
     * {@link #sendOtp} (síncrono, 5 s, nunca lança: devolve {@code FAILED}); grava
     * {@code outbound_message.purpose = SIGN_OTP}. Textos {@code otp.sign.*} ou, com
     * {@code assistedByName}, {@code otp.sign-assisted.*}; WhatsApp com o modelo {@code moseguros_sign_otp}.
     *
     * @param correlationId id do desafio OTP; volta no evento {@link OtpDeliveryFailed}
     */
    public DeliveryResult sendSignOtp(Channel channel, String destination, String code, int validityMinutes,
                                      SignOtpText text, String language, UUID correlationId) {
        return otpMessages.sendSignOtp(channel, destination, code, validityMinutes, text, language, correlationId);
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

    /**
     * Envia já, de forma síncrona, um SMS a partir do modelo {@code templateKey} (a chave em
     * {@code messages/notifications_*.properties}, com os {@code args} formatados por
     * {@code MessageFormat}) pelo canal SMS, sem alternativa (S5-02 §10), e regista-o em
     * {@code outbound_message}. Nunca lança por falha do fornecedor: devolve {@code FAILED}. O texto não
     * é gravado nem registado.
     *
     * @param toE164        telemóvel em E.164 ({@code +258…})
     * @param language      {@code en} → inglês; outro valor → {@code pt}
     * @param purpose       finalidade gravada em {@code outbound_message.purpose} (ex.: {@code PAYMENT})
     * @param correlationId id do objecto que originou o envio (ex.: a intenção de pagamento)
     */
    public DeliveryResult sendSms(String toE164, String templateKey, Object[] args, String language, String purpose,
                                  UUID correlationId) {
        return smsMessages.sendSms(toE164, templateKey, args, language, purpose, correlationId);
    }
}
