package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.ProposalTestSupport.PROPOSALS;
import static mz.co.moseguros.proposal.ProposalTestSupport.acknowledge;
import static mz.co.moseguros.proposal.ProposalTestSupport.declarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitability;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitabilityAnswers;
import static mz.co.moseguros.proposal.ProposalTestSupport.update;
import static mz.co.moseguros.proposal.ProposalTestSupport.validDeclarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.KycTestSupport;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Assinatura da proposta por OTP sobre o hash do PDF (S4-04 §7 e §8): completude, pedido, PDF
 * descarregado com o mesmo hash, código enviado, confirmação, invalidação, erros, isolamento do PDF e
 * logs sem dados sensíveis.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(OutputCaptureExtension.class)
class ProposalSignatureIT {

    static final String FILES = "/api/v1/files/{id}";
    static final String NBSP = " ";

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
    Insurer insurer;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Assinatura");
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

    // --- Pedidos ---

    static Response requestSignature(String token, UUID proposalId, Boolean acknowledged, String channel,
                                     long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("documentsAcknowledged", acknowledged);
        if (channel != null) {
            body.put("channel", channel);
        }
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/sign/request", proposalId);
    }

    static Response confirm(String token, UUID proposalId, String challengeId, String code) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("challengeId", challengeId);
        body.put("code", code);
        return given().auth().oauth2(token).contentType(ContentType.JSON).header("User-Agent", "JUnit-Assinatura")
                .header("X-Forwarded-For", "6.6.6.6").body(body)
                .post(PROPOSALS + "/{id}/sign/confirm", proposalId);
    }

    static byte[] download(String token, UUID fileId) {
        return given().auth().oauth2(token).get(FILES, fileId).then().statusCode(200).extract().asByteArray();
    }

    static String sha256(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    // --- Preparação ---

    /** Proposta MTPL_ONLY com as declarações e a adequação {@code SUITABLE} (pronta a assinar). */
    UUID completeMtpl(Customer c) throws Exception {
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        declarations(c.token(), id, validDeclarations(), true, version(c.token(), id)).then().statusCode(200);
        suitability(c.token(), id, suitabilityAnswers("MANDATORY_ONLY", "NO", "YES", "NO", "UNSURE"),
                version(c.token(), id)).then().statusCode(200).body("suitability.result", equalTo("SUITABLE"));
        return id;
    }

    private String phone(Customer c) {
        return c.account().getMsisdn().e164();
    }

    /** Avança o relógio para o próximo pedido de código do mesmo telemóvel (60 s entre pedidos). */
    private void nextCodeAllowed() {
        clock.advance(Duration.ofSeconds(61));
    }

    // --- Testes ---

    @Test
    void incompleteProposalsCannotBeSigned() throws Exception {
        Customer c = support.customerWithProfile();

        // Sem declarações.
        UUID noDeclarations = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        suitability(c.token(), noDeclarations, suitabilityAnswers("MANDATORY_ONLY", "NO", "YES", "NO", "UNSURE"),
                version(c.token(), noDeclarations)).then().statusCode(200);
        requestSignature(c.token(), noDeclarations, true, null, version(c.token(), noDeclarations))
                .then().statusCode(409)
                .body("code", equalTo("PROPOSAL_INCOMPLETE"))
                .body("missing", contains("DECLARATIONS"));

        // WARNED sem confirmação dos avisos.
        UUID warned = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        declarations(c.token(), warned, validDeclarations(), true, version(c.token(), warned)).then().statusCode(200);
        suitability(c.token(), warned, suitabilityAnswers("OWN_DAMAGE", "YES", "NO", "NO", "YES"),
                version(c.token(), warned)).then().statusCode(200).body("suitability.result", equalTo("WARNED"));
        requestSignature(c.token(), warned, true, null, version(c.token(), warned)).then().statusCode(409)
                .body("missing", contains("SUITABILITY_ACKNOWLEDGEMENT"));
        acknowledge(c.token(), warned, version(c.token(), warned)).then().statusCode(200);

        // Danos próprios sem vistoria submetida.
        UUID ownDamage = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        declarations(c.token(), ownDamage, validDeclarations(), true, version(c.token(), ownDamage))
                .then().statusCode(200);
        suitability(c.token(), ownDamage, suitabilityAnswers("OWN_DAMAGE", "NO", "YES", "NO", "YES"),
                version(c.token(), ownDamage)).then().statusCode(200);
        requestSignature(c.token(), ownDamage, true, null, version(c.token(), ownDamage)).then().statusCode(409)
                .body("missing", contains("INSPECTION"));

        // Documentos não confirmados; versão antiga; nada foi enviado.
        requestSignature(c.token(), warned, false, null, version(c.token(), warned)).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("documentsAcknowledged"))
                .body("errors[0].code", equalTo("MUST_BE_TRUE"));
        requestSignature(c.token(), warned, true, null, version(c.token(), warned) - 1).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        assertThat(channels.sentTo(phone(c))).isEmpty();
    }

    @Test
    void signsWithTheCodeAndAnyLaterChangeInvalidatesTheSignature(CapturedOutput output) throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = completeMtpl(c);
        String number = get(c.token(), id).then().statusCode(200)
                .body("signature", nullValue())
                .body("checklist.signature", equalTo("MISSING"))
                .body("product.documents", hasSize(2))
                .body("product.documents[0].type", equalTo("GENERAL_CONDITIONS"))
                .body("product.documents[1].type", equalTo("PRE_CONTRACT_INFO"))
                .extract().path("number");

        // 1. Pedido: PDF + código.
        Response request = requestSignature(c.token(), id, true, null, version(c.token(), id));
        request.then().statusCode(202)
                .header("X-Debug-Otp", nullValue())
                .body("channel", equalTo("SMS"))
                .body("destinationMasked", equalTo(c.account().getMsisdn().masked()))
                .body("resendAfterSeconds", equalTo(60))
                .body("fallbackUsed", equalTo(false))
                .body("expiresAt", notNullValue())
                .body("documentHash", org.hamcrest.Matchers.matchesPattern("^[0-9a-f]{64}$"));
        String challengeId = request.path("challengeId");
        UUID fileId = UUID.fromString(request.path("documentFileId"));
        String hash = request.path("documentHash");

        byte[] pdf = download(c.token(), fileId);
        assertThat(sha256(pdf)).isEqualTo(hash);
        assertThat(new String(pdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(phone(c));
        assertThat(sms.text()).contains(number).contains(insurer.name());
        Map<String, Object> premium = get(c.token(), id).then().extract().path("premium.total");
        long total = ((Number) premium.get("amount")).longValue();
        assertThat(sms.text()).contains(mz.co.moseguros.shared.format.MoneyFormat.format(
                mz.co.moseguros.shared.domain.Money.ofCentavos(total), "pt"));
        Map<String, Object> row = support.owner().queryForMap("""
                select sign_request_file_id, sign_request_hash, sign_request_challenge_id, sign_request_revision,
                       content_revision from proposal.proposal where id = ?""", id);
        assertThat(row.get("sign_request_file_id")).isEqualTo(fileId);
        assertThat(row.get("sign_request_hash")).isEqualTo(hash);
        assertThat(row.get("sign_request_challenge_id").toString()).isEqualTo(challengeId);
        assertThat(row.get("sign_request_revision")).isEqualTo(row.get("content_revision"));
        assertThat(support.owner().queryForObject("""
                select count(*) from audit.audit_event where entity_id = ? and action = 'PROPOSAL.SIGN_REQUEST'
                   and after_state->>'documentHash' = ?""", Integer.class, id.toString(), hash)).isEqualTo(1);
        assertThat(support.owner().queryForMap(
                "select owner_type, purpose, classification, linked_at from document.file where id = ?", fileId))
                .containsEntry("owner_type", "PROPOSAL").containsEntry("purpose", "PROPOSAL_DOCUMENT")
                .containsEntry("classification", "SENSITIVE")
                .hasEntrySatisfying("linked_at", v -> assertThat(v).isNotNull());

        // 2. Confirmação com o código.
        confirm(c.token(), id, challengeId, sms.code()).then().statusCode(200)
                .body("signature.channel", equalTo("SMS"))
                .body("signature.destinationMasked", equalTo(c.account().getMsisdn().masked()))
                .body("signature.documentFileId", equalTo(fileId.toString()))
                .body("signature.documentHash", equalTo(hash))
                .body("signature.assisted", equalTo(false))
                .body("signature.signedAt", notNullValue())
                .body("checklist.signature", equalTo("DONE"));
        Map<String, Object> signature = support.owner().queryForMap(
                "select * from proposal.signature where proposal_id = ?", id);
        assertThat(signature).containsEntry("document_hash", hash).containsEntry("document_file_id", fileId)
                .containsEntry("method", "OTP").containsEntry("otp_channel", "SMS")
                .containsEntry("signer_user_id", c.account().getId())
                .containsEntry("user_agent", "JUnit-Assinatura")
                .containsEntry("ip", "127.0.0.1")
                .containsEntry("content_revision", row.get("content_revision"));
        assertThat(support.owner().queryForMap("""
                select current_signature_id, sign_request_challenge_id from proposal.proposal where id = ?""", id))
                .containsEntry("current_signature_id", signature.get("id"))
                .containsEntry("sign_request_challenge_id", null);
        assertThat(support.owner().queryForObject("""
                select count(*) from audit.audit_event where entity_id = ? and action = 'PROPOSAL.SIGN'
                   and after_state->>'signatureId' = ? and after_state->>'documentHash' = ?""",
                Integer.class, id.toString(), signature.get("id").toString(), hash)).isEqualTo(1);

        // 3. Alterar a data de início invalida a assinatura.
        update(c.token(), id, ProposalTestSupport.START.plusDays(1).toString(), version(c.token(), id))
                .then().statusCode(200)
                .body("signature", nullValue())
                .body("checklist.signature", equalTo("MISSING"));
        assertThat(support.owner().queryForMap(
                "select invalidated_reason, invalidated_at from proposal.signature where proposal_id = ?", id))
                .containsEntry("invalidated_reason", "CONTENT_CHANGED")
                .hasEntrySatisfying("invalidated_at", v -> assertThat(v).isNotNull());

        // Logs do fluxo sem o código, o documento nem o NUIT.
        assertThat(output.getAll()).doesNotContain(sms.code())
                .doesNotContain(KycTestSupport.DOC)
                .doesNotContain(ProposalTestSupport.NUIT);
    }

    @Test
    void changingTheProposalBetweenRequestAndConfirmationIsRefused() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = completeMtpl(c);
        Response request = requestSignature(c.token(), id, true, null, version(c.token(), id));
        request.then().statusCode(202);
        String code = channels.lastTo(phone(c)).code();

        update(c.token(), id, ProposalTestSupport.START.plusDays(2).toString(), version(c.token(), id))
                .then().statusCode(200);

        confirm(c.token(), id, request.path("challengeId"), code).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_CHANGED_SINCE_SIGN_REQUEST"));

        // Pedir de novo resolve.
        nextCodeAllowed();
        Response again = requestSignature(c.token(), id, true, null, version(c.token(), id));
        again.then().statusCode(202);
        confirm(c.token(), id, again.path("challengeId"), channels.lastTo(phone(c)).code()).then().statusCode(200)
                .body("checklist.signature", equalTo("DONE"));
        // O pedido de novo não alterou a revisão.
        assertThat(support.owner().queryForObject(
                "select count(*) from proposal.signature where proposal_id = ?", Integer.class, id)).isEqualTo(1);
    }

    @Test
    void otherChallengeAndWrongCodeAreRefused() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = completeMtpl(c);

        confirm(c.token(), id, UUID.randomUUID().toString(), "123456").then().statusCode(409)
                .body("code", equalTo("SIGN_REQUEST_NOT_FOUND"));

        Response request = requestSignature(c.token(), id, true, null, version(c.token(), id));
        request.then().statusCode(202);
        String challengeId = request.path("challengeId");
        String code = channels.lastTo(phone(c)).code();

        confirm(c.token(), id, UUID.randomUUID().toString(), code).then().statusCode(409)
                .body("code", equalTo("SIGN_REQUEST_NOT_FOUND"));
        confirm(c.token(), id, challengeId, wrong(code)).then().statusCode(400)
                .body("code", equalTo("OTP_INVALID"))
                .body("attemptsRemaining", equalTo(2));
        confirm(c.token(), id, challengeId, wrong(code)).then().statusCode(400)
                .body("attemptsRemaining", equalTo(1));
        get(c.token(), id).then().body("signature", nullValue());

        // A terceira tentativa, com o código certo, ainda serve; depois o pedido fica concluído.
        confirm(c.token(), id, challengeId, code).then().statusCode(200).body("checklist.signature", equalTo("DONE"));
        confirm(c.token(), id, challengeId, code).then().statusCode(409).body("code", equalTo("SIGN_REQUEST_NOT_FOUND"));
    }

    @Test
    void customerWithoutPhoneOrVerifiedEmailCannotRequest() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = completeMtpl(c);
        support.owner().update("""
                update identity.user_account set msisdn = null, msisdn_verified = false, email = ?,
                       email_verified = false where id = ?""",
                "sem.contacto" + System.nanoTime() + "@exemplo.co.mz", c.account().getId());

        requestSignature(c.token(), id, true, null, version(c.token(), id)).then().statusCode(409)
                .body("code", equalTo("SIGNER_CONTACT_MISSING"));
        assertThat(support.owner().queryForObject("""
                select count(*) from document.file where owner_type = 'PROPOSAL' and owner_id = ?""",
                Integer.class, id)).isZero();
    }

    @Test
    void onlyTheOwnerAndTheInsurerAfterSubmissionReadThePdf() throws Exception {
        Customer a = support.customerWithProfile();
        Customer b = support.customerWithProfile();
        UUID id = completeMtpl(a);
        Response request = requestSignature(a.token(), id, true, null, version(a.token(), id));
        request.then().statusCode(202);
        UUID fileId = UUID.fromString(request.path("documentFileId"));

        download(a.token(), fileId);
        given().auth().oauth2(b.token()).get(FILES, fileId).then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
        given().auth().oauth2(insurer.underwriter()).get(FILES, fileId).then().statusCode(404);
        given().auth().oauth2(b.token()).contentType(ContentType.JSON)
                .body(Map.of("documentsAcknowledged", true, "version", version(a.token(), id)))
                .post(PROPOSALS + "/{id}/sign/request", id).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));

        // Depois da submissão (S4-05) a seguradora lê-o, com proposal:read (o gestor de produtos não tem).
        support.owner().update("update proposal.proposal set submitted_at = now() where id = ?", id);
        assertThat(sha256(given().auth().oauth2(insurer.underwriter()).get(FILES, fileId).then().statusCode(200)
                .extract().asByteArray())).isEqualTo(request.<String>path("documentHash"));
        given().auth().oauth2(insurer.manager()).get(FILES, fileId).then().statusCode(404);
        given().auth().oauth2(b.token()).get(FILES, fileId).then().statusCode(404);
    }

    @Test
    void staffWithoutPermissionCannotUseTheEndpoints() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = completeMtpl(c);
        given().auth().oauth2(insurer.manager()).contentType(ContentType.JSON)
                .body(Map.of("documentsAcknowledged", true, "version", 1))
                .post(PROPOSALS + "/{id}/sign/request", id).then().statusCode(403);
        given().auth().oauth2(insurer.manager()).contentType(ContentType.JSON)
                .body(Map.of("challengeId", UUID.randomUUID().toString(), "code", "123456"))
                .post(PROPOSALS + "/{id}/sign/confirm", id).then().statusCode(403);
        given().contentType(ContentType.JSON).body(Map.of("documentsAcknowledged", true, "version", 1))
                .post(PROPOSALS + "/{id}/sign/request", id).then().statusCode(401);
    }

    /** Um código errado com o mesmo comprimento. */
    static String wrong(String code) {
        char first = code.charAt(0) == '9' ? '0' : (char) (code.charAt(0) + 1);
        return first + code.substring(1);
    }
}
