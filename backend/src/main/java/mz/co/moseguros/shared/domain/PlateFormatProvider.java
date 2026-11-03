package mz.co.moseguros.shared.domain;

import java.util.List;

/**
 * Fonte dos formatos de matrícula aceites. Desde o S2-02 vem da tabela
 * {@code catalog.plate_format} ({@code DbPlateFormatProvider}, módulo {@code catalog}).
 */
public interface PlateFormatProvider {

    /** Formatos activos, pela ordem em que devem ser testados. */
    List<PlateFormat> activeFormats();
}
