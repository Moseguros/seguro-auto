package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Duration;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.application.TotpService;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
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
 * Login de staff com e-mail, palavra-passe e TOTP (S1-05 §1): sucesso, falhas, código repetido,
 * bloqueio de 15 minutos, conta desactivada e organização suspensa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class StaffLoginIT {

    static final String PASSWORD = "Maputo-Baixa-Acacias-2026";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

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
        insurer = fixtures.organization(OrganizationType.INSURER, "Seg Login");
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    @Test
    void rightCredentialsReturnTokensAndMeWorks() {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Gestora Login", insurer.getId(), PASSWORD, secret, "INSURER_ADMIN");

        Response response = login(account.getEmail().toUpperCase(), PASSWORD, code(secret));
        response.then().statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("expiresIn", equalTo(900));

        given().auth().oauth2((String) response.path("accessToken")).get("/api/v1/me")
                .then().statusCode(200)
                .body("type", equalTo("STAFF"))
                .body("email", equalTo(account.getEmail()))
                .body("roles", contains("INSURER_ADMIN"))
                .body("organization.id", equalTo(insurer.getId().toString()));

        Map<String, Object> row = owner.queryForMap("select failed_attempts, last_totp_step, last_login_at "
                + "from identity.user_account where id = ?", account.getId());
        assertThat(row.get("failed_attempts")).isEqualTo(0);
        assertThat(((Number) row.get("last_totp_step")).longValue()).isEqualTo(totp.currentStep());
        assertThat(row.get("last_login_at")).isNotNull();
        // sessão de staff: 12 h, sem extensão
        assertThat(owner.queryForObject("select count(*) from identity.refresh_token where user_id = ? "
                + "and session_expires_at is not null and revoked_at is null", Integer.class, account.getId()))
                .isEqualTo(1);
    }

    @Test
    void wrongPasswordAndWrongTotpGiveTheSame401() {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Distraído", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        String good = code(secret);

        Response wrongPassword = login(account.getEmail(), PASSWORD + "x", good);
        Response wrongCode = login(account.getEmail(), PASSWORD, OtpTestSupport.wrong(good));

        wrongPassword.then().statusCode(401).body("code", equalTo("INVALID_CREDENTIALS"));
        wrongCode.then().statusCode(401).body("code", equalTo("INVALID_CREDENTIALS"));
        assertThat((String) wrongPassword.path("detail")).isEqualTo(wrongCode.path("detail"));
        assertThat(failedAttempts(account)).isEqualTo(2);

        login(account.getEmail(), PASSWORD, good).then().statusCode(200);
        assertThat(failedAttempts(account)).isZero();
    }

    @Test
    void theSameTotpCodeDoesNotWorkTwice() {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Repetente", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        String code = code(secret);

        login(account.getEmail(), PASSWORD, code).then().statusCode(200);
        login(account.getEmail(), PASSWORD, code).then().statusCode(401).body("code", equalTo("INVALID_CREDENTIALS"));

        clock.advance(Duration.ofSeconds(TotpService.PERIOD_SECONDS));
        login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(200);
    }

    @Test
    void fifthFailureLocksTheAccountForFifteenMinutes(CapturedOutput output) {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Bloqueado", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        String good = code(secret);

        for (int attempt = 1; attempt <= 5; attempt++) {
            login(account.getEmail(), "Errada-" + attempt + "-palavra", good)
                    .then().statusCode(401).body("code", equalTo("INVALID_CREDENTIALS"));
        }
        assertThat(owner.queryForObject("select status from identity.user_account where id = ?", String.class,
                account.getId())).isEqualTo("LOCKED");
        assertThat(owner.queryForList("select action from audit.audit_event where entity_id = ?", String.class,
                account.getId().toString())).containsExactly("USER.LOCK");

        // mesmo as credenciais certas são recusadas, sem verificar a palavra-passe
        login(account.getEmail(), PASSWORD, good).then().statusCode(403)
                .header("Retry-After", notNullValue())
                .body("code", equalTo("ACCOUNT_LOCKED"))
                .body("retryAfterSeconds", greaterThan(14 * 60))
                .body("retryAfterSeconds", lessThanOrEqualTo(15 * 60));
        assertThat(failedAttempts(account)).isEqualTo(5);

        clock.advance(Duration.ofMinutes(15).plusSeconds(1));
        login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(200);
        Map<String, Object> row = owner.queryForMap(
                "select status, failed_attempts, locked_until from identity.user_account where id = ?",
                account.getId());
        assertThat(row.get("status")).isEqualTo("ACTIVE");
        assertThat(row.get("failed_attempts")).isEqualTo(0);
        assertThat(row.get("locked_until")).isNull();

        assertThat(output.getAll()).doesNotContain(PASSWORD).doesNotContain("Errada-1-palavra").doesNotContain(good);
    }

    @Test
    void lockExpiryAfterFailuresStartsCountingAgain() {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Paciente", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        for (int attempt = 1; attempt <= 5; attempt++) {
            login(account.getEmail(), "Errada-" + attempt + "-palavra", code(secret)).then().statusCode(401);
        }
        clock.advance(Duration.ofMinutes(16));
        login(account.getEmail(), "Ainda-errada-palavra", code(secret)).then().statusCode(401);
        Map<String, Object> row = owner.queryForMap(
                "select status, failed_attempts from identity.user_account where id = ?", account.getId());
        assertThat(row.get("status")).isEqualTo("ACTIVE");
        assertThat(row.get("failed_attempts")).isEqualTo(1);
    }

    @Test
    void disabledAccountWithRightCredentialsIsRefused() {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Desactivado", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        fixtures.setAccountStatus(account.getId(), "DISABLED");

        login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(403)
                .body("code", equalTo("ACCOUNT_DISABLED"));
        // credenciais erradas continuam a dar 401 e nunca bloqueiam uma conta desactivada
        for (int attempt = 1; attempt <= 5; attempt++) {
            login(account.getEmail(), "Errada-" + attempt + "-palavra", code(secret)).then().statusCode(401);
        }
        assertThat(owner.queryForObject("select status from identity.user_account where id = ?", String.class,
                account.getId())).isEqualTo("DISABLED");
    }

    @Test
    void suspendedOrganizationIsRefused() {
        String secret = totp.newSecret();
        Organization suspended = fixtures.organization(OrganizationType.INSURER, "Seg Suspensa");
        UserAccount account = fixtures.activeStaff("Suspenso", suspended.getId(), PASSWORD, secret, "UNDERWRITER");
        fixtures.suspendOrganization(suspended.getId());

        login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(403)
                .body("code", equalTo("ORGANIZATION_SUSPENDED"));
    }

    @Test
    void platformStaffSignsInWithoutOrganization() {
        String secret = totp.newSecret();
        UserAccount account = fixtures.activeStaff("Plataforma", null, PASSWORD, secret, "PLATFORM_ADMIN");

        login(account.getEmail(), PASSWORD, code(secret)).then().statusCode(200);
    }

    @Test
    void unknownEmailAndPendingAccountGive401() {
        login("ninguem" + System.nanoTime() + "@exemplo.co.mz", PASSWORD, "123456").then().statusCode(401)
                .body("code", equalTo("INVALID_CREDENTIALS"));
        login("nao-e-um-email", PASSWORD, "123456").then().statusCode(401)
                .body("code", equalTo("INVALID_CREDENTIALS"));

        UserAccount pending = fixtures.staff("Pendente", insurer.getId(), "UNDERWRITER");
        fixtures.setAccountStatus(pending.getId(), "PENDING_ACTIVATION");
        login(pending.getEmail(), PASSWORD, "123456").then().statusCode(401)
                .body("code", equalTo("INVALID_CREDENTIALS"));

        // convite aceite (ou acesso reposto) mas ainda não activado: já tem palavra-passe e TOTP
        String secret = totp.newSecret();
        UserAccount accepted = fixtures.activeStaff("Aceite", insurer.getId(), PASSWORD, secret, "UNDERWRITER");
        fixtures.setAccountStatus(accepted.getId(), "PENDING_ACTIVATION");
        login(accepted.getEmail(), PASSWORD, code(secret)).then().statusCode(401)
                .body("code", equalTo("INVALID_CREDENTIALS"));
        login(accepted.getEmail(), PASSWORD + "x", code(secret)).then().statusCode(401)
                .body("code", equalTo("INVALID_CREDENTIALS"));
        assertThat(failedAttempts(accepted)).isZero();

        given().contentType(ContentType.JSON).body(Map.of("email", "a@b.co.mz"))
                .post("/api/v1/auth/login").then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"));
    }

    // --- apoio ---

    private String code(String secret) {
        return totp.generate(secret, totp.currentStep());
    }

    private int failedAttempts(UserAccount account) {
        return owner.queryForObject("select failed_attempts from identity.user_account where id = ?", Integer.class,
                account.getId());
    }

    static Response login(String email, String password, String totpCode) {
        return given().contentType(ContentType.JSON).header("User-Agent", "JUnit")
                .body(Map.of("email", email, "password", password, "totpCode", totpCode))
                .post("/api/v1/auth/login");
    }
}
