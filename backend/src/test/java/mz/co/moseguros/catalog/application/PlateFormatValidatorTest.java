package mz.co.moseguros.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.FieldValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Cada linha da tabela de validação da ficha S2-02 §4, com um caso válido e um inválido.
 */
class PlateFormatValidatorTest {

    private static final String CODE = "STANDARD";
    private static final String LABEL = "Actual (ABC-123-MC)";
    private static final String PATTERN = "^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$";
    private static final String TEMPLATE = "{l}-{n}-{p}";
    private static final String EXAMPLE = "ABC-123-MC";
    private static final int SORT = 10;

    @Test
    void validFormatPasses() {
        assertThatCode(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, EXAMPLE, SORT))
                .doesNotThrowAnyException();
        // na alteração o código não é validado
        assertThatCode(() -> PlateFormatValidator.validate(null, LABEL, PATTERN, TEMPLATE, EXAMPLE, SORT))
                .doesNotThrowAnyException();
    }

    // code fora de ^[A-Z0-9_]{2,20}$ → INVALID_CODE

    @ParameterizedTest
    @ValueSource(strings = {"AB", "TEST_2", "A1234567890123456789"})
    void validCodes(String code) {
        assertThatCode(() -> PlateFormatValidator.validate(code, LABEL, PATTERN, TEMPLATE, EXAMPLE, SORT))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "standard", "STAN-DARD", "A12345678901234567890", ""})
    void invalidCodes(String code) {
        assertError(() -> PlateFormatValidator.validate(code, LABEL, PATTERN, TEMPLATE, EXAMPLE, SORT),
                "code", "INVALID_CODE");
    }

    // label com menos de 2 ou mais de 80 caracteres → OUT_OF_RANGE

    @Test
    void labelLength() {
        assertThatCode(() -> PlateFormatValidator.validate(CODE, "AB", PATTERN, TEMPLATE, EXAMPLE, SORT))
                .doesNotThrowAnyException();
        assertThatCode(() -> PlateFormatValidator.validate(CODE, "x".repeat(80), PATTERN, TEMPLATE, EXAMPLE, SORT))
                .doesNotThrowAnyException();
        assertError(() -> PlateFormatValidator.validate(CODE, "A", PATTERN, TEMPLATE, EXAMPLE, SORT),
                "label", "OUT_OF_RANGE");
        assertError(() -> PlateFormatValidator.validate(CODE, "x".repeat(81), PATTERN, TEMPLATE, EXAMPLE, SORT),
                "label", "OUT_OF_RANGE");
        assertError(() -> PlateFormatValidator.validate(CODE, null, PATTERN, TEMPLATE, EXAMPLE, SORT),
                "label", "OUT_OF_RANGE");
    }

    // pattern com mais de 200 caracteres (OUT_OF_RANGE), ou sem ^ ou $ (PATTERN_NOT_ANCHORED)

    @Test
    void patternLength() {
        String longest = "^(?<a>" + "A".repeat(192) + ")$";
        assertThat(longest).hasSize(200);
        assertThatCode(() -> PlateFormatValidator.validateExpression(longest, "{a}")).doesNotThrowAnyException();
        String tooLong = "^(?<a>" + "A".repeat(193) + ")$";
        assertError(() -> PlateFormatValidator.validateExpression(tooLong, "{a}"), "pattern", "OUT_OF_RANGE");
    }

    @ParameterizedTest
    @ValueSource(strings = {"(?<l>[A-Z]{3})$", "^(?<l>[A-Z]{3})", "(?<l>[A-Z]{3})", "^(?<l>[A-Z]{3})\\$"})
    void patternMustBeAnchored(String pattern) {
        assertError(() -> PlateFormatValidator.validateExpression(pattern, "{l}"), "pattern", "PATTERN_NOT_ANCHORED");
    }

    @Test
    void anchoredPatternPasses() {
        assertThatCode(() -> PlateFormatValidator.validateExpression("^(?<l>[A-Z]{3})$", "{l}"))
                .doesNotThrowAnyException();
    }

    // pattern não compila → INVALID_PATTERN

    @Test
    void patternMustCompile() {
        assertError(() -> PlateFormatValidator.validateExpression("^(?<l>[A-Z]{3}$", "{l}"),
                "pattern", "INVALID_PATTERN");
        assertError(() -> PlateFormatValidator.validateExpression("^(?<l>[A-Z]{3,1})$", "{l}"),
                "pattern", "INVALID_PATTERN");
        Pattern compiled = PlateFormatValidator.validateExpression(PATTERN, TEMPLATE);
        assertThat(compiled.matcher("ABC123MC").matches()).isTrue();
    }

    // pattern sem nenhum grupo nomeado → PATTERN_WITHOUT_GROUPS

    @Test
    void patternNeedsANamedGroup() {
        assertError(() -> PlateFormatValidator.validateExpression("^([A-Z]{3})(\\d{3})$", "ABC"),
                "pattern", "PATTERN_WITHOUT_GROUPS");
        // um "(?<" escapado ou um lookbehind não são grupos nomeados
        assertError(() -> PlateFormatValidator.validateExpression("^\\(?<l>[A-Z]{3}(?<=A)$", "{l}"),
                "pattern", "PATTERN_WITHOUT_GROUPS");
        assertThatCode(() -> PlateFormatValidator.validateExpression("^(?<l>[A-Z]{3})(\\d{3})$", "{l}"))
                .doesNotThrowAnyException();
    }

    // displayTemplate com {x} que não é grupo, ou com caracteres não permitidos → TEMPLATE_GROUP_UNKNOWN

    @ParameterizedTest
    @ValueSource(strings = {"{l}-{n}-{p}", "{l} {n} {p}", "{l}{n}{p}", "MZ {l}-{n}", "{p}"})
    void validTemplates(String template) {
        assertThatCode(() -> PlateFormatValidator.validateExpression(PATTERN, template)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{l}-{x}-{p}", "{l}/{n}/{p}", "{l}_{n}", "{l}-{n", "{l}-n}", "{}", "${l}", "{l}.{n}",
        ""})
    void invalidTemplates(String template) {
        assertError(() -> PlateFormatValidator.validateExpression(PATTERN, template),
                "displayTemplate", "TEMPLATE_GROUP_UNKNOWN");
    }

    // example normalizado não corresponde, ou o formatado é diferente → EXAMPLE_DOES_NOT_MATCH

    @Test
    void exampleMustMatchAndBeFormatted() {
        assertThatCode(() -> PlateFormatValidator.validate("TEST2", "Teste", "^(?<l>[A-Z]{2})(?<n>\\d{4})$",
                "{l}-{n}", "AB-1234", SORT)).doesNotThrowAnyException();
        // não corresponde ao padrão
        assertError(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, "AB-1234", SORT),
                "example", "EXAMPLE_DOES_NOT_MATCH");
        // corresponde, mas formatado dá outro valor
        assertError(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, "ABC 123 MC", SORT),
                "example", "EXAMPLE_DOES_NOT_MATCH");
        assertError(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, "abc-123-mc", SORT),
                "example", "EXAMPLE_DOES_NOT_MATCH");
    }

    // sortOrder fora de 0–999 → OUT_OF_RANGE

    @Test
    void sortOrderRange() {
        assertThatCode(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, EXAMPLE, 0))
                .doesNotThrowAnyException();
        assertThatCode(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, EXAMPLE, 999))
                .doesNotThrowAnyException();
        assertError(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, EXAMPLE, -1),
                "sortOrder", "OUT_OF_RANGE");
        assertError(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, EXAMPLE, 1000),
                "sortOrder", "OUT_OF_RANGE");
        assertError(() -> PlateFormatValidator.validate(CODE, LABEL, PATTERN, TEMPLATE, EXAMPLE, null),
                "sortOrder", "OUT_OF_RANGE");
    }

    @Test
    void renderFormatsLikeThePlateParser() {
        Pattern pattern = Pattern.compile("^(?<l>[A-Z]{2})(?<n>\\d{4})$");
        assertThat(PlateFormatValidator.render(pattern, "{l}-{n}", "ab 1234")).contains("AB-1234");
        assertThat(PlateFormatValidator.render(pattern, "{l}-{n}", "abc 1234")).isEmpty();
        Pattern any = Pattern.compile("^(?<a>[A-Z0-9]+)$");
        assertThat(PlateFormatValidator.render(any, "{a}", "A".repeat(15))).isPresent();
        assertThat(PlateFormatValidator.render(any, "{a}", "A".repeat(16))).isEmpty();
    }

    private static void assertError(org.assertj.core.api.ThrowableAssert.ThrowingCallable call, String field,
                                    String code) {
        assertThatThrownBy(call)
                .isInstanceOfSatisfying(FieldValidationException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
                    assertThat(e.getField()).isEqualTo(field);
                    assertThat(e.getFieldCode()).isEqualTo(code);
                });
    }
}
