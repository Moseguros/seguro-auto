package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/**
 * Evento público: uma proposta passou a {@code PAID} ({@code ProposalPaymentListener}, S5-02 §9), na
 * mesma transacção (outbox do Spring Modulith). Ouvido pelo {@code policy} no S6-01, que emite a apólice
 * automaticamente.
 *
 * @param paidAt momento da confirmação do pagamento ({@code PaymentSucceeded.paidAt})
 */
public record ProposalPaid(UUID eventId, Instant occurredAt, UUID proposalId, UUID insurerId, UUID customerUserId,
                           UUID paymentIntentId, Money amount, Instant paidAt) {
}
