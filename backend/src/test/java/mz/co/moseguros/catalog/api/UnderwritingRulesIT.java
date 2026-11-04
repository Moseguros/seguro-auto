package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.catalog.application.UnderwritingRulesService;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.SubmissionTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Regras de aceitação de uma versão de produto (S4-05 §2): leitura com os valores por defeito,
 * gravação em rascunho, versão publicada imutável, erros do schema, isolamento entre seguradoras,
 * permissões, e o {@code proposal} a usar as regras da versão na submissão.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class, SubmissionTestSupport.EventsConfig.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnderwritingRulesIT {

    static final String RULES = CatalogTestSupport.VERSIONS + "/{id}/underwriting-rules";
    static final ObjectMapper MAPPER = new ObjectMapper();

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    ProposalTestSupport support;
    CatalogTestSupport catalog;
    Insurer insurerA;
    Insurer insurerB;

    @BeforeAll
    void createInsurers() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        catalog = support.quotes().catalog();
        support.atNow();
        insurerA = support.quotes().insurerWithCharges("Seguradora Regras A");
        insurerB = support.quotes().insurerWithCharges("Seguradora Regras B");
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        channels.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    static Response getRules(String token, UUID versionId) {
        return given().auth().oauth2(token).get(RULES, versionId);
    }

    static Response putRules(String token, UUID versionId, JsonNode rules, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("rules", rules);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put(RULES, versionId);
    }

    static ObjectNode rules(boolean enabled, long kycLimit) {
        ObjectNode rules = (ObjectNode) UnderwritingRulesService.defaults();
        ((ObjectNode) rules.get("autoAccept")).put("enabled", enabled);
        ((ObjectNode) rules.at("/autoAccept/simplifiedKycMaxPremium")).put("amount", kycLimit);
        return rules;
    }

    @Test
    void productManagerSavesRulesOnADraftAndPublishedVersionsAreImmutable() throws Exception {
        Created draft = catalog.product(insurerA.manager(), "MTPL_ONLY");
        getRules(insurerA.manager(), draft.versionId()).then().statusCode(200)
                .body("isDefault", equalTo(true))
                .body("rules.schemaVersion", equalTo(1))
                .body("rules.autoAccept.simplifiedKycMaxPremium.amount", equalTo(1_000_000))
                .body("rules.autoAccept.ownDamage.maxVehicleValue.amount", equalTo(150_000_000));

        long v = catalog.version(insurerA.manager(), draft.versionId());
        putRules(insurerA.manager(), draft.versionId(), rules(true, 2_000_000), v).then().statusCode(200)
                .body("isDefault", equalTo(false))
                .body("rules.autoAccept.simplifiedKycMaxPremium.amount", equalTo(2_000_000))
                .body("version", equalTo((int) v + 1));
        getRules(insurerA.manager(), draft.versionId()).then().statusCode(200)
                .body("isDefault", equalTo(false))
                .body("rules.autoAccept.simplifiedKycMaxPremium.amount", equalTo(2_000_000));
        // As outras chaves de rules ficam como estavam; a escrita é auditada.
        assertThat(support.owner().queryForObject("""
                select rules->'underwriting'->'autoAccept'->'simplifiedKycMaxPremium'->>'amount'
                  from catalog.product_version where id = ?""", String.class, draft.versionId())).isEqualTo("2000000");
        assertThat(support.owner().queryForObject("""
                select count(*) from audit.audit_event
                 where action = 'PRODUCT_VERSION.UNDERWRITING_UPDATE' and entity_id = ?""",
                Integer.class, draft.versionId().toString())).isEqualTo(1);

        // Versão antiga.
        putRules(insurerA.manager(), draft.versionId(), rules(true, 3_000_000), v).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));

        // Versão publicada.
        Created published = catalog.publishedMtplOnly(insurerB.manager());
        putRules(insurerB.manager(), published.versionId(), rules(false, 1_000_000),
                catalog.version(insurerB.manager(), published.versionId())).then().statusCode(409)
                .body("code", equalTo("PRODUCT_VERSION_NOT_EDITABLE"));
        getRules(insurerB.manager(), published.versionId()).then().statusCode(200).body("isDefault", equalTo(true));
    }

    @Test
    void invalidRulesAreRefusedWithThePath() {
        Created draft = catalog.product(insurerA.manager(), "TEMPORARY");
        long v = catalog.version(insurerA.manager(), draft.versionId());
        ObjectNode unknown = rules(true, 1_000_000);
        ((ObjectNode) unknown.get("autoAccept")).put("maxPremium", 1);
        putRules(insurerA.manager(), draft.versionId(), unknown, v).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", hasItem("rules.autoAccept"))
                .body("errors.code", hasItem("INVALID_RULES"));
        ObjectNode negative = rules(true, -5);
        putRules(insurerA.manager(), draft.versionId(), negative, v).then().statusCode(400)
                .body("errors.field", hasItem("rules.autoAccept.simplifiedKycMaxPremium.amount"));
        ObjectNode version2 = rules(true, 1_000_000);
        version2.put("schemaVersion", 2);
        putRules(insurerA.manager(), draft.versionId(), version2, v).then().statusCode(400)
                .body("errors.field", hasItem("rules.schemaVersion"));
        putRules(insurerA.manager(), draft.versionId(), null, v).then().statusCode(400)
                .body("errors[0].field", equalTo("rules")).body("errors[0].code", equalTo("REQUIRED"));
        // Nada foi gravado.
        getRules(insurerA.manager(), draft.versionId()).then().statusCode(200).body("isDefault", equalTo(true));
    }

    @Test
    void otherInsurersAndUnderwritersCannotChangeTheRules() {
        Created draft = catalog.product(insurerA.manager(), "COMPREHENSIVE");
        long v = catalog.version(insurerA.manager(), draft.versionId());
        getRules(insurerB.manager(), draft.versionId()).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        putRules(insurerB.manager(), draft.versionId(), rules(false, 1_000_000), v).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        getRules(insurerA.manager(), UUID.randomUUID()).then().statusCode(404);
        putRules(insurerA.underwriter(), draft.versionId(), rules(false, 1_000_000), v).then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        given().get(RULES, draft.versionId()).then().statusCode(401);
        getRules(insurerA.manager(), draft.versionId()).then().statusCode(200).body("isDefault", equalTo(true));
    }

    @Test
    void theProposalUsesTheRulesOfTheProductVersion() throws Exception {
        Insurer insurer = support.quotes().insurerWithCharges("Seguradora Regras C");
        Created draft = support.quotes().draft(insurer.manager(), "MTPL_ONLY", List.of("LIGHT_PASSENGER"),
                List.of("PRIVATE"), QuoteTestSupport.demoRcCoverages());
        support.quotes().putTariff(insurer.manager(), draft.versionId(), ReferenceData.tariff("DEMO-RC"));
        putRules(insurer.manager(), draft.versionId(), rules(false, 1_000_000),
                catalog.version(insurer.manager(), draft.versionId())).then().statusCode(200);
        catalog.publish(insurer.manager(), draft.versionId()).then().statusCode(200);

        Customer c = support.customerWithProfile();
        SubmissionTestSupport submissions = new SubmissionTestSupport(support, channels, clock);
        UUID id = submissions.readyMtpl(c, insurer);
        SubmissionTestSupport.submit(c.token(), id).then().statusCode(200)
                .body("status", equalTo("UNDER_REVIEW"))
                .body("kyc.requiredForAutomaticAcceptance", equalTo(false));
        JsonNode result = MAPPER.readTree(support.owner().queryForObject(
                "select underwriting_result::text from proposal.proposal where id = ?", String.class, id));
        assertThat(result.get("rulesSource").asText()).isEqualTo("PRODUCT");
        assertThat(result.get("failures").get(0).asText()).isEqualTo("AUTO_ACCEPT_DISABLED");
        assertThat(result.get("failures")).hasSize(1);
    }
}
