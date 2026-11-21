package mz.co.moseguros.verification.application;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.verification.domain.CheckerType;
import org.springframework.stereotype.Component;

/**
 * Limites de pedidos da verificação (S6-03 §5), com Bucket4j em memória: público por IP
 * ({@code public-per-minute}, 60; consulta por código e por matrícula juntas) e polícia por utilizador
 * ({@code police-per-minute}, 300; os três endpoints). Recarga gradual com capacidade igual ao limite. Os
 * <i>buckets</i> ficam numa cache Caffeine que os expira 10 minutos depois do último uso.
 *
 * <p>Excedido → 429 {@code RATE_LIMITED} com {@code retryAfterSeconds} (e o cabeçalho {@code Retry-After}).
 * O limite é por instância: com N instâncias o efectivo é N × o limite (revisão no S12).
 */
@Component
public class VerificationRateLimiter {

    static final Duration IDLE_EXPIRY = Duration.ofMinutes(10);
    static final Duration PERIOD = Duration.ofMinutes(1);

    private final VerificationProperties.RateLimit limits;
    private final VerificationMetrics metrics;
    private final Cache<String, Bucket> publicBuckets;
    private final Cache<UUID, Bucket> policeBuckets;

    public VerificationRateLimiter(VerificationProperties properties, VerificationMetrics metrics) {
        this.limits = properties.rateLimit();
        this.metrics = metrics;
        this.publicBuckets = Caffeine.newBuilder().expireAfterAccess(IDLE_EXPIRY).maximumSize(200_000).build();
        this.policeBuckets = Caffeine.newBuilder().expireAfterAccess(IDLE_EXPIRY).maximumSize(50_000).build();
    }

    /** Conta um pedido público do IP. @throws DomainException 429 {@code RATE_LIMITED} */
    public void consumePublic(String clientIp) {
        String key = clientIp == null ? "unknown" : clientIp;
        consume(publicBuckets.get(key, k -> bucket(limits.publicPerMinute())), CheckerType.PUBLIC);
    }

    /** Conta um pedido do agente. @throws DomainException 429 {@code RATE_LIMITED} */
    public void consumePolice(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        consume(policeBuckets.get(userId, k -> bucket(limits.policePerMinute())), CheckerType.POLICE);
    }

    private void consume(Bucket bucket, CheckerType checker) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return;
        }
        metrics.rateLimited(checker);
        long seconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill() + 999_999_999L));
        throw DomainException.of(ErrorCodes.RATE_LIMITED, "Too many verification requests")
                .withProperty(DomainException.RETRY_AFTER_SECONDS, seconds);
    }

    private static Bucket bucket(int perMinute) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(perMinute).refillGreedy(perMinute, PERIOD).build())
                .build();
    }
}
