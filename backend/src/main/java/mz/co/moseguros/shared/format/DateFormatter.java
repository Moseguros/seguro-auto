package mz.co.moseguros.shared.format;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Datas para os textos das notificações (S6-04 §1): {@code dd/MM/yyyy HH:mm} para instantes, sempre em
 * {@code Africa/Maputo}, e {@code dd/MM/yyyy} para datas; iguais em pt e en. Os PDF usam o seu próprio
 * formatador ({@code document.infrastructure.PdfFormatter}).
 */
public final class DateFormatter {

    public static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(MAPUTO);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateFormatter() {
    }

    /** {@code 2027-02-10T08:31Z} → {@code 10/02/2027 10:31}. */
    public static String dateTime(Instant instant) {
        return DATE_TIME.format(Objects.requireNonNull(instant, "instant"));
    }

    /** {@code 2027-02-10} → {@code 10/02/2027}. */
    public static String date(LocalDate date) {
        return DATE.format(Objects.requireNonNull(date, "date"));
    }
}
