package mz.co.moseguros.policy.application;

import java.time.Duration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Métricas da emissão (S6-01 §5). Nomes Micrometer com pontos; no Prometheus (S6-08) são
 * {@code moseguros_policy_issued_total{productType}}, {@code moseguros_policy_issuance_failures_total{reason}}
 * e {@code moseguros_policy_issuance_latency_seconds} (com histograma: o alerta do S6-08 usa os
 * {@code _bucket}). Cada nome tem sempre as mesmas etiquetas.
 */
@Component
public class PolicyMetrics {

    public static final String ISSUED = "moseguros.policy.issued";
    public static final String FAILURES = "moseguros.policy.issuance.failures";
    public static final String LATENCY = "moseguros.policy.issuance.latency";

    /** Motivos das falhas de negócio da emissão (etiqueta {@code reason}). */
    public enum FailureReason {
        PROPOSAL_NOT_PAID,
        PAYMENT_NOT_VALID,
        AMOUNT_MISMATCH
    }

    private final ObjectProvider<MeterRegistry> registry;

    public PolicyMetrics(ObjectProvider<MeterRegistry> registry) {
        this.registry = registry;
    }

    public void issued(String productType, Duration latency) {
        MeterRegistry r = registry.getIfAvailable();
        if (r == null) {
            return;
        }
        Counter.builder(ISSUED).tag("productType", productType).register(r).increment();
        Timer.builder(LATENCY).publishPercentileHistogram().register(r)
                .record(latency.isNegative() ? Duration.ZERO : latency);
    }

    public void failure(FailureReason reason) {
        MeterRegistry r = registry.getIfAvailable();
        if (r != null) {
            Counter.builder(FAILURES).tag("reason", reason.name()).register(r).increment();
        }
    }
}
