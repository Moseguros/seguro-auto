package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.identity.api.OtpTestSupport.e164;
import static mz.co.moseguros.identity.api.OtpTestSupport.randomEmail;
import static mz.co.moseguros.identity.api.OtpTestSupport.randomMsisdn;
import static mz.co.moseguros.identity.api.OtpTestSupport.requestOtp;
import static mz.co.moseguros.identity.api.OtpTestSupport.verifyOtp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels.Mode;
import mz.co.moseguros.shared.domain.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Administração das definições do OTP: leitura, alteração (efeito imediato, validação,
 * concorrência, auditoria), permissões e envio de teste.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class OtpSettingsIT {

    static final String PATH = "/api/v1/admin/otp-settings";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    JdbcTemplate owner;
    String adminToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        OtpTestSupport.resetSettings(owner);
        channels.reset();
        UserAccount admin = fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN");
        adminToken = identityApi.issueTokens(admin, null).accessToken();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
        OtpTestSupport.resetSettings(owner);
    }

    @Test
    void platformAdminReadsTheSettings() {
        given().auth().oauth2(adminToken).get(PATH).then().statusCode(200)
                .body("enabledChannels", contains("SMS", "WHATSAPP", "EMAIL"))
                .body("defaultChannel", equalTo("SMS"))
                .body("customerCanChoose", equalTo(true))
                .body("fallbackOrder", contains("SMS", "WHATSAPP", "EMAIL"))
                .body("codeLength", equalTo(6))
                .body("validitySeconds", equalTo(300))
                .body("maxAttempts", equalTo(3))
                .body("resendAfterSeconds", equalTo(60))
                .body("maxRequestsPerHour", equalTo(5))
                .body("version", notNullValue())
                .body("updatedAt", notNullValue())
                .body("updatedBy", notNullValue());
    }

    @Test
    void disablingWhatsAppTakesEffectOnTheNextRequestAndIsAudited() {
        given().get("/api/v1/auth/otp/options").then().statusCode(200)
                .body("identifierTypes", contains("MSISDN", "EMAIL"))
                .body("channels.MSISDN", contains("SMS", "WHATSAPP"))
                .body("channels.EMAIL", contains("EMAIL"))
                .body("defaultChannels.MSISDN", equalTo("SMS"))
                .body("defaultChannels.EMAIL", equalTo("EMAIL"))
                .body("customerCanChoose", equalTo(true))
                .body("codeLength", equalTo(6))
                .body("resendAfterSeconds", equalTo(60));

        Map<String, Object> body = current();
        body.put("enabledChannels", List.of("SMS", "EMAIL"));
        body.put("fallbackOrder", List.of("EMAIL", "SMS"));
        Response updated = put(adminToken, body);
        updated.then().statusCode(200)
                .body("enabledChannels", contains("SMS", "EMAIL"))
                .body("version", equalTo(((Number) body.get("version")).intValue() + 1));

        given().get("/api/v1/auth/otp/options").then().statusCode(200)
                .body("channels.MSISDN", contains("SMS"))
                .body("channels.EMAIL", contains("EMAIL"));

        channels.mode(Channel.SMS, Mode.FAIL);
        requestOtp(randomMsisdn(), "MSISDN").then().statusCode(502).body("code", equalTo("OTP_DELIVERY_FAILED"));
        requestOtp(randomMsisdn(), "MSISDN", "WHATSAPP").then().statusCode(400)
                .body("code", equalTo("OTP_CHANNEL_NOT_AVAILABLE"));

        Map<String, Object> audit = owner.queryForMap("""
                select actor_type, before_state::text as before, after_state::text as after from audit.audit_event
                 where action = 'OTP_SETTINGS.UPDATE' order by occurred_at desc limit 1""");
        assertThat(audit.get("actor_type")).isEqualTo("STAFF");
        assertThat((String) audit.get("before")).contains("WHATSAPP");
        assertThat((String) audit.get("after")).doesNotContain("WHATSAPP").contains("EMAIL");
    }

    @Test
    void onlyEmailActiveRejectsAMobileNumber() {
        Map<String, Object> body = current();
        body.put("enabledChannels", List.of("EMAIL"));
        body.put("defaultChannel", "EMAIL");
        body.put("fallbackOrder", List.of("EMAIL"));
        put(adminToken, body).then().statusCode(200);

        given().get("/api/v1/auth/otp/options").then().statusCode(200)
                .body("identifierTypes", contains("EMAIL"))
                .body("channels", not(hasKey("MSISDN")))
                .body("defaultChannels", not(hasKey("MSISDN")));
        requestOtp(randomMsisdn(), "MSISDN").then().statusCode(400)
                .body("code", equalTo("OTP_CHANNEL_NOT_AVAILABLE"));
        requestOtp(randomEmail(), "EMAIL").then().statusCode(202).body("channel", equalTo("EMAIL"));
    }

    @Test
    void choosingTheChannelCanBeDisabled() {
        Map<String, Object> body = current();
        body.put("customerCanChoose", false);
        put(adminToken, body).then().statusCode(200);

        requestOtp(randomMsisdn(), "MSISDN", "SMS").then().statusCode(400)
                .body("code", equalTo("OTP_CHANNEL_CHOICE_DISABLED"));
        requestOtp(randomMsisdn(), "MSISDN").then().statusCode(202);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "enabledChannels | []                  | enabledChannels    | CHANNELS_EMPTY",
            "enabledChannels | [\"SMS\",\"SMS\"]   | enabledChannels    | CHANNELS_DUPLICATED",
            "defaultChannel  | \"EMAIL\"           | defaultChannel     | DEFAULT_CHANNEL_NOT_ENABLED",
            "fallbackOrder   | [\"SMS\"]           | fallbackOrder      | FALLBACK_ORDER_MISMATCH",
            "codeLength      | 3                   | codeLength         | OUT_OF_RANGE",
            "validitySeconds | 1801                | validitySeconds    | OUT_OF_RANGE",
            "maxAttempts     | 0                   | maxAttempts        | OUT_OF_RANGE",
            "resendAfterSeconds | 14               | resendAfterSeconds | OUT_OF_RANGE",
            "maxRequestsPerHour | 51               | maxRequestsPerHour | OUT_OF_RANGE",
            "codeLength      | null                | codeLength         | NOT_NULL"})
    void eachValidationRuleReturnsItsFieldCode(String property, String json, String field, String code) {
        Map<String, Object> body = current();
        body.put("enabledChannels", List.of("SMS", "WHATSAPP"));
        body.put("fallbackOrder", List.of("SMS", "WHATSAPP"));
        String payload = toJson(body).replaceFirst("\"" + property + "\":(\\[[^\\]]*\\]|\"[A-Z]+\"|[0-9a-z]+)",
                "\"" + property + "\":" + json);

        given().auth().oauth2(adminToken).contentType(ContentType.JSON).body(payload).put(PATH)
                .then().statusCode(400)
                .contentType("application/problem+json")
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo(field))
                .body("errors[0].code", equalTo(code));
    }

    @Test
    void staleVersionIsAConflict() {
        Map<String, Object> body = current();
        Map<String, Object> other = new HashMap<>(body);
        other.put("maxAttempts", 5);
        put(adminToken, other).then().statusCode(200);

        put(adminToken, body).then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void customerIsForbiddenAndAnonymousIsUnauthorized() {
        UserAccount customer = fixtures.customer("Cliente Curioso");
        String customerToken = identityApi.issueTokens(customer, null).accessToken();

        given().auth().oauth2(customerToken).get(PATH).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        put(customerToken, current()).then().statusCode(403);
        given().auth().oauth2(customerToken).contentType(ContentType.JSON)
                .body(Map.of("identifier", randomMsisdn(), "identifierType", "MSISDN", "channel", "SMS"))
                .post(PATH + "/test").then().statusCode(403);
        given().get(PATH).then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
        given().contentType(ContentType.JSON).body(current()).put(PATH).then().statusCode(401);
    }

    @Test
    void testDeliveryUsesOnlyTheRequestedChannelEvenIfDisabled() {
        Map<String, Object> body = current();
        body.put("enabledChannels", List.of("SMS"));
        body.put("fallbackOrder", List.of("SMS"));
        put(adminToken, body).then().statusCode(200);
        String msisdn = randomMsisdn();

        given().auth().oauth2(adminToken).contentType(ContentType.JSON)
                .body(Map.of("identifier", msisdn, "identifierType", "MSISDN", "channel", "WHATSAPP"))
                .post(PATH + "/test").then().statusCode(200)
                .body("channel", equalTo("WHATSAPP"))
                .body("deliveryStatus", equalTo("SENT"))
                .body("messageId", notNullValue());
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo(e164(msisdn));
        assertThat(sent.channel()).isEqualTo(Channel.WHATSAPP);
        String testChallenge = owner.queryForObject(
                "select id::text from identity.otp_challenge where identifier = ? and purpose = 'TEST'",
                String.class, "258" + msisdn);
        verifyOtp(testChallenge, sent.code()).then().statusCode(404).body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));

        channels.mode(Channel.WHATSAPP, Mode.FAIL);
        given().auth().oauth2(adminToken).contentType(ContentType.JSON)
                .body(Map.of("identifier", randomMsisdn(), "identifierType", "MSISDN", "channel", "WHATSAPP"))
                .post(PATH + "/test").then().statusCode(200)
                .body("deliveryStatus", equalTo("FAILED"));

        given().auth().oauth2(adminToken).contentType(ContentType.JSON)
                .body(Map.of("identifier", msisdn, "identifierType", "MSISDN", "channel", "EMAIL"))
                .post(PATH + "/test").then().statusCode(400)
                .body("code", equalTo("OTP_CHANNEL_NOT_AVAILABLE"));
    }

    private Map<String, Object> current() {
        Map<String, Object> read = given().auth().oauth2(adminToken).get(PATH).then().statusCode(200)
                .extract().jsonPath().getMap("");
        Map<String, Object> body = new HashMap<>(read);
        body.remove("updatedAt");
        body.remove("updatedBy");
        return body;
    }

    private static Response put(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put(PATH);
    }

    private static String toJson(Map<String, Object> body) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
