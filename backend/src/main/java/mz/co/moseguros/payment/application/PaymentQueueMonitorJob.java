package mz.co.moseguros.payment.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.payment.infrastructure.amqp.PaymentQueueInspector;
import mz.co.moseguros.payment.infrastructure.amqp.PaymentQueueInspector.QueueStatus;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Vigia das filas dos pagamentos (S5-10 §5), a cada 60 s: gauge {@code moseguros.payment.queue.messages{queue}}
 * e, quando uma {@code .dlq} tem mais mensagens do que na leitura anterior, alerta {@code PAYMENT_DLQ_NOT_EMPTY}.
 * Broker inacessível → {@code WARN}, sem excepção. Não usa a BD.
 */
@Component
public class PaymentQueueMonitorJob {

    static final String ALERT = "PAYMENT_DLQ_NOT_EMPTY";
    private static final Logger log = LoggerFactory.getLogger(PaymentQueueMonitorJob.class);

    private final PaymentQueueInspector inspector;
    private final PaymentAlerts alerts;
    private final ObjectProvider<MeterRegistry> meters;
    private final Map<String, AtomicLong> gauges = new ConcurrentHashMap<>();
    private final Map<String, Long> lastDlqCounts = new ConcurrentHashMap<>();

    public PaymentQueueMonitorJob(PaymentQueueInspector inspector, PaymentAlerts alerts,
                                  ObjectProvider<MeterRegistry> meters) {
        this.inspector = inspector;
        this.alerts = alerts;
        this.meters = meters;
    }

    /** @return as filas lidas, ou vazio se o broker não estiver acessível */
    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    public Optional<List<QueueStatus>> monitor() {
        Optional<List<QueueStatus>> status = inspector.inspect();
        if (status.isEmpty()) {
            log.warn("Payment queues not monitored: RabbitMQ unreachable");
            return status;
        }
        for (QueueStatus q : status.get()) {
            if (q.messages() < 0) {
                continue;
            }
            gauge(q.name()).set(q.messages());
            if (q.name().endsWith(PaymentsTopology.DLQ_SUFFIX)) {
                Long previous = lastDlqCounts.put(q.name(), q.messages());
                if (q.messages() > (previous == null ? 0 : previous)) {
                    alerts.raise(ALERT, null, Map.of("queue", q.name(), "messages", Long.toString(q.messages())));
                }
            }
        }
        return status;
    }

    private AtomicLong gauge(String queue) {
        return gauges.computeIfAbsent(queue, name -> {
            AtomicLong value = new AtomicLong();
            MeterRegistry registry = meters.getIfAvailable();
            if (registry != null) {
                Gauge.builder("moseguros.payment.queue.messages", value, AtomicLong::get)
                        .tag("queue", name)
                        .register(registry);
            }
            return value;
        });
    }
}
