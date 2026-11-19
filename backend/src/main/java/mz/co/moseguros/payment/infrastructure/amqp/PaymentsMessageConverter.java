package mz.co.moseguros.payment.infrastructure.amqp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.shared.json.JsonSchemas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

/**
 * Conversor das mensagens do {@code payment-file-worker} (S5-10 §2): {@link Jackson2JsonMessageConverter} com
 * o mapa {@code type} (propriedade AMQP, {@link MessageHeaders#typeOf}) → classe dos quatro tipos do S5-06.
 *
 * <p>Antes de converter, recusa ({@link MessageConversionException}, que o consumidor não repete e manda logo
 * para a {@code .dlq}, com {@code ERROR} no log e o {@code messageId}):
 * <ul>
 *   <li>{@code type} desconhecido, ou de um tipo que não pertence à fila de onde veio;</li>
 *   <li>cabeçalho {@value MessageHeaders#SCHEMA_VERSION_HEADER} presente e diferente de {@code 1};</li>
 *   <li>JSON ilegível ou que não cumpre o JSON Schema do tipo ({@code schemas/*.v1.json} do
 *       {@code payment-contracts}; inclui {@code schemaVersion = 1});</li>
 *   <li>{@code insurerId} diferente do último segmento da chave de encaminhamento.</li>
 * </ul>
 */
public class PaymentsMessageConverter extends Jackson2JsonMessageConverter {

    private static final Logger log = LoggerFactory.getLogger(PaymentsMessageConverter.class);

    /** {@code type} → classe. */
    static final Map<String, Class<?>> TYPES = Map.of(
            MessageHeaders.typeOf(ReferencePaymentReceived.class), ReferencePaymentReceived.class,
            MessageHeaders.typeOf(PaymentFileProcessed.class), PaymentFileProcessed.class,
            MessageHeaders.typeOf(PaymentFileRejected.class), PaymentFileRejected.class,
            MessageHeaders.typeOf(PaymentSourcePolled.class), PaymentSourcePolled.class);

    /** JSON Schema de cada tipo. */
    private static final Map<Class<?>, String> SCHEMAS = Map.of(
            ReferencePaymentReceived.class, "reference-payment-received.v1.json",
            PaymentFileProcessed.class, "payment-file-processed.v1.json",
            PaymentFileRejected.class, "payment-file-rejected.v1.json",
            PaymentSourcePolled.class, "payment-source-polled.v1.json");

    /** Tipos aceites em cada fila. */
    private static final Map<String, Set<Class<?>>> QUEUE_TYPES = Map.of(
            PaymentsTopology.REFERENCE_PAYMENTS_QUEUE, Set.of(ReferencePaymentReceived.class),
            PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE,
            Set.of(PaymentFileProcessed.class, PaymentFileRejected.class, PaymentSourcePolled.class));

    private final ObjectMapper objectMapper;

    public PaymentsMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
        setClassMapper(new TypePropertyClassMapper());
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        MessageProperties props = message.getMessageProperties();
        String messageId = props.getMessageId();
        Class<?> type = TYPES.get(props.getType());
        if (type == null) {
            throw reject(messageId, "unknown type " + props.getType());
        }
        Set<Class<?>> accepted = QUEUE_TYPES.get(props.getConsumerQueue());
        if (accepted != null && !accepted.contains(type)) {
            throw reject(messageId, "type " + props.getType() + " not accepted in " + props.getConsumerQueue());
        }
        Object version = props.getHeader(MessageHeaders.SCHEMA_VERSION_HEADER);
        if (version != null && !String.valueOf(MessageHeaders.SCHEMA_VERSION).equals(String.valueOf(version))) {
            throw reject(messageId, "schemaVersion " + version);
        }
        JsonNode json;
        try {
            json = objectMapper.readTree(message.getBody());
        } catch (IOException e) {
            throw reject(messageId, "invalid JSON");
        }
        if (json == null || !json.isObject()) {
            throw reject(messageId, "invalid JSON");
        }
        List<JsonSchemas.Issue> issues = JsonSchemas.issues(SCHEMAS.get(type), json);
        if (!issues.isEmpty()) {
            // Só ponteiros e palavras-chave: nunca os valores (podem ser dados do pagamento).
            throw reject(messageId, "schema violations " + issues.stream().limit(5)
                    .map(i -> i.pointer() + " " + i.keyword()).toList());
        }
        Object payload;
        try {
            payload = objectMapper.treeToValue(json, type);
        } catch (IOException | IllegalArgumentException e) {
            throw reject(messageId, "unreadable payload");
        }
        String routingKey = originalRoutingKey(props);
        String insurerId = String.valueOf(json.path("insurerId").asText());
        String keyInsurer = routingKey == null ? null : routingKey.substring(routingKey.lastIndexOf('.') + 1);
        if (!insurerId.equals(keyInsurer)) {
            throw reject(messageId, "insurerId does not match the routing key " + routingKey);
        }
        return payload;
    }

    /**
     * Chave com que o worker publicou. Uma mensagem reposta a partir da {@code .dlq} (consola do RabbitMQ ou
     * <i>shovel</i>, {@code infra/README.md}) chega com a chave da fila; a original está no cabeçalho
     * {@code x-death}.
     */
    static String originalRoutingKey(MessageProperties props) {
        String received = props.getReceivedRoutingKey();
        if (received != null && received.startsWith("reference.")) {
            return received;
        }
        List<Map<String, ?>> deaths = props.getXDeathHeader();
        if (deaths != null) {
            for (Map<String, ?> death : deaths) {
                if (death.get("routing-keys") instanceof List<?> keys) {
                    for (Object key : keys) {
                        if (key != null && key.toString().startsWith("reference.")) {
                            return key.toString();
                        }
                    }
                }
            }
        }
        return received;
    }

    private static MessageConversionException reject(String messageId, String reason) {
        log.error("Payment message rejected to the dead-letter queue messageId={} reason={}", messageId, reason);
        return new MessageConversionException("Payment message rejected: " + reason);
    }

    /** {@code type} da propriedade AMQP ({@code MessageProperties.getType()}), não o cabeçalho {@code __TypeId__}. */
    private static final class TypePropertyClassMapper implements ClassMapper {

        @Override
        public void fromClass(Class<?> clazz, MessageProperties properties) {
            properties.setType(clazz.getSimpleName());
        }

        @Override
        public Class<?> toClass(MessageProperties properties) {
            Class<?> type = TYPES.get(properties.getType());
            if (type == null) {
                throw new MessageConversionException("Unknown payment message type " + properties.getType());
            }
            return type;
        }
    }
}
