package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.InspectionTestSupport.INSURER_INSPECTIONS;
import static mz.co.moseguros.proposal.InspectionTestSupport.approve;
import static mz.co.moseguros.proposal.InspectionTestSupport.inspectionId;
import static mz.co.moseguros.proposal.InspectionTestSupport.markSubmitted;
import static mz.co.moseguros.proposal.InspectionTestSupport.reject;
import static mz.co.moseguros.proposal.InspectionTestSupport.remove;
import static mz.co.moseguros.proposal.InspectionTestSupport.submit;
import static mz.co.moseguros.proposal.InspectionTestSupport.upload;
import static mz.co.moseguros.proposal.InspectionTestSupport.uploadRequired;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.proposal.InspectionTestSupport.Photo;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionPhoto;
import mz.co.moseguros.proposal.domain.InspectionPhotoRepository;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
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
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Isolamento da vistoria (S4-03 §1, §6 e §8): entre seguradoras (RLS, serviço e
 * {@code InspectionFileAccessPolicy}), entre clientes, antes da submissão da proposta, e permissões.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InspectionIsolationIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    InspectionRepository inspectionRepository;

    @Autowired
    InspectionPhotoRepository photoRepository;

    @Autowired
    TransactionTemplate tx;

    ProposalTestSupport support;
    JdbcTemplate owner;
    Insurer insurerA;
    Organization insurerB;

    @BeforeAll
    void createInsurers() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        owner = support.owner();
        support.atNow();
        insurerA = support.insurerWithProducts("Seguradora Vistoria A");
        insurerB = fixtures.organization(OrganizationType.INSURER, "Seguradora Vistoria B");
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
        TenantContext.clear();
    }

    private String staff(UUID org, String role) {
        return support.token(fixtures.staff("Staff " + role, org, role));
    }

    private List<String> fileIds(String token, UUID proposalId) {
        return get(token, proposalId).then().statusCode(200).extract().jsonPath()
                .getList("inspection.photos.fileId", String.class);
    }

    @Test
    void otherInsurersOtherCustomersAndUnsubmittedProposalsSeeNothing() throws Exception {
        String underwriterA = staff(insurerA.id(), "UNDERWRITER");
        String adminA = staff(insurerA.id(), "INSURER_ADMIN");
        String financeA = staff(insurerA.id(), "FINANCE");
        String underwriterB = staff(insurerB.getId(), "UNDERWRITER");

        Customer a = support.customerWithProfile();
        UUID proposal = ProposalTestSupport.created(a.token(), support.comprehensive(a, insurerA).quoteId());
        UUID inspection = inspectionId(owner, proposal);
        uploadRequired(a.token(), proposal);
        submit(a.token(), proposal, 84_500, version(a.token(), proposal)).then().statusCode(200);
        List<String> files = fileIds(a.token(), proposal);
        assertThat(files).hasSize(6);

        // Antes da submissão da proposta: nem a própria seguradora vê a vistoria nem as fotografias.
        given().auth().oauth2(underwriterA).queryParam("size", 100).get(INSURER_INSPECTIONS).then().statusCode(200)
                .body("content.id", not(hasItem(inspection.toString())));
        given().auth().oauth2(underwriterA).get(INSURER_INSPECTIONS + "/{id}", inspection).then().statusCode(404)
                .body("code", equalTo("INSPECTION_NOT_FOUND"));
        for (String file : files) {
            given().auth().oauth2(underwriterA).get("/api/v1/files/{id}", file).then().statusCode(404)
                    .body("code", equalTo("FILE_NOT_FOUND"));
        }
        // o cliente descarrega as suas
        for (String file : files) {
            given().auth().oauth2(a.token()).get("/api/v1/files/{id}", file).then().statusCode(200);
        }

        markSubmitted(owner, proposal, "UNDER_REVIEW");
        long v = given().auth().oauth2(underwriterA).get(INSURER_INSPECTIONS + "/{id}", inspection).then()
                .statusCode(200).extract().jsonPath().getLong("version");

        // Seguradora B: não lista, não abre, não decide e não descarrega.
        given().auth().oauth2(underwriterB).queryParam("size", 100).get(INSURER_INSPECTIONS).then().statusCode(200)
                .body("content.id", not(hasItem(inspection.toString())));
        given().auth().oauth2(underwriterB).get(INSURER_INSPECTIONS + "/{id}", inspection).then().statusCode(404)
                .body("code", equalTo("INSPECTION_NOT_FOUND"));
        approve(underwriterB, inspection, null, null, v).then().statusCode(404)
                .body("code", equalTo("INSPECTION_NOT_FOUND"));
        reject(underwriterB, inspection, "A fotografia do chassis está ilegível.", null, v).then().statusCode(404)
                .body("code", equalTo("INSPECTION_NOT_FOUND"));
        for (String file : files) {
            given().auth().oauth2(underwriterB).get("/api/v1/files/{id}", file).then().statusCode(404)
                    .body("code", equalTo("FILE_NOT_FOUND"));
        }

        // Seguradora A depois da submissão: o subscritor e o administrador descarregam; o financeiro não.
        for (String file : files) {
            given().auth().oauth2(underwriterA).get("/api/v1/files/{id}", file).then().statusCode(200);
        }
        given().auth().oauth2(adminA).get("/api/v1/files/{id}", files.get(0)).then().statusCode(200);
        given().auth().oauth2(financeA).get("/api/v1/files/{id}", files.get(0)).then().statusCode(404);

        // Outro cliente: não vê a proposta, não envia, não descarrega.
        Customer b = support.customerWithProfile();
        upload(b.token(), proposal, Photo.jpeg("FRONT")).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        remove(b.token(), proposal, UUID.randomUUID()).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        submit(b.token(), proposal, 1, 0).then().statusCode(404);
        for (String file : files) {
            given().auth().oauth2(b.token()).get("/api/v1/files/{id}", file).then().statusCode(404)
                    .body("code", equalTo("FILE_NOT_FOUND"));
        }
        given().get("/api/v1/files/{id}", files.get(0)).then().statusCode(404);

        // RLS: as linhas só existem onde a proposta é visível.
        TenantContext.set(TenantSnapshot.customer(b.id()));
        Optional<Inspection> asB = tx.execute(s -> inspectionRepository.findById(inspection));
        assertThat(asB).isEmpty();
        List<InspectionPhoto> photosAsB = tx.execute(s -> photoRepository.findActive(inspection));
        assertThat(photosAsB).isEmpty();
        TenantContext.set(TenantSnapshot.org(null, insurerB.getId(), insurerB.getId()));
        Optional<Inspection> asOtherInsurer = tx.execute(s -> inspectionRepository.findById(inspection));
        assertThat(asOtherInsurer).isEmpty();
        TenantContext.set(TenantSnapshot.customer(a.id()));
        List<InspectionPhoto> photosAsA = tx.execute(s -> photoRepository.findActive(inspection));
        assertThat(photosAsA).hasSize(6).allSatisfy(p -> assertThat(p.getInsurerId()).isEqualTo(insurerA.id()));
    }

    @Test
    void permissionsAreEnforced() {
        String finance = staff(insurerA.id(), "FINANCE");
        String underwriter = staff(insurerA.id(), "UNDERWRITER");
        Customer customer = support.customerWithProfile();
        UUID id = UUID.randomUUID();

        for (String token : List.of(finance, customer.token())) {
            given().auth().oauth2(token).get(INSURER_INSPECTIONS).then().statusCode(403);
            given().auth().oauth2(token).get(INSURER_INSPECTIONS + "/{id}", id).then().statusCode(403);
            approve(token, id, null, null, 0).then().statusCode(403);
            reject(token, id, "A fotografia do chassis está ilegível.", null, 0).then().statusCode(403);
        }
        // staff da seguradora não usa os endpoints do cliente
        upload(underwriter, id, new Photo("FRONT", null, null, null, null, null, "f.jpg", new byte[] {1},
                "image/jpeg")).then().statusCode(403);
        submit(underwriter, id, 1, 0).then().statusCode(403);
        remove(underwriter, id, UUID.randomUUID()).then().statusCode(403);
        // sem token
        given().get(INSURER_INSPECTIONS).then().statusCode(401);
        given().contentType(ContentType.JSON).body("{\"version\":0}")
                .post(INSURER_INSPECTIONS + "/{id}/approve", id).then().statusCode(401);
        given().multiPart("slot", "FRONT").post(ProposalTestSupport.PROPOSALS + "/{id}/inspection/photos", id)
                .then().statusCode(401);
    }
}
