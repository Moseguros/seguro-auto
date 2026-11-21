package mz.co.moseguros.shared.scheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Liga os jobs agendados ({@code @Scheduled}) quando {@code moseguros.jobs.enabled} é {@code true}
 * (por defeito; {@link JobProperties}). Nos testes fica {@code false}: nenhum {@code @Scheduled} corre,
 * o {@code @SchedulerLock} não tem efeito e os testes chamam o método do job directamente.
 *
 * <p>Os jobs correm no fuso {@code Africa/Maputo}, são idempotentes e registam início, fim, duração e
 * contagens em log. Desde o S6-01 os jobs novos têm {@code @SchedulerLock(name = "<modulo>.<nome>")}
 * (ShedLock na tabela {@code shared.shedlock}, {@link SchedulerLockConfig}): uma só execução entre
 * instâncias. Os jobs do S2–S5 ainda não têm lock (Questões do S6-01); até lá o backend corre numa só
 * instância.
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
@ConditionalOnProperty(name = "moseguros.jobs.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
