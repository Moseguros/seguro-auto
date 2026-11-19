package mz.co.moseguros.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
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
 * Row Level Security das tabelas do S6-04 (aplicação ligada como {@code moseguros_app_user}): em {@code CUSTOMER}
 * de A não se lêem nem alteram mensagens, preferências ou dispositivos de B; {@code NONE} não vê nada; os modelos
 * só em {@code SYSTEM}/{@code PLATFORM}; sem {@code delete}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationRlsIT {

    static final List<String> USER_TABLES = List.of("notification.message", "notification.preference",
            "notification.device");

    @Autowired
    NotificationApi notifications;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    JdbcTemplate jdbc;

    NotificationTestSupport support;
    Client a;
    Client b;

    @BeforeAll
    void seed() {
        support = new NotificationTestSupport(fixtures, identityApi);
        a = support.customer();
        b = support.customer();
        for (Client c : List.of(a, b)) {
            tx.execute(s -> notifications.notify(c.id(), "PROPOSAL_EXPIRED", Map.of("proposalNumber", "PR-2027-000777"),
                    NotificationCategory.PROPOSAL));
            support.preference(c.id(), "PROPOSAL", "SMS", false);
            support.device(c.id(), "rls-" + UUID.randomUUID());
        }
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

    List<UUID> usersIn(String table) {
        return jdbc.queryForList("select distinct user_id from " + table + " where user_id in (?, ?)", UUID.class,
                a.id(), b.id());
    }

    @Test
    void customerScopeSeesAndChangesOnlyItsOwnRows() {
        for (String table : USER_TABLES) {
            assertThat(inScope(TenantSnapshot.customer(a.id()), () -> usersIn(table))).as(table)
                    .containsExactly(a.id());
            assertThat(inScope(TenantSnapshot.customer(b.id()), () -> usersIn(table))).as(table)
                    .containsExactly(b.id());
            assertThat(inScope(TenantSnapshot.system(null), () -> usersIn(table))).as(table)
                    .containsExactlyInAnyOrder(a.id(), b.id());
        }
        int updated = inScope(TenantSnapshot.customer(a.id()), () -> jdbc.update(
                "update notification.message set read_at = now() where user_id = ? and channel = 'INBOX'", b.id())
                + jdbc.update("update notification.preference set enabled = true where user_id = ?", b.id())
                + jdbc.update("update notification.device set app_version = 'x' where user_id = ?", b.id()));
        assertThat(updated).isZero();
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.message where user_id = ? and read_at is not null""", Integer.class,
                b.id())).isZero();
    }

    @Test
    void noScopeSeesNothing() {
        for (String table : USER_TABLES) {
            assertThat(inScope(TenantSnapshot.none(), () -> usersIn(table))).as(table).isEmpty();
        }
        assertThat(inScope(TenantSnapshot.none(), () -> jdbc.queryForObject(
                "select count(*) from notification.template", Integer.class))).isZero();
    }

    @Test
    void templatesOnlyInSystemOrPlatform() {
        assertThat(inScope(TenantSnapshot.customer(a.id()), () -> jdbc.queryForObject(
                "select count(*) from notification.template", Integer.class))).isZero();
        assertThat(inScope(TenantSnapshot.org(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
                () -> jdbc.queryForObject("select count(*) from notification.template", Integer.class))).isZero();
        assertThat(inScope(TenantSnapshot.platform(UUID.randomUUID()), () -> jdbc.queryForObject(
                "select count(*) from notification.template", Integer.class))).isGreaterThanOrEqualTo(72);
        assertThat(inScope(TenantSnapshot.system(null), () -> jdbc.queryForObject(
                "select count(*) from notification.template", Integer.class))).isGreaterThanOrEqualTo(72);
    }

    @Test
    void theApplicationCannotDelete() {
        for (String table : List.of("notification.template", "notification.message", "notification.preference",
                "notification.device")) {
            boolean denied;
            try {
                inScope(TenantSnapshot.system(null), () -> jdbc.update("delete from " + table + " where false"));
                denied = false;
            } catch (RuntimeException e) {
                denied = true;
            }
            assertThat(denied).as(table).isTrue();
        }
    }
}
