package mz.co.moseguros.document.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Motor Thymeleaf só para os PDF (S4-04 §1), sem a resolução de vistas do MVC: modelos XHTML em
 * {@code classpath:templates/pdf/} ({@link TemplateMode#XML}, UTF-8, com cache fora do perfil
 * {@code local}) e textos de {@code classpath:messages/pdf_{pt,en}.properties} (decisão D7).
 *
 * <p>Uma chave de texto em falta lança excepção (o PDF falha com {@code PDF_RENDER_FAILED}): nunca sai
 * um texto vazio nem {@code ??chave??}. O {@code SpringMessageResolver} do Thymeleaf trata a
 * {@link NoSuchMessageException} como texto em falta e mostra {@code ??chave??}; por isso os textos
 * chegam-lhe por um {@link StrictMessageSource}, que a troca por uma {@link IllegalStateException}.
 */
@Configuration(proxyBeanMethods = false)
public class ThymeleafPdfConfig {

    public static final String TEMPLATE_ENGINE = "pdfTemplateEngine";
    public static final String MESSAGE_SOURCE = "pdfMessageSource";
    /** Língua por defeito dos textos (sem alternativa para a língua do sistema). */
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("pt");

    @Bean(MESSAGE_SOURCE)
    MessageSource pdfMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages/pdf");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        source.setDefaultLocale(DEFAULT_LOCALE);
        source.setUseCodeAsDefaultMessage(false);
        source.setAlwaysUseMessageFormat(true);
        return source;
    }

    @Bean(TEMPLATE_ENGINE)
    SpringTemplateEngine pdfTemplateEngine(@Qualifier(MESSAGE_SOURCE) MessageSource messages, Environment environment) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/pdf/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.XML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(!environment.acceptsProfiles(Profiles.of("local")));
        resolver.setCheckExistence(true);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.setTemplateEngineMessageSource(new StrictMessageSource(messages));
        engine.setEnableSpringELCompiler(false);
        return engine;
    }

    /** Os textos dos PDF; uma chave em falta lança {@link IllegalStateException} (não a ignora). */
    static final class StrictMessageSource implements MessageSource {

        private final MessageSource messages;

        StrictMessageSource(MessageSource messages) {
            this.messages = messages;
        }

        @Override
        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            return getMessage(code, args, locale);
        }

        @Override
        public String getMessage(String code, Object[] args, Locale locale) {
            try {
                return messages.getMessage(code, args, locale);
            } catch (NoSuchMessageException e) {
                throw new IllegalStateException("Missing PDF text: " + code + " (" + locale + ")", e);
            }
        }

        @Override
        public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
            try {
                return messages.getMessage(resolvable, locale);
            } catch (NoSuchMessageException e) {
                throw new IllegalStateException("Missing PDF text (" + locale + ")", e);
            }
        }
    }
}
