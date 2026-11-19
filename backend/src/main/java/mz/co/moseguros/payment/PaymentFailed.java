package mz.co.moseguros.payment;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: uma intenção passou a {@code FAILED} ou {@code EXPIRED} (S5-02 §3). Ouvido a partir
 * do S6-04 ({@code PaymentNotificationsListener}).
 *
 * @param status      {@code FAILED} ou {@code EXPIRED}
 * @param failureCode nome de {@link FailureCode}
 */
public record PaymentFailed(UUID eventId, Instant occurredAt, UUID intentId, UUID insurerId, UUID payerUserId,
                            PaymentTargetType targetType, UUID targetId, PaymentMethod method, PaymentStatus status,
                            String failureCode) {
}
