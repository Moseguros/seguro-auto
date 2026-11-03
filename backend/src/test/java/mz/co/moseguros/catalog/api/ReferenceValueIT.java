package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tabelas de referência (S2-02 §5 e §6): criação de marcas e modelos, regras do pai, código
 * repetido, lista pública (sem token, sem modelos de marcas inactivas), versão, acesso negado e
 * auditoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class ReferenceValueIT {

    private static final String ADMIN_BASE = "/api/v1/admin/reference-domains/";
    private static final String PUBLIC_BASE = "/api/v1/reference-domains/";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    ReferenceValueLookup lookup;

    JdbcTemplate owner;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        admin = token(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"));
    }

    @Test
    void publicListsWorkWithoutTokenAndAreOrdered() {
        given().get(PUBLIC_BASE + "VEHICLE_MAKE/values").then().statusCode(200)
                .body("values.code", hasItems("TOYOTA", "MERCEDES_BENZ", "LAND_ROVER"))
                .body("values.find { it.code == 'TOYOTA' }.labelPt", equalTo("Toyota"));
        given().get(PUBLIC_BASE + "VEHICLE_MODEL/values?parentCode=TOYOTA").then().statusCode(200)
                .body("values.code", hasItems("TOYOTA_HILUX", "TOYOTA_COROLLA"))
                .body("values.parentCode", everyItem(equalTo("TOYOTA")));
        List<String> categories = given().get(PUBLIC_BASE + "LICENCE_CATEGORY/values").then().statusCode(200)
                .extract().path("values.code");
        assertThat(categories).containsSubsequence("A1", "A", "B", "C1", "C", "D1", "D", "BE", "CE", "DE");
        // um token inválido é ignorado nos caminhos públicos
        given().auth().oauth2("invalid").get(PUBLIC_BASE + "VEHICLE_MAKE/values").then().statusCode(200);
    }

    @Test
    void unknownDomainIs404() {
        given().get(PUBLIC_BASE + "COLOUR/values").then().statusCode(404)
                .body("code", equalTo("REFERENCE_DOMAIN_UNKNOWN"));
        given().auth().oauth2(admin).get(ADMIN_BASE + "COLOUR/values").then().statusCode(404)
                .body("code", equalTo("REFERENCE_DOMAIN_UNKNOWN"));
    }

    @Test
    void createsMakeAndModel() {
        String make = code("MAKE");
        Response created = create("VEHICLE_MAKE", Map.of("code", make, "labelPt", "Marca Teste"));
        created.then().statusCode(201)
                .header("Location", endsWith("/api/v1/admin/reference-domains/VEHICLE_MAKE/values/"
                        + created.path("id")))
                .body("domain", equalTo("VEHICLE_MAKE"))
                .body("code", equalTo(make))
                .body("labelPt", equalTo("Marca Teste"))
                .body("sortOrder", equalTo(100))
                .body("active", equalTo(true))
                .body("version", equalTo(0))
                .body("updatedAt", notNullValue());

        String model = make + "_X1";
        create("VEHICLE_MODEL", Map.of("code", model, "labelPt", "X1", "labelEn", "X1 (en)", "parentCode", make,
                "sortOrder", 5)).then().statusCode(201)
                .body("parentCode", equalTo(make))
                .body("labelEn", equalTo("X1 (en)"))
                .body("sortOrder", equalTo(5));

        given().get(PUBLIC_BASE + "VEHICLE_MODEL/values?parentCode=" + make).then().statusCode(200)
                .body("values.code", equalTo(List.of(model)));
        assertThat(lookup.isActive("VEHICLE_MODEL", model)).isTrue();
        assertThat(lookup.isActive("VEHICLE_MAKE", model)).isFalse();
        assertThat(lookup.isActive("COLOUR", model)).isFalse();
    }

    @Test
    void parentRules() {
        create("VEHICLE_MODEL", Map.of("code", code("MODEL"), "labelPt", "Sem marca")).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("parentCode"))
                .body("errors[0].code", equalTo("REQUIRED"));
        create("VEHICLE_MODEL", Map.of("code", code("MODEL"), "labelPt", "Marca inexistente",
                "parentCode", "NO_SUCH_MAKE")).then().statusCode(400)
                .body("errors[0].field", equalTo("parentCode"))
                .body("errors[0].code", equalTo("PARENT_NOT_FOUND"));
        // um modelo não serve de marca
        create("VEHICLE_MODEL", Map.of("code", code("MODEL"), "labelPt", "Pai errado",
                "parentCode", "TOYOTA_HILUX")).then().statusCode(400)
                .body("errors[0].code", equalTo("PARENT_NOT_FOUND"));
        create("LICENCE_CATEGORY", Map.of("code", code("CAT"), "labelPt", "Com pai", "parentCode", "TOYOTA"))
                .then().statusCode(400)
                .body("errors[0].field", equalTo("parentCode"))
                .body("errors[0].code", equalTo("NOT_ALLOWED"));
    }

    @Test
    void codeAndSortOrderRules() {
        create("VEHICLE_MAKE", Map.of("code", "marca-x", "labelPt", "Inválida")).then().statusCode(400)
                .body("errors[0].field", equalTo("code"))
                .body("errors[0].code", equalTo("INVALID_CODE"));
        create("VEHICLE_MAKE", Map.of("code", code("MAKE"), "labelPt", "Ordem", "sortOrder", 10000))
                .then().statusCode(400)
                .body("errors[0].field", equalTo("sortOrder"))
                .body("errors[0].code", equalTo("OUT_OF_RANGE"));
        create("VEHICLE_MAKE", Map.of("code", "TOYOTA", "labelPt", "Toyota outra vez")).then().statusCode(409)
                .body("code", equalTo("REFERENCE_CODE_ALREADY_EXISTS"));
        // o mesmo código noutro domínio é permitido
        String shared = code("SAME");
        create("VEHICLE_MAKE", Map.of("code", shared, "labelPt", "Marca")).then().statusCode(201);
        create("LICENCE_CATEGORY", Map.of("code", shared, "labelPt", "Categoria")).then().statusCode(201);
    }

    @Test
    void deactivatingAMakeHidesItsModelsFromThePublicList() {
        String make = code("MAKE");
        String makeId = create("VEHICLE_MAKE", Map.of("code", make, "labelPt", "Marca")).path("id");
        create("VEHICLE_MODEL", Map.of("code", make + "_M", "labelPt", "Modelo", "parentCode", make))
                .then().statusCode(201);
        given().get(PUBLIC_BASE + "VEHICLE_MODEL/values").then().body("values.code", hasItem(make + "_M"));

        given().auth().oauth2(admin).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + makeId + "/deactivate")
                .then().statusCode(200).body("active", equalTo(false)).body("version", equalTo(1));
        given().auth().oauth2(admin).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + makeId + "/deactivate")
                .then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));

        given().get(PUBLIC_BASE + "VEHICLE_MAKE/values").then().body("values.code", not(hasItem(make)));
        given().get(PUBLIC_BASE + "VEHICLE_MODEL/values").then().body("values.code", not(hasItem(make + "_M")));
        assertThat(lookup.isActive("VEHICLE_MAKE", make)).isFalse();
        // a marca inactiva continua a servir de pai a modelos novos
        create("VEHICLE_MODEL", Map.of("code", make + "_N", "labelPt", "Novo", "parentCode", make))
                .then().statusCode(201);
        // o administrador continua a vê-la
        given().auth().oauth2(admin).get(ADMIN_BASE + "VEHICLE_MODEL/values?parentCode=" + make + "&sort=labelPt")
                .then().statusCode(200).body("totalElements", equalTo(2))
                .body("content.code", equalTo(List.of(make + "_M", make + "_N")));

        given().auth().oauth2(admin).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + makeId + "/activate")
                .then().statusCode(200).body("active", equalTo(true));
        given().auth().oauth2(admin).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + makeId + "/activate")
                .then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        given().get(PUBLIC_BASE + "VEHICLE_MODEL/values").then().body("values.code", hasItem(make + "_M"));
    }

    @Test
    void updateChecksVersionAndKeepsCode() {
        String make = code("MAKE");
        String id = create("VEHICLE_MAKE", Map.of("code", make, "labelPt", "Antes")).path("id");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("labelPt", "Depois");
        body.put("labelEn", "After");
        body.put("sortOrder", 7);
        body.put("version", 0);
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(body)
                .put(ADMIN_BASE + "VEHICLE_MAKE/values/" + id).then().statusCode(200)
                .body("code", equalTo(make))
                .body("labelPt", equalTo("Depois"))
                .body("labelEn", equalTo("After"))
                .body("sortOrder", equalTo(7))
                .body("version", equalTo(1));
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(body)
                .put(ADMIN_BASE + "VEHICLE_MAKE/values/" + id).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        // id de outro domínio
        body.put("version", 1);
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(body)
                .put(ADMIN_BASE + "LICENCE_CATEGORY/values/" + id).then().statusCode(404)
                .body("code", equalTo("REFERENCE_VALUE_NOT_FOUND"));
    }

    @Test
    void adminListFiltersAndSearches() {
        String make = code("SEARCHME");
        create("VEHICLE_MAKE", Map.of("code", make, "labelPt", "Procurável")).then().statusCode(201);
        given().auth().oauth2(admin).get(ADMIN_BASE + "VEHICLE_MAKE/values?q=" + make.toLowerCase())
                .then().statusCode(200).body("totalElements", equalTo(1)).body("content[0].code", equalTo(make));
        given().auth().oauth2(admin).get(ADMIN_BASE + "VEHICLE_MAKE/values?q=PROCUR&active=true")
                .then().statusCode(200).body("content.code", hasItem(make));
        given().auth().oauth2(admin).get(ADMIN_BASE + "VEHICLE_MAKE/values?active=false&size=100")
                .then().statusCode(200).body("content.code", not(hasItem(make)));
        given().auth().oauth2(admin).get(ADMIN_BASE + "VEHICLE_MAKE/values?sort=code")
                .then().statusCode(400).body("errors[0].code", equalTo("INVALID_SORT"));
    }

    @Test
    void writesAreAudited() {
        String make = code("AUDIT");
        String id = create("VEHICLE_MAKE", Map.of("code", make, "labelPt", "Auditada")).path("id");
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("labelPt", "Auditada 2", "version", 0))
                .put(ADMIN_BASE + "VEHICLE_MAKE/values/" + id).then().statusCode(200);
        given().auth().oauth2(admin).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + id + "/deactivate")
                .then().statusCode(200);
        given().auth().oauth2(admin).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + id + "/activate")
                .then().statusCode(200);

        List<Map<String, Object>> events = owner.queryForList("""
                select action, insurer_id, before_state is not null as has_before, after_state is not null as has_after
                  from audit.audit_event where entity_type = 'ReferenceValue' and entity_id = ?
                 order by occurred_at, id""", id);
        assertThat(events).extracting(e -> e.get("action")).containsExactly("REFERENCE_VALUE.CREATE",
                "REFERENCE_VALUE.UPDATE", "REFERENCE_VALUE.DEACTIVATE", "REFERENCE_VALUE.ACTIVATE");
        assertThat(events).allSatisfy(e -> {
            assertThat(e.get("insurer_id")).isNull();
            assertThat(e.get("has_after")).isEqualTo(true);
        });
        assertThat(events).extracting(e -> e.get("has_before")).containsExactly(false, true, true, true);
    }

    @Test
    void customersAndInsurerStaffAreForbiddenAndAnonymousIsUnauthorized() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Referências");
        String customer = token(fixtures.customer("Cliente Referências"));
        String insurerAdmin = token(fixtures.staff("Gestor Seg", insurer.getId(), "INSURER_ADMIN"));
        String id = create("VEHICLE_MAKE", Map.of("code", code("DENY"), "labelPt", "Negada")).path("id");
        for (String token : List.of(customer, insurerAdmin)) {
            given().auth().oauth2(token).get(ADMIN_BASE + "VEHICLE_MAKE/values").then().statusCode(403)
                    .body("code", equalTo("FORBIDDEN"));
            given().auth().oauth2(token).contentType(ContentType.JSON)
                    .body(Map.of("code", code("DENY"), "labelPt", "Negada"))
                    .post(ADMIN_BASE + "VEHICLE_MAKE/values").then().statusCode(403);
            given().auth().oauth2(token).contentType(ContentType.JSON).body(Map.of("labelPt", "X", "version", 0))
                    .put(ADMIN_BASE + "VEHICLE_MAKE/values/" + id).then().statusCode(403);
            given().auth().oauth2(token).post(ADMIN_BASE + "VEHICLE_MAKE/values/" + id + "/deactivate")
                    .then().statusCode(403);
            // a lista pública serve a todos
            given().auth().oauth2(token).get(PUBLIC_BASE + "VEHICLE_MAKE/values").then().statusCode(200);
        }
        given().get(ADMIN_BASE + "VEHICLE_MAKE/values").then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
        given().contentType(ContentType.JSON).body(Map.of("code", code("DENY"), "labelPt", "Negada"))
                .post(ADMIN_BASE + "VEHICLE_MAKE/values").then().statusCode(401);
    }

    // --- apoio ---

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private Response create(String domain, Map<String, Object> body) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON).body(body)
                .post(ADMIN_BASE + domain + "/values");
    }

    /** Código único por execução: {@code PREFIXO_123456}. */
    private static String code(String prefix) {
        return prefix + "_" + ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
    }
}
