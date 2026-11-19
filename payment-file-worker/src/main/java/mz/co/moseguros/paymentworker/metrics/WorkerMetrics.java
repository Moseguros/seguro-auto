package mz.co.moseguros.paymentworker.metrics;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Métricas do worker (S5-09 §8), em {@code /actuator/metrics}:
 *
 * <ul>
 *   <li>{@value #FILES} (contador; {@code source}, {@code outcome} = {@code done}/{@code error}/{@code skipped});</li>
 *   <li>{@value #RECORDS_PUBLISHED} (contador; {@code source});</li>
 *   <li>{@value #POLLS} (contador; {@code source}, {@code outcome} = {@code OK}/{@code ERROR});</li>
 *   <li>{@value #LAST_SUCCESS} (gauge; {@code source}): <i>epoch</i> em segundos da última leitura {@code OK}.</li>
 * </ul>
 *
 * <p>Também conta os ficheiros descarregados ({@value #DOWNLOADS}), para ver nos testes e em operação
 * que um ficheiro já tratado não volta a ser descarregado.
 */
@Component
public class WorkerMetrics {

    public static final String FILES = "worker.files";
    public static final String RECORDS_PUBLISHED = "worker.records.published";
    public static final String POLLS = "worker.polls";
    public static final String LAST_SUCCESS = "worker.last.success";
    public static final String DOWNLOADS = "worker.downloads";

    public static final String DONE = "done";
    public static final String ERROR = "error";
    public static final String SKIPPED = "skipped";

    private final MeterRegistry registry;
    private final Map<UUID, AtomicLong> lastSuccess = new ConcurrentHashMap<>();

    public WorkerMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void file(UUID source, String outcome) {
        Counter.builder(FILES).tag("source", source.toString()).tag("outcome", outcome).register(registry).increment();
    }

    public void recordsPublished(UUID source, int count) {
        if (count > 0) {
            Counter.builder(RECORDS_PUBLISHED).tag("source", source.toString()).register(registry).increment(count);
        }
    }

    public void download(UUID source) {
        Counter.builder(DOWNLOADS).tag("source", source.toString()).register(registry).increment();
    }

    public void poll(UUID source, String outcome, Instant finishedAt) {
        Counter.builder(POLLS).tag("source", source.toString()).tag("outcome", outcome).register(registry).increment();
        if ("OK".equals(outcome)) {
            lastSuccess.computeIfAbsent(source, s -> {
                AtomicLong value = new AtomicLong();
                Gauge.builder(LAST_SUCCESS, value, AtomicLong::get).tag("source", s.toString()).register(registry);
                return value;
            }).set(finishedAt.getEpochSecond());
        }
    }

    /** Ficheiros descarregados desta fonte desde o arranque. */
    public double downloads(UUID source) {
        Counter c = registry.find(DOWNLOADS).tag("source", source.toString()).counter();
        return c == null ? 0 : c.count();
    }
}
