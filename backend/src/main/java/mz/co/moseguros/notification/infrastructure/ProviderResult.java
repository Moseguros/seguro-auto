package mz.co.moseguros.notification.infrastructure;

/**
 * Resposta de um fornecedor a um envio.
 *
 * @param accepted          o fornecedor aceitou a mensagem
 * @param providerMessageId id devolvido pelo fornecedor (volta nos recibos); pode ser {@code null}
 * @param failureReason     motivo curto, sem dados pessoais, quando não foi aceite
 */
public record ProviderResult(boolean accepted, String providerMessageId, String failureReason) {

    public static ProviderResult accepted(String providerMessageId) {
        return new ProviderResult(true, providerMessageId, null);
    }

    public static ProviderResult failed(String reason) {
        return new ProviderResult(false, null, reason);
    }
}
