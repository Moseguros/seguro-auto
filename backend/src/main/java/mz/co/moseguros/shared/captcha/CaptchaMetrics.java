package mz.co.moseguros.shared.captcha;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * {@code moseguros_captcha_results_total{result}} (S6-03 §7): um incremento por cada token confirmado,
 * qualquer que seja o fornecedor. Quem chama o {@link CaptchaVerifier} regista o resultado.
 */
@Component
public class CaptchaMetrics {

    public static final String RESULTS = "moseguros.captcha.results";

    private final ObjectProvider<MeterRegistry> registry;

    public CaptchaMetrics(ObjectProvider<MeterRegistry> registry) {
        this.registry = registry;
    }

    public void record(CaptchaResult result) {
        MeterRegistry r = registry.getIfAvailable();
        if (r != null) {
            Counter.builder(RESULTS).tag("result", result.name()).register(r).increment();
        }
    }
}
