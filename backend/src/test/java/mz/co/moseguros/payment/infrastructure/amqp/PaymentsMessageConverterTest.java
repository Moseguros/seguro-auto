package mz.co.moseguros.payment.infrastructure.amqp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentcontracts.MoneyAmount;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

/** Validação das mensagens do worker antes do consumo (S5-10 §2). */
class PaymentsMessageConverterTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final PaymentsMessageConverter converter = new PaymentsMessageConverter(mapper);
    private final UUID insurer = UUID.randomUUID();

    private ReferencePaymentReceived payment() {
        UUID source = UUID.randomUUID();
        String sha = MessageIds.sha256Hex("file");
        return new ReferencePaymentReceived(MessageIds.referencePaymentReceived(source, sha, 2), 1, insurer, source,
                "MEPS", "90002", "00001240106", MoneyAmount.mzn(299_900), null,
                Instant.parse("2027-01-15T09:12:44Z"), null, null, "MEPS_90002.txt", sha, "202701151", 2);
    }

    /** A mensagem como o cliente AMQP a entrega ao consumidor. */
    private Message delivered(byte[] body, String type, Object schemaVersion, String routingKey, String queue,
                              Map<String, Object> extraHeaders) {
        Map<String, Object> headers = new HashMap<>(extraHeaders);
        if (schemaVersion != null) {
            headers.put(MessageHeaders.SCHEMA_VERSION_HEADER, schemaVersion);
        }
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .messageId("m-1").type(type).contentType(MessageHeaders.CONTENT_TYPE).headers(headers).build();
        MessageProperties mp = new DefaultMessagePropertiesConverter().toMessageProperties(props,
                new Envelope(1, false, PaymentsTopology.EVENTS_EXCHANGE, routingKey), "UTF-8");
        mp.setConsumerQueue(queue);
        return new Message(body, mp);
    }

    private Message valid(String routingKey, Map<String, Object> headers) throws Exception {
        return delivered(mapper.writeValueAsBytes(payment()), "ReferencePaymentReceived", 1, routingKey,
                PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, headers);
    }

    @Test
    void aValidMessageBecomesTheRecord() throws Exception {
        Object payload = converter.fromMessage(valid(PaymentsTopology.referencePaidKey(insurer), Map.of()));
        assertThat(payload).isInstanceOf(ReferencePaymentReceived.class);
        assertThat(((ReferencePaymentReceived) payload).amount().amount()).isEqualTo(299_900);
    }

    @Test
    void aMessageReplayedFromTheDlqUsesTheOriginalRoutingKey() throws Exception {
        Map<String, Object> death = new HashMap<>();
        death.put("queue", PaymentsTopology.REFERENCE_PAYMENTS_QUEUE);
        death.put("reason", "rejected");
        death.put("count", 1L);
        death.put("exchange", PaymentsTopology.EVENTS_EXCHANGE);
        death.put("routing-keys", List.of(PaymentsTopology.referencePaidKey(insurer)));
        Message replayed = valid(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, Map.of("x-death", List.of(death)));

        assertThat(converter.fromMessage(replayed)).isInstanceOf(ReferencePaymentReceived.class);
    }

    @Test
    void invalidMessagesAreRejected() throws Exception {
        String key = PaymentsTopology.referencePaidKey(insurer);
        byte[] body = mapper.writeValueAsBytes(payment());
        String queue = PaymentsTopology.REFERENCE_PAYMENTS_QUEUE;
        for (Message m : List.of(
                delivered(body, "SomethingElse", 1, key, queue, Map.of()),
                delivered(body, "PaymentSourcePolled", 1, key, queue, Map.of()),
                delivered(body, "ReferencePaymentReceived", 2, key, queue, Map.of()),
                delivered("{x".getBytes(StandardCharsets.UTF_8), "ReferencePaymentReceived", 1, key, queue, Map.of()),
                delivered("{}".getBytes(StandardCharsets.UTF_8), "ReferencePaymentReceived", 1, key, queue, Map.of()),
                delivered(body, "ReferencePaymentReceived", 1, PaymentsTopology.referencePaidKey(UUID.randomUUID()),
                        queue, Map.of()))) {
            assertThatThrownBy(() -> converter.fromMessage(m)).isInstanceOf(MessageConversionException.class);
        }
    }

    @Test
    void theSchemaVersionHeaderIsOptional() throws Exception {
        Message m = delivered(mapper.writeValueAsBytes(payment()), "ReferencePaymentReceived", null,
                PaymentsTopology.referencePaidKey(insurer), PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, Map.of());
        assertThat(converter.fromMessage(m)).isInstanceOf(ReferencePaymentReceived.class);
    }
}
