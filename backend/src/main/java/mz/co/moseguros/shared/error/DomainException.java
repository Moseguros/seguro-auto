package mz.co.moseguros.shared.error;

import java.util.Objects;

/**
 * Erro de regra de negócio com um código estável (ex.: {@code POLICY_NOT_ACTIVE})
 * e o estado HTTP com que deve ser devolvido ao cliente.
 *
 * <p>O {@code detail} é técnico e em inglês; os textos para o utilizador vêm do
 * i18n do frontend a partir do {@code code}.
 */
public class DomainException extends RuntimeException {

    private final String code;
    private final int httpStatus;

    public DomainException(String code, int httpStatus, String detail) {
        super(detail);
        this.code = Objects.requireNonNull(code, "code");
        if (httpStatus < 400 || httpStatus > 599) {
            throw new IllegalArgumentException("httpStatus must be 4xx or 5xx: " + httpStatus);
        }
        this.httpStatus = httpStatus;
    }

    /** Atalho para erros de validação de dados (400). */
    public static DomainException badRequest(String code, String detail) {
        return new DomainException(code, 400, detail);
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
