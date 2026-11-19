package mz.co.moseguros.verification.application;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.verification.domain.CheckerType;
import mz.co.moseguros.verification.domain.SubjectType;
import mz.co.moseguros.verification.domain.VerificationResult;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Métricas da verificação (S6-03 §3 e §5). No Prometheus:
 * {@code moseguros_verification_checks_total{checker_type, subject_type, result}} e
 * {@code moseguros_verification_rate_limited_total{checker_type}}.
 */
@Component
public class VerificationMetrics {

    public static final String CHECKS = "moseguros.verification.checks";
    public static final String RATE_LIMITED = "moseguros.verification.rate.limited";

    private final ObjectProvider<MeterRegistry> registry;

    public VerificationMetrics(ObjectProvider<MeterRegistry> registry) {
        this.registry = registry;
    }

    public void checked(CheckerType checker, SubjectType subject, VerificationResult result) {
        MeterRegistry r = registry.getIfAvailable();
        if (r != null) {
            Counter.builder(CHECKS).tag("checker_type", checker.name()).tag("subject_type", subject.name())
                    .tag("result", result.name()).register(r).increment();
        }
    }

    public void rateLimited(CheckerType checker) {
        MeterRegistry r = registry.getIfAvailable();
        if (r != null) {
            Counter.builder(RATE_LIMITED).tag("checker_type", checker.name()).register(r).increment();
        }
    }
}
