package mz.co.moseguros.payment.application.reference;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/**
 * Pedido de uma referência (S5-07 §1).
 *
 * @param entity   entidade Ponto24 da seguradora (5 dígitos)
 * @param amount   montante exacto (entra nos dígitos de controlo)
 * @param issuedAt momento da emissão (o mês conta-se em {@code Africa/Maputo})
 */
public record ReferenceRequest(UUID insurerId, UUID intentId, String entity, Money amount, Instant issuedAt) {
}
