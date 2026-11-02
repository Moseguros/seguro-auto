package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.identity.api.StaffLoginIT.login;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Duration;
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
import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.application.TotpService;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
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
 * Gestão de utilizadores de staff (S1-05 §3): listar, consultar, alterar perfis, desactivar,
 * reactivar, desbloquear e repor o acesso, com o isolamento entre seguradoras.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class StaffUserIT {

    static final String PASSWORD = "Matola-Rio-Infulene-2026";
    static final String NEW_PASSWORD = "Xai-Xai-Praia-Limpopo-31";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    TotpService totp;

    @Autowired
    MutableClock clock;

    JdbcTemplate owner;
    Organization insurerA;
    Organization insurerB;
    UserAccount managerA;
    String manager;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        channels.reset();
        insurerA = fixtures.organization(OrganizationType.INSURER, "Seg A");
        insurerB = fixtures.organization(OrganizationType.INSURER, "Seg B");
        managerA = fixtures.staff("Gestora A", insurerA.getId(), "INSURER_ADMIN");
        manager = token(managerA);
        admin = token(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"));
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
    }

    @Test
    void managerOfInsurerAListsTheirUsersAndDoesNotSeeInsurerB() {
        UserAccount underwriter = fixtures.staff("Subscritor Zé", insurerA.getId(), "UNDERWRITER");
        UserAccount userB = fixtures.staff("Alheio", insurerB.getId(), "UNDERWRITER");

        given().auth().oauth2(manager).get("/api/v1/organizations/" + insurerA.getId() + "/users")
                .then().statusCode(200)
                .body("totalElements", equalTo(2))
                .body("content.id", containsInAnyOrder(managerA.getId().toString(), underwriter.getId().toString()))
                .body("content.email", not(hasItem(userB.getEmail())))
                .body("content[0].status", equalTo("ACTIVE"))
                .body("content[0].version", notNullValue())
                .body("content[0].createdAt", notNullValue());
        given().auth().oauth2(manager).queryParam("q", "SUBSCRITOR")
                .get("/api/v1/organizations/" + insurerA.getId() + "/users")
                .then().statusCode(200).body("totalElements", equalTo(1))
                .body("content[0].displayName", equalTo("Subscritor Zé"))
                .body("content[0].roles", contains("UNDERWRITER"));
        given().auth().oauth2(manager).queryParam("q", underwriter.getEmail().substring(0, 10))
                .get("/api/v1/organizations/" + insurerA.getId() + "/users")
                .then().statusCode(200).body("totalElements", equalTo(1));

        given().auth().oauth2(manager).get("/api/v1/users/" + underwriter.getId())
                .then().statusCode(200).body("email", equalTo(underwriter.getEmail()))
                .body("roles", contains("UNDERWRITER"));
        given().auth().oauth2(manager).get("/api/v1/users/" + userB.getId())
                .then().statusCode(404).body("code", equalTo("USER_NOT_FOUND"));
        given().auth().oauth2(manager).get("/api/v1/organizations/" + insurerB.getId() + "/users")
                .then().statusCode(404).body("code", equalTo("ORGANIZATION_NOT_FOUND"));
        given().auth().oauth2(manager).post("/api/v1/users/" + userB.getId() + "/disable")
                .then().statusCode(404).body("code", equalTo("USER_NOT_FOUND"));
        given().auth().oauth2(manager).get("/api/v1/admin/users")
                .then().statusCode(403).body("code", equalTo("FORBIDDEN"));

        // a plataforma vê as duas seguradoras e o seu próprio staff
        given().auth().oauth2(admin).get("/api/v1/users/" + userB.getId()).then().statusCode(200);
        given().auth().oauth2(admin).queryParam("size", 100).get("/api/v1/admin/users")
                .then().statusCode(200).body("content.roles.flatten()", hasItem("PLATFORM_ADMIN"))
                .body("content.email", not(hasItem(underwriter.getEmail())));
    }

    @Test
    void lockedStatusIsShownOnlyWhileTheLockLasts() {
        UserAccount locked = fixtures.staff("Bloqueada", insurerA.getId(), "UNDERWRITER");
        owner.update("update identity.user_account set status = 'LOCKED', failed_attempts = 5, "
                + "locked_until = now() + interval '10 minutes' where id = ?", locked.getId());

        given().auth().oauth2(manager).queryParam("status", "LOCKED")
                .get("/api/v1/organizations/" + insurerA.getId() + "/users")
                .then().statusCode(200).body("content.id", contains(locked.getId().toString()));

        clock.advance(Duration.ofMinutes(11));
        given().auth().oauth2(manager).queryParam("status", "LOCKED")
                .get("/api/v1/organizations/" + insurerA.getId() + "/users")
                .then().statusCode(200).body("totalElements", equalTo(0));
        given().auth().oauth2(manager).get("/api/v1/users/" + locked.getId())
                .then().statusCode(200).body("status", equalTo("ACTIVE"));
        clock.reset();

        given().auth().oauth2(manager).post("/api/v1/users/" + locked.getId() + "/unlock")
                .then().statusCode(200).body("status", equalTo("ACTIVE"));
        assertThat(owner.queryForObject("select failed_attempts from identity.user_account where id = ?",
                Integer.class, locked.getId())).isZero();
        given().auth().oauth2(manager).post("/api/v1/users/" + locked.getId() + "/unlock")
                .then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        assertThat(actions(locked)).containsExactly("USER.UNLOCK");
    }

    @Test
    void managerChangesRolesWithVersionCheck() {
        UserAccount underwriter = fixtures.staff("Mudança", insurerA.getId(), "UNDERWRITER");
        long version = ((Number) given().auth().oauth2(manager).get("/api/v1/users/" + underwriter.getId())
                .path("version")).longValue();

        Response updated = updateRoles(manager, underwriter, List.of("CLAIMS_HANDLER", "UNDERWRITER"), version);
        updated.then().statusCode(200)
                .body("roles", contains("CLAIMS_HANDLER", "UNDERWRITER"))
                .body("version", greaterThan((int) version));
        assertThat(owner.queryForList("select role_code from identity.user_role where user_id = ? order by 1",
                String.class, underwriter.getId())).containsExactly("CLAIMS_HANDLER", "UNDERWRITER");
        assertThat(actions(underwriter)).containsExactly("USER.ROLES_UPDATE");

        // versão antiga → conflito
        updateRoles(manager, underwriter, List.of("FINANCE"), version).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        long current = ((Number) updated.path("version")).longValue();
        updateRoles(manager, underwriter, List.of("PLATFORM_ADMIN"), current).then().statusCode(400)
                .body("code", equalTo("ROLE_NOT_ALLOWED_FOR_ORGANIZATION"));
        updateRoles(manager, underwriter, List.of("ORG_ADMIN"), current).then().statusCode(400)
                .body("code", equalTo("ROLE_NOT_ALLOWED_FOR_ORGANIZATION"));

        // a mudança vale no token seguinte
        String next = token(underwriter);
        given().auth().oauth2(next).get("/api/v1/me").then().statusCode(200)
                .body("roles", contains("CLAIMS_HANDLER", "UNDERWRITER"));
    }

    @Test
    void managerCannotModifyThemselves() {
        long version = ((Number) given().auth().oauth2(manager).get("/api/v1/users/" + managerA.getId())
                .path("version")).longValue();
        updateRoles(manager, managerA, List.of("UNDERWRITER"), version).then().statusCode(400)
                .body("code", equalTo("CANNOT_MODIFY_SELF"));
        for (String action : List.of("disable", "enable", "unlock", "reset-access")) {
            given().auth().oauth2(manager).post("/api/v1/users/" + managerA.getId() + "/" + action)
                    .then().statusCode(400).body("code", equalTo("CANNOT_MODIFY_SELF"));
        }
    }

    @Test
    void theLastActiveManagerCannotBeRemoved() {
        long version = version(admin, managerA);
        updateRoles(admin, managerA, List.of("UNDERWRITER"), version).then().statusCode(400)
                .body("code", equalTo("LAST_MANAGER"));
        given().auth().oauth2(admin).post("/api/v1/users/" + managerA.getId() + "/disable")
                .then().statusCode(400).body("code", equalTo("LAST_MANAGER"));

        // um gestor desactivado não conta
        UserAccount disabledManager = fixtures.staff("Gestor Parado", insurerA.getId(), "INSURER_ADMIN");
        fixtures.setAccountStatus(disabledManager.getId(), "DISABLED");
        given().auth().oauth2(admin).post("/api/v1/users/" + managerA.getId() + "/disable")
                .then().statusCode(400).body("code", equalTo("LAST_MANAGER"));

        // com outro gestor activo, já pode
        UserAccount second = fixtures.staff("Segunda Gestora", insurerA.getId(), "INSURER_ADMIN");
        updateRoles(admin, managerA, List.of("UNDERWRITER"), version).then().statusCode(200);
        String secondToken = token(second);
        given().auth().oauth2(secondToken).post("/api/v1/users/" + managerA.getId() + "/disable")
                .then().statusCode(200).body("status", equalTo("DISABLED"));

        // a plataforma também não fica sem administrador
        UserAccount platformAdmin = fixtures.staff("Outro Admin", null, "PLATFORM_ADMIN");
        given().auth().oauth2(admin).post("/api/v1/users/" + platformAdmin.getId() + "/disable")
                .then().statusCode(200);
    }

    @Test
    void disablingRevokesSessionsAndEnablingRestoresAccess() {
        String secret = totp.newSecret();
        UserAccount user = fixtures.activeStaff("Sessões", insurerA.getId(), PASSWORD, secret, "UNDERWRITER");
        TokenPair session = identityApi.issueTokens(user, "JUnit");

        given().auth().oauth2(manager).post("/api/v1/users/" + user.getId() + "/disable")
                .then().statusCode(200).body("status", equalTo("DISABLED"));
        given().contentType(ContentType.JSON).body(Map.of("refreshToken", session.refreshToken()))
                .post("/api/v1/auth/refresh").then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
        assertThat(owner.queryForList("select distinct revoked_reason from identity.refresh_token where user_id = ?",
                String.class, user.getId())).containsExactly("DISABLED");
        login(user.getEmail(), PASSWORD, code(secret)).then().statusCode(403).body("code", equalTo("ACCOUNT_DISABLED"));
        given().auth().oauth2(manager).post("/api/v1/users/" + user.getId() + "/disable")
                .then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));

        given().auth().oauth2(manager).post("/api/v1/users/" + user.getId() + "/enable")
                .then().statusCode(200).body("status", equalTo("ACTIVE"));
        given().auth().oauth2(manager).post("/api/v1/users/" + user.getId() + "/enable")
                .then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        login(user.getEmail(), PASSWORD, code(secret)).then().statusCode(200);
        assertThat(actions(user)).containsExactly("USER.DISABLE", "USER.ENABLE");
    }

    @Test
    void resetAccessSendsANewInvitationAndReactivationWorksWithANewTotp(CapturedOutput output) {
        String oldSecret = totp.newSecret();
        UserAccount user = fixtures.activeStaff("Telemóvel Perdido", insurerA.getId(), PASSWORD, oldSecret,
                "UNDERWRITER", "CLAIMS_HANDLER");
        TokenPair session = identityApi.issueTokens(user, "JUnit");

        Response reset = given().auth().oauth2(manager).post("/api/v1/users/" + user.getId() + "/reset-access");
        reset.then().statusCode(200)
                .body("email", equalTo(user.getEmail()))
                .body("organizationId", equalTo(insurerA.getId().toString()))
                .body("roleCodes", containsInAnyOrder("UNDERWRITER", "CLAIMS_HANDLER"))
                .body("status", equalTo("PENDING"));
        Map<String, Object> row = owner.queryForMap("select status, password_hash, totp_secret_enc from "
                + "identity.user_account where id = ?", user.getId());
        assertThat(row.get("status")).isEqualTo("PENDING_ACTIVATION");
        assertThat(row.get("password_hash")).isNull();
        assertThat(row.get("totp_secret_enc")).isNull();
        assertThat(owner.queryForObject("select user_id from identity.invitation where id = ?::uuid",
                Object.class, (String) reset.path("id")).toString()).isEqualTo(user.getId().toString());
        assertThat(owner.queryForList("select distinct revoked_reason from identity.refresh_token where user_id = ?",
                String.class, user.getId())).containsExactly("ACCESS_RESET");
        given().contentType(ContentType.JSON).body(Map.of("refreshToken", session.refreshToken()))
                .post("/api/v1/auth/refresh").then().statusCode(401);
        login(user.getEmail(), PASSWORD, code(oldSecret)).then().statusCode(401);
        given().auth().oauth2(manager).post("/api/v1/users/" + user.getId() + "/reset-access")
                .then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));

        String invitationToken = InvitationFlowIT.tokenFrom(channels.lastTo(user.getEmail()));
        String newSecret = given().contentType(ContentType.JSON).body(Map.of("password", NEW_PASSWORD))
                .post("/api/v1/auth/invitations/" + invitationToken + "/accept")
                .then().statusCode(200).extract().path("totpSecret");
        assertThat(newSecret).isNotEqualTo(oldSecret);
        given().contentType(ContentType.JSON).body(Map.of("totpCode", code(oldSecret)))
                .post("/api/v1/auth/invitations/" + invitationToken + "/activate")
                .then().statusCode(400).body("code", equalTo("TOTP_INVALID"));
        given().contentType(ContentType.JSON).body(Map.of("totpCode", code(newSecret)))
                .post("/api/v1/auth/invitations/" + invitationToken + "/activate")
                .then().statusCode(200).body("accessToken", notNullValue());

        clock.advance(Duration.ofSeconds(TotpService.PERIOD_SECONDS));
        login(user.getEmail(), NEW_PASSWORD, code(newSecret)).then().statusCode(200);
        given().auth().oauth2(manager).get("/api/v1/users/" + user.getId()).then().statusCode(200)
                .body("status", equalTo("ACTIVE"))
                .body("roles", contains("CLAIMS_HANDLER", "UNDERWRITER"));
        assertThat(actions(user)).containsExactly("USER.ACCESS_RESET", "USER.ACTIVATE");
        assertThat(output.getAll()).doesNotContain(invitationToken).doesNotContain(NEW_PASSWORD)
                .doesNotContain(newSecret);
    }

    @Test
    void usersWithoutOrgUserManageAreForbidden() {
        UserAccount underwriterAccount = fixtures.staff("Sem Gestão", insurerA.getId(), "UNDERWRITER");
        UserAccount other = fixtures.staff("Colega", insurerA.getId(), "UNDERWRITER");
        String underwriter = token(underwriterAccount);

        given().auth().oauth2(underwriter).post("/api/v1/users/" + other.getId() + "/disable")
                .then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        given().auth().oauth2(underwriter).post("/api/v1/users/" + other.getId() + "/reset-access")
                .then().statusCode(403);
        updateRoles(underwriter, other, List.of("FINANCE"), 0).then().statusCode(403);
        given().auth().oauth2(underwriter).get("/api/v1/organizations/" + insurerA.getId() + "/users")
                .then().statusCode(403);
        given().get("/api/v1/users/" + other.getId()).then().statusCode(401);
    }

    // --- apoio ---

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private String code(String secret) {
        return totp.generate(secret, totp.currentStep());
    }

    private long version(String token, UserAccount account) {
        return ((Number) given().auth().oauth2(token).get("/api/v1/users/" + account.getId()).path("version"))
                .longValue();
    }

    private List<String> actions(UserAccount account) {
        return owner.queryForList("select action from audit.audit_event where entity_id = ? order by occurred_at",
                String.class, account.getId().toString());
    }

    private static Response updateRoles(String token, UserAccount account, List<String> roles, long version) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("roleCodes", roles, "version", version))
                .put("/api/v1/users/" + account.getId() + "/roles");
    }
}
