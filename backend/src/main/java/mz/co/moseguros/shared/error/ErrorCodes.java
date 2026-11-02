package mz.co.moseguros.shared.error;

/**
 * Códigos de erro transversais. Cada módulo define os seus próprios códigos.
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

    // Definições de OTP (400; o S1-03 passa-os para FieldErrorCodes)
    public static final String CHANNELS_EMPTY = "CHANNELS_EMPTY";
    public static final String CHANNELS_DUPLICATED = "CHANNELS_DUPLICATED";
    public static final String DEFAULT_CHANNEL_NOT_ENABLED = "DEFAULT_CHANNEL_NOT_ENABLED";
    public static final String FALLBACK_ORDER_MISMATCH = "FALLBACK_ORDER_MISMATCH";
    public static final String OUT_OF_RANGE = "OUT_OF_RANGE";

    private ErrorCodes() {
    }
}
