package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.identity.api.OtpTestSupport.e164;
import static mz.co.moseguros.identity.api.OtpTestSupport.randomEmail;
import static mz.co.moseguros.identity.api.OtpTestSupport.randomMsisdn;
import static mz.co.moseguros.identity.api.OtpTestSupport.requestOtp;
import static mz.co.moseguros.identity.api.OtpTestSupport.verifyOtp;
import static mz.co.moseguros.identity.api.OtpTestSupport.wrong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Duration;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Fluxo do cliente: pedido e validação do código por SMS e e-mail, criação da conta, erros do
 * código e limites de pedidos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class OtpFlowIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    JdbcTemplate owner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        OtpTestSupport.resetSettings(owner);
        channels.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
        OtpTestSupport.resetSettings(owner);
    }

    @Test
    void smsLoginCreatesTheAccountAndSecondLoginIsNotNew() {
        String msisdn = randomMsisdn();

        Response requested = requestOtp(msisdn, "MSISDN");
        requested.then().statusCode(202)
                .body("channel", equalTo("SMS"))
                .body("fallbackUsed", equalTo(false))
                .body("resendAfterSeconds", equalTo(60))
                .body("expiresAt", notNullValue());
        assertThat(requested.getHeader("X-Debug-Otp")).isNull();
        String challengeId = requested.path("challengeId");
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(e164(msisdn));
        assertThat(sms.channel()).hasToString("SMS");
        assertThat(sms.code()).matches("\\d{6}");
        assertThat(sms.text()).contains(sms.code()).contains("Válido por 5 min");

        Response login = verifyOtp(challengeId, sms.code());
        login.then().statusCode(200)
                .body("isNewUser", equalTo(true))
                .body("expiresIn", equalTo(900));
        String accessToken = login.path("accessToken");
        given().auth().oauth2(accessToken).get("/api/v1/me").then().statusCode(200)
                .body("type", equalTo("CUSTOMER"))
                .body("msisdn", equalTo(e164(msisdn)))
                .body("msisdnVerified", equalTo(true))
                .body("roles", hasItem("CUSTOMER"))
                .body("profileComplete", equalTo(false));
        String userId = given().auth().oauth2(accessToken).get("/api/v1/me").path("id");
        assertThat(owner.queryForObject(
                "select count(*) from audit.audit_event where action = 'CUSTOMER.REGISTER' and entity_id = ?",
                Integer.class, userId)).isEqualTo(1);
        assertThat(owner.queryForObject("select count(*) from identity.customer_profile where user_id = ?::uuid",
                Integer.class, userId)).isEqualTo(1);

        clock.advance(Duration.ofSeconds(61));
        String second = requestOtp(msisdn, "MSISDN").then().statusCode(202).extract().path("challengeId");
        verifyOtp(second, channels.lastCodeTo(e164(msisdn)).code()).then().statusCode(200)
                .body("isNewUser", equalTo(false));
        assertThat(owner.queryForObject("select last_login_at is not null from identity.user_account where id = ?::uuid",
                Boolean.class, userId)).isTrue();
    }

    @Test
    void emailLoginWorksTheSame() {
        String email = randomEmail();

        Response requested = requestOtp(email.toUpperCase(), "EMAIL");
        requested.then().statusCode(202).body("channel", equalTo("EMAIL"));
        FakeOtpDeliveryChannels.Sent mail = channels.lastTo(email);
        assertThat(mail.params()).containsEntry("subject", "O seu código MoSeguros");
        assertThat(mail.text()).contains(mail.code());

        String accessToken = verifyOtp(requested.path("challengeId"), mail.code()).then().statusCode(200)
                .body("isNewUser", equalTo(true)).extract().path("accessToken");
        given().auth().oauth2(accessToken).get("/api/v1/me").then().statusCode(200)
                .body("email", equalTo(email))
                .body("emailVerified", equalTo(true));

        clock.advance(Duration.ofSeconds(61));
        String second = requestOtp(email, "EMAIL").then().statusCode(202).extract().path("challengeId");
        verifyOtp(second, channels.lastCodeTo(email).code()).then().statusCode(200).body("isNewUser", equalTo(false));
    }

    @Test
    void customerCanChooseTheChannel() {
        String msisdn = randomMsisdn();
        requestOtp(msisdn, "MSISDN", "WHATSAPP").then().statusCode(202)
                .body("channel", equalTo("WHATSAPP"))
                .body("fallbackUsed", equalTo(false));
        assertThat(channels.lastTo(e164(msisdn)).channel()).hasToString("WHATSAPP");

        requestOtp(randomMsisdn(), "MSISDN", "EMAIL").then().statusCode(400)
                .body("code", equalTo("OTP_CHANNEL_NOT_AVAILABLE"));
    }

    @Test
    void expiredCodeIsRejected() {
        String msisdn = randomMsisdn();
        String challengeId = requestOtp(msisdn, "MSISDN").path("challengeId");
        String code = channels.lastCodeTo(e164(msisdn)).code();

        clock.advance(Duration.ofSeconds(301));

        verifyOtp(challengeId, code).then().statusCode(400).body("code", equalTo("OTP_EXPIRED"));
    }

    @Test
    void tooManyWrongAttemptsBlockTheCode() {
        String msisdn = randomMsisdn();
        String challengeId = requestOtp(msisdn, "MSISDN").path("challengeId");
        String code = channels.lastCodeTo(e164(msisdn)).code();

        verifyOtp(challengeId, wrong(code)).then().statusCode(400)
                .body("code", equalTo("OTP_INVALID")).body("attemptsRemaining", equalTo(2));
        verifyOtp(challengeId, wrong(code)).then().statusCode(400)
                .body("code", equalTo("OTP_INVALID")).body("attemptsRemaining", equalTo(1));
        verifyOtp(challengeId, wrong(code)).then().statusCode(400).body("code", equalTo("OTP_ATTEMPTS_EXCEEDED"));
        verifyOtp(challengeId, code).then().statusCode(400).body("code", equalTo("OTP_ATTEMPTS_EXCEEDED"));

        assertThat(owner.queryForObject("select attempts from identity.otp_challenge where id = ?::uuid",
                Integer.class, challengeId)).isEqualTo(3);
    }

    @Test
    void codeCannotBeUsedTwice() {
        String msisdn = randomMsisdn();
        String challengeId = requestOtp(msisdn, "MSISDN").path("challengeId");
        String code = channels.lastCodeTo(e164(msisdn)).code();

        verifyOtp(challengeId, code).then().statusCode(200);
        verifyOtp(challengeId, code).then().statusCode(400).body("code", equalTo("OTP_ALREADY_USED"));
    }

    @Test
    void codeOfAnEarlierRequestIsSuperseded() {
        String msisdn = randomMsisdn();
        String first = requestOtp(msisdn, "MSISDN").path("challengeId");
        String firstCode = channels.lastCodeTo(e164(msisdn)).code();
        clock.advance(Duration.ofSeconds(61));
        String second = requestOtp(msisdn, "MSISDN").then().statusCode(202).extract().path("challengeId");

        verifyOtp(first, firstCode).then().statusCode(400).body("code", equalTo("OTP_SUPERSEDED"));
        verifyOtp(second, channels.lastCodeTo(e164(msisdn)).code()).then().statusCode(200);
    }

    @Test
    void unknownChallengeIsNotFound() {
        verifyOtp("0192f0c4-0000-7000-8000-000000000000", "123456").then().statusCode(404)
                .body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));
        OtpTestSupport.challenge("0192f0c4-0000-7000-8000-000000000000").then().statusCode(404)
                .body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));
    }

    @Test
    void hourlyLimitReturns429WithRetryAfter() {
        String msisdn = randomMsisdn();
        for (int i = 0; i < 5; i++) {
            requestOtp(msisdn, "MSISDN").then().statusCode(202);
            clock.advance(Duration.ofSeconds(61));
        }

        Response limited = requestOtp(msisdn, "MSISDN");

        limited.then().statusCode(429)
                .contentType("application/problem+json")
                .body("code", equalTo("OTP_RATE_LIMITED"))
                .body("retryAfterSeconds", greaterThan(0));
        int retryAfter = Integer.parseInt(limited.getHeader("Retry-After"));
        assertThat(retryAfter).isBetween(3600 - 5 * 61 - 5, 3600 - 5 * 61 + 5);

        clock.advance(Duration.ofSeconds(retryAfter + 1));
        requestOtp(msisdn, "MSISDN").then().statusCode(202);
    }

    @Test
    void resendBeforeTheTimeReturns429() {
        String msisdn = randomMsisdn();
        requestOtp(msisdn, "MSISDN").then().statusCode(202);
        clock.advance(Duration.ofSeconds(20));

        Response tooSoon = requestOtp(msisdn, "MSISDN");

        tooSoon.then().statusCode(429)
                .body("code", equalTo("OTP_RESEND_TOO_SOON"))
                .body("retryAfterSeconds", equalTo(40));
        assertThat(tooSoon.getHeader("Retry-After")).isEqualTo("40");
    }

    @Test
    void invalidIdentifierIsAFieldError() {
        requestOtp("12345", "MSISDN").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("identifier"))
                .body("errors[0].code", equalTo("INVALID_MSISDN"));
        requestOtp("sem-arroba", "EMAIL").then().statusCode(400)
                .body("errors[0].field", equalTo("identifier"))
                .body("errors[0].code", equalTo("INVALID_EMAIL"));
        requestOtp("", "EMAIL").then().statusCode(400)
                .body("errors[0].field", equalTo("identifier"))
                .body("errors[0].code", equalTo("NOT_BLANK"));
    }

    @Test
    void disabledAccountCannotSignIn() {
        var customer = fixtures.customer("Conta Desactivada");
        fixtures.setAccountStatus(customer.getId(), "DISABLED");
        String national = customer.getMsisdn().national();

        String challengeId = requestOtp(national, "MSISDN").then().statusCode(202).extract().path("challengeId");
        verifyOtp(challengeId, channels.lastCodeTo(e164(national)).code()).then().statusCode(403)
                .body("code", equalTo("ACCOUNT_DISABLED"));
    }

    @Test
    void outboundMessageNeverStoresTheCodeOrTheFullNumber() {
        String msisdn = randomMsisdn();
        String challengeId = requestOtp(msisdn, "MSISDN").path("challengeId");
        String code = channels.lastCodeTo(e164(msisdn)).code();

        Map<String, Object> row = owner.queryForMap(
                "select * from notification.outbound_message where correlation_id = ?::uuid", challengeId);
        assertThat(row.get("status")).isEqualTo("SENT");
        assertThat(row.get("provider")).isEqualTo("mock-sms");
        assertThat(row.get("destination_masked")).isEqualTo("+258*****" + msisdn.substring(5));
        assertThat(row.values()).noneMatch(v -> v != null && (v.toString().contains(code)
                || v.toString().contains(msisdn)));
    }

    @Test
    void logsOfACompleteFlowNeverContainTheCode(CapturedOutput output) {
        owner.update("update identity.otp_settings set code_length = 8");
        String msisdn = randomMsisdn();
        String challengeId = requestOtp(msisdn, "MSISDN").then().statusCode(202).extract().path("challengeId");
        String code = channels.lastCodeTo(e164(msisdn)).code();
        verifyOtp(challengeId, wrong(code)).then().statusCode(400);
        verifyOtp(challengeId, code).then().statusCode(200);

        assertThat(code).hasSize(8);
        assertThat(output.getAll()).contains(challengeId)
                .doesNotContain(code)
                .doesNotContain("258" + msisdn);
        String hash = owner.queryForObject("select code_hash from identity.otp_challenge where id = ?::uuid",
                String.class, challengeId);
        assertThat(output.getAll()).doesNotContain(hash);
    }
}
