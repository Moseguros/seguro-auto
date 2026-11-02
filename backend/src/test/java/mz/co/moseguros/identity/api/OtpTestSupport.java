package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes de integração do OTP: pedidos à API, identificadores aleatórios (cada teste
 * usa os seus, para os limites não se misturarem) e reposição das definições.
 */
final class OtpTestSupport {

    static final String WEBHOOK_SECRET = "test-webhook-secret";

    private OtpTestSupport() {
    }

    /** Telemóvel nacional aleatório ({@code 84XXXXXXX}). */
    static String randomMsisdn() {
        StringBuilder sb = new StringBuilder("84");
        for (int i = 0; i < 7; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    static String e164(String national) {
        return "+258" + national;
    }

    static String randomEmail() {
        return "cliente" + ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999) + "@exemplo.co.mz";
    }

    static Response requestOtp(String identifier, String type) {
        return requestOtp(identifier, type, null);
    }

    static Response requestOtp(String identifier, String type, String channel) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("identifier", identifier);
        body.put("identifierType", type);
        if (channel != null) {
            body.put("channel", channel);
        }
        return given().contentType(ContentType.JSON).body(body).post("/api/v1/auth/otp/request");
    }

    static Response verifyOtp(String challengeId, String code) {
        return given().contentType(ContentType.JSON).header("User-Agent", "JUnit")
                .body(Map.of("challengeId", challengeId, "code", code))
                .post("/api/v1/auth/otp/verify");
    }

    static Response challenge(String challengeId) {
        return given().get("/api/v1/auth/otp/challenges/" + challengeId);
    }

    static Response receipt(String provider, String secret, String providerMessageId, String status) {
        var request = given().contentType(ContentType.JSON)
                .body(Map.of("providerMessageId", providerMessageId, "status", status, "reason", "mock"));
        if (secret != null) {
            request = request.header("X-Webhook-Secret", secret);
        }
        return request.post("/api/v1/integrations/delivery-receipts/" + provider);
    }

    /** Um código errado com o mesmo comprimento. */
    static String wrong(String code) {
        char first = code.charAt(0) == '9' ? '0' : (char) (code.charAt(0) + 1);
        return first + code.substring(1);
    }

    /** Repõe as definições da migração (todos os canais, 6 dígitos, 300 s, 3 tentativas, 60 s, 5/h). */
    static void resetSettings(JdbcTemplate owner) {
        owner.update("""
                update identity.otp_settings
                   set enabled_channels = '{SMS,WHATSAPP,EMAIL}', default_channel = 'SMS', customer_can_choose = true,
                       fallback_order = '{SMS,WHATSAPP,EMAIL}', code_length = 6, validity_seconds = 300,
                       max_attempts = 3, resend_after_seconds = 60, max_requests_per_hour = 5""");
    }

    /** Espera até a condição ser verdadeira (no máximo {@code timeoutMillis}). */
    static boolean eventually(BooleanSupplier condition, long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return condition.getAsBoolean();
    }
}
