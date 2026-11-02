package mz.co.moseguros.notification;

/**
 * Estado de uma mensagem enviada ({@code notification.outbound_message.status}).
 */
public enum DeliveryStatus {
    /** Gravada, ainda sem resposta do fornecedor. */
    PENDING,
    /** Aceite pelo fornecedor. */
    SENT,
    /** O fornecedor confirmou a entrega (recibo). */
    DELIVERED,
    /** O fornecedor recusou, não respondeu ou enviou um recibo de falha. */
    FAILED
}
