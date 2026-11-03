package mz.co.moseguros.notification.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.shared.domain.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code POST /integrations/delivery-receipts/{provider}}: segredo, fornecedor, mensagem
 * desconhecida e actualização de {@code outbound_message}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class DeliveryReceiptIT {

    static final String SECRET = "test-webhook-secret";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    NotificationApi notifications;

    @Autowired
    FakeOtpDeliveryChannels channels;

    JdbcTemplate owner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        channels.reset();
    }

    @Test
    void wrongOrMissingSecretIsUnauthorized() {
        receipt("mock-sms", "wrong-secret", "x", "DELIVERED").then().statusCode(401)
                .contentType("application/problem+json")
                .body("code", equalTo("UNAUTHORIZED"));
        receipt("mock-sms", null, "x", "DELIVERED").then().statusCode(401);
    }

    @Test
    void unknownProviderIsNotFound() {
        receipt("acme-sms", SECRET, "x", "DELIVERED").then().statusCode(404).body("code", equalTo("NOT_FOUND"));
        receipt("smtp", SECRET, "x", "DELIVERED").then().statusCode(404);
    }

    @Test
    void unknownMessageIsIgnored() {
        receipt("mock-sms", SECRET, "does-not-exist", "FAILED").then().statusCode(204);
    }

    @Test
    void deliveredReceiptUpdatesTheMessage() {
        DeliveryResult sent = notifications.sendOtp(Channel.SMS, "+258841110001", "123456", 5, "pt", null);
        assertThat(sent.status()).isEqualTo(DeliveryStatus.SENT);
        String providerId = channels.lastTo("+258841110001").providerMessageId();

        receipt("mock-sms", SECRET, providerId, "DELIVERED").then().statusCode(204);

        Map<String, Object> row = row(sent.messageId());
        assertThat(row.get("status")).isEqualTo("DELIVERED");
        assertThat(row.get("delivered_at")).isNotNull();
        assertThat(row.get("provider_message_id")).isEqualTo(providerId);
        assertThat(row.get("destination_masked")).isEqualTo("+258*****0001");
        assertThat(row.get("purpose")).isEqualTo("OTP");
    }

    @Test
    void failedReceiptUpdatesTheMessageWithTheReason() {
        DeliveryResult sent = notifications.sendOtp(Channel.WHATSAPP, "+258841110002", "654321", 5, "en", null);
        String providerId = channels.lastTo("+258841110002").providerMessageId();

        receipt("mock-whatsapp", SECRET, providerId, "FAILED").then().statusCode(204);

        Map<String, Object> row = row(sent.messageId());
        assertThat(row.get("status")).isEqualTo("FAILED");
        assertThat((String) row.get("failure_reason")).contains("mock");
    }

    @Test
    void synchronousFailureIsRecordedWithoutPersonalData() {
        channels.mode(Channel.EMAIL, FakeOtpDeliveryChannels.Mode.FAIL);

        DeliveryResult result = notifications.sendOtp(Channel.EMAIL, "ana.mussa@exemplo.co.mz", "111222", 5, "pt",
                UUID.randomUUID());

        assertThat(result.status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(result.failureReason()).isEqualTo("HTTP 503");
        Map<String, Object> row = row(result.messageId());
        assertThat(row.get("status")).isEqualTo("FAILED");
        assertThat(row.get("provider")).isEqualTo("smtp");
        assertThat(row.get("destination_masked")).isEqualTo("a***@exemplo.co.mz");
        assertThat((String) row.get("destination_hash")).hasSize(64);
    }

    private Map<String, Object> row(UUID messageId) {
        return owner.queryForMap("select * from notification.outbound_message where id = ?", messageId);
    }

    private static Response receipt(String provider, String secret, String providerMessageId, String status) {
        var request = given().contentType(ContentType.JSON)
                .body(Map.of("providerMessageId", providerMessageId, "status", status, "reason", "mock"));
        if (secret != null) {
            request = request.header("X-Webhook-Secret", secret);
        }
        return request.post("/api/v1/integrations/delivery-receipts/" + provider);
    }
}
