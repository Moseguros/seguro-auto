package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: uma proposta passou a {@code EXPIRED} ({@code ProposalExpiryJob}, S4-02 §13).
 * Publicado na mesma transacção (outbox do Spring Modulith). Ouvido a partir do S6-04
 * ({@code ProposalNotificationsListener}: aviso ao cliente).
 *
 * @param brokerOrgId mediador da proposta; {@code null} nas do cliente
 * @param fromStatus  {@code DRAFT}, {@code INFO_REQUESTED} ou {@code ACCEPTED}
 */
public record ProposalExpired(UUID eventId, Instant occurredAt, UUID proposalId, UUID insurerId,
                              UUID customerUserId, UUID brokerOrgId, ProposalStatus fromStatus) {
}
