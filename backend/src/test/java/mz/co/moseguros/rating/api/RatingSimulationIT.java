package mz.co.moseguros.rating.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.VERSIONS;
import static mz.co.moseguros.rating.QuoteTestSupport.FULL_OD;
import static mz.co.moseguros.rating.QuoteTestSupport.coverages;
import static mz.co.moseguros.rating.QuoteTestSupport.demoAutoCoverages;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Simulador da tarifa (S3-02 §12): versões em preparação e publicadas, tarifa e encargos ainda não
 * gravados, tarifa inválida (400), nada gravado, outra seguradora (404) e permissões (403).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class RatingSimulationIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    QuoteTestSupport support;
    Insurer insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
        support = new QuoteTestSupport(fixtures, identityApi, clock);
        insurer = support.insurerWithCharges("Seguradora Simulador");
    }

    /** Pedido do caso C05 (só os campos que a tarifa usa). */
    static Map<String, Object> c05() {
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("year", 2024);
        vehicle.put("category", "LIGHT_PASSENGER");
        vehicle.put("usage", "PRIVATE");
        vehicle.put("provinceOfCirculation", "MAPUTO_CITY");
        vehicle.put("commercialValue", Map.of("amount", 123_456_789, "currency", "MZN"));
        vehicle.put("hasTracker", false);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vehicle", vehicle);
        body.put("driver", Map.of("birthDate", "1991-09-09", "licenceIssueDate", "2011-04-04"));
        body.put("coverages", coverages(FULL_OD));
        body.put("startDate", "2027-01-15");
        body.put("durationDays", 365);
        return body;
    }

    static Response simulate(String token, UUID versionId, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(VERSIONS + "/{id}/rating/simulate", versionId);
    }

    Created draftDemoAuto() {
        return support.draft(insurer.manager(), "COMPREHENSIVE", List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                demoAutoCoverages());
    }

    int quoteRows() {
        return fixtures.owner().queryForObject("select count(*) from rating.quote where insurer_id = ?",
                Integer.class, insurer.id());
    }

    @Test
    void simulatesADraftWithTheSavedTariffAndThePublishedVersion() {
        Created draft = draftDemoAuto();
        support.putTariff(insurer.manager(), draft.versionId(), ReferenceData.tariff("DEMO-AUTO"));
        simulate(insurer.manager(), draft.versionId(), c05()).then().statusCode(200)
                .body("status", equalTo("OFFERED"))
                .body("unavailableReason", nullValue())
                .body("breakdown.provider", equalTo("INTERNAL"))
                .body("breakdown.variables.vehicleAgeYears", equalTo(3))
                .body("breakdown.variables.driverAgeYears", equalTo(35))
                .body("breakdown.durationFactor", equalTo("1"))
                .body("breakdown.coverages[1].code", equalTo("OD_COLLISION"))
                .body("breakdown.coverages[1].base.type", equalTo("RATE_OF_VEHICLE_VALUE"))
                .body("breakdown.coverages[1].base.rowLabel", equalTo("Até 4 anos"))
                .body("breakdown.coverages[1].factors[2].code", equalTo("PROVINCE"))
                .body("breakdown.coverages[1].premiumAmount", equalTo(3_549_383))
                .body("breakdown.commercialPremiumAmount", equalTo(5_093_766))
                .body("breakdown.subtotalAmount", equalTo(5_226_110))
                .body("breakdown.roundingAdjustmentAmount", equalTo(90))
                .body("breakdown.totalAmount", equalTo(5_226_200));

        Created published = support.published(insurer.manager(), "COMPREHENSIVE", List.of("LIGHT_PASSENGER"),
                List.of("PRIVATE"), demoAutoCoverages(), ReferenceData.tariff("DEMO-AUTO"));
        simulate(insurer.manager(), published.versionId(), c05()).then().statusCode(200)
                .body("status", equalTo("OFFERED"))
                .body("breakdown.totalAmount", equalTo(5_226_200));
        assertThat(quoteRows()).isZero();
    }

    @Test
    void anUnsavedTariffAndUnsavedChargesAreUsed() throws Exception {
        Created draft = draftDemoAuto();
        simulate(insurer.manager(), draft.versionId(), c05()).then().statusCode(200)
                .body("status", equalTo("NOT_AVAILABLE"))
                .body("unavailableReason", equalTo("TARIFF_MISSING"))
                .body("breakdown", nullValue());

        Map<String, Object> withTariff = c05();
        withTariff.put("rating", QuoteTestSupport.MAPPER.convertValue(ReferenceData.tariff("DEMO-AUTO"), Map.class));
        simulate(insurer.manager(), draft.versionId(), withTariff).then().statusCode(200)
                .body("breakdown.totalAmount", equalTo(5_226_200));

        // sem encargos: total = prémio comercial arredondado para cima
        withTariff.put("charges", List.of());
        simulate(insurer.manager(), draft.versionId(), withTariff).then().statusCode(200)
                .body("breakdown.charges.size()", equalTo(0))
                .body("breakdown.commercialPremiumAmount", equalTo(5_093_766))
                .body("breakdown.totalAmount", equalTo(5_093_800));
        withTariff.put("charges", List.of(Map.of("code", "X", "labelPt", "x", "labelEn", "x", "type", "FIXED",
                "value", "abc")));
        simulate(insurer.manager(), draft.versionId(), withTariff).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.code", hasItem("SCHEMA_VIOLATION"));

        // a tarifa gravada não mudou
        assertThat(given().auth().oauth2(insurer.manager()).get(VERSIONS + "/{id}/rating", draft.versionId())
                .then().statusCode(200).extract().<Object>path("rating")).isNull();
        assertThat(quoteRows()).isZero();
    }

    @Test
    void anInvalidTariffIsRejected() {
        Created draft = draftDemoAuto();
        ObjectNode tariff = (ObjectNode) ReferenceData.tariff("DEMO-AUTO");
        ((ArrayNode) tariff.get("coverages")).remove(0);
        Map<String, Object> body = c05();
        body.put("rating", QuoteTestSupport.MAPPER.convertValue(tariff, Map.class));
        simulate(insurer.manager(), draft.versionId(), body).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("/coverages"))
                .body("errors[0].code", equalTo("COVERAGE_TARIFF_MISSING"));

        Map<String, Object> schema = c05();
        schema.put("rating", Map.of("schemaVersion", "rating-v9"));
        simulate(insurer.manager(), draft.versionId(), schema).then().statusCode(400)
                .body("errors.code", hasItem("SCHEMA_VIOLATION"));
    }

    @Test
    void requestRulesExceptTheStartWindow() {
        Created draft = draftDemoAuto();
        support.putTariff(insurer.manager(), draft.versionId(), ReferenceData.tariff("DEMO-AUTO"));
        Map<String, Object> old = c05();
        old.put("startDate", "2020-01-01");
        simulate(insurer.manager(), draft.versionId(), old).then().statusCode(200).body("status", equalTo("OFFERED"));

        Map<String, Object> bad = c05();
        bad.put("durationDays", 30);
        bad.put("coverages", List.of());
        @SuppressWarnings("unchecked")
        Map<String, Object> vehicle = (Map<String, Object>) bad.get("vehicle");
        vehicle.remove("year");
        simulate(insurer.manager(), draft.versionId(), bad).then().statusCode(400)
                .body("errors.code", hasItem("DURATION_NOT_ALLOWED"))
                .body("errors.code", hasItem("OWN_DAMAGE_REQUIRED"))
                .body("errors.field", hasItem("vehicle.year"));

        Map<String, Object> noValue = c05();
        ((Map<?, ?>) noValue.get("vehicle")).remove("commercialValue");
        simulate(insurer.manager(), draft.versionId(), noValue).then().statusCode(400)
                .body("code", equalTo("VEHICLE_VALUE_REQUIRED"));
    }

    @Test
    void anExternalInsurerIsSimulatedWithTheInternalTariff() {
        Created draft = draftDemoAuto();
        support.putTariff(insurer.manager(), draft.versionId(), ReferenceData.tariff("DEMO-AUTO"));
        fixtures.owner().update("""
                update organization.insurer_profile set rating_mode = 'EXTERNAL',
                       rating_endpoint_url = 'https://nao-existe.invalid/r', rating_secret_ref = 'config:x'
                 where organization_id = ?""", insurer.id());
        simulate(insurer.manager(), draft.versionId(), c05()).then().statusCode(200)
                .body("breakdown.provider", equalTo("INTERNAL"))
                .body("breakdown.totalAmount", equalTo(5_226_200));
    }

    @Test
    void otherInsurersAndRolesWithoutRatingManage() {
        Created draft = draftDemoAuto();
        Insurer other = support.insurerWithCharges("Seguradora Outra");
        simulate(other.manager(), draft.versionId(), c05()).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        simulate(insurer.underwriter(), draft.versionId(), c05()).then().statusCode(403);
        simulate(support.customer().token(), draft.versionId(), c05()).then().statusCode(403);
        UUID broker = fixtures.organization(OrganizationType.BROKER, "Corretora Simulador").getId();
        simulate(support.catalog().staffToken(broker, "BROKER"), draft.versionId(), c05()).then().statusCode(403);
        given().contentType(ContentType.JSON).body(c05()).post(VERSIONS + "/{id}/rating/simulate", draft.versionId())
                .then().statusCode(401);
    }

    @Test
    void theSimulatedVariablesAreOnlyThePresentOnes() {
        Created draft = draftDemoAuto();
        support.putTariff(insurer.manager(), draft.versionId(), ReferenceData.tariff("DEMO-AUTO"));
        Response r = simulate(insurer.manager(), draft.versionId(), c05());
        r.then().statusCode(200).body("breakdown.variables.vehicleValue", equalTo(123_456_789))
                .body("breakdown.variables.engineCc", nullValue())
                .body("breakdown.coverages[0].deductible.type", notNullValue());
    }
}
