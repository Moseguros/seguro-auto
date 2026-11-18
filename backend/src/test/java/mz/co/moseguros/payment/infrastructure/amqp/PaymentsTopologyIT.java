package mz.co.moseguros.payment.infrastructure.amqp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.GetResponse;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.RabbitMQContainer;

/**
 * A topologia dos pagamentos existe logo depois do arranque (S5-06 §3): exchanges, filas com os
 * argumentos, ligações explícitas e dead-letter. Lê a API de gestão do RabbitMQ do contentor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class PaymentsTopologyIT {

    private static final long RECEIVE_TIMEOUT_MS = 5_000;
    private static final List<String> QUEUES = List.of(
            PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE,
            PaymentsTopology.REFERENCE_PAYMENTS_DLQ, PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ);

    @Autowired
    RabbitMQContainer rabbitmq;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    ObjectMapper objectMapper;

    private final HttpClient http = HttpClient.newHttpClient();

    private void purgeQueues() {
        QUEUES.forEach(q -> amqpAdmin.purgeQueue(q, false));
    }

    @Test
    void exchangesExistAfterStartup() throws Exception {
        JsonNode events = management("/api/exchanges/%2F/" + PaymentsTopology.EVENTS_EXCHANGE);
        assertThat(events.get("type").asText()).isEqualTo("topic");
        assertThat(events.get("durable").asBoolean()).isTrue();
        assertThat(events.get("auto_delete").asBoolean()).isFalse();

        JsonNode dlx = management("/api/exchanges/%2F/" + PaymentsTopology.DEAD_LETTER_EXCHANGE);
        assertThat(dlx.get("type").asText()).isEqualTo("direct");
        assertThat(dlx.get("durable").asBoolean()).isTrue();
    }

    @Test
    void queuesHaveTheArgumentsOfTheTopology() throws Exception {
        for (String queue : List.of(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE,
                PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE)) {
            JsonNode info = management("/api/queues/%2F/" + queue);
            assertThat(info.get("durable").asBoolean()).as(queue).isTrue();
            assertThat(info.get("type").asText()).as(queue).isEqualTo("quorum");
            JsonNode args = info.get("arguments");
            assertThat(args.get("x-queue-type").asText()).isEqualTo("quorum");
            assertThat(args.get("x-dead-letter-exchange").asText()).isEqualTo("payments.dlx");
            assertThat(args.get("x-dead-letter-routing-key").asText()).isEqualTo(queue);
            assertThat(args.get("x-delivery-limit").asInt()).isEqualTo(20);
        }
        for (String dlq : List.of(PaymentsTopology.REFERENCE_PAYMENTS_DLQ, PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ)) {
            JsonNode info = management("/api/queues/%2F/" + dlq);
            assertThat(info.get("durable").asBoolean()).as(dlq).isTrue();
            assertThat(info.get("type").asText()).as(dlq).isEqualTo("quorum");
            assertThat(info.get("arguments").has("x-dead-letter-exchange")).as(dlq).isFalse();
        }
    }

    @Test
    void bindingsAreExplicit() throws Exception {
        assertThat(bindingKeys(PaymentsTopology.EVENTS_EXCHANGE, PaymentsTopology.REFERENCE_PAYMENTS_QUEUE))
                .containsExactly("reference.paid.*");
        assertThat(bindingKeys(PaymentsTopology.EVENTS_EXCHANGE, PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE))
                .containsExactlyInAnyOrder("reference.file-processed.*", "reference.file-rejected.*",
                        "reference.source-polled.*");
        assertThat(bindingKeys(PaymentsTopology.DEAD_LETTER_EXCHANGE, PaymentsTopology.REFERENCE_PAYMENTS_DLQ))
                .containsExactly("core.reference-payments");
        assertThat(bindingKeys(PaymentsTopology.DEAD_LETTER_EXCHANGE, PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ))
                .containsExactly("core.payment-file-events");
    }

    @Test
    void referencePaidGoesToReferencePayments() {
        purgeQueues();
        String id = UUID.randomUUID().toString();
        publish(PaymentsTopology.referencePaidKey(UUID.randomUUID()), id);

        Message received = rabbitTemplate.receive(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, RECEIVE_TIMEOUT_MS);
        assertThat(received).isNotNull();
        assertThat(received.getMessageProperties().getMessageId()).isEqualTo(id);
        assertThat(received.getMessageProperties().getType()).isEqualTo("ReferencePaymentReceived");
        assertThat((Object) received.getMessageProperties().getHeader(MessageHeaders.SCHEMA_VERSION_HEADER))
                .isEqualTo(1);
        assertThat(rabbitTemplate.receive(PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE, 500)).isNull();
    }

    @Test
    void fileEventsGoToPaymentFileEvents() {
        purgeQueues();
        UUID insurer = UUID.randomUUID();
        for (String key : List.of(PaymentsTopology.sourcePolledKey(insurer), PaymentsTopology.fileProcessedKey(insurer),
                PaymentsTopology.fileRejectedKey(insurer))) {
            String id = UUID.randomUUID().toString();
            publish(key, id);
            Message received = rabbitTemplate.receive(PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE, RECEIVE_TIMEOUT_MS);
            assertThat(received).as(key).isNotNull();
            assertThat(received.getMessageProperties().getMessageId()).isEqualTo(id);
        }
        assertThat(rabbitTemplate.receive(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, 500)).isNull();
    }

    @Test
    void rejectedMessageGoesToTheMatchingDlq() {
        purgeQueues();
        String paid = UUID.randomUUID().toString();
        publish(PaymentsTopology.referencePaidKey(UUID.randomUUID()), paid);
        rejectOne(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE);

        Message deadPaid = rabbitTemplate.receive(PaymentsTopology.REFERENCE_PAYMENTS_DLQ, RECEIVE_TIMEOUT_MS);
        assertThat(deadPaid).isNotNull();
        assertThat(deadPaid.getMessageProperties().getMessageId()).isEqualTo(paid);

        String polled = UUID.randomUUID().toString();
        publish(PaymentsTopology.sourcePolledKey(UUID.randomUUID()), polled);
        rejectOne(PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE);

        Message deadPolled = rabbitTemplate.receive(PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ, RECEIVE_TIMEOUT_MS);
        assertThat(deadPolled).isNotNull();
        assertThat(deadPolled.getMessageProperties().getMessageId()).isEqualTo(polled);
        assertThat(rabbitTemplate.receive(PaymentsTopology.REFERENCE_PAYMENTS_DLQ, 500)).isNull();
    }

    private void publish(String routingKey, String messageId) {
        String type = routingKey.startsWith(PaymentsTopology.REFERENCE_PAID_PREFIX) ? "ReferencePaymentReceived"
                : "PaymentSourcePolled";
        Message message = MessageBuilder.withBody("{}".getBytes(StandardCharsets.UTF_8))
                .setMessageId(messageId)
                .setContentType(MessageHeaders.CONTENT_TYPE)
                .setType(type)
                .setHeader(MessageHeaders.SCHEMA_VERSION_HEADER, MessageHeaders.SCHEMA_VERSION)
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        rabbitTemplate.send(PaymentsTopology.EVENTS_EXCHANGE, routingKey, message);
    }

    /** {@code basicReject} sem {@code requeue}: a fila manda a mensagem para o dead-letter exchange. */
    private void rejectOne(String queue) {
        rabbitTemplate.execute(channel -> {
            GetResponse response = null;
            long deadline = System.currentTimeMillis() + RECEIVE_TIMEOUT_MS;
            while (response == null && System.currentTimeMillis() < deadline) {
                response = channel.basicGet(queue, false);
                if (response == null) {
                    Thread.sleep(50);
                }
            }
            assertThat(response).as("message in " + queue).isNotNull();
            channel.basicReject(response.getEnvelope().getDeliveryTag(), false);
            return null;
        });
    }

    private List<String> bindingKeys(String exchange, String queue) throws Exception {
        JsonNode bindings = management("/api/bindings/%2F/e/" + encode(exchange) + "/q/" + encode(queue));
        return StreamSupport.stream(bindings.spliterator(), false)
                .map(b -> b.get("routing_key").asText())
                .toList();
    }

    private JsonNode management(String path) throws Exception {
        String auth = Base64.getEncoder().encodeToString(
                (rabbitmq.getAdminUsername() + ":" + rabbitmq.getAdminPassword()).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(URI.create(rabbitmq.getHttpUrl() + path))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).as("GET " + path).isEqualTo(200);
        return objectMapper.readTree(response.body());
    }

    private static String encode(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8);
    }

}
