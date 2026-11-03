package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.PRODUCTS;
import static mz.co.moseguros.catalog.CatalogTestSupport.VERSIONS;
import static mz.co.moseguros.catalog.CatalogTestSupport.money;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static mz.co.moseguros.catalog.CatalogTestSupport.ownDamage;
import static mz.co.moseguros.catalog.CatalogTestSupport.vtplOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Versões de produtos (S2-04 §2 e §3): elegibilidade e coberturas, bloqueio optimista, versão
 * publicada imutável, nova versão copiada da anterior e uma só {@code DRAFT}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class ProductVersionIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    CatalogTestSupport support;
    Insurer insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seg Versões " + UUID.randomUUID().toString().substring(0, 4));
    }

    @Test
    void savesEligibilityAndCoveragesAndEachWriteIncrementsTheVersion() {
        String t = insurer.manager();
        Created p = support.product(t, "COMPREHENSIVE");
        long v0 = support.version(t, p.versionId());

        Response eligibility = given().auth().oauth2(t).contentType(ContentType.JSON)
                .body(Map.of("vehicleCategories", List.of("LIGHT_PASSENGER", "LIGHT_MIXED", "LIGHT_PASSENGER"),
                        "usages", List.of("PRIVATE"), "changeNotes", "Primeira versão", "version", v0))
                .put(VERSIONS + "/{id}", p.versionId());
        eligibility.then().statusCode(200)
                .body("vehicleCategories", contains("LIGHT_PASSENGER", "LIGHT_MIXED"))
                .body("usages", contains("PRIVATE"))
                .body("changeNotes", equalTo("Primeira versão"))
                .body("version", equalTo((int) v0 + 1));

        Map<String, Object> theft = ownDamage("OD_THEFT");
        theft.put("requiresCodes", List.of("OD_COLLISION", "OD_FIRE", "OD_NATURE"));
        theft.put("deductiblePercent", 20);
        theft.put("deductibleMinAmount", money(5_000_000));
        Map<String, Object> glass = ownDamage("OD_GLASS");
        glass.put("deductibleType", "FIXED");
        glass.remove("deductiblePercent");
        glass.remove("deductibleMinAmount");
        glass.put("deductibleAmount", money(500_000));
        Map<String, Object> occupants = CatalogTestSupport.base("OCCUPANTS", false, "PER_PERSON");
        occupants.put("capital", money(50_000_000));
        occupants.put("description", "Ocupantes do veículo");
        occupants.put("exclusions", "Condutor sem carta");
        Map<String, Object> crossBorder = CatalogTestSupport.base("CROSS_BORDER", false, "FIXED");
        crossBorder.put("capital", money(10_000_000));
        crossBorder.put("territorialScope", List.of("MZ", "ZA", "SZ"));
        List<Map<String, Object>> coverages = List.of(mtpl(MIN_CAPITAL), vtplOptions(500_000_000, 1_000_000_000),
                ownDamage("OD_COLLISION"), ownDamage("OD_FIRE"), ownDamage("OD_NATURE"), glass, theft, occupants,
                crossBorder);

        Response saved = support.replaceCoverages(t, p.versionId(), coverages, v0 + 1);
        saved.then().statusCode(200)
                .body("version", equalTo((int) v0 + 2))
                .body("coverages.code", contains("MTPL", "VTPL", "OD_COLLISION", "OD_FIRE", "OD_NATURE", "OD_GLASS",
                        "OD_THEFT", "OCCUPANTS", "CROSS_BORDER"))
                .body("coverages[0].capital.amount", equalTo(300_000_000))
                .body("coverages[0].capital.currency", equalTo("MZN"))
                .body("coverages[0]", not(hasKey("capitalOptions")))
                .body("coverages[0].deductibleType", equalTo("NONE"))
                .body("coverages[1].capitalOptions.amount", contains(500_000_000, 1_000_000_000))
                .body("coverages[2].deductiblePercent", equalTo(10.0f))
                .body("coverages[2].deductibleMinAmount.amount", equalTo(2_500_000))
                .body("coverages[5].deductibleAmount.amount", equalTo(500_000))
                .body("coverages[6].requiresCodes", contains("OD_COLLISION", "OD_FIRE", "OD_NATURE"))
                .body("coverages[7].description", equalTo("Ocupantes do veículo"))
                .body("coverages[8].territorialScope", contains("MZ", "ZA", "SZ"));
        assertThat(fixtures.owner().queryForList("""
                select code from catalog.coverage where product_version_id = ? order by sort_order""", String.class,
                p.versionId())).hasSize(9).startsWith("MTPL", "VTPL");

        // substituir por uma lista mais curta apaga as restantes
        support.replaceCoverages(t, p.versionId(), List.of(mtpl(MIN_CAPITAL), ownDamage("OD_FIRE")), v0 + 2)
                .then().statusCode(200).body("version", equalTo((int) v0 + 3))
                .body("coverages.code", contains("MTPL", "OD_FIRE"));

        // version antiga
        support.replaceCoverages(t, p.versionId(), List.of(mtpl(MIN_CAPITAL)), v0 + 2).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        given().auth().oauth2(t).contentType(ContentType.JSON)
                .body(Map.of("vehicleCategories", List.of(), "usages", List.of(), "version", v0))
                .put(VERSIONS + "/{id}", p.versionId()).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));

        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'PRODUCT_VERSION.COVERAGES_UPDATE'
                   and entity_id = ? and jsonb_array_length(after_state -> 'coverages') = 2
                   and jsonb_array_length(before_state -> 'coverages') = 9""", Integer.class,
                p.versionId().toString())).isEqualTo(1);
    }

    @Test
    void invalidCoveragesAreRejectedWithAllTheFieldErrors() {
        String t = insurer.manager();
        Created p = support.product(t, "COMPREHENSIVE");
        Map<String, Object> noCapital = mtpl(MIN_CAPITAL);
        noCapital.remove("capital");
        Map<String, Object> badScope = ownDamage("OD_FIRE");
        badScope.put("territorialScope", List.of("ZA"));
        Map<String, Object> self = ownDamage("OD_THEFT");
        self.put("requiresCodes", List.of("OD_THEFT"));
        Map<String, Object> options = vtplOptions(500, 100);

        support.replaceCoverages(t, p.versionId(), List.of(noCapital, badScope, self, options, mtpl(MIN_CAPITAL)))
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.collect { it.field + ':' + it.code }", contains(
                        "coverages[0].capital:REQUIRED",
                        "coverages[1].territorialScope:MZ_SCOPE_REQUIRED",
                        "coverages[2].requiresCodes:INVALID_REQUIREMENT",
                        "coverages[3].capitalOptions:CAPITAL_OPTIONS_INVALID",
                        "coverages[4].code:DUPLICATED"));
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then().body("coverages.size()", equalTo(0));
    }

    @Test
    void aPublishedVersionIsNotEditable() {
        String t = insurer.manager();
        Created p = support.publishedMtplOnly(t);
        long version = support.version(t, p.versionId());
        String documentId = given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then()
                .body("editable", equalTo(false)).body("status", equalTo("PUBLISHED"))
                .extract().path("documents[0].id");

        given().auth().oauth2(t).contentType(ContentType.JSON)
                .body(Map.of("vehicleCategories", List.of("HEAVY"), "usages", List.of("GOODS"), "version", version))
                .put(VERSIONS + "/{id}", p.versionId()).then().statusCode(409)
                .body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));
        support.replaceCoverages(t, p.versionId(), List.of(mtpl(MIN_CAPITAL * 2)), version).then().statusCode(409)
                .body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));
        support.uploadPdf(t, p.versionId(), "SPECIAL_CONDITIONS").then().statusCode(409)
                .body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));
        given().auth().oauth2(t).delete(VERSIONS + "/{id}/documents/{doc}", p.versionId(), documentId)
                .then().statusCode(409).body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));
        given().auth().oauth2(t).contentType(ContentType.JSON).body(Map.of("version", version))
                .post(VERSIONS + "/{id}/publish", p.versionId()).then().statusCode(409)
                .body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));

        assertThat(support.version(t, p.versionId())).isEqualTo(version);
        assertThat(fixtures.owner().queryForObject("select count(*) from catalog.product_document where product_version_id = ?",
                Integer.class, p.versionId())).isEqualTo(2);
    }

    @Test
    void aNewVersionCopiesThePreviousOneAndOnlyOneDraftExists() {
        String t = insurer.manager();
        Created p = support.product(t, "COMPREHENSIVE");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE", "TAXI"),
                List.of(mtpl(MIN_CAPITAL), ownDamage("OD_COLLISION")));
        // regras de outras fichas (S3/S4): só se guardam e copiam
        fixtures.owner().update("update catalog.product_version set rules = '{\"rating\": {\"base\": 1}}' where id = ?",
                p.versionId());
        support.publish(t, p.versionId()).then().statusCode(200);
        JsonPath v1 = given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then().extract().jsonPath();

        Response created = given().auth().oauth2(t).post(PRODUCTS + "/{id}/versions", p.productId());
        created.then().statusCode(201)
                .body("versionNo", equalTo(2))
                .body("status", equalTo("DRAFT"))
                .body("basedOnVersionId", equalTo(p.versionId().toString()))
                .body("vehicleCategories", contains("LIGHT_PASSENGER"))
                .body("usages", contains("PRIVATE", "TAXI"))
                .body("validFrom", nullValue())
                .body("editable", equalTo(true))
                .body("coverages.code", contains("MTPL", "OD_COLLISION"))
                .body("coverages[1].deductibleMinAmount.amount", equalTo(2_500_000))
                .body("documents.size()", equalTo(2));
        UUID v2 = UUID.fromString(created.path("id"));
        JsonPath copy = created.jsonPath();
        assertThat(copy.getList("documents.fileId")).containsExactlyInAnyOrderElementsOf(v1.getList("documents.fileId"));
        assertThat(copy.getList("documents.id")).doesNotContainAnyElementsOf(v1.getList("documents.id"));
        assertThat(fixtures.owner().queryForObject("select rules ->> 'rating' from catalog.product_version where id = ?",
                String.class, v2)).isEqualTo("{\"base\": 1}");

        given().auth().oauth2(t).post(PRODUCTS + "/{id}/versions", p.productId()).then().statusCode(409)
                .body("code", equalTo("PRODUCT_DRAFT_EXISTS"));

        // a cópia altera-se sem mexer na v1
        support.replaceCoverages(t, v2, List.of(mtpl(MIN_CAPITAL + 100), ownDamage("OD_FIRE"))).then().statusCode(200);
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then()
                .body("coverages.code", contains("MTPL", "OD_COLLISION"))
                .body("coverages[0].capital.amount", equalTo((int) MIN_CAPITAL));
        given().auth().oauth2(t).get(PRODUCTS + "/{id}", p.productId()).then().statusCode(200)
                .body("currentVersion.versionNo", equalTo(1))
                .body("draftVersion.versionNo", equalTo(2))
                .body("draftVersion.id", equalTo(v2.toString()))
                .body("versions.versionNo", contains(2, 1));
    }
}
