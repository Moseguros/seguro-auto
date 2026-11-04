package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: o cliente (ou o mediador) submeteu a proposta (S4-05 §6, passo 4). Publicado na
 * transacção da submissão (outbox do Spring Modulith), antes da decisão automática. Ninguém o ouve
 * neste sprint.
 *
 * @param brokerOrgId mediador da proposta; {@code null} nas do cliente
 * @param totalAmount prémio total em centavos
 */
public record ProposalSubmitted(UUID eventId, Instant occurredAt, UUID proposalId, UUID insurerId,
                                UUID customerUserId, UUID brokerOrgId, long totalAmount) {
}
