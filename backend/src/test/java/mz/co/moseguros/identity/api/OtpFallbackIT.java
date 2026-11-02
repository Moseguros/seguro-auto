package mz.co.moseguros.identity.api;

import static mz.co.moseguros.identity.api.OtpTestSupport.challenge;
import static mz.co.moseguros.identity.api.OtpTestSupport.e164;
import static mz.co.moseguros.identity.api.OtpTestSupport.eventually;
import static mz.co.moseguros.identity.api.OtpTestSupport.randomMsisdn;
import static mz.co.moseguros.identity.api.OtpTestSupport.receipt;
import static mz.co.moseguros.identity.api.OtpTestSupport.requestOtp;
import static mz.co.moseguros.identity.api.OtpTestSupport.verifyOtp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels.Mode;
import mz.co.moseguros.shared.domain.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Alternativa automática de canal: por falha síncrona do envio e por recibo de entrega.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class OtpFallbackIT {

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
    void synchronousSmsFailureFallsBackToWhatsApp() {
        channels.mode(Channel.SMS, Mode.FAIL);
        String msisdn = randomMsisdn();

        Response response = requestOtp(msisdn, "MSISDN");

        response.then().statusCode(202)
                .body("channel", equalTo("WHATSAPP"))
                .body("fallbackUsed", equalTo(true));
        String challengeId = response.path("challengeId");
        FakeOtpDeliveryChannels.Sent whatsapp = channels.lastTo(e164(msisdn));
        assertThat(whatsapp.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(channels.sentTo(e164(msisdn))).hasSize(1);

        List<String> statuses = owner.queryForList("""
                select delivery_status || ':' || channel || ':' || (superseded_at is not null)
                  from identity.otp_challenge where root_challenge_id = ?::uuid order by created_at""",
                String.class, challengeId);
        assertThat(statuses).containsExactly("FAILED:SMS:true", "SENT:WHATSAPP:false");
        challenge(challengeId).then().statusCode(200)
                .body("channel", equalTo("WHATSAPP"))
                .body("deliveryStatus", equalTo("SENT"))
                .body("fallbackUsed", equalTo(true));

        verifyOtp(challengeId, whatsapp.code()).then().statusCode(200).body("isNewUser", equalTo(true));
    }

    @Test
    void receiptFailureWithinThirtySecondsFallsBackToWhatsApp() {
        String msisdn = randomMsisdn();
        Response response = requestOtp(msisdn, "MSISDN");
        response.then().statusCode(202).body("channel", equalTo("SMS"));
        String challengeId = response.path("challengeId");
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(e164(msisdn));
        challenge(challengeId).then().body("channel", equalTo("SMS")).body("deliveryStatus", equalTo("SENT"));

        clock.advance(Duration.ofSeconds(20));
        receipt("mock-sms", OtpTestSupport.WEBHOOK_SECRET, sms.providerMessageId(), "FAILED").then().statusCode(204);

        assertThat(eventually(() -> "WHATSAPP".equals(challenge(challengeId).path("channel")), 10_000)).isTrue();
        challenge(challengeId).then().statusCode(200)
                .body("deliveryStatus", equalTo("SENT"))
                .body("fallbackUsed", equalTo(true));
        FakeOtpDeliveryChannels.Sent whatsapp = channels.lastTo(e164(msisdn));
        assertThat(whatsapp.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(whatsapp.code()).isNotEqualTo(sms.code());

        verifyOtp(challengeId, sms.code()).then().statusCode(400);
        verifyOtp(challengeId, whatsapp.code()).then().statusCode(200);
    }

    @Test
    void receiptFailureAfterThirtySecondsDoesNotFallBack() throws InterruptedException {
        String msisdn = randomMsisdn();
        String challengeId = requestOtp(msisdn, "MSISDN").then().statusCode(202).extract().path("challengeId");
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(e164(msisdn));

        clock.advance(Duration.ofSeconds(31));
        receipt("mock-sms", OtpTestSupport.WEBHOOK_SECRET, sms.providerMessageId(), "FAILED").then().statusCode(204);

        assertThat(eventually(() -> "FAILED".equals(challenge(challengeId).path("deliveryStatus")), 10_000)).isTrue();
        Thread.sleep(500);
        challenge(challengeId).then().body("channel", equalTo("SMS")).body("fallbackUsed", equalTo(false));
        assertThat(channels.sentTo(e164(msisdn))).hasSize(1);
        verifyOtp(challengeId, sms.code()).then().statusCode(200);
    }

    @Test
    void emailIsNeverAFallbackForAMobileNumber() {
        channels.mode(Channel.SMS, Mode.FAIL);
        channels.mode(Channel.WHATSAPP, Mode.FAIL);
        String msisdn = randomMsisdn();
        int sentBefore = channels.sent().size();

        requestOtp(msisdn, "MSISDN").then().statusCode(502).body("code", equalTo("OTP_DELIVERY_FAILED"));

        assertThat(channels.sent()).hasSize(sentBefore);
        assertThat(owner.queryForList("""
                select channel from identity.otp_challenge where identifier = ? order by created_at""",
                String.class, "258" + msisdn)).containsExactly("SMS", "WHATSAPP");
    }

    @Test
    void customerChoiceStillFallsBackToTheOtherMobileChannel() {
        channels.mode(Channel.WHATSAPP, Mode.FAIL);
        String msisdn = randomMsisdn();

        requestOtp(msisdn, "MSISDN", "WHATSAPP").then().statusCode(202)
                .body("channel", equalTo("SMS"))
                .body("fallbackUsed", equalTo(true));
    }
}
