package mz.co.moseguros.shared.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Formatos de matrícula em {@code moseguros.plates.formats}, pela ordem em que são testados.
 */
@ConfigurationProperties(prefix = "moseguros.plates")
public record PlateFormatsProperties(List<Format> formats) {

    public PlateFormatsProperties {
        formats = formats == null ? List.of() : List.copyOf(formats);
    }

    /**
     * @param active omitido = activo
     */
    public record Format(String code, String label, String pattern, String displayTemplate, Boolean active) {
    }
}
