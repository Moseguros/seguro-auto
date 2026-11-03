package mz.co.moseguros.rating.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.PRODUCTS;
import static mz.co.moseguros.catalog.CatalogTestSupport.money;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static mz.co.moseguros.catalog.CatalogTestSupport.ownDamage;
import static mz.co.moseguros.catalog.CatalogTestSupport.vtplOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import mz.co.moseguros.shared.error.DomainException;
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
 * Tarifa das versões de produto (S3-01 §5): gravar e ler em {@code DRAFT}, versão publicada e
 * {@code version} antiga (409), validação (400), tamanho (400), {@code validateRatingRules} sem
 * gravar, isolamento entre seguradoras (404), permissões (403), auditoria e
 * {@code CatalogApi.insurerVersion}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class RatingRulesIT {

    static final String RATING = CatalogTestSupport.VERSIONS + "/{id}/rating";
    static final ObjectMapper MAPPER = new ObjectMapper();

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    CatalogApi catalogApi;

    CatalogTestSupport support;
    Insurer insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seg Tarifa " + UUID.randomUUID().toString().substring(0, 4));
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    /** As coberturas do produto {@code DEMO-AUTO} de {@code products.json}. */
    static List<Map<String, Object>> demoAutoCoverages() {
        Map<String, Object> theft = ownDamage("OD_THEFT");
        theft.put("requiresCodes", List.of("OD_COLLISION", "OD_FIRE", "OD_NATURE"));
        theft.put("deductiblePercent", 20);
        theft.put("deductibleMinAmount", money(5_000_000));
        return List.of(mtpl(MIN_CAPITAL), vtplOptions(500_000_000, 1_000_000_000), ownDamage("OD_COLLISION"),
                ownDamage("OD_FIRE"), ownDamage("OD_NATURE"), theft);
    }

    /** Versão {@code DRAFT} de um produto {@code COMPREHENSIVE} com as coberturas do {@code DEMO-AUTO}. */
    UUID comprehensiveDraft(String token) {
        Created p = support.product(token, "COMPREHENSIVE");
        support.replaceCoverages(token, p.versionId(), demoAutoCoverages()).then().statusCode(200);
        return p.versionId();
    }

    static JsonNode mtplOnlyTariff() {
        ObjectNode rc = (ObjectNode) ReferenceData.tariff("DEMO-RC");
        ((ArrayNode) rc.get("coverages")).remove(1);
        return rc;
    }

    static Response put(String token, UUID versionId, JsonNode rating, long version) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("rating", rating, "version", version)).put(RATING, versionId);
    }

    static Response validate(String token, UUID versionId, JsonNode rating) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("rating", rating)).post(RATING + "/validate", versionId);
    }

    static Response get(String token, UUID versionId) {
        return given().auth().oauth2(token).get(RATING, versionId);
    }

    static JsonNode ratingOf(Response response) throws Exception {
        return MAPPER.readTree(response.asString()).get("rating");
    }

    @Test
    void savesAndReadsTheTariffOfADraft() throws Exception {
        String t = insurer.manager();
        UUID versionId = comprehensiveDraft(t);
        long v0 = support.version(t, versionId);

        Response empty = get(t, versionId);
        empty.then().statusCode(200)
                .body("productVersionId", equalTo(versionId.toString()))
                .body("productType", equalTo("COMPREHENSIVE"))
                .body("editable", equalTo(true))
                .body("configured", equalTo(false))
                .body("rating", nullValue())
                .body("version", equalTo((int) v0))
                .body("coverages.code", contains("MTPL", "VTPL", "OD_COLLISION", "OD_FIRE", "OD_NATURE", "OD_THEFT"))
                .body("coverages[0].mandatory", equalTo(true))
                .body("coverages[0].capitalType", equalTo("FIXED"))
                .body("coverages[0].capital.amount", equalTo(300_000_000))
                .body("coverages[0].capitalOptions", nullValue())
                .body("coverages[1].capital", nullValue())
                .body("coverages[1].capitalOptions.amount", contains(500_000_000, 1_000_000_000));
        assertThat(MAPPER.readTree(empty.asString()).has("rating")).isTrue();

        JsonNode tariff = ReferenceData.tariff("DEMO-AUTO");
        Response saved = put(t, versionId, tariff, v0);
        saved.then().statusCode(200)
                .body("configured", equalTo(true))
                .body("editable", equalTo(true))
                .body("version", equalTo((int) v0 + 1));
        assertThat(ratingOf(saved)).isEqualTo(tariff);

        Response read = get(t, versionId);
        read.then().statusCode(200).body("configured", equalTo(true)).body("version", equalTo((int) v0 + 1));
        assertThat(ratingOf(read)).isEqualTo(tariff);
        // a versão da linha do catálogo é a mesma
        assertThat(support.version(t, versionId)).isEqualTo(v0 + 1);

        // gravar a mesma tarifa conta como escrita
        put(t, versionId, tariff, v0 + 1).then().statusCode(200).body("version", equalTo((int) v0 + 2));

        // as outras chaves de rules ficam como estavam
        support.owner().update("""
                update catalog.product_version set rules = rules || '{"underwriting": {"x": 1}}'::jsonb where id = ?""",
                versionId);
        put(t, versionId, ReferenceData.tariff("DEMO-AUTO"), v0 + 2).then().statusCode(200);
        assertThat(support.owner().queryForObject("""
                select rules -> 'underwriting' ->> 'x' from catalog.product_version where id = ?""",
                String.class, versionId)).isEqualTo("1");

        List<Map<String, Object>> audit = support.owner().queryForList("""
                select actor_type, insurer_id, entity_type, before_state::text as before, after_state::text as after
                  from audit.audit_event where action = 'RATING_RULES.UPDATE' and entity_id = ?
                 order by occurred_at, id""", versionId.toString());
        assertThat(audit).hasSize(3);
        assertThat(audit.get(0).get("entity_type")).isEqualTo("ProductVersion");
        assertThat(audit.get(0).get("insurer_id")).isEqualTo(insurer.id());
        assertThat(audit.get(0).get("before")).isNull();
        assertThat(MAPPER.readTree((String) audit.get(0).get("after"))).isEqualTo(tariff);
        assertThat(MAPPER.readTree((String) audit.get(1).get("before"))).isEqualTo(tariff);
    }

    @Test
    void aPublishedVersionIsNotEditable() {
        String t = insurer.manager();
        Created published = support.publishedMtplOnly(t);
        put(t, published.versionId(), mtplOnlyTariff(), support.version(t, published.versionId()))
                .then().statusCode(409).body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));
        get(t, published.versionId()).then().statusCode(200).body("editable", equalTo(false))
                .body("configured", equalTo(false));
        // validateRatingRules funciona também em versões publicadas
        validate(t, published.versionId(), mtplOnlyTariff()).then().statusCode(200).body("valid", equalTo(true));
    }

    @Test
    void anOldVersionIsAConcurrentModification() {
        String t = insurer.manager();
        UUID versionId = comprehensiveDraft(t);
        long v0 = support.version(t, versionId);
        put(t, versionId, ReferenceData.tariff("DEMO-AUTO"), v0).then().statusCode(200);
        put(t, versionId, ReferenceData.tariff("DEMO-AUTO"), v0).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void anInvalidTariffIsRejectedWithAllTheErrors() {
        String t = insurer.manager();
        UUID versionId = comprehensiveDraft(t);
        long v0 = support.version(t, versionId);
        ObjectNode tariff = (ObjectNode) ReferenceData.tariff("DEMO-AUTO");
        ((ObjectNode) tariff.at("/coverages/2/base/rows/1")).put("value", "1.5");
        ((ObjectNode) tariff.at("/coverages/0/factors/0/rows/1/when/driverAgeYears")).put("gte", 80);
        put(t, versionId, tariff, v0).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", contains("/coverages/0/factors/0/rows/1/when/driverAgeYears",
                        "/coverages/2/base/rows/1/value"))
                .body("errors.code", contains("INVALID_RANGE", "VALUE_OUT_OF_RANGE"));

        ObjectNode schema = (ObjectNode) ReferenceData.tariff("DEMO-AUTO");
        schema.put("schemaVersion", "rating-v2");
        put(t, versionId, schema, v0).then().statusCode(400)
                .body("errors.field", hasItem("/schemaVersion"))
                .body("errors.code", hasItem("SCHEMA_VIOLATION"));

        given().auth().oauth2(t).contentType(ContentType.JSON).body(Map.of("version", v0)).put(RATING, versionId)
                .then().statusCode(400).body("errors.field", hasItem("rating"));
        get(t, versionId).then().body("configured", equalTo(false)).body("version", equalTo((int) v0));
    }

    @Test
    void aTariffAbove256KiBIsTooLarge() {
        String t = insurer.manager();
        UUID versionId = comprehensiveDraft(t);
        ObjectNode big = (ObjectNode) ReferenceData.tariff("DEMO-AUTO");
        ArrayNode rows = (ArrayNode) big.at("/coverages/0/factors/0/rows");
        while (big.toString().length() < 270_000) {
            rows.addObject().put("label", "x".repeat(80)).putObject("when");
        }
        put(t, versionId, big, support.version(t, versionId)).then().statusCode(400)
                .body("code", equalTo("RATING_RULES_TOO_LARGE"));
        validate(t, versionId, big).then().statusCode(400).body("code", equalTo("RATING_RULES_TOO_LARGE"));
        // sem Content-Length (chunked): conta os bytes lidos
        given().auth().oauth2(t).contentType(ContentType.JSON)
                .body(new java.io.ByteArrayInputStream(("{\"rating\": " + big + ", \"version\": 0}")
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .post(RATING + "/validate", versionId)
                .then().statusCode(400).body("code", equalTo("RATING_RULES_TOO_LARGE"));
    }

    @Test
    void validateDoesNotSave() {
        String t = insurer.manager();
        UUID versionId = comprehensiveDraft(t);
        long v0 = support.version(t, versionId);
        validate(t, versionId, ReferenceData.tariff("DEMO-AUTO")).then().statusCode(200)
                .body("valid", equalTo(true)).body("errors", org.hamcrest.Matchers.empty());
        ObjectNode bad = (ObjectNode) ReferenceData.tariff("DEMO-AUTO");
        ((ArrayNode) bad.get("coverages")).remove(5);
        validate(t, versionId, bad).then().statusCode(200)
                .body("valid", equalTo(false))
                .body("errors[0].field", equalTo("/coverages"))
                .body("errors[0].code", equalTo("COVERAGE_TARIFF_MISSING"))
                .body("errors[0].message", equalTo("Missing tariff for coverage OD_THEFT"));
        get(t, versionId).then().body("configured", equalTo(false)).body("version", equalTo((int) v0));
        assertThat(support.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'RATING_RULES.UPDATE' and entity_id = ?""",
                Integer.class, versionId.toString())).isZero();
    }

    @Test
    void anotherInsurerNeitherReadsNorWritesTheTariff() {
        UUID versionId = comprehensiveDraft(insurer.manager());
        long v0 = support.version(insurer.manager(), versionId);
        Insurer other = support.insurer("Seg Outra " + UUID.randomUUID().toString().substring(0, 4));
        get(other.manager(), versionId).then().statusCode(404).body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        put(other.manager(), versionId, ReferenceData.tariff("DEMO-AUTO"), v0).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        validate(other.manager(), versionId, ReferenceData.tariff("DEMO-AUTO")).then().statusCode(404);
        get(insurer.manager(), UUID.randomUUID()).then().statusCode(404);
        get(insurer.manager(), versionId).then().body("configured", equalTo(false)).body("version", equalTo((int) v0));
    }

    @Test
    void permissions() {
        UUID versionId = comprehensiveDraft(insurer.manager());
        long v0 = support.version(insurer.manager(), versionId);
        get(insurer.underwriter(), versionId).then().statusCode(200);
        put(insurer.underwriter(), versionId, ReferenceData.tariff("DEMO-AUTO"), v0).then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        validate(insurer.underwriter(), versionId, ReferenceData.tariff("DEMO-AUTO")).then().statusCode(403);
        String admin = support.staffToken(insurer.id(), "INSURER_ADMIN");
        validate(admin, versionId, ReferenceData.tariff("DEMO-AUTO")).then().statusCode(200);

        String customer = support.customerToken();
        String broker = support.staffToken(fixtures.organization(OrganizationType.BROKER, "Corretora Tarifa").getId(),
                "BROKER");
        for (String token : List.of(customer, broker)) {
            get(token, versionId).then().statusCode(403);
            put(token, versionId, ReferenceData.tariff("DEMO-AUTO"), v0).then().statusCode(403);
            validate(token, versionId, ReferenceData.tariff("DEMO-AUTO")).then().statusCode(403);
        }
        given().get(RATING, versionId).then().statusCode(401);
    }

    @Test
    void insurerVersionSeesOnlyTheInsurersOwnVersionsInAnyState() {
        UUID draft = comprehensiveDraft(insurer.manager());
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), insurer.id(), insurer.id()));
        ProductVersionView view = catalogApi.insurerVersion(draft);
        assertThat(view.status()).isEqualTo("DRAFT");
        assertThat(view.insurerId()).isEqualTo(insurer.id());
        assertThat(view.coverages()).hasSize(6);
        assertThatThrownBy(() -> catalogApi.version(draft)).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", "PRODUCT_VERSION_NOT_FOUND");

        Insurer other = support.insurer("Seg Alheia " + UUID.randomUUID().toString().substring(0, 4));
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), other.id(), other.id()));
        assertThatThrownBy(() -> catalogApi.insurerVersion(draft)).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", "PRODUCT_VERSION_NOT_FOUND");
        assertThatThrownBy(() -> catalogApi.updateRatingRules(draft, mtplOnlyTariff(), view.version()))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", "PRODUCT_VERSION_NOT_FOUND");
        TenantContext.set(TenantSnapshot.customer(UUID.randomUUID()));
        assertThatThrownBy(() -> catalogApi.insurerVersion(draft)).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("code", "PRODUCT_VERSION_NOT_FOUND");
    }

    @Test
    void aNewVersionFromAPublishedOneBringsTheTariff() throws Exception {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        JsonNode tariff = mtplOnlyTariff();
        put(t, p.versionId(), tariff, support.version(t, p.versionId())).then().statusCode(200);
        support.publish(t, p.versionId()).then().statusCode(200);
        get(t, p.versionId()).then().statusCode(200).body("editable", equalTo(false)).body("configured", equalTo(true));

        Response created = given().auth().oauth2(t).post(PRODUCTS + "/{id}/versions", p.productId());
        created.then().statusCode(201);
        UUID v2 = UUID.fromString(created.path("id"));
        Response draft = get(t, v2);
        draft.then().statusCode(200).body("editable", equalTo(true)).body("configured", equalTo(true));
        assertThat(ratingOf(draft)).isEqualTo(tariff);
    }
}
