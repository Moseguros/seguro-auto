package mz.co.moseguros.payment;

import static org.awaitility.Awaitility.await;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentcontracts.MoneyAmount;
import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes do consumo (S5-10): publica no {@code payments.events} as mensagens do {@code payment-contracts}
 * com as propriedades que o worker põe (S5-06: {@code message-id}, {@code type}, {@code schemaVersion},
 * persistente), espera pelo seu tratamento ({@code inbound_message}) e procura mensagens nas {@code .dlq}.
 */
public final class PaymentMessagesSupport {

    public static final Duration WAIT = Duration.ofSeconds(20);

    private final RabbitTemplate rabbit;
    private final ObjectMapper mapper;
    private final JdbcTemplate owner;
    private final Map<String, Message> received = new java.util.concurrent.ConcurrentHashMap<>();

    public PaymentMessagesSupport(RabbitTemplate rabbit, ObjectMapper mapper, JdbcTemplate owner) {
        this.rabbit = rabbit;
        this.mapper = mapper;
        this.owner = owner;
    }

    /** Ficheiro de teste: o SHA-256 de um texto aleatório. */
    public static String newFileSha() {
        return MessageIds.sha256Hex(UUID.randomUUID().toString());
    }

    /** Um registo de um ficheiro MEPS, como o worker o publica. */
    public static ReferencePaymentReceived payment(UUID insurerId, UUID sourceId, String fileSha, int lineNo,
                                                   String entity, String reference, long amount, Instant paidAt) {
        return new ReferencePaymentReceived(MessageIds.referencePaymentReceived(sourceId, fileSha, lineNo), 1,
                insurerId, sourceId, "MEPS", entity, reference, MoneyAmount.mzn(amount), MoneyAmount.mzn(1_500),
                paidAt, "01", null, "MEPS_" + entity + "_test.txt", fileSha, "202701151", lineNo);
    }

    public static PaymentFileProcessed processed(UUID insurerId, UUID sourceId, String fileSha, int records,
                                                 long total, Instant at) {
        return new PaymentFileProcessed(MessageIds.paymentFileProcessed(sourceId, fileSha), 1, insurerId, sourceId,
                "MEPS", "MEPS_test_" + fileSha.substring(0, 8) + ".txt", fileSha, "202701151", records,
                MoneyAmount.mzn(total), MoneyAmount.mzn(3_000), at);
    }

    public static PaymentFileRejected rejected(UUID insurerId, UUID sourceId, String fileSha, Instant at) {
        return new PaymentFileRejected(MessageIds.paymentFileRejected(sourceId, fileSha, at), 1, insurerId, sourceId,
                "MEPS", "MEPS_bad_" + fileSha.substring(0, 8) + ".txt", fileSha, "202701152",
                mz.co.moseguros.paymentcontracts.FileRejectionReason.TRAILER_TOTAL_MISMATCH, "Total do rodapé errado",
                null, at);
    }

    public static PaymentSourcePolled polled(UUID insurerId, UUID sourceId, Instant startedAt, String outcome,
                                             mz.co.moseguros.paymentcontracts.PollErrorCode error) {
        return new PaymentSourcePolled(MessageIds.paymentSourcePolled(), 1, insurerId, sourceId, startedAt,
                startedAt.plusSeconds(2), outcome, error, 1, PaymentSourcePolled.OUTCOME_OK.equals(outcome) ? 1 : 0,
                0);
    }

    // --- Publicação ---

    public void publish(ReferencePaymentReceived m) {
        send(PaymentsTopology.referencePaidKey(m.insurerId()), m.messageId(), MessageHeaders.typeOf(m.getClass()),
                json(m));
    }

    public void publish(PaymentFileProcessed m) {
        send(PaymentsTopology.fileProcessedKey(m.insurerId()), m.messageId(), MessageHeaders.typeOf(m.getClass()),
                json(m));
    }

    public void publish(PaymentFileRejected m) {
        send(PaymentsTopology.fileRejectedKey(m.insurerId()), m.messageId(), MessageHeaders.typeOf(m.getClass()),
                json(m));
    }

    public void publish(PaymentSourcePolled m) {
        send(PaymentsTopology.sourcePolledKey(m.insurerId()), m.messageId(), MessageHeaders.typeOf(m.getClass()),
                json(m));
    }

    /** Publica um corpo qualquer (mensagens inválidas). */
    public void send(String routingKey, String messageId, String type, byte[] body) {
        Message message = MessageBuilder.withBody(body)
                .setMessageId(messageId)
                .setContentType(MessageHeaders.CONTENT_TYPE)
                .setType(type)
                .setHeader(MessageHeaders.SCHEMA_VERSION_HEADER, MessageHeaders.SCHEMA_VERSION)
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        rabbit.send(PaymentsTopology.EVENTS_EXCHANGE, routingKey, message);
    }

    public byte[] json(Object m) {
        try {
            return mapper.writeValueAsBytes(m);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] text(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    // --- Espera ---

    /**
     * A linha de {@code inbound_message} da mensagem, quando o tratamento terminou: a linha e o resultado final
     * são gravados na mesma transacção, por isso a linha só se vê depois do commit.
     */
    public Map<String, Object> awaitInbound(String messageId) {
        await().atMost(WAIT).until(() -> !owner.queryForList(
                "select 1 from payment.inbound_message where message_id = ?", messageId).isEmpty());
        return owner.queryForMap("select * from payment.inbound_message where message_id = ?", messageId);
    }

    /** A excepção criada pela mensagem. */
    public Map<String, Object> exceptionOf(String messageId) {
        return owner.queryForMap("select * from payment.payment_exception where message_id = ?", messageId);
    }

    /**
     * A mensagem com este {@code message-id} na fila indicada, ou {@code null}. As outras que encontrar ficam
     * guardadas para pedidos seguintes (os testes procuram várias na mesma {@code .dlq}).
     */
    public Message receiveById(String queue, String messageId, Duration timeout) {
        Message buffered = received.remove(queue + "|" + messageId);
        if (buffered != null) {
            return buffered;
        }
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            Message m = rabbit.receive(queue, 500);
            if (m == null) {
                continue;
            }
            String id = m.getMessageProperties().getMessageId();
            if (messageId.equals(id)) {
                return m;
            }
            received.put(queue + "|" + id, m);
        }
        return null;
    }
}
