package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.identity.api.StaffLoginIT.login;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.application.TotpService;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.shared.domain.Channel;
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
 * Recuperação de palavra-passe de staff (S1-05 §2): pedido por e-mail, link de 30 minutos,
 * palavra-passe nova, sessões terminadas e limite de 3 e-mails por hora.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class PasswordResetIT {

    static final String PASSWORD = "Ilha-de-Mocambique-1818";
    static final String NEW_PASSWORD = "Costa-do-Sol-Marginal-77";
    static final Pattern LINK = Pattern.compile("http://localhost:5173/recuperar/([A-Za-z0-9_-]{43})");

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    TotpService totp;

    @Autowired
    MutableClock clock;

    JdbcTemplate owner;
    Organization insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        channels.reset();
        insurer = fixtures.organization(OrganizationType.INSURER, "Seg Recupera");
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
    }

    @Test
    void fullFlowChangesThePasswordUnlocksAndEndsSessions(CapturedOutput output) {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Esquecida", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        String refreshToken = login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(200)
                .extract().path("refreshToken");
        // 5 falhas: a conta fica bloqueada, mas pode recuperar a palavra-passe
        for (int attempt = 1; attempt <= 5; attempt++) {
            login(account.getEmail(), "Errada-" + attempt + "-palavra", "000000").then().statusCode(401);
        }

        forgot(account.getEmail().toUpperCase()).then().statusCode(202);
        FakeOtpDeliveryChannels.Sent mail = channels.lastTo(account.getEmail());
        assertThat(mail.channel()).isEqualTo(Channel.EMAIL);
        assertThat(mail.params().get("subject")).isEqualTo("Recuperar a palavra-passe da MoSeguros");
        assertThat(mail.text()).startsWith("Olá Esquecida,").contains("nos próximos 30 minutos");
        String token = tokenFrom(mail);
        assertThat(owner.queryForObject("select count(*) from notification.outbound_message "
                + "where purpose = 'PASSWORD_RESET' and status = 'SENT' and correlation_id = "
                + "(select id from identity.password_reset where user_id = ?)", Integer.class, account.getId()))
                .isEqualTo(1);

        reset(token, "curta").then().statusCode(400)
                .body("code", equalTo("PASSWORD_TOO_WEAK"))
                .body("errors[0].field", equalTo("newPassword"))
                .body("errors[0].code", equalTo("TOO_SHORT"));
        reset(token, NEW_PASSWORD).then().statusCode(204);

        Map<String, Object> row = owner.queryForMap(
                "select status, failed_attempts, locked_until from identity.user_account where id = ?",
                account.getId());
        assertThat(row.get("status")).isEqualTo("ACTIVE");
        assertThat(row.get("failed_attempts")).isEqualTo(0);
        assertThat(row.get("locked_until")).isNull();
        assertThat(owner.queryForObject("select used_at is not null from identity.password_reset where user_id = ?",
                Boolean.class, account.getId())).isTrue();
        assertThat(owner.queryForList("select distinct revoked_reason from identity.refresh_token where user_id = ?",
                String.class, account.getId())).containsExactly("PASSWORD_RESET");
        assertThat(owner.queryForList("select action from audit.audit_event where entity_id = ? order by occurred_at",
                String.class, account.getId().toString())).containsExactly("USER.LOCK", "USER.PASSWORD_RESET");

        // a sessão anterior terminou
        given().contentType(ContentType.JSON).body(Map.of("refreshToken", refreshToken))
                .post("/api/v1/auth/refresh").then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
        // o token não serve outra vez
        reset(token, NEW_PASSWORD + "!").then().statusCode(400).body("code", equalTo("RESET_TOKEN_INVALID"));

        // o TOTP não muda: entra com a palavra-passe nova e o mesmo segredo
        clock.advance(Duration.ofSeconds(TotpService.PERIOD_SECONDS));
        login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(401);
        login(account.getEmail(), NEW_PASSWORD, code(secret)).then().statusCode(200);

        assertThat(output.getAll()).doesNotContain(token).doesNotContain(PASSWORD).doesNotContain(NEW_PASSWORD);
    }

    @Test
    void tokenOlderThanThirtyMinutesFails() {
        UserAccount account = fixtures.activeStaff("Lenta", insurer.getId(), PASSWORD, totp.newSecret(),
                "UNDERWRITER");
        forgot(account.getEmail()).then().statusCode(202);
        String token = tokenFrom(channels.lastTo(account.getEmail()));

        clock.advance(Duration.ofMinutes(30).plusSeconds(1));
        reset(token, NEW_PASSWORD).then().statusCode(400).body("code", equalTo("RESET_TOKEN_INVALID"));
        reset("desconhecido", NEW_PASSWORD).then().statusCode(400).body("code", equalTo("RESET_TOKEN_INVALID"));
    }

    @Test
    void newRequestInvalidatesThePreviousOne() {
        UserAccount account = fixtures.activeStaff("Insistente", insurer.getId(), PASSWORD, totp.newSecret(),
                "UNDERWRITER");
        forgot(account.getEmail()).then().statusCode(202);
        String first = tokenFrom(channels.lastTo(account.getEmail()));
        forgot(account.getEmail()).then().statusCode(202);
        String second = tokenFrom(channels.lastTo(account.getEmail()));
        assertThat(second).isNotEqualTo(first);

        reset(first, NEW_PASSWORD).then().statusCode(400).body("code", equalTo("RESET_TOKEN_INVALID"));
        reset(second, NEW_PASSWORD).then().statusCode(204);
    }

    @Test
    void unknownEmailReturns202AndSendsNothing() {
        String email = "ninguem" + System.nanoTime() + "@exemplo.co.mz";
        int before = channels.sent().size();
        forgot(email).then().statusCode(202);
        forgot("nao-e-um-email").then().statusCode(202);
        assertThat(channels.sentTo(email)).isEmpty();
        assertThat(channels.sent()).hasSize(before);

        // conta desactivada ou por activar: também não envia
        UserAccount disabled = fixtures.activeStaff("Desactivada", insurer.getId(), PASSWORD, totp.newSecret(),
                "UNDERWRITER");
        fixtures.setAccountStatus(disabled.getId(), "DISABLED");
        forgot(disabled.getEmail()).then().statusCode(202);
        assertThat(channels.sentTo(disabled.getEmail())).isEmpty();
        assertThat(channels.sent()).hasSize(before);
    }

    @Test
    void fourthRequestInTheSameHourSendsNoEmail() {
        UserAccount account = fixtures.activeStaff("Repetitiva", insurer.getId(), PASSWORD, totp.newSecret(),
                "UNDERWRITER");
        for (int request = 1; request <= 4; request++) {
            forgot(account.getEmail()).then().statusCode(202);
        }
        assertThat(channels.sentTo(account.getEmail())).hasSize(3);
        assertThat(owner.queryForObject("select count(*) from identity.password_reset where user_id = ?",
                Integer.class, account.getId())).isEqualTo(3);

        clock.advance(Duration.ofMinutes(61));
        forgot(account.getEmail()).then().statusCode(202);
        assertThat(channels.sentTo(account.getEmail())).hasSize(4);
    }

    @Test
    void missingFieldsAreValidationErrors() {
        given().contentType(ContentType.JSON).body(Map.of()).post("/api/v1/auth/password/forgot")
                .then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"));
        given().contentType(ContentType.JSON).body(Map.of("token", "x")).post("/api/v1/auth/password/reset")
                .then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", contains("newPassword"));
    }

    // --- apoio ---

    private String code(String secret) {
        return totp.generate(secret, totp.currentStep());
    }

    private static Response forgot(String email) {
        return given().contentType(ContentType.JSON).body(Map.of("email", email))
                .post("/api/v1/auth/password/forgot");
    }

    private static Response reset(String token, String newPassword) {
        return given().contentType(ContentType.JSON).body(Map.of("token", token, "newPassword", newPassword))
                .post("/api/v1/auth/password/reset");
    }

    static String tokenFrom(FakeOtpDeliveryChannels.Sent mail) {
        Matcher matcher = LINK.matcher(mail.text());
        assertThat(matcher.find()).as("link in the password reset e-mail").isTrue();
        return matcher.group(1);
    }
}
