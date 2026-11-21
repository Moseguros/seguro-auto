package mz.co.moseguros.rating.application;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Aviso de simulação a expirar (S6-04 §9.1, decisão D16): todos os dias às 09:00 ({@code Africa/Maputo}, lock
 * {@value #LOCK}) avisa, uma vez por pedido ({@code expiry_notified_at}), os pedidos de cotação do próprio
 * cliente ({@code origin = CUSTOMER}) sem nenhuma cotação {@code USED} e com pelo menos uma cotação
 * {@code OFFERED} efectiva (validade futura e data de início ≥ hoje) cuja validade acaba daqui a
 * {@code moseguros.rating.quote-expiry-notice-days} dias (data de Maputo). Não há evento {@code QuoteExpiring}:
 * o {@code rating} chama {@code NotificationApi.notify} directamente. Uma falha num pedido não pára os outros.
 */
@Component
public class QuoteExpiryNotifierJob {

    public static final String LOCK = "rating.quote-expiry-notifier";
    static final String TEMPLATE = "QUOTE_EXPIRING";
    static final int BATCH = 200;
    private static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");
    private static final Logger log = LoggerFactory.getLogger(QuoteExpiryNotifierJob.class);

    /** Pedidos a avisar (por id, em lotes), com o menor total e a maior validade das cotações efectivas. */
    private static final String CANDIDATES = """
            select r.id, r.customer_user_id, min(q.total_amount), max(q.valid_until)
              from rating.quote_request r
              join rating.quote q on q.quote_request_id = r.id
             where r.origin = 'CUSTOMER'
               and r.expiry_notified_at is null
               and r.start_date >= :today
               and r.id > :after
               and q.status = 'OFFERED'
               and q.valid_until > :now
               and not exists (select 1 from rating.quote u where u.quote_request_id = r.id and u.status = 'USED')
             group by r.id, r.customer_user_id
            having bool_or(cast(q.valid_until at time zone 'Africa/Maputo' as date) = :target)
             order by r.id
             limit :limit""";

    private final EntityManager entityManager;
    private final NotificationApi notifications;
    private final RatingProperties properties;
    private final TransactionTemplate tx;
    private final Clock clock;

    public QuoteExpiryNotifierJob(EntityManager entityManager, NotificationApi notifications,
                                  RatingProperties properties, PlatformTransactionManager transactionManager,
                                  Clock clock) {
        this.entityManager = entityManager;
        this.notifications = notifications;
        this.properties = properties;
        this.tx = new TransactionTemplate(transactionManager);
        this.tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.clock = clock;
    }

    /**
     * Entrada do agendador. O ShedLock só bloqueia métodos sem valor de retorno
     * ({@code LockingNotSupportedException} com {@code int}/{@code long}); os testes chamam {@link #run()}.
     */
    @Scheduled(cron = "0 0 9 * * *", zone = "Africa/Maputo")
    @SchedulerLock(name = LOCK, lockAtMostFor = "PT15M")
    @RunAsSystem
    public void scheduled() {
        run();
    }

    /** @return o número de pedidos avisados */
    @RunAsSystem
    public int run() {
        Instant startedAt = clock.instant();
        LocalDate today = LocalDate.ofInstant(startedAt, MAPUTO);
        LocalDate target = today.plusDays(properties.getQuoteExpiryNoticeDays());
        log.info("Job started job={} target={}", LOCK, target);
        int processed = 0;
        int failed = 0;
        UUID after = new UUID(0L, 0L); // o menor uuid na ordem do PostgreSQL
        while (true) {
            List<Candidate> batch = candidates(startedAt, today, target, after);
            if (batch.isEmpty()) {
                break;
            }
            for (Candidate candidate : batch) {
                try {
                    if (Boolean.TRUE.equals(tx.execute(status -> notify(candidate)))) {
                        processed++;
                    }
                } catch (RuntimeException e) {
                    failed++;
                    log.error("Job job={} failed for requestId={} ({})", LOCK, candidate.requestId(),
                            e.getClass().getSimpleName(), e);
                }
                after = candidate.requestId();
            }
            if (batch.size() < BATCH) {
                break;
            }
        }
        log.info("Job finished job={} processed={} failed={} durationMs={}", LOCK, processed, failed,
                Duration.between(startedAt, clock.instant()).toMillis());
        return processed;
    }

    private List<Candidate> candidates(Instant now, LocalDate today, LocalDate target, UUID after) {
        List<?> rows = tx.execute(status -> entityManager.createNativeQuery(CANDIDATES)
                .setParameter("today", today)
                .setParameter("after", after)
                .setParameter("now", Timestamp.from(now))
                .setParameter("target", target)
                .setParameter("limit", BATCH)
                .getResultList());
        return rows == null ? List.of() : rows.stream().map(r -> {
            Object[] row = (Object[]) r;
            return new Candidate((UUID) row[0], (UUID) row[1], ((Number) row[2]).longValue(), toInstant(row[3]));
        }).toList();
    }

    /** Marca o pedido e notifica, na mesma transacção; {@code false} se outro já o marcou. */
    private boolean notify(Candidate c) {
        int updated = entityManager.createNativeQuery("""
                        update rating.quote_request set expiry_notified_at = :now
                         where id = :id and expiry_notified_at is null""")
                .setParameter("now", Timestamp.from(clock.instant()))
                .setParameter("id", c.requestId())
                .executeUpdate();
        if (updated == 0) {
            return false;
        }
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("amount", Money.ofCentavos(c.minTotal()));
        params.put("validUntil", c.validUntil());
        params.put("webPath", "/cliente/simular/" + c.requestId());
        params.put("deepLink", "moseguros://quote/" + c.requestId());
        notifications.notify(c.customerUserId(), TEMPLATE, params, NotificationCategory.QUOTE);
        return true;
    }

    private static Instant toInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (value instanceof java.time.OffsetDateTime odt) {
            return odt.toInstant();
        }
        throw new IllegalStateException("Unexpected timestamp type " + value.getClass());
    }

    private record Candidate(UUID requestId, UUID customerUserId, long minTotal, Instant validUntil) {
    }
}
