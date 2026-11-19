package mz.co.moseguros.payment;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/**
 * Evento público: uma intenção foi reembolsada. Definido no S5-02; publicado só a partir do S7-05 (a
 * devolução do dinheiro faz-se sempre fora da plataforma).
 */
public record PaymentRefunded(UUID eventId, Instant occurredAt, UUID intentId, UUID insurerId,
                              PaymentTargetType targetType, UUID targetId, Money amount) {
}
