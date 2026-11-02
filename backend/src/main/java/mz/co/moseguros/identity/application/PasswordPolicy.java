package mz.co.moseguros.identity.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.springframework.stereotype.Component;

/**
 * Política de palavras-passe do staff (S1-04 §4): 12 a 128 caracteres, sem a parte local do
 * e-mail (se tiver 4 ou mais caracteres) e fora da lista das 10 000 mais comuns
 * ({@code identity/common-passwords.txt}); as duas últimas sem diferenciar maiúsculas.
 *
 * <p>Falha → 400 {@code PASSWORD_TOO_WEAK} com um erro por regra em {@code errors[]} (campo
 * {@code password}, ou {@code newPassword} no S1-05). Nunca regista a palavra-passe.
 */
@Component
public class PasswordPolicy {

    public static final int MIN_LENGTH = 12;
    public static final int MAX_LENGTH = 128;
    static final int MIN_EMAIL_LOCAL_PART = 4;
    static final String COMMON_PASSWORDS = "identity/common-passwords.txt";

    private final Set<String> common;

    public PasswordPolicy() {
        this(loadCommonPasswords());
    }

    PasswordPolicy(Set<String> commonPasswords) {
        this.common = Set.copyOf(commonPasswords);
    }

    /** @throws DomainException {@code PASSWORD_TOO_WEAK} com os motivos em {@code errors[]} */
    public void check(String password, String email, String field) {
        List<FieldError> errors = violations(password, email, field);
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.PASSWORD_TOO_WEAK, "The password does not follow the password policy")
                    .withProperty("errors", errors);
        }
    }

    /** Regras violadas (vazio = aceite). */
    public List<FieldError> violations(String password, String email, String field) {
        String value = password == null ? "" : password;
        int length = value.codePointCount(0, value.length());
        List<FieldError> errors = new ArrayList<>();
        if (length < MIN_LENGTH) {
            errors.add(new FieldError(field, FieldErrorCodes.TOO_SHORT,
                    "Must have at least " + MIN_LENGTH + " characters"));
        }
        if (length > MAX_LENGTH) {
            errors.add(new FieldError(field, FieldErrorCodes.TOO_LONG,
                    "Must have at most " + MAX_LENGTH + " characters"));
        }
        String lower = value.toLowerCase(Locale.ROOT);
        String localPart = localPart(email);
        if (localPart != null && localPart.length() >= MIN_EMAIL_LOCAL_PART && lower.contains(localPart)) {
            errors.add(new FieldError(field, FieldErrorCodes.CONTAINS_EMAIL, "Must not contain the e-mail address"));
        }
        if (common.contains(lower)) {
            errors.add(new FieldError(field, FieldErrorCodes.TOO_COMMON, "Is one of the most common passwords"));
        }
        return errors;
    }

    private static String localPart(String email) {
        if (email == null) {
            return null;
        }
        int at = email.indexOf('@');
        return (at < 0 ? email : email.substring(0, at)).toLowerCase(Locale.ROOT);
    }

    static Set<String> loadCommonPasswords() {
        InputStream in = PasswordPolicy.class.getClassLoader().getResourceAsStream(COMMON_PASSWORDS);
        if (in == null) {
            throw new IllegalStateException(COMMON_PASSWORDS + " is missing from the classpath");
        }
        Set<String> passwords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && !line.startsWith("#")) {
                    passwords.add(line.toLowerCase(Locale.ROOT));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return passwords;
    }
}
