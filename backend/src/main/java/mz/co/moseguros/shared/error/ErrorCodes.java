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

    private ErrorCodes() {
    }
}
