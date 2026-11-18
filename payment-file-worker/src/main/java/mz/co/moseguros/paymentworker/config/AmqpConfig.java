package mz.co.moseguros.paymentworker.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ do worker (S5-09 §7, ADR-004): declara só o exchange {@code payments.events} (topic,
 * durável, igual ao do backend, S5-06) e nunca filas. As mensagens vão em JSON com as datas em ISO 8601
 * (o mesmo texto que entra nos {@code messageId}, {@code MessageIds}) e sem campos {@code null}.
 * <i>Publisher confirms</i> e {@code mandatory} vêm do {@code application.yml}.
 */
@Configuration(proxyBeanMethods = false)
public class AmqpConfig {

    @Bean
    Exchange paymentsEventsExchange() {
        return ExchangeBuilder.topicExchange(PaymentsTopology.EVENTS_EXCHANGE).durable(true).build();
    }

    /** JSON das mensagens: Instant em ISO 8601, sem {@code null}, sem o cabeçalho {@code __TypeId__}. */
    @Bean
    MessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        converter.setClassMapper(new org.springframework.amqp.support.converter.ClassMapper() {
            @Override
            public void fromClass(Class<?> clazz, org.springframework.amqp.core.MessageProperties properties) {
                // sem __TypeId__: o tipo vai na propriedade AMQP "type" (MessageHeaders.typeOf)
            }

            @Override
            public Class<?> toClass(org.springframework.amqp.core.MessageProperties properties) {
                return Object.class;
            }
        });
        return converter;
    }
}
