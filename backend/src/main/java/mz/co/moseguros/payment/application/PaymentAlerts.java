package mz.co.moseguros.payment.application;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Alertas operacionais dos pagamentos (S5-10 §5; o S5-04 usa-o para {@code UNRESOLVED_STATUS}): uma linha
 * {@code WARN} com {@code alert=<code>} e o contador {@code moseguros.payment.alerts{code}}. O envio por
 * e-mail ou outro canal fica para o S12-08. As etiquetas nunca levam dados pessoais nem segredos.
 */
@Component
public class PaymentAlerts {

    public static final String COUNTER = "moseguros.payment.alerts";
    private static final Logger log = LoggerFactory.getLogger(PaymentAlerts.class);

    private final ObjectProvider<MeterRegistry> meters;

    public PaymentAlerts(ObjectProvider<MeterRegistry> meters) {
        this.meters = meters;
    }

    /**
     * @param insurerId seguradora afectada, ou {@code null}
     * @param tags      etiquetas do log (ex.: {@code sourceId}, {@code queue}); não vão para a métrica
     */
    public void raise(String code, UUID insurerId, Map<String, String> tags) {
        Objects.requireNonNull(code, "code");
        String extra = tags == null || tags.isEmpty() ? "" : " " + new TreeMap<>(tags).entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(" "));
        log.warn("Payment alert alert={} insurerId={}{}", code, insurerId, extra);
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            registry.counter(COUNTER, "code", code).increment();
        }
    }
}
