package mz.co.moseguros.shared.error;

import java.util.Objects;

/**
 * Erro de um campo do pedido: 400 {@code VALIDATION_ERROR} com
 * {@code errors[0] = { field, code }}. O {@code code} do campo está em {@link FieldErrorCodes}.
 */
public class FieldValidationException extends DomainException {

    private final String field;
    private final String fieldCode;

    public FieldValidationException(String field, String fieldCode, String detail) {
        super(ErrorCodes.VALIDATION_ERROR, 400, detail);
        this.field = Objects.requireNonNull(field, "field");
        this.fieldCode = Objects.requireNonNull(fieldCode, "fieldCode");
    }

    /** Converte o erro de um value object (ex.: {@code INVALID_MSISDN}) num erro do campo indicado. */
    public static FieldValidationException of(String field, DomainException cause) {
        FieldValidationException e = new FieldValidationException(field, cause.getCode(), cause.getMessage());
        e.initCause(cause);
        return e;
    }

    public String getField() {
        return field;
    }

    public String getFieldCode() {
        return fieldCode;
    }
}
