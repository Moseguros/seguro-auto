package mz.co.moseguros.shared.idempotency;

import java.util.UUID;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;

/**
 * Leitura e validação do cabeçalho {@code Idempotency-Key} (AGENTS.md §4; S5-02 §5): obrigatório nos
 * endpoints de pagamento, emissão e indemnização.
 */
public final class IdempotencyKeys {

    /** Nome do cabeçalho. */
    public static final String HEADER = "Idempotency-Key";

    private static final Pattern UUID_TEXT =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private IdempotencyKeys() {
    }

    /**
     * @throws DomainException 400 {@code IDEMPOTENCY_KEY_REQUIRED} se faltar ou estiver vazio; 400
     *                         {@code VALIDATION_ERROR} com {@code errors[] = { field: "Idempotency-Key",
     *                         code: "INVALID_UUID" }} se não for um UUID
     */
    public static UUID require(String header) {
        if (header == null || header.isBlank()) {
            throw DomainException.of(ErrorCodes.IDEMPOTENCY_KEY_REQUIRED, "The Idempotency-Key header is required");
        }
        String value = header.strip();
        if (!UUID_TEXT.matcher(value).matches()) {
            throw new FieldValidationException(HEADER, FieldErrorCodes.INVALID_UUID,
                    "The Idempotency-Key header must be a UUID");
        }
        return UUID.fromString(value);
    }
}
