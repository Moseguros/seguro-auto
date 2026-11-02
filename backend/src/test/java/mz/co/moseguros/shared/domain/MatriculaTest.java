package mz.co.moseguros.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MatriculaTest {

    static final PlateFormat STANDARD = new PlateFormat("STANDARD", "Actual (ABC-123-MC)",
            Pattern.compile("^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$"), "{l}-{n}-{p}", true);
    static final PlateFormat LEGACY = new PlateFormat("LEGACY", "Antigo (ABC-12-34)",
            Pattern.compile("^(?<l>[A-Z]{3})(?<a>\\d{2})(?<b>\\d{2})$"), "{l}-{a}-{b}", true);

    private final PlateParser parser = new PlateParser(() -> List.of(STANDARD, LEGACY));

    @ParameterizedTest
    @CsvSource({
        "ABC-123-MC,ABC-123-MC,STANDARD",
        "abc123mc,ABC-123-MC,STANDARD",
        "'AAA 001 MP',AAA-001-MP,STANDARD",
        "' mmm-999 - zz ',MMM-999-ZZ,STANDARD",
        "ABC-12-34,ABC-12-34,LEGACY",
        "mlb1234,MLB-12-34,LEGACY",
        "'XYZ 00 01',XYZ-00-01,LEGACY"
    })
    void acceptsConfiguredFormats(String raw, String value, String format) {
        Matricula m = parser.parse(raw);
        assertThat(m.value()).isEqualTo(value);
        assertThat(m.formatCode()).isEqualTo(format);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "AB-123-MC", "ABC-1234-MC", "ABC-123-M", "123-ABC-MC", "ABC_123_MC", "AB-1234", "ÁBC-123-MC"})
    void rejectsUnknownFormats(String raw) {
        assertThatThrownBy(() -> parser.parse(raw))
                .isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode())
                .isEqualTo("INVALID_PLATE");
    }

    @Test
    void firstMatchingFormatWins() {
        PlateFormat broad = new PlateFormat("BROAD", "Tudo",
                Pattern.compile("^(?<all>[A-Z0-9]+)$"), "{all}", true);
        PlateParser ordered = new PlateParser(() -> List.of(STANDARD, broad));
        assertThat(ordered.parse("ABC123MC").formatCode()).isEqualTo("STANDARD");
        assertThat(ordered.parse("ZZ9").formatCode()).isEqualTo("BROAD");
    }

    @Test
    void inactiveFormatsAreIgnored() {
        PlateFormat inactive = new PlateFormat("LEGACY", "Antigo",
                LEGACY.pattern(), LEGACY.displayTemplate(), false);
        PlateParser p = new PlateParser(() -> List.of(STANDARD, inactive));
        assertThatThrownBy(() -> p.parse("ABC1234")).isInstanceOf(DomainException.class);
    }

    @Test
    void masksEveryDigit() {
        assertThat(parser.parse("ABC123MC").masked()).isEqualTo("ABC-***-MC");
        assertThat(parser.parse("ABC1234").masked()).isEqualTo("ABC-**-**");
    }

    @Test
    void restoreDoesNotValidateAgain() {
        Matricula old = Matricula.restore("OLD-FORMAT-1", "RETIRED");
        assertThat(old.value()).isEqualTo("OLD-FORMAT-1");
        assertThat(old.formatCode()).isEqualTo("RETIRED");
    }
}
