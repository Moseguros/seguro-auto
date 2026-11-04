package mz.co.moseguros.document.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileContent;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.PdfTarget;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * {@code DocumentApi.renderPdf} (S4-04 §1) com o modelo {@code proposal}, sobre Postgres real (RLS) e o
 * dono de teste {@code TEST}: texto do PDF lido com o PDFBox, hash, ligação ao dono e erros.
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PdfRenderServiceIT {

    static final String NBSP = " ";

    @Autowired
    DocumentApi documents;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PostgreSQLContainer<?> postgres;

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void rendersTheProposalTemplateInPortugueseStoresAndLinksIt() throws IOException {
        UUID customer = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID insurerId = UUID.randomUUID();
        PdfTarget target = new PdfTarget(TestFileAccessPolicy.OWNER_TYPE, ownerId, "PROPOSAL_DOCUMENT", insurerId,
                FileClassification.SENSITIVE, "PR-2027-000123-r3.pdf");

        FileRef ref = as(TenantSnapshot.customer(customer), () -> tx.execute(status ->
                documents.renderPdf("proposal", model("pt"), Locale.forLanguageTag("pt"), target)));

        assertThat(ref.contentType()).isEqualTo("application/pdf");
        assertThat(ref.linked()).isTrue();
        assertThat(ref.ownerType()).isEqualTo(TestFileAccessPolicy.OWNER_TYPE);
        assertThat(ref.ownerId()).isEqualTo(ownerId);
        assertThat(ref.purpose()).isEqualTo("PROPOSAL_DOCUMENT");
        assertThat(ref.insurerId()).isEqualTo(insurerId);
        assertThat(ref.classification()).isEqualTo(FileClassification.SENSITIVE);
        assertThat(ref.originalFilename()).isEqualTo("PR-2027-000123-r3.pdf");
        assertThat(ref.sha256()).matches("^[0-9a-f]{64}$");

        byte[] bytes = read(customer, ref.id());
        assertThat(FileStoreService.sha256(bytes)).isEqualTo(ref.sha256());
        Map<String, Object> row = TestcontainersConfiguration.ownerJdbc(postgres)
                .queryForMap("select linked_at, sha256, owner_type, purpose from document.file where id = ?", ref.id());
        assertThat(row.get("linked_at")).isNotNull();
        assertThat(row.get("sha256")).isEqualTo(ref.sha256());

        String text = text(bytes);
        assertThat(text).contains("PR-2027-000123")
                .contains("Ana Maria Mussa")
                .contains("12" + NBSP + "345,67 MT")
                .contains("Página 1 de")
                .contains("Proposta de seguro automóvel")
                .contains("De 10/01/2027 a 09/01/2028")
                .contains("Responsabilidade civil obrigatória")
                .contains("110100123456A")
                .contains("123456789")
                .contains("+258*****4567")
                .contains("5,00" + NBSP + "%")
                .contains("«Particular»");
    }

    @Test
    void rendersInEnglish() throws IOException {
        UUID customer = UUID.randomUUID();
        PdfTarget target = new PdfTarget(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(), "PROPOSAL_DOCUMENT",
                null, FileClassification.SENSITIVE, "PR-2027-000123-r1.pdf");

        FileRef ref = as(TenantSnapshot.customer(customer), () -> tx.execute(status ->
                documents.renderPdf("proposal", model("en"), Locale.ENGLISH, target)));

        String text = text(read(customer, ref.id()));
        assertThat(text).contains("MZN 12,345.67")
                .contains("Motor insurance proposal")
                .contains("Page 1 of")
                .contains("From 10 Jan 2027 to 09 Jan 2028")
                .contains("I'm not sure");
    }

    @Test
    void anotherLocaleFallsBackToPortuguese() throws IOException {
        UUID customer = UUID.randomUUID();
        PdfTarget target = new PdfTarget(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(), "PROPOSAL_DOCUMENT",
                null, FileClassification.SENSITIVE, "x.pdf");

        FileRef ref = as(TenantSnapshot.customer(customer), () -> tx.execute(status ->
                documents.renderPdf("proposal", model("pt"), Locale.FRENCH, target)));

        assertThat(text(read(customer, ref.id()))).contains("Proposta de seguro automóvel");
    }

    @Test
    void nullTargetIsAProgrammingError() {
        assertThatThrownBy(() -> documents.renderPdf("proposal", model("pt"), Locale.ENGLISH, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void needsTheCallersTransaction() {
        PdfTarget target = new PdfTarget(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(), "PROPOSAL_DOCUMENT",
                null, FileClassification.SENSITIVE, "x.pdf");
        assertThatThrownBy(() -> as(TenantSnapshot.customer(UUID.randomUUID()),
                () -> documents.renderPdf("proposal", model("pt"), Locale.ENGLISH, target)))
                .isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    void unknownTemplateAndMissingTextAreErrors() {
        PdfTarget target = new PdfTarget(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(), "PROPOSAL_DOCUMENT",
                null, FileClassification.SENSITIVE, "x.pdf");
        UUID customer = UUID.randomUUID();

        assertThatThrownBy(() -> as(TenantSnapshot.customer(customer), () -> tx.execute(status ->
                documents.renderPdf("certificate", model("pt"), Locale.ENGLISH, target))))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("PDF_TEMPLATE_NOT_FOUND");
                    assertThat(e.getHttpStatus()).isEqualTo(500);
                });

        // Um código sem texto (pdf.coverage.UNKNOWN) faz falhar o PDF: nunca sai um texto vazio.
        Map<String, Object> broken = model("pt");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> coverages = (List<Map<String, Object>>) broken.get("coverages");
        Map<String, Object> unknown = new LinkedHashMap<>(coverages.get(0));
        unknown.put("code", "UNKNOWN");
        broken.put("coverages", List.of(unknown));
        assertThatThrownBy(() -> as(TenantSnapshot.customer(customer), () -> tx.execute(status ->
                documents.renderPdf("proposal", broken, Locale.ENGLISH, target))))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("PDF_RENDER_FAILED");
                    assertThat(e.getHttpStatus()).isEqualTo(500);
                });
    }

    // --- Apoio ---

    private byte[] read(UUID customer, UUID fileId) {
        return as(TenantSnapshot.customer(customer), () -> tx.execute(status -> {
            try (FileContent content = documents.open(fileId)) {
                return content.content().readAllBytes();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }));
    }

    static String text(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }

    /** Modelo de teste com todas as secções do {@code proposal.html}. */
    static Map<String, Object> model(String language) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("number", "PR-2027-000123");
        m.put("revision", 3);
        m.put("generatedAt", Instant.parse("2027-01-09T12:05:00Z"));
        m.put("insurerName", "Seguradora Demo");
        m.put("product", Map.of("name", "Auto Base", "versionId", UUID.randomUUID().toString(), "type",
                "COMPREHENSIVE"));
        m.put("channel", "APP");
        m.put("brokerName", null);
        Map<String, Object> holder = new LinkedHashMap<>();
        holder.put("fullName", "Ana Maria Mussa");
        holder.put("birthDate", LocalDate.of(1990, 5, 17));
        holder.put("idDocType", "BI");
        holder.put("idDocNumber", "110100123456A");
        holder.put("nuit", "123456789");
        holder.put("address", "Av. Julius Nyerere, 1234");
        holder.put("province", "MAPUTO_CITY");
        holder.put("msisdn", "+258841234567");
        holder.put("email", null);
        m.put("holder", holder);
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("plate", "AAA-123-MC");
        vehicle.put("make", "Toyota");
        vehicle.put("model", "Corolla");
        vehicle.put("year", 2018);
        vehicle.put("chassisNumber", "JTDBR32E720123456");
        vehicle.put("category", "LIGHT_PASSENGER");
        vehicle.put("usage", "PRIVATE");
        vehicle.put("seats", 5);
        vehicle.put("commercialValue", Money.ofCentavos(80000000));
        m.put("vehicle", vehicle);
        Map<String, Object> driver = new LinkedHashMap<>();
        driver.put("fullName", "Ana Maria Mussa");
        driver.put("birthDate", LocalDate.of(1990, 5, 17));
        driver.put("licenceNumberMasked", "****4567");
        driver.put("licenceCategories", "B");
        driver.put("licenceIssueDate", LocalDate.of(2012, 3, 1));
        m.put("driver", driver);
        m.put("period", Map.of("startDate", LocalDate.of(2027, 1, 10), "endDate", LocalDate.of(2028, 1, 9),
                "durationDays", 365));
        Map<String, Object> mtpl = new LinkedHashMap<>();
        mtpl.put("code", "MTPL");
        mtpl.put("capital", Money.ofCentavos(50000000));
        mtpl.put("deductibleType", "NONE");
        mtpl.put("deductibleAmount", null);
        mtpl.put("deductiblePercent", null);
        mtpl.put("deductibleMinAmount", null);
        mtpl.put("premium", Money.ofCentavos(300000));
        Map<String, Object> collision = new LinkedHashMap<>();
        collision.put("code", "OD_COLLISION");
        collision.put("capital", Money.ofCentavos(80000000));
        collision.put("deductibleType", "PERCENT");
        collision.put("deductibleAmount", null);
        collision.put("deductiblePercent", new BigDecimal("5"));
        collision.put("deductibleMinAmount", Money.ofCentavos(500000));
        collision.put("premium", Money.ofCentavos(900000));
        m.put("coverages", List.of(mtpl, collision));
        Map<String, Object> premium = new LinkedHashMap<>();
        premium.put("commercial", Money.ofCentavos(1200000));
        premium.put("charges", List.of(Map.of("label", "Imposto de selo", "amount", Money.ofCentavos(34567))));
        premium.put("roundingAdjustment", null);
        premium.put("total", Money.ofCentavos(1234567));
        m.put("premium", premium);
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("code", "CLAIMS_LAST_3_YEARS");
        claims.put("answer", true);
        claims.put("claimsCount", 1);
        claims.put("details", "Colisão traseira em Maputo, Março de 2025.");
        Map<String, Object> usage = new LinkedHashMap<>();
        usage.put("code", "USAGE_DIFFERENT");
        usage.put("answer", false);
        usage.put("claimsCount", null);
        usage.put("details", null);
        m.put("declarations", Map.of("confirmedAt", Instant.parse("2027-01-09T11:00:00Z"),
                "answers", List.of(claims, usage)));
        Map<String, Object> suitability = new LinkedHashMap<>();
        suitability.put("result", "WARNED");
        suitability.put("answers", List.of(Map.of("code", "SUIT_NEED", "answer", "UNSURE"),
                Map.of("code", "SUIT_FINANCED", "answer", "NO")));
        suitability.put("reasons", List.of("NEED_UNSURE"));
        suitability.put("acknowledgedAt", Instant.parse("2027-01-09T11:30:00Z"));
        m.put("suitability", suitability);
        m.put("inspection", null);
        m.put("documents", List.of(Map.of("type", "GENERAL_CONDITIONS", "fileId", UUID.randomUUID().toString())));
        m.put("signatureDestination", "+258*****4567");
        return m;
    }

    private static <T> T as(TenantSnapshot tenant, Supplier<T> action) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(tenant);
        try {
            return action.get();
        } finally {
            TenantContext.set(previous);
        }
    }
}
