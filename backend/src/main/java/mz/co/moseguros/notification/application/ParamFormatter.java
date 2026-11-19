package mz.co.moseguros.notification.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.format.DateFormatter;
import mz.co.moseguros.shared.format.MoneyFormat;

/**
 * Formata os parâmetros de {@code notify} na língua do destinatário (S6-04 §1): {@link Money} →
 * {@link MoneyFormat}; {@link Instant} → {@code dd/MM/yyyy HH:mm} em {@code Africa/Maputo}; {@link LocalDate} →
 * {@code dd/MM/yyyy}; o resto → {@code toString()}. Os parâmetros reservados ({@code webPath},
 * {@code deepLink}, {@code link}, {@code siteUrl}) não entram.
 */
public final class ParamFormatter {

    public static final String WEB_PATH = "webPath";
    public static final String DEEP_LINK = "deepLink";
    public static final String LINK = "link";
    public static final String SITE_URL = "siteUrl";
    /** Variáveis que o {@code notify} acrescenta (não se passam). */
    public static final Set<String> AUTOMATIC = Set.of(LINK, SITE_URL);
    /** Parâmetros que nunca são variáveis do modelo. */
    public static final Set<String> RESERVED = Set.of(WEB_PATH, DEEP_LINK, LINK, SITE_URL);

    private ParamFormatter() {
    }

    public static Map<String, String> format(Map<String, Object> params, String language) {
        Map<String, String> out = new LinkedHashMap<>();
        params.forEach((name, value) -> {
            if (!RESERVED.contains(name) && value != null) {
                out.put(name, format(value, language));
            }
        });
        return out;
    }

    public static String format(Object value, String language) {
        if (value instanceof Money money) {
            return MoneyFormat.format(money, language);
        }
        if (value instanceof Instant instant) {
            return DateFormatter.dateTime(instant);
        }
        if (value instanceof LocalDate date) {
            return DateFormatter.date(date);
        }
        return value.toString();
    }
}
