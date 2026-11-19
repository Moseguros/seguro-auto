package mz.co.moseguros.shared.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Datas das notificações (S6-04 §1): sempre em {@code Africa/Maputo}, iguais em pt e en. */
class DateFormatterTest {

    @Test
    void instantsAreShownInMaputo() {
        assertThat(DateFormatter.dateTime(Instant.parse("2027-02-10T08:31:00Z"))).isEqualTo("10/02/2027 10:31");
        assertThat(DateFormatter.dateTime(Instant.parse("2027-02-10T22:30:00Z"))).isEqualTo("11/02/2027 00:30");
    }

    @Test
    void datesAreDayMonthYear() {
        assertThat(DateFormatter.date(LocalDate.of(2027, 2, 10))).isEqualTo("10/02/2027");
    }
}
