package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.identity.KycTestSupport.DOC;
import static mz.co.moseguros.identity.KycTestSupport.completeProfile;
import static mz.co.moseguros.identity.KycTestSupport.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.identity.KycStatus;
import mz.co.moseguros.identity.KycTestSupport.Part;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels.Sent;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Fluxo do KYC (S4-01 §3 a §9): envio, validação, revisão pelo subscritor, estado
 * {@code OUTDATED}, recusa com e-mail, concorrência, auditoria e cifra na BD.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class KycFlowIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    FakeOtpDeliveryChannels channels;

    JdbcTemplate owner;
    Part jpeg;
    Part png;
    Part heic;
    Part pdf;

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.port = port;
        owner = fixtures.owner();
        channels.reset();
        jpeg = new Part("frente.jpg", TestFiles.bytes("sample.jpg"), "image/jpeg");
        png = new Part("verso.png", TestFiles.bytes("sample.png"), "image/png");
        heic = new Part("selfie.heic", TestFiles.bytes("sample.heic"), "image/heic");
        pdf = new Part("selfie.pdf", TestFiles.bytes("sample.pdf"), "application/pdf");
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private Organization insurer(String name) {
        return fixtures.organization(OrganizationType.INSURER, name);
    }

    private long kycRows(UUID userId) {
        return owner.queryForObject("select count(*) from identity.kyc_check where user_id = ?", Long.class, userId);
    }

    private static Response post(String token, String path, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(path);
    }

    @Test
    void incompleteProfileIsRefusedBeforeAnythingElse() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        Organization insurer = insurer("Seguros KYC Incompleto");

        submit(token(customer), insurer.getId(), jpeg, png, heic).then().statusCode(409)
                .body("code", equalTo("PROFILE_INCOMPLETE"));
        submit(token(customer), null, null, null, null).then().statusCode(409)
                .body("code", equalTo("PROFILE_INCOMPLETE"));
        assertThat(kycRows(customer.getId())).isZero();
    }

    @Test
    void partsAreValidatedAndAWrongFileTypeSavesNothing() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = token(customer);
        completeProfile(token, "BI", DOC, 0);
        Organization insurer = insurer("Seguros KYC Validacao");

        submit(token, insurer.getId(), jpeg, null, heic).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors", hasSize(1))
                .body("errors[0].field", equalTo("docBack"))
                .body("errors[0].code", equalTo("REQUIRED"));

        submit(token, null, null, null, null).then().statusCode(400)
                .body("errors.field", equalTo(List.of("insurerId", "docFront", "docBack", "selfie")))
                .body("errors.code", equalTo(List.of("REQUIRED", "REQUIRED", "REQUIRED", "REQUIRED")));

        Organization broker = fixtures.organization(OrganizationType.BROKER, "Corretora KYC");
        submit(token, broker.getId(), jpeg, png, heic).then().statusCode(400)
                .body("errors[0].field", equalTo("insurerId"))
                .body("errors[0].code", equalTo("INVALID_INSURER"));
        submit(token, UUID.randomUUID(), jpeg, png, heic).then().statusCode(400)
                .body("errors[0].code", equalTo("INVALID_INSURER"));
        Organization suspended = insurer("Seguros KYC Suspensa");
        fixtures.suspendOrganization(suspended.getId());
        submit(token, suspended.getId(), jpeg, png, heic).then().statusCode(400)
                .body("errors[0].code", equalTo("INVALID_INSURER"));

        submit(token, insurer.getId(), jpeg, png, pdf).then().statusCode(415)
                .body("code", equalTo("FILE_TYPE_NOT_ALLOWED"));
        submit(token, insurer.getId(), new Part("vazio.jpg", new byte[0], "image/jpeg"), png, heic)
                .then().statusCode(400).body("code", equalTo("FILE_EMPTY"));
        assertThat(kycRows(customer.getId())).isZero();

        UserAccount traveller = fixtures.customer("Bruno Viajante");
        String travellerToken = token(traveller);
        completeProfile(travellerToken, "PASSPORT", "AB1234567", 0);
        submit(travellerToken, insurer.getId(), jpeg, png, heic).then().statusCode(400)
                .body("errors[0].field", equalTo("docBack"))
                .body("errors[0].code", equalTo("NOT_ALLOWED"));
        submit(travellerToken, insurer.getId(), jpeg, null, heic).then().statusCode(201)
                .body("idDocType", equalTo("PASSPORT"))
                .body("status", equalTo("PENDING"));
    }

    @Test
    void submitReviewOutdateAndReject() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = token(customer);
        completeProfile(token, "BI", DOC, 0);
        String email = "ana.kyc." + customer.getId().toString().substring(0, 8) + "@exemplo.co.mz";
        owner.update("update identity.user_account set email = ?, email_verified = true where id = ?", email,
                customer.getId());
        Organization insurer = insurer("Seguros KYC Fluxo");
        String underwriter = token(fixtures.staff("Subscritor KYC", insurer.getId(), "UNDERWRITER"));

        given().auth().oauth2(token).get("/api/v1/me/kyc").then().statusCode(200)
                .body("state", equalTo("NONE")).body("$", not(hasKey("check")));
        assertThat(identityApi.kycStatus(customer.getId()).state()).isEqualTo(KycState.NONE);

        // Envio válido com BI (três ficheiros).
        Response created = submit(token, insurer.getId(), jpeg, png, heic);
        created.then().statusCode(201)
                .body("status", equalTo("PENDING"))
                .body("insurerId", equalTo(insurer.getId().toString()))
                .body("idDocType", equalTo("BI"))
                .body("idDocNumberMasked", equalTo("*********456A"))
                .body("$", not(hasKey("docFrontFileId")));
        UUID first = UUID.fromString(created.path("id"));
        given().auth().oauth2(token).get("/api/v1/me/kyc").then().statusCode(200)
                .body("state", equalTo("PENDING"))
                .body("check.id", equalTo(first.toString()))
                .body("check", not(hasKey("files")));

        submit(token, insurer.getId(), jpeg, png, heic).then().statusCode(409)
                .body("code", equalTo("KYC_ALREADY_PENDING"));

        // Na BD: cópia do perfil e número cifrado.
        Map<String, Object> row = owner.queryForMap("select full_name, id_doc_type, id_doc_number_enc, "
                + "profile_fingerprint from identity.kyc_check where id = ?", first);
        assertThat(row.get("full_name")).isEqualTo("Ana Maria Mussa");
        assertThat(new String((byte[]) row.get("id_doc_number_enc"), StandardCharsets.ISO_8859_1)).doesNotContain(DOC);
        assertThat((String) row.get("profile_fingerprint")).matches("^[0-9a-f]{64}$");

        // O subscritor lista, abre e verifica.
        given().auth().oauth2(underwriter).get("/api/v1/insurer/kyc").then().statusCode(200)
                .body("content.id", hasItem(first.toString()));
        given().auth().oauth2(underwriter).queryParam("q", "MARIA mus").get("/api/v1/insurer/kyc").then().statusCode(200)
                .body("content.id", hasItem(first.toString()));
        given().auth().oauth2(underwriter).queryParam("status", "VERIFIED").get("/api/v1/insurer/kyc").then().statusCode(200)
                .body("content.id", not(hasItem(first.toString())));
        Response opened = given().auth().oauth2(underwriter).get("/api/v1/insurer/kyc/{id}", first);
        opened.then().statusCode(200)
                .body("status", equalTo("PENDING"))
                .body("customer.userId", equalTo(customer.getId().toString()))
                .body("customer.fullName", equalTo("Ana Maria Mussa"))
                .body("customer.birthDate", equalTo("1990-05-14"))
                .body("customer.idDocType", equalTo("BI"))
                .body("customer.idDocNumber", equalTo(DOC))
                .body("customer.msisdnMasked", equalTo(customer.getMsisdn().masked()))
                .body("files.docFrontFileId", notNullValue())
                .body("files.docBackFileId", notNullValue())
                .body("files.selfieFileId", notNullValue())
                .body("previousChecks", hasSize(0))
                .body("version", equalTo(0));
        for (String file : List.of("files.docFrontFileId", "files.docBackFileId", "files.selfieFileId")) {
            given().auth().oauth2(underwriter).get("/api/v1/files/{id}", (String) opened.path(file))
                    .then().statusCode(200);
        }

        post(underwriter, "/api/v1/insurer/kyc/" + first + "/verify", Map.of("version", 3)).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        post(underwriter, "/api/v1/insurer/kyc/" + first + "/verify", Map.of("version", 0)).then().statusCode(200)
                .body("status", equalTo("VERIFIED"))
                .body("reviewedAt", notNullValue())
                .body("version", equalTo(1));
        post(underwriter, "/api/v1/insurer/kyc/" + first + "/verify", Map.of("version", 1)).then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));
        post(underwriter, "/api/v1/insurer/kyc/" + first + "/reject",
                Map.of("reasonCode", "DATA_MISMATCH", "version", 1)).then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));

        given().auth().oauth2(token).get("/api/v1/me/kyc").then().statusCode(200)
                .body("state", equalTo("VERIFIED"))
                .body("check.status", equalTo("VERIFIED"));
        KycStatus status = identityApi.kycStatus(customer.getId());
        assertThat(status.state()).isEqualTo(KycState.VERIFIED);
        assertThat(status.checkId()).isEqualTo(first);
        assertThat(status.verifiedByInsurerId()).isEqualTo(insurer.getId());
        assertThat(status.verifiedAt()).isNotNull();
        Sent verifiedMail = channels.lastTo(email);
        assertThat(verifiedMail.params().get("subject")).isEqualTo("A sua identidade foi verificada");
        assertThat(verifiedMail.text()).contains("Olá Ana,", "foi concluída", "Equipa MoSeguros");
        assertThat(owner.queryForObject("select count(*) from notification.outbound_message where purpose = 'KYC' "
                + "and correlation_id = ?", Long.class, first)).isEqualTo(1L);

        submit(token, insurer.getId(), jpeg, png, heic).then().statusCode(409)
                .body("code", equalTo("KYC_ALREADY_VERIFIED"));

        // O cliente muda o número do documento: OUTDATED e pode enviar de novo.
        completeProfile(token, "BI", "110100999999B", 1);
        assertThat(identityApi.kycStatus(customer.getId()).state()).isEqualTo(KycState.OUTDATED);
        assertThat(identityApi.kycStatus(customer.getId()).verifiedByInsurerId()).isEqualTo(insurer.getId());
        given().auth().oauth2(token).get("/api/v1/me/kyc").then().statusCode(200)
                .body("state", equalTo("OUTDATED"));
        UUID second = UUID.fromString(submit(token, insurer.getId(), jpeg, png, heic).then().statusCode(201)
                .body("idDocNumberMasked", equalTo("*********999B"))
                .extract().path("id"));

        // Recusa: OTHER sem motivo → 400; com motivo → REJECTED e e-mail com o motivo.
        post(underwriter, "/api/v1/insurer/kyc/" + second + "/reject", Map.of("reasonCode", "OTHER", "version", 0))
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("reason"))
                .body("errors[0].code", equalTo("REQUIRED"));
        post(underwriter, "/api/v1/insurer/kyc/" + second + "/reject",
                Map.of("reasonCode", "OTHER", "reason", "curto", "version", 0)).then().statusCode(400)
                .body("errors[0].code", equalTo("TOO_SHORT"));
        post(underwriter, "/api/v1/insurer/kyc/" + second + "/reject",
                Map.of("reasonCode", "OTHER", "reason", "x".repeat(501), "version", 0)).then().statusCode(400)
                .body("errors[0].code", equalTo("TOO_LONG"));
        post(underwriter, "/api/v1/insurer/kyc/" + second + "/reject", Map.of("version", 0)).then().statusCode(400)
                .body("errors[0].field", equalTo("reasonCode"))
                .body("errors[0].code", equalTo("REQUIRED"));
        String reason = "A fotografia da frente está desfocada.";
        post(underwriter, "/api/v1/insurer/kyc/" + second + "/reject",
                Map.of("reasonCode", "OTHER", "reason", reason, "version", 0)).then().statusCode(200)
                .body("status", equalTo("REJECTED"))
                .body("rejectionReasonCode", equalTo("OTHER"))
                .body("rejectionReason", equalTo(reason))
                .body("previousChecks", hasSize(1))
                .body("previousChecks[0].id", equalTo(first.toString()))
                .body("previousChecks[0].status", equalTo("VERIFIED"));
        Sent rejectedMail = channels.lastTo(email);
        assertThat(rejectedMail.params().get("subject")).isEqualTo("Não foi possível verificar a sua identidade");
        assertThat(rejectedMail.text()).contains("Olá Ana,", "não conseguimos verificar a sua identidade: " + reason,
                "/cliente/identidade");

        given().auth().oauth2(token).get("/api/v1/me/kyc").then().statusCode(200)
                .body("state", equalTo("REJECTED"))
                .body("check.id", equalTo(second.toString()))
                .body("check.rejectionReasonCode", equalTo("OTHER"))
                .body("check.rejectionReason", equalTo(reason));
        assertThat(identityApi.kycStatus(customer.getId()).state()).isEqualTo(KycState.REJECTED);
        assertThat(identityApi.kycStatus(customer.getId()).verifiedAt()).isNull();

        // Auditoria sem o número do documento em claro.
        List<Map<String, Object>> events = owner.queryForList("""
                select action, coalesce(before_state::text, '') as b, coalesce(after_state::text, '') as a
                  from audit.audit_event where entity_type = 'KycCheck' and entity_id in (?, ?)""",
                first.toString(), second.toString());
        assertThat(events).extracting(e -> e.get("action"))
                .contains("KYC.SUBMIT", "KYC.VIEW", "KYC.VERIFY", "KYC.REJECT");
        assertThat(events).allSatisfy(e -> assertThat(e.get("b") + " " + e.get("a"))
                .doesNotContain(DOC, "110100999999B"));
        assertThat(owner.queryForObject("""
                select after_state->>'idDocNumberMasked' from audit.audit_event
                 where action = 'KYC.SUBMIT' and entity_id = ?""", String.class, first.toString()))
                .isEqualTo("*********456A");
        assertThat(owner.queryForObject("""
                select after_state->>'id' from audit.audit_event
                 where action = 'KYC.VIEW' and entity_id = ? and before_state is null""", String.class,
                first.toString())).isEqualTo(first.toString());
        assertThat(owner.queryForObject("""
                select after_state->>'reasonCode' from audit.audit_event
                 where action = 'KYC.REJECT' and entity_id = ?""", String.class, second.toString()))
                .isEqualTo("OTHER");
    }

    @Test
    void rejectionEmailUsesTheCustomerLanguageAndTheReasonText() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = token(customer);
        completeProfile(token, "BI", DOC, 0);
        String email = "ana.en." + customer.getId().toString().substring(0, 8) + "@exemplo.co.mz";
        owner.update("update identity.user_account set email = ?, email_verified = true, preferred_language = 'en' "
                + "where id = ?", email, customer.getId());
        Organization insurer = insurer("Seguros KYC Ingles");
        String underwriter = token(fixtures.staff("Subscritor EN", insurer.getId(), "INSURER_ADMIN"));
        UUID check = UUID.fromString(submit(token, insurer.getId(), jpeg, png, heic).then().statusCode(201)
                .extract().path("id"));

        post(underwriter, "/api/v1/insurer/kyc/" + check + "/reject",
                Map.of("reasonCode", "DOCUMENT_EXPIRED", "version", 0)).then().statusCode(200)
                .body("status", equalTo("REJECTED"))
                .body("$", not(hasKey("rejectionReason")));

        Sent mail = channels.lastTo(email);
        assertThat(mail.params().get("subject")).isEqualTo("We could not verify your identity");
        assertThat(mail.text()).contains("Hello Ana,", "we could not verify your identity: the document has expired.");

        // Sem e-mail verificado não se envia nada.
        UserAccount other = fixtures.customer("Bruno Mussa");
        String otherToken = token(other);
        completeProfile(otherToken, "BI", DOC, 0);
        UUID otherCheck = UUID.fromString(submit(otherToken, insurer.getId(), jpeg, png, heic).then().statusCode(201)
                .extract().path("id"));
        post(underwriter, "/api/v1/insurer/kyc/" + otherCheck + "/verify", Map.of("version", 0)).then().statusCode(200);
        assertThat(owner.queryForObject("select count(*) from notification.outbound_message where correlation_id = ?",
                Long.class, otherCheck)).isZero();
    }

    @Test
    void customerViewReturnsTheProfileOnlyForCustomers() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        completeProfile(token(customer), "BI", DOC, 0);

        CustomerView view = identityApi.customer(customer.getId()).orElseThrow();
        assertThat(view.fullName()).isEqualTo("Ana Maria Mussa");
        assertThat(view.idDocType()).isEqualTo("BI");
        assertThat(view.idDocNumber()).isEqualTo(DOC);
        assertThat(view.nuit()).isEqualTo("123456789");
        assertThat(view.province()).isEqualTo("MAPUTO_CITY");
        assertThat(view.msisdn()).isEqualTo(customer.getMsisdn());
        assertThat(view.profileComplete()).isTrue();
        assertThat(view.accountActive()).isTrue();
        assertThat(view.toString()).doesNotContain(DOC, "123456789");

        fixtures.setAccountStatus(customer.getId(), "DISABLED");
        assertThat(identityApi.customer(customer.getId()).orElseThrow().accountActive()).isFalse();

        Organization insurer = insurer("Seguros KYC Vista");
        UserAccount staff = fixtures.staff("Subscritor", insurer.getId(), "UNDERWRITER");
        assertThat(identityApi.customer(staff.getId())).isEmpty();
        assertThat(identityApi.customer(UUID.randomUUID())).isEmpty();
    }
}
