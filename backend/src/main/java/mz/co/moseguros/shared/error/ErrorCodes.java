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

    private ErrorCodes() {
    }
}
