package mz.co.moseguros.identity;

import static io.restassured.RestAssured.given;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/** Apoio aos testes do KYC (S4-01): perfil completo e envio por {@code multipart}. */
public final class KycTestSupport {

    public static final String DOC = "110100123456A";

    private KycTestSupport() {
    }

    /** Perfil completo com o documento indicado ({@code PUT /me/profile}). */
    public static void completeProfile(String token, String idDocType, String idDocNumber, long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", "Ana Maria Mussa");
        body.put("birthDate", "1990-05-14");
        body.put("idDocType", idDocType);
        body.put("idDocNumber", idDocNumber);
        body.put("nuit", "123456789");
        body.put("address", "Av. Julius Nyerere, 1234");
        body.put("province", "MAPUTO_CITY");
        body.put("version", version);
        given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/profile")
                .then().statusCode(200);
    }

    /** Ficheiro de uma parte. */
    public record Part(String filename, byte[] content, String contentType) {
    }

    /** {@code POST /me/kyc}; as partes {@code null} não são enviadas. */
    public static Response submit(String token, UUID insurerId, Part docFront, Part docBack, Part selfie) {
        RequestSpecification request = given().auth().oauth2(token);
        if (insurerId != null) {
            request = request.multiPart("insurerId", insurerId.toString());
        }
        request = add(request, "docFront", docFront);
        request = add(request, "docBack", docBack);
        request = add(request, "selfie", selfie);
        if (insurerId == null && docFront == null && docBack == null && selfie == null) {
            request = request.multiPart("nothing", "x");
        }
        return request.post("/api/v1/me/kyc");
    }

    private static RequestSpecification add(RequestSpecification request, String name, Part part) {
        return part == null ? request : request.multiPart(name, part.filename(), part.content(), part.contentType());
    }
}
