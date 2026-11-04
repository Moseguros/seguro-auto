package mz.co.moseguros.document.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;

import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.Test;

/** {@link PdfFormatter} (variável {@code fmt} dos modelos de PDF, S4-04 §1). */
class PdfFormatterTest {

    static final String NBSP = " ";

    private final PdfFormatter pt = new PdfFormatter(Locale.forLanguageTag("pt"));
    private final PdfFormatter en = new PdfFormatter(Locale.ENGLISH);

    @Test
    void moneyIsTheSameAsTheWeb() {
        assertThat(pt.money(Money.ofCentavos(1234567))).isEqualTo("12" + NBSP + "345,67 MT");
        assertThat(pt.money(Money.ofCentavos(466300))).isEqualTo("4" + NBSP + "663,00 MT");
        assertThat(pt.money(Money.ofCentavos(5))).isEqualTo("0,05 MT");
        assertThat(pt.money(Money.ofCentavos(123456789012L))).isEqualTo("1" + NBSP + "234" + NBSP + "567" + NBSP
                + "890,12 MT");
        assertThat(en.money(Money.ofCentavos(1234567))).isEqualTo("MZN 12,345.67");
        assertThat(en.money(Money.ofCentavos(-150))).isEqualTo("MZN -1.50");
        assertThat(pt.centavos(100L)).isEqualTo("1,00 MT");
        assertThat(pt.money(null)).isEqualTo("—");
        assertThat(pt.centavos(null)).isEqualTo("—");
    }

    @Test
    void datesAreInMaputo() {
        assertThat(pt.date(LocalDate.of(2027, 1, 9))).isEqualTo("09/01/2027");
        assertThat(en.date(LocalDate.of(2027, 1, 9))).isEqualTo("09 Jan 2027");
        // 23:30 UTC de 9 de Janeiro = 01:30 de 10 de Janeiro em Maputo (UTC+2).
        Instant lateEvening = Instant.parse("2027-01-09T23:30:00Z");
        assertThat(pt.date(lateEvening)).isEqualTo("10/01/2027");
        assertThat(pt.dateTime(Instant.parse("2027-01-09T12:05:00Z"))).isEqualTo("09/01/2027 14:05");
        assertThat(en.dateTime(Instant.parse("2027-01-09T12:05:00Z"))).isEqualTo("09 Jan 2027 14:05");
        assertThat(pt.date("2027-01-09")).isEqualTo("09/01/2027");
        assertThat(pt.date(null)).isEqualTo("—");
        assertThat(pt.date(" ")).isEqualTo("—");
        assertThat(pt.dateTime(null)).isEqualTo("—");
        assertThatThrownBy(() -> pt.date(42)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void percentAndText() {
        assertThat(pt.percent(new BigDecimal("5"))).isEqualTo("5,00" + NBSP + "%");
        assertThat(en.percent(new BigDecimal("12.5"))).isEqualTo("12.50%");
        assertThat(pt.percent(null)).isEqualTo("—");
        assertThat(pt.text(null)).isEqualTo("—");
        assertThat(pt.text("  ")).isEqualTo("—");
        assertThat(pt.text(12)).isEqualTo("12");
        assertThat(pt.language()).isEqualTo("pt");
        assertThat(en.language()).isEqualTo("en");
        assertThat(new PdfFormatter(null).language()).isEqualTo("pt");
        assertThat(new PdfFormatter(Locale.FRENCH).language()).isEqualTo("pt");
    }
}
