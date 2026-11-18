package mz.co.moseguros.payment.application.reference;

import java.time.Clock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.DispatchMode;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
import mz.co.moseguros.payment.application.gateway.ReferenceData;
import mz.co.moseguros.payment.application.gateway.RefundRequest;
import mz.co.moseguros.payment.application.gateway.RefundResult;
import mz.co.moseguros.payment.application.gateway.StatusResult;
import mz.co.moseguros.payment.domain.PaymentIntent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Entidade e referência na rede Ponto24 (S5-02 §6; ADR-007): pede ao {@link ReferenceGenerator} uma
 * referência para a entidade da seguradora ({@code settings.entity}) e o montante exacto da intenção,
 * na mesma transacção ({@link DispatchMode#SYNC}). A confirmação vem dos ficheiros MEPS/BMEPS do banco
 * (S5-10): a consulta de estado devolve sempre {@code STILL_UNKNOWN}.
 *
 * <p>Sem bean {@link ReferenceGenerator} (antes do S5-07) o método não está disponível.
 */
@Component
public class BankReferenceGateway implements PaymentGateway {

    private final ObjectProvider<ReferenceGenerator> generator;
    private final Clock clock;

    public BankReferenceGateway(ObjectProvider<ReferenceGenerator> generator, Clock clock) {
        this.generator = generator;
        this.clock = clock;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.BANK_REFERENCE;
    }

    @Override
    public DispatchMode dispatchMode() {
        return DispatchMode.SYNC;
    }

    @Override
    public boolean available() {
        return generator.getIfUnique() != null;
    }

    @Override
    public InitiationResult initiate(PaymentIntent intent, MethodConfig config) {
        ReferenceGenerator gen = generator.getIfUnique();
        if (gen == null) {
            throw new IllegalStateException("No ReferenceGenerator bean");
        }
        String entity = config.settings() == null ? null : config.settings().path("entity").asText(null);
        if (entity == null || !entity.matches("^[0-9]{5}$")) {
            throw new IllegalStateException("BANK_REFERENCE configuration without a valid entity: " + config.id());
        }
        GeneratedReference generated = gen.generate(new ReferenceRequest(intent.getInsurerId(), intent.getId(),
                entity, intent.amount(), clock.instant()));
        ReferenceData data = new ReferenceData(entity, generated.reference(), generated.invoiceNumber(),
                generated.month());
        return InitiationResult.reference(data, masked(data));
    }

    @Override
    public StatusResult queryStatus(PaymentIntent intent, MethodConfig config) {
        return StatusResult.stillUnknown();
    }

    @Override
    public RefundResult refund(RefundRequest request, MethodConfig config) {
        throw new UnsupportedOperationException("Refunds are made outside the platform (S7)");
    }

    /** Sem dados pessoais: só a entidade, a referência e a factura. */
    private static JsonNode masked(ReferenceData data) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("entity", data.entity());
        node.put("reference", data.reference());
        node.put("invoiceNumber", data.invoiceNumber());
        node.put("month", data.month());
        return node;
    }
}
