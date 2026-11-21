package mz.co.moseguros.notification.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationTemplate.TemplateText;

/**
 * Modelos por defeito das notificações (S6-04 §3), lidos de {@code messages/notifications_{pt,en}.properties}
 * com {@link Properties} em UTF-8 (não pelo {@code MessageSource}: o Mustache usa {@code {{…}}}). Chaves
 * {@code notify.<CODE>.variables}, {@code notify.<CODE>.sample.<var>} (só no {@code _pt}),
 * {@code notify.<CODE>.<canal>.subject|body}, {@code notify.<CODE>.whatsapp.template|params|body}.
 */
public final class TemplateCatalog {

    public static final List<String> LOCALES = List.of("pt", "en");
    static final String PREFIX = "notify.";
    static final String RESOURCE = "messages/notifications_%s.properties";

    private final Map<String, CodeDefaults> codes;

    private TemplateCatalog(Map<String, CodeDefaults> codes) {
        this.codes = Collections.unmodifiableMap(codes);
    }

    /** Os ficheiros do classpath. */
    public static TemplateCatalog load() {
        Map<String, Properties> byLocale = new LinkedHashMap<>();
        for (String locale : LOCALES) {
            byLocale.put(locale, read(String.format(RESOURCE, locale)));
        }
        return of(byLocale);
    }

    /** As propriedades lidas (por língua): para os testes alterarem um texto. */
    public static Map<String, Properties> readFiles() {
        Map<String, Properties> byLocale = new LinkedHashMap<>();
        for (String locale : LOCALES) {
            byLocale.put(locale, read(String.format(RESOURCE, locale)));
        }
        return byLocale;
    }

    /** Catálogo a partir das propriedades de cada língua. */
    public static TemplateCatalog of(Map<String, Properties> byLocale) {
        Properties pt = byLocale.get("pt");
        TreeSet<String> codeNames = new TreeSet<>();
        for (String key : pt.stringPropertyNames()) {
            if (key.startsWith(PREFIX) && key.endsWith(".variables")) {
                codeNames.add(key.substring(PREFIX.length(), key.length() - ".variables".length()));
            }
        }
        Map<String, CodeDefaults> codes = new TreeMap<>();
        for (String code : codeNames) {
            String p = PREFIX + code + ".";
            List<String> variables = split(pt.getProperty(p + "variables"));
            Map<String, String> samples = new LinkedHashMap<>();
            for (String variable : variables) {
                String sample = pt.getProperty(p + "sample." + variable);
                if (sample != null) {
                    samples.put(variable, sample);
                }
            }
            Map<Key, TemplateText> texts = new LinkedHashMap<>();
            for (String locale : LOCALES) {
                Properties props = byLocale.get(locale);
                if (props == null) {
                    continue;
                }
                for (MessageChannel channel : List.of(MessageChannel.SMS, MessageChannel.WHATSAPP, MessageChannel.EMAIL,
                        MessageChannel.PUSH)) {
                    String c = p + channel.name().toLowerCase() + ".";
                    String body = props.getProperty(c + "body");
                    if (body == null) {
                        continue;
                    }
                    String subject = props.getProperty(c + "subject");
                    String waName = channel == MessageChannel.WHATSAPP ? props.getProperty(c + "template") : null;
                    List<String> waParams = channel == MessageChannel.WHATSAPP
                            ? split(props.getProperty(c + "params")) : null;
                    texts.put(new Key(channel, locale), new TemplateText(subject, body, waName, waParams));
                }
            }
            codes.put(code, new CodeDefaults(code, variables, samples, texts));
        }
        return new TemplateCatalog(codes);
    }

    public Map<String, CodeDefaults> codes() {
        return codes;
    }

    public Optional<TemplateText> text(String code, MessageChannel channel, String locale) {
        CodeDefaults defaults = codes.get(code);
        return defaults == null ? Optional.empty() : Optional.ofNullable(defaults.texts().get(new Key(channel, locale)));
    }

    /** SHA-256 (hexadecimal) do texto por defeito (assunto, corpo e WhatsApp). */
    public static String hash(TemplateText text) {
        String joined = String.join("\u0000", nullToEmpty(text.subject()), nullToEmpty(text.body()),
                nullToEmpty(text.whatsappTemplateName()),
                text.whatsappParams() == null ? "" : String.join(",", text.whatsappParams()));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(joined.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static List<String> split(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String part : Arrays.asList(csv.split(","))) {
            if (!part.isBlank()) {
                out.add(part.strip());
            }
        }
        return List.copyOf(out);
    }

    private static Properties read(String resource) {
        Properties properties = new Properties();
        try (InputStream in = TemplateCatalog.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + resource);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + resource, e);
        }
        return properties;
    }

    /** Canal e língua de um texto. */
    public record Key(MessageChannel channel, String locale) {
    }

    /**
     * Modelos de um código.
     *
     * @param variables variáveis permitidas (incluindo {@code link} e {@code siteUrl})
     * @param samples   valores de exemplo
     */
    public record CodeDefaults(String code, List<String> variables, Map<String, String> samples,
                               Map<Key, TemplateText> texts) {
    }
}
