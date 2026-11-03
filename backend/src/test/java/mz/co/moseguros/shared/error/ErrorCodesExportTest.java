package mz.co.moseguros.shared.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/**
 * {@code api/error-codes.json} é a lista oficial dos códigos de erro para a web e a app
 * (traduções {@code errors.<CODE>} e {@code validation.<CODE>}).
 *
 * <p>Com {@code -Derror-codes.export=true} escreve o ficheiro a partir de {@link ErrorCodes} e
 * {@link FieldErrorCodes}; sem a flag falha se o ficheiro não lhes corresponder.
 */
class ErrorCodesExportTest {

    static final Path FILE = Path.of(System.getProperty("error-codes.output", "../api/error-codes.json"))
            .toAbsolutePath().normalize();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void errorCodesFileMatchesTheClasses() throws IOException {
        JsonNode expected = expected();
        if (Boolean.getBoolean("error-codes.export")) {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, render(expected), StandardCharsets.UTF_8);
        }
        assertThat(Files.exists(FILE))
                .as("%s não existe: corre ./mvnw test -Dtest=ErrorCodesExportTest -Derror-codes.export=true", FILE)
                .isTrue();
        JsonNode actual = mapper.readTree(Files.readString(FILE, StandardCharsets.UTF_8));
        assertThat(actual)
                .as("%s está desactualizado: corre ./mvnw test -Dtest=ErrorCodesExportTest -Derror-codes.export=true",
                        FILE)
                .isEqualTo(expected);
    }

    @Test
    void everyErrorCodeHasAnHttpStatus() {
        Map<String, Integer> all = ErrorCodes.all();
        assertThat(all).containsEntry("REFRESH_TOKEN_INVALID", 401)
                .containsEntry("REFRESH_TOKEN_REUSED", 401)
                .containsEntry("TENANT_REQUIRED", 500)
                .containsEntry("LICENSE_NUMBER_REQUIRED", 400)
                .containsEntry("INVALID_STATE_TRANSITION", 409)
                .containsEntry("DECRYPTION_FAILED", 500);
        assertThat(all.values()).allSatisfy(status -> assertThat(status).isBetween(400, 599));
        assertThat(all.keySet()).allSatisfy(code -> assertThat(code).matches("^[A-Z][A-Z0-9_]*$"));
        assertThatThrownBy(() -> ErrorCodes.status("NOPE")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fieldCodesAreUpperSnakeCase() {
        assertThat(FieldErrorCodes.all()).contains("INVALID_MSISDN", "NOT_BLANK")
                .allSatisfy(code -> assertThat(code).matches("^[A-Z][A-Z0-9_]*$"))
                .isSorted();
    }

    private JsonNode expected() {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode errors = root.putArray("errors");
        ErrorCodes.all().forEach((code, status) -> errors.addObject().put("code", code).put("status", status));
        ArrayNode validation = root.putArray("validation");
        FieldErrorCodes.all().forEach(validation::add);
        return root;
    }

    private String render(JsonNode node) throws IOException {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter()
                .withObjectIndenter(new DefaultIndenter("  ", "\n"))
                .withArrayIndenter(new DefaultIndenter("  ", "\n"));
        return mapper.writer(printer).writeValueAsString(node) + "\n";
    }
}
