package mz.co.moseguros.payment;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.Money;

/**
 * O que o cliente tem de fazer a seguir ({@code IntentView.nextAction}, S5-02 §1). Só os campos do
 * tipo vêm preenchidos.
 *
 * @param timeoutAt   {@code AWAIT_CUSTOMER_CONFIRMATION}: até quando confirmar no telemóvel
 * @param nextCheckAt {@code CHECKING_STATUS}: próxima consulta de estado
 * @param entity      {@code PAY_BY_REFERENCE}: entidade (5 dígitos)
 * @param reference   {@code PAY_BY_REFERENCE}: referência (11 dígitos, sem espaços)
 * @param amount      {@code PAY_BY_REFERENCE}: montante exacto (a rede recusa outro)
 * @param validUntil  {@code PAY_BY_REFERENCE}: prazo para pagar
 */
@Schema(name = "PaymentNextAction")
public record NextAction(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) Type type, Instant timeoutAt,
                         Instant nextCheckAt, String entity, String reference, Money amount, Instant validUntil) {

    /** Tipo da próxima acção. */
    @Schema(name = "PaymentNextActionType", enumAsRef = true)
    public enum Type { AWAIT_CUSTOMER_CONFIRMATION, CHECKING_STATUS, PAY_BY_REFERENCE, NONE }

    public static NextAction none() {
        return new NextAction(Type.NONE, null, null, null, null, null, null);
    }

    public static NextAction awaitConfirmation(Instant timeoutAt) {
        return new NextAction(Type.AWAIT_CUSTOMER_CONFIRMATION, timeoutAt, null, null, null, null, null);
    }

    public static NextAction checkingStatus(Instant nextCheckAt) {
        return new NextAction(Type.CHECKING_STATUS, null, nextCheckAt, null, null, null, null);
    }

    public static NextAction payByReference(String entity, String reference, Money amount, Instant validUntil) {
        return new NextAction(Type.PAY_BY_REFERENCE, null, null, entity, reference, amount, validUntil);
    }
}
