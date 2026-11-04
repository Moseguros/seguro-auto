package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.SubmissionTestSupport.INSURER_PROPOSALS;
import static mz.co.moseguros.proposal.SubmissionTestSupport.accept;
import static mz.co.moseguros.proposal.SubmissionTestSupport.insurerGet;
import static mz.co.moseguros.proposal.SubmissionTestSupport.reject;
import static mz.co.moseguros.proposal.SubmissionTestSupport.requestInfo;
import static mz.co.moseguros.proposal.SubmissionTestSupport.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.SubmissionTestSupport;
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
 * Isolamento das propostas na seguradora (S4-05 §7): a seguradora B não lista, não abre nem decide
 * propostas da A (404), e nenhuma seguradora vê propostas nunca submetidas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class, SubmissionTestSupport.EventsConfig.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InsurerProposalIsolationIT {

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
    SubmissionTestSupport submissions;
    Insurer insurerA;
    Insurer insurerB;
    String underwriterA;
    String underwriterB;

    @BeforeAll
    void createInsurers() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        submissions = new SubmissionTestSupport(support, channels, clock);
        support.atNow();
        insurerA = support.insurerWithProducts("Seguradora Isolamento A");
        insurerB = support.insurerWithProducts("Seguradora Isolamento B");
        underwriterA = support.token(fixtures.staff("Subscritor A", insurerA.id(), "UNDERWRITER"));
        underwriterB = support.token(fixtures.staff("Subscritor B", insurerB.id(), "UNDERWRITER"));
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

    List<String> listed(String token, String... statuses) {
        var request = given().auth().oauth2(token).queryParam("size", 100);
        for (String status : statuses) {
            request = request.queryParam("status", status);
        }
        JsonPath page = request.get(INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath();
        return page.getList("content.id", String.class);
    }

    @Test
    void otherInsurersAndDraftsAreNotVisible() throws Exception {
        // A: uma submetida (aceite automaticamente), uma em análise e um rascunho nunca submetido.
        Customer c = support.customerWithProfile();
        UUID accepted = submissions.readyMtpl(c, insurerA);
        submit(c.token(), accepted).then().statusCode(200).body("status", equalTo("ACCEPTED"));
        UUID reviewing = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurerA).quoteId());
        Instant now = clock.instant();
        support.owner().update("""
                update proposal.proposal
                   set status = 'UNDER_REVIEW', submitted_at = ?, expires_at = null, review_started_at = ?,
                       sla_alert_at = ?, sla_due_at = ?
                 where id = ?""", Timestamp.from(now), Timestamp.from(now),
                Timestamp.from(now.plus(Duration.ofDays(10))), Timestamp.from(now.plus(Duration.ofDays(15))),
                reviewing);
        UUID draft = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurerA).quoteId());

        // A vê as submetidas (UNDER_REVIEW por defeito) e não vê o rascunho.
        assertThat(listed(underwriterA)).contains(reviewing.toString()).doesNotContain(accepted.toString());
        assertThat(listed(underwriterA, "ACCEPTED", "UNDER_REVIEW")).contains(accepted.toString(),
                reviewing.toString());
        assertThat(listed(underwriterA, "DRAFT")).doesNotContain(draft.toString());
        insurerGet(underwriterA, accepted).then().statusCode(200).body("status", equalTo("ACCEPTED"));
        insurerGet(underwriterA, draft).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        accept(underwriterA, draft, null, 0L).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));

        // B não lista, não abre nem decide as da A.
        assertThat(listed(underwriterB, "ACCEPTED", "UNDER_REVIEW", "DRAFT"))
                .doesNotContain(accepted.toString(), reviewing.toString(), draft.toString());
        for (UUID id : List.of(accepted, reviewing, draft)) {
            insurerGet(underwriterB, id).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
            accept(underwriterB, id, null, 0L).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
            reject(underwriterB, id, "OTHER", "Motivo suficientemente longo.", 0L).then().statusCode(404)
                    .body("code", equalTo("PROPOSAL_NOT_FOUND"));
            requestInfo(underwriterB, id, "Mensagem suficientemente longa.", List.of("OTHER"), 0L)
                    .then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        }
        assertThat(support.owner().queryForObject("select status from proposal.proposal where id = ?",
                String.class, reviewing)).isEqualTo("UNDER_REVIEW");

        // Filtros e pesquisa da fila.
        String number = support.owner().queryForObject("select number from proposal.proposal where id = ?",
                String.class, reviewing);
        JsonPath byNumber = given().auth().oauth2(underwriterA).queryParam("q", number).get(INSURER_PROPOSALS)
                .then().statusCode(200).extract().jsonPath();
        assertThat(byNumber.getList("content.id", String.class)).containsExactly(reviewing.toString());
        assertThat(byNumber.getString("content[0].customerName")).isEqualTo("Ana Maria Mussa");
        assertThat(byNumber.getString("content[0].slaStatus")).isEqualTo("ON_TIME");
        assertThat(byNumber.getString("content[0].kycState")).isEqualTo("NONE");
        assertThat(given().auth().oauth2(underwriterA).queryParam("q", "maria mus").queryParam("size", 100)
                .get(INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath()
                .getList("content.id", String.class)).contains(reviewing.toString());
        assertThat(given().auth().oauth2(underwriterA).queryParam("productType", "COMPREHENSIVE")
                .get(INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath()
                .getList("content.id", String.class)).doesNotContain(reviewing.toString());
        String today = java.time.LocalDate.ofInstant(now, java.time.ZoneId.of("Africa/Maputo")).toString();
        assertThat(given().auth().oauth2(underwriterA).queryParam("submittedFrom", today)
                .queryParam("submittedTo", today).get(INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath()
                .getList("content.id", String.class)).contains(reviewing.toString());
        assertThat(given().auth().oauth2(underwriterA).queryParam("submittedFrom",
                        java.time.LocalDate.parse(today).plusDays(1).toString())
                .get(INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath()
                .getList("content.id", String.class)).doesNotContain(reviewing.toString());
        given().auth().oauth2(underwriterA).queryParam("sort", "slaDueAt,desc").get(INSURER_PROPOSALS)
                .then().statusCode(200);
        given().auth().oauth2(underwriterA).queryParam("sort", "number,asc").get(INSURER_PROPOSALS)
                .then().statusCode(400);
    }
}
