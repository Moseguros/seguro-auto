package mz.co.moseguros.payment.infrastructure.amqp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Mensagens e consumidores das quatro filas dos pagamentos (S5-06), lidos com
 * {@link AmqpAdmin#getQueueProperties}, para o {@code PaymentQueueMonitorJob} e o estado do worker
 * ({@code getPaymentWorkerStatus}, S5-10 §5, §8).
 */
@Component
public class PaymentQueueInspector {

    /** As quatro filas, por esta ordem. */
    public static final List<String> QUEUES = List.of(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE,
            PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE, PaymentsTopology.REFERENCE_PAYMENTS_DLQ,
            PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ);

    private static final Logger log = LoggerFactory.getLogger(PaymentQueueInspector.class);

    private final ObjectProvider<AmqpAdmin> admin;

    public PaymentQueueInspector(ObjectProvider<AmqpAdmin> admin) {
        this.admin = admin;
    }

    /** Estado de uma fila; {@code -1} se a fila não existir. */
    public record QueueStatus(String name, long messages, long consumers) {
    }

    /** As quatro filas, ou vazio se o broker não estiver acessível. Nunca lança. */
    public Optional<List<QueueStatus>> inspect() {
        AmqpAdmin amqp = admin.getIfAvailable();
        if (amqp == null) {
            return Optional.empty();
        }
        List<QueueStatus> result = new ArrayList<>();
        try {
            for (String queue : QUEUES) {
                Properties p = amqp.getQueueProperties(queue);
                if (p == null) {
                    result.add(new QueueStatus(queue, -1, -1));
                } else {
                    result.add(new QueueStatus(queue, number(p.get(RabbitAdmin.QUEUE_MESSAGE_COUNT)),
                            number(p.get(RabbitAdmin.QUEUE_CONSUMER_COUNT))));
                }
            }
        } catch (RuntimeException e) {
            log.warn("RabbitMQ unreachable while reading the payment queues: {}", e.getClass().getSimpleName());
            return Optional.empty();
        }
        return Optional.of(List.copyOf(result));
    }

    private static long number(Object value) {
        return value instanceof Number n ? n.longValue() : -1;
    }
}
