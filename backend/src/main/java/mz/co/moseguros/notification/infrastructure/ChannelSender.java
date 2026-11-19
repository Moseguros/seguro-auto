package mz.co.moseguros.notification.infrastructure;

import java.util.Map;

import mz.co.moseguros.shared.domain.Channel;

/**
 * Envio por um canal (ADR-003): uma implementação por canal, escolhida pela propriedade
 * {@code moseguros.notification.<canal>.provider}. Reutilizado pelas notificações no Sprint 6.
 *
 * <p>As implementações nunca registam o texto, o código nem o destino completo.
 */
public interface OtpDeliveryChannel {

    /** Parâmetro do modelo com o código (WhatsApp {@code moseguros_otp}). */
    String PARAM_CODE = "code";
    /** Assunto (só e-mail). */
    String PARAM_SUBJECT = "subject";
    /** Modelo do WhatsApp; sem ele, {@code moseguros_otp}. */
    String PARAM_TEMPLATE = "template";
    /** Modelo WhatsApp do código de assinatura de uma proposta (S4-04): {@code code}, {@code ref}, {@code insurer}, {@code total}. */
    String SIGN_TEMPLATE = "moseguros_sign_otp";
    /** Número da proposta (modelo {@link #SIGN_TEMPLATE}). */
    String PARAM_REF = "ref";
    /** Nome da seguradora (modelo {@link #SIGN_TEMPLATE}). */
    String PARAM_INSURER = "insurer";
    /** Prémio total formatado (modelo {@link #SIGN_TEMPLATE}). */
    String PARAM_TOTAL = "total";

    Channel channel();

    /** Nome do fornecedor, gravado em {@code outbound_message.provider} (ex.: {@code mock-sms}). */
    String provider();

    /** Se o fornecedor envia recibos de entrega para {@code /integrations/delivery-receipts/{provider}}. */
    default boolean acceptsDeliveryReceipts() {
        return false;
    }

    /**
     * Entrega a mensagem ao fornecedor. Pode lançar excepção: quem chama converte-a em
     * {@code FAILED}.
     *
     * @param destination    telemóvel em E.164 ou e-mail
     * @param text           texto já traduzido (SMS e corpo do e-mail)
     * @param templateParams parâmetros do modelo ({@link #PARAM_CODE}, {@link #PARAM_SUBJECT}; no código de
     *                       assinatura {@link #PARAM_TEMPLATE} e os parâmetros de {@link #SIGN_TEMPLATE})
     */
    ProviderResult send(String destination, String text, Map<String, String> templateParams);
}
