package mz.co.moseguros.payment;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/**
 * Evento público: foi gerada uma entidade e referência para uma intenção (S5-02 §6). Sem ouvintes no
 * S5 (modelo transversal §1.2); o S6-04 envia o aviso ao cliente.
 */
public record ReferenceGenerated(UUID eventId, Instant occurredAt, UUID intentId, UUID insurerId, UUID payerUserId,
                                 String entity, String reference, Money amount, Instant validUntil) {
}
