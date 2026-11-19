package mz.co.moseguros.shared.scheduling;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração dos jobs agendados ({@code moseguros.jobs}, S6-01 §2).
 *
 * @param enabled {@code true} (por defeito) liga o {@link SchedulingConfig}; {@code false} nos testes
 *                ({@code application-test.yml}): nenhum {@code @Scheduled} corre
 */
@ConfigurationProperties("moseguros.jobs")
public record JobProperties(@DefaultValue("true") boolean enabled) {

    /** Regista as propriedades. */
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(JobProperties.class)
    static class Registration {
    }
}
