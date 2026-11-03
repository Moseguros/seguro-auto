package mz.co.moseguros.document.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.document.infrastructure.storage.FileStorage;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Limpeza de órfãos (S2-01 §7) com um relógio controlado: só os ficheiros nunca ligados com mais
 * de 30 dias perdem o conteúdo; a linha fica, com {@code purged_at}.
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class OrphanFileCleanupJobIT {

    @Autowired
    OrphanFileCleanupJob job;

    @Autowired
    DocumentApi documents;

    @Autowired
    FileStorage storage;

    @Autowired
    MutableClock clock;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PostgreSQLContainer<?> postgres;

    @AfterEach
    void resetClock() {
        clock.reset();
    }

    @Test
    void purgesOnlyOrphansOlderThan30Days() throws IOException {
        JdbcTemplate owner = TestcontainersConfiguration.ownerJdbc(postgres);
        byte[] pdf = TestFiles.bytes("sample.pdf");

        // t0: ficheiro ligado; t0+29d: órfão "velho"; t0+31d: órfão "novo"; agora = t0+60d
        FileRef linked = system(() -> tx.execute(status -> {
            FileRef ref = documents.store(command(pdf));
            documents.markLinked(ref.id());
            return ref;
        }));
        clock.advance(Duration.ofDays(29));
        FileRef old = system(() -> documents.store(command(pdf)));
        clock.advance(Duration.ofDays(2));
        FileRef young = system(() -> documents.store(command(pdf)));
        clock.advance(Duration.ofDays(29));

        OrphanFileCleanupJob.Result result = job.purgeOrphans();
        assertThat(result.purged()).isGreaterThanOrEqualTo(1);
        assertThat(result.failed()).isZero();

        Map<String, Object> oldRow = row(owner, old.id());
        assertThat(oldRow.get("purged_at")).isNotNull();
        assertThat(storage.exists((String) oldRow.get("storage_key"))).isFalse();

        for (FileRef kept : new FileRef[] {young, linked}) {
            Map<String, Object> row = row(owner, kept.id());
            assertThat(row.get("purged_at")).as(kept.originalFilename()).isNull();
            assertThat(storage.exists((String) row.get("storage_key"))).isTrue();
        }

        // idempotente: a segunda execução já não encontra o órfão velho
        assertThat(job.purgeOrphans().purged()).isZero();
        assertThat(TenantContext.current().scope()).isEqualTo(TenantSnapshot.none().scope());
    }

    @Test
    void aFailureDoesNotStopTheBatch() throws IOException {
        JdbcTemplate owner = TestcontainersConfiguration.ownerJdbc(postgres);
        byte[] pdf = TestFiles.bytes("sample.pdf");
        FileRef broken = system(() -> documents.store(command(pdf)));
        FileRef fine = system(() -> documents.store(command(pdf)));
        // chave que o armazenamento recusa: falha só este ficheiro
        owner.update("update document.file set storage_key = ? where id = ?", "invalida/" + broken.id(), broken.id());
        clock.advance(Duration.ofDays(31));

        OrphanFileCleanupJob.Result result = job.purgeOrphans();

        assertThat(result.failed()).isEqualTo(1);
        assertThat(row(owner, broken.id()).get("purged_at")).isNull();
        assertThat(row(owner, fine.id()).get("purged_at")).isNotNull();
        owner.update("update document.file set purged_at = now() where id = ?", broken.id());
    }

    private static Map<String, Object> row(JdbcTemplate owner, UUID id) {
        return owner.queryForMap("select purged_at, storage_key from document.file where id = ?", id);
    }

    private static StoreFileCommand command(byte[] content) {
        return new StoreFileCommand(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(), "TEST_FILE",
                FileClassification.PERSONAL, null, "orfao.pdf", null, null, content);
    }

    private static <T> T system(Supplier<T> action) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.system(null));
        try {
            return action.get();
        } finally {
            TenantContext.set(previous);
        }
    }
}
