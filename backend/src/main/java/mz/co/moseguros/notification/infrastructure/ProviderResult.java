package mz.co.moseguros.notification.infrastructure;

/**
 * Resposta de um fornecedor a um envio (S6-04 §6).
 *
 * @param success           o fornecedor aceitou a mensagem
 * @param provider          nome do fornecedor ({@code ChannelSender.provider()}, {@code mock-push}, {@code fcm})
 * @param providerMessageId id devolvido pelo fornecedor (volta nos recibos); pode ser {@code null}
 * @param failureKind       {@code TEMPORARY} (volta a tentar) ou {@code PERMANENT}; {@code null} com sucesso
 * @param reason            motivo curto, sem dados pessoais, quando não foi aceite
 */
public record ProviderResult(boolean success, String provider, String providerMessageId, FailureKind failureKind,
                             String reason) {

    /** Tipo de falha. */
    public enum FailureKind {
        /** Timeout, rede, 5xx, 429: tenta de novo. */
        TEMPORARY,
        /** Destino ou token inválido: não adianta tentar de novo. */
        PERMANENT
    }

    public static ProviderResult accepted(String provider, String providerMessageId) {
        return new ProviderResult(true, provider, providerMessageId, null, null);
    }

    public static ProviderResult temporary(String provider, String reason) {
        return new ProviderResult(false, provider, null, FailureKind.TEMPORARY, reason);
    }

    public static ProviderResult permanent(String provider, String reason) {
        return new ProviderResult(false, provider, null, FailureKind.PERMANENT, reason);
    }

    public boolean permanent() {
        return !success && failureKind == FailureKind.PERMANENT;
    }
}
