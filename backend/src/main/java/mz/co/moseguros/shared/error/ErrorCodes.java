package mz.co.moseguros.shared.error;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Lista oficial dos códigos de erro de topo (o {@code code} do {@code ProblemDetail}), cada um
 * com o estado HTTP com que é devolvido. É exportada para {@code api/error-codes.json}
 * ({@code ErrorCodesExportTest}); a web e a app têm de ter a tradução {@code errors.<CODE>} de
 * cada código, em pt e en (AGENTS.md §4).
 *
 * <p>Quem acrescenta uma constante acrescenta o estado em {@link #STATUS}. Os códigos de
 * {@code errors[].code} estão em {@link FieldErrorCodes}.
 */
public final class ErrorCodes {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    // Value objects partilhados (400). Ao ler um pedido aparecem em errors[].code.
    public static final String INVALID_CURRENCY = "INVALID_CURRENCY";
    public static final String INVALID_MSISDN = "INVALID_MSISDN";
    public static final String INVALID_NUIT = "INVALID_NUIT";
    public static final String INVALID_PLATE = "INVALID_PLATE";
    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String INVALID_PHONE = "INVALID_PHONE";

    // Infra-estrutura (500): erros de programação ou de configuração, nunca do utilizador.
    /** Operação que precisa de uma seguradora no {@code TenantContext} sem a ter. */
    public static final String TENANT_REQUIRED = "TENANT_REQUIRED";
    /** Valor cifrado alterado, chave errada ou versão de chave desconhecida. */
    public static final String DECRYPTION_FAILED = "DECRYPTION_FAILED";

    // Regras de domínio partilhadas
    /** Transição de estado não permitida a partir do estado actual (409). */
    public static final String INVALID_STATE_TRANSITION = "INVALID_STATE_TRANSITION";

    // Organizações (400)
    public static final String LICENSE_NUMBER_REQUIRED = "LICENSE_NUMBER_REQUIRED";

    // Sessões (401)
    /** Refresh token desconhecido, expirado, revogado, ou conta/organização que já não o permite. */
    public static final String REFRESH_TOKEN_INVALID = "REFRESH_TOKEN_INVALID";
    /** Refresh token já usado numa renovação: toda a sessão (família) foi revogada. */
    public static final String REFRESH_TOKEN_REUSED = "REFRESH_TOKEN_REUSED";

    // Definições de OTP (400; o S1-03 passa-os para FieldErrorCodes)
    public static final String CHANNELS_EMPTY = "CHANNELS_EMPTY";
    public static final String CHANNELS_DUPLICATED = "CHANNELS_DUPLICATED";
    public static final String DEFAULT_CHANNEL_NOT_ENABLED = "DEFAULT_CHANNEL_NOT_ENABLED";
    public static final String FALLBACK_ORDER_MISMATCH = "FALLBACK_ORDER_MISMATCH";
    public static final String OUT_OF_RANGE = "OUT_OF_RANGE";

    /** Estado HTTP de cada código. */
    private static final Map<String, Integer> STATUS = Map.ofEntries(
            Map.entry(VALIDATION_ERROR, 400),
            Map.entry(NOT_FOUND, 404),
            Map.entry(UNAUTHORIZED, 401),
            Map.entry(FORBIDDEN, 403),
            Map.entry(INTERNAL_ERROR, 500),
            Map.entry(INVALID_CURRENCY, 400),
            Map.entry(INVALID_MSISDN, 400),
            Map.entry(INVALID_NUIT, 400),
            Map.entry(INVALID_PLATE, 400),
            Map.entry(INVALID_EMAIL, 400),
            Map.entry(INVALID_PHONE, 400),
            Map.entry(TENANT_REQUIRED, 500),
            Map.entry(DECRYPTION_FAILED, 500),
            Map.entry(INVALID_STATE_TRANSITION, 409),
            Map.entry(LICENSE_NUMBER_REQUIRED, 400),
            Map.entry(REFRESH_TOKEN_INVALID, 401),
            Map.entry(REFRESH_TOKEN_REUSED, 401),
            Map.entry(CHANNELS_EMPTY, 400),
            Map.entry(CHANNELS_DUPLICATED, 400),
            Map.entry(DEFAULT_CHANNEL_NOT_ENABLED, 400),
            Map.entry(FALLBACK_ORDER_MISMATCH, 400),
            Map.entry(OUT_OF_RANGE, 400));

    private ErrorCodes() {
    }

    /** Estado HTTP de um código desta lista. */
    public static int status(String code) {
        Integer status = STATUS.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Unknown error code: " + code);
        }
        return status;
    }

    /**
     * Todos os códigos (as constantes desta classe) com o seu estado, por ordem alfabética.
     * Falha se alguma constante não tiver estado.
     */
    public static Map<String, Integer> all() {
        Map<String, Integer> all = new TreeMap<>();
        for (String code : constants(ErrorCodes.class)) {
            all.put(code, status(code));
        }
        return all;
    }

    /** Valores das constantes {@code public static final String} de {@code type}. */
    static List<String> constants(Class<?> type) {
        List<String> values = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                    && field.getType() == String.class) {
                try {
                    values.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return values;
    }
}
