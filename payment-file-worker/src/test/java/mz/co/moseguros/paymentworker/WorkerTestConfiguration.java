package mz.co.moseguros.paymentworker;

import java.util.concurrent.atomic.AtomicInteger;

import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.paymentworker.secrets.InMemorySecretReader;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Beans dos testes de integração do worker: o cofre em memória e um {@link RabbitTemplate} que pode devolver {@code nack} ao N-ésimo
 * {@code ReferencePaymentReceived}.
 */
@TestConfiguration(proxyBeanMethods = false)
public class WorkerTestConfiguration {

    @Bean
    @Primary
    InMemorySecretReader inMemorySecretReader() {
        return new InMemorySecretReader();
    }

    /** Configurado como o do Spring Boot (confirms, mandatory, conversor JSON do worker). */
    @Bean
    NackingRabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        NackingRabbitTemplate template = new NackingRabbitTemplate();
        configurer.configure(template, connectionFactory);
        return template;
    }

    /** {@link #nackReferencePayment(int)}: o N-ésimo {@code ReferencePaymentReceived} seguinte recebe {@code nack}. */
    public static class NackingRabbitTemplate extends RabbitTemplate {

        private final AtomicInteger nackAt = new AtomicInteger();
        private final AtomicInteger seen = new AtomicInteger();

        public void nackReferencePayment(int n) {
            seen.set(0);
            nackAt.set(n);
        }

        public void reset() {
            nackAt.set(0);
            seen.set(0);
        }

        @Override
        public void convertAndSend(String exchange, String routingKey, Object message,
                                   MessagePostProcessor messagePostProcessor, CorrelationData correlationData)
                throws AmqpException {
            if (message instanceof ReferencePaymentReceived && nackAt.get() > 0
                    && seen.incrementAndGet() == nackAt.get()) {
                nackAt.set(0);
                correlationData.getFuture().complete(new CorrelationData.Confirm(false, "test nack"));
                return;
            }
            super.convertAndSend(exchange, routingKey, message, messagePostProcessor, correlationData);
        }
    }
}
