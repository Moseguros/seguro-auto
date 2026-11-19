package mz.co.moseguros.paymentcontracts;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Topologia RabbitMQ dos pagamentos por referência (ADR-004 e adendas, S5-06 §3).
 *
 * <pre>
 * payments.events (topic, durável)
 *   reference.paid.*            → core.reference-payments    (quorum, DLX payments.dlx)
 *   reference.file-processed.*  → core.payment-file-events   (quorum, DLX payments.dlx)
 *   reference.file-rejected.*   → core.payment-file-events
 *   reference.source-polled.*   → core.payment-file-events
 * payments.dlx (direct, durável)
 *   core.reference-payments     → core.reference-payments.dlq   (quorum)
 *   core.payment-file-events    → core.payment-file-events.dlq  (quorum)
 * </pre>
 *
 * <p>As ligações são explícitas: num exchange {@code topic} o {@code *} substitui uma palavra
 * inteira entre pontos, por isso {@code reference.file-*} não seria um curinga.
 *
 * <p>O backend declara tudo; o worker declara só {@link #EVENTS_EXCHANGE} (com os mesmos
 * argumentos) e publica com {@code mandatory = true}. As chaves terminam no {@code insurerId}.
 */
public final class PaymentsTopology {

    public static final String EVENTS_EXCHANGE = "payments.events";
    public static final String DEAD_LETTER_EXCHANGE = "payments.dlx";

    public static final String REFERENCE_PAYMENTS_QUEUE = "core.reference-payments";
    public static final String PAYMENT_FILE_EVENTS_QUEUE = "core.payment-file-events";
    public static final String DLQ_SUFFIX = ".dlq";
    public static final String REFERENCE_PAYMENTS_DLQ = REFERENCE_PAYMENTS_QUEUE + DLQ_SUFFIX;
    public static final String PAYMENT_FILE_EVENTS_DLQ = PAYMENT_FILE_EVENTS_QUEUE + DLQ_SUFFIX;

    public static final String REFERENCE_PAID_PREFIX = "reference.paid.";
    public static final String FILE_PROCESSED_PREFIX = "reference.file-processed.";
    public static final String FILE_REJECTED_PREFIX = "reference.file-rejected.";
    public static final String SOURCE_POLLED_PREFIX = "reference.source-polled.";

    public static final String REFERENCE_PAID_BINDING = REFERENCE_PAID_PREFIX + "*";
    public static final String FILE_PROCESSED_BINDING = FILE_PROCESSED_PREFIX + "*";
    public static final String FILE_REJECTED_BINDING = FILE_REJECTED_PREFIX + "*";
    public static final String SOURCE_POLLED_BINDING = SOURCE_POLLED_PREFIX + "*";

    /** Ligações de {@link #PAYMENT_FILE_EVENTS_QUEUE} a {@link #EVENTS_EXCHANGE}. */
    public static final List<String> PAYMENT_FILE_EVENTS_BINDINGS =
            List.of(FILE_PROCESSED_BINDING, FILE_REJECTED_BINDING, SOURCE_POLLED_BINDING);

    public static final String ARG_QUEUE_TYPE = "x-queue-type";
    public static final String QUEUE_TYPE_QUORUM = "quorum";
    public static final String ARG_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String ARG_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String ARG_DELIVERY_LIMIT = "x-delivery-limit";
    /** Entregas de uma mensagem antes de ir para a {@code .dlq} (explícito; é também o defeito da 4.x). */
    public static final int DELIVERY_LIMIT = 20;

    private PaymentsTopology() {}

    /** Argumentos de uma fila principal: quorum, dead-letter para {@code payments.dlx} com o nome da fila. */
    public static Map<String, Object> mainQueueArguments(String queue) {
        Objects.requireNonNull(queue, "queue");
        return Map.of(
                ARG_QUEUE_TYPE, QUEUE_TYPE_QUORUM,
                ARG_DEAD_LETTER_EXCHANGE, DEAD_LETTER_EXCHANGE,
                ARG_DEAD_LETTER_ROUTING_KEY, queue,
                ARG_DELIVERY_LIMIT, DELIVERY_LIMIT);
    }

    /** Argumentos de uma fila de mensagens mortas: só quorum. */
    public static Map<String, Object> deadLetterQueueArguments() {
        return Map.of(ARG_QUEUE_TYPE, QUEUE_TYPE_QUORUM);
    }

    /** Chave de {@link ReferencePaymentReceived}: {@code reference.paid.{insurerId}}. */
    public static String referencePaidKey(UUID insurerId) {
        return REFERENCE_PAID_PREFIX + Objects.requireNonNull(insurerId, "insurerId");
    }

    /** Chave de {@link PaymentFileProcessed}: {@code reference.file-processed.{insurerId}}. */
    public static String fileProcessedKey(UUID insurerId) {
        return FILE_PROCESSED_PREFIX + Objects.requireNonNull(insurerId, "insurerId");
    }

    /** Chave de {@link PaymentFileRejected}: {@code reference.file-rejected.{insurerId}}. */
    public static String fileRejectedKey(UUID insurerId) {
        return FILE_REJECTED_PREFIX + Objects.requireNonNull(insurerId, "insurerId");
    }

    /** Chave de {@link PaymentSourcePolled}: {@code reference.source-polled.{insurerId}}. */
    public static String sourcePolledKey(UUID insurerId) {
        return SOURCE_POLLED_PREFIX + Objects.requireNonNull(insurerId, "insurerId");
    }
}
