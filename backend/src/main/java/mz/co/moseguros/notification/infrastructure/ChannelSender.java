package mz.co.moseguros.notification.infrastructure;

import mz.co.moseguros.shared.domain.Channel;

/**
 * Envio por um canal externo (ADR-003): uma implementação por canal ({@code SMS}, {@code WHATSAPP},
 * {@code EMAIL}), escolhida pela propriedade {@code moseguros.notification.<canal>.provider}. Usado pelos
 * códigos OTP (S1-03), pelos e-mails e SMS de modelo (S1-04, S5-02) e pelas notificações (S6-04). O push
 * tem o seu próprio {@link PushSender}. Era o {@code OtpDeliveryChannel} até ao S6-04.
 *
 * <p>As implementações nunca registam o texto, o código nem o destino completo.
 */
public interface ChannelSender {

    /** Modelo WhatsApp do código OTP (S1-03): um parâmetro, {@code code}. */
    String OTP_TEMPLATE = "moseguros_otp";
    /** Modelo WhatsApp do código de assinatura (S4-04): {@code code}, {@code ref}, {@code insurer}, {@code total}. */
    String SIGN_TEMPLATE = "moseguros_sign_otp";

    Channel channel();

    /** Nome do fornecedor, gravado em {@code provider} (ex.: {@code mock-sms}). */
    String provider();

    /** Se o fornecedor envia recibos de entrega para {@code /integrations/delivery-receipts/{provider}}. */
    default boolean acceptsDeliveryReceipts() {
        return false;
    }

    /**
     * Entrega a mensagem ao fornecedor. Pode lançar excepção: quem chama converte-a com
     * {@link ProviderErrors#classify}.
     */
    ProviderResult send(OutboundContent content);
}
