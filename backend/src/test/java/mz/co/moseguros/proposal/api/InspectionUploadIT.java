package mz.co.moseguros.proposal.api;

import static mz.co.moseguros.proposal.InspectionTestSupport.auditCount;
import static mz.co.moseguros.proposal.InspectionTestSupport.contentRevision;
import static mz.co.moseguros.proposal.InspectionTestSupport.inspectionId;
import static mz.co.moseguros.proposal.InspectionTestSupport.remove;
import static mz.co.moseguros.proposal.InspectionTestSupport.requestOnSite;
import static mz.co.moseguros.proposal.InspectionTestSupport.submit;
import static mz.co.moseguros.proposal.InspectionTestSupport.upload;
import static mz.co.moseguros.proposal.InspectionTestSupport.uploaded;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.time.Duration;
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
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.InspectionTestSupport;
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
 * Vistoria pelo cliente (S4-03 §3 a §5): fotografias por ângulo, substituição, idempotência,
 * validações, submissão, pedido presencial, {@code content_revision} e o bloco {@code inspection}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InspectionUploadIT {

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

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        owner = support.owner();
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Vistoria");
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

    /** Cliente novo com uma proposta COMPREHENSIVE (danos próprios). */
    private Owned comprehensive() throws Exception {
        Customer c = support.customerWithProfile();
        UUID proposalId = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        return new Owned(c.token(), proposalId);
    }

    private record Owned(String token, UUID proposalId) {
    }

    private int filesOf(UUID inspection) {
        Integer n = owner.queryForObject("select count(*) from document.file where owner_type = 'INSPECTION' "
                + "and owner_id = ?", Integer.class, inspection);
        return n == null ? 0 : n;
    }

    @Test
    void proposalWithoutOwnDamageHasNoInspection() throws Exception {
        Customer c = support.customerWithProfile();
        UUID proposalId = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        get(c.token(), proposalId).then().statusCode(200)
                .body("inspection.required", equalTo(false))
                .body("inspection.id", nullValue())
                .body("checklist.inspection", equalTo("NOT_REQUIRED"));
        assertThat(owner.queryForObject("select count(*) from proposal.inspection where proposal_id = ?",
                Integer.class, proposalId)).isZero();
        upload(c.token(), proposalId, Photo.jpeg("FRONT")).then().statusCode(409)
                .body("code", equalTo("INSPECTION_NOT_REQUIRED"));
        submit(c.token(), proposalId, 1000, version(c.token(), proposalId)).then().statusCode(409)
                .body("code", equalTo("INSPECTION_NOT_REQUIRED"));
        requestOnSite(c.token(), proposalId, "Matola, dias úteis de manhã", version(c.token(), proposalId))
                .then().statusCode(409).body("code", equalTo("INSPECTION_NOT_REQUIRED"));
        remove(c.token(), proposalId, UUID.randomUUID()).then().statusCode(409)
                .body("code", equalTo("INSPECTION_NOT_REQUIRED"));
    }

    @Test
    void sixAnglesAndTwoExtrasThenSubmit() throws Exception {
        Owned o = comprehensive();
        UUID inspection = inspectionId(owner, o.proposalId());
        JsonPath created = get(o.token(), o.proposalId()).then().statusCode(200).extract().jsonPath();
        assertThat(created.getBoolean("inspection.required")).isTrue();
        assertThat(created.getString("inspection.id")).isEqualTo(inspection.toString());
        assertThat(created.getString("inspection.status")).isEqualTo("PENDING");
        assertThat(created.getString("inspection.mode")).isEqualTo("SELF_PHOTOS");
        assertThat(created.getList("inspection.missingSlots")).containsExactly("FRONT", "REAR", "LEFT", "RIGHT",
                "ODOMETER", "CHASSIS");
        assertThat(created.getList("inspection.photos")).isEmpty();
        assertThat(created.getInt("inspection.maxExtraPhotos")).isEqualTo(6);
        assertThat(created.getString("checklist.inspection")).isEqualTo("MISSING");
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(1);

        // extras primeiro e os ângulos por ordem inversa: a resposta ordena por ângulo
        Photo extra = Photo.jpeg("EXTRA").withTakenAt(ProposalTestSupport.NOW.minus(Duration.ofDays(6)).toString());
        upload(o.token(), o.proposalId(), extra).then().statusCode(201)
                .body("slot", equalTo("EXTRA"))
                .body("source", equalTo("CAMERA"))
                .body("latitude", equalTo(-25.965312f))
                .body("longitude", equalTo(32.583121f))
                .body("takenAt", equalTo(ProposalTestSupport.NOW.minus(Duration.ofDays(6)).toString()))
                .body("uploadedAt", notNullValue());
        uploaded(o.token(), o.proposalId(), Photo.jpeg("EXTRA").withCoordinates(null, null));
        List<String> reversed = List.of("CHASSIS", "ODOMETER", "RIGHT", "LEFT", "REAR", "FRONT");
        for (String slot : reversed) {
            uploaded(o.token(), o.proposalId(), Photo.jpeg(slot));
        }
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(9);

        JsonPath ready = get(o.token(), o.proposalId()).then().statusCode(200).extract().jsonPath();
        assertThat(ready.getList("inspection.missingSlots")).isEmpty();
        assertThat(ready.getList("inspection.photos.slot")).containsExactly("FRONT", "REAR", "LEFT", "RIGHT",
                "ODOMETER", "CHASSIS", "EXTRA", "EXTRA");
        assertThat(ready.getString("checklist.inspection")).isEqualTo("MISSING");
        assertThat(ready.getString("inspection.status")).isEqualTo("PENDING");

        // os ficheiros ficam ligados, do dono INSPECTION, PERSONAL e da seguradora
        List<Map<String, Object>> files = owner.queryForList("select purpose, classification, insurer_id, linked_at "
                + "from document.file where owner_type = 'INSPECTION' and owner_id = ?", inspection);
        assertThat(files).hasSize(8).allSatisfy(f -> {
            assertThat(f.get("classification")).isEqualTo("PERSONAL");
            assertThat(f.get("insurer_id")).isEqualTo(insurer.id());
            assertThat(f.get("linked_at")).isNotNull();
        });
        assertThat(files).extracting(f -> f.get("purpose")).contains("INSPECTION_FRONT", "INSPECTION_CHASSIS",
                "INSPECTION_EXTRA");

        // submissão: quilometragem obrigatória e dentro dos limites
        long v = version(o.token(), o.proposalId());
        submit(o.token(), o.proposalId(), null, v).then().statusCode(400)
                .body("errors.field", hasItem("odometerKm")).body("errors.code", hasItem("REQUIRED"));
        submit(o.token(), o.proposalId(), 2_000_001, v).then().statusCode(400)
                .body("errors.code", hasItem("OUT_OF_RANGE"));
        submit(o.token(), o.proposalId(), 84_500, v - 1).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        submit(o.token(), o.proposalId(), 84_500, v).then().statusCode(200)
                .body("inspection.status", equalTo("SUBMITTED"))
                .body("inspection.odometerKm", equalTo(84_500))
                .body("inspection.submittedAt", startsWith("2027-01-10T08:1"))
                .body("checklist.inspection", equalTo("DONE"))
                .body("status", equalTo("DRAFT"));
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(10);

        // depois de SUBMITTED já não se altera
        upload(o.token(), o.proposalId(), Photo.jpeg("EXTRA")).then().statusCode(409)
                .body("code", equalTo("INSPECTION_NOT_EDITABLE"));
        requestOnSite(o.token(), o.proposalId(), "Matola, dias úteis de manhã", version(o.token(), o.proposalId()))
                .then().statusCode(409).body("code", equalTo("INSPECTION_NOT_EDITABLE"));
        UUID anyPhoto = UUID.fromString(ready.getString("inspection.photos[0].id"));
        remove(o.token(), o.proposalId(), anyPhoto).then().statusCode(409)
                .body("code", equalTo("INSPECTION_NOT_EDITABLE"));
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(10);
        assertThat(filesOf(inspection)).isEqualTo(8);

        assertThat(auditCount(owner, "INSPECTION.PHOTO_ADD", inspection)).isEqualTo(8);
        assertThat(auditCount(owner, "INSPECTION.SUBMIT", inspection)).isEqualTo(1);
    }

    @Test
    void submitWithoutChassisListsTheMissingSlot() throws Exception {
        Owned o = comprehensive();
        for (String slot : List.of("FRONT", "REAR", "LEFT", "RIGHT", "ODOMETER")) {
            uploaded(o.token(), o.proposalId(), Photo.jpeg(slot));
        }
        get(o.token(), o.proposalId()).then().body("inspection.missingSlots", contains("CHASSIS"));
        submit(o.token(), o.proposalId(), 84_500, version(o.token(), o.proposalId())).then().statusCode(409)
                .body("code", equalTo("INSPECTION_PHOTOS_MISSING"))
                .body("missingSlots", contains("CHASSIS"));
        get(o.token(), o.proposalId()).then().body("inspection.status", equalTo("PENDING"));
    }

    @Test
    void aNewPhotoReplacesTheSameAngleAndRemovalKeepsTheRow() throws Exception {
        Owned o = comprehensive();
        UUID inspection = inspectionId(owner, o.proposalId());
        UUID first = uploaded(o.token(), o.proposalId(), Photo.jpeg("FRONT"));
        UUID second = uploaded(o.token(), o.proposalId(), Photo.jpeg("FRONT"));
        get(o.token(), o.proposalId()).then()
                .body("inspection.photos", hasSize(1))
                .body("inspection.photos[0].id", equalTo(second.toString()));
        assertThat(owner.queryForObject("select removed_at is not null from proposal.inspection_photo where id = ?",
                Boolean.class, first)).isTrue();
        int before = contentRevision(owner, o.proposalId());

        remove(o.token(), o.proposalId(), second).then().statusCode(204);
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(before + 1);
        remove(o.token(), o.proposalId(), second).then().statusCode(404).body("code", equalTo("PHOTO_NOT_FOUND"));
        remove(o.token(), o.proposalId(), UUID.randomUUID()).then().statusCode(404)
                .body("code", equalTo("PHOTO_NOT_FOUND"));
        get(o.token(), o.proposalId()).then().body("inspection.photos", empty())
                .body("inspection.missingSlots", hasSize(6));
        // nada se apaga
        assertThat(owner.queryForObject("select count(*) from proposal.inspection_photo where inspection_id = ?",
                Integer.class, inspection)).isEqualTo(2);
        assertThat(auditCount(owner, "INSPECTION.PHOTO_REMOVE", inspection)).isEqualTo(1);
    }

    @Test
    void repeatingTheSameClientPhotoIdReturnsTheSamePhoto() throws Exception {
        Owned o = comprehensive();
        UUID inspection = inspectionId(owner, o.proposalId());
        UUID clientPhotoId = UUID.randomUUID();
        Photo photo = Photo.jpeg("ODOMETER").withClientPhotoId(clientPhotoId);
        UUID id = uploaded(o.token(), o.proposalId(), photo);
        int revision = contentRevision(owner, o.proposalId());
        upload(o.token(), o.proposalId(), photo).then().statusCode(200).body("id", equalTo(id.toString()));
        // mesmo com outro ângulo: a chave é o clientPhotoId
        upload(o.token(), o.proposalId(), new Photo("CHASSIS", clientPhotoId, "CAMERA", null, null, null, "x.jpg",
                InspectionTestSupport.jpeg(), "image/jpeg")).then().statusCode(200).body("id", equalTo(id.toString()));
        assertThat(filesOf(inspection)).isEqualTo(1);
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(revision);
        assertThat(auditCount(owner, "INSPECTION.PHOTO_ADD", inspection)).isEqualTo(1);
    }

    @Test
    void invalidPartsAreFieldErrors() throws Exception {
        Owned o = comprehensive();
        UUID inspection = inspectionId(owner, o.proposalId());
        String eightDaysAgo = ProposalTestSupport.NOW.minus(Duration.ofDays(8)).toString();
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withTakenAt(eightDaysAgo)).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", contains("takenAt")).body("errors.code", contains("PHOTO_TOO_OLD"));
        String inTenMinutes = ProposalTestSupport.NOW.plus(Duration.ofMinutes(10)).toString();
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withTakenAt(inTenMinutes)).then().statusCode(400)
                .body("errors.code", contains("PHOTO_IN_FUTURE"));
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withTakenAt("ontem")).then().statusCode(400)
                .body("errors.code", contains("INVALID_DATE"));
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withCoordinates("-25.965312", null))
                .then().statusCode(400).body("errors.code", contains("INVALID_COORDINATES"));
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withCoordinates("-95", "32.5"))
                .then().statusCode(400).body("errors.field", contains("latitude"))
                .body("errors.code", contains("INVALID_COORDINATES"));
        upload(o.token(), o.proposalId(), Photo.jpeg("ROOF")).then().statusCode(400)
                .body("errors.field", contains("slot")).body("errors.code", contains("INVALID_SLOT"));
        upload(o.token(), o.proposalId(), Photo.jpeg(null)).then().statusCode(400)
                .body("errors.field", contains("slot")).body("errors.code", contains("REQUIRED"));
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withFile(null, null, null)).then().statusCode(400)
                .body("errors.field", contains("file")).body("errors.code", contains("REQUIRED"));
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withFile("livrete.pdf", TestFiles.bytes("sample.pdf"),
                "application/pdf")).then().statusCode(415).body("code", equalTo("FILE_TYPE_NOT_ALLOWED"));
        upload(o.token(), o.proposalId(), Photo.jpeg("FRONT").withFile("vazio.jpg", new byte[0], "image/jpeg"))
                .then().statusCode(400).body("code", equalTo("FILE_EMPTY"));

        // limite de extras: a sétima é recusada
        for (int i = 0; i < 6; i++) {
            uploaded(o.token(), o.proposalId(), Photo.jpeg("EXTRA"));
        }
        upload(o.token(), o.proposalId(), Photo.jpeg("EXTRA")).then().statusCode(400)
                .body("errors.field", contains("slot")).body("errors.code", contains("EXTRA_PHOTOS_LIMIT"));
        assertThat(filesOf(inspection)).isEqualTo(6);
        // um ângulo fixo continua a ser aceite
        uploaded(o.token(), o.proposalId(), Photo.jpeg("FRONT"));
    }

    @Test
    void onSiteRequestNeedsNotes() throws Exception {
        Owned o = comprehensive();
        uploaded(o.token(), o.proposalId(), Photo.jpeg("FRONT"));
        long v = version(o.token(), o.proposalId());
        requestOnSite(o.token(), o.proposalId(), null, v).then().statusCode(400)
                .body("errors.field", contains("notes")).body("errors.code", contains("REQUIRED"));
        requestOnSite(o.token(), o.proposalId(), "Matola", v).then().statusCode(400)
                .body("errors.code", contains("TOO_SHORT"));
        requestOnSite(o.token(), o.proposalId(), "x".repeat(501), v).then().statusCode(400)
                .body("errors.code", contains("TOO_LONG"));
        int revision = contentRevision(owner, o.proposalId());
        requestOnSite(o.token(), o.proposalId(), "Matola, Av. das Indústrias, dias úteis de manhã", v)
                .then().statusCode(200)
                .body("inspection.status", equalTo("SUBMITTED"))
                .body("inspection.mode", equalTo("ON_SITE"))
                .body("inspection.customerNotes", equalTo("Matola, Av. das Indústrias, dias úteis de manhã"))
                .body("inspection.missingSlots", empty())
                .body("inspection.photos", hasSize(1))
                .body("checklist.inspection", equalTo("DONE"));
        assertThat(contentRevision(owner, o.proposalId())).isEqualTo(revision + 1);
        assertThat(auditCount(owner, "INSPECTION.REQUEST_ON_SITE", inspectionId(owner, o.proposalId())))
                .isEqualTo(1);
    }

    @Test
    void aProposalThatIsNoLongerEditableBlocksTheInspection() throws Exception {
        Owned o = comprehensive();
        uploaded(o.token(), o.proposalId(), Photo.jpeg("FRONT"));
        support.setStatus(o.proposalId(), "UNDER_REVIEW");
        upload(o.token(), o.proposalId(), Photo.jpeg("REAR")).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_NOT_EDITABLE"));
        submit(o.token(), o.proposalId(), 1000, version(o.token(), o.proposalId())).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_NOT_EDITABLE"));
    }
}
