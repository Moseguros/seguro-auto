package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogApi;
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
 * Parâmetros da plataforma (S2-02 §5 e §6.6): valor inicial de {@code MTPL_MIN_CAPITAL}, validação
 * por tipo, versão, leitura imediata em {@link CatalogApi}, lista pública e acesso negado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PlatformSettingIT {

    private static final String ADMIN_BASE = "/api/v1/admin/platform-settings";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    CatalogApi catalogApi;

    JdbcTemplate owner;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
        admin = token(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"));
    }

    /** Os testes partilham a BD: o valor inicial é reposto depois de cada um. */
    @AfterEach
    void restore() {
        owner.update("update catalog.platform_setting set value = '300000000' where key = 'MTPL_MIN_CAPITAL'");
    }

    @Test
    void mtplMinCapitalStartsAtTheSeedValue() {
        assertThat(owner.queryForObject("""
                select value_type || ':' || value || ':' || created_by from catalog.platform_setting
                 where key = 'MTPL_MIN_CAPITAL'""", String.class)).isEqualTo("MONEY:300000000:system");
        assertThat(catalogApi.platformSetting("MTPL_MIN_CAPITAL")).isEqualTo("300000000");
        given().auth().oauth2(admin).get(ADMIN_BASE).then().statusCode(200)
                .body("settings.find { it.key == 'MTPL_MIN_CAPITAL' }.valueType", equalTo("MONEY"))
                .body("settings.find { it.key == 'MTPL_MIN_CAPITAL' }.value", equalTo("300000000"));
    }

    @Test
    void publicListWorksWithoutToken() {
        given().get("/api/v1/platform-settings").then().statusCode(200)
                .body("settings.find { it.key == 'MTPL_MIN_CAPITAL' }.valueType", equalTo("MONEY"))
                .body("settings.find { it.key == 'MTPL_MIN_CAPITAL' }.value", equalTo("300000000"))
                .body("settings.find { it.key == 'MTPL_MIN_CAPITAL' }", not(hasKey("version")));
        // um token inválido é ignorado no caminho público
        given().auth().oauth2("invalid").get("/api/v1/platform-settings").then().statusCode(200);
    }

    @Test
    void invalidMoneyValuesAreRejected() {
        long version = version();
        for (String value : List.of("0", "-5", "12,5", "1.5", "", "abc", "1234567890123456")) {
            update(value, version).then().statusCode(400)
                    .body("code", equalTo("VALIDATION_ERROR"))
                    .body("errors[0].field", equalTo("value"))
                    .body("errors[0].code", equalTo("INVALID_VALUE"));
        }
        assertThat(catalogApi.platformSetting("MTPL_MIN_CAPITAL")).isEqualTo("300000000");
    }

    @Test
    void successfulUpdateIsVisibleInCatalogApiImmediately() {
        long version = version();
        update("350000000", version).then().statusCode(200)
                .body("key", equalTo("MTPL_MIN_CAPITAL"))
                .body("valueType", equalTo("MONEY"))
                .body("value", equalTo("350000000"))
                .body("version", equalTo((int) version + 1))
                .body("updatedBy", equalTo(owner.queryForObject("""
                        select updated_by from catalog.platform_setting where key = 'MTPL_MIN_CAPITAL'""",
                        String.class)));
        assertThat(catalogApi.platformSetting("MTPL_MIN_CAPITAL")).isEqualTo("350000000");

        // version antiga
        update("360000000", version).then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));

        assertThat(owner.queryForObject("""
                select count(*) from audit.audit_event
                 where action = 'PLATFORM_SETTING.UPDATE' and entity_type = 'PlatformSetting'
                   and before_state ->> 'value' = '300000000' and after_state ->> 'value' = '350000000'
                   and insurer_id is null""", Integer.class)).isEqualTo(1);
    }

    @Test
    void unknownKeyIs404() {
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("value", "1", "version", 0))
                .put(ADMIN_BASE + "/NO_SUCH_KEY").then().statusCode(404)
                .body("code", equalTo("PLATFORM_SETTING_NOT_FOUND"));
        assertThatThrownBy(() -> catalogApi.platformSetting("NO_SUCH_KEY"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void customersAndInsurerStaffAreForbidden() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Parâmetros");
        String customer = token(fixtures.customer("Cliente Parâmetros"));
        String insurerAdmin = token(fixtures.staff("Gestor Seg", insurer.getId(), "INSURER_ADMIN"));
        for (String token : List.of(customer, insurerAdmin)) {
            given().auth().oauth2(token).get(ADMIN_BASE).then().statusCode(403);
            given().auth().oauth2(token).contentType(ContentType.JSON).body(Map.of("value", "1", "version", 0))
                    .put(ADMIN_BASE + "/MTPL_MIN_CAPITAL").then().statusCode(403);
        }
        given().get(ADMIN_BASE).then().statusCode(401);
    }

    // --- apoio ---

    private long version() {
        return owner.queryForObject("select version from catalog.platform_setting where key = 'MTPL_MIN_CAPITAL'",
                Long.class);
    }

    private Response update(String value, long version) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("value", value, "version", version))
                .put(ADMIN_BASE + "/MTPL_MIN_CAPITAL");
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }
}
