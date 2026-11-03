package mz.co.moseguros.catalog.application;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.PlateFormat;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;

/**
 * Regras de um formato de matrícula (S2-02 §4). Cada falha é um erro de campo em
 * {@code VALIDATION_ERROR}; a validação pára no primeiro campo inválido, pela ordem da tabela da
 * ficha. O exemplo é verificado com o próprio {@link PlateParser}, para que a regra seja
 * exactamente a dos pedidos.
 */
public final class PlateFormatValidator {

    static final Pattern CODE = Pattern.compile("^[A-Z0-9_]{2,20}$");
    static final int LABEL_MIN = 2;
    static final int LABEL_MAX = 80;
    static final int PATTERN_MAX = 200;
    static final int SORT_ORDER_MAX = 999;

    /** Caracteres permitidos no modelo: chavetas, letras, dígitos, espaço e hífen. */
    private static final Pattern TEMPLATE_CHARS = Pattern.compile("^[{}A-Za-z0-9 \\-]*$");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9]*)}");
    /** Abertura de um grupo nomeado: {@code (?<nome>} (não apanha {@code (?<=} nem {@code (?<!}). */
    private static final Pattern NAMED_GROUP = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

    private PlateFormatValidator() {
    }

    /**
     * Valida um formato completo.
     *
     * @param code {@code null} numa alteração (o código não muda e não é validado)
     * @throws FieldValidationException no primeiro campo inválido
     */
    public static void validate(String code, String label, String pattern, String displayTemplate, String example,
                                Integer sortOrder) {
        if (code != null && !CODE.matcher(code).matches()) {
            throw error("code", FieldErrorCodes.INVALID_CODE, "code must match " + CODE.pattern());
        }
        if (label == null || label.trim().length() < LABEL_MIN || label.trim().length() > LABEL_MAX) {
            throw error("label", FieldErrorCodes.OUT_OF_RANGE,
                    "label must have between " + LABEL_MIN + " and " + LABEL_MAX + " characters");
        }
        Pattern compiled = validateExpression(pattern, displayTemplate);
        if (example == null || !example.equals(render(compiled, displayTemplate, example).orElse(null))) {
            throw error("example", FieldErrorCodes.EXAMPLE_DOES_NOT_MATCH,
                    "example does not match the format or is formatted differently");
        }
        if (sortOrder == null || sortOrder < 0 || sortOrder > SORT_ORDER_MAX) {
            throw error("sortOrder", FieldErrorCodes.OUT_OF_RANGE,
                    "sortOrder must be between 0 and " + SORT_ORDER_MAX);
        }
    }

    /**
     * Valida e compila o padrão e o modelo (as regras de {@code pattern} e {@code displayTemplate}).
     *
     * @throws FieldValidationException em {@code pattern} ou {@code displayTemplate}
     */
    public static Pattern validateExpression(String pattern, String displayTemplate) {
        if (pattern == null || pattern.isEmpty() || pattern.length() > PATTERN_MAX) {
            throw error("pattern", FieldErrorCodes.OUT_OF_RANGE,
                    "pattern must have between 1 and " + PATTERN_MAX + " characters");
        }
        if (!pattern.startsWith("^") || !pattern.endsWith("$") || pattern.endsWith("\\$")) {
            throw error("pattern", FieldErrorCodes.PATTERN_NOT_ANCHORED, "pattern must start with ^ and end with $");
        }
        Pattern compiled;
        try {
            compiled = Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            throw error("pattern", FieldErrorCodes.INVALID_PATTERN, "pattern does not compile");
        }
        Set<String> groups = namedGroups(pattern);
        if (groups.isEmpty()) {
            throw error("pattern", FieldErrorCodes.PATTERN_WITHOUT_GROUPS, "pattern needs at least one named group");
        }
        if (displayTemplate == null || displayTemplate.isEmpty() || !TEMPLATE_CHARS.matcher(displayTemplate).matches()) {
            throw templateError();
        }
        Matcher placeholder = PLACEHOLDER.matcher(displayTemplate);
        StringBuilder rest = new StringBuilder();
        while (placeholder.find()) {
            if (!groups.contains(placeholder.group(1))) {
                throw templateError();
            }
            placeholder.appendReplacement(rest, "");
        }
        placeholder.appendTail(rest);
        if (rest.indexOf("{") >= 0 || rest.indexOf("}") >= 0) {
            throw templateError();
        }
        return compiled;
    }

    /**
     * Aplica um formato (já validado) a {@code value} como faz o {@link PlateParser}: normaliza,
     * recusa mais de {@value PlateParser#MAX_NORMALIZED_LENGTH} caracteres, testa o padrão e
     * formata com o modelo.
     *
     * @return o valor formatado, ou vazio se não corresponder
     */
    public static Optional<String> render(Pattern pattern, String displayTemplate, String value) {
        PlateFormat format = new PlateFormat("TEST", "", pattern, displayTemplate, true);
        try {
            Matricula m = new PlateParser(() -> List.of(format)).parse(value);
            return Optional.of(m.value());
        } catch (DomainException e) {
            return Optional.empty();
        }
    }

    /** Nomes dos grupos nomeados, ignorando {@code \(} escapado. */
    static Set<String> namedGroups(String pattern) {
        Set<String> names = new LinkedHashSet<>();
        Matcher m = NAMED_GROUP.matcher(pattern);
        while (m.find()) {
            int backslashes = 0;
            for (int i = m.start() - 1; i >= 0 && pattern.charAt(i) == '\\'; i--) {
                backslashes++;
            }
            if (backslashes % 2 == 0) {
                names.add(m.group(1));
            }
        }
        return names;
    }

    private static FieldValidationException templateError() {
        return error("displayTemplate", FieldErrorCodes.TEMPLATE_GROUP_UNKNOWN,
                "displayTemplate must use only named groups of the pattern, letters, digits, space and -");
    }

    private static FieldValidationException error(String field, String code, String detail) {
        return new FieldValidationException(field, code, detail);
    }
}
