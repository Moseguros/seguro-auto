package mz.co.moseguros.paymentworker.sources;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.FileSourceList;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import mz.co.moseguros.paymentworker.processing.SourcePoller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Fontes agendadas (S5-09 §4). No arranque e a cada {@code sources-refresh-interval} pede as fontes ao
 * backend:
 *
 * <ul>
 *   <li>fonte nova, ou com {@code version} diferente → (re)agenda {@link SourcePoller#pollWithLock} com
 *       {@code scheduleWithFixedDelay(…, poll-interval)}: a leitura seguinte começa {@code poll-interval}
 *       depois do fim da anterior, a primeira logo a seguir. A plataforma fica sempre em escuta: a
 *       frequência real é a que o banco combinou com a seguradora (ADR-007);</li>
 *   <li>fonte que deixou de vir → cancela;</li>
 *   <li>backend inacessível → mantém os agendamentos, {@code WARN}, tenta no ciclo seguinte; no arranque,
 *       sem resposta, tenta a cada {@value #STARTUP_RETRY_SECONDS} s.</li>
 * </ul>
 *
 * <p>Também é o indicador de saúde {@code sources}: {@code UP} com o número de fontes agendadas;
 * {@code DOWN} se o backend não responde há mais de 15 min.
 */
@Component("sources")
public class SourceRegistry implements HealthIndicator {

    static final long STARTUP_RETRY_SECONDS = 30;
    static final Duration BACKEND_DOWN_AFTER = Duration.ofMinutes(15);

    private static final Logger log = LoggerFactory.getLogger(SourceRegistry.class);

    private final BackendSourceClient backend;
    private final SourcePoller poller;
    private final TaskScheduler sourceScheduler;
    private final TaskScheduler refreshScheduler;
    private final WorkerProperties properties;
    private final Clock clock;
    private final Instant startedAt;
    private final Map<UUID, Scheduled> scheduled = new HashMap<>();
    private volatile Instant lastBackendSuccess;
    private volatile boolean stopped;

    private record Scheduled(FileSourceDescriptor descriptor, ScheduledFuture<?> future) {
    }

    public SourceRegistry(BackendSourceClient backend, SourcePoller poller,
                          @Qualifier("sourceScheduler") TaskScheduler sourceScheduler,
                          @Qualifier("refreshScheduler") TaskScheduler refreshScheduler,
                          WorkerProperties properties, Clock clock) {
        this.backend = backend;
        this.poller = poller;
        this.sourceScheduler = sourceScheduler;
        this.refreshScheduler = refreshScheduler;
        this.properties = properties;
        this.clock = clock;
        this.startedAt = clock.instant();
    }

    /** Começa a pedir as fontes quando a aplicação está pronta. */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        refreshScheduler.schedule(this::refreshAndReschedule, clock.instant());
    }

    private void refreshAndReschedule() {
        if (stopped) {
            return;
        }
        boolean ok = refresh();
        Duration next = ok || lastBackendSuccess != null ? properties.sourcesRefreshInterval()
                : Duration.ofSeconds(STARTUP_RETRY_SECONDS);
        if (!stopped) {
            refreshScheduler.schedule(this::refreshAndReschedule, clock.instant().plus(next));
        }
    }

    /**
     * Pede as fontes ao backend e acerta os agendamentos.
     *
     * @return {@code false} se o backend não respondeu (os agendamentos ficam como estavam)
     */
    public synchronized boolean refresh() {
        FileSourceList list;
        try {
            list = backend.fetch();
        } catch (RuntimeException e) {
            log.warn("Backend did not return the file sources ({}): keeping the {} scheduled sources",
                    e.getClass().getSimpleName(), scheduled.size());
            return false;
        }
        lastBackendSuccess = clock.instant();
        Map<UUID, FileSourceDescriptor> current = new LinkedHashMap<>();
        for (FileSourceDescriptor d : list.sources()) {
            current.put(d.sourceId(), d);
        }
        for (Iterator<Map.Entry<UUID, Scheduled>> it = scheduled.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, Scheduled> entry = it.next();
            if (!current.containsKey(entry.getKey())) {
                entry.getValue().future().cancel(false);
                it.remove();
                log.info("Source {} (insurer {}) no longer active: unscheduled", entry.getKey(),
                        entry.getValue().descriptor().insurerId());
            }
        }
        for (FileSourceDescriptor d : current.values()) {
            Scheduled existing = scheduled.get(d.sourceId());
            if (existing != null && existing.descriptor().version() == d.version()) {
                continue;
            }
            if (existing != null) {
                existing.future().cancel(false);
            }
            ScheduledFuture<?> future = sourceScheduler.scheduleWithFixedDelay(() -> poller.pollWithLock(d),
                    clock.instant(), properties.pollInterval());
            scheduled.put(d.sourceId(), new Scheduled(d, future));
            log.info("Source {} (insurer {}, {}, entity {}, enabled {}, version {}) {}: read every {}",
                    d.sourceId(), d.insurerId(), d.fileFormat(), d.entity(), d.enabled(), d.version(),
                    existing == null ? "scheduled" : "rescheduled", properties.pollInterval());
        }
        return true;
    }

    /** Número de fontes agendadas. */
    public synchronized int size() {
        return scheduled.size();
    }

    /** O descritor agendado da fonte. */
    public synchronized Optional<FileSourceDescriptor> descriptor(UUID sourceId) {
        Scheduled s = scheduled.get(sourceId);
        return s == null ? Optional.empty() : Optional.of(s.descriptor());
    }

    /** Pára os pedidos e cancela todas as fontes (testes e encerramento). */
    public synchronized void stop() {
        stopped = true;
        scheduled.values().forEach(s -> s.future().cancel(false));
        scheduled.clear();
    }

    @Override
    public Health health() {
        Instant now = clock.instant();
        Instant reference = lastBackendSuccess == null ? startedAt : lastBackendSuccess;
        Health.Builder builder = Duration.between(reference, now).compareTo(BACKEND_DOWN_AFTER) > 0
                ? Health.down() : Health.up();
        builder.withDetail("scheduledSources", size());
        if (lastBackendSuccess != null) {
            builder.withDetail("lastBackendSuccess", lastBackendSuccess.toString());
        }
        return builder.build();
    }
}
