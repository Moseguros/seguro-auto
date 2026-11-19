package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.FileSourceHealth;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Alerta de fonte de ficheiros parada (S5-10 §5), de 5 em 5 minutos: para cada {@code BANK_REFERENCE} activo,
 * se a última leitura com sucesso (ou a activação, se nunca houve) tiver mais de
 * {@code moseguros.payment.file-source.stale-after} (defeito 15 min), {@code stale_since} fica preenchido e há
 * um alerta {@code PAYMENT_SOURCE_STALE} (uma vez); quando a fonte volta a ler, {@code stale_since} é limpo.
 *
 * <p>Mede a <b>ligação</b> ao SFTP, não a chegada de ficheiros: a frequência dos ficheiros é combinada entre a
 * seguradora e o banco, e um dia sem ficheiros não é um erro.
 */
@Component
public class PaymentSourceHealthJob {

    static final String ALERT = "PAYMENT_SOURCE_STALE";
    private static final Logger log = LoggerFactory.getLogger(PaymentSourceHealthJob.class);

    private final InsurerPaymentMethodRepository methods;
    private final PaymentAlerts alerts;
    private final PaymentProperties properties;
    private final TransactionTemplate tx;
    private final Clock clock;

    public PaymentSourceHealthJob(InsurerPaymentMethodRepository methods, PaymentAlerts alerts,
                                  PaymentProperties properties, PlatformTransactionManager transactionManager,
                                  Clock clock) {
        this.methods = methods;
        this.alerts = alerts;
        this.properties = properties;
        this.tx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /** Resultado de uma execução. */
    public record Result(int checked, int becameStale, int recovered) {
    }

    @RunAsSystem
    @Scheduled(fixedDelay = 300_000, initialDelay = 120_000)
    public Result check() {
        Instant now = clock.instant();
        Duration staleAfter = properties.getFileSource().getStaleAfter();
        Result result = tx.execute(status -> {
            int stale = 0;
            int recovered = 0;
            var sources = methods.enabledSourcesHealth(PaymentMethod.BANK_REFERENCE);
            for (FileSourceHealth s : sources) {
                Instant lastGood = s.lastGoodAt();
                if (lastGood == null) {
                    continue;
                }
                boolean isStale = Duration.between(lastGood, now).compareTo(staleAfter) > 0;
                if (isStale && s.staleSince() == null) {
                    if (methods.markStale(s.id(), now) == 1) {
                        stale++;
                        alerts.raise(ALERT, s.insurerId(), Map.of("sourceId", s.id().toString(),
                                "lastSuccessAt", String.valueOf(s.lastPollSuccessAt())));
                    }
                } else if (!isStale && s.staleSince() != null) {
                    if (methods.clearStale(s.id()) == 1) {
                        recovered++;
                        log.info("Payment file source recovered insurerId={} sourceId={} staleSince={}",
                                s.insurerId(), s.id(), s.staleSince());
                    }
                }
            }
            return new Result(sources.size(), stale, recovered);
        });
        log.info("Payment file source health checked sources={} stale={} recovered={}", result.checked(),
                result.becameStale(), result.recovered());
        return result;
    }
}
