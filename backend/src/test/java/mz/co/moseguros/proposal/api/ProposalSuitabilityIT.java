package mz.co.moseguros.proposal.api;

import static mz.co.moseguros.proposal.ProposalTestSupport.acknowledge;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitability;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitabilityAnswers;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitabilityList;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
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
 * Questionário de adequação (S4-02 §8 e §9): resultado e motivos na resposta, confirmação dos avisos,
 * gravação nova que limpa a confirmação e validação das respostas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalSuitabilityIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    ProposalTestSupport support;
    Insurer insurer;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Adequação");
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    @Test
    void suitableResultCannotBeAcknowledged() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        suitability(c.token(), id, suitabilityAnswers("MANDATORY_ONLY", "NO", "YES", "NO", "UNSURE"),
                version(c.token(), id)).then().statusCode(200)
                .body("suitability.result", equalTo("SUITABLE"))
                .body("suitability.reasons", hasSize(0))
                .body("suitability.answeredAt", notNullValue())
                .body("suitability.acknowledgedAt", nullValue())
                .body("suitability.answers", hasSize(5))
                .body("suitability.answers.find { it.questionCode == 'SUIT_NEED' }.answer", equalTo("MANDATORY_ONLY"))
                .body("checklist.suitability", equalTo("DONE"));
        acknowledge(c.token(), id, version(c.token(), id)).then().statusCode(409)
                .body("code", equalTo("SUITABILITY_NOT_WARNED"));
    }

    @Test
    void warnedResultIsAcknowledgedAndSavingAgainClearsTheAcknowledgement() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        int revision = revision(id);
        suitability(c.token(), id, suitabilityAnswers("OWN_DAMAGE", "YES", "NO", "NO", "YES"), version(c.token(), id))
                .then().statusCode(200)
                .body("suitability.result", equalTo("WARNED"))
                .body("suitability.reasons", equalTo(List.of("NEED_NOT_COVERED_OWN_DAMAGE",
                        "FINANCED_WITHOUT_OWN_DAMAGE", "CANNOT_AFFORD_REPAIR")))
                .body("checklist.suitability", equalTo("ACK_REQUIRED"));
        assertThat(revision(id)).isEqualTo(revision + 1);

        acknowledge(c.token(), id, version(c.token(), id)).then().statusCode(200)
                .body("suitability.acknowledgedAt", notNullValue())
                .body("checklist.suitability", equalTo("DONE"));
        assertThat(revision(id)).isEqualTo(revision + 2);
        assertThat(support.owner().queryForObject("select suitability_reasons::text from proposal.proposal "
                + "where id = ?", String.class, id))
                .isEqualTo("{NEED_NOT_COVERED_OWN_DAMAGE,FINANCED_WITHOUT_OWN_DAMAGE,CANNOT_AFFORD_REPAIR}");

        // gravar de novo limpa a confirmação
        suitability(c.token(), id, suitabilityAnswers("UNSURE", "NO", "UNSURE", "NO", "UNSURE"), version(c.token(), id))
                .then().statusCode(200)
                .body("suitability.result", equalTo("WARNED"))
                .body("suitability.reasons", equalTo(List.of("NEED_UNSURE")))
                .body("suitability.acknowledgedAt", nullValue())
                .body("checklist.suitability", equalTo("ACK_REQUIRED"));
        assertThat(support.owner().queryForObject("select count(*) from proposal.suitability_answer "
                + "where proposal_id = ?", Integer.class, id)).isEqualTo(5);
        assertThat(support.owner().queryForObject("select count(*) from audit.audit_event where entity_id = ? "
                + "and action in ('PROPOSAL.SUITABILITY_SAVE','PROPOSAL.SUITABILITY_ACKNOWLEDGE')", Integer.class,
                id.toString())).isEqualTo(3);
    }

    @Test
    void comprehensiveWithFullCoverAndAnExcessIsDeductibleNotWanted() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        suitability(c.token(), id, suitabilityAnswers("FULL", "YES", "NO", "NO", "NO"), version(c.token(), id))
                .then().statusCode(200)
                .body("suitability.result", equalTo("WARNED"))
                .body("suitability.reasons", equalTo(List.of("DEDUCTIBLE_NOT_WANTED")));
    }

    @Test
    void invalidAnswersAreValidationErrors() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        long v = version(c.token(), id);

        Map<String, String> four = new LinkedHashMap<>(suitabilityAnswers("FULL", "NO", "YES", "NO", "NO"));
        four.remove("SUIT_DEDUCTIBLE");
        suitability(c.token(), id, four, v).then().statusCode(400)
                .body("errors.find { it.field == 'answers' }.code", equalTo("ANSWERS_INCOMPLETE"));

        suitability(c.token(), id, suitabilityAnswers("FULL", "MAYBE", "YES", "NO", "NO"), v).then().statusCode(400)
                .body("errors.find { it.field == 'answers[1].answer' }.code", equalTo("INVALID_ANSWER"));
        suitability(c.token(), id, suitabilityAnswers("YES", "NO", "YES", "NO", "NO"), v).then().statusCode(400)
                .body("errors.find { it.field == 'answers[0].answer' }.code", equalTo("INVALID_ANSWER"));

        List<Map<String, Object>> list = new ArrayList<>();
        suitabilityAnswers("FULL", "NO", "YES", "NO", "NO").forEach((code, answer) -> list.add(entry(code, answer)));
        list.add(entry("SUIT_NEED", "FULL"));
        list.add(entry("SUIT_AGE", "YES"));
        suitabilityList(c.token(), id, list, v).then().statusCode(400)
                .body("errors.find { it.field == 'answers[5].questionCode' }.code", equalTo("ANSWER_DUPLICATED"))
                .body("errors.find { it.field == 'answers[6].questionCode' }.code", equalTo("UNKNOWN_QUESTION"));
    }

    private static Map<String, Object> entry(String code, String answer) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("questionCode", code);
        m.put("answer", answer);
        return m;
    }

    private int revision(UUID id) {
        return support.owner().queryForObject("select content_revision from proposal.proposal where id = ?",
                Integer.class, id);
    }
}
