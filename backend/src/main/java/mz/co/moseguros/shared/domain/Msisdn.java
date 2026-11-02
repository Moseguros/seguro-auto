package mz.co.moseguros.shared.domain;

import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Número de telemóvel moçambicano, normalizado para {@code 258} + 9 dígitos
 * (ex.: {@code 258841234567}).
 *
 * <p>Aceita {@code +258841234567}, {@code 258841234567} ou {@code 841234567}, com
 * espaços ou hífens. O número nacional começa por 82–87.
 *
 * <p>JSON: string E.164 ({@code +258841234567}).
 */
public record Msisdn(String value) {

    private static final String COUNTRY_CODE = "258";
    private static final Pattern NATIONAL = Pattern.compile("^8[2-7]\\d{7}$");
    private static final Pattern SEPARATORS = Pattern.compile("[\\s-]");

    public Msisdn {
        value = normalize(value);
    }

    public static Msisdn of(String raw) {
        return new Msisdn(raw);
    }

    /** {@code +258841234567}. */
    public String e164() {
        return "+" + value;
    }

    /** {@code 841234567}. */
    public String national() {
        return value.substring(COUNTRY_CODE.length());
    }

    /**
     * {@code +258*****4567}: a mesma máscara do {@code SensitiveDataMasker}
     * (últimos 4 dígitos visíveis).
     */
    public String masked() {
        return "+" + COUNTRY_CODE + "*****" + value.substring(value.length() - 4);
    }

    /** Mascarado, para nunca ir completo para um log por engano. */
    @Override
    public String toString() {
        return masked();
    }

    private static String normalize(String raw) {
        if (raw == null) {
            throw invalid();
        }
        String digits = SEPARATORS.matcher(raw).replaceAll("");
        boolean international = digits.startsWith("+");
        if (international) {
            digits = digits.substring(1);
        }
        String national;
        if (digits.length() == 12 && digits.startsWith(COUNTRY_CODE)) {
            national = digits.substring(COUNTRY_CODE.length());
        } else if (digits.length() == 9 && !international) {
            national = digits;
        } else {
            throw invalid();
        }
        if (!NATIONAL.matcher(national).matches()) {
            throw invalid();
        }
        return COUNTRY_CODE + national;
    }

    private static DomainException invalid() {
        // Sem o valor no detalhe: é um dado pessoal.
        return DomainException.badRequest(ErrorCodes.INVALID_MSISDN, "Invalid Mozambican mobile number");
    }
}
