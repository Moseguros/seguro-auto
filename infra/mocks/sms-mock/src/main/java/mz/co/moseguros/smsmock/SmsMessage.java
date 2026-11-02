package mz.co.moseguros.smsmock;

import java.time.Instant;
import java.util.Map;

/**
 * Mensagem recebida pelo simulador.
 *
 * @param channel  {@code SMS} ou {@code WHATSAPP}
 * @param text     texto do SMS; no WhatsApp, o modelo e os parâmetros em texto
 * @param template modelo do WhatsApp ({@code null} no SMS)
 * @param params   parâmetros do modelo do WhatsApp ({@code null} no SMS)
 */
public record SmsMessage(String id, String channel, String to, String text, String template,
                         Map<String, String> params, Instant receivedAt) {
}
