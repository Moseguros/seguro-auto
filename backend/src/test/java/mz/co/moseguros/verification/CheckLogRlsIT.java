package mz.co.moseguros.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import mz.co.moseguros.verification.domain.CheckLog;
import mz.co.moseguros.verification.domain.CheckLogRepository;
import mz.co.moseguros.verification.domain.CheckerType;
import mz.co.moseguros.verification.domain.SubjectType;
import mz.co.moseguros.verification.domain.VerificationResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * RLS de {@code verification.check_log} (S6-03 §1), com a aplicação ligada como {@code moseguros_app_user}:
 * {@code NONE} insere mas não lê; {@code ORG} só lê as consultas do próprio agente; {@code SYSTEM} e
 * {@code PLATFORM} lêem tudo; ninguém altera nem apaga.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class CheckLogRlsIT {

    @Autowired
    CheckLogRepository checkLogs;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    JdbcTemplate jdbc;

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    <T> T inScope(TenantSnapshot snapshot, Supplier<T> work) {
        TenantContext.set(snapshot);
        try {
            return tx.execute(status -> work.get());
        } finally {
            TenantContext.clear();
        }
    }

    static CheckLog publicCheck() {
        return CheckLog.record(SubjectType.TOKEN, "a".repeat(64), null, VerificationResult.NOT_FOUND, null, null,
                CheckerType.PUBLIC, null, null, "41.220.15.0/24", Instant.now());
    }

    static CheckLog policeCheck(UUID userId, UUID orgId) {
        return CheckLog.record(SubjectType.PLATE, "b".repeat(64), "ABC-***-MC", VerificationResult.NOT_FOUND, null,
                null, CheckerType.POLICE, userId, orgId, null, Instant.now());
    }

    List<UUID> visible(List<UUID> ids) {
        return jdbc.queryForList("select id from verification.check_log where id in (?, ?, ?)", UUID.class,
                ids.toArray());
    }

    @Test
    void noneScopeInsertsButCannotRead() {
        UUID id = inScope(TenantSnapshot.none(), () -> checkLogs.save(publicCheck()).getId());

        assertThat(inScope(TenantSnapshot.none(), () -> checkLogs.findById(id))).isEmpty();
        assertThat(inScope(TenantSnapshot.none(),
                () -> jdbc.queryForObject("select count(*) from verification.check_log", Long.class))).isZero();
        assertThat(inScope(TenantSnapshot.system(null), () -> checkLogs.findById(id))).isPresent();
    }

    @Test
    void anAgentReadsOnlyTheirOwnChecks() {
        UUID org = UUID.randomUUID();
        UUID agent1 = UUID.randomUUID();
        UUID agent2 = UUID.randomUUID();
        UUID mine = inScope(TenantSnapshot.org(agent1, org, null), () -> checkLogs.save(policeCheck(agent1, org)).getId());
        UUID theirs = inScope(TenantSnapshot.org(agent2, org, null),
                () -> checkLogs.save(policeCheck(agent2, org)).getId());
        UUID anonymous = inScope(TenantSnapshot.none(), () -> checkLogs.save(publicCheck()).getId());
        List<UUID> all = List.of(mine, theirs, anonymous);

        assertThat(inScope(TenantSnapshot.org(agent1, org, null), () -> visible(all))).containsExactly(mine);
        assertThat(inScope(TenantSnapshot.org(agent2, org, null), () -> visible(all))).containsExactly(theirs);
        assertThat(inScope(TenantSnapshot.customer(agent1), () -> visible(all))).isEmpty();
        assertThat(inScope(TenantSnapshot.system(null), () -> visible(all))).containsExactlyInAnyOrderElementsOf(all);
        assertThat(inScope(TenantSnapshot.platform(UUID.randomUUID()), () -> visible(all)))
                .containsExactlyInAnyOrderElementsOf(all);
    }

    @Test
    void updateAndDeleteAreDenied() {
        UUID id = inScope(TenantSnapshot.none(), () -> checkLogs.save(publicCheck()).getId());

        for (TenantSnapshot scope : List.of(TenantSnapshot.none(), TenantSnapshot.system(null))) {
            assertThatThrownBy(() -> inScope(scope, () ->
                    jdbc.update("update verification.check_log set result = 'VALID' where id = ?", id)))
                    .rootCause().hasMessageContaining("permission denied");
            assertThatThrownBy(() -> inScope(scope, () ->
                    jdbc.update("delete from verification.check_log where id = ?", id)))
                    .rootCause().hasMessageContaining("permission denied");
        }
    }

    @Test
    void theCheckerConstraintHoldsInTheDatabase() {
        assertThatThrownBy(() -> inScope(TenantSnapshot.system(null), () -> jdbc.update("""
                insert into verification.check_log
                  (id, created_at, created_by, updated_at, updated_by, subject_type, subject_hash, result,
                   checker_type, checker_user_id, checked_at)
                values (?, now(), 'test', now(), 'test', 'TOKEN', ?, 'NOT_FOUND', 'PUBLIC', ?, now())""",
                UUID.randomUUID(), "c".repeat(64), UUID.randomUUID())))
                .rootCause().hasMessageContaining("ck_check_log_checker");
    }
}
