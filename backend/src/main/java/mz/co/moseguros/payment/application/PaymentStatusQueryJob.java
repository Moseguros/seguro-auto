package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.GatewayRegistry;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
import mz.co.moseguros.payment.application.gateway.StatusResult;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Consultas de estado das intenções {@code UNKNOWN} (S5-04 §4), a cada 5 s; genérico
 * ({@code PaymentGateway.queryStatus}; hoje só o M-Pesa fica {@code UNKNOWN}).
 *
 * <p>Até {@value #BATCH_SIZE} intenções com {@code next_query_at <= agora}, escolhidas com
 * {@code for update skip locked} e reservadas por {@value #LEASE_SECONDS} s (outra instância não as
 * apanha; o ShedLock só chega no S6). A consulta HTTP corre fora de transacção; o resultado aplica-se com
 * o {@link IntentStateService#applyStatusQuery} (tentativa, {@code STATUS_QUERY}, estado ou próxima
 * consulta pelo calendário {@code moseguros.payment.status-query}, e {@code UNRESOLVED_STATUS} no fim).
 *
 * <p><b>A consulta nunca chama o C2B.</b>
 */
@Component
public class PaymentStatusQueryJob {

    static final String ACTOR = "PaymentStatusQueryJob";
    static final int BATCH_SIZE = 20;
    static final int LEASE_SECONDS = 120;
    private static final Logger log = LoggerFactory.getLogger(PaymentStatusQueryJob.class);

    private final PaymentIntentRepository intents;
    private final IntentStateService states;
    private final GatewayRegistry gateways;
    private final PaymentSystemLookups lookups;
    private final TransactionTemplate tx;
    private final Clock clock;

    public PaymentStatusQueryJob(PaymentIntentRepository intents, IntentStateService states,
                                 GatewayRegistry gateways, PaymentSystemLookups lookups,
                                 PlatformTransactionManager transactionManager, Clock clock) {
        this.intents = intents;
        this.states = states;
        this.gateways = gateways;
        this.lookups = lookups;
        this.tx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /** @return quantas intenções consultou */
    @RunAsSystem
    @Scheduled(fixedDelay = 5_000, initialDelay = 5_000)
    public int query() {
        Instant startedAt = clock.instant();
        log.info("Payment status query started at={}", startedAt);
        List<UUID> due = tx.execute(status -> {
            List<UUID> ids = new ArrayList<>(intents.lockQueryDue(startedAt, BATCH_SIZE));
            if (!ids.isEmpty()) {
                intents.deferQuery(ids, startedAt.plusSeconds(LEASE_SECONDS));
            }
            return ids;
        });
        int queried = 0;
        int resolved = 0;
        for (UUID id : due == null ? List.<UUID>of() : due) {
            try {
                PaymentStatus after = queryOne(id);
                queried++;
                if (after != PaymentStatus.UNKNOWN) {
                    resolved++;
                }
            } catch (RuntimeException e) {
                log.warn("Payment status query failed intentId={} ({})", id, e.getClass().getSimpleName());
            }
        }
        log.info("Payment status query finished due={} queried={} resolved={} durationMs={}",
                due == null ? 0 : due.size(), queried, resolved,
                Duration.between(startedAt, clock.instant()).toMillis());
        return queried;
    }

    private PaymentStatus queryOne(UUID id) {
        PaymentIntent intent = tx.execute(status -> intents.findById(id).orElseThrow());
        if (intent.getStatus() != PaymentStatus.UNKNOWN) {
            return intent.getStatus();
        }
        PaymentGateway gateway = gateways.find(intent.getMethod()).orElse(null);
        MethodConfig config = lookups.configById(intent.getMethodConfigId()).orElse(null);
        StatusResult result;
        if (gateway == null || config == null) {
            result = StatusResult.stillUnknown(JsonNodeFactory.instance.objectNode()
                    .put("error", "METHOD_NOT_CONFIGURED"));
        } else {
            try {
                result = gateway.queryStatus(intent, config);
            } catch (RuntimeException e) {
                result = StatusResult.stillUnknown(JsonNodeFactory.instance.objectNode()
                        .put("error", e.getClass().getSimpleName()));
            }
        }
        StatusResult applied = result;
        PaymentIntent after = tx.execute(status -> states.applyStatusQuery(id, applied, ACTOR));
        return after == null ? PaymentStatus.UNKNOWN : after.getStatus();
    }
}
