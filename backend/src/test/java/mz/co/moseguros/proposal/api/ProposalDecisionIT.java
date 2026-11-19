package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.SubmissionTestSupport.INSURER_PROPOSALS;
import static mz.co.moseguros.proposal.SubmissionTestSupport.accept;
import static mz.co.moseguros.proposal.SubmissionTestSupport.insurerVersion;
import static mz.co.moseguros.proposal.SubmissionTestSupport.reject;
import static mz.co.moseguros.proposal.SubmissionTestSupport.requestInfo;
import static mz.co.moseguros.proposal.SubmissionTestSupport.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Timestamp;
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
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.proposal.InspectionTestSupport;
import mz.co.moseguros.proposal.ProposalInfoRequested;
import mz.co.moseguros.proposal.ProposalRejected;
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
 * Decisões do subscritor (S4-05 §7): aceitar (com KYC e vistoria), recusar com motivo, pedir
 * informação e voltar a submeter, estados e versões, permissões e auditoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class, SubmissionTestSupport.EventsConfig.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalDecisionIT {

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
        insurer = support.insurerWithProducts("Seguradora Decisão");
        underwriter = support.token(fixtures.staff("Subscritor Decisão", insurer.id(), "UNDERWRITER"));
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

    /** Proposta COMPREHENSIVE submetida (KYC por verificar: fica UNDER_REVIEW). */
    UUID underReview(Customer c) throws Exception {
        UUID id = submissions.readyComprehensive(c, insurer);
        submit(c.token(), id).then().statusCode(200).body("status", equalTo("UNDER_REVIEW"));
        return id;
    }

    void approveInspection(UUID proposalId) {
        UUID inspectionId = InspectionTestSupport.inspectionId(support.owner(), proposalId);
        long v = ((Number) given().auth().oauth2(underwriter)
                .get(InspectionTestSupport.INSURER_INSPECTIONS + "/{id}", inspectionId).then().statusCode(200)
                .extract().path("version")).longValue();
        InspectionTestSupport.approve(underwriter, inspectionId, null, null, v).then().statusCode(200);
    }

    /** Notificação ao cliente (S6-04): a linha INBOX com o deepLink da proposta e uma linha de envio. */
    void awaitNotification(UUID customerId, String code, UUID proposalId) {
        org.awaitility.Awaitility.await().atMost(java.time.Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.owner().queryForList("""
                        select channel from notification.message where user_id = ? and template_code = ?
                           and data->>'deepLink' = ?""", String.class, customerId, code,
                        "moseguros://proposal/" + proposalId)).contains("INBOX").hasSizeGreaterThan(1));
    }

    Map<String, Object> audit(String action, UUID id) {
        return support.owner().queryForMap("""
                select actor_type, actor_id, insurer_id, after_state->>'status' as status
                  from audit.audit_event where action = ? and entity_id = ?""", action, id.toString());
    }

    @Test
    void acceptRequiresTheInspectionAndTheKycAndIsManual() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = underReview(c);

        accept(underwriter, id, null, insurerVersion(underwriter, id)).then().statusCode(409)
                .body("code", equalTo("INSPECTION_NOT_APPROVED"));
        approveInspection(id);
        // KYC por verificar e total acima do limiar: continua em análise e não se aceita.
        SubmissionTestSupport.insurerGet(underwriter, id).then().statusCode(200)
                .body("status", equalTo("UNDER_REVIEW"))
                .body("kyc.requiredForAutomaticAcceptance", equalTo(true))
                .body("underwriting.failures", contains("KYC_REQUIRED"));
        accept(underwriter, id, null, insurerVersion(underwriter, id)).then().statusCode(409)
                .body("code", equalTo("KYC_NOT_VERIFIED"));
        accept(underwriter, id, "x".repeat(1001), insurerVersion(underwriter, id)).then().statusCode(400)
                .body("errors[0].field", equalTo("note")).body("errors[0].code", equalTo("TOO_LONG"));

        submissions.verifyKyc(c, insurer, underwriter);
        Instant now = clock.instant();
        accept(underwriter, id, "Aceite depois de verificar o documento.", insurerVersion(underwriter, id))
                .then().statusCode(200)
                .body("status", equalTo("ACCEPTED"))
                .body("decision.mode", equalTo("MANUAL"))
                .body("decision.reason", equalTo("Aceite depois de verificar o documento."))
                .body("sla.dueAt", nullValue())
                .body("sla.status", nullValue())
                .body("expiresAt", SubmissionTestSupport.instantNear(now.plus(Duration.ofDays(15))));
        assertThat(support.owner().queryForMap(
                "select acceptance_mode, decided_by from proposal.proposal where id = ?", id))
                .containsEntry("acceptance_mode", "MANUAL");
        assertThat(support.owner().queryForObject("select decided_by from proposal.proposal where id = ?",
                String.class, id)).isNotEqualTo("system");
        // O cliente não vê quem decidiu.
        get(c.token(), id).then().statusCode(200).body("decision.mode", equalTo("MANUAL"))
                .body("decision.decidedBy", nullValue());
        assertThat(audit("PROPOSAL.ACCEPT", id)).containsEntry("actor_type", "STAFF")
                .containsEntry("insurer_id", insurer.id()).containsEntry("status", "ACCEPTED");

        // Decidir uma proposta aceite.
        long v = insurerVersion(underwriter, id);
        reject(underwriter, id, "RISK_NOT_ACCEPTED", "Histórico de sinistros acima do aceitável.", v)
                .then().statusCode(409).body("code", equalTo("PROPOSAL_NOT_UNDER_REVIEW"));
        accept(underwriter, id, null, v).then().statusCode(409).body("code", equalTo("PROPOSAL_NOT_UNDER_REVIEW"));
    }

    @Test
    void rejectRequiresAReasonAndTellsTheCustomer() throws Exception {
        Customer c = support.customerWithProfile();
        String email = submissions.verifiedEmail(c);
        UUID id = underReview(c);
        long v = insurerVersion(underwriter, id);

        reject(underwriter, id, "RISK_NOT_ACCEPTED", null, v).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("reason")).body("errors[0].code", equalTo("REQUIRED"));
        reject(underwriter, id, null, "curto", v).then().statusCode(400)
                .body("errors.field", contains("reasonCode", "reason"))
                .body("errors.code", contains("REQUIRED", "TOO_SHORT"));
        reject(underwriter, id, "BAD", "x".repeat(1001), v).then().statusCode(400)
                .body("errors.code", contains("INVALID_VALUE", "TOO_LONG"));
        reject(underwriter, id, "RISK_NOT_ACCEPTED", "Histórico de sinistros acima do aceitável.", v - 1)
                .then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));

        reject(underwriter, id, "RISK_NOT_ACCEPTED", "Histórico de sinistros acima do aceitável.", v)
                .then().statusCode(200)
                .body("status", equalTo("REJECTED"))
                .body("expiresAt", nullValue())
                .body("decision.rejectionReasonCode", equalTo("RISK_NOT_ACCEPTED"))
                .body("decision.reason", equalTo("Histórico de sinistros acima do aceitável."));
        // S6-04: a recusa chega por notify (PROPOSAL_REJECTED); o motivo vê-se no detalhe, pelo link.
        awaitNotification(c.id(), "PROPOSAL_REJECTED", id);
        assertThat(channels.sentTo(email)).extracting(FakeOtpDeliveryChannels.Sent::subject)
                .noneMatch(subject -> subject.contains("não aceite")); // só o proposal.submitted do S4-05
        List<ProposalRejected> rejected = events.of(ProposalRejected.class).stream()
                .filter(e -> e.proposalId().equals(id)).toList();
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).reasonCode()).isEqualTo("RISK_NOT_ACCEPTED");
        assertThat(audit("PROPOSAL.REJECT", id)).containsEntry("actor_type", "STAFF")
                .containsEntry("status", "REJECTED");
    }

    @Test
    void requestInfoInvalidatesTheSignatureReopensTheInspectionAndTheCustomerSubmitsAgain() throws Exception {
        Customer c = support.customerWithProfile();
        String email = submissions.verifiedEmail(c);
        UUID id = underReview(c);
        approveInspection(id);
        Instant firstReview = support.owner().queryForObject(
                "select review_started_at from proposal.proposal where id = ?", Timestamp.class, id).toInstant();
        long v = insurerVersion(underwriter, id);

        requestInfo(underwriter, id, "curta", List.of(), v).then().statusCode(400)
                .body("errors.field", contains("message", "items"))
                .body("errors.code", contains("TOO_SHORT", "REQUIRED"));
        requestInfo(underwriter, id, "Envie de novo a fotografia do chassis.", List.of("INSPECTION", "INSPECTION",
                "FOO"), v).then().statusCode(400)
                .body("errors.field", contains("items[1]", "items[2]"))
                .body("errors.code", contains("ANSWER_DUPLICATED", "INVALID_VALUE"));

        Instant now = clock.instant();
        requestInfo(underwriter, id, "Envie de novo a fotografia do chassis.", List.of("INSPECTION"), v)
                .then().statusCode(200)
                .body("status", equalTo("INFO_REQUESTED"))
                .body("decision.infoRequest.message", equalTo("Envie de novo a fotografia do chassis."))
                .body("decision.infoRequest.items", contains("INSPECTION"))
                .body("decision.infoRequest.requestedAt", notNullValue())
                .body("signature", nullValue())
                .body("signatures[0].invalidatedReason", equalTo("INFO_REQUESTED"))
                .body("inspection.status", equalTo("PENDING"))
                .body("expiresAt", SubmissionTestSupport.instantNear(now.plus(Duration.ofDays(15))));
        assertThat(support.owner().queryForMap(
                "select invalidated_reason from proposal.signature where proposal_id = ?", id))
                .containsEntry("invalidated_reason", "INFO_REQUESTED");
        assertThat(support.owner().queryForObject("select status from proposal.inspection where proposal_id = ?",
                String.class, id)).isEqualTo("PENDING");
        awaitNotification(c.id(), "PROPOSAL_INFO_REQUESTED", id);
        assertThat(events.of(ProposalInfoRequested.class).stream().filter(e -> e.proposalId().equals(id))
                .map(ProposalInfoRequested::items).toList()).containsExactly(List.of("INSPECTION"));
        assertThat(audit("PROPOSAL.REQUEST_INFO", id)).containsEntry("status", "INFO_REQUESTED");

        // Sem nova assinatura nem vistoria não se submete.
        submit(c.token(), id).then().statusCode(409).body("missing", contains("INSPECTION", "SIGNATURE"));

        // O cliente corrige (envia a vistoria de novo), assina e submete: nova avaliação, SLA reiniciado.
        support.advance(Duration.ofDays(1));
        Customer later = new Customer(c.account(), support.token(c.account())); // o token anterior expirou
        InspectionTestSupport.uploaded(later.token(), id, InspectionTestSupport.Photo.jpeg("CHASSIS"));
        InspectionTestSupport.submit(later.token(), id, 84_600, ProposalTestSupport.version(later.token(), id))
                .then().statusCode(200);
        submissions.sign(later, id);
        Instant resubmitted = clock.instant();
        submit(later.token(), id).then().statusCode(200)
                .body("status", equalTo("UNDER_REVIEW"))
                .body("decision.infoRequest", nullValue());
        Map<String, Object> row = support.owner().queryForMap("""
                select review_started_at, submitted_at, info_request_message, underwriting_result->>'evaluatedAt' as at
                  from proposal.proposal where id = ?""", id);
        Instant reviewStarted = ((Timestamp) row.get("review_started_at")).toInstant();
        assertThat(reviewStarted).isCloseTo(resubmitted,
                org.assertj.core.api.Assertions.within(5, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(reviewStarted).isAfter(firstReview.plus(Duration.ofHours(23)));
        assertThat(((Timestamp) row.get("submitted_at")).toInstant()).isBefore(firstReview.plusSeconds(5));
        assertThat(row).containsEntry("info_request_message", null);
        assertThat(Instant.parse((String) row.get("at"))).isCloseTo(resubmitted,
                org.assertj.core.api.Assertions.within(5, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(submissions.audits("PROPOSAL.SUBMIT", id)).isEqualTo(2);
    }

    @Test
    void onlyUnderwritersOfTheInsurerReadAndDecide() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = underReview(c);
        String finance = support.token(fixtures.staff("Financeiro", insurer.id(), "FINANCE"));
        String reader = support.token(fixtures.staff("Administrador", insurer.id(), "INSURER_ADMIN"));
        UUID brokerOrg = fixtures.organization(OrganizationType.BROKER, "Corretora Decisão").getId();
        String broker = support.token(fixtures.staff("Corretor", brokerOrg, "BROKER"));

        for (String token : List.of(c.token(), broker, finance)) {
            given().auth().oauth2(token).get(INSURER_PROPOSALS).then().statusCode(403)
                    .body("code", equalTo("FORBIDDEN"));
            given().auth().oauth2(token).get(INSURER_PROPOSALS + "/{id}", id).then().statusCode(403);
            accept(token, id, null, 0L).then().statusCode(403);
            reject(token, id, "OTHER", "Motivo suficientemente longo.", 0L).then().statusCode(403);
            requestInfo(token, id, "Mensagem suficientemente longa.", List.of("OTHER"), 0L).then().statusCode(403);
        }
        given().get(INSURER_PROPOSALS).then().statusCode(401);
        given().get(INSURER_PROPOSALS + "/{id}", id).then().statusCode(401);
        accept(null, id, null, 0L).then().statusCode(401);

        // INSURER_ADMIN lê e decide (proposal:read e proposal:decide).
        SubmissionTestSupport.insurerGet(reader, id).then().statusCode(200);
        given().auth().oauth2(reader).get(INSURER_PROPOSALS).then().statusCode(200);
    }
}
