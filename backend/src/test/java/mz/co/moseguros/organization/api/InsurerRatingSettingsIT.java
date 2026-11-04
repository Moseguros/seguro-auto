package mz.co.moseguros.organization.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Encargos da seguradora (S3-01 §4) e {@code OrganizationApi.insurerProfile} (§3): ler e gravar,
 * erros de validação, bloqueio optimista, isolamento entre seguradoras, permissões e leitura em
 * âmbito {@code CUSTOMER}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class InsurerRatingSettingsIT {

    static final String SETTINGS = "/api/v1/insurer/rating-settings";
    static final ObjectMapper MAPPER = new ObjectMapper();
    static final String DEMO = """
            [{"code":"DEMO_STAMP","labelPt":"Imposto de selo (provisório)","labelEn":"Stamp duty (provisional)",
              "type":"PERCENT","value":"2.5"},
             {"code":"DEMO_FEE","labelPt":"Taxa fixa (provisória)","labelEn":"Fixed fee (provisional)",
              "type":"FIXED","value":5000}]""";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    OrganizationApi organizationApi;

    CatalogTestSupport support;
    Insurer insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seg Encargos " + UUID.randomUUID().toString().substring(0, 4));
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    static JsonNode json(String text) {
        try {
            return MAPPER.readTree(text);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    static Response put(String token, JsonNode charges, long version) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("charges", charges, "version", version)).put(SETTINGS);
    }

    static long version(String token) {
        return given().auth().oauth2(token).get(SETTINGS).then().statusCode(200).extract().jsonPath().getLong("version");
    }

    @Test
    void readsAndSavesTheCharges() throws Exception {
        String t = insurer.manager();
        given().auth().oauth2(t).get(SETTINGS).then().statusCode(200)
                .body("ratingMode", equalTo("INTERNAL"))
                .body("charges", empty());
        long v0 = version(t);

        Response saved = put(t, json(DEMO), v0);
        saved.then().statusCode(200)
                .body("ratingMode", equalTo("INTERNAL"))
                .body("charges.code", contains("DEMO_STAMP", "DEMO_FEE"))
                .body("charges[0].value", equalTo("2.5"))
                .body("charges[1].value", equalTo(5000))
                .body("charges[1].labelEn", equalTo("Fixed fee (provisional)"))
                .body("version", equalTo((int) v0 + 1));
        assertThat(MAPPER.readTree(saved.asString()).get("charges")).isEqualTo(json(DEMO));
        given().auth().oauth2(insurer.underwriter()).get(SETTINGS).then().statusCode(200)
                .body("charges.code", contains("DEMO_STAMP", "DEMO_FEE"));

        List<Map<String, Object>> audit = support.owner().queryForList("""
                select entity_type, before_state::text as before, after_state::text as after from audit.audit_event
                 where action = 'INSURER_CHARGES.UPDATE' and insurer_id = ?""", insurer.id());
        assertThat(audit).hasSize(1);
        assertThat(audit.get(0).get("entity_type")).isEqualTo("InsurerProfile");
        assertThat(MAPPER.readTree((String) audit.get(0).get("before"))).isEqualTo(json("[]"));
        assertThat(MAPPER.readTree((String) audit.get(0).get("after"))).isEqualTo(json(DEMO));

        put(t, json("[]"), v0 + 1).then().statusCode(200).body("charges", empty());
    }

    @Test
    void validationAndConcurrency() {
        String t = insurer.manager();
        long v0 = version(t);
        put(t, json("""
                [{"code":"A1","labelPt":"a","labelEn":"a","type":"FIXED","value":1},
                 {"code":"A1","labelPt":"b","labelEn":"b","type":"PERCENT","value":"1"}]"""), v0)
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", contains("/1/code"))
                .body("errors.code", contains("DUPLICATE_CHARGE"));
        put(t, json("""
                [{"code":"A1","labelPt":"a","labelEn":"a","type":"FIXED","value":"5000"}]"""), v0)
                .then().statusCode(400)
                .body("errors.field", contains("/0/value"))
                .body("errors.code", contains("SCHEMA_VIOLATION"));
        given().auth().oauth2(t).contentType(ContentType.JSON).body(Map.of("version", v0)).put(SETTINGS)
                .then().statusCode(400).body("errors.field", contains("charges"));

        put(t, json(DEMO), v0).then().statusCode(200);
        put(t, json(DEMO), v0).then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void anInsurerDoesNotSeeTheChargesOfAnother() {
        Insurer other = support.insurer("Seg Encargos B " + UUID.randomUUID().toString().substring(0, 4));
        put(other.manager(), json(DEMO), version(other.manager())).then().statusCode(200);
        given().auth().oauth2(insurer.manager()).get(SETTINGS).then().statusCode(200).body("charges", empty());
        put(insurer.manager(), json("[]"), version(insurer.manager())).then().statusCode(200);
        given().auth().oauth2(other.manager()).get(SETTINGS).then().statusCode(200)
                .body("charges.code", contains("DEMO_STAMP", "DEMO_FEE"));
    }

    @Test
    void permissions() {
        long v0 = version(insurer.manager());
        put(insurer.underwriter(), json(DEMO), v0).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        String admin = support.staffToken(insurer.id(), "INSURER_ADMIN");
        put(admin, json(DEMO), v0).then().statusCode(200);

        String customer = support.customerToken();
        String broker = support.staffToken(fixtures.organization(OrganizationType.BROKER, "Corretora Encargos").getId(),
                "BROKER");
        String platform = support.staffToken(null, "PLATFORM_ADMIN");
        for (String token : List.of(customer, broker, platform)) {
            given().auth().oauth2(token).get(SETTINGS).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
            put(token, json(DEMO), v0).then().statusCode(403);
        }
        given().get(SETTINGS).then().statusCode(401);
    }

    @Test
    void insurerProfileWorksInCustomerScopeAndReturnsOnlyWhatWasAsked() {
        put(insurer.manager(), json(DEMO), version(insurer.manager())).then().statusCode(200);
        Insurer other = support.insurer("Seg Perfil B " + UUID.randomUUID().toString().substring(0, 4));
        UUID broker = fixtures.organization(OrganizationType.BROKER, "Corretora Perfil").getId();
        UserAccount customer = fixtures.customer("Cliente Perfil");

        TenantContext.set(TenantSnapshot.customer(customer.getId()));
        // em CUSTOMER o RLS não mostra as organizações...
        assertThat(organizationApi.findSummary(insurer.id())).isEmpty();
        // ...mas o perfil da seguradora pedida lê-se em SYSTEM
        InsurerProfileView view = organizationApi.insurerProfile(insurer.id()).orElseThrow();
        assertThat(view.insurerId()).isEqualTo(insurer.id());
        assertThat(view.displayName()).isEqualTo(insurer.name());
        assertThat(view.shortCode()).startsWith("T");
        assertThat(view.status()).isEqualTo(OrganizationStatus.ACTIVE);
        assertThat(view.ratingMode()).isEqualTo(RatingMode.INTERNAL);
        assertThat(view.ratingEndpointUrl()).isNull();
        assertThat(view.ratingSecretRef()).isNull();
        assertThat(view.logoFileId()).isNull();
        assertThat(view.charges()).containsExactly(
                ChargeRule.percent("DEMO_STAMP", "Imposto de selo (provisório)", "Stamp duty (provisional)",
                        new BigDecimal("2.5")),
                ChargeRule.fixed("DEMO_FEE", "Taxa fixa (provisória)", "Fixed fee (provisional)", 5000));
        assertThat(TenantContext.current().scope()).isEqualTo(mz.co.moseguros.shared.tenant.Scope.CUSTOMER);

        assertThat(organizationApi.insurerProfile(broker)).isEmpty();
        assertThat(organizationApi.insurerProfile(UUID.randomUUID())).isEmpty();
        assertThat(organizationApi.insurerProfile(null)).isEmpty();
        Map<UUID, InsurerProfileView> many = organizationApi.insurerProfiles(
                Set.of(insurer.id(), other.id(), broker, UUID.randomUUID()));
        assertThat(many).containsOnlyKeys(insurer.id(), other.id());
        assertThat(many.get(other.id()).charges()).isEmpty();
        assertThat(organizationApi.insurerProfiles(Set.of())).isEmpty();

        // um mediador também lê o perfil (âmbito ORG sem seguradora)
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), broker, null));
        assertThat(organizationApi.insurerProfile(insurer.id())).isPresent();
    }
}
