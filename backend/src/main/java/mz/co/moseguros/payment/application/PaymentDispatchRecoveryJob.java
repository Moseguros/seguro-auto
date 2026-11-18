package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.AsyncDispatcher;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaProperties;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Recuperação dos envios M-Pesa perdidos (S5-04 §4), a cada 15 s, com {@code for update skip locked}:
 * <ul>
 *   <li>{@code CREATED} criadas há mais de {@value #CREATED_GRACE_SECONDS} s e ainda dentro do prazo
 *       (executor cheio, reinício antes do envio) → voltam a ser submetidas ao {@link AsyncDispatcher}; o
 *       compare-and-set do {@code MpesaC2bJob} impede o envio duplo;</li>
 *   <li>{@code PENDING} com {@code dispatched_at} anterior a agora − (prazo do C2B +
 *       {@value #PENDING_GRACE_SECONDS} s) (a thread perdeu-se, ex.: reinício) → {@code UNKNOWN}, e daí a
 *       consulta de estado. <b>Nunca</b> se volta a enviar o C2B.</li>
 * </ul>
 */
@Component
public class PaymentDispatchRecoveryJob {

    static final String ACTOR = "PaymentDispatchRecoveryJob";
    static final int CREATED_GRACE_SECONDS = 15;
    static final int PENDING_GRACE_SECONDS = 60;
    static final int BATCH_SIZE = 50;
    /** O menor prazo de C2B que o schema aceita ({@code c2bTimeoutSeconds ≥ 30}). */
    private static final int MIN_C2B_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_C2B_TIMEOUT_SECONDS = 90;
    private static final Logger log = LoggerFactory.getLogger(PaymentDispatchRecoveryJob.class);

    private final PaymentIntentRepository intents;
    private final IntentStateService states;
    private final PaymentSystemLookups lookups;
    private final AsyncDispatcher dispatcher;
    private final MpesaProperties mpesa;
    private final TransactionTemplate tx;
    private final Clock clock;

    public PaymentDispatchRecoveryJob(PaymentIntentRepository intents, IntentStateService states,
                                      PaymentSystemLookups lookups, AsyncDispatcher dispatcher,
                                      MpesaProperties mpesa, PlatformTransactionManager transactionManager,
                                      Clock clock) {
        this.intents = intents;
        this.states = states;
        this.lookups = lookups;
        this.dispatcher = dispatcher;
        this.mpesa = mpesa;
        this.tx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /** Contagens de um ciclo. */
    public record Outcome(int resubmitted, int rejected, int markedUnknown) {
    }

    @RunAsSystem
    @Scheduled(fixedDelay = 15_000, initialDelay = 15_000)
    public Outcome recover() {
        Instant startedAt = clock.instant();
        log.info("Payment dispatch recovery started at={}", startedAt);

        List<UUID> undispatched = tx.execute(status -> new ArrayList<>(intents.lockUndispatchedMpesa(
                startedAt.minusSeconds(CREATED_GRACE_SECONDS), startedAt, BATCH_SIZE)));
        int resubmitted = 0;
        int rejected = 0;
        for (UUID id : undispatched == null ? List.<UUID>of() : undispatched) {
            if (dispatcher.submit(id)) {
                resubmitted++;
            } else {
                rejected++;
            }
        }

        Integer unknown = tx.execute(status -> {
            Map<UUID, Integer> timeouts = new HashMap<>();
            Instant now = clock.instant();
            int count = 0;
            Instant earliest = now.minus(mpesa.c2bReadTimeout(MIN_C2B_TIMEOUT_SECONDS))
                    .minusSeconds(PENDING_GRACE_SECONDS);
            for (UUID id : intents.lockStalePendingMpesa(earliest, BATCH_SIZE)) {
                PaymentIntent intent = intents.findById(id).orElseThrow();
                int timeout = timeouts.computeIfAbsent(intent.getMethodConfigId(), this::c2bTimeoutSeconds);
                Instant limit = intent.getDispatchedAt().plus(mpesa.c2bReadTimeout(timeout))
                        .plusSeconds(PENDING_GRACE_SECONDS);
                if (now.isAfter(limit)) {
                    log.warn("M-Pesa dispatch lost, status unknown intentId={} reference={} dispatchedAt={}",
                            intent.getId(), intent.getReference(), intent.getDispatchedAt());
                    states.markUnknown(id, ACTOR);
                    count++;
                }
            }
            return count;
        });
        Outcome outcome = new Outcome(resubmitted, rejected, unknown == null ? 0 : unknown);
        log.info("Payment dispatch recovery finished resubmitted={} rejected={} markedUnknown={} durationMs={}",
                outcome.resubmitted(), outcome.rejected(), outcome.markedUnknown(),
                Duration.between(startedAt, clock.instant()).toMillis());
        return outcome;
    }

    private int c2bTimeoutSeconds(UUID configId) {
        return lookups.configById(configId).map(MethodConfig::settings)
                .map(s -> s.path("c2bTimeoutSeconds"))
                .filter(JsonNode::canConvertToInt)
                .map(JsonNode::asInt)
                .filter(n -> n > 0)
                .orElse(DEFAULT_C2B_TIMEOUT_SECONDS);
    }
}
