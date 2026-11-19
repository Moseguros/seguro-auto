package mz.co.moseguros.payment.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.PaymentMethod;

/**
 * Segredos de cada forma de pagamento (S5-01 §4). Nunca ficam em {@code settings} nem na BD: vão
 * para o cofre em {@code secret/insurers/{insurerId}/payments/{METHOD}/{campo}} e a linha guarda só
 * o caminho ({@code secret_refs}).
 *
 * <p>Para {@code BANK_REFERENCE} o que se aplica depende de {@code settings.sftp.authType}: sem
 * {@code authType}, aplicam-se todos (gravação a meio) e nenhum é obrigatório.
 */
public enum SecretField {

    MPESA_API_KEY(PaymentMethod.MPESA, "apiKey"),
    MPESA_PUBLIC_KEY(PaymentMethod.MPESA, "publicKey"),
    MPESA_SECURITY_CREDENTIAL(PaymentMethod.MPESA, "securityCredential"),
    SFTP_PASSWORD(PaymentMethod.BANK_REFERENCE, "sftp.password"),
    SFTP_PRIVATE_KEY(PaymentMethod.BANK_REFERENCE, "sftp.privateKey"),
    SFTP_PASSPHRASE(PaymentMethod.BANK_REFERENCE, "sftp.passphrase");

    /** Comprimento máximo de um valor secreto. */
    public static final int MAX_LENGTH = 8000;
    /** Mount KV v2 do cofre (primeiro segmento dos caminhos). */
    public static final String MOUNT = "secret";

    private final PaymentMethod method;
    private final String fieldName;

    SecretField(PaymentMethod method, String fieldName) {
        this.method = method;
        this.fieldName = fieldName;
    }

    public PaymentMethod method() {
        return method;
    }

    /** Nome do campo no pedido ({@code secrets.<nome>}), em {@code secret_refs} e no caminho do cofre. */
    public String fieldName() {
        return fieldName;
    }

    /** Campos do método, pela ordem da tabela da ficha. */
    public static List<SecretField> of(PaymentMethod method) {
        return Arrays.stream(values()).filter(f -> f.method == method).toList();
    }

    public static Optional<SecretField> of(PaymentMethod method, String fieldName) {
        return of(method).stream().filter(f -> f.fieldName.equals(fieldName)).findFirst();
    }

    /** Caminho no cofre para a seguradora. */
    public String path(UUID insurerId) {
        return MOUNT + "/insurers/" + insurerId + "/payments/" + method.name() + "/" + fieldName;
    }

    /** O segredo aplica-se com estes {@code settings} (senão não pode ser enviado e é apagado). */
    public boolean applies(JsonNode settings) {
        String auth = authType(settings);
        return switch (this) {
            case SFTP_PASSWORD -> !"KEY".equals(auth);
            case SFTP_PRIVATE_KEY, SFTP_PASSPHRASE -> !"PASSWORD".equals(auth);
            default -> true;
        };
    }

    /** O segredo é obrigatório para activar com estes {@code settings}. */
    public boolean required(JsonNode settings) {
        return switch (this) {
            case MPESA_API_KEY, MPESA_PUBLIC_KEY -> true;
            case SFTP_PASSWORD -> "PASSWORD".equals(authType(settings));
            case SFTP_PRIVATE_KEY -> "KEY".equals(authType(settings));
            default -> false;
        };
    }

    private static String authType(JsonNode settings) {
        JsonNode auth = settings == null ? null : settings.path("sftp").path("authType");
        return auth != null && auth.isTextual() ? auth.asText() : null;
    }
}
