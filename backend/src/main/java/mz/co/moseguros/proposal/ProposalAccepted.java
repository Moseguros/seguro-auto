package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: a proposta passou a {@code ACCEPTED} (S4-05 §6 e §7), automaticamente ou pelo
 * subscritor. Publicado na mesma transacção (outbox). Ouvido a partir do S6-04 só pelo próprio
 * {@code proposal} ({@code ProposalNotificationsListener}); o {@code fraud} não ouve eventos de
 * proposta (D17).
 */
public record ProposalAccepted(UUID eventId, Instant occurredAt, UUID proposalId, UUID insurerId,
                               UUID customerUserId, UUID brokerOrgId, AcceptanceMode mode) {
}
