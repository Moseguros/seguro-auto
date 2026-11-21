package mz.co.moseguros.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Row Level Security das quatro tabelas do {@code policy} (S6-01 §3), com a aplicação ligada como
 * {@code moseguros_app_user}: {@code ORG} só a sua seguradora, {@code CUSTOMER} só as suas, {@code NONE}
 * nada; sem {@code delete}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyRlsIT {

    static final List<String> TABLES = List.of("policy.policy", "policy.instalment", "policy.certificate",
            "policy.policy_event");

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
    JdbcTemplate jdbc;

    PolicyTestSupport support;
    UUID insurerA;
    UUID insurerB;
    UUID customer1;
    UUID customer2;
    UUID policyA1;
    UUID policyB2;

    @BeforeAll
    void seed() {
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurerA = support.payments().insurer("Seguradora RLS A").id();
        insurerB = support.payments().insurer("Seguradora RLS B").id();
        customer1 = support.payments().customer().id();
        customer2 = support.payments().customer().id();
        Instant now = Instant.now();
        policyA1 = support.insert(Row.of(insurerA, customer1, PolicyTestSupport.plate(), "ACTIVE",
                now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(300))));
        policyB2 = support.insert(Row.of(insurerB, customer2, PolicyTestSupport.plate(), "ACTIVE",
                now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(300))));
        support.certificate(policyA1, "VALID", PolicyTestSupport.token());
        support.certificate(policyB2, "VALID", PolicyTestSupport.token());
    }

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

    List<UUID> policiesOf(String table) {
        String column = table.equals("policy.policy") ? "id" : "policy_id";
        return jdbc.queryForList("select " + column + " from " + table + " where " + column + " in (?, ?)",
                UUID.class, policyA1, policyB2);
    }

    @Test
    void orgScopeSeesOnlyItsInsurer() {
        for (String table : TABLES) {
            assertThat(inScope(TenantSnapshot.org(UUID.randomUUID(), insurerA, insurerA), () -> policiesOf(table)))
                    .as(table).containsOnly(policyA1).isNotEmpty();
            assertThat(inScope(TenantSnapshot.org(UUID.randomUUID(), insurerB, insurerB), () -> policiesOf(table)))
                    .as(table).containsOnly(policyB2).isNotEmpty();
            // mediador: ORG sem seguradora
            assertThat(inScope(TenantSnapshot.org(UUID.randomUUID(), UUID.randomUUID(), null),
                    () -> policiesOf(table))).as(table).isEmpty();
        }
        inScope(TenantSnapshot.org(UUID.randomUUID(), insurerA, insurerA), () -> {
            assertThat(jdbc.update("update policy.policy set status_reason = 'X' where id = ?", policyB2)).isZero();
            assertThat(jdbc.update("update policy.policy set status_reason = 'Y' where id = ?", policyA1)).isOne();
            return null;
        });
        assertThatThrownBy(() -> inScope(TenantSnapshot.org(UUID.randomUUID(), insurerA, insurerA), () ->
                jdbc.update("update policy.policy set insurer_id = ? where id = ?", insurerB, policyA1)))
                .rootCause().hasMessageContaining("row-level security");
    }

    @Test
    void customerScopeSeesOnlyTheirPolicies() {
        for (String table : TABLES) {
            assertThat(inScope(TenantSnapshot.customer(customer1), () -> policiesOf(table))).as(table)
                    .containsExactly(policyA1);
            assertThat(inScope(TenantSnapshot.customer(customer2), () -> policiesOf(table))).as(table)
                    .containsExactly(policyB2);
        }
    }

    @Test
    void noneScopeSeesNothingAndSystemSeesEverything() {
        for (String table : TABLES) {
            assertThat(inScope(TenantSnapshot.none(), () -> policiesOf(table))).as(table).isEmpty();
            assertThat(inScope(TenantSnapshot.system(null), () -> policiesOf(table))).as(table)
                    .containsExactlyInAnyOrder(policyA1, policyB2);
            assertThat(inScope(TenantSnapshot.platform(UUID.randomUUID()), () -> policiesOf(table))).as(table)
                    .hasSize(2);
        }
    }

    @Test
    void theApplicationCannotDelete() {
        for (String table : TABLES) {
            String column = table.equals("policy.policy") ? "id" : "policy_id";
            assertThatThrownBy(() -> inScope(TenantSnapshot.system(null), () ->
                    jdbc.update("delete from " + table + " where " + column + " = ?", policyA1)))
                    .as(table).rootCause().hasMessageContaining("permission denied");
        }
    }
}
