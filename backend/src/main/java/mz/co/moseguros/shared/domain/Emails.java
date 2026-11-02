package mz.co.moseguros.shared.domain;

import java.util.Locale;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Normalização e validação de endereços de e-mail: minúsculas, sem espaços,
 * {@code ^[^@\s]+@[^@\s]+\.[^@\s]+$} e no máximo 254 caracteres. Inválido →
 * {@code INVALID_EMAIL} (400).
 */
public final class Emails {

    public static final int MAX_LENGTH = 254;

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern SPACES = Pattern.compile("\\s");

    private Emails() {
    }

    /** E-mail normalizado; {@code INVALID_EMAIL} se for nulo ou inválido. */
    public static String normalize(String raw) {
        if (raw == null) {
            throw invalid();
        }
        String email = SPACES.matcher(raw).replaceAll("").toLowerCase(Locale.ROOT);
        if (email.length() > MAX_LENGTH || !FORMAT.matcher(email).matches()) {
            throw invalid();
        }
        return email;
    }

    /** Como {@link #normalize(String)}, mas {@code null} ou em branco → {@code null}. */
    public static String normalizeOrNull(String raw) {
        return raw == null || raw.isBlank() ? null : normalize(raw);
    }

    public static boolean isValid(String raw) {
        try {
            normalize(raw);
            return true;
        } catch (DomainException e) {
            return false;
        }
    }

    private static DomainException invalid() {
        // Sem o valor no detalhe: é um dado pessoal.
        return DomainException.badRequest(ErrorCodes.INVALID_EMAIL, "Invalid e-mail address");
    }
}
