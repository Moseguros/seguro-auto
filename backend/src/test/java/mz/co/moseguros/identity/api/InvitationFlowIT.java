package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
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
 * Convites e activação de staff (S1-04 §2 e §3), de ponta a ponta: criar a seguradora, convidar o
 * gestor, abrir o link do e-mail, definir a palavra-passe e o TOTP, activar e entrar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class InvitationFlowIT {

    static final String PASSWORD = "Cajueiro-Marginal-2026";
    static final Pattern LINK = Pattern.compile("http://localhost:5173/convite/([A-Za-z0-9_-]{43})");

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
    UserAccount adminAccount;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        channels.reset();
        adminAccount = fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN");
        admin = token(adminAccount);
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
    }

    @Test
    void adminCreatesInsurerInvitesManagerWhoActivatesAndSignsIn(CapturedOutput output) {
        Map<String, Object> insurerBody = Map.of("type", "INSURER", "legalName", "Seguradora Ponta, SA",
                "tradeName", "Seguradora Ponta", "nuit", digits(9), "licenseNumber", "ASFPM-0001",
                "contactEmail", "geral@ponta.co.mz",
                "insurerProfile", Map.of("shortCode", "P" + digits(6), "complaintsEmail", "rec@ponta.co.mz"));
        String insurerId = given().auth().oauth2(admin).contentType(ContentType.JSON).body(insurerBody)
                .post("/api/v1/organizations").then().statusCode(201).extract().path("id");

        String email = randomEmail();
        given().auth().oauth2(admin).queryParam("organizationId", insurerId).get("/api/v1/roles")
                .then().statusCode(200).body("managerInviteRoles", contains("INSURER_ADMIN"));
        Response invited = invite(admin, insurerId, "Gestora Ponta", email, List.of("INSURER_ADMIN"));
        invited.then().statusCode(201)
                .body("id", notNullValue())
                .body("email", equalTo(email))
                .body("fullName", equalTo("Gestora Ponta"))
                .body("organizationId", equalTo(insurerId))
                .body("roleCodes", contains("INSURER_ADMIN"))
                .body("status", equalTo("PENDING"))
                .body("expiresAt", notNullValue())
                .body("$", not(hasKey("token")))
                .body("$", not(hasKey("tokenHash")));
        String invitationId = invited.path("id");

        FakeOtpDeliveryChannels.Sent mail = channels.lastTo(email);
        assertThat(mail.channel()).isEqualTo(Channel.EMAIL);
        assertThat(mail.params().get("subject")).isEqualTo("Convite para a MoSeguros");
        assertThat(mail.text()).startsWith("Olá Gestora Ponta,")
                .contains("Foi convidado para usar a MoSeguros como membro de Seguradora Ponta.")
                .contains("Google Authenticator");
        String token = tokenFrom(mail);
        assertThat(owner.queryForObject("""
                select count(*) from notification.outbound_message
                 where purpose = 'INVITATION' and correlation_id = ?::uuid and status = 'SENT'""",
                Integer.class, invitationId)).isEqualTo(1);

        given().get("/api/v1/auth/invitations/" + token).then().statusCode(200)
                .body("email", equalTo(email))
                .body("fullName", equalTo("Gestora Ponta"))
                .body("organization.displayName", equalTo("Seguradora Ponta"))
                .body("organization.type", equalTo("INSURER"))
                .body("expiresAt", notNullValue());

        Response accepted = accept(token, PASSWORD);
        accepted.then().statusCode(200)
                .body("otpauthUri", startsWith("otpauth://totp/MoSeguros:" + email + "?secret="));
        String secret = accepted.path("totpSecret");
        assertThat(secret).matches("^[A-Z2-7]{32}$");
        assertThat((String) accepted.path("otpauthUri"))
                .endsWith("secret=" + secret + "&issuer=MoSeguros&algorithm=SHA1&digits=6&period=30");

        Response activated = activate(token, totp.generate(secret, totp.currentStep()));
        activated.then().statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("expiresIn", equalTo(900));

        given().auth().oauth2((String) activated.path("accessToken")).get("/api/v1/me")
                .then().statusCode(200)
                .body("type", equalTo("STAFF"))
                .body("email", equalTo(email))
                .body("emailVerified", equalTo(true))
                .body("displayName", equalTo("Gestora Ponta"))
                .body("roles", contains("INSURER_ADMIN"))
                .body("organization.id", equalTo(insurerId))
                .body("organization.type", equalTo("INSURER"));

        Map<String, Object> account = owner.queryForMap(
                "select id, status, password_hash, last_totp_step, totp_secret_enc from identity.user_account "
                        + "where email = ? and type = 'STAFF'", email);
        assertThat(account.get("status")).isEqualTo("ACTIVE");
        assertThat((String) account.get("password_hash")).startsWith("$argon2id$");
        assertThat(account.get("last_totp_step")).isNotNull();
        assertThat(new String((byte[]) account.get("totp_secret_enc"), java.nio.charset.StandardCharsets.ISO_8859_1))
                .doesNotContain(secret);
        assertThat(owner.queryForList("select action from audit.audit_event where entity_id in (?, ?) order by "
                        + "occurred_at", String.class, invitationId, account.get("id").toString()))
                .containsExactly("USER.INVITE", "USER.ACTIVATE");

        given().auth().oauth2(admin).queryParam("status", "ACCEPTED")
                .get("/api/v1/organizations/" + insurerId + "/invitations")
                .then().statusCode(200).body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(invitationId)).body("content[0].status", equalTo("ACCEPTED"));

        // já aceite → o link deixa de funcionar
        given().get("/api/v1/auth/invitations/" + token).then().statusCode(404)
                .body("code", equalTo("INVITATION_INVALID"));

        assertThat(output.getAll())
                .doesNotContain(token)
                .doesNotContain(PASSWORD)
                .doesNotContain(secret);
    }

    @Test
    void managerInvitesOnlyIntoTheirOwnOrganizationWithAllowedRoles() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Gestor");
        Organization other = fixtures.organization(OrganizationType.INSURER, "Seg Alheia");
        String manager = token(fixtures.staff("Gestor", insurer.getId(), "INSURER_ADMIN"));

        invite(manager, insurer.getId().toString(), "Subscritor", randomEmail(), List.of("UNDERWRITER"))
                .then().statusCode(201).body("roleCodes", contains("UNDERWRITER"));
        invite(manager, other.getId().toString(), "Intruso", randomEmail(), List.of("UNDERWRITER"))
                .then().statusCode(404).body("code", equalTo("ORGANIZATION_NOT_FOUND"));
        invite(manager, insurer.getId().toString(), "Corretor", randomEmail(), List.of("BROKER"))
                .then().statusCode(400).body("code", equalTo("ROLE_NOT_ALLOWED_FOR_ORGANIZATION"));
        invite(manager, insurer.getId().toString(), "Cliente", randomEmail(), List.of("CUSTOMER"))
                .then().statusCode(400).body("code", equalTo("ROLE_NOT_ALLOWED_FOR_ORGANIZATION"));
        invite(manager, insurer.getId().toString(), "Inventado", randomEmail(), List.of("NAO_EXISTE"))
                .then().statusCode(400).body("code", equalTo("ROLE_NOT_ALLOWED_FOR_ORGANIZATION"));

        given().auth().oauth2(manager).contentType(ContentType.JSON)
                .body(Map.of("fullName", "Plataforma", "email", randomEmail(), "roleCodes", List.of("PLATFORM_ADMIN")))
                .post("/api/v1/admin/users").then().statusCode(403).body("code", equalTo("FORBIDDEN"));

        given().auth().oauth2(manager).queryParam("organizationId", insurer.getId()).get("/api/v1/roles")
                .then().statusCode(200)
                .body("roles", hasItems("INSURER_ADMIN", "UNDERWRITER", "CLAIMS_HANDLER"))
                .body("roles", not(hasItem("BROKER")))
                .body("managerRoles", contains("INSURER_ADMIN"))
                .body("managerInviteRoles", contains("INSURER_ADMIN"));
        given().auth().oauth2(manager).queryParam("organizationId", other.getId()).get("/api/v1/roles")
                .then().statusCode(404).body("code", equalTo("ORGANIZATION_NOT_FOUND"));
        given().auth().oauth2(manager).queryParam("scope", "PLATFORM").get("/api/v1/roles")
                .then().statusCode(403);
        given().auth().oauth2(manager).get("/api/v1/roles").then().statusCode(400);

        given().auth().oauth2(manager).get("/api/v1/organizations/" + insurer.getId() + "/invitations")
                .then().statusCode(200).body("totalElements", equalTo(1));
        given().auth().oauth2(manager).get("/api/v1/organizations/" + other.getId() + "/invitations")
                .then().statusCode(404);
        given().auth().oauth2(manager).get("/api/v1/admin/invitations").then().statusCode(403);

        String customer = token(fixtures.customer("Cliente"));
        invite(customer, insurer.getId().toString(), "Xavier", randomEmail(), List.of("UNDERWRITER"))
                .then().statusCode(403);
    }

    @Test
    void platformInvitationsAndRolesOfOtherOrganizationTypes() {
        Organization broker = fixtures.organization(OrganizationType.BROKER, "Corretora Papéis");
        given().auth().oauth2(admin).queryParam("organizationId", broker.getId()).get("/api/v1/roles")
                .then().statusCode(200)
                .body("roles", contains("BROKER", "ORG_ADMIN"))
                .body("managerRoles", contains("ORG_ADMIN"))
                .body("managerInviteRoles", contains("ORG_ADMIN", "BROKER"));
        given().auth().oauth2(admin).queryParam("scope", "PLATFORM").get("/api/v1/roles")
                .then().statusCode(200)
                .body("roles", contains("PLATFORM_ADMIN"))
                .body("managerRoles", contains("PLATFORM_ADMIN"))
                .body("managerInviteRoles", contains("PLATFORM_ADMIN"));

        String email = randomEmail();
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("fullName", "Nova Admin", "email", email, "roleCodes", List.of("PLATFORM_ADMIN")))
                .post("/api/v1/admin/users")
                .then().statusCode(201)
                .body("$", not(hasKey("organizationId")))
                .body("roleCodes", contains("PLATFORM_ADMIN"));
        assertThat(channels.lastTo(email).text()).contains("Foi convidado para usar a MoSeguros. Para activar");
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("fullName", "Errado", "email", randomEmail(), "roleCodes", List.of("UNDERWRITER")))
                .post("/api/v1/admin/users")
                .then().statusCode(400).body("code", equalTo("ROLE_NOT_ALLOWED_FOR_ORGANIZATION"));

        given().auth().oauth2(admin).queryParam("status", "PENDING").queryParam("size", 100)
                .get("/api/v1/admin/invitations")
                .then().statusCode(200).body("content.email", hasItem(email));

        fixtures.suspendOrganization(broker.getId());
        invite(admin, broker.getId().toString(), "Suspensa", randomEmail(), List.of("ORG_ADMIN", "BROKER"))
                .then().statusCode(409).body("code", equalTo("ORGANIZATION_NOT_ACTIVE"));
    }

    @Test
    void reinvitingAPendingAccountReusesItAndActiveEmailsAreRejected() {
        Organization insurerA = fixtures.organization(OrganizationType.INSURER, "Seg A");
        Organization insurerB = fixtures.organization(OrganizationType.INSURER, "Seg B");
        String managerA = token(fixtures.staff("Gestor A", insurerA.getId(), "INSURER_ADMIN"));
        String email = randomEmail();

        invite(managerA, insurerA.getId().toString(), "Pendente", email, List.of("UNDERWRITER"))
                .then().statusCode(201);
        String firstToken = tokenFrom(channels.lastTo(email));
        accept(firstToken, PASSWORD).then().statusCode(200);
        String accountId = owner.queryForObject(
                "select id::text from identity.user_account where email = ? and type = 'STAFF'", String.class, email);
        assertThat(owner.queryForObject("select status from identity.user_account where id = ?::uuid", String.class,
                accountId)).isEqualTo("PENDING_ACTIVATION");

        String secondId = invite(managerA, insurerA.getId().toString(), "Pendente Outra Vez", email,
                List.of("CLAIMS_HANDLER")).then().statusCode(201).extract().path("id");
        String secondToken = tokenFrom(channels.lastTo(email));
        assertThat(owner.queryForObject("select user_id::text from identity.invitation where id = ?::uuid",
                String.class, secondId)).isEqualTo(accountId);
        given().get("/api/v1/auth/invitations/" + firstToken).then().statusCode(404);

        String secret = accept(secondToken, PASSWORD).then().statusCode(200).extract().path("totpSecret");
        String access = activate(secondToken, totp.generate(secret, totp.currentStep()))
                .then().statusCode(200).extract().path("accessToken");
        given().auth().oauth2(access).get("/api/v1/me").then().statusCode(200)
                .body("id", equalTo(accountId))
                .body("displayName", equalTo("Pendente Outra Vez"))
                .body("roles", contains("CLAIMS_HANDLER"));
        assertThat(owner.queryForObject("select count(*) from identity.user_account where email = ?", Integer.class,
                email)).isEqualTo(1);

        UserAccount activeInB = fixtures.staff("Activo B", insurerB.getId(), "UNDERWRITER");
        invite(managerA, insurerA.getId().toString(), "Roubado", activeInB.getEmail(), List.of("UNDERWRITER"))
                .then().statusCode(409).body("code", equalTo("EMAIL_ALREADY_IN_USE"));
        invite(managerA, insurerA.getId().toString(), "Já activo", email, List.of("UNDERWRITER"))
                .then().statusCode(409).body("code", equalTo("EMAIL_ALREADY_IN_USE"));
    }

    @Test
    void newInvitationReplacesPendingInvitationsOfOtherOrganizations() {
        Organization insurerA = fixtures.organization(OrganizationType.INSURER, "Seg Antiga");
        Organization insurerB = fixtures.organization(OrganizationType.INSURER, "Seg Nova");
        String email = randomEmail();
        String firstId = invite(admin, insurerA.getId().toString(), "Disputada", email, List.of("UNDERWRITER"))
                .then().statusCode(201).extract().path("id");
        String firstToken = tokenFrom(channels.lastTo(email));
        String managerB = token(fixtures.staff("Gestor B", insurerB.getId(), "INSURER_ADMIN"));

        invite(managerB, insurerB.getId().toString(), "Disputada", email, List.of("FINANCE")).then().statusCode(201);

        assertThat(owner.queryForObject("select revoked_at is not null from identity.invitation where id = ?::uuid",
                Boolean.class, firstId)).isTrue();
        given().get("/api/v1/auth/invitations/" + firstToken).then().statusCode(404);
        given().get("/api/v1/auth/invitations/" + tokenFrom(channels.lastTo(email))).then().statusCode(200)
                .body("organization.displayName", equalTo("Seg Nova"));
    }

    @Test
    void expiredRevokedAndUnknownInvitationsAreInvalid() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Prazos");
        String id = insurer.getId().toString();

        String expiredEmail = randomEmail();
        String expiredId = invite(admin, id, "Expira", expiredEmail, List.of("UNDERWRITER")).path("id");
        String expiredToken = tokenFrom(channels.lastTo(expiredEmail));
        clock.advance(Duration.ofDays(7).plusSeconds(1));
        admin = token(adminAccount); // o token de acesso anterior expirou
        given().get("/api/v1/auth/invitations/" + expiredToken).then().statusCode(404)
                .body("code", equalTo("INVITATION_INVALID"));
        accept(expiredToken, PASSWORD).then().statusCode(404);
        given().auth().oauth2(admin).queryParam("status", "EXPIRED").get("/api/v1/organizations/" + id + "/invitations")
                .then().statusCode(200).body("content.id", hasItem(expiredId));

        // reenviar um convite expirado dá-lhe um token novo e mais 7 dias; o antigo continua inválido
        given().auth().oauth2(admin).post("/api/v1/invitations/" + expiredId + "/resend")
                .then().statusCode(200).body("status", equalTo("PENDING"));
        String renewedToken = tokenFrom(channels.lastTo(expiredEmail));
        assertThat(renewedToken).isNotEqualTo(expiredToken);
        given().get("/api/v1/auth/invitations/" + renewedToken).then().statusCode(200);
        given().get("/api/v1/auth/invitations/" + expiredToken).then().statusCode(404);
        clock.reset();

        String revokedEmail = randomEmail();
        String revokedId = invite(admin, id, "Revogado", revokedEmail, List.of("UNDERWRITER")).path("id");
        String revokedToken = tokenFrom(channels.lastTo(revokedEmail));
        given().auth().oauth2(admin).post("/api/v1/invitations/" + revokedId + "/revoke").then().statusCode(204);
        given().get("/api/v1/auth/invitations/" + revokedToken).then().statusCode(404);
        given().auth().oauth2(admin).post("/api/v1/invitations/" + revokedId + "/revoke").then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));
        given().auth().oauth2(admin).post("/api/v1/invitations/" + revokedId + "/resend").then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));
        given().auth().oauth2(admin).post("/api/v1/invitations/" + UUID.randomUUID() + "/resend").then()
                .statusCode(404);

        given().get("/api/v1/auth/invitations/desconhecido").then().statusCode(404)
                .body("code", equalTo("INVITATION_INVALID"));
    }

    @Test
    void resendInvalidatesTheOldToken() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Reenvio");
        String manager = token(fixtures.staff("Gestor", insurer.getId(), "INSURER_ADMIN"));
        String email = randomEmail();
        String invitationId = invite(manager, insurer.getId().toString(), "Reenviado", email, List.of("FINANCE"))
                .path("id");
        String oldToken = tokenFrom(channels.lastTo(email));

        given().auth().oauth2(manager).post("/api/v1/invitations/" + invitationId + "/resend")
                .then().statusCode(200).body("id", equalTo(invitationId));
        String newToken = tokenFrom(channels.lastTo(email));

        assertThat(newToken).isNotEqualTo(oldToken);
        given().get("/api/v1/auth/invitations/" + oldToken).then().statusCode(404);
        given().get("/api/v1/auth/invitations/" + newToken).then().statusCode(200);

        Organization other = fixtures.organization(OrganizationType.INSURER, "Seg Sem Acesso");
        String otherManager = token(fixtures.staff("Outro", other.getId(), "INSURER_ADMIN"));
        given().auth().oauth2(otherManager).post("/api/v1/invitations/" + invitationId + "/resend")
                .then().statusCode(404);
        given().auth().oauth2(otherManager).post("/api/v1/invitations/" + invitationId + "/revoke")
                .then().statusCode(404);
    }

    @Test
    void activationRulesAndFiveWrongCodesRevokeTheInvitation() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Códigos");
        String email = randomEmail();
        String invitationId = invite(admin, insurer.getId().toString(), "Distraída", email,
                List.of("UNDERWRITER")).path("id");
        String token = tokenFrom(channels.lastTo(email));

        activate(token, "123456").then().statusCode(400).body("code", equalTo("INVITATION_NOT_ACCEPTED"));

        accept(token, "curta").then().statusCode(400)
                .body("code", equalTo("PASSWORD_TOO_WEAK"))
                .body("errors[0].field", equalTo("password"))
                .body("errors[0].code", equalTo("TOO_SHORT"));
        String localPart = email.substring(0, email.indexOf('@'));
        accept(token, "Xx-" + localPart.toUpperCase() + "-2026").then().statusCode(400)
                .body("errors.code", contains("CONTAINS_EMAIL"));
        accept(token, "unbelievable").then().statusCode(400).body("errors.code", contains("TOO_COMMON"));

        String firstSecret = accept(token, PASSWORD).then().statusCode(200).extract().path("totpSecret");
        String secret = accept(token, PASSWORD + "!").then().statusCode(200).extract().path("totpSecret");
        assertThat(secret).isNotEqualTo(firstSecret);
        String good = totp.generate(secret, totp.currentStep());
        String wrong = OtpTestSupport.wrong(good);
        assertThat(totp.verify(secret, wrong, null)).isEmpty();

        for (int attempt = 1; attempt <= 4; attempt++) {
            activate(token, wrong).then().statusCode(400).body("code", equalTo("TOTP_INVALID"));
        }
        assertThat(owner.queryForObject("select activation_attempts from identity.invitation where id = ?::uuid",
                Integer.class, invitationId)).isEqualTo(4);
        activate(token, wrong).then().statusCode(404).body("code", equalTo("INVITATION_INVALID"));
        activate(token, good).then().statusCode(404).body("code", equalTo("INVITATION_INVALID"));
        assertThat(owner.queryForObject("select revoked_at is not null from identity.invitation where id = ?::uuid",
                Boolean.class, invitationId)).isTrue();
        assertThat(owner.queryForObject(
                "select status from identity.user_account where email = ? and type = 'STAFF'", String.class, email))
                .isEqualTo("PENDING_ACTIVATION");
    }

    @Test
    void codeFromTheOldSecretDoesNotActivate() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Segredo");
        String email = randomEmail();
        invite(admin, insurer.getId().toString(), "Dupla", email, List.of("UNDERWRITER")).then().statusCode(201);
        String token = tokenFrom(channels.lastTo(email));
        String oldSecret = accept(token, PASSWORD).path("totpSecret");
        accept(token, PASSWORD).then().statusCode(200);

        activate(token, totp.generate(oldSecret, totp.currentStep())).then().statusCode(400)
                .body("code", equalTo("TOTP_INVALID"));
    }

    // --- apoio ---

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private static Response invite(String token, String organizationId, String fullName, String email,
                                   List<String> roles) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("fullName", fullName, "email", email, "roleCodes", roles))
                .post("/api/v1/organizations/" + organizationId + "/users");
    }

    private static Response accept(String token, String password) {
        return given().contentType(ContentType.JSON).body(Map.of("password", password))
                .post("/api/v1/auth/invitations/" + token + "/accept");
    }

    private static Response activate(String token, String code) {
        return given().contentType(ContentType.JSON).header("User-Agent", "JUnit").body(Map.of("totpCode", code))
                .post("/api/v1/auth/invitations/" + token + "/activate");
    }

    static String tokenFrom(FakeOtpDeliveryChannels.Sent mail) {
        Matcher matcher = LINK.matcher(mail.text());
        assertThat(matcher.find()).as("link in the invitation e-mail").isTrue();
        return matcher.group(1);
    }

    static String randomEmail() {
        return "staff" + ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999) + "@exemplo.co.mz";
    }

    static String digits(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(ThreadLocalRandom.current().nextInt(1, 10));
        for (int i = 1; i < count; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }
}
