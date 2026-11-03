package mz.co.moseguros.rating.application;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.MDC;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Executor da fase 2 das cotações (S3-02 §4): {@code ratingExecutor}, um
 * {@link ThreadPoolTaskExecutor} com os tamanhos de {@code moseguros.rating.executor}, threads
 * {@code rating-*} e o MDC de quem pediu (o {@code traceId}) copiado para cada tarefa. As tarefas
 * não têm {@code TenantContext} nem transacção.
 *
 * <p>Fica dentro de {@link RatingExecutor} (e não como bean {@code Executor}) para não substituir o
 * {@code applicationTaskExecutor} que o Spring Boot cria quando não há nenhum {@code Executor}.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RatingProperties.class)
public class RatingExecutorConfig {

    public static final String RATING_EXECUTOR = "ratingExecutor";

    @Bean(name = RATING_EXECUTOR)
    RatingExecutor ratingExecutor(RatingProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getExecutor().getCoreSize());
        executor.setMaxPoolSize(properties.getExecutor().getMaxSize());
        executor.setQueueCapacity(properties.getExecutor().getQueueCapacity());
        executor.setThreadNamePrefix("rating-");
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
        return new RatingExecutor(executor);
    }

    /** O executor das avaliações. */
    public static final class RatingExecutor implements DisposableBean {

        private final ThreadPoolTaskExecutor executor;

        RatingExecutor(ThreadPoolTaskExecutor executor) {
            this.executor = executor;
        }

        /** @throws TaskRejectedException com a fila cheia */
        public <T> Future<T> submit(Callable<T> task) {
            return executor.submit(task);
        }

        @Override
        public void destroy() {
            executor.shutdown();
        }
    }

    /** Copia o MDC da thread que submete (ex.: {@code traceId}) para a thread que executa. */
    static final class MdcTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> context = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> previous = MDC.getCopyOfContextMap();
                if (context == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(context);
                }
                try {
                    runnable.run();
                } finally {
                    if (previous == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(previous);
                    }
                }
            };
        }
    }
}
