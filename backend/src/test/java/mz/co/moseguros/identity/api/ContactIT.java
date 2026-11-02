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
import static org.hamcrest.Matchers.notNullValue;

import java.time.Duration;
import java.util.LinkedHashMap;
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
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
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
 * Contactos do cliente (S1-06 §3): acrescentar e trocar com confirmação por código, contactos de
 * outra conta, desafios de outra finalidade, remoção e login com o contacto novo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class ContactIT {

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

    private String tokenOf(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private static Response start(String token, String type, String value) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", type);
        body.put("value", value);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post("/api/v1/me/contacts");
    }

    private static Response verify(String token, String challengeId, String code) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("challengeId", challengeId, "code", code))
                .post("/api/v1/me/contacts/verify");
    }

    private static Response contacts(String token) {
        return given().auth().oauth2(token).get("/api/v1/me/contacts");
    }

    private int auditCount(String action, UserAccount account) {
        return owner.queryForObject("select count(*) from audit.audit_event where action = ? and entity_id = ?",
                Integer.class, action, account.getId().toString());
    }

    /** Pede e confirma o contacto; devolve a resposta da confirmação. */
    private Response addContact(String token, String type, String value, String destination) {
        Response started = start(token, type, value);
        started.then().statusCode(202);
        return verify(token, started.path("challengeId"), channels.lastTo(destination).code());
    }

    @Test
    void addEmailToAnAccountWithOnlyAPhone() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = tokenOf(customer);
        String phone = customer.getMsisdn().e164();
        String email = randomEmail();

        contacts(token).then().statusCode(200)
                .body("contacts.size()", equalTo(1))
                .body("contacts[0].type", equalTo("MSISDN"))
                .body("contacts[0].value", equalTo(phone))
                .body("contacts[0].verified", equalTo(true));

        Response started = start(token, "EMAIL", email.toUpperCase());
        started.then().statusCode(202)
                .body("channel", equalTo("EMAIL"))
                .body("resendAfterSeconds", equalTo(60))
                .body("fallbackUsed", equalTo(false))
                .body("expiresAt", notNullValue());
        String challengeId = started.path("challengeId");
        assertThat(owner.queryForObject("select purpose || ':' || user_id from identity.otp_challenge where id = ?::uuid",
                String.class, challengeId)).isEqualTo("VERIFY_CONTACT:" + customer.getId());

        String code = channels.lastTo(email).code();
        verify(token, challengeId, wrong(code)).then().statusCode(400)
                .body("code", equalTo("OTP_INVALID"))
                .body("attemptsRemaining", equalTo(2));

        verify(token, challengeId, code).then().statusCode(200)
                .body("contacts.type", equalTo(List.of("MSISDN", "EMAIL")))
                .body("contacts[1].value", equalTo(email))
                .body("contacts[1].verified", equalTo(true));
        verify(token, challengeId, code).then().statusCode(400).body("code", equalTo("OTP_ALREADY_USED"));

        given().auth().oauth2(token).get("/api/v1/me").then().statusCode(200)
                .body("email", equalTo(email))
                .body("emailVerified", equalTo(true));
        assertThat(auditCount("CUSTOMER.CONTACT_CHANGE", customer)).isEqualTo(1);
        String after = owner.queryForObject("""
                select after_state::text from audit.audit_event
                 where action = 'CUSTOMER.CONTACT_CHANGE' and entity_id = ?""", String.class,
                customer.getId().toString());
        assertThat(after).contains("EMAIL").contains("***").doesNotContain(email);

        start(token, "EMAIL", email).then().statusCode(409).body("code", equalTo("CONTACT_ALREADY_VERIFIED"));
    }

    @Test
    void changeThePhoneAndSignInWithTheNewNumber() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = tokenOf(customer);
        String oldPhone = customer.getMsisdn().e164();
        String newPhone = randomMsisdn();

        addContact(token, "MSISDN", newPhone, e164(newPhone)).then().statusCode(200)
                .body("contacts.size()", equalTo(1))
                .body("contacts[0].value", equalTo(e164(newPhone)))
                .body("contacts[0].verified", equalTo(true));
        String before = owner.queryForObject("""
                select before_state::text from audit.audit_event
                 where action = 'CUSTOMER.CONTACT_CHANGE' and entity_id = ?""", String.class,
                customer.getId().toString());
        assertThat(before).contains("MSISDN").doesNotContain(oldPhone).doesNotContain(oldPhone.substring(1));

        // O pedido de confirmação conta para o tempo de reenvio do mesmo número (S1-03).
        clock.advance(Duration.ofSeconds(61));
        Response login = requestOtp(newPhone, "MSISDN");
        login.then().statusCode(202);
        String accessToken = verifyOtp(login.path("challengeId"), channels.lastTo(e164(newPhone)).code())
                .then().statusCode(200)
                .body("isNewUser", equalTo(false))
                .extract().path("accessToken");
        given().auth().oauth2(accessToken).get("/api/v1/me").then().statusCode(200)
                .body("id", equalTo(customer.getId().toString()))
                .body("msisdn", equalTo(e164(newPhone)));
    }

    @Test
    void contactOfAnotherAccountIsAConflict() {
        UserAccount other = fixtures.customer("Outro Cliente");
        String token = tokenOf(fixtures.customer("Ana Mussa"));

        start(token, "MSISDN", other.getMsisdn().national()).then().statusCode(409)
                .contentType("application/problem+json")
                .body("code", equalTo("CONTACT_ALREADY_IN_USE"));
    }

    @Test
    void contactTakenByAnotherAccountBeforeTheCodeIsConfirmedIsAConflict() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        UserAccount other = fixtures.customer("Outro Cliente");
        String token = tokenOf(customer);
        String email = randomEmail();

        Response started = start(token, "EMAIL", email);
        started.then().statusCode(202);
        owner.update("update identity.user_account set email = ?, email_verified = true where id = ?", email,
                other.getId());

        verify(token, started.path("challengeId"), channels.lastTo(email).code()).then().statusCode(409)
                .body("code", equalTo("CONTACT_ALREADY_IN_USE"));
        contacts(token).then().body("contacts.size()", equalTo(1));
    }

    @Test
    void loginChallengeCannotVerifyAContact() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        String phone = randomMsisdn();
        Response login = requestOtp(phone, "MSISDN");
        login.then().statusCode(202);

        verify(token, login.path("challengeId"), channels.lastTo(e164(phone)).code()).then().statusCode(404)
                .body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));
    }

    @Test
    void challengeOfAnotherCustomerIsNotFound() {
        String tokenA = tokenOf(fixtures.customer("Ana Mussa"));
        String tokenB = tokenOf(fixtures.customer("Bela Cossa"));
        String email = randomEmail();
        Response started = start(tokenA, "EMAIL", email);
        started.then().statusCode(202);

        verify(tokenB, started.path("challengeId"), channels.lastTo(email).code()).then().statusCode(404)
                .body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));
    }

    @Test
    void contactVerificationCannotBeUsedToSignIn() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        String email = randomEmail();
        Response started = start(token, "EMAIL", email);
        started.then().statusCode(202);

        verifyOtp(started.path("challengeId"), channels.lastTo(email).code()).then().statusCode(404)
                .body("code", equalTo("OTP_CHALLENGE_NOT_FOUND"));
    }

    @Test
    void removingTheOnlyContactIsRefused() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = tokenOf(customer);

        given().auth().oauth2(token).delete("/api/v1/me/contacts/MSISDN").then().statusCode(400)
                .body("code", equalTo("LAST_CONTACT"));
        given().auth().oauth2(token).delete("/api/v1/me/contacts/EMAIL").then().statusCode(204);
        contacts(token).then().body("contacts.size()", equalTo(1));
        assertThat(auditCount("CUSTOMER.CONTACT_REMOVE", customer)).isZero();
    }

    @Test
    void removingOneOfTwoContacts() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = tokenOf(customer);
        String email = randomEmail();
        addContact(token, "EMAIL", email, email).then().statusCode(200);

        given().auth().oauth2(token).delete("/api/v1/me/contacts/MSISDN").then().statusCode(204);
        contacts(token).then().statusCode(200)
                .body("contacts.size()", equalTo(1))
                .body("contacts[0].type", equalTo("EMAIL"));
        assertThat(auditCount("CUSTOMER.CONTACT_REMOVE", customer)).isEqualTo(1);
        given().auth().oauth2(token).delete("/api/v1/me/contacts/EMAIL").then().statusCode(400)
                .body("code", equalTo("LAST_CONTACT"));
    }

    @Test
    void invalidValuesAreFieldErrors() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        start(token, "MSISDN", "12345").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("value"))
                .body("errors[0].code", equalTo("INVALID_MSISDN"));
        given().auth().oauth2(token).delete("/api/v1/me/contacts/FAX").then().statusCode(400)
                .body("errors[0].code", equalTo("TYPE_MISMATCH"));
    }

    @Test
    void resendTooSoonFollowsTheOtpRules() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        String email = randomEmail();
        start(token, "EMAIL", email).then().statusCode(202);
        start(token, "EMAIL", email).then().statusCode(429).body("code", equalTo("OTP_RESEND_TOO_SOON"));
    }

    @Test
    void staffIsForbidden() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Contactos");
        String token = tokenOf(fixtures.staff("Bruno Staff", insurer.getId(), "INSURER_ADMIN"));

        contacts(token).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        start(token, "EMAIL", randomEmail()).then().statusCode(403);
        given().auth().oauth2(token).delete("/api/v1/me/contacts/EMAIL").then().statusCode(403);
    }
}
