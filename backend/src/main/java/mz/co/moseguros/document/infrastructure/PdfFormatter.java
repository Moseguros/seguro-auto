package mz.co.moseguros.document.infrastructure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.format.MoneyFormat;

/**
 * Formatação dos valores nos modelos de PDF (variável {@code fmt}, S4-04 §1). Os {@link Instant}
 * mostram-se em {@code Africa/Maputo}. {@code null} → {@code —}.
 *
 * <ul>
 *   <li>{@link #money}: pt {@code 12 345,67 MT} (U+00A0 nos milhares), en {@code MZN 12,345.67};</li>
 *   <li>{@link #date}: pt {@code 09/01/2027}, en {@code 09 Jan 2027};</li>
 *   <li>{@link #dateTime}: pt {@code 09/01/2027 14:05}, en {@code 09 Jan 2027 14:05};</li>
 *   <li>{@link #percent}: pt {@code 5,00 %} (U+00A0 antes do {@code %}), en {@code 5.00%}.</li>
 * </ul>
 */
public final class PdfFormatter {

    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    static final String EMPTY = "—";

    private static final DateTimeFormatter PT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);
    private static final DateTimeFormatter EN_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);

    private final boolean english;

    public PdfFormatter(Locale locale) {
        this.english = locale != null && "en".equals(locale.getLanguage());
    }

    /** Língua efectiva: {@code en} ou {@code pt}. */
    public String language() {
        return english ? "en" : "pt";
    }

    public String money(Money money) {
        return money == null ? EMPTY : MoneyFormat.format(money, language());
    }

    /** Centavos (ex.: {@code total_amount}). */
    public String centavos(Long amount) {
        return amount == null ? EMPTY : money(Money.ofCentavos(amount));
    }

    /** Uma data ({@link LocalDate}) ou um instante ({@link Instant}, convertido para Maputo). */
    public String date(Object value) {
        LocalDate date = toDate(value);
        if (date == null) {
            return EMPTY;
        }
        return (english ? EN_DATE : PT_DATE).format(date);
    }

    public String dateTime(Instant instant) {
        if (instant == null) {
            return EMPTY;
        }
        LocalDateTime local = LocalDateTime.ofInstant(instant, ZONE);
        return date(local.toLocalDate()) + " " + TIME.format(local);
    }

    /** Percentagem com 2 casas ({@code 5} → {@code 5,00 %} / {@code 5.00%}). */
    public String percent(BigDecimal value) {
        if (value == null) {
            return EMPTY;
        }
        String number = value.setScale(2, RoundingMode.HALF_UP).toPlainString();
        return english ? number + "%" : number.replace('.', ',') + MoneyFormat.NBSP + "%";
    }

    /** Texto ou {@code —} se vazio. */
    public String text(Object value) {
        if (value == null) {
            return EMPTY;
        }
        String text = value.toString();
        return text.isBlank() ? EMPTY : text;
    }

    private static LocalDate toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate date) {
            return date;
        }
        if (value instanceof Instant instant) {
            return LocalDate.ofInstant(instant, ZONE);
        }
        if (value instanceof CharSequence text) {
            String s = text.toString();
            return s.isBlank() ? null : LocalDate.parse(s);
        }
        throw new IllegalArgumentException("Not a date: " + value.getClass().getName());
    }
}
