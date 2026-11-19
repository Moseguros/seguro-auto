package mz.co.moseguros.payment;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/**
 * Um alvo pagável, devolvido por {@link PaymentTargetResolver#resolve} (S5-02 §1).
 *
 * @param description número de negócio do alvo (ex.: {@code PR-2027-000123}); nunca texto livre
 * @param expiresAt   prazo para pagar o alvo (a referência bancária vale até lá)
 */
public record PayableTarget(UUID insurerId, UUID payerUserId, Money amount, String description,
                            PaymentPurpose purpose, Instant expiresAt) {

    public PayableTarget {
        Objects.requireNonNull(insurerId, "insurerId");
        Objects.requireNonNull(payerUserId, "payerUserId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(purpose, "purpose");
        Objects.requireNonNull(expiresAt, "expiresAt");
    }
}
