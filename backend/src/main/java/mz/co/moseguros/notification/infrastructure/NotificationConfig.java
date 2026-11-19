package mz.co.moseguros.notification.infrastructure;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

/**
 * Configuração dos canais de envio: propriedades, textos das mensagens
 * ({@code messages/notifications_*.properties}), o cliente HTTP dos fornecedores e o executor das
 * notificações ({@code notificationExecutor}, 4 threads, S6-04 §4). Com o perfil {@code prod}, o push
 * {@code mock} impede o arranque (S6-04 §6).
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationConfig {

    /** Língua das mensagens quando a pedida não existe. */
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("pt");
    public static final String NOTIFICATION_EXECUTOR = "notificationExecutor";

    /** Textos das mensagens enviadas (pt e en). Não substitui o {@code messageSource} da aplicação. */
    @Bean
    MessageSource notificationMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages/notifications");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        source.setDefaultLocale(DEFAULT_LOCALE);
        return source;
    }

    /** Cliente HTTP dos fornecedores, com o timeout de {@code moseguros.notification.timeout}. */
    @Bean
    RestClient notificationRestClient(RestClient.Builder builder, NotificationProperties properties,
                                      Environment environment) {
        checkPushProvider(properties, environment);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    /**
     * Executor dos envios depois do commit. Fica dentro de {@link NotificationExecutor} (e não como bean
     * {@code Executor}) para não substituir o {@code applicationTaskExecutor} do Spring Boot.
     */
    @Bean(name = NOTIFICATION_EXECUTOR)
    NotificationExecutor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10_000);
        executor.setThreadNamePrefix("notification-");
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
        return new NotificationExecutor(executor);
    }

    /** {@code push.provider: mock} com o perfil {@code prod} → a aplicação não arranca. */
    static void checkPushProvider(NotificationProperties properties, Environment environment) {
        boolean prod = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        if (prod && "mock".equalsIgnoreCase(properties.push().provider())) {
            throw new IllegalStateException(
                    "moseguros.notification.push.provider=mock is not allowed with the prod profile");
        }
    }

    /** Executor das notificações. */
    public static final class NotificationExecutor implements DisposableBean {

        private final ThreadPoolTaskExecutor executor;

        NotificationExecutor(ThreadPoolTaskExecutor executor) {
            this.executor = executor;
        }

        /** Agenda a tarefa; com a fila cheia lança {@code TaskRejectedException} (o job envia depois). */
        public void execute(Runnable task) {
            executor.execute(task);
        }

        @Override
        public void destroy() {
            executor.shutdown();
        }
    }
}
