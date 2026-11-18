package mz.co.moseguros.shared.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON canónico dos pedidos idempotentes (S5-02 §2): chaves ordenadas em todos os níveis, sem
 * espaços, {@code null} incluído. Quem chama passa só os campos que definem o pedido (nunca a própria
 * chave de idempotência nem o utilizador).
 */
public final class CanonicalJson {

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    private CanonicalJson() {
    }

    /** JSON canónico do valor (um {@code Map}, um record ou um {@link JsonNode}). */
    public static String of(Object value) {
        JsonNode node = value instanceof JsonNode json ? json : MAPPER.valueToTree(value);
        try {
            return MAPPER.writeValueAsString(sorted(node));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot write canonical JSON", e);
        }
    }

    /** {@code SHA-256} hexadecimal (minúsculas) de {@code operation + "\n" + of(request)}. */
    public static String requestHash(String operation, Map<String, ?> request) {
        Objects.requireNonNull(operation, "operation");
        return sha256(operation + "\n" + of(request));
    }

    static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static JsonNode sorted(JsonNode node) {
        if (node.isObject()) {
            List<String> names = new ArrayList<>();
            node.fieldNames().forEachRemaining(names::add);
            names.sort(null);
            ObjectNode out = JsonNodeFactory.instance.objectNode();
            for (String name : names) {
                out.set(name, sorted(node.get(name)));
            }
            return out;
        }
        if (node.isArray()) {
            ArrayNode out = JsonNodeFactory.instance.arrayNode();
            node.forEach(item -> out.add(sorted(item)));
            return out;
        }
        return node;
    }
}
