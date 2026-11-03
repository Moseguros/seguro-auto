package mz.co.moseguros.shared.domain;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Formatos de matrícula fixos para testes unitários, sem base de dados. Em execução os formatos
 * vêm de {@code catalog.plate_format} ({@code DbPlateFormatProvider}, S2-02); {@link #STANDARD} e
 * {@link #LEGACY} são os mesmos da migração {@code V202612010201}.
 */
public final class StaticPlateFormatProvider implements PlateFormatProvider {

    public static final PlateFormat STANDARD = new PlateFormat("STANDARD", "Actual (ABC-123-MC)",
            Pattern.compile("^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$"), "{l}-{n}-{p}", true);
    public static final PlateFormat LEGACY = new PlateFormat("LEGACY", "Antigo (ABC-12-34)",
            Pattern.compile("^(?<l>[A-Z]{3})(?<a>\\d{2})(?<b>\\d{2})$"), "{l}-{a}-{b}", true);

    private final List<PlateFormat> formats;

    private StaticPlateFormatProvider(List<PlateFormat> formats) {
        this.formats = List.copyOf(formats);
    }

    /** {@link #STANDARD} e {@link #LEGACY}, por esta ordem. */
    public static StaticPlateFormatProvider defaults() {
        return new StaticPlateFormatProvider(List.of(STANDARD, LEGACY));
    }

    /** Os formatos indicados, pela ordem dada. */
    public static StaticPlateFormatProvider of(PlateFormat... formats) {
        return new StaticPlateFormatProvider(List.of(formats));
    }

    /** {@link PlateParser} com {@link #defaults()}. */
    public static PlateParser defaultParser() {
        return new PlateParser(defaults());
    }

    @Override
    public List<PlateFormat> activeFormats() {
        return formats;
    }
}
