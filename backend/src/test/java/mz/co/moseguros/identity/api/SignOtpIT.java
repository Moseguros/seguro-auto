package mz.co.moseguros.identity.api;

import static mz.co.moseguros.identity.api.OtpTestSupport.eventually;
import static mz.co.moseguros.identity.api.OtpTestSupport.receipt;
import static mz.co.moseguros.identity.api.OtpTestSupport.requestOtp;
import static mz.co.moseguros.identity.api.OtpTestSupport.verifyOtp;
import static mz.co.moseguros.identity.api.OtpTestSupport.wrong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.SignOtpChallenge;
import mz.co.moseguros.identity.SignOtpRequest;
import mz.co.moseguros.identity.SignOtpVerification;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels.Mode;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
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
 * Código de assinatura de um documento ({@code IdentityApi.requestSignOtp} / {@code verifySignOtp}, S4-04
 * §4): ligação ao hash do documento, separação do login, alternativa de canal, limites e telemóvel
 * verificado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class SignOtpIT {

    static final String HASH = "9f2c4b7e0d1a3c5e7f9b2d4a6c8e0f1b3d5a7c9e1f3b5d7a9c1e3f5b7d9a1c3e";
    static final String OTHER_HASH = "0".repeat(64);
    static final String NBSP = " ";

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

    private static SignOtpRequest request(UserAccount signer, String hash) {
        return new SignOtpRequest(signer.getId(), UUID.randomUUID(), hash, "PR-2027-000123", "Seguradora Demo",
                Money.ofCentavos(466300), null, null);
    }

    private static String destination(UserAccount account) {
        return account.getMsisdn().e164();
    }

    @Test
    void theRightCodeWithTheRightHashVerifiesAndOnlyForThatDocument() {
        UserAccount signer = fixtures.customer("Ana Assinatura");

        SignOtpChallenge challenge = identityApi.requestSignOtp(request(signer, HASH));

        assertThat(challenge.channel()).isEqualTo(Channel.SMS);
        assertThat(challenge.destinationMasked()).isEqualTo(signer.getMsisdn().masked());
        assertThat(challenge.fallbackUsed()).isFalse();
        assertThat(challenge.resendAfterSeconds()).isEqualTo(60);
        assertThat(challenge.debugCode()).isNull();
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(destination(signer));
        assertThat(sms.text()).contains("assina a proposta PR-2027-000123 da Seguradora Demo")
                .contains("4" + NBSP + "663,00 MT").contains(sms.code());
        assertThat(owner.queryForObject("""
                select purpose || ':' || (sign_context->>'documentHash') from identity.otp_challenge where id = ?""",
                String.class, challenge.challengeId())).isEqualTo("SIGN:" + HASH);
        assertThat(owner.queryForObject("select purpose from notification.outbound_message where correlation_id = ?",
                String.class, challenge.challengeId())).isEqualTo("SIGN_OTP");

        // O mesmo código com outro hash não serve (e conta uma tentativa).
        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), sms.code(),
                OTHER_HASH)).isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_INVALID");
                    assertThat(e.getProperties()).containsEntry("attemptsRemaining", 2);
                });
        assertThat(owner.queryForObject("select attempts from identity.otp_challenge where id = ?", Integer.class,
                challenge.challengeId())).isEqualTo(1);

        SignOtpVerification verification = identityApi.verifySignOtp(signer.getId(), challenge.challengeId(),
                sms.code(), HASH);
        assertThat(verification.challengeId()).isEqualTo(challenge.challengeId());
        assertThat(verification.channel()).isEqualTo(Channel.SMS);
        assertThat(verification.destinationMasked()).isEqualTo(signer.getMsisdn().masked());
        assertThat(verification.verifiedAt()).isNotNull();

        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), sms.code(), HASH))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("OTP_ALREADY_USED"));
    }

    @Test
    void signAndLoginChallengesDoNotMix() {
        UserAccount signer = fixtures.customer("Bruno Separado");
        SignOtpChallenge sign = identityApi.requestSignOtp(request(signer, HASH));
        String signCode = channels.lastTo(destination(signer)).code();

        // O desafio de assinatura não serve para entrar.
        verifyOtp(sign.challengeId().toString(), signCode).then().statusCode(404)
                .body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));

        // Nem um desafio de login serve para assinar.
        clock.advance(Duration.ofSeconds(61));
        String national = signer.getMsisdn().value().substring(3);
        Response login = requestOtp(national, "MSISDN");
        login.then().statusCode(202);
        String loginCode = channels.lastTo(destination(signer)).code();
        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(),
                UUID.fromString(login.path("challengeId")), loginCode, HASH))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_CHALLENGE_NOT_FOUND");
                    assertThat(e.getHttpStatus()).isEqualTo(404);
                });

        // Nem o desafio de assinatura de outro utilizador.
        UserAccount other = fixtures.customer("Outro Cliente");
        assertThatThrownBy(() -> identityApi.verifySignOtp(other.getId(), sign.challengeId(), signCode, HASH))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("OTP_CHALLENGE_NOT_FOUND"));
        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), UUID.randomUUID(), signCode, HASH))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("OTP_CHALLENGE_NOT_FOUND"));
        // O desafio de assinatura continua válido.
        assertThat(identityApi.verifySignOtp(signer.getId(), sign.challengeId(), signCode, HASH)).isNotNull();
    }

    @Test
    void synchronousSmsFailureFallsBackToWhatsAppWithTheSignTemplate() {
        UserAccount signer = fixtures.customer("Carla Alternativa");
        channels.mode(Channel.SMS, Mode.FAIL);

        SignOtpChallenge challenge = identityApi.requestSignOtp(request(signer, HASH));

        assertThat(challenge.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(challenge.fallbackUsed()).isTrue();
        FakeOtpDeliveryChannels.Sent whatsapp = channels.lastTo(destination(signer));
        assertThat(whatsapp.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(whatsapp.params()).containsEntry("template", "moseguros_sign_otp")
                .containsEntry("ref", "PR-2027-000123").containsEntry("insurer", "Seguradora Demo")
                .containsEntry("total", "4" + NBSP + "663,00 MT");
        List<String> chain = owner.queryForList("""
                select delivery_status || ':' || channel || ':' || purpose || ':' || (sign_context->>'documentHash')
                  from identity.otp_challenge where root_challenge_id = ? order by created_at""",
                String.class, challenge.challengeId());
        assertThat(chain).containsExactly("FAILED:SMS:SIGN:" + HASH, "SENT:WHATSAPP:SIGN:" + HASH);

        // O código da alternativa também só serve para o mesmo documento.
        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), challenge.challengeId(),
                whatsapp.code(), OTHER_HASH)).isInstanceOf(DomainException.class);
        assertThat(identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), whatsapp.code(), HASH)
                .channel()).isEqualTo(Channel.WHATSAPP);
    }

    @Test
    void receiptFailureFallsBackToWhatsAppForTheSameDocument() {
        UserAccount signer = fixtures.customer("Dina Recibo");
        SignOtpChallenge challenge = identityApi.requestSignOtp(request(signer, HASH));
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(destination(signer));

        clock.advance(Duration.ofSeconds(10));
        receipt("mock-sms", OtpTestSupport.WEBHOOK_SECRET, sms.providerMessageId(), "FAILED").then().statusCode(204);

        assertThat(eventually(() -> channels.sentTo(destination(signer)).size() == 2, 10_000)).isTrue();
        FakeOtpDeliveryChannels.Sent whatsapp = channels.lastTo(destination(signer));
        assertThat(whatsapp.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(whatsapp.params()).containsEntry("template", "moseguros_sign_otp");
        assertThat(identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), whatsapp.code(), HASH)
                .channel()).isEqualTo(Channel.WHATSAPP);
    }

    @Test
    void signRequestsCountForTheHourlyLimitAndTheResendDelay() {
        UserAccount signer = fixtures.customer("Eva Limites");
        String national = signer.getMsisdn().value().substring(3);
        // O tempo mínimo entre pedidos vale também para um código de assinatura logo a seguir a um de login.
        requestOtp(national, "MSISDN").then().statusCode(202);
        assertThatThrownBy(() -> identityApi.requestSignOtp(request(signer, HASH)))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_RESEND_TOO_SOON");
                    assertThat(e.getHttpStatus()).isEqualTo(429);
                    assertThat(e.getProperties()).containsKey(DomainException.RETRY_AFTER_SECONDS);
                });
        for (int i = 0; i < 4; i++) {
            clock.advance(Duration.ofSeconds(61));
            identityApi.requestSignOtp(request(signer, HASH));
        }
        // 1 login + 4 assinaturas = 5 pedidos na última hora (o limite): o sexto é recusado nos dois fluxos.
        clock.advance(Duration.ofSeconds(61));
        assertThatThrownBy(() -> identityApi.requestSignOtp(request(signer, HASH)))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_RATE_LIMITED");
                    assertThat(e.getHttpStatus()).isEqualTo(429);
                });
        requestOtp(national, "MSISDN").then().statusCode(429).body("code", equalTo("OTP_RATE_LIMITED"));
    }

    @Test
    void aNewSignRequestSupersedesThePreviousOne() {
        UserAccount signer = fixtures.customer("Filipe Novo Pedido");
        SignOtpChallenge first = identityApi.requestSignOtp(request(signer, HASH));
        String firstCode = channels.lastTo(destination(signer)).code();
        clock.advance(Duration.ofSeconds(61));
        SignOtpChallenge second = identityApi.requestSignOtp(request(signer, OTHER_HASH));

        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), first.challengeId(), firstCode, HASH))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("OTP_SUPERSEDED"));
        assertThat(identityApi.verifySignOtp(signer.getId(), second.challengeId(),
                channels.lastTo(destination(signer)).code(), OTHER_HASH)).isNotNull();
    }

    @Test
    void verificationMarksTheMobileNumberAsVerified() {
        UserAccount signer = fixtures.customer("Gil Por Verificar");
        owner.update("update identity.user_account set msisdn_verified = false where id = ?", signer.getId());

        SignOtpChallenge challenge = identityApi.requestSignOtp(request(signer, HASH));
        identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), channels.lastTo(destination(signer)).code(),
                HASH);

        assertThat(owner.queryForObject("select msisdn_verified from identity.user_account where id = ?",
                Boolean.class, signer.getId())).isTrue();
    }

    @Test
    void withoutMobileTheVerifiedEmailIsUsedAndWithoutBothTheRequestFails() {
        UserAccount signer = fixtures.customer("Helena Email");
        String email = "helena" + System.nanoTime() + "@exemplo.co.mz";
        owner.update("update identity.user_account set msisdn = null, msisdn_verified = false, email = ?, "
                + "email_verified = true where id = ?", email, signer.getId());

        SignOtpChallenge challenge = identityApi.requestSignOtp(request(signer, HASH));
        assertThat(challenge.channel()).isEqualTo(Channel.EMAIL);
        assertThat(challenge.destinationMasked()).isEqualTo("h***@exemplo.co.mz");
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo(email);
        assertThat(sent.params()).containsEntry("subject", "Código para assinar a proposta PR-2027-000123");

        owner.update("update identity.user_account set email_verified = false where id = ?", signer.getId());
        assertThatThrownBy(() -> identityApi.requestSignOtp(request(signer, HASH)))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("SIGNER_CONTACT_MISSING");
                    assertThat(e.getHttpStatus()).isEqualTo(409);
                });
    }

    @Test
    void wrongCodesAreCountedUntilTheLimit() {
        UserAccount signer = fixtures.customer("Ivo Tentativas");
        SignOtpChallenge challenge = identityApi.requestSignOtp(request(signer, HASH));
        String code = channels.lastTo(destination(signer)).code();

        for (int remaining = 2; remaining >= 1; remaining--) {
            int expected = remaining;
            assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), wrong(code),
                    HASH)).isInstanceOfSatisfying(DomainException.class,
                    e -> assertThat(e.getProperties()).containsEntry("attemptsRemaining", expected));
        }
        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), wrong(code), HASH))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("OTP_ATTEMPTS_EXCEEDED"));
        assertThatThrownBy(() -> identityApi.verifySignOtp(signer.getId(), challenge.challengeId(), code, HASH))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("OTP_ATTEMPTS_EXCEEDED"));
    }
}
