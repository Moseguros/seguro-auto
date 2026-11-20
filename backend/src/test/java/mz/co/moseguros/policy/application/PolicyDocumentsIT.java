package mz.co.moseguros.policy.application;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentFlowSupport.digits;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.format.MoneyFormat;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Documentos da apólice (S6-02 §1 a §7): gerados logo depois do pagamento (referência → {@code ProposalPaid}
 * → emissão → {@code PolicyIssued}), com o texto e o QR certos, em pt e en, com e sem logótipo; idempotência;
 * falha do PDF e nova tentativa pelo job; ficheiros com dono {@code POLICY}; auditoria; aviso
 * {@code POLICY_ISSUED}; lista {@code documents} do detalhe.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyDocumentsIT {

    /** Início pedido: 10/02/2027 10:31 em Maputo. */
    static final Instant START = Instant.parse("2027-02-10T08:31:00Z");
    static final String URL_PREFIX = "https://verificar.moseguros.test/v/";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    IntentStateService states;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PolicyDocumentService documents;

    @Autowired
    PolicyDocumentsJob job;

    @Autowired
    DocumentApi documentApi;

    @Autowired
    MeterRegistry meters;

    @Autowired
    ObjectMapper objectMapper;

    PolicyTestSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.setUp("Seguradora Documentos");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    // --- Geração depois do pagamento ---

    @Test
    void theCertificateAndTheScheduleAreReadyRightAfterThePayment() throws Exception {
        Customer c = support.payments().customer();
        Instant paidAt = Instant.now();
        Issued issued = pay(c, insurer, null);
        UUID[] files = support.awaitDocuments(issued.policyId());
        assertThat(Duration.between(paidAt, Instant.now())).isLessThan(Duration.ofSeconds(10));

        Map<String, Object> policy = support.policyRow(issued.policyId());
        Map<String, Object> certificate = certificateRow(issued.policyId());
        String verificationUrl = URL_PREFIX + certificate.get("verification_token");

        // certificado: números, matrícula, URL, validade e título em pt; QR com o URL
        byte[] certificatePdf = download(c.token(), files[0]);
        String text = text(certificatePdf);
        assertThat(text).contains((String) certificate.get("number"), (String) policy.get("number"),
                (String) policy.get("plate"), verificationUrl, "10/02/2027 10:31", "Certificado de seguro automóvel",
                insurer.name(), "TEXTO PROVISÓRIO");
        assertThat(qr(certificatePdf)).isEqualTo(verificationUrl);
        try (PDDocument doc = Loader.loadPDF(certificatePdf)) {
            assertThat(doc.getNumberOfPages()).isOne();
        }

        // condições particulares: montante em pt
        String schedule = normalize(text(download(c.token(), files[1])));
        assertThat(schedule).contains("Condições particulares", (String) policy.get("number"),
                normalize(MoneyFormat.format(Money.ofCentavos(299_900), "pt")), "2 999,00 MT", "10/02/2027 10:31",
                "Pagamento único", "Responsabilidade civil obrigatória");

        // ficheiros com dono POLICY, ligados, PERSONAL
        assertThat(fileRow(files[0])).containsEntry("owner_type", "POLICY").containsEntry("owner_id", issued.policyId())
                .containsEntry("classification", "PERSONAL").containsEntry("purpose", "POLICY_CERTIFICATE")
                .containsEntry("insurer_id", insurer.id())
                .containsEntry("original_filename", certificate.get("number") + ".pdf");
        assertThat(fileRow(files[0]).get("linked_at")).isNotNull();
        assertThat(fileRow(files[1])).containsEntry("owner_type", "POLICY").containsEntry("owner_id", issued.policyId())
                .containsEntry("classification", "PERSONAL").containsEntry("purpose", "POLICY_SCHEDULE")
                .containsEntry("original_filename", policy.get("number") + "-condicoes-particulares.pdf");
        assertThat(fileRow(files[1]).get("linked_at")).isNotNull();
        FileRef ref = inSystem(tx, () -> documentApi.metadata(files[0]));
        assertThat(ref.linked()).isTrue();

        // linha temporal, auditoria e aviso
        assertThat(documentsReadyEvents(issued.policyId())).isOne();
        Map<String, Object> event = support.owner().queryForMap("""
                select actor_type, actor_id, details::text as details from policy.policy_event
                 where policy_id = ? and type = 'DOCUMENTS_READY'""", issued.policyId());
        assertThat(event).containsEntry("actor_type", "SYSTEM").containsEntry("actor_id", "policy-documents");
        JsonNode details = objectMapper.readTree((String) event.get("details"));
        assertThat(details.path("certificateFileId").asText()).isEqualTo(files[0].toString());
        assertThat(details.path("scheduleFileId").asText()).isEqualTo(files[1].toString());

        JsonNode audit = objectMapper.readTree(support.owner().queryForObject("""
                select after_state::text from audit.audit_event
                 where action = 'POLICY.DOCUMENTS_GENERATE' and entity_id = ?""", String.class,
                issued.policyId().toString()));
        assertThat(audit.path("certificateFileId").asText()).isEqualTo(files[0].toString());
        assertThat(audit.path("certificateSha256").asText()).isEqualTo(fileRow(files[0]).get("sha256"));
        assertThat(audit.path("scheduleSha256").asText()).isEqualTo(fileRow(files[1]).get("sha256"));
        assertThat(support.owner().queryForObject("""
                select count(*) from audit.audit_event
                 where action = 'POLICY.DOCUMENTS_GENERATE' and entity_id = ? and actor_type = 'SYSTEM'
                   and actor_id = 'policy-documents' and insurer_id = ?""", Integer.class,
                issued.policyId().toString(), insurer.id())).isOne();

        awaitNotification(c.id());
        Map<String, Object> inbox = inbox(c.id());
        assertThat(inbox).containsEntry("category", "POLICY");
        JsonNode data = objectMapper.readTree((String) inbox.get("data"));
        assertThat(data.path("webPath").asText()).isEqualTo("/cliente/apolices/" + issued.policyId());
        assertThat(data.path("deepLink").asText()).isEqualTo("moseguros://policy/" + issued.policyId());
    }

    @Test
    void anEnglishSpeakingCustomerGetsTheDocumentsInEnglish() throws Exception {
        Customer c = support.payments().customer();
        support.payments().preferredLanguage(c.id(), "en");
        Issued issued = pay(c, insurer, null);
        UUID[] files = support.awaitDocuments(issued.policyId());

        String certificate = text(download(c.token(), files[0]));
        assertThat(certificate).contains("Motor insurance certificate", "10 Feb 2027 10:31");
        assertThat(certificate).doesNotContain("Certificado de seguro automóvel");
        assertThat(normalize(text(download(c.token(), files[1])))).contains("Policy schedule", "MZN 2,999.00");
    }

    @Test
    void generatingTwiceKeepsTheSameFilesAndNotifiesOnce() throws Exception {
        Customer c = support.payments().customer();
        Issued issued = pay(c, insurer, null);
        UUID[] files = support.awaitDocuments(issued.policyId());
        awaitNotification(c.id());

        assertThat(system(() -> documents.generate(issued.policyId())))
                .isEqualTo(PolicyDocumentService.Outcome.ALREADY_READY);
        assertThat(system(() -> documents.generate(issued.policyId())))
                .isEqualTo(PolicyDocumentService.Outcome.ALREADY_READY);

        assertThat(support.awaitDocuments(issued.policyId())).containsExactly(files);
        assertThat(documentsReadyEvents(issued.policyId())).isOne();
        assertThat(notifications(c.id())).isOne();
        assertThat(support.owner().queryForObject(
                "select count(*) from document.file where owner_type = 'POLICY' and owner_id = ?", Integer.class,
                issued.policyId())).isEqualTo(2);
    }

    @Test
    void anInsurerWithALogoGetsItInTheHeader() throws Exception {
        Seg withLogo = support.insurer("Seguradora Com Logo");
        UUID logo = system(() -> {
            FileRef stored = documentApi.store(new StoreFileCommand("INSURER_LOGO", withLogo.id(), "LOGO",
                    FileClassification.PUBLIC, withLogo.id(), "logo.png", "image/png", Set.of("image/png"),
                    bytes("sample.png")));
            return stored.id();
        });
        support.owner().update("update document.file set linked_at = now() where id = ?", logo);
        support.owner().update("update organization.insurer_profile set logo_file_id = ? where organization_id = ?",
                logo, withLogo.id());

        Customer c = support.payments().customer();
        Issued issued = pay(c, withLogo, null);
        UUID[] files = support.awaitDocuments(issued.policyId());

        try (PDDocument doc = Loader.loadPDF(download(c.token(), files[0]))) {
            assertThat(images(doc)).isEqualTo(2); // logótipo e QR
        }
        try (PDDocument doc = Loader.loadPDF(download(c.token(), files[1]))) {
            assertThat(images(doc)).isEqualTo(1); // só o logótipo
        }
    }

    @Test
    void anInsurerWithoutALogoGetsItsNameInText() throws Exception {
        Customer c = support.payments().customer();
        Issued issued = pay(c, insurer, null);
        UUID[] files = support.awaitDocuments(issued.policyId());

        byte[] pdf = download(c.token(), files[0]);
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            assertThat(images(doc)).isEqualTo(1); // só o QR
        }
        // a primeira linha da página (cabeçalho) tem o nome da seguradora em texto
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            assertThat(stripper.getText(doc).lines().findFirst().orElse("")).contains(insurer.name());
        }
    }

    // --- Falha e nova tentativa ---

    @Test
    void aRenderingFailureLeavesNothingAndTheJobRetries() throws Exception {
        Customer c = support.payments().customer();
        UUID policyId = support.insert(Row.of(insurer.id(), c.id(), PolicyTestSupport.plate(), "ACTIVE",
                START, START.plus(Duration.ofDays(365))));
        support.certificate(policyId, "VALID", PolicyTestSupport.token());
        // uma categoria sem texto faz falhar o modelo (PDF_RENDER_FAILED)
        support.owner().update("""
                update policy.policy set vehicle_snapshot = vehicle_snapshot || '{"category":"UNKNOWN_CATEGORY"}'::jsonb,
                       issued_at = timestamptz '2020-01-01 00:00:00+00' where id = ?""", policyId);

        // antes: PENDING na lista do cliente
        JsonPath before = myPolicy(c.token(), policyId);
        assertThat(before.getList("documents.type", String.class)).containsExactly("CERTIFICATE", "SCHEDULE");
        assertThat(before.getList("documents.status", String.class)).containsOnly("PENDING");
        assertThat(before.getString("documents[0].fileId")).isNull();

        assertThatThrownBy(() -> system(() -> documents.generate(policyId)))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(((DomainException) e).getCode()).isEqualTo("PDF_RENDER_FAILED"));
        assertThat(support.documentsReady(policyId)).isFalse();
        assertThat(certificateRow(policyId).get("pdf_file_id")).isNull();
        assertThat(documentsReadyEvents(policyId)).isZero();
        assertThat(notifications(c.id())).isZero();
        // o PDF guardado antes da falha fica por ligar (o OrphanFileCleanupJob limpa-o)
        assertThat(support.owner().queryForObject("""
                select count(*) from document.file where owner_type = 'POLICY' and owner_id = ?
                   and linked_at is not null""", Integer.class, policyId)).isZero();

        // o job falha esta apólice (contador), não pára
        double failuresBefore = failures();
        system(() -> job.run());
        assertThat(failures()).isGreaterThan(failuresBefore);
        assertThat(support.documentsReady(policyId)).isFalse();

        // corrigido o dado, a execução seguinte gera os documentos
        support.owner().update("""
                update policy.policy set vehicle_snapshot = vehicle_snapshot || '{"category":"LIGHT_PASSENGER"}'::jsonb
                 where id = ?""", policyId);
        system(() -> job.run());
        assertThat(support.documentsReady(policyId)).isTrue();
        assertThat(documentsReadyEvents(policyId)).isOne();
        assertThat(notifications(c.id())).isOne();

        JsonPath after = myPolicy(c.token(), policyId);
        assertThat(after.getList("documents.status", String.class)).containsOnly("READY");
        assertThat(after.getString("documents[0].fileName")).endsWith(".pdf");
        assertThat(after.getString("documents[1].fileName")).endsWith("-condicoes-particulares.pdf");
    }

    @Test
    void theJobIgnoresRecentPolicies() {
        Customer c = support.payments().customer();
        UUID policyId = support.insert(Row.of(insurer.id(), c.id(), PolicyTestSupport.plate(), "ACTIVE",
                START, START.plus(Duration.ofDays(365))));
        support.certificate(policyId, "VALID", PolicyTestSupport.token());
        support.owner().update("update policy.policy set issued_at = ? where id = ?",
                Timestamp.from(Instant.now().plus(Duration.ofHours(1))), policyId);

        system(() -> job.run());

        assertThat(support.documentsReady(policyId)).isFalse();
    }

    // --- Lista dos documentos no detalhe ---

    @Test
    void theDetailListsTheDocumentsWithTheirNames() throws Exception {
        Customer c = support.payments().customer();
        Issued issued = pay(c, insurer, "PR-9999-" + digits(6) + "-r1.pdf");
        UUID[] files = support.awaitDocuments(issued.policyId());
        Map<String, Object> certificate = certificateRow(issued.policyId());
        Map<String, Object> policy = support.policyRow(issued.policyId());

        for (JsonPath detail : List.of(myPolicy(c.token(), issued.policyId()), insurerPolicy(issued.policyId()))) {
            assertThat(detail.getList("documents.type", String.class)).containsExactly("CERTIFICATE", "SCHEDULE",
                    "GENERAL_CONDITIONS", "PRE_CONTRACT_INFO", "PROPOSAL");
            assertThat(detail.getList("documents.status", String.class)).containsOnly("READY");
            assertThat(detail.getString("documents[0].fileId")).isEqualTo(files[0].toString());
            assertThat(detail.getString("documents[0].fileName")).isEqualTo(certificate.get("number") + ".pdf");
            assertThat(detail.getString("documents[1].fileId")).isEqualTo(files[1].toString());
            assertThat(detail.getString("documents[1].fileName"))
                    .isEqualTo(policy.get("number") + "-condicoes-particulares.pdf");
            assertThat(detail.getString("documents[2].fileName")).isEqualTo("general_conditions.pdf");
            assertThat(detail.getString("documents[3].fileName")).isEqualTo("pre_contract_info.pdf");
            assertThat(detail.getString("documents[4].fileId")).isEqualTo(issued.signedFileId().toString());
            assertThat(detail.getString("documents[4].fileName")).isEqualTo(issued.signedFileName());
            assertThat(detail.getString("certificate.fileId")).isEqualTo(files[0].toString());
        }
        // o cliente descarrega as condições gerais e o PDF da proposta (políticas dos módulos donos)
        String generalConditions = myPolicy(c.token(), issued.policyId()).getString("documents[2].fileId");
        given().auth().oauth2(c.token()).get("/api/v1/files/{id}", generalConditions).then().statusCode(200);
        given().auth().oauth2(c.token()).get("/api/v1/files/{id}", issued.signedFileId()).then().statusCode(200);
    }

    @Test
    void aMissingFileIsLeftOutOfTheList() {
        Customer c = support.payments().customer();
        Issued issued = pay(c, insurer, "PR-9999-" + digits(6) + "-r1.pdf");
        support.awaitDocuments(issued.policyId());
        support.owner().update("update document.file set purged_at = now() where id = ?", issued.signedFileId());

        assertThat(myPolicy(c.token(), issued.policyId()).getList("documents.type", String.class))
                .containsExactly("CERTIFICATE", "SCHEDULE", "GENERAL_CONDITIONS", "PRE_CONTRACT_INFO");
    }

    // --- Apoio ---

    /** Apólice emitida e, se pedido, o PDF assinado da proposta. */
    record Issued(UUID policyId, UUID proposalId, UUID signedFileId, String signedFileName) {
    }

    /**
     * Proposta paga por referência, com o início em {@link #START}; com {@code signedFileName}, a proposta
     * leva uma assinatura com um PDF desse nome (dono {@code PROPOSAL}).
     */
    Issued pay(Customer c, Seg seg, String signedFileName) {
        UUID proposal = support.payments().accepted(c.id(), seg);
        support.owner().update("""
                update proposal.proposal set start_at = ?, end_at = end_at - (start_at - ?) where id = ?""",
                Timestamp.from(START), Timestamp.from(START), proposal);
        UUID signed = signedFileName == null ? null : sign(proposal, seg, c.id(), signedFileName);
        UUID intentId = id(payReference(c.token(), proposal));
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        return new Issued(support.awaitPolicy(proposal), proposal, signed, signedFileName);
    }

    /** PDF da proposta (dono {@code PROPOSAL}) e a assinatura que o refere, gravados directamente. */
    UUID sign(UUID proposal, Seg seg, UUID customerId, String fileName) {
        FileRef file = system(() -> documentApi.store(new StoreFileCommand("PROPOSAL", proposal, "PROPOSAL_DOCUMENT",
                FileClassification.SENSITIVE, seg.id(), fileName, "application/pdf", Set.of("application/pdf"),
                bytes("sample.pdf"))));
        support.owner().update("update document.file set linked_at = now() where id = ?", file.id());
        UUID signature = UUID.randomUUID();
        support.owner().update("""
                insert into proposal.signature
                  (id, created_at, created_by, updated_at, updated_by, version, insurer_id, proposal_id,
                   content_revision, signer_user_id, method, document_file_id, document_hash, otp_challenge_id,
                   otp_channel, destination_masked, signed_at)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, 1, ?, 'OTP', ?, ?, ?, 'SMS', '+258*****4567', now())""",
                signature, seg.id(), proposal, customerId, file.id(), file.sha256(), UUID.randomUUID());
        support.owner().update("update proposal.proposal set current_signature_id = ? where id = ?", signature,
                proposal);
        return file.id();
    }

    byte[] download(String token, UUID fileId) {
        return given().auth().oauth2(token).get("/api/v1/files/{id}", fileId)
                .then().statusCode(200).contentType("application/pdf").extract().asByteArray();
    }

    JsonPath myPolicy(String token, UUID policyId) {
        return given().auth().oauth2(token).get(PolicyTestSupport.MY_POLICIES + "/{id}", policyId)
                .then().statusCode(200).extract().jsonPath();
    }

    JsonPath insurerPolicy(UUID policyId) {
        return given().auth().oauth2(insurer.underwriter()).get(PolicyTestSupport.INSURER_POLICIES + "/{id}", policyId)
                .then().statusCode(200).extract().jsonPath();
    }

    Map<String, Object> certificateRow(UUID policyId) {
        return support.owner().queryForMap("select number, verification_token, pdf_file_id from policy.certificate "
                + "where policy_id = ? and status = 'VALID'", policyId);
    }

    Map<String, Object> fileRow(UUID fileId) {
        return support.owner().queryForMap("""
                select owner_type, owner_id, purpose, classification, insurer_id, original_filename, sha256, linked_at
                  from document.file where id = ?""", fileId);
    }

    int documentsReadyEvents(UUID policyId) {
        return support.owner().queryForObject("select count(*) from policy.policy_event where policy_id = ? "
                + "and type = 'DOCUMENTS_READY'", Integer.class, policyId);
    }

    int notifications(UUID userId) {
        return support.owner().queryForObject("select count(*) from notification.message where user_id = ? "
                + "and template_code = 'POLICY_ISSUED' and channel = 'INBOX'", Integer.class, userId);
    }

    void awaitNotification(UUID userId) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(notifications(userId)).isOne());
    }

    Map<String, Object> inbox(UUID userId) {
        return support.owner().queryForMap("select category, data::text as data from notification.message "
                + "where user_id = ? and template_code = 'POLICY_ISSUED' and channel = 'INBOX'", userId);
    }

    double failures() {
        Counter counter = meters.find(PolicyDocumentsJob.FAILURES).counter();
        return counter == null ? 0 : counter.count();
    }

    static String text(byte[] pdf) throws Exception {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(doc);
        }
    }

    /** Espaços inquebráveis como espaços normais (o MoneyFormat usa U+00A0). */
    static String normalize(String text) {
        return text.replace(' ', ' ').replace(' ', ' ');
    }

    /** O QR lido da página 1 renderizada a 150 dpi. */
    static String qr(byte[] pdf) throws Exception {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            BufferedImage page = new PDFRenderer(doc).renderImageWithDPI(0, 150, ImageType.RGB);
            return QrCodeRendererTest.decode(page).getText();
        }
    }

    static int images(PDDocument doc) throws Exception {
        int count = 0;
        for (var name : doc.getPage(0).getResources().getXObjectNames()) {
            if (doc.getPage(0).getResources().getXObject(name) instanceof PDImageXObject) {
                count++;
            }
        }
        return count;
    }

    static byte[] bytes(String name) {
        try {
            return TestFiles.bytes(name);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /** Corre em {@code SYSTEM} (como o listener e o job), sem transacção. */
    static <T> T system(java.util.function.Supplier<T> work) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.system(null));
        try {
            return work.get();
        } finally {
            TenantContext.set(previous);
        }
    }
}
