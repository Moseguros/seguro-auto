package mz.co.moseguros.payment.application.gateway;

import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.domain.PaymentIntent;

/**
 * Adaptador de uma forma de pagamento (S5-02 §6): {@code BankReferenceGateway} (S5-02, gerador real no
 * S5-07) e o adaptador M-Pesa (S5-04). Um só bean por método ({@link GatewayRegistry}).
 *
 * <p>Os adaptadores nunca registam segredos, tokens nem respostas cruas: o que devolvem em
 * {@code maskedRawResponse} já vem mascarado.
 */
public interface PaymentGateway {

    PaymentMethod method();

    /** {@code SYNC} (referência: na mesma transacção) ou {@code ASYNC_AFTER_COMMIT} (M-Pesa). */
    DispatchMode dispatchMode();

    /**
     * {@code false} se o adaptador não puder ser usado neste arranque (ex.: sem {@code ReferenceGenerator});
     * o método não aparece em {@code availableMethods} e o pedido dá {@code PAYMENT_METHOD_NOT_AVAILABLE}.
     */
    default boolean available() {
        return true;
    }

    InitiationResult initiate(PaymentIntent intent, MethodConfig config);

    /** Consulta de estado (S5-04). */
    StatusResult queryStatus(PaymentIntent intent, MethodConfig config);

    /** Reembolso (S7); até lá {@link UnsupportedOperationException}. */
    RefundResult refund(RefundRequest request, MethodConfig config);
}
