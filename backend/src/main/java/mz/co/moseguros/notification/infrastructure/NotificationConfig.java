package mz.co.moseguros.notification.infrastructure;

import java.util.Locale;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuração dos canais de envio: propriedades, textos das mensagens
 * ({@code messages/notifications_*.properties}) e o cliente HTTP dos fornecedores.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationConfig {

    /** Língua das mensagens quando a pedida não existe. */
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("pt");

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
    RestClient notificationRestClient(RestClient.Builder builder, NotificationProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }
}
