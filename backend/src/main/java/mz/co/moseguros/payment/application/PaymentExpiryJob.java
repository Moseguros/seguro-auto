package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Expiração das intenções (S5-02 §8), a cada 60 s: referências em {@code CREATED}/{@code PENDING} e
 * M-Pesa em {@code CREATED} (nunca enviadas) com {@code expires_at} passado passam a {@code EXPIRED}
 * (a referência também), com {@code PaymentFailed}. M-Pesa em {@code PENDING}/{@code UNKNOWN} não
 * expira aqui (S5-04).
 *
 * <p>Até {@value #BATCH_SIZE} por ciclo, numa transacção, com {@code for update skip locked} (duas
 * instâncias não tratam a mesma; o ShedLock só chega no S6).
 */
@Component
public class PaymentExpiryJob {

    static final int BATCH_SIZE = 100;
    static final String ACTOR = "PaymentExpiryJob";
    private static final Logger log = LoggerFactory.getLogger(PaymentExpiryJob.class);

    private final PaymentIntentRepository intents;
    private final IntentStateService states;
    private final TransactionTemplate tx;
    private final Clock clock;

    public PaymentExpiryJob(PaymentIntentRepository intents, IntentStateService states,
                            PlatformTransactionManager transactionManager, Clock clock) {
        this.intents = intents;
        this.states = states;
        this.tx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /** @return quantas intenções expirou */
    @RunAsSystem
    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    public int expire() {
        Instant startedAt = clock.instant();
        log.info("Payment intent expiry started at={}", startedAt);
        Integer expired = tx.execute(status -> {
            List<UUID> ids = intents.lockExpired(startedAt, BATCH_SIZE);
            ids.forEach(id -> states.expire(id, ACTOR));
            return ids.size();
        });
        int count = expired == null ? 0 : expired;
        log.info("Payment intent expiry finished expired={} durationMs={}", count,
                Duration.between(startedAt, clock.instant()).toMillis());
        return count;
    }
}
