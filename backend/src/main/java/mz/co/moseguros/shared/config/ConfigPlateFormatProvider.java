package mz.co.moseguros.shared.config;

import java.util.List;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.domain.PlateFormat;
import mz.co.moseguros.shared.domain.PlateFormatProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Formatos de matrícula lidos do {@code application.yml} ({@code moseguros.plates.formats}).
 * As expressões são compiladas no arranque: um formato mal escrito impede a aplicação de arrancar.
 *
 * <p>No Sprint 2 é substituído por uma implementação que lê {@code catalog.plate_format}.
 */
@Component
@EnableConfigurationProperties(PlateFormatsProperties.class)
public class ConfigPlateFormatProvider implements PlateFormatProvider {

    private final List<PlateFormat> active;

    public ConfigPlateFormatProvider(PlateFormatsProperties properties) {
        this.active = properties.formats().stream()
                .map(ConfigPlateFormatProvider::toFormat)
                .filter(PlateFormat::active)
                .toList();
    }

    @Override
    public List<PlateFormat> activeFormats() {
        return active;
    }

    private static PlateFormat toFormat(PlateFormatsProperties.Format f) {
        if (f.code() == null || f.pattern() == null || f.displayTemplate() == null) {
            throw new IllegalStateException(
                    "moseguros.plates.formats: code, pattern and display-template are required (" + f.code() + ")");
        }
        boolean active = f.active() == null || f.active();
        return new PlateFormat(f.code(), f.label(), Pattern.compile(f.pattern()), f.displayTemplate(), active);
    }
}
