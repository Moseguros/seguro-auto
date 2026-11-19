package mz.co.moseguros.paymentworker.config;

import java.time.Clock;
import java.time.Duration;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestClient;

/**
 * Agendamento das leituras (S5-09 §4): um {@code ThreadPoolTaskScheduler} de 4 <i>threads</i>
 * ({@code source-}) para as fontes, e outro de 1 ({@code sources-refresh-}) para pedir as fontes ao
 * backend. Também o relógio e o {@link RestClient} do backend (<i>timeout</i> de 10 s).
 */
@Configuration(proxyBeanMethods = false)
public class SchedulingConfig {

    public static final int SOURCE_POOL_SIZE = 4;
    static final Duration BACKEND_TIMEOUT = Duration.ofSeconds(10);

    @Bean(destroyMethod = "shutdown")
    ThreadPoolTaskScheduler sourceScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(SOURCE_POOL_SIZE);
        scheduler.setThreadNamePrefix("source-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        scheduler.initialize();
        return scheduler;
    }

    @Bean(destroyMethod = "shutdown")
    ThreadPoolTaskScheduler refreshScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("sources-refresh-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    RestClient backendRestClient(RestClient.Builder builder) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(BACKEND_TIMEOUT)
                .withReadTimeout(BACKEND_TIMEOUT);
        return builder.requestFactory(ClientHttpRequestFactories.get(settings)).build();
    }
}
