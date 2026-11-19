package mz.co.moseguros.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/** Para que é o pagamento (S5-02 §1). No S5 só {@link #NEW_POLICY} (proposta); os outros chegam no S7. */
@Schema(enumAsRef = true)
public enum PaymentPurpose {
    NEW_POLICY,
    INSTALMENT,
    RENEWAL,
    ENDORSEMENT,
    FLEET_INCLUSION
}
