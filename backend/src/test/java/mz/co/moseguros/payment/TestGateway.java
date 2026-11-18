package mz.co.moseguros.payment;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.DispatchMode;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
import mz.co.moseguros.payment.application.gateway.RefundRequest;
import mz.co.moseguros.payment.application.gateway.RefundResult;
import mz.co.moseguros.payment.application.gateway.StatusResult;
import mz.co.moseguros.payment.domain.PaymentIntent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Adaptador M-Pesa controlável dos testes do S5-02 (perfil {@code test}; o real é o do S5-04, que
 * substitui este): assíncrono como o real, devolve o resultado escolhido pelo teste ({@code PENDING} por
 * defeito) e regista as intenções enviadas.
 */
@Component
@Profile("test")
public class TestGateway implements PaymentGateway {

    private final AtomicReference<InitiationResult> next = new AtomicReference<>();
    private final List<UUID> initiated = new CopyOnWriteArrayList<>();

    /** Resultado dos envios seguintes; {@code null} = {@code PENDING}. */
    public void nextResult(InitiationResult result) {
        next.set(result);
    }

    public void reset() {
        next.set(null);
        initiated.clear();
    }

    /** Intenções enviadas, por ordem. */
    public List<UUID> initiated() {
        return List.copyOf(initiated);
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.MPESA;
    }

    @Override
    public DispatchMode dispatchMode() {
        return DispatchMode.ASYNC_AFTER_COMMIT;
    }

    @Override
    public InitiationResult initiate(PaymentIntent intent, MethodConfig config) {
        initiated.add(intent.getId());
        InitiationResult result = next.get();
        if (result != null) {
            return result;
        }
        return InitiationResult.pending("TEST-TX-" + intent.getReference(), "TEST-CONV", intent.getReference(),
                JsonNodeFactory.instance.objectNode().put("output_ResponseCode", "INS-0")
                        .put("msisdn", intent.getPayerMsisdn().masked()));
    }

    @Override
    public StatusResult queryStatus(PaymentIntent intent, MethodConfig config) {
        return StatusResult.stillUnknown();
    }

    @Override
    public RefundResult refund(RefundRequest request, MethodConfig config) {
        throw new UnsupportedOperationException();
    }
}
