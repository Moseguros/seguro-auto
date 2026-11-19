package mz.co.moseguros.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipo do alvo de um pagamento (S5-02 §1; modelo transversal §1.1). O {@code payment} não conhece os
 * alvos: o módulo dono de cada tipo implementa {@link PaymentTargetResolver}.
 */
@Schema(enumAsRef = true)
public enum PaymentTargetType {
    PROPOSAL,
    INSTALMENT,
    RENEWAL,
    ENDORSEMENT,
    FLEET_INCLUSION
}
