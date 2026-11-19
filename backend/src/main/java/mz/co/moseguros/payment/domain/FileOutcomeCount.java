package mz.co.moseguros.payment.domain;

import java.util.UUID;

/** Pagamentos de um ficheiro ({@code inbound_message}) com um resultado. */
public record FileOutcomeCount(UUID sourceId, String fileSha256, InboundMessage.Outcome outcome, long count) {
}
