package mz.co.moseguros.shared.events;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Reenvia as publicações de eventos incompletas do Spring Modulith (S6-01 §2; modelo transversal §7): a
 * cada 5 minutos, as que estão por concluir há mais de 5 minutos (um listener que falhou, ou a aplicação
 * que parou a meio). Antes de reenviar, conta-as no <i>gauge</i> {@value #GAUGE} (Prometheus
 * {@code moseguros_events_incomplete}, alertas do S6-08).
 *
 * <p>Sem {@code @RunAsSystem}: só lê {@code public.event_publication}; os listeners reenviados tratam do
 * seu âmbito.
 */
@Component
public class IncompletePublicationsJob {

    public static final String GAUGE = "moseguros.events.incomplete";
    public static final String LOCK = "shared.incomplete-publications";
    static final Duration OLDER_THAN = Duration.ofMinutes(5);
    private static final String COUNT = """
            select count(*) from public.event_publication
             where completion_date is null and publication_date < ?""";
    private static final Logger log = LoggerFactory.getLogger(IncompletePublicationsJob.class);

    private final IncompleteEventPublications publications;
    private final JdbcTemplate jdbc;
    private final Clock clock;
    private final AtomicLong incomplete = new AtomicLong();

    public IncompletePublicationsJob(IncompleteEventPublications publications, JdbcTemplate jdbc, Clock clock,
                                     ObjectProvider<MeterRegistry> meters) {
        this.publications = publications;
        this.jdbc = jdbc;
        this.clock = clock;
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            Gauge.builder(GAUGE, incomplete, AtomicLong::get)
                    .description("Publicações de eventos incompletas há mais de 5 minutos")
                    .register(registry);
        }
    }

    /**
     * Entrada do agendador. O ShedLock só bloqueia métodos sem valor de retorno
     * ({@code LockingNotSupportedException} com {@code int}/{@code long}); os testes chamam {@link #resubmit()}.
     */
    @Scheduled(cron = "0 */5 * * * *", zone = "Africa/Maputo")
    @SchedulerLock(name = LOCK, lockAtMostFor = "PT4M")
    public void scheduled() {
        resubmit();
    }

    /** @return o número de publicações incompletas contadas antes do reenvio */
    public long resubmit() {
        Instant startedAt = clock.instant();
        log.info("Job started job={}", LOCK);
        long count = count(startedAt.minus(OLDER_THAN));
        incomplete.set(count);
        long failed = 0;
        try {
            publications.resubmitIncompletePublicationsOlderThan(OLDER_THAN);
        } catch (RuntimeException e) {
            failed = 1;
            log.error("Job failed job={} ({})", LOCK, e.getClass().getSimpleName(), e);
        }
        log.info("Job finished job={} processed={} failed={} durationMs={}", LOCK, count, failed,
                Duration.between(startedAt, clock.instant()).toMillis());
        return count;
    }

    /** Valor actual do <i>gauge</i>. */
    public long incomplete() {
        return incomplete.get();
    }

    private long count(Instant before) {
        Long n = jdbc.queryForObject(COUNT, Long.class, Timestamp.from(before));
        return n == null ? 0 : n;
    }
}
