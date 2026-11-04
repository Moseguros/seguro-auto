package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: o subscritor recusou a proposta (S4-05 §7). Publicado na mesma transacção (outbox).
 *
 * @param reasonCode um dos {@code RejectionReasonCode}
 */
public record ProposalRejected(UUID eventId, Instant occurredAt, UUID proposalId, UUID insurerId,
                               UUID customerUserId, UUID brokerOrgId, String reasonCode) {
}
