package mz.co.moseguros.shared.web;

import java.util.List;

import jakarta.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.JsonMappingException;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Converte excepções em respostas RFC 7807 ({@code application/problem+json}).
 *
 * <p>Erros inesperados devolvem 500 {@code INTERNAL_ERROR} sem detalhes internos;
 * o stack trace fica apenas no log.
 *
 * <p>Tem precedência sobre o {@code ProblemDetailsExceptionHandler} do Spring Boot
 * ({@code spring.mvc.problemdetails.enabled}), que também trata
 * {@code MethodArgumentNotValidException} mas sem {@code code} nem {@code errors[]}.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleInvalidBody(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), codeOf(e.getCode()), e.getDefaultMessage()))
                .toList();
        return Problems.validation("Request validation failed", errors);
    }

    /**
     * Corpo ilegível. Se a causa for um value object inválido (ex.: {@code Msisdn}), é um
     * erro de campo: 400 {@code VALIDATION_ERROR} com {@code errors[0].field} = caminho
     * JSON do campo e {@code errors[0].code} = código do value object.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadableBody(HttpMessageNotReadableException ex) {
        DomainException domain = findCause(ex, DomainException.class);
        if (domain != null) {
            JsonMappingException mapping = findCause(ex, JsonMappingException.class);
            String field = mapping == null ? "" : fieldPath(mapping.getPath());
            return Problems.validation("Request validation failed",
                    List.of(new FieldError(field, domain.getCode(), domain.getMessage())));
        }
        return Problems.validation("Malformed request body", List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        List<FieldError> errors = ex.getConstraintViolations().stream()
                .map(v -> new FieldError(
                        v.getPropertyPath().toString(),
                        codeOf(v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()),
                        v.getMessage()))
                .toList();
        return Problems.validation("Request validation failed", errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail handleNotFound(NoResourceFoundException ex) {
        return Problems.of(HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND, "Resource not found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return Problems.of(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException ex) {
        return Problems.of(HttpStatusCode.valueOf(ex.getHttpStatus()), ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return Problems.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_ERROR,
                "An unexpected error occurred");
    }

    /** Caminho JSON de um erro do Jackson: {@code vehicle.plate}, {@code drivers[0].msisdn}. */
    static String fieldPath(List<JsonMappingException.Reference> path) {
        StringBuilder sb = new StringBuilder();
        for (JsonMappingException.Reference ref : path) {
            if (ref.getFieldName() != null) {
                if (!sb.isEmpty()) {
                    sb.append('.');
                }
                sb.append(ref.getFieldName());
            } else if (ref.getIndex() >= 0) {
                sb.append('[').append(ref.getIndex()).append(']');
            }
        }
        return sb.toString();
    }

    private static <T extends Throwable> T findCause(Throwable ex, Class<T> type) {
        Throwable current = ex;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause() == current ? null : current.getCause();
        }
        return null;
    }

    /** Converte o nome de uma restrição (ex.: {@code NotBlank}) em código (ex.: {@code NOT_BLANK}). */
    static String codeOf(String constraint) {
        if (constraint == null || constraint.isBlank()) {
            return "INVALID";
        }
        return constraint.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
