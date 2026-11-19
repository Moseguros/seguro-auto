package mz.co.moseguros.paymentworker.processing;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicação de uma mensagem no {@code payments.events} com <i>publisher confirms</i> (S5-09 §7):
 * espera pela confirmação até {@code publish-confirm-timeout}. Só há sucesso com {@code ack} e sem a
 * mensagem devolvida ({@code mandatory}, sem fila ligada); {@code nack}, devolução ou falta de resposta
 * → {@link PublishException}. O worker nunca considera um registo publicado sem {@code ack}.
 */
@Component
public class RecordPublisher {

    private final RabbitTemplate rabbit;
    private final Duration confirmTimeout;

    public RecordPublisher(RabbitTemplate rabbit, WorkerProperties properties) {
        this.rabbit = rabbit;
        this.confirmTimeout = properties.publishConfirmTimeout();
    }

    /** A mensagem não foi confirmada. A mensagem da excepção nunca traz o conteúdo. */
    public static class PublishException extends Exception {

        public PublishException(String message) {
            super(message);
        }

        public PublishException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Publica e espera pelo {@code ack}.
     *
     * @param routingKey ex.: {@link PaymentsTopology#referencePaidKey(UUID)}
     * @param messageId  o {@code messageId} da mensagem (também a propriedade AMQP {@code message-id})
     */
    public void publish(Object message, String routingKey, String messageId) throws PublishException {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(messageId, "messageId");
        CorrelationData correlation = new CorrelationData(messageId);
        String type = MessageHeaders.typeOf(message.getClass());
        try {
            rabbit.convertAndSend(PaymentsTopology.EVENTS_EXCHANGE, routingKey, message, m -> {
                m.getMessageProperties().setMessageId(messageId);
                m.getMessageProperties().setContentType(MessageHeaders.CONTENT_TYPE);
                m.getMessageProperties().setType(type);
                m.getMessageProperties().setHeader(MessageHeaders.SCHEMA_VERSION_HEADER, MessageHeaders.SCHEMA_VERSION);
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            }, correlation);
        } catch (AmqpException e) {
            throw new PublishException("Could not publish " + type + ": " + e.getClass().getSimpleName(), e);
        }
        CorrelationData.Confirm confirm;
        try {
            confirm = correlation.getFuture().get(confirmTimeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new PublishException("No publisher confirm for " + type + " within " + confirmTimeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PublishException("Interrupted waiting for the publisher confirm of " + type);
        } catch (ExecutionException e) {
            throw new PublishException("Publisher confirm of " + type + " failed", e.getCause());
        }
        if (!confirm.isAck()) {
            throw new PublishException("Broker nack for " + type + ": " + confirm.getReason());
        }
        if (correlation.getReturned() != null) {
            throw new PublishException("Message " + type + " returned by the broker (no queue bound for "
                    + routingKey + ")");
        }
    }
}
