package mz.co.moseguros.catalog.application;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.shared.domain.PlateFormat;
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
 * Formatos de matrícula a partir da BD (S2-02 §4): um formato criado pela API passa a ser aceite
 * nos pedidos sem reiniciar; desactivado deixa de ser; mais de 15 caracteres normalizados nunca
 * são aceites. Substitui o teste "formato novo só por configuração" do S0-03.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class DbPlateFormatProviderIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    DbPlateFormatProvider provider;

    @Autowired
    MutableClock clock;

    JdbcTemplate owner;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        admin = identityApi.issueTokens(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"), null)
                .accessToken();
    }

    @AfterEach
    void cleanUp() {
        owner.update("update catalog.plate_format set active = false where code like 'TEST%'");
        provider.invalidate();
        clock.reset();
    }

    @Test
    void newFormatIsAcceptedWithoutRestartAndDeactivationRejectsIt() {
        // o formato ainda não existe: 2 letras e 4 dígitos não é aceite
        probe("ab 1234").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("plate"))
                .body("errors[0].code", equalTo("INVALID_PLATE"));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", "TEST2");
        body.put("label", "Teste (AB-1234)");
        body.put("pattern", "^(?<l>[A-Z]{2})(?<n>\\d{4})$");
        body.put("displayTemplate", "{l}-{n}");
        body.put("example", "AB-1234");
        body.put("sortOrder", 30);
        String id = given().auth().oauth2(admin).contentType(ContentType.JSON).body(body)
                .post("/api/v1/admin/plate-formats")
                .then().statusCode(201).extract().path("id");

        probe("ab 1234").then().statusCode(200)
                .body("plate", equalTo("AB-1234"))
                .body("formatCode", equalTo("TEST2"));

        given().auth().oauth2(admin).post("/api/v1/admin/plate-formats/" + id + "/deactivate")
                .then().statusCode(200).body("active", equalTo(false));

        probe("ab 1234").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].code", equalTo("INVALID_PLATE"));
    }

    @Test
    void valuesLongerThanFifteenNormalizedCharactersAreRejected() {
        // um formato que aceitaria qualquer comprimento
        owner.update("""
                insert into catalog.plate_format (id, created_at, created_by, updated_at, updated_by, code, label,
                                                  pattern, display_template, example, sort_order)
                values (gen_random_uuid(), now(), 'test', now(), 'test', 'TEST_ANY', 'Qualquer',
                        '^(?<all>[A-Z0-9]+)$', '{all}', 'A1', 999)
                on conflict (code) do update set active = true""");
        provider.invalidate();

        probe("ABCDEFGHIJ-12345").then().statusCode(200).body("plate", equalTo("ABCDEFGHIJ12345"));
        probe("ABCDEFGHIJ 123456").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].code", equalTo("INVALID_PLATE"));
    }

    @Test
    void changesFromOtherInstancesAreSeenWithinThirtySeconds() {
        provider.invalidate();
        assertThat(provider.activeFormats()).extracting(PlateFormat::code).doesNotContain("TEST_OTHER");
        // outra instância grava directamente na BD (sem evento nesta instância)
        owner.update("""
                insert into catalog.plate_format (id, created_at, created_by, updated_at, updated_by, code, label,
                                                  pattern, display_template, example, sort_order)
                values (gen_random_uuid(), now(), 'test', now(), 'test', 'TEST_OTHER', 'Outra',
                        '^(?<l>[A-Z]{4})(?<n>\\d{2})$', '{l} {n}', 'ABCD 12', 40)
                on conflict (code) do update set active = true""");

        assertThat(provider.activeFormats()).extracting(PlateFormat::code).doesNotContain("TEST_OTHER");
        clock.advance(Duration.ofSeconds(31));
        assertThat(provider.activeFormats()).extracting(PlateFormat::code).contains("TEST_OTHER");
    }

    @Test
    void seedFormatsAreLoadedInOrder() {
        assertThat(provider.activeFormats()).extracting(PlateFormat::code).startsWith("STANDARD", "LEGACY");
    }

    /** Pedido de teste com uma matrícula ({@link PlateProbeController}). */
    private Response probe(String plate) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("plate", plate))
                .post("/api/v1/test-probes/plates");
    }
}
