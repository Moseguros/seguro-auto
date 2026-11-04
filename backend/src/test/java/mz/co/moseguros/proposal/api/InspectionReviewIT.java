package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.InspectionTestSupport.INSURER_INSPECTIONS;
import static mz.co.moseguros.proposal.InspectionTestSupport.approve;
import static mz.co.moseguros.proposal.InspectionTestSupport.auditCount;
import static mz.co.moseguros.proposal.InspectionTestSupport.inspectionId;
import static mz.co.moseguros.proposal.InspectionTestSupport.markSubmitted;
import static mz.co.moseguros.proposal.InspectionTestSupport.reject;
import static mz.co.moseguros.proposal.InspectionTestSupport.requestOnSite;
import static mz.co.moseguros.proposal.InspectionTestSupport.submit;
import static mz.co.moseguros.proposal.InspectionTestSupport.uploadRequired;
import static mz.co.moseguros.proposal.InspectionTestSupport.uploaded;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.InspectionTestSupport.Photo;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Revisão da vistoria pelo subscritor (S4-03 §6 e §7): fila, fotografias, recusa com ângulos a repetir,
 * aprovação presencial, proposta fora de {@code UNDER_REVIEW}, permissões e auditoria. A proposta é
 * posta em {@code UNDER_REVIEW} com {@code submitted_at} pelo teste (a submissão é do S4-05).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InspectionReviewIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    ProposalTestSupport support;
    JdbcTemplate owner;
    Insurer insurer;
    String underwriter;
    String admin;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        owner = support.owner();
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Revisao Vistoria");
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        underwriter = support.token(fixtures.staff("Subscritor", insurer.id(), "UNDERWRITER"));
        admin = support.token(fixtures.staff("Administrador", insurer.id(), "INSURER_ADMIN"));
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    private record Owned(String token, UUID proposalId, UUID inspectionId) {
    }

    /** Proposta COMPREHENSIVE com os seis ângulos (e uma extra), vistoria submetida e proposta em análise. */
    private Owned submittedForReview(String proposalStatus) throws Exception {
        Customer c = support.customerWithProfile();
        UUID proposalId = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        uploadRequired(c.token(), proposalId);
        uploaded(c.token(), proposalId, Photo.jpeg("EXTRA"));
        submit(c.token(), proposalId, 84_500, version(c.token(), proposalId)).then().statusCode(200);
        markSubmitted(owner, proposalId, proposalStatus);
        return new Owned(c.token(), proposalId, inspectionId(owner, proposalId));
    }

    private long inspectionVersion(UUID inspectionId) {
        return given().auth().oauth2(underwriter).get(INSURER_INSPECTIONS + "/{id}", inspectionId).then()
                .statusCode(200).extract().jsonPath().getLong("version");
    }

    @Test
    void underwriterListsViewsPhotosAndRejectsWithRetakeSlots() throws Exception {
        Owned o = submittedForReview("UNDER_REVIEW");

        // fila: só as SUBMITTED por defeito, com os dados da proposta
        JsonPath list = given().auth().oauth2(underwriter).queryParam("size", 100).get(INSURER_INSPECTIONS).then()
                .statusCode(200).extract().jsonPath();
        Map<String, Object> row = list.getList("content", Map.class).stream()
                .filter(r -> o.inspectionId().toString().equals(r.get("id"))).findFirst().orElseThrow();
        assertThat(row.get("proposalId")).isEqualTo(o.proposalId().toString());
        assertThat((String) row.get("proposalNumber")).startsWith("PR-2027-");
        assertThat(row.get("customerName")).isEqualTo("Ana Maria Mussa");
        assertThat(row.get("vehiclePlate")).isNotNull();
        assertThat(row.get("mode")).isEqualTo("SELF_PHOTOS");
        assertThat(row.get("status")).isEqualTo("SUBMITTED");
        assertThat(row.get("photoCount")).isEqualTo(7);
        assertThat((String) row.get("submittedAt")).startsWith("2027-01-10T08:1");
        given().auth().oauth2(underwriter).queryParam("mode", "ON_SITE").queryParam("size", 100)
                .get(INSURER_INSPECTIONS).then().statusCode(200)
                .body("content.id", not(hasItem(o.inspectionId().toString())));
        given().auth().oauth2(underwriter).queryParam("sort", "foo,asc").get(INSURER_INSPECTIONS).then()
                .statusCode(400);

        // detalhe com as fotografias, que o subscritor e o administrador (proposal:read) descarregam
        JsonPath detail = given().auth().oauth2(underwriter).get(INSURER_INSPECTIONS + "/{id}", o.inspectionId())
                .then().statusCode(200).extract().jsonPath();
        assertThat(detail.getList("photos")).hasSize(7);
        assertThat(detail.getList("missingSlots")).isEmpty();
        assertThat(detail.getString("status")).isEqualTo("SUBMITTED");
        for (String fileId : detail.getList("photos.fileId", String.class)) {
            given().auth().oauth2(underwriter).get("/api/v1/files/{id}", fileId).then().statusCode(200)
                    .contentType("image/jpeg");
        }
        given().auth().oauth2(admin).get("/api/v1/files/{id}", detail.getString("photos[0].fileId")).then()
                .statusCode(200);
        long v = detail.getLong("version");

        // INSURER_ADMIN não tem inspection:review
        approve(admin, o.inspectionId(), null, null, v).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        reject(admin, o.inspectionId(), "A fotografia do chassis está ilegível.", null, v).then().statusCode(403);
        given().auth().oauth2(admin).get(INSURER_INSPECTIONS).then().statusCode(403);

        // validações da recusa
        reject(underwriter, o.inspectionId(), "Ilegível", null, v).then().statusCode(400)
                .body("errors.field", contains("reason")).body("errors.code", contains("TOO_SHORT"));
        reject(underwriter, o.inspectionId(), "A fotografia do chassis está ilegível.",
                List.of("ROOF", "CHASSIS", "CHASSIS"), v).then().statusCode(400)
                .body("errors.field", contains("retakeSlots[0]", "retakeSlots[2]"))
                .body("errors.code", contains("INVALID_SLOT", "DUPLICATED"));
        reject(underwriter, o.inspectionId(), "A fotografia do chassis está ilegível.", List.of("CHASSIS"), v + 1)
                .then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));

        reject(underwriter, o.inspectionId(), "A fotografia do chassis está ilegível.", List.of("CHASSIS"), v)
                .then().statusCode(200)
                .body("status", equalTo("REJECTED"))
                .body("review.rejectionReason", equalTo("A fotografia do chassis está ilegível."))
                .body("review.retakeSlots", contains("CHASSIS"))
                .body("review.reviewedAt", startsWith("2027-01-10T08:1"))
                .body("review", not(hasKey("reviewedBy")));
        reject(underwriter, o.inspectionId(), "Outra vez a mesma coisa.", null, v + 1).then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));
        assertThat(owner.queryForObject("select reviewed_by from proposal.inspection where id = ?", UUID.class,
                o.inspectionId())).isNotNull();

        // o cliente vê o motivo e os ângulos a repetir; a proposta continua em análise
        get(o.token(), o.proposalId()).then().statusCode(200)
                .body("status", equalTo("UNDER_REVIEW"))
                .body("inspection.status", equalTo("REJECTED"))
                .body("inspection.review.rejectionReason", equalTo("A fotografia do chassis está ilegível."))
                .body("inspection.review.retakeSlots", contains("CHASSIS"))
                .body("inspection.review", not(hasKey("reviewedBy")))
                .body("checklist.inspection", equalTo("REJECTED"));
        given().auth().oauth2(underwriter).queryParam("status", "REJECTED").queryParam("size", 100)
                .get(INSURER_INSPECTIONS).then().statusCode(200)
                .body("content.id", hasItem(o.inspectionId().toString()));

        assertThat(auditCount(owner, "INSPECTION.REJECT", o.inspectionId())).isEqualTo(1);
        Map<String, Object> audit = owner.queryForMap("select actor_type, insurer_id, after_state::text as after "
                + "from audit.audit_event where action = 'INSPECTION.REJECT' and entity_id = ?",
                o.inspectionId().toString());
        assertThat(audit.get("actor_type")).isEqualTo("STAFF");
        assertThat(audit.get("insurer_id")).isEqualTo(insurer.id());
        assertThat((String) audit.get("after")).contains("CHASSIS", "REJECTED");
    }

    @Test
    void approvalOfSelfPhotosAndOfAnOnSiteInspection() throws Exception {
        // fotografias: aprovar sem modo usa o da vistoria
        Owned photos = submittedForReview("UNDER_REVIEW");
        long v = inspectionVersion(photos.inspectionId());
        approve(underwriter, photos.inspectionId(), "ON_SITE", null, v).then().statusCode(400)
                .body("errors.field", contains("notes")).body("errors.code", contains("REQUIRED"));
        approve(underwriter, photos.inspectionId(), null, "x".repeat(1001), v).then().statusCode(400)
                .body("errors.code", contains("TOO_LONG"));
        approve(underwriter, photos.inspectionId(), null, null, v).then().statusCode(200)
                .body("status", equalTo("APPROVED"))
                .body("mode", equalTo("SELF_PHOTOS"))
                .body("review.reviewedAt", startsWith("2027-01-10T08:1"));
        get(photos.token(), photos.proposalId()).then().body("checklist.inspection", equalTo("DONE"))
                .body("inspection.status", equalTo("APPROVED"));
        approve(underwriter, photos.inspectionId(), null, null, v + 1).then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));
        assertThat(auditCount(owner, "INSPECTION.APPROVE", photos.inspectionId())).isEqualTo(1);

        // presencial pedida pelo cliente com uma só fotografia
        Customer c = support.customerWithProfile();
        UUID proposalId = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        uploaded(c.token(), proposalId, Photo.jpeg("FRONT"));
        requestOnSite(c.token(), proposalId, "Matola, Av. das Indústrias, dias úteis de manhã",
                version(c.token(), proposalId)).then().statusCode(200);
        markSubmitted(owner, proposalId, "UNDER_REVIEW");
        UUID onSite = inspectionId(owner, proposalId);
        given().auth().oauth2(underwriter).queryParam("mode", "ON_SITE").queryParam("size", 100)
                .get(INSURER_INSPECTIONS).then().statusCode(200).body("content.id", hasItem(onSite.toString()));
        long w = inspectionVersion(onSite);
        approve(underwriter, onSite, null, null, w).then().statusCode(400).body("errors.code", contains("REQUIRED"));
        approve(underwriter, onSite, "SELF_PHOTOS", null, w).then().statusCode(409)
                .body("code", equalTo("INSPECTION_PHOTOS_MISSING"))
                .body("missingSlots", hasItems("REAR", "CHASSIS"));
        approve(underwriter, onSite, "ON_SITE", "Vistoria feita na Matola em 08/01.", w).then().statusCode(200)
                .body("status", equalTo("APPROVED"))
                .body("mode", equalTo("ON_SITE"))
                .body("review.notes", equalTo("Vistoria feita na Matola em 08/01."))
                .body("photos", hasSize(1));
        get(c.token(), proposalId).then().body("inspection.review.notes", equalTo("Vistoria feita na Matola em 08/01."))
                .body("checklist.inspection", equalTo("DONE"));
        assertThat(auditCount(owner, "INSPECTION.APPROVE", onSite)).isEqualTo(1);
    }

    @Test
    void decisionsNeedTheProposalUnderReview() throws Exception {
        // já submetida uma vez (submitted_at) mas de volta a DRAFT
        Owned o = submittedForReview("DRAFT");
        long v = inspectionVersion(o.inspectionId());
        approve(underwriter, o.inspectionId(), null, null, v).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_NOT_UNDER_REVIEW"));
        reject(underwriter, o.inspectionId(), "A fotografia do chassis está ilegível.", null, v).then()
                .statusCode(409).body("code", equalTo("PROPOSAL_NOT_UNDER_REVIEW"));
        assertThat(auditCount(owner, "INSPECTION.APPROVE", o.inspectionId())).isZero();
        approve(underwriter, UUID.randomUUID(), null, null, 0).then().statusCode(404)
                .body("code", equalTo("INSPECTION_NOT_FOUND"));
    }
}
