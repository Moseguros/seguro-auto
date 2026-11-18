package mz.co.moseguros.payment.application.gateway;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mz.co.moseguros.payment.PaymentMethod;
import org.springframework.stereotype.Component;

/**
 * Os adaptadores por método (S5-02 §6). Dois beans para o mesmo método impedem o arranque.
 */
@Component
public class GatewayRegistry {

    private final Map<PaymentMethod, PaymentGateway> gateways = new EnumMap<>(PaymentMethod.class);

    public GatewayRegistry(List<PaymentGateway> beans) {
        for (PaymentGateway gateway : beans) {
            PaymentGateway previous = gateways.putIfAbsent(gateway.method(), gateway);
            if (previous != null) {
                throw new IllegalStateException("Two payment gateways for " + gateway.method() + ": "
                        + previous.getClass().getName() + " and " + gateway.getClass().getName());
            }
        }
    }

    /** O adaptador do método, se existir e estiver disponível. */
    public Optional<PaymentGateway> find(PaymentMethod method) {
        return Optional.ofNullable(gateways.get(method)).filter(PaymentGateway::available);
    }

    public boolean isAvailable(PaymentMethod method) {
        return find(method).isPresent();
    }
}
