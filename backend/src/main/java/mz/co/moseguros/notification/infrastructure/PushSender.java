package mz.co.moseguros.notification.infrastructure;

import java.util.Map;

/**
 * Envio de push (S6-04 §6; modelo transversal §7), escolhido por {@code moseguros.notification.push.provider}:
 * {@code mock} ({@link MockPushSender}) ou {@code fcm} ({@link FcmPushSender}). Com o perfil {@code prod},
 * {@code mock} impede o arranque. O token nunca aparece em logs.
 */
public interface PushSender {

    /** Nome do fornecedor ({@code mock-push} ou {@code fcm}). */
    String provider();

    /**
     * Pode lançar excepção: quem chama converte-a com {@link ProviderErrors#classify}.
     *
     * @param data {@code notificationId} e, se houver, {@code deepLink}
     */
    ProviderResult send(String deviceToken, String title, String body, Map<String, String> data);
}
