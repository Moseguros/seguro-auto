package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/**
 * As quatro regras da política de palavras-passe (S1-04 §4) e a lista das mais comuns.
 */
class PasswordPolicyTest {

    static final String EMAIL = "ana.machava@seguradora.co.mz";

    private final PasswordPolicy policy = new PasswordPolicy();

    @Test
    void acceptsAGoodPassword() {
        assertThat(policy.violations("Cajueiro-Marginal-2026", EMAIL, "password")).isEmpty();
    }

    @Test
    void tooShort() {
        assertThat(codes(policy.violations("Xy7#kq!Lm2p", EMAIL, "password"))).containsExactly("TOO_SHORT");
        assertThat(policy.violations("Xy7#kq!Lm2pZ", EMAIL, "password")).isEmpty();
        assertThat(codes(policy.violations(null, EMAIL, "password"))).containsExactly("TOO_SHORT");
    }

    @Test
    void tooLong() {
        assertThat(policy.violations("a1B!".repeat(32), EMAIL, "password")).isEmpty();
        assertThat(codes(policy.violations("a1B!".repeat(32) + "x", EMAIL, "password"))).containsExactly("TOO_LONG");
    }

    @Test
    void containsTheLocalPartOfTheEmailIgnoringCase() {
        assertThat(codes(policy.violations("xx-ANA.Machava-2026", EMAIL, "password")))
                .containsExactly("CONTAINS_EMAIL");
    }

    @Test
    void shortLocalPartsAreNotChecked() {
        assertThat(policy.violations("ana-e-o-cajueiro-2026", "ana@seguradora.co.mz", "password")).isEmpty();
        assertThat(codes(policy.violations("o-joao-e-o-cajueiro", "joao@seguradora.co.mz", "password")))
                .containsExactly("CONTAINS_EMAIL");
    }

    @Test
    void tooCommonIgnoringCase() {
        // "unbelievable" e "masterbating" estão na lista e têm 12 caracteres
        assertThat(codes(policy.violations("UnBelievable", EMAIL, "password"))).containsExactly("TOO_COMMON");
        assertThat(codes(policy.violations("password", EMAIL, "password")))
                .containsExactly("TOO_SHORT", "TOO_COMMON");
    }

    @Test
    void theListHasTheTenThousandPasswordsWithoutTheComments() {
        assertThat(PasswordPolicy.loadCommonPasswords())
                .hasSizeGreaterThan(9_900)
                .contains("password", "123456", "qwerty")
                .noneMatch(p -> p.startsWith("#"));
    }

    @Test
    void checkThrowsPasswordTooWeakWithTheErrorsOfTheField() {
        assertThatThrownBy(() -> policy.check("curta", EMAIL, "newPassword"))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("PASSWORD_TOO_WEAK");
                    assertThat(e.getHttpStatus()).isEqualTo(400);
                    @SuppressWarnings("unchecked")
                    List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
                    assertThat(errors).extracting(FieldError::field).containsOnly("newPassword");
                    assertThat(codes(errors)).containsExactly("TOO_SHORT");
                    assertThat(e.getMessage()).doesNotContain("curta");
                });
    }

    private static List<String> codes(List<FieldError> errors) {
        return errors.stream().map(FieldError::code).toList();
    }
}
