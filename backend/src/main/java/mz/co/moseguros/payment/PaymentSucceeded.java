package mz.co.moseguros.payment;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/**
 * Evento público: uma intenção passou a {@code SUCCEEDED} (S5-02 §3; modelo transversal §1.2). Publicado
 * na mesma transacção (outbox do Spring Modulith). Ouvido pelo {@code proposal}
 * ({@code ProposalPaymentListener}: alvo {@code PROPOSAL} → {@code PAID}) e, a partir do S6/S7, pelo
 * {@code payment}, {@code policy} e {@code fraud}.
 *
 * @param paidAt momento da confirmação do pagamento
 */
public record PaymentSucceeded(UUID eventId, Instant occurredAt, UUID intentId, String reference, UUID insurerId,
                               UUID payerUserId, PaymentTargetType targetType, UUID targetId, PaymentPurpose purpose,
                               PaymentMethod method, Money amount, Instant paidAt) {
}
