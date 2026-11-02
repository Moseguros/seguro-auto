package mz.co.moseguros.notification;

import java.util.UUID;

import mz.co.moseguros.shared.domain.Channel;

/**
 * Evento: o fornecedor informou, por recibo de entrega, que não entregou um código OTP.
 *
 * @param messageId     id da mensagem em {@code notification.outbound_message}
 * @param correlationId o {@code correlationId} indicado no envio (o id do {@code otp_challenge})
 * @param channel       canal da mensagem que falhou
 */
public record OtpDeliveryFailed(UUID messageId, UUID correlationId, Channel channel) {
}
