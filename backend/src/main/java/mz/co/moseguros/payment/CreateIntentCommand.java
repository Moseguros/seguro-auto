package mz.co.moseguros.payment;

import java.util.Objects;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Pedido de uma intenção de pagamento ({@link PaymentApi#createIntent}, S5-02 §4).
 *
 * @param payerMsisdn       obrigatório para M-Pesa; ignorado nas referências
 * @param idempotencyKey    valor do cabeçalho {@code Idempotency-Key}
 * @param initiatedByUserId quem pede (no S5 tem de ser o pagador do alvo)
 */
public record CreateIntentCommand(PaymentTargetType targetType, UUID targetId, PaymentMethod method,
                                  Msisdn payerMsisdn, UUID idempotencyKey, InitiatedVia initiatedVia,
                                  UUID initiatedByUserId) {

    public CreateIntentCommand {
        Objects.requireNonNull(targetType, "targetType");
        Objects.requireNonNull(targetId, "targetId");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        Objects.requireNonNull(initiatedVia, "initiatedVia");
        Objects.requireNonNull(initiatedByUserId, "initiatedByUserId");
    }
}
