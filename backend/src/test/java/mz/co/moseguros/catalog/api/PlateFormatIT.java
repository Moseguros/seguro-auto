package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.application.DbPlateFormatProvider;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
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
 * Formatos de matrícula (S2-02 §4–§6): último formato activo, experiência de um padrão, lista
 * pública sem o padrão, validação, versão, auditoria e acesso negado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PlateFormatIT {

    private static final String ADMIN_BASE = "/api/v1/admin/plate-formats";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    DbPlateFormatProvider provider;

    JdbcTemplate owner;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        admin = token(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"));
    }

    /** Os testes partilham a BD: repõe os formatos iniciais e desactiva os criados aqui. */
    @AfterEach
    void restore() {
        owner.update("update catalog.plate_format set active = true where code in ('STANDARD','LEGACY')");
        owner.update("update catalog.plate_format set active = false where code like 'PFIT%'");
        provider.invalidate();
    }

    @Test
    void lastActiveFormatCannotBeDeactivated() {
        // só STANDARD e LEGACY activos
        owner.update("update catalog.plate_format set active = false where code not in ('STANDARD','LEGACY')");
        String legacy = idOf("LEGACY");
        String standard = idOf("STANDARD");

        deactivate(legacy).then().statusCode(200).body("active", equalTo(false));
        deactivate(legacy).then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        deactivate(standard).then().statusCode(409).body("code", equalTo("LAST_ACTIVE_PLATE_FORMAT"));
        given().get("/api/v1/plate-formats").then().statusCode(200).body("formats.code", equalTo(List.of("STANDARD")));

        given().auth().oauth2(admin).post(ADMIN_BASE + "/" + legacy + "/activate").then().statusCode(200)
                .body("active", equalTo(true));
        given().auth().oauth2(admin).post(ADMIN_BASE + "/" + legacy + "/activate").then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));

        assertThat(owner.queryForList("""
                select action from audit.audit_event where entity_type = 'PlateFormat' and entity_id = ?
                 order by occurred_at, id""", String.class, legacy))
                .containsExactly("PLATE_FORMAT.DEACTIVATE", "PLATE_FORMAT.ACTIVATE");
    }

    @Test
    void testPlateFormatReturnsMatchesAndFormatted() {
        Integer before = owner.queryForObject("select count(*) from catalog.plate_format", Integer.class);
        test("^(?<l>[A-Z]{2})(?<n>\\d{4})$", "{l}-{n}", "ab 1234").then().statusCode(200)
                .body("matches", equalTo(true)).body("formatted", equalTo("AB-1234"));
        test("^(?<l>[A-Z]{2})(?<n>\\d{4})$", "{l}-{n}", "abc 1234").then().statusCode(200)
                .body("matches", equalTo(false)).body("$", not(hasKey("formatted")));
        test("^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$", "{l} {n} {p}", "abc-123-mc").then().statusCode(200)
                .body("matches", equalTo(true)).body("formatted", equalTo("ABC 123 MC"));
        // mais de 15 caracteres normalizados nunca correspondem
        test("^(?<a>[A-Z0-9]+)$", "{a}", "ABCDEFGHIJ123456").then().statusCode(200)
                .body("matches", equalTo(false));
        // as regras do padrão e do modelo aplicam-se
        test("(?<l>[A-Z]{2})", "{l}", "AB").then().statusCode(400)
                .body("errors[0].field", equalTo("pattern")).body("errors[0].code", equalTo("PATTERN_NOT_ANCHORED"));
        test("^(?<l>[A-Z]{2})$", "{x}", "AB").then().statusCode(400)
                .body("errors[0].field", equalTo("displayTemplate"))
                .body("errors[0].code", equalTo("TEMPLATE_GROUP_UNKNOWN"));
        // nada foi gravado
        assertThat(owner.queryForObject("select count(*) from catalog.plate_format", Integer.class)).isEqualTo(before);
    }

    @Test
    void publicListHasNoPatternAndWorksWithoutToken() {
        given().get("/api/v1/plate-formats").then().statusCode(200)
                .body("formats.code", hasItems("STANDARD", "LEGACY"))
                .body("formats[0].code", equalTo("STANDARD"))
                .body("formats[0].label", equalTo("Actual (ABC-123-MC)"))
                .body("formats[0].example", equalTo("ABC-123-MC"))
                .body("formats[0]", not(hasKey("pattern")))
                .body("formats[0]", not(hasKey("displayTemplate")))
                .body("formats[0]", not(hasKey("id")));
        given().get("/api/v1/platform-settings").then().statusCode(200);

        given().auth().oauth2(admin).get(ADMIN_BASE).then().statusCode(200)
                .body("formats.find { it.code == 'STANDARD' }.pattern",
                        equalTo("^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$"))
                .body("formats.find { it.code == 'STANDARD' }.displayTemplate", equalTo("{l}-{n}-{p}"))
                .body("formats.find { it.code == 'STANDARD' }.sortOrder", equalTo(10))
                .body("formats.find { it.code == 'LEGACY' }.example", equalTo("ABC-12-34"));
    }

    @Test
    void createValidatesAndRejectsDuplicateCodes() {
        Map<String, Object> body = format("PFIT_A");
        body.put("example", "AB-9999-X");
        create(body).then().statusCode(400)
                .body("errors[0].field", equalTo("example"))
                .body("errors[0].code", equalTo("EXAMPLE_DOES_NOT_MATCH"));
        body = format("PFIT_A");
        body.put("pattern", "^(?<l>[A-Z]{2}(?<n>\\d{4})$");
        create(body).then().statusCode(400)
                .body("errors[0].field", equalTo("pattern"))
                .body("errors[0].code", equalTo("INVALID_PATTERN"));
        body = format("pfit");
        create(body).then().statusCode(400)
                .body("errors[0].field", equalTo("code")).body("errors[0].code", equalTo("INVALID_CODE"));

        create(format("PFIT_A")).then().statusCode(201)
                .body("code", equalTo("PFIT_A"))
                .body("active", equalTo(true))
                .body("version", equalTo(0));
        create(format("PFIT_A")).then().statusCode(409).body("code", equalTo("PLATE_FORMAT_CODE_ALREADY_EXISTS"));
        create(format("STANDARD")).then().statusCode(409).body("code", equalTo("PLATE_FORMAT_CODE_ALREADY_EXISTS"));
    }

    @Test
    void updateChecksVersionAndIsAudited() {
        String id = create(format("PFIT_U")).path("id");
        Map<String, Object> body = format("IGNORED");
        body.remove("code");
        body.put("label", "Alterado");
        body.put("displayTemplate", "{l} {n}");
        body.put("example", "AB 1234");
        body.put("version", 0);
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(body).put(ADMIN_BASE + "/" + id)
                .then().statusCode(200)
                .body("code", equalTo("PFIT_U"))
                .body("label", equalTo("Alterado"))
                .body("example", equalTo("AB 1234"))
                .body("version", equalTo(1));
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(body).put(ADMIN_BASE + "/" + id)
                .then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(body)
                .put(ADMIN_BASE + "/0190a8e2-0000-7000-8000-000000000000")
                .then().statusCode(404).body("code", equalTo("PLATE_FORMAT_NOT_FOUND"));

        assertThat(owner.queryForList("""
                select action from audit.audit_event where entity_type = 'PlateFormat' and entity_id = ?
                   and insurer_id is null and after_state is not null
                 order by occurred_at, id""", String.class, id))
                .containsExactly("PLATE_FORMAT.CREATE", "PLATE_FORMAT.UPDATE");
    }

    @Test
    void customersAndInsurerStaffAreForbidden() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Matrículas");
        String customer = token(fixtures.customer("Cliente Matrículas"));
        String insurerAdmin = token(fixtures.staff("Gestor Seg", insurer.getId(), "INSURER_ADMIN"));
        String standard = idOf("STANDARD");
        for (String token : List.of(customer, insurerAdmin)) {
            given().auth().oauth2(token).get(ADMIN_BASE).then().statusCode(403);
            given().auth().oauth2(token).contentType(ContentType.JSON).body(format("PFIT_DENY"))
                    .post(ADMIN_BASE).then().statusCode(403);
            given().auth().oauth2(token).post(ADMIN_BASE + "/" + standard + "/deactivate").then().statusCode(403);
            given().auth().oauth2(token).contentType(ContentType.JSON)
                    .body(Map.of("pattern", "^(?<l>[A-Z]{2})$", "displayTemplate", "{l}", "value", "AB"))
                    .post(ADMIN_BASE + "/test").then().statusCode(403);
        }
        given().get(ADMIN_BASE).then().statusCode(401);
        given().get("/api/v1/plate-formats").then().body("formats.code", hasItem("STANDARD"))
                .body("formats.find { it.code == 'PFIT_DENY' }", nullValue());
    }

    // --- apoio ---

    private String idOf(String code) {
        return owner.queryForObject("select id::text from catalog.plate_format where code = ?", String.class, code);
    }

    private Response deactivate(String id) {
        return given().auth().oauth2(admin).post(ADMIN_BASE + "/" + id + "/deactivate");
    }

    private Response create(Map<String, Object> body) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON).body(body).post(ADMIN_BASE);
    }

    private Response test(String pattern, String template, String value) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("pattern", pattern, "displayTemplate", template, "value", value))
                .post(ADMIN_BASE + "/test");
    }

    /** Formato válido de 2 letras e 4 dígitos ({@code AB-1234}), com ordem alta (testado por último). */
    private static Map<String, Object> format(String code) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("label", "Teste " + code);
        body.put("pattern", "^(?<l>[A-Z]{2})(?<n>\\d{4})$");
        body.put("displayTemplate", "{l}-{n}");
        body.put("example", "AB-1234");
        body.put("sortOrder", 900);
        return body;
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }
}
