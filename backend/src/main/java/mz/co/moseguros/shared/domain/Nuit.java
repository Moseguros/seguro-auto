package mz.co.moseguros.shared.domain;

import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Número Único de Identificação Tributária: exactamente 9 dígitos, sem dígito de
 * controlo (decisão de 31/10/2026). Os espaços são ignorados.
 *
 * <p>JSON: string com os 9 dígitos.
 */
public record Nuit(String value) {

    private static final Pattern NINE_DIGITS = Pattern.compile("^\\d{9}$");
    private static final Pattern SPACES = Pattern.compile("\\s");

    public Nuit {
        if (value == null) {
            throw invalid();
        }
        value = SPACES.matcher(value).replaceAll("");
        if (!NINE_DIGITS.matcher(value).matches()) {
            throw invalid();
        }
    }

    public static Nuit of(String raw) {
        return new Nuit(raw);
    }

    /** {@code *****6789}. */
    public String masked() {
        return "*****" + value.substring(5);
    }

    /** Mascarado, para nunca ir completo para um log por engano. */
    @Override
    public String toString() {
        return masked();
    }

    private static DomainException invalid() {
        return DomainException.badRequest(ErrorCodes.INVALID_NUIT, "NUIT must have exactly 9 digits");
    }
}
