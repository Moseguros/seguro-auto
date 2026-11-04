package mz.co.moseguros.proposal.api;

import static mz.co.moseguros.proposal.ProposalTestSupport.answer;
import static mz.co.moseguros.proposal.ProposalTestSupport.declarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.validDeclarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
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
 * Declarações de risco (S4-02 §7 e §9): cada linha da tabela de validação, uma gravação válida
 * (confirmação, revisão do conteúdo, substituição das respostas, auditoria sem os pormenores) e
 * escritas fora dos estados editáveis.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalDeclarationsIT {

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
    Customer customer;
    UUID proposalId;

    @BeforeAll
    void createProposal() throws Exception {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Declarações");
        customer = support.customerWithProfile();
        proposalId = ProposalTestSupport.created(customer.token(), support.mtplOnly(customer, insurer).quoteId());
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        customer = new Customer(customer.account(), support.token(customer.account()));
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    Response save(List<Map<String, Object>> answers, Boolean confirmed) {
        return declarations(customer.token(), proposalId, answers, confirmed, version(customer.token(), proposalId));
    }

    static List<Map<String, Object>> with(int index, Map<String, Object> replacement) {
        List<Map<String, Object>> list = new ArrayList<>(validDeclarations());
        list.set(index, replacement);
        return list;
    }

    void expectFieldError(Response r, String field, String code) {
        r.then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.find { it.field == '" + field + "' }.code", equalTo(code));
    }

    @Test
    void missingQuestionIsAnswersIncomplete() {
        List<Map<String, Object>> four = new ArrayList<>(validDeclarations());
        four.remove(4);
        expectFieldError(save(four, true), "answers", "ANSWERS_INCOMPLETE");
        expectFieldError(save(List.of(), true), "answers", "ANSWERS_INCOMPLETE");
    }

    @Test
    void repeatedQuestionIsAnswerDuplicated() {
        List<Map<String, Object>> list = new ArrayList<>(validDeclarations());
        list.add(answer("PREVIOUS_REFUSAL", false, null, null));
        expectFieldError(save(list, true), "answers[5].questionCode", "ANSWER_DUPLICATED");
    }

    @Test
    void unknownCodeIsUnknownQuestion() {
        List<Map<String, Object>> list = new ArrayList<>(validDeclarations());
        list.add(answer("DRUNK_DRIVING", false, null, null));
        expectFieldError(save(list, true), "answers[5].questionCode", "UNKNOWN_QUESTION");
    }

    @Test
    void yesWithoutDetailsOrClaimsCountIsRequired() {
        expectFieldError(save(with(0, answer("CLAIMS_LAST_3_YEARS", true, null, "Colisão em Maputo em 2025.")), true),
                "answers[0].claimsCount", "REQUIRED");
        expectFieldError(save(with(0, answer("CLAIMS_LAST_3_YEARS", true, 1, null)), true),
                "answers[0].details", "REQUIRED");
        expectFieldError(save(with(1, answer("PREVIOUS_REFUSAL", true, null, null)), true),
                "answers[1].details", "REQUIRED");
    }

    @Test
    void detailsOutsideTenToFiveHundredCharacters() {
        expectFieldError(save(with(2, answer("USAGE_DIFFERENT", true, null, "Táxi")), true),
                "answers[2].details", "TOO_SHORT");
        expectFieldError(save(with(3, answer("VEHICLE_MODIFIED", true, null, "x".repeat(501))), true),
                "answers[3].details", "TOO_LONG");
    }

    @Test
    void claimsCountOutsideOneToTwentyIsOutOfRange() {
        expectFieldError(save(with(0, answer("CLAIMS_LAST_3_YEARS", true, 0, "Colisão em Maputo em 2025.")), true),
                "answers[0].claimsCount", "OUT_OF_RANGE");
        expectFieldError(save(with(0, answer("CLAIMS_LAST_3_YEARS", true, 21, "Colisão em Maputo em 2025.")), true),
                "answers[0].claimsCount", "OUT_OF_RANGE");
    }

    @Test
    void noWithDetailsOrClaimsCountIsNotAllowed() {
        expectFieldError(save(with(4, answer("NOVICE_DRIVERS", false, null, "O meu filho tem carta há 1 ano.")), true),
                "answers[4].details", "NOT_ALLOWED");
        expectFieldError(save(with(0, answer("CLAIMS_LAST_3_YEARS", false, 2, null)), true),
                "answers[0].claimsCount", "NOT_ALLOWED");
    }

    @Test
    void truthfulnessNotConfirmedIsMustBeTrue() {
        expectFieldError(save(validDeclarations(), false), "truthfulnessConfirmed", "MUST_BE_TRUE");
        expectFieldError(save(validDeclarations(), null), "truthfulnessConfirmed", "MUST_BE_TRUE");
    }

    @Test
    void validSaveConfirmsIncrementsTheRevisionAndReplacesTheAnswers() {
        int revision = support.owner().queryForObject(
                "select content_revision from proposal.proposal where id = ?", Integer.class, proposalId);
        save(validDeclarations(), true).then().statusCode(200)
                .body("declarations.confirmedAt", notNullValue())
                .body("declarations.answers", hasSize(5))
                .body("declarations.answers.find { it.questionCode == 'CLAIMS_LAST_3_YEARS' }.claimsCount",
                        equalTo(1))
                .body("declarations.answers.find { it.questionCode == 'PREVIOUS_REFUSAL' }.answer", equalTo(false))
                .body("checklist.declarations", equalTo("DONE"));
        assertThat(support.owner().queryForObject("select content_revision from proposal.proposal where id = ?",
                Integer.class, proposalId)).isEqualTo(revision + 1);

        // nova gravação: as mesmas 5 linhas, actualizadas
        save(with(0, answer("CLAIMS_LAST_3_YEARS", false, null, null)), true).then().statusCode(200)
                .body("declarations.answers.find { it.questionCode == 'CLAIMS_LAST_3_YEARS' }.answer", equalTo(false))
                .body("declarations.answers.find { it.questionCode == 'CLAIMS_LAST_3_YEARS' }.details", nullValue());
        assertThat(support.owner().queryForObject("select count(*) from proposal.declaration where proposal_id = ?",
                Integer.class, proposalId)).isEqualTo(5);
        assertThat(support.owner().queryForObject("""
                select count(*) from proposal.declaration d join proposal.proposal p on p.id = d.proposal_id
                 where d.proposal_id = ? and d.insurer_id = p.insurer_id""", Integer.class, proposalId)).isEqualTo(5);
        assertThat(support.owner().queryForObject("select content_revision from proposal.proposal where id = ?",
                Integer.class, proposalId)).isEqualTo(revision + 2);

        // auditoria só com os códigos e as respostas sim/não
        List<String> audits = support.owner().queryForList("""
                select after_state::text from audit.audit_event
                 where action = 'PROPOSAL.DECLARATIONS_SAVE' and entity_id = ?""", String.class,
                proposalId.toString());
        assertThat(audits).hasSizeGreaterThanOrEqualTo(2)
                .allSatisfy(a -> assertThat(a).contains("CLAIMS_LAST_3_YEARS").doesNotContain("Colisão"));
    }

    @Test
    void writesOutsideDraftOrInfoRequestedAreNotEditable() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        long v = version(c.token(), id);
        support.setStatus(id, "UNDER_REVIEW");
        declarations(c.token(), id, validDeclarations(), true, v).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_NOT_EDITABLE"));
        ProposalTestSupport.update(c.token(), id, ProposalTestSupport.START.toString(), v).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_NOT_EDITABLE"));
        ProposalTestSupport.suitability(c.token(), id, ProposalTestSupport.suitabilityAnswers("MANDATORY_ONLY", "NO",
                "YES", "NO", "UNSURE"), v).then().statusCode(409).body("code", equalTo("PROPOSAL_NOT_EDITABLE"));
        ProposalTestSupport.acknowledge(c.token(), id, v).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_NOT_EDITABLE"));

        // INFO_REQUESTED volta a ser editável
        support.setStatus(id, "INFO_REQUESTED");
        declarations(c.token(), id, validDeclarations(), true, v).then().statusCode(200);
    }

    @Test
    void oldVersionIsAConflict() {
        long v = version(customer.token(), proposalId);
        declarations(customer.token(), proposalId, validDeclarations(), true, v).then().statusCode(200);
        declarations(customer.token(), proposalId, validDeclarations(), true, v).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
    }
}
