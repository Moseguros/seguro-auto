package mz.co.moseguros.rating.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.VERSIONS;
import static mz.co.moseguros.catalog.CatalogTestSupport.money;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
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
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Os 10 casos de {@code docs/tasks/S3-rating-cases.md} pelo endpoint do simulador (S3-02 §12): os
 * produtos de {@code products.json} em preparação, com as tarifas e os encargos do ficheiro,
 * dão os mesmos totais e motivos de {@code cases.json}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimulatorReferenceCasesIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    Insurer insurer;
    final Map<String, UUID> versions = new HashMap<>();

    @BeforeAll
    void createProducts() {
        RestAssured.port = port;
        clock.reset();
        QuoteTestSupport support = new QuoteTestSupport(fixtures, identityApi, clock);
        insurer = support.insurerWithCharges("Seguradora Casos");
        for (JsonNode product : ReferenceData.json("products.json")) {
            String code = product.get("code").asText();
            List<Map<String, Object>> coverages = new ArrayList<>();
            product.get("coverages").forEach(c -> coverages.add(coverage(c)));
            Created draft = support.draft(insurer.manager(), product.get("type").asText(), List.of("LIGHT_PASSENGER"),
                    List.of("PRIVATE"), coverages);
            support.putTariff(insurer.manager(), draft.versionId(), ReferenceData.tariff(code));
            versions.put(code, draft.versionId());
        }
    }

    /** Cobertura de {@code products.json} no formato do {@code replaceProductVersionCoverages}. */
    static Map<String, Object> coverage(JsonNode c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", c.get("code").asText());
        m.put("mandatory", c.get("mandatory").asBoolean());
        List<String> requires = new ArrayList<>();
        c.get("requiresCodes").forEach(r -> requires.add(r.asText()));
        m.put("requiresCodes", requires);
        m.put("capitalType", c.get("capitalType").asText());
        if (c.hasNonNull("capital")) {
            m.put("capital", money(c.get("capital").asLong()));
        }
        if (c.hasNonNull("capitalOptions")) {
            List<Map<String, Object>> options = new ArrayList<>();
            c.get("capitalOptions").forEach(o -> options.add(money(o.asLong())));
            m.put("capitalOptions", options);
        }
        m.put("deductibleType", c.get("deductibleType").asText());
        if (c.hasNonNull("deductibleAmount")) {
            m.put("deductibleAmount", money(c.get("deductibleAmount").asLong()));
        }
        if (c.hasNonNull("deductiblePercent")) {
            m.put("deductiblePercent", new BigDecimal(c.get("deductiblePercent").asText()));
        }
        if (c.hasNonNull("deductibleMinAmount")) {
            m.put("deductibleMinAmount", money(c.get("deductibleMinAmount").asLong()));
        }
        m.put("territorialScope", List.of("MZ"));
        return m;
    }

    Stream<Arguments> cases() {
        List<Arguments> list = new ArrayList<>();
        ReferenceData.json("cases.json").forEach(c -> list.add(Arguments.of(c.get("id").asText(), c)));
        return list.stream();
    }

    /** Pedido do simulador com os dados de um caso (só os campos que a tarifa usa). */
    static Map<String, Object> body(JsonNode testCase) {
        JsonNode v = testCase.get("vehicle");
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("year", v.get("year").asInt());
        vehicle.put("category", v.get("category").asText());
        vehicle.put("usage", v.get("usage").asText());
        vehicle.put("provinceOfCirculation", v.get("provinceOfCirculation").asText());
        vehicle.put("hasTracker", v.get("hasTracker").asBoolean());
        if (v.hasNonNull("commercialValue")) {
            vehicle.put("commercialValue", money(v.get("commercialValue").asLong()));
        }
        JsonNode d = testCase.get("driver");
        Map<String, Object> driver = new LinkedHashMap<>();
        driver.put("birthDate", d.get("birthDate").asText());
        driver.put("licenceIssueDate", d.get("licenceIssueDate").asText());
        List<Map<String, Object>> coverages = new ArrayList<>();
        for (JsonNode c : testCase.get("requestedCoverages")) {
            Map<String, Object> coverage = new LinkedHashMap<>();
            coverage.put("code", c.get("code").asText());
            if (c.hasNonNull("capital")) {
                coverage.put("capital", money(c.get("capital").asLong()));
            }
            coverages.add(coverage);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vehicle", vehicle);
        body.put("driver", driver);
        body.put("coverages", coverages);
        body.put("startDate", testCase.get("startDate").asText());
        body.put("durationDays", testCase.get("durationDays").asInt());
        return body;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void theSimulatorGivesTheReferenceResult(String id, JsonNode testCase) throws Exception {
        UUID versionId = versions.get(testCase.get("product").asText());
        Response r = given().auth().oauth2(insurer.manager()).contentType(ContentType.JSON).body(body(testCase))
                .post(VERSIONS + "/{id}/rating/simulate", versionId);
        r.then().statusCode(200);
        JsonNode result = QuoteTestSupport.MAPPER.readTree(r.asString());
        JsonNode expected = testCase.get("expected");
        assertThat(result.get("status").asText()).as(id).isEqualTo(expected.get("status").asText());

        if (expected.get("status").asText().equals("NOT_AVAILABLE")) {
            assertThat(result.get("unavailableReason").asText()).as(id).isEqualTo(expected.get("reason").asText());
            assertThat(result.get("unavailableDetail")).as(id).isEqualTo(expected.get("detail"));
            assertThat(result.get("breakdown").isNull()).as(id).isTrue();
            return;
        }
        JsonNode b = result.get("breakdown");
        assertThat(result.get("unavailableReason").isNull()).as(id).isTrue();
        for (String field : List.of("commercialPremiumAmount", "subtotalAmount", "roundingAdjustmentAmount",
                "totalAmount")) {
            assertThat(b.get(field).asLong()).as(id + " " + field).isEqualTo(expected.get(field).asLong());
        }
        JsonNode expectedCoverages = expected.get("coverages");
        assertThat(b.get("coverages")).as(id).hasSize(expectedCoverages.size());
        for (int i = 0; i < expectedCoverages.size(); i++) {
            JsonNode e = expectedCoverages.get(i);
            JsonNode a = b.get("coverages").get(i);
            assertThat(a.get("code").asText()).as(id).isEqualTo(e.get("code").asText());
            assertThat(a.get("premiumAmount").asLong()).as(id + " " + e.get("code").asText())
                    .isEqualTo(e.get("premiumAmount").asLong());
            assertThat(new BigDecimal(a.get("exactPremium").asText()))
                    .as(id + " " + e.get("code").asText() + " exactPremium")
                    .isEqualByComparingTo(new BigDecimal(e.get("exactPremium").asText()));
        }
        JsonNode expectedCharges = expected.get("charges");
        assertThat(b.get("charges")).as(id).hasSize(expectedCharges.size());
        for (int i = 0; i < expectedCharges.size(); i++) {
            assertThat(b.get("charges").get(i).get("code").asText()).isEqualTo(expectedCharges.get(i).get("code").asText());
            assertThat(b.get("charges").get(i).get("amount").asLong()).as(id + " charge " + i)
                    .isEqualTo(expectedCharges.get(i).get("amount").asLong());
        }
    }
}
