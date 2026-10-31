package mz.co.moseguros.shared.error;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

/**
 * Construção uniforme de respostas RFC 7807 com as propriedades {@code code} e,
 * quando aplicável, {@code errors}.
 */
public final class Problems {

    /** Prefixo do campo {@code type}; o sufixo é o código em minúsculas. */
    public static final String TYPE_BASE = "https://moseguros.co.mz/problems/";

    private Problems() {
    }

    public static ProblemDetail of(HttpStatusCode status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(TYPE_BASE + code.toLowerCase().replace('_', '-')));
        HttpStatus resolved = HttpStatus.resolve(status.value());
        problem.setTitle(resolved != null ? resolved.getReasonPhrase() : "Error");
        problem.setProperty("code", code);
        return problem;
    }

    public static ProblemDetail validation(String detail, List<FieldError> errors) {
        ProblemDetail problem = of(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_ERROR, detail);
        problem.setProperty("errors", errors);
        return problem;
    }

    /** Erro de um campo do pedido. */
    public record FieldError(String field, String code, String message) {
    }
}
