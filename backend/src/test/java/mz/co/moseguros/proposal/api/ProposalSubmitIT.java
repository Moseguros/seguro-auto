package mz.co.moseguros.proposal.api;

import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.update;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static mz.co.moseguros.proposal.SubmissionTestSupport.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.Duration;
import java.time.Instant;
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
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.proposal.AcceptanceMode;
import mz.co.moseguros.proposal.InspectionTestSupport;
import mz.co.moseguros.proposal.ProposalAccepted;
import mz.co.moseguros.proposal.ProposalSubmitted;
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
 * Submissão da proposta (S4-05 §6): completude, aceitação automática (caso 1), análise com o SLA
 * (caso 8) e aceitação automática ao aprovar a vistoria (caso 9, §8), e a proposta deixa de ser
 * editável depois de submetida.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class, SubmissionTestSupport.EventsConfig.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalSubmitIT {

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

    @Autowired
    SubmissionTestSupport.Events events;

    ProposalTestSupport support;
    SubmissionTestSupport submissions;
    Insurer insurer;
    String underwriter;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        submissions = new SubmissionTestSupport(support, channels, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Submissão");
        underwriter = support.token(fixtures.staff("Subscritor Submissão", insurer.id(), "UNDERWRITER"));
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
        channels.reset();
    }

    @Test
    void incompleteProposalsAreNotSubmitted() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        submit(c.token(), id).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_INCOMPLETE"))
                .body("missing", contains("DECLARATIONS", "SUITABILITY", "SIGNATURE"));

        // Tudo respondido mas sem assinatura.
        submissions.answerMtpl(c, id);
        submit(c.token(), id).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_INCOMPLETE"))
                .body("missing", contains("SIGNATURE"));

        // Versão antiga; sem versão.
        submit(c.token(), id, version(c.token(), id) - 1).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        submit(c.token(), id, null).then().statusCode(400).body("errors[0].field", equalTo("version"));
        assertThat(support.owner().queryForObject("select submitted_at from proposal.proposal where id = ?",
                java.sql.Timestamp.class, id)).isNull();
    }

    @Test
    void ownDamageWithoutInspectionIsNotSubmitted() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        ProposalTestSupport.declarations(c.token(), id, SubmissionTestSupport.allNegativeDeclarations(), true,
                version(c.token(), id)).then().statusCode(200);
        ProposalTestSupport.suitability(c.token(), id,
                ProposalTestSupport.suitabilityAnswers("OWN_DAMAGE", "NO", "YES", "NO", "YES"),
                version(c.token(), id)).then().statusCode(200);
        submit(c.token(), id).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_INCOMPLETE"))
                .body("missing", contains("INSPECTION", "SIGNATURE"));
    }

    @Test
    void case1IsAcceptedAutomatically() throws Exception {
        Customer c = support.customerWithProfile();
        String email = submissions.verifiedEmail(c);
        UUID id = submissions.readyMtpl(c, insurer);
        Instant now = clock.instant();

        submit(c.token(), id).then().statusCode(200)
                .body("status", equalTo("ACCEPTED"))
                .body("decision.mode", equalTo("AUTOMATIC"))
                .body("decision.decidedAt", notNullValue())
                .body("decision.insurerDueAt", nullValue())
                .body("decision.rejectionReasonCode", nullValue())
                .body("decision.infoRequest", nullValue())
                .body("kyc.requiredForAutomaticAcceptance", equalTo(false))
                .body("submittedAt", notNullValue())
                .body("expiresAt", SubmissionTestSupport.instantNear(now.plus(Duration.ofDays(15))));

        Map<String, Object> row = support.owner().queryForMap("""
                select status, acceptance_mode, decided_by, review_started_at, sla_due_at,
                       underwriting_result->>'outcome' as outcome, underwriting_result->>'rulesSource' as source,
                       underwriting_result->'inputs'->>'kycState' as kyc
                  from proposal.proposal where id = ?""", id);
        assertThat(row).containsEntry("status", "ACCEPTED").containsEntry("acceptance_mode", "AUTOMATIC")
                .containsEntry("decided_by", "system").containsEntry("review_started_at", null)
                .containsEntry("sla_due_at", null).containsEntry("outcome", "AUTOMATIC")
                .containsEntry("source", "DEFAULT").containsEntry("kyc", "NONE");

        List<ProposalSubmitted> submitted = events.of(ProposalSubmitted.class).stream()
                .filter(e -> e.proposalId().equals(id)).toList();
        assertThat(submitted).hasSize(1);
        assertThat(submitted.get(0).insurerId()).isEqualTo(insurer.id());
        assertThat(submitted.get(0).customerUserId()).isEqualTo(c.id());
        assertThat(submitted.get(0).brokerOrgId()).isNull();
        List<ProposalAccepted> accepted = events.of(ProposalAccepted.class).stream()
                .filter(e -> e.proposalId().equals(id)).toList();
        assertThat(accepted).hasSize(1);
        assertThat(accepted.get(0).mode()).isEqualTo(AcceptanceMode.AUTOMATIC);

        assertThat(submissions.audits("PROPOSAL.SUBMIT", id)).isEqualTo(1);
        assertThat(support.owner().queryForObject("""
                select actor_type from audit.audit_event where action = 'PROPOSAL.ACCEPT' and entity_id = ?""",
                String.class, id.toString())).isEqualTo("SYSTEM");
        assertThat(support.owner().queryForObject("""
                select after_state->>'outcome' from audit.audit_event
                 where action = 'PROPOSAL.SUBMIT' and entity_id = ?""", String.class, id.toString()))
                .isEqualTo("AUTOMATIC");

        // Desde o S6-04 a aceitação chega ao cliente por notify (PROPOSAL_ACCEPTED: centro de notificações e o
        // primeiro canal, aqui o WhatsApp); o e-mail "aceite" do S4-05 deixou de existir (um só aviso).
        String number = get(c.token(), id).then().extract().path("number");
        org.awaitility.Awaitility.await().atMost(java.time.Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.owner().queryForList("""
                        select channel from notification.message where user_id = ? and template_code = 'PROPOSAL_ACCEPTED'
                           and data->>'deepLink' = ?""", String.class, c.id(), "moseguros://proposal/" + id))
                        .containsExactlyInAnyOrder("INBOX", "WHATSAPP"));
        assertThat(support.owner().queryForObject("""
                select body from notification.message where user_id = ? and template_code = 'PROPOSAL_ACCEPTED'
                   and channel = 'INBOX' and data->>'deepLink' = ?""", String.class, c.id(), "moseguros://proposal/" + id))
                .contains(number).contains(insurer.name());
        assertThat(channels.sentTo(email)).isEmpty();
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.outbound_message where correlation_id = ? and purpose = 'PROPOSAL'""",
                Integer.class, id)).isZero();

        // Já não se submete nem se altera.
        submit(c.token(), id).then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
    }

    @Test
    void case8GoesToReviewWithTheSlaAndCase9IsAcceptedWhenTheInspectionIsApproved() throws Exception {
        Customer c = support.customerWithProfile();
        submissions.verifyKyc(c, insurer, underwriter);
        UUID id = submissions.readyComprehensive(c, insurer);
        Instant now = clock.instant();

        submit(c.token(), id).then().statusCode(200)
                .body("status", equalTo("UNDER_REVIEW"))
                .body("expiresAt", nullValue())
                .body("decision.mode", nullValue())
                .body("decision.insurerDueAt", SubmissionTestSupport.instantNear(now.plus(Duration.ofDays(15))))
                .body("kyc.state", equalTo("VERIFIED"))
                .body("kyc.requiredForAutomaticAcceptance", equalTo(false));
        Map<String, Object> row = support.owner().queryForMap("""
                select review_started_at, sla_alert_at, sla_due_at, sla_alerted_at,
                       underwriting_result->>'outcome' as outcome, underwriting_result->'failures'->>0 as failure
                  from proposal.proposal where id = ?""", id);
        assertThat(((java.sql.Timestamp) row.get("review_started_at")).toInstant()).isCloseTo(now, org.assertj.core.api.Assertions.within(5, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(((java.sql.Timestamp) row.get("sla_alert_at")).toInstant()).isCloseTo(now.plus(Duration.ofDays(10)), org.assertj.core.api.Assertions.within(5, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(((java.sql.Timestamp) row.get("sla_due_at")).toInstant()).isCloseTo(now.plus(Duration.ofDays(15)), org.assertj.core.api.Assertions.within(5, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(row).containsEntry("sla_alerted_at", null).containsEntry("outcome", "MANUAL_REVIEW")
                .containsEntry("failure", "INSPECTION_NOT_APPROVED");

        // Submetida: não se submete de novo nem se altera.
        submit(c.token(), id).then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        update(c.token(), id, ProposalTestSupport.START.plusDays(1).toString(), version(c.token(), id))
                .then().statusCode(409).body("code", equalTo("PROPOSAL_NOT_EDITABLE"));

        // A seguradora vê-a na fila, com o SLA.
        SubmissionTestSupport.insurerGet(underwriter, id).then().statusCode(200)
                .body("underwriting.outcome", equalTo("MANUAL_REVIEW"))
                .body("underwriting.failures", contains("INSPECTION_NOT_APPROVED"))
                .body("underwriting.inputs.hasOwnDamage", equalTo(true))
                .body("sla.status", equalTo("ON_TIME"))
                .body("kyc.verifiedByThisInsurer", equalTo(true))
                .body("kyc.checkId", notNullValue())
                .body("vehicle.chassisNumber", notNullValue())
                .body("driver.licenceCategories", hasItems("B"))
                .body("signatures[0].invalidatedAt", nullValue());

        // O subscritor aprova a vistoria: as regras passam e a proposta é aceite automaticamente.
        UUID inspectionId = InspectionTestSupport.inspectionId(support.owner(), id);
        long iv = ((Number) io.restassured.RestAssured.given().auth().oauth2(underwriter)
                .get(InspectionTestSupport.INSURER_INSPECTIONS + "/{id}", inspectionId).then().statusCode(200)
                .extract().path("version")).longValue();
        InspectionTestSupport.approve(underwriter, inspectionId, null, null, iv).then().statusCode(200);
        get(c.token(), id).then().statusCode(200)
                .body("status", equalTo("ACCEPTED"))
                .body("decision.mode", equalTo("AUTOMATIC"))
                .body("expiresAt", notNullValue());
        assertThat(support.owner().queryForMap("""
                select acceptance_mode, decided_by, sla_due_at, underwriting_result->>'outcome' as outcome
                  from proposal.proposal where id = ?""", id))
                .containsEntry("acceptance_mode", "AUTOMATIC").containsEntry("decided_by", "system")
                .containsEntry("sla_due_at", null).containsEntry("outcome", "AUTOMATIC");
        assertThat(events.of(ProposalAccepted.class).stream().filter(e -> e.proposalId().equals(id)).toList())
                .hasSize(1);
    }

    @Test
    void ownDamageAboveTheLimitStaysUnderReviewAfterTheInspectionIsApproved() throws Exception {
        Customer c = support.customerWithProfile();
        submissions.verifyKyc(c, insurer, underwriter);
        UUID vehicleId = mz.co.moseguros.rating.QuoteTestSupport.vehicle(c.token(),
                mz.co.moseguros.rating.QuoteTestSupport.vehicleBody("LIGHT_PASSENGER", "PRIVATE", "MAPUTO_CITY", 2024,
                        160_000_000L, false));
        UUID driverId = mz.co.moseguros.rating.QuoteTestSupport.c05Driver(c.token());
        UUID quoteId = support.quote(c, insurer, vehicleId, driverId, "COMPREHENSIVE", 365,
                mz.co.moseguros.rating.QuoteTestSupport.FULL_OD).quoteId();
        UUID id = ProposalTestSupport.created(c.token(), quoteId);
        submissions.answerOwnDamage(c, id);
        submissions.sign(c, id);
        submit(c.token(), id).then().statusCode(200).body("status", equalTo("UNDER_REVIEW"));

        UUID inspectionId = InspectionTestSupport.inspectionId(support.owner(), id);
        long iv = ((Number) io.restassured.RestAssured.given().auth().oauth2(underwriter)
                .get(InspectionTestSupport.INSURER_INSPECTIONS + "/{id}", inspectionId).then().statusCode(200)
                .extract().path("version")).longValue();
        InspectionTestSupport.approve(underwriter, inspectionId, null, null, iv).then().statusCode(200);
        assertThat(support.owner().queryForMap("""
                select status, underwriting_result->'failures'->>0 as failure,
                       underwriting_result->'inputs'->>'inspectionStatus' as inspection
                  from proposal.proposal where id = ?""", id))
                .containsEntry("status", "UNDER_REVIEW").containsEntry("failure", "VEHICLE_VALUE_ABOVE_LIMIT")
                .containsEntry("inspection", "APPROVED");
    }
}
