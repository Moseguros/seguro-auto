package mz.co.moseguros.payment.infrastructure.amqp;

import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

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
 * <p>Esta tarefa só declara: os consumidores são do S5-10.
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
