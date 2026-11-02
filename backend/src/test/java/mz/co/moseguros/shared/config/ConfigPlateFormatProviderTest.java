package mz.co.moseguros.shared.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Os formatos de matrícula vêm só da configuração: acrescentar um formato não exige código.
 */
class ConfigPlateFormatProviderTest {

    private static final String[] DEFAULT_FORMATS = {
        "moseguros.plates.formats[0].code=STANDARD",
        "moseguros.plates.formats[0].label=Actual (ABC-123-MC)",
        "moseguros.plates.formats[0].pattern=^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$",
        "moseguros.plates.formats[0].display-template={l}-{n}-{p}",
        "moseguros.plates.formats[1].code=LEGACY",
        "moseguros.plates.formats[1].label=Antigo (ABC-12-34)",
        "moseguros.plates.formats[1].pattern=^(?<l>[A-Z]{3})(?<a>\\d{2})(?<b>\\d{2})$",
        "moseguros.plates.formats[1].display-template={l}-{a}-{b}"
    };

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ConfigPlateFormatProvider.class, PlateParser.class)
            .withPropertyValues(DEFAULT_FORMATS);

    @Test
    void defaultFormatsRejectTwoLettersFourDigits() {
        runner.run(ctx -> {
            PlateParser parser = ctx.getBean(PlateParser.class);
            assertThat(parser.parse("ABC 123 MC").value()).isEqualTo("ABC-123-MC");
            assertThatThrownBy(() -> parser.parse("AB-1234")).isInstanceOf(DomainException.class);
        });
    }

    @Test
    void newFormatIsAcceptedOnlyByConfiguration() {
        runner.withPropertyValues(
                        "moseguros.plates.formats[2].code=DIPLOMATIC",
                        "moseguros.plates.formats[2].label=Novo (AB-1234)",
                        "moseguros.plates.formats[2].pattern=^(?<l>[A-Z]{2})(?<n>\\d{4})$",
                        "moseguros.plates.formats[2].display-template={l} {n}")
                .run(ctx -> {
                    Matricula m = ctx.getBean(PlateParser.class).parse("ab-1234");
                    assertThat(m.value()).isEqualTo("AB 1234");
                    assertThat(m.formatCode()).isEqualTo("DIPLOMATIC");
                });
    }

    @Test
    void inactiveFormatIsNotAccepted() {
        runner.withPropertyValues("moseguros.plates.formats[1].active=false")
                .run(ctx -> {
                    PlateParser parser = ctx.getBean(PlateParser.class);
                    assertThatThrownBy(() -> parser.parse("ABC1234")).isInstanceOf(DomainException.class);
                    assertThat(ctx.getBean(ConfigPlateFormatProvider.class).activeFormats()).hasSize(1);
                });
    }

    @Test
    void invalidPatternPreventsStartup() {
        runner.withPropertyValues("moseguros.plates.formats[1].pattern=^(?<l>[A-Z]{3}$")
                .run(ctx -> assertThat(ctx).hasFailed());
    }
}
