package mz.co.moseguros.shared.error;

import java.util.List;
import java.util.TreeSet;

/**
 * Lista oficial dos códigos de {@code errors[].code} (erros por campo de um
 * {@code VALIDATION_ERROR}). É exportada para {@code api/error-codes.json}
 * ({@code ErrorCodesExportTest}); a web e a app têm de ter a tradução {@code validation.<CODE>}
 * de cada código, em pt e en (AGENTS.md §4).
 *
 * <p>Inclui os códigos dos value objects (lidos de um pedido são erros de campo) e os das
 * restrições de Bean Validation usadas nos DTOs ({@code @NotBlank} → {@code NOT_BLANK}, ver
 * {@code GlobalExceptionHandler.codeOf}).
 */
public final class FieldErrorCodes {

    // Value objects partilhados
    public static final String INVALID_CURRENCY = ErrorCodes.INVALID_CURRENCY;
    public static final String INVALID_MSISDN = ErrorCodes.INVALID_MSISDN;
    public static final String INVALID_NUIT = ErrorCodes.INVALID_NUIT;
    public static final String INVALID_PLATE = ErrorCodes.INVALID_PLATE;
    public static final String INVALID_EMAIL = ErrorCodes.INVALID_EMAIL;
    public static final String INVALID_PHONE = ErrorCodes.INVALID_PHONE;

    // Definições de OTP (S1-03)
    public static final String CHANNELS_EMPTY = "CHANNELS_EMPTY";
    public static final String CHANNELS_DUPLICATED = "CHANNELS_DUPLICATED";
    public static final String DEFAULT_CHANNEL_NOT_ENABLED = "DEFAULT_CHANNEL_NOT_ENABLED";
    public static final String FALLBACK_ORDER_MISMATCH = "FALLBACK_ORDER_MISMATCH";
    public static final String OUT_OF_RANGE = "OUT_OF_RANGE";

    // Bean Validation
    /** {@code @NotBlank}: campo obrigatório vazio. */
    public static final String NOT_BLANK = "NOT_BLANK";
    /** {@code @NotNull}: campo obrigatório em falta. */
    public static final String NOT_NULL = "NOT_NULL";

    private FieldErrorCodes() {
    }

    /** Todos os códigos, por ordem alfabética. */
    public static List<String> all() {
        return List.copyOf(new TreeSet<>(ErrorCodes.constants(FieldErrorCodes.class)));
    }
}
