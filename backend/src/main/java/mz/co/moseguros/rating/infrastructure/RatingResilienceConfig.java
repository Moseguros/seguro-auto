package mz.co.moseguros.rating.infrastructure;

import java.time.Duration;
import java.util.UUID;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import mz.co.moseguros.rating.application.RatingProperties;
import org.springframework.stereotype.Component;

/**
 * Circuit breaker e bulkhead de cada seguradora externa (Resilience4j, S3-02 §8), criados nos
 * registos com o nome {@code rating-<insurerId>} e os valores de {@code moseguros.rating.external}.
 * Os registos são próprios da tarifação (não substituem os do auto-configure do Resilience4j).
 */
@Component
public class RatingResilienceConfig {

    public static final String PREFIX = "rating-";

    private final CircuitBreakerRegistry circuitBreakers;
    private final BulkheadRegistry bulkheads;

    public RatingResilienceConfig(RatingProperties properties) {
        RatingProperties.CircuitBreaker cb = properties.getExternal().getCircuitBreaker();
        this.circuitBreakers = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(cb.getSlidingWindowSize())
                .minimumNumberOfCalls(cb.getMinimumNumberOfCalls())
                .failureRateThreshold(cb.getFailureRateThreshold())
                .waitDurationInOpenState(cb.getWaitDurationInOpenState())
                .permittedNumberOfCallsInHalfOpenState(cb.getPermittedCallsInHalfOpenState())
                // As falhas são registadas à mão (HttpRatingProvider): DECLINED e 4xx não contam.
                .recordExceptions(Exception.class)
                .build());
        this.bulkheads = BulkheadRegistry.of(BulkheadConfig.custom()
                .maxConcurrentCalls(properties.getExternal().getBulkhead().getMaxConcurrentCalls())
                .maxWaitDuration(Duration.ZERO)
                .build());
    }

    public CircuitBreaker circuitBreaker(UUID insurerId) {
        return circuitBreakers.circuitBreaker(PREFIX + insurerId);
    }

    public Bulkhead bulkhead(UUID insurerId) {
        return bulkheads.bulkhead(PREFIX + insurerId);
    }
}
