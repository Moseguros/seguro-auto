package mz.co.moseguros.document.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileContent;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.document.infrastructure.storage.FileStorage;
import mz.co.moseguros.document.infrastructure.storage.FilesystemFileStorage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * {@code DocumentApi} sobre Postgres real, ligado como {@code moseguros_app_user} (RLS), com a
 * {@link TestFileAccessPolicy} para o tipo {@code TEST} (S2-01 §2).
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class FileStoreServiceIT {

    @Autowired
    DocumentApi documents;

    @Autowired
    FileStorage storage;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PostgreSQLContainer<?> postgres;

    JdbcTemplate owner;
    byte[] jpeg;
    byte[] png;
    byte[] pdf;

    @BeforeEach
    void setUp() throws IOException {
        owner = TestcontainersConfiguration.ownerJdbc(postgres);
        jpeg = TestFiles.bytes("sample.jpg");
        png = TestFiles.bytes("sample.png");
        pdf = TestFiles.bytes("sample.pdf");
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void storesAndReadsBackWithTheSameSha256() throws IOException {
        UUID customer = UUID.randomUUID();
        FileRef ref = as(TenantSnapshot.customer(customer), () -> documents.store(
                command("../fotos/Livrete do carro.png", "image/jpeg", jpeg)));

        assertThat(ref.contentType()).isEqualTo("image/jpeg");
        assertThat(ref.sizeBytes()).isEqualTo(jpeg.length);
        assertThat(ref.sha256()).isEqualTo(FileStoreService.sha256(jpeg)).matches("^[0-9a-f]{64}$");
        assertThat(ref.originalFilename()).isEqualTo("Livrete do carro.jpg");
        assertThat(ref.ownerType()).isEqualTo("TEST");
        assertThat(ref.purpose()).isEqualTo("REGISTRATION_DOC");
        assertThat(ref.classification()).isEqualTo(FileClassification.PERSONAL);
        assertThat(ref.linked()).isFalse();
        assertThat(ref.createdAt()).isNotNull();

        Map<String, Object> row = owner.queryForMap("select * from document.file where id = ?", ref.id());
        assertThat(row.get("uploaded_by_user_id")).isEqualTo(customer);
        assertThat(row.get("uploaded_by_org_id")).isNull();
        assertThat(row.get("linked_at")).isNull();
        assertThat((String) row.get("storage_key")).matches("^\\d{4}/\\d{2}/\\d{2}/" + ref.id() + "\\.jpg$")
                .doesNotContain("Livrete");

        try (FileContent content = as(TenantSnapshot.customer(customer), () -> documents.open(ref.id()))) {
            byte[] bytes = content.content().readAllBytes();
            assertThat(FileStoreService.sha256(bytes)).isEqualTo(ref.sha256());
            assertThat(content.ref()).isEqualTo(ref);
        }
        assertThat(as(TenantSnapshot.customer(customer), () -> documents.metadata(ref.id()))).isEqualTo(ref);
    }

    @Test
    void emptyContentIsRefused() {
        assertCode(() -> documents.store(command("vazio.pdf", null, new byte[0])), "FILE_EMPTY", 400);
    }

    @Test
    void contentAbove10MbIsRefused() {
        byte[] big = new byte[10 * 1024 * 1024 + 1];
        System.arraycopy(pdf, 0, big, 0, pdf.length);
        assertCode(() -> documents.store(command("grande.pdf", null, big)), "FILE_TOO_LARGE", 413);
    }

    @Test
    void gifIsRefusedAndNothingIsStored() throws IOException {
        int before = rows();
        byte[] gif = TestFiles.bytes("sample.gif");
        assertCode(() -> documents.store(command("imagem.gif", "image/gif", gif)), "FILE_TYPE_NOT_ALLOWED", 415);
        assertThat(rows()).isEqualTo(before);
    }

    @Test
    void declaredTypeMustMatchTheContent() {
        assertCode(() -> documents.store(command("foto.png", "image/png", jpeg)), "FILE_TYPE_NOT_ALLOWED", 415);
    }

    @Test
    void octetStreamIsAcceptedWithTheRealType() {
        FileRef ref = system(() -> documents.store(command("foto", "application/octet-stream", jpeg)));
        assertThat(ref.contentType()).isEqualTo("image/jpeg");
        assertThat(ref.originalFilename()).isEqualTo("foto.jpg");

        FileRef withParameters = system(() -> documents.store(command("doc", "Application/PDF; charset=binary", pdf)));
        assertThat(withParameters.contentType()).isEqualTo("application/pdf");
    }

    @Test
    void allowedContentTypesRestrictTheTypes() {
        StoreFileCommand onlyPdf = new StoreFileCommand("TEST", UUID.randomUUID(), "GENERAL_CONDITIONS",
                FileClassification.PUBLIC, null, "condicoes.png", null, Set.of("application/pdf"), png);
        assertCode(() -> documents.store(onlyPdf), "FILE_TYPE_NOT_ALLOWED", 415);

        StoreFileCommand pdfOk = new StoreFileCommand("TEST", UUID.randomUUID(), "GENERAL_CONDITIONS",
                FileClassification.PUBLIC, null, "condicoes.pdf", null, Set.of("application/pdf"), pdf);
        assertThat(system(() -> documents.store(pdfOk)).contentType()).isEqualTo("application/pdf");
    }

    @Test
    void ownerTypeWithoutPolicyIsAProgrammingError() {
        StoreFileCommand command = new StoreFileCommand("NO_POLICY", UUID.randomUUID(), "ANY",
                FileClassification.PUBLIC, null, "x.pdf", null, null, pdf);
        assertThatThrownBy(() -> system(() -> documents.store(command)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NO_POLICY");
    }

    @Test
    void callerTransactionFailingAfterStoreLeavesAnOrphan() {
        UUID[] stored = new UUID[1];
        assertThatThrownBy(() -> system(() -> tx.execute(status -> {
            stored[0] = documents.store(command("orfao.pdf", null, pdf)).id();
            throw new IllegalStateException("caller failed");
        }))).isInstanceOf(IllegalStateException.class);

        Map<String, Object> row = owner.queryForMap("select linked_at from document.file where id = ?", stored[0]);
        assertThat(row.get("linked_at")).isNull();
    }

    @Test
    void markLinkedInTheCallerTransactionSetsLinkedAt() {
        UUID customer = UUID.randomUUID();
        UUID id = as(TenantSnapshot.customer(customer), () -> tx.execute(status -> {
            FileRef ref = documents.store(command("ligado.pdf", null, pdf));
            documents.markLinked(ref.id());
            documents.markLinked(ref.id()); // idempotente
            return ref.id();
        }));

        Timestamp linkedAt = owner.queryForObject("select linked_at from document.file where id = ?",
                Timestamp.class, id);
        assertThat(linkedAt).isNotNull();
        assertThat(as(TenantSnapshot.customer(customer), () -> documents.metadata(id)).linked()).isTrue();

        as(TenantSnapshot.customer(customer), () -> {
            documents.markLinked(id);
            return null;
        });
        assertThat(owner.queryForObject("select linked_at from document.file where id = ?", Timestamp.class, id))
                .isEqualTo(linkedAt);
    }

    @Test
    void markLinkedOfAnInvisibleFileIsNotFound() {
        FileRef ref = as(TenantSnapshot.customer(UUID.randomUUID()), () -> documents.store(command("a.pdf", null, pdf)));
        assertCodeAs(TenantSnapshot.customer(UUID.randomUUID()), () -> {
            documents.markLinked(ref.id());
            return null;
        }, "FILE_NOT_FOUND", 404);
        assertCode(() -> {
            documents.markLinked(UUID.randomUUID());
            return null;
        }, "FILE_NOT_FOUND", 404);
    }

    @Test
    void customerBDoesNotSeeTheFilesOfCustomerA() {
        UUID customerA = UUID.randomUUID();
        FileRef ref = as(TenantSnapshot.customer(customerA), () -> documents.store(command("a.pdf", null, pdf)));

        assertCodeAs(TenantSnapshot.customer(UUID.randomUUID()), () -> documents.metadata(ref.id()),
                "FILE_NOT_FOUND", 404);
        assertCodeAs(TenantSnapshot.customer(UUID.randomUUID()), () -> documents.open(ref.id()),
                "FILE_NOT_FOUND", 404);
        assertThat(as(TenantSnapshot.customer(customerA), () -> documents.metadata(ref.id())).id()).isEqualTo(ref.id());
    }

    @Test
    void insurerBDoesNotSeeTheFilesOfInsurerA() {
        UUID insurerA = UUID.randomUUID();
        UUID insurerB = UUID.randomUUID();
        FileRef ref = as(TenantSnapshot.org(UUID.randomUUID(), insurerA, insurerA), () -> documents.store(
                new StoreFileCommand("TEST", UUID.randomUUID(), "GENERAL_CONDITIONS", FileClassification.PUBLIC,
                        insurerA, "condicoes.pdf", "application/pdf", null, pdf)));

        assertCodeAs(TenantSnapshot.org(UUID.randomUUID(), insurerB, insurerB), () -> documents.metadata(ref.id()),
                "FILE_NOT_FOUND", 404);
        assertThat(as(TenantSnapshot.org(UUID.randomUUID(), insurerA, insurerA), () -> documents.metadata(ref.id()))
                .insurerId()).isEqualTo(insurerA);
        assertThat(as(TenantSnapshot.platform(UUID.randomUUID()), () -> documents.metadata(ref.id())).id())
                .isEqualTo(ref.id());
    }

    @Test
    void purgedFilesAreNotFound() {
        FileRef ref = system(() -> documents.store(command("limpo.pdf", null, pdf)));
        owner.update("update document.file set purged_at = now() where id = ?", ref.id());
        assertCode(() -> documents.metadata(ref.id()), "FILE_NOT_FOUND", 404);
        assertCode(() -> documents.open(ref.id()), "FILE_NOT_FOUND", 404);
    }

    @Test
    void missingContentIsAStorageError() {
        FileRef ref = system(() -> documents.store(command("sem-conteudo.pdf", null, pdf)));
        storage.delete(owner.queryForObject("select storage_key from document.file where id = ?", String.class,
                ref.id()));
        assertCode(() -> documents.open(ref.id()), "FILE_STORAGE_ERROR", 500);
    }

    @Test
    void contentIsNotLeftBehindWhenTheInsertFails() throws IOException {
        // âmbito NONE: o RLS recusa a inserção depois de o conteúdo já estar gravado
        int before = rows();
        Set<Path> filesBefore = storedFiles();
        assertThatThrownBy(() -> as(TenantSnapshot.none(), () -> documents.store(command("x.pdf", null, pdf))))
                .isNotInstanceOf(DomainException.class);
        assertThat(rows()).isEqualTo(before);
        assertThat(storedFiles()).isEqualTo(filesBefore);
    }

    private Set<Path> storedFiles() throws IOException {
        Path base = ((FilesystemFileStorage) storage).baseDir();
        try (Stream<Path> files = Files.walk(base)) {
            return files.filter(Files::isRegularFile).collect(Collectors.toSet());
        }
    }

    private int rows() {
        return owner.queryForObject("select count(*) from document.file", Integer.class);
    }

    private static StoreFileCommand command(String filename, String declaredType, byte[] content) {
        return new StoreFileCommand("TEST", UUID.randomUUID(), "REGISTRATION_DOC", FileClassification.PERSONAL, null,
                filename, declaredType, null, content);
    }

    private void assertCode(Supplier<?> action, String code, int status) {
        assertCodeAs(TenantSnapshot.system(null), action, code, status);
    }

    private void assertCodeAs(TenantSnapshot tenant, Supplier<?> action, String code, int status) {
        assertThatThrownBy(() -> as(tenant, action))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo(code);
                    assertThat(e.getHttpStatus()).isEqualTo(status);
                });
    }

    private static <T> T system(Supplier<T> action) {
        return as(TenantSnapshot.system(null), action);
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
