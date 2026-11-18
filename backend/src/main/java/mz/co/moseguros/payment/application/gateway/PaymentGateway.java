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

    /**
     * Envio ao fornecedor. Os adaptadores {@code ASYNC_AFTER_COMMIT} só são chamados pelo
     * {@code MpesaC2bJob}, depois do compare-and-set {@code CREATED → PENDING}, uma vez por intenção.
     */
    InitiationResult initiate(PaymentIntent intent, MethodConfig config);

    /**
     * Referência do pedido gravada em {@code third_party_reference} <b>antes</b> do envio (M-Pesa:
     * {@code input_ThirdPartyReference}); {@code null} se o adaptador não a usar.
     */
    default String newThirdPartyReference() {
        return null;
    }

    /**
     * Consulta de estado de uma intenção {@code UNKNOWN} (S5-04 §4). <b>Nunca</b> volta a enviar o
     * pedido de pagamento; erros técnicos dão {@code STILL_UNKNOWN}.
     */
    StatusResult queryStatus(PaymentIntent intent, MethodConfig config);

    /**
     * Reversão no fornecedor (M-Pesa, S5-04 §7; nenhum endpoint nem job a usa no S5, as devoluções
     * fazem-se fora da plataforma). Métodos sem reversão: {@link UnsupportedOperationException}.
     */
    RefundResult refund(RefundRequest request, MethodConfig config);
}
