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

    // Concorrência
    /** O recurso foi alterado por outro pedido depois de ser lido ({@code version} diferente) (409). */
    public static final String CONCURRENT_MODIFICATION = "CONCURRENT_MODIFICATION";

    // OTP do cliente (S1-03). Os erros das definições são de campo (FieldErrorCodes).
    /** O cliente escolheu o canal, mas a escolha está desligada nas definições (400). */
    public static final String OTP_CHANNEL_CHOICE_DISABLED = "OTP_CHANNEL_CHOICE_DISABLED";
    /** Canal inactivo ou incompatível com o tipo de identificador (400). */
    public static final String OTP_CHANNEL_NOT_AVAILABLE = "OTP_CHANNEL_NOT_AVAILABLE";
    /** Demasiados pedidos na última hora (429, com {@code retryAfterSeconds}). */
    public static final String OTP_RATE_LIMITED = "OTP_RATE_LIMITED";
    /** Novo pedido antes do tempo de reenvio (429, com {@code retryAfterSeconds}). */
    public static final String OTP_RESEND_TOO_SOON = "OTP_RESEND_TOO_SOON";
    /** Nenhum canal conseguiu enviar o código (502). */
    public static final String OTP_DELIVERY_FAILED = "OTP_DELIVERY_FAILED";
    public static final String OTP_CHALLENGE_NOT_FOUND = "OTP_CHALLENGE_NOT_FOUND";
    public static final String OTP_ALREADY_USED = "OTP_ALREADY_USED";
    /** Há um código mais recente para o mesmo identificador (400). */
    public static final String OTP_SUPERSEDED = "OTP_SUPERSEDED";
    public static final String OTP_EXPIRED = "OTP_EXPIRED";
    /** Código errado (400, com {@code attemptsRemaining}). */
    public static final String OTP_INVALID = "OTP_INVALID";
    public static final String OTP_ATTEMPTS_EXCEEDED = "OTP_ATTEMPTS_EXCEEDED";
    /** Conta desactivada (403). */
    public static final String ACCOUNT_DISABLED = "ACCOUNT_DISABLED";

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
            Map.entry(CONCURRENT_MODIFICATION, 409),
            Map.entry(OTP_CHANNEL_CHOICE_DISABLED, 400),
            Map.entry(OTP_CHANNEL_NOT_AVAILABLE, 400),
            Map.entry(OTP_RATE_LIMITED, 429),
            Map.entry(OTP_RESEND_TOO_SOON, 429),
            Map.entry(OTP_DELIVERY_FAILED, 502),
            Map.entry(OTP_CHALLENGE_NOT_FOUND, 404),
            Map.entry(OTP_ALREADY_USED, 400),
            Map.entry(OTP_SUPERSEDED, 400),
            Map.entry(OTP_EXPIRED, 400),
            Map.entry(OTP_INVALID, 400),
            Map.entry(OTP_ATTEMPTS_EXCEEDED, 400),
            Map.entry(ACCOUNT_DISABLED, 403));

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
