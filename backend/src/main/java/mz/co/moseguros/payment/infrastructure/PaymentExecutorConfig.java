package mz.co.moseguros.payment.infrastructure;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Executor do envio do C2B (S5-04 §2): {@code mpesaC2bExecutor}, um {@link ThreadPoolTaskExecutor} com
 * 10 threads base, até 50, fila de 100, threads {@code mpesa-c2b-*}; no encerramento espera até 180 s
 * pelos C2B em curso (o C2B da Vodacom só responde quando o cliente confirma ou expira).
 *
 * <p>Fica dentro de {@link MpesaC2bExecutor} (e não como bean {@code Executor}) para não substituir o
 * {@code applicationTaskExecutor} do Spring Boot (como o {@code ratingExecutor} do S3-02).
 */
@Configuration(proxyBeanMethods = false)
public class PaymentExecutorConfig {

    public static final String MPESA_C2B_EXECUTOR = "mpesaC2bExecutor";

    @Bean(name = MPESA_C2B_EXECUTOR)
    MpesaC2bExecutor mpesaC2bExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mpesa-c2b-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(180);
        executor.initialize();
        return new MpesaC2bExecutor(executor);
    }

    /** O executor dos envios C2B. */
    public static final class MpesaC2bExecutor implements DisposableBean {

        private final ThreadPoolTaskExecutor executor;

        public MpesaC2bExecutor(ThreadPoolTaskExecutor executor) {
            this.executor = executor;
        }

        /** @throws TaskRejectedException com as threads e a fila cheias */
        public void execute(Runnable task) {
            executor.execute(task);
        }

        /** O executor por baixo (só para os testes e as métricas). */
        public Executor executor() {
            return executor;
        }

        @Override
        public void destroy() {
            executor.destroy();
        }
    }
}
