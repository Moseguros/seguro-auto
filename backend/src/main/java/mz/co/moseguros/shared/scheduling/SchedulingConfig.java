package mz.co.moseguros.shared.scheduling;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Liga os jobs agendados ({@code @Scheduled}) quando {@code moseguros.jobs.enabled} é {@code true}
 * (por defeito). Nos testes fica {@code false}: os testes chamam o método do job directamente.
 *
 * <p>Os jobs correm no fuso {@code Africa/Maputo}, com {@code @RunAsSystem}, são idempotentes e
 * registam início, fim e contagens em log. O ShedLock (uma só execução entre instâncias) chega
 * no S6; até lá o backend corre numa só instância.
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@ConditionalOnProperty(name = "moseguros.jobs.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
