package mz.co.moseguros.mpesamock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/** Utilitários partilhados pelos controladores da API simulada. */
final class MpesaSupport {

    static final Pattern REFERENCE = Pattern.compile("^[A-Za-z0-9]{1,20}$");
    static final Pattern MSISDN = Pattern.compile("^258(84|85)[0-9]{7}$");
    static final Pattern AMOUNT = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MpesaSupport() {}

    /** Corpo JSON lido como texto; valores não textuais passam a texto. {@code null} se não é um objecto JSON. */
    static Map<String, String> parseBody(String body) {
        Map<String, String> fields = new HashMap<>();
        if (body == null || body.isBlank()) {
            return fields;
        }
        try {
            JsonNode node = MAPPER.readTree(body);
            if (node == null || !node.isObject()) {
                return null;
            }
            node.fields().forEachRemaining(e -> {
                JsonNode value = e.getValue();
                if (!value.isNull() && !value.isContainerNode()) {
                    fields.put(e.getKey(), value.asText());
                }
            });
            return fields;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    static boolean missing(String value) {
        return value == null || value.isBlank();
    }

    static boolean validAmount(String amount) {
        return AMOUNT.matcher(amount).matches() && new BigDecimal(amount).signum() > 0;
    }

    /** {@code 258841234567 → 258*****4567}; nunca se regista o número completo. */
    static String mask(String msisdn) {
        if (msisdn == null || msisdn.length() < 8) {
            return "***";
        }
        return msisdn.substring(0, 3) + "*".repeat(msisdn.length() - 7) + msisdn.substring(msisdn.length() - 4);
    }

    static Map<String, String> output(String conversationId, ResponseCodes code) {
        Map<String, String> out = new LinkedHashMap<>();
        out.put("output_ConversationID", conversationId);
        out.put("output_ResponseCode", code.code());
        out.put("output_ResponseDesc", code.description());
        return out;
    }

    static ResponseEntity<Map<String, String>> respond(int httpStatus, Map<String, String> body) {
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
