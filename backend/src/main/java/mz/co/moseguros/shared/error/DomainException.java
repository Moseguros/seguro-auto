package mz.co.moseguros.shared.error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Erro de regra de negócio com um código estável (ex.: {@code POLICY_NOT_ACTIVE})
 * e o estado HTTP com que deve ser devolvido ao cliente.
 *
 * <p>O {@code detail} é técnico e em inglês; os textos para o utilizador vêm do
 * i18n do frontend a partir do {@code code}.
 *
 * <p>Propriedades extra ({@link #withProperty}) vão para o {@code ProblemDetail} da resposta
 * (ex.: {@code retryAfterSeconds}, {@code attemptsRemaining}); nunca dados pessoais.
 */
public class DomainException extends RuntimeException {

    /** Propriedade que, além de ir no corpo, gera o cabeçalho {@code Retry-After}. */
    public static final String RETRY_AFTER_SECONDS = "retryAfterSeconds";

    private final String code;
    private final int httpStatus;
    private final Map<String, Object> properties = new LinkedHashMap<>();

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

    /** Erro com o estado HTTP registado em {@link ErrorCodes} para o código. */
    public static DomainException of(String code, String detail) {
        return new DomainException(code, ErrorCodes.status(code), detail);
    }

    /** Acrescenta uma propriedade ao {@code ProblemDetail} da resposta. */
    public DomainException withProperty(String name, Object value) {
        properties.put(Objects.requireNonNull(name, "name"), value);
        return this;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
