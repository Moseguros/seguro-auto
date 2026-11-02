package mz.co.moseguros.organization.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Organizações (S1-04 §1): criação de cada tipo, regras do perfil de seguradora, unicidade,
 * lista com filtros e paginação, alteração com versão, suspensão (com revogação das sessões) e
 * acesso negado a clientes e a staff de organizações.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class OrganizationControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    JdbcTemplate owner;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        admin = token(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"));
    }

    @Test
    void createsEachType() {
        for (OrganizationType type : OrganizationType.values()) {
            Map<String, Object> body = body(type, "Org " + type);
            Response response = create(admin, body);
            response.then().statusCode(201)
                    .header("Location", endsWith("/api/v1/organizations/" + response.path("id")))
                    .body("type", equalTo(type.name()))
                    .body("legalName", equalTo("Org " + type + ", SA"))
                    .body("tradeName", equalTo("Org " + type))
                    .body("displayName", equalTo("Org " + type))
                    .body("nuit", equalTo(body.get("nuit")))
                    .body("status", equalTo("ACTIVE"))
                    .body("contactEmail", equalTo(body.get("contactEmail")))
                    .body("version", equalTo(0))
                    .body("createdAt", notNullValue())
                    .body("updatedAt", notNullValue());
            if (type == OrganizationType.INSURER) {
                response.then().body("insurerProfile.shortCode", equalTo(body.get("shortCodeUsed")))
                        .body("insurerProfile.complaintsEmail", equalTo("reclamacoes@seguradora.co.mz"));
            } else {
                response.then().body("$", not(hasKey("insurerProfile")));
            }
            String id = response.path("id");
            assertThat(owner.queryForObject("""
                    select count(*) from audit.audit_event
                     where action = 'ORGANIZATION.CREATE' and entity_id = ? and after_state is not null""",
                    Integer.class, id)).isEqualTo(1);
        }
    }

    @Test
    void displayNameFallsBackToTheLegalName() {
        Map<String, Object> body = body(OrganizationType.WORKSHOP, "Oficina");
        body.remove("tradeName");
        create(admin, body).then().statusCode(201)
                .body("$", not(hasKey("tradeName")))
                .body("displayName", equalTo("Oficina, SA"));
    }

    @Test
    void insurerProfileIsRequiredForInsurersAndForbiddenForOthers() {
        Map<String, Object> insurer = body(OrganizationType.INSURER, "Seg Sem Perfil");
        insurer.remove("insurerProfile");
        create(admin, insurer).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("insurerProfile"))
                .body("errors[0].code", equalTo("REQUIRED"));

        Map<String, Object> broker = body(OrganizationType.BROKER, "Corretor Com Perfil");
        broker.put("insurerProfile", insurerProfile(shortCode()));
        create(admin, broker).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("insurerProfile"))
                .body("errors[0].code", equalTo("NOT_ALLOWED"));
    }

    @Test
    void licenseNumberIsRequiredForInsurersBrokersAndAgents() {
        Map<String, Object> agent = body(OrganizationType.AGENT, "Agente");
        agent.remove("licenseNumber");
        create(admin, agent).then().statusCode(400).body("code", equalTo("LICENSE_NUMBER_REQUIRED"));
    }

    @Test
    void invalidFieldsAreFieldErrors() {
        Map<String, Object> body = body(OrganizationType.WORKSHOP, "Oficina Inválida");
        body.put("nuit", "12345");
        create(admin, body).then().statusCode(400)
                .body("errors[0].field", equalTo("nuit")).body("errors[0].code", equalTo("INVALID_NUIT"));

        body = body(OrganizationType.WORKSHOP, "Oficina Inválida");
        body.put("contactEmail", "sem-arroba");
        create(admin, body).then().statusCode(400)
                .body("errors[0].field", equalTo("contactEmail")).body("errors[0].code", equalTo("INVALID_EMAIL"));

        body = body(OrganizationType.WORKSHOP, "Oficina Inválida");
        body.put("contactPhone", "+258 84 123 4567");
        create(admin, body).then().statusCode(400)
                .body("errors[0].field", equalTo("contactPhone")).body("errors[0].code", equalTo("PATTERN"));
    }

    @Test
    void nuitAndShortCodeMustBeUnique() {
        Map<String, Object> first = body(OrganizationType.INSURER, "Seg Primeira");
        create(admin, first).then().statusCode(201);

        Map<String, Object> sameNuit = body(OrganizationType.BROKER, "Corretor Mesmo NUIT");
        sameNuit.put("nuit", first.get("nuit"));
        create(admin, sameNuit).then().statusCode(409).body("code", equalTo("NUIT_ALREADY_IN_USE"));

        Map<String, Object> sameShortCode = body(OrganizationType.INSURER, "Seg Mesmo Código");
        sameShortCode.put("insurerProfile", insurerProfile((String) first.get("shortCodeUsed")));
        create(admin, sameShortCode).then().statusCode(409).body("code", equalTo("SHORT_CODE_ALREADY_IN_USE"));
    }

    @Test
    void ombudsmanMustBeAnExistingOmbudsman() {
        String ombudsman = create(admin, body(OrganizationType.OMBUDSMAN, "Provedor")).path("id");
        String workshop = create(admin, body(OrganizationType.WORKSHOP, "Oficina Provedora")).path("id");

        Map<String, Object> withOmbudsman = body(OrganizationType.INSURER, "Seg Com Provedor");
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) withOmbudsman.get("insurerProfile");
        profile.put("ombudsmanOrganizationId", ombudsman);
        create(admin, withOmbudsman).then().statusCode(201)
                .body("insurerProfile.ombudsmanOrganizationId", equalTo(ombudsman));

        for (String invalid : List.of(workshop, UUID.randomUUID().toString())) {
            Map<String, Object> body = body(OrganizationType.INSURER, "Seg Provedor Errado");
            @SuppressWarnings("unchecked")
            Map<String, Object> p = (Map<String, Object>) body.get("insurerProfile");
            p.put("ombudsmanOrganizationId", invalid);
            create(admin, body).then().statusCode(400).body("code", equalTo("OMBUDSMAN_INVALID"));
        }
    }

    @Test
    void listsWithFiltersAndPagination() {
        String prefix = "Lista" + ThreadLocalRandom.current().nextInt(100_000, 999_999);
        Map<String, Object> c = body(OrganizationType.BROKER, prefix + " C");
        create(admin, c).then().statusCode(201);
        create(admin, body(OrganizationType.BROKER, prefix + " A")).then().statusCode(201);
        String workshopId = create(admin, body(OrganizationType.WORKSHOP, prefix + " B")).path("id");

        given().auth().oauth2(admin)
                .queryParam("q", prefix.toLowerCase()).queryParam("sort", "legalName,asc").queryParam("size", 2)
                .get("/api/v1/organizations")
                .then().statusCode(200)
                .body("totalElements", equalTo(3))
                .body("page", equalTo(0))
                .body("size", equalTo(2))
                .body("content.displayName", contains(prefix + " A", prefix + " B"))
                .body("content[0]", hasKey("nuit"))
                .body("content[0]", hasKey("createdAt"));
        given().auth().oauth2(admin)
                .queryParam("q", prefix).queryParam("sort", "legalName,asc").queryParam("size", 2)
                .queryParam("page", 1)
                .get("/api/v1/organizations")
                .then().statusCode(200).body("content.displayName", contains(prefix + " C"));

        given().auth().oauth2(admin).queryParam("q", prefix).queryParam("type", "WORKSHOP")
                .get("/api/v1/organizations")
                .then().statusCode(200).body("totalElements", equalTo(1)).body("content[0].id", equalTo(workshopId));

        given().auth().oauth2(admin).queryParam("q", c.get("nuit"))
                .get("/api/v1/organizations")
                .then().statusCode(200).body("totalElements", equalTo(1))
                .body("content[0].displayName", equalTo(prefix + " C"));

        suspend(workshopId, "teste").then().statusCode(200);
        given().auth().oauth2(admin).queryParam("q", prefix).queryParam("status", "SUSPENDED")
                .get("/api/v1/organizations")
                .then().statusCode(200).body("totalElements", equalTo(1)).body("content[0].status", equalTo("SUSPENDED"));

        given().auth().oauth2(admin).queryParam("sort", "nuit,asc").get("/api/v1/organizations")
                .then().statusCode(400).body("errors[0].field", equalTo("sort"))
                .body("errors[0].code", equalTo("INVALID_SORT"));
        given().auth().oauth2(admin).queryParam("size", 101).get("/api/v1/organizations")
                .then().statusCode(400).body("errors[0].code", equalTo("OUT_OF_RANGE"));
        given().auth().oauth2(admin).queryParam("type", "BANCO").get("/api/v1/organizations")
                .then().statusCode(400).body("errors[0].field", equalTo("type"))
                .body("errors[0].code", equalTo("TYPE_MISMATCH"));
    }

    @Test
    void getUnknownOrganizationIsNotFound() {
        given().auth().oauth2(admin).get("/api/v1/organizations/" + UUID.randomUUID())
                .then().statusCode(404).body("code", equalTo("ORGANIZATION_NOT_FOUND"));
    }

    @Test
    void updatesWithTheCurrentVersionOnly() {
        Map<String, Object> body = body(OrganizationType.INSURER, "Seg Alterar");
        String id = create(admin, body).path("id");
        given().auth().oauth2(admin).get("/api/v1/organizations/" + id)
                .then().statusCode(200).body("version", equalTo(0));

        Map<String, Object> update = new LinkedHashMap<>(body);
        update.remove("type");
        update.remove("shortCodeUsed");
        update.put("tradeName", "Seg Alterada");
        update.put("province", "SOFALA");
        update.put("version", 0);
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(update)
                .put("/api/v1/organizations/" + id)
                .then().statusCode(200)
                .body("displayName", equalTo("Seg Alterada"))
                .body("province", equalTo("SOFALA"))
                .body("version", equalTo(1));

        update.put("tradeName", "Versão Antiga");
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(update)
                .put("/api/v1/organizations/" + id)
                .then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));

        assertThat(owner.queryForObject("""
                select before_state->>'tradeName' || '>' || (after_state->>'tradeName') from audit.audit_event
                 where action = 'ORGANIZATION.UPDATE' and entity_id = ?""", String.class, id))
                .isEqualTo("Seg Alterar>Seg Alterada");
    }

    @Test
    void suspendsAndReactivates() {
        String id = create(admin, body(OrganizationType.ASSESSOR, "Perito")).path("id");

        suspend(id, "Licença caducada").then().statusCode(200).body("status", equalTo("SUSPENDED"));
        suspend(id, "outra vez").then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        reactivate(id).then().statusCode(200).body("status", equalTo("ACTIVE"));
        reactivate(id).then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));

        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("reason", ""))
                .post("/api/v1/organizations/" + id + "/suspend")
                .then().statusCode(400).body("errors[0].field", equalTo("reason"));

        assertThat(owner.queryForList("""
                select action from audit.audit_event where entity_id = ? order by occurred_at""", String.class, id))
                .containsExactly("ORGANIZATION.CREATE", "ORGANIZATION.SUSPEND", "ORGANIZATION.REACTIVATE");
        assertThat(owner.queryForObject("""
                select after_state->>'reason' from audit.audit_event
                 where action = 'ORGANIZATION.SUSPEND' and entity_id = ?""", String.class, id))
                .isEqualTo("Licença caducada");
    }

    @Test
    void suspendingRevokesTheSessionsOfItsUsers() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Suspensa");
        UserAccount staff = fixtures.staff("Gestor", insurer.getId(), "INSURER_ADMIN");
        TokenPair session = identityApi.issueTokens(staff, null);
        Organization other = fixtures.organization(OrganizationType.INSURER, "Seg Outra");
        TokenPair otherSession = identityApi.issueTokens(fixtures.staff("Outro", other.getId(), "INSURER_ADMIN"),
                null);

        suspend(insurer.getId().toString(), "Fraude").then().statusCode(200);

        // O listener do identity é assíncrono (@ApplicationModuleListener corre depois do commit).
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(owner.queryForList(
                "select revoked_reason from identity.refresh_token where user_id = ?", String.class, staff.getId()))
                .containsExactly("ORG_SUSPENDED"));
        refresh(session.refreshToken()).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
        refresh(otherSession.refreshToken()).then().statusCode(200);
    }

    @Test
    void customersAndOrganizationStaffAreForbidden() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Proibida");
        String customer = token(fixtures.customer("Cliente"));
        String insurerAdmin = token(fixtures.staff("Gestor Seg", insurer.getId(), "INSURER_ADMIN"));
        String id = insurer.getId().toString();
        Map<String, Object> update = body(OrganizationType.INSURER, "X");
        update.remove("type");
        update.remove("shortCodeUsed");
        update.put("version", 0);

        for (String token : List.of(customer, insurerAdmin)) {
            create(token, body(OrganizationType.WORKSHOP, "Oficina Proibida")).then().statusCode(403)
                    .body("code", equalTo("FORBIDDEN"));
            given().auth().oauth2(token).get("/api/v1/organizations").then().statusCode(403);
            given().auth().oauth2(token).get("/api/v1/organizations/" + id).then().statusCode(403);
            given().auth().oauth2(token).contentType(ContentType.JSON).body(update)
                    .put("/api/v1/organizations/" + id).then().statusCode(403);
            given().auth().oauth2(token).contentType(ContentType.JSON).body(Map.of("reason", "x"))
                    .post("/api/v1/organizations/" + id + "/suspend").then().statusCode(403);
            given().auth().oauth2(token).post("/api/v1/organizations/" + id + "/reactivate").then().statusCode(403);
        }
        given().get("/api/v1/organizations").then().statusCode(401);
    }

    // --- apoio ---

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private Response create(String token, Map<String, Object> body) {
        Map<String, Object> request = new LinkedHashMap<>(body);
        request.remove("shortCodeUsed");
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(request)
                .post("/api/v1/organizations");
    }

    private Response suspend(String id, String reason) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("reason", reason))
                .post("/api/v1/organizations/" + id + "/suspend");
    }

    private Response reactivate(String id) {
        return given().auth().oauth2(admin).post("/api/v1/organizations/" + id + "/reactivate");
    }

    private static Response refresh(String refreshToken) {
        return given().contentType(ContentType.JSON).body(Map.of("refreshToken", refreshToken))
                .post("/api/v1/auth/refresh");
    }

    /** Pedido válido; nas seguradoras inclui o perfil (código curto em {@code shortCodeUsed}). */
    static Map<String, Object> body(OrganizationType type, String tradeName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", type.name());
        body.put("legalName", tradeName + ", SA");
        body.put("tradeName", tradeName);
        body.put("nuit", digits(9));
        if (type.requiresLicense()) {
            body.put("licenseNumber", "ASFPM-" + digits(4));
        }
        body.put("contactEmail", "geral" + digits(6) + "@exemplo.co.mz");
        body.put("contactPhone", "+258211" + digits(6));
        body.put("address", "Av. Julius Nyerere, Maputo");
        body.put("province", "MAPUTO_CITY");
        if (type == OrganizationType.INSURER) {
            String shortCode = shortCode();
            body.put("insurerProfile", insurerProfile(shortCode));
            body.put("shortCodeUsed", shortCode);
        }
        return body;
    }

    static Map<String, Object> insurerProfile(String shortCode) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("shortCode", shortCode);
        profile.put("complaintsEmail", "Reclamacoes@Seguradora.co.mz");
        return profile;
    }

    static String shortCode() {
        return "S" + digits(8);
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
