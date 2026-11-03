package mz.co.moseguros.shared.domain;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.stereotype.Component;

/**
 * Cria {@link Matricula} a partir de texto livre, com os formatos activos do
 * {@link PlateFormatProvider}.
 */
@Component
public class PlateParser {

    /**
     * Comprimento máximo do valor normalizado (S2-02): acima dele a matrícula é recusada sem
     * aplicar nenhum padrão, o que protege contra expressões lentas configuradas na BD.
     */
    public static final int MAX_NORMALIZED_LENGTH = 15;

    private static final Pattern SEPARATORS = Pattern.compile("[\\s-]");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9]*)}");

    private final PlateFormatProvider formats;

    public PlateParser(PlateFormatProvider formats) {
        this.formats = formats;
    }

    /**
     * Normaliza (maiúsculas, sem espaços nem hífens), testa os formatos activos por ordem
     * e devolve a matrícula formatada pelo primeiro que corresponder.
     *
     * @throws DomainException {@code INVALID_PLATE} se nenhum formato corresponder ou se o valor
     *                         normalizado tiver mais de {@value #MAX_NORMALIZED_LENGTH} caracteres
     */
    public Matricula parse(String raw) {
        if (raw == null) {
            throw invalid();
        }
        String normalized = SEPARATORS.matcher(raw).replaceAll("").toUpperCase(Locale.ROOT);
        if (normalized.length() > MAX_NORMALIZED_LENGTH) {
            throw invalid();
        }
        for (PlateFormat format : formats.activeFormats()) {
            if (!format.active()) {
                continue;
            }
            Matcher m = format.pattern().matcher(normalized);
            if (m.matches()) {
                return new Matricula(render(format.displayTemplate(), m), format.code());
            }
        }
        throw invalid();
    }

    /** Substitui cada {@code {grupo}} do modelo pelo valor do grupo nomeado. */
    static String render(String template, Matcher groups) {
        Matcher placeholder = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder();
        while (placeholder.find()) {
            String value = groups.group(placeholder.group(1));
            placeholder.appendReplacement(out, Matcher.quoteReplacement(value == null ? "" : value));
        }
        placeholder.appendTail(out);
        return out.toString();
    }

    private static DomainException invalid() {
        return DomainException.badRequest(ErrorCodes.INVALID_PLATE, "Plate does not match any active format");
    }
}
