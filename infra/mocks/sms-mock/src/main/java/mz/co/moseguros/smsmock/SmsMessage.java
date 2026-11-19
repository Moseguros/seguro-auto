package mz.co.moseguros.smsmock;

import java.time.Instant;
import java.util.Map;

/**
 * Mensagem recebida pelo simulador.
 *
 * @param channel  {@code SMS}, {@code WHATSAPP} ou {@code PUSH} (S6-04)
 * @param to       telemóvel; no push, as reticências e os últimos 8 caracteres do token
 * @param text     texto do SMS; no WhatsApp, o modelo e os parâmetros em texto; no push, título e texto
 * @param template modelo do WhatsApp ({@code null} no SMS e no push)
 * @param params   parâmetros do modelo do WhatsApp; no push, os dados ({@code notificationId}, {@code deepLink})
 */
public record SmsMessage(String id, String channel, String to, String text, String template,
                         Map<String, String> params, Instant receivedAt) {
}
