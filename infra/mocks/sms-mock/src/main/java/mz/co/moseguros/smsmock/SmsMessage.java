package mz.co.moseguros.smsmock;

import java.time.Instant;

/** SMS recebido pelo simulador. */
public record SmsMessage(String id, String to, String text, Instant receivedAt) {
}
