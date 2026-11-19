package mz.co.moseguros.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/** Por onde foi pedido o pagamento ({@code payment_intent.initiated_via}, S5-02 §1). */
@Schema(enumAsRef = true)
public enum InitiatedVia {
    APP,
    WEB,
    /** Página aberta pelo link que o mediador enviou ao cliente (paga o cliente; o mediador nunca recebe dinheiro). */
    BROKER_LINK,
    /** S12-02. */
    USSD
}
