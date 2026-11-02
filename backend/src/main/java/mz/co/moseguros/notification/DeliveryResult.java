package mz.co.moseguros.notification;

import java.util.UUID;

/**
 * Resultado de um envio.
 *
 * @param messageId     id da linha em {@code notification.outbound_message}
 * @param status        {@code SENT} se o fornecedor aceitou; {@code FAILED} se não
 * @param failureReason motivo curto, sem dados pessoais; {@code null} sem falha
 */
public record DeliveryResult(UUID messageId, DeliveryStatus status, String failureReason) {

    public boolean failed() {
        return status == DeliveryStatus.FAILED;
    }
}
