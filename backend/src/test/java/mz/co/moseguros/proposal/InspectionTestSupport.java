package mz.co.moseguros.proposal;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.jdbc.core.JdbcTemplate;

/** Apoio aos testes de integração da vistoria (S4-03): envio de fotografias e pedidos REST. */
public final class InspectionTestSupport {

    public static final String INSURER_INSPECTIONS = "/api/v1/insurer/inspections";
    public static final List<String> REQUIRED_SLOTS = List.of("FRONT", "REAR", "LEFT", "RIGHT", "ODOMETER", "CHASSIS");

    private InspectionTestSupport() {
    }

    /** JPEG de teste em {@code src/test/resources/inspection/}. */
    public static byte[] jpeg() throws IOException {
        try (InputStream in = InspectionTestSupport.class.getResourceAsStream("/inspection/vehicle.jpg")) {
            if (in == null) {
                throw new IllegalStateException("Missing /inspection/vehicle.jpg");
            }
            return in.readAllBytes();
        }
    }

    /** Partes do envio; as {@code null} não vão no pedido. */
    public record Photo(String slot, UUID clientPhotoId, String source, String takenAt, String latitude,
                        String longitude, String filename, byte[] content, String contentType) {

        public static Photo jpeg(String slot) throws IOException {
            return new Photo(slot, UUID.randomUUID(), "CAMERA", null, "-25.965312", "32.583121", "foto.jpg",
                    InspectionTestSupport.jpeg(), "image/jpeg");
        }

        public Photo withClientPhotoId(UUID id) {
            return new Photo(slot, id, source, takenAt, latitude, longitude, filename, content, contentType);
        }

        public Photo withTakenAt(String value) {
            return new Photo(slot, clientPhotoId, source, value, latitude, longitude, filename, content, contentType);
        }

        public Photo withCoordinates(String lat, String lon) {
            return new Photo(slot, clientPhotoId, source, takenAt, lat, lon, filename, content, contentType);
        }

        public Photo withFile(String name, byte[] bytes, String type) {
            return new Photo(slot, clientPhotoId, source, takenAt, latitude, longitude, name, bytes, type);
        }
    }

    public static Response upload(String token, UUID proposalId, Photo photo) {
        RequestSpecification r = given().auth().oauth2(token);
        r = part(r, "slot", photo.slot());
        r = part(r, "clientPhotoId", photo.clientPhotoId() == null ? null : photo.clientPhotoId().toString());
        r = part(r, "source", photo.source());
        r = part(r, "takenAt", photo.takenAt());
        r = part(r, "latitude", photo.latitude());
        r = part(r, "longitude", photo.longitude());
        if (photo.content() != null) {
            r = r.multiPart("file", photo.filename(), photo.content(), photo.contentType());
        }
        return r.post(ProposalTestSupport.PROPOSALS + "/{id}/inspection/photos", proposalId);
    }

    private static RequestSpecification part(RequestSpecification r, String name, String value) {
        return value == null ? r : r.multiPart(name, value);
    }

    /** Envia (201) e devolve o id da fotografia. */
    public static UUID uploaded(String token, UUID proposalId, Photo photo) {
        return UUID.fromString(upload(token, proposalId, photo).then().statusCode(201).extract().path("id"));
    }

    /** Os seis ângulos obrigatórios. */
    public static void uploadRequired(String token, UUID proposalId) throws IOException {
        for (String slot : REQUIRED_SLOTS) {
            uploaded(token, proposalId, Photo.jpeg(slot));
        }
    }

    public static Response remove(String token, UUID proposalId, UUID photoId) {
        return given().auth().oauth2(token)
                .delete(ProposalTestSupport.PROPOSALS + "/{id}/inspection/photos/{photoId}", proposalId, photoId);
    }

    public static Response submit(String token, UUID proposalId, Integer odometerKm, long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("odometerKm", odometerKm);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(ProposalTestSupport.PROPOSALS + "/{id}/inspection/submit", proposalId);
    }

    public static Response requestOnSite(String token, UUID proposalId, String notes, long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("notes", notes);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(ProposalTestSupport.PROPOSALS + "/{id}/inspection/request-on-site", proposalId);
    }

    public static Response approve(String token, UUID inspectionId, String mode, String notes, long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (mode != null) {
            body.put("mode", mode);
        }
        if (notes != null) {
            body.put("notes", notes);
        }
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(INSURER_INSPECTIONS + "/{id}/approve", inspectionId);
    }

    public static Response reject(String token, UUID inspectionId, String reason, List<String> retakeSlots,
                                  long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("reason", reason);
        if (retakeSlots != null) {
            body.put("retakeSlots", retakeSlots);
        }
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(INSURER_INSPECTIONS + "/{id}/reject", inspectionId);
    }

    /** Id da vistoria da proposta (como dono, sem RLS). */
    public static UUID inspectionId(JdbcTemplate owner, UUID proposalId) {
        return owner.queryForObject("select id from proposal.inspection where proposal_id = ?", UUID.class,
                proposalId);
    }

    /** Põe a proposta no estado indicado e marca-a como submetida (o S4-05 fá-lo pela API). */
    public static void markSubmitted(JdbcTemplate owner, UUID proposalId, String status) {
        owner.update("update proposal.proposal set status = ?, submitted_at = now(), expires_at = null where id = ?",
                status, proposalId);
    }

    public static int contentRevision(JdbcTemplate owner, UUID proposalId) {
        Integer value = owner.queryForObject("select content_revision from proposal.proposal where id = ?",
                Integer.class, proposalId);
        return value == null ? -1 : value;
    }

    public static int auditCount(JdbcTemplate owner, String action, UUID inspectionId) {
        Integer value = owner.queryForObject(
                "select count(*) from audit.audit_event where action = ? and entity_id = ?", Integer.class,
                action, inspectionId.toString());
        return value == null ? 0 : value;
    }
}
