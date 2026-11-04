package mz.co.moseguros.proposal.application;

import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;

/** Erros de campo de um pedido, devolvidos todos juntos em 400 {@code VALIDATION_ERROR}. */
final class FieldErrors {

    private final List<FieldError> errors = new ArrayList<>();

    FieldErrors add(String field, String code, String message) {
        errors.add(new FieldError(field, code, message));
        return this;
    }

    boolean isEmpty() {
        return errors.isEmpty();
    }

    List<FieldError> list() {
        return List.copyOf(errors);
    }

    /** @throws DomainException 400 {@code VALIDATION_ERROR} com {@code errors[]}, se houver erros */
    void throwIfAny() {
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", list());
        }
    }
}
