package mz.co.moseguros.shared.domain;

import java.util.List;

/**
 * Fonte dos formatos de matrícula aceites. No Sprint 0 vem do {@code application.yml}
 * ({@code ConfigPlateFormatProvider}); no Sprint 2 passa a vir da tabela
 * {@code catalog.plate_format}.
 */
public interface PlateFormatProvider {

    /** Formatos activos, pela ordem em que devem ser testados. */
    List<PlateFormat> activeFormats();
}
