package mz.co.moseguros.payment.infrastructure.amqp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;

/**
 * Topologia RabbitMQ dos pagamentos por referência (ADR-004, S5-06 §3), com os nomes e os
 * argumentos de {@link PaymentsTopology}. O backend declara tudo (exchanges, filas, filas de
 * mensagens mortas e ligações); o worker só o exchange {@code payments.events}.
 *
 * <p>O {@link RabbitAdmin} do Spring Boot declara os {@link Declarables} sempre que abre uma
 * ligação. Para a topologia existir logo depois do arranque (e não só quando o primeiro consumidor
 * do S5-10 ligar), declara-se também quando a aplicação fica pronta; se o RabbitMQ não estiver
 * disponível nesse momento fica um aviso, e a declaração repete-se na primeira ligação.
 *
 * <p>Consumo (S5-10 §2): {@link #paymentsListenerFactory} para o {@code ReferencePaymentListener} e o
 * {@code PaymentFileEventsListener}. {@code AcknowledgeMode.AUTO} do Spring: o consumidor regista-se sem
 * <i>auto-ack</i> e o {@code basic.ack} só sai depois de o método terminar sem erro (depois do commit), a
 * "confirmação manual" do ADR-004. Os erros técnicos repetem-se 5 vezes (1 s, 2 s, 4 s, 8 s, 16 s) e depois a
 * mensagem é rejeitada para a {@code .dlq}; as mensagens inválidas ({@link PaymentsMessageConverter}) vão logo.
 */
@Configuration(proxyBeanMethods = false)
class PaymentsAmqpConfig {

    private static final Logger log = LoggerFactory.getLogger(PaymentsAmqpConfig.class);

    @Bean
    Declarables paymentsTopology() {
        TopicExchange events = ExchangeBuilder.topicExchange(PaymentsTopology.EVENTS_EXCHANGE).durable(true).build();
        DirectExchange deadLetters =
                ExchangeBuilder.directExchange(PaymentsTopology.DEAD_LETTER_EXCHANGE).durable(true).build();

        Queue referencePayments = mainQueue(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE);
        Queue fileEvents = mainQueue(PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE);
        Queue referencePaymentsDlq = deadLetterQueue(PaymentsTopology.REFERENCE_PAYMENTS_DLQ);
        Queue fileEventsDlq = deadLetterQueue(PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ);

        List<Declarable> declarables = new ArrayList<>(List.of(
                events, deadLetters, referencePayments, fileEvents, referencePaymentsDlq, fileEventsDlq,
                BindingBuilder.bind(referencePayments).to(events).with(PaymentsTopology.REFERENCE_PAID_BINDING),
                deadLetterBinding(referencePaymentsDlq, deadLetters, PaymentsTopology.REFERENCE_PAYMENTS_QUEUE),
                deadLetterBinding(fileEventsDlq, deadLetters, PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE)));
        // Ligações explícitas: num exchange topic "reference.file-*" não seria um curinga.
        for (String pattern : PaymentsTopology.PAYMENT_FILE_EVENTS_BINDINGS) {
            declarables.add(BindingBuilder.bind(fileEvents).to(events).with(pattern));
        }
        return new Declarables(declarables);
    }

    /** Tentativas de uma mensagem com erro técnico: 1 + 5 novas. */
    static final int MAX_ATTEMPTS = 6;

    @Bean
    PaymentsMessageConverter paymentsMessageConverter(ObjectMapper objectMapper) {
        return new PaymentsMessageConverter(objectMapper);
    }

    @Bean
    SimpleRabbitListenerContainerFactory paymentsListenerFactory(ConnectionFactory connectionFactory,
                                                                 PaymentsMessageConverter paymentsMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(paymentsMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setPrefetchCount(10);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(4);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryInterceptor());
        return factory;
    }

    /**
     * Repetição sem estado: 6 tentativas com 1 s, 2 s, 4 s, 8 s e 16 s de espera (máximo 30 s); depois
     * {@link RejectAndDontRequeueRecoverer} (a fila manda a mensagem para a {@code .dlq}). Mensagens inválidas
     * ({@link MessageConversionException}) e rejeições explícitas não se repetem.
     */
    static RetryOperationsInterceptor retryInterceptor() {
        SimpleRetryPolicy policy = new SimpleRetryPolicy(MAX_ATTEMPTS, Map.of(
                MessageConversionException.class, false,
                AmqpRejectAndDontRequeueException.class, false), true, true);
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1_000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(30_000);
        return RetryInterceptorBuilder.stateless()
                .retryPolicy(policy)
                .backOffPolicy(backOff)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    void declareOnStartup(ApplicationReadyEvent event) {
        AmqpAdmin admin = event.getApplicationContext().getBeanProvider(AmqpAdmin.class).getIfAvailable();
        if (!(admin instanceof RabbitAdmin rabbitAdmin)) {
            return;
        }
        try {
            rabbitAdmin.initialize();
            log.info("RabbitMQ payments topology declared (exchange {})", PaymentsTopology.EVENTS_EXCHANGE);
        } catch (RuntimeException e) {
            log.warn("RabbitMQ unavailable at startup; payments topology will be declared on the first connection: {}",
                    e.getClass().getSimpleName());
        }
    }

    private static Queue mainQueue(String name) {
        return QueueBuilder.durable(name).withArguments(PaymentsTopology.mainQueueArguments(name)).build();
    }

    private static Queue deadLetterQueue(String name) {
        return QueueBuilder.durable(name).withArguments(PaymentsTopology.deadLetterQueueArguments()).build();
    }

    private static Binding deadLetterBinding(Queue dlq, DirectExchange deadLetters, String routingKey) {
        return BindingBuilder.bind(dlq).to(deadLetters).with(routingKey);
    }
}
