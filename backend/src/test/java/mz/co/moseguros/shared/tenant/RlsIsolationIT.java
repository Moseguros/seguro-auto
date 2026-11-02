package mz.co.moseguros.shared.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.Invitation;
import mz.co.moseguros.identity.domain.InvitationRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.domain.Ids;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Isolamento por âmbito com Row Level Security (ADR-002, adenda), com a aplicação ligada como
 * {@code moseguros_app_user}. Os dados de cada teste são inseridos pelo dono (sem RLS).
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, RlsIsolationIT.Probes.class})
@ActiveProfiles("test")
class RlsIsolationIT {

    @TestConfiguration(proxyBeanMethods = false)
    static class Probes {
        @Bean
        SystemProbe systemProbe(JdbcTemplate jdbc) {
            return new SystemProbe(jdbc);
        }
    }

    /** Bean com métodos {@code @RunAsSystem} (o aspecto só se aplica entre beans). */
    static class SystemProbe {

        private final JdbcTemplate jdbc;

        SystemProbe(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @RunAsSystem
        @Transactional(readOnly = true)
        public List<UUID> organizationIds() {
            return jdbc.queryForList("select id from organization.organization", UUID.class);
        }

        @RunAsSystem
        @Transactional(readOnly = true)
        public String scopeSeenByTheDatabase() {
            return jdbc.queryForObject("select current_setting('app.scope', true)", String.class);
        }

        @RunAsSystem
        public int invitationsFor(String email) {
            return jdbc.queryForObject("select count(*) from identity.invitation where email = ?", Integer.class, email);
        }

        @RunAsSystem
        public void fail() {
            throw new IllegalStateException("boom");
        }
    }

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    OrganizationRepository organizations;

    @Autowired
    UserAccountRepository accounts;

    @Autowired
    CustomerProfileRepository profiles;

    @Autowired
    InvitationRepository invitations;

    @Autowired
    SystemProbe probe;

    JdbcTemplate owner;

    // Dados do teste
    UUID orgA;
    UUID orgB;
    UUID staffA;
    UUID staffB;
    UUID invitationA;
    UUID invitationB;
    UUID customer1;
    UUID customer2;
    UUID profile1;
    UUID profile2;
    UUID challenge;

    @BeforeEach
    void seed() {
        owner = TestcontainersConfiguration.ownerJdbc(postgres);
        orgA = org("INSURER");
        orgB = org("BROKER");
        staffA = staff(orgA);
        staffB = staff(orgB);
        invitationA = invitation(orgA);
        invitationB = invitation(orgB);
        customer1 = customer();
        customer2 = customer();
        profile1 = profile(customer1);
        profile2 = profile(customer2);
        challenge = otpChallenge();
    }

    @AfterEach
    void clearContext() {
        TenantContext.clear();
    }

    @Test
    void applicationUserIsNotOwnerNorSuperuserNorBypassesRls() {
        Map<String, Object> role = jdbc.queryForMap("""
                select current_user as name, rolsuper, rolbypassrls
                  from pg_roles where rolname = current_user""");
        assertThat(role.get("name")).isEqualTo("moseguros_app_user");
        assertThat(role.get("rolsuper")).isEqualTo(false);
        assertThat(role.get("rolbypassrls")).isEqualTo(false);
        assertThat(jdbc.queryForObject("""
                select count(*) from pg_tables
                 where schemaname in ('identity','organization') and tableowner = current_user""", Integer.class))
                .isZero();
    }

    @Nested
    class OrgScope {

        @Test
        void organizationsOfOtherOrgAreNeitherReadNorChanged() {
            inScope(TenantSnapshot.org(staffA, orgA, orgA), () -> {
                assertThat(organizations.findAll()).extracting(Organization::getId).containsExactly(orgA);
                assertThat(organizations.findById(orgB)).isEmpty();
                assertThat(jdbc.update("update organization.organization set legal_name = 'X' where id = ?", orgB))
                        .isZero();
                assertThat(jdbc.update("update organization.organization set address = 'Maputo' where id = ?", orgA))
                        .isOne();
                return null;
            });
            assertThat(owner.queryForObject("select legal_name from organization.organization where id = ?",
                    String.class, orgB)).isNotEqualTo("X");
        }

        @Test
        void accountsOfOtherOrgAreNeitherReadNorChanged() {
            inScope(TenantSnapshot.org(staffA, orgA, orgA), () -> {
                assertThat(accounts.findAll()).extracting(UserAccount::getId).containsExactly(staffA);
                assertThat(accounts.findById(staffB)).isEmpty();
                assertThat(accounts.findById(customer1)).isEmpty();
                assertThat(jdbc.update("update identity.user_account set display_name = 'X' where id = ?", staffB))
                        .isZero();
                assertThat(jdbc.update("update identity.user_account set display_name = 'Y' where id = ?", staffA))
                        .isOne();
                return null;
            });
        }

        @Test
        void invitationsOfOtherOrgAreNeitherReadNorChanged() {
            inScope(TenantSnapshot.org(staffA, orgA, orgA), () -> {
                assertThat(invitations.findAll()).extracting(Invitation::getId).containsExactly(invitationA);
                assertThat(jdbc.update("update identity.invitation set full_name = 'X' where id = ?", invitationB))
                        .isZero();
                return null;
            });
        }

        @Test
        void cannotInsertRowsForAnotherOrg() {
            assertThatThrownBy(() -> inScope(TenantSnapshot.org(staffA, orgA, orgA), () ->
                    invitations.saveAndFlush(Invitation.create("x" + nuit() + "@b.mz", "X", orgB, List.of("ORG_ADMIN"),
                            hash(), java.time.Instant.now().plusSeconds(60), staffA))))
                    .isInstanceOf(DataAccessException.class)
                    .rootCause().hasMessageContaining("row-level security");
        }

        @Test
        void cannotMoveOwnRowToAnotherOrg() {
            assertThatThrownBy(() -> inScope(TenantSnapshot.org(staffA, orgA, orgA), () ->
                    jdbc.update("update identity.invitation set organization_id = ? where id = ?", orgB, invitationA)))
                    .rootCause().hasMessageContaining("row-level security");
        }

        @Test
        void otpChallengesAreInvisible() {
            assertThat(countIn(TenantSnapshot.org(staffA, orgA, orgA), "identity.otp_challenge")).isZero();
        }
    }

    @Test
    void noneScopeSeesNothing() {
        TenantSnapshot none = TenantSnapshot.none();
        for (String table : List.of("organization.organization", "organization.insurer_profile",
                "identity.user_account", "identity.customer_profile", "identity.user_role", "identity.invitation",
                "identity.otp_settings", "identity.otp_challenge", "identity.refresh_token",
                "identity.password_reset")) {
            assertThat(countIn(none, table)).as(table).isZero();
        }
        // Sem transacção também não há âmbito.
        assertThat(jdbc.queryForObject("select count(*) from organization.organization", Integer.class)).isZero();
    }

    @Test
    void platformScopeSeesEverything() {
        inScope(TenantSnapshot.platform(UUID.randomUUID()), () -> {
            assertThat(organizations.findAll()).extracting(Organization::getId).contains(orgA, orgB);
            assertThat(accounts.findAll()).extracting(UserAccount::getId)
                    .contains(staffA, staffB, customer1, customer2);
            assertThat(invitations.findAll()).extracting(Invitation::getId).contains(invitationA, invitationB);
            assertThat(jdbc.queryForObject("select count(*) from identity.customer_profile where id in (?, ?)",
                    Integer.class, profile1, profile2)).isEqualTo(2);
            assertThat(jdbc.queryForObject("select count(*) from identity.otp_settings", Integer.class)).isOne();
            return null;
        });
        assertThat(countIn(TenantSnapshot.platform(null), "identity.otp_challenge")).isZero();
    }

    @Test
    void customerSeesOnlyOwnAccountAndProfile() {
        inScope(TenantSnapshot.customer(customer1), () -> {
            assertThat(accounts.findAll()).extracting(UserAccount::getId).containsExactly(customer1);
            assertThat(jdbc.queryForList("select id from identity.customer_profile", UUID.class))
                    .containsExactly(profile1);
            assertThat(profiles.findByUserId(customer2)).isEmpty();
            assertThat(profiles.findByUserId(customer1)).isPresent();
            assertThat(organizations.findAll()).isEmpty();
            assertThat(invitations.findAll()).isEmpty();
            assertThat(jdbc.update("update identity.user_account set preferred_language = 'en' where id = ?",
                    customer2)).isZero();
            return null;
        });
        assertThat(countIn(TenantSnapshot.customer(customer1), "identity.otp_challenge")).isZero();
        assertThat(countIn(TenantSnapshot.customer(customer1), "identity.otp_settings")).isZero();
    }

    @Test
    void otpChallengeIsVisibleOnlyInSystemScope() {
        assertThat(inScope(TenantSnapshot.system(null), () -> jdbc.queryForList(
                "select id from identity.otp_challenge where id = ?", UUID.class, challenge)))
                .containsExactly(challenge);
        for (TenantSnapshot other : List.of(TenantSnapshot.none(), TenantSnapshot.platform(null),
                TenantSnapshot.org(staffA, orgA, orgA), TenantSnapshot.customer(customer1))) {
            assertThat(countIn(other, "identity.otp_challenge")).as(other.scope().name()).isZero();
        }
    }

    @Test
    void applicationCannotDeleteAccounts() {
        assertThatThrownBy(() -> inScope(TenantSnapshot.platform(null), () ->
                jdbc.update("delete from identity.user_account where id = ?", customer2)))
                .isInstanceOf(DataAccessException.class)
                .rootCause().hasMessageContaining("permission denied");
        assertThat(owner.queryForObject("select count(*) from identity.user_account where id = ?",
                Integer.class, customer2)).isOne();
    }

    @Test
    void applicationCannotDeleteOrganizationsNorChangeRoles() {
        assertThatThrownBy(() -> inScope(TenantSnapshot.platform(null), () ->
                jdbc.update("delete from organization.organization where id = ?", orgB)))
                .rootCause().hasMessageContaining("permission denied");
        assertThatThrownBy(() -> inScope(TenantSnapshot.platform(null), () ->
                jdbc.update("update identity.role set org_types = null where code = 'INSURER_ADMIN'")))
                .rootCause().hasMessageContaining("permission denied");
        assertThatThrownBy(() -> inScope(TenantSnapshot.platform(null), () ->
                jdbc.update("insert into identity.role_permission (role_code, permission, created_at, created_by) "
                        + "values ('CUSTOMER', 'x:y', now(), 'x')")))
                .rootCause().hasMessageContaining("permission denied");
    }

    @Nested
    class RunAsSystemBehaviour {

        @Test
        void outsideTransactionTheNextTransactionStartsInSystemScope() {
            TenantContext.set(TenantSnapshot.org(staffA, orgA, orgA));

            assertThat(probe.organizationIds()).contains(orgA, orgB);
            assertThat(probe.scopeSeenByTheDatabase()).isEqualTo("SYSTEM");
            assertThat(TenantContext.current()).isEqualTo(TenantSnapshot.org(staffA, orgA, orgA));
        }

        @Test
        void insideOrgTransactionSeesOtherOrgThenGoesBack() {
            inScope(TenantSnapshot.org(staffA, orgA, orgA), () -> {
                assertThat(organizationIdsHere()).containsExactly(orgA);

                assertThat(probe.organizationIds()).contains(orgA, orgB);

                assertThat(organizationIdsHere()).containsExactly(orgA);
                assertThat(jdbc.queryForObject("select current_setting('app.scope', true)", String.class))
                        .isEqualTo("ORG");
                assertThat(TenantContext.current().scope()).isEqualTo(Scope.ORG);
                return null;
            });
        }

        @Test
        void keepsTheUserAndFlushesPendingChangesFirst() {
            String email = "pendente" + nuit() + "@exemplo.mz";
            inScope(TenantSnapshot.org(staffA, orgA, orgA), () -> {
                invitations.save(Invitation.create(email, "Pendente", orgA, List.of("ORG_ADMIN"), hash(),
                        java.time.Instant.now().plusSeconds(60), staffA));
                // Ainda não gravado: o aspecto faz flush antes de mudar de âmbito.
                assertThat(probe.invitationsFor(email)).isOne();
                assertThat(jdbc.queryForObject("select current_setting('app.user_id', true)", String.class))
                        .isEqualTo(staffA.toString());
                return null;
            });
        }

        @Test
        void restoresScopeWhenTheMethodFails() {
            inScope(TenantSnapshot.org(staffA, orgA, orgA), () -> {
                assertThatThrownBy(probe::fail).isInstanceOf(IllegalStateException.class);
                assertThat(organizationIdsHere()).containsExactly(orgA);
                return null;
            });
        }

        private List<UUID> organizationIdsHere() {
            return jdbc.queryForList("select id from organization.organization", UUID.class);
        }
    }

    // --- auxiliares ---

    private <T> T inScope(TenantSnapshot snapshot, Supplier<T> work) {
        TenantContext.set(snapshot);
        try {
            return tx.execute(status -> work.get());
        } finally {
            TenantContext.clear();
        }
    }

    private int countIn(TenantSnapshot snapshot, String table) {
        return inScope(snapshot, () -> jdbc.queryForObject("select count(*) from " + table, Integer.class));
    }

    private UUID org(String type) {
        UUID id = Ids.newId();
        owner.update("""
                insert into organization.organization
                  (id, created_at, created_by, updated_at, updated_by, type, legal_name, nuit, license_number, contact_email)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, 'L-1', 'geral@org.mz')""",
                id, type, "Org " + id, nuit());
        return id;
    }

    private UUID staff(UUID orgId) {
        UUID id = Ids.newId();
        owner.update("""
                insert into identity.user_account
                  (id, created_at, created_by, updated_at, updated_by, type, email, display_name, status, organization_id)
                values (?, now(), 'test', now(), 'test', 'STAFF', ?, 'Staff', 'ACTIVE', ?)""",
                id, "staff" + nuit() + "@org.mz", orgId);
        return id;
    }

    private UUID customer() {
        UUID id = Ids.newId();
        owner.update("""
                insert into identity.user_account
                  (id, created_at, created_by, updated_at, updated_by, type, msisdn, msisdn_verified, status)
                values (?, now(), 'test', now(), 'test', 'CUSTOMER', ?, true, 'ACTIVE')""",
                id, "25884" + nuit().substring(2));
        return id;
    }

    private UUID profile(UUID userId) {
        UUID id = Ids.newId();
        owner.update("""
                insert into identity.customer_profile (id, created_at, created_by, updated_at, updated_by, user_id)
                values (?, now(), 'test', now(), 'test', ?)""", id, userId);
        return id;
    }

    private UUID invitation(UUID orgId) {
        UUID id = Ids.newId();
        owner.update("""
                insert into identity.invitation
                  (id, created_at, created_by, updated_at, updated_by, email, full_name, organization_id, role_codes,
                   token_hash, expires_at)
                values (?, now(), 'test', now(), 'test', ?, 'Convidado', ?, '{ORG_ADMIN}', ?, now() + interval '1 day')""",
                id, "conv" + nuit() + "@org.mz", orgId, hash());
        return id;
    }

    private UUID otpChallenge() {
        UUID id = Ids.newId();
        owner.update("""
                insert into identity.otp_challenge
                  (id, created_at, created_by, updated_at, updated_by, identifier, identifier_type, channel, purpose,
                   code_hash, expires_at, delivery_status, root_challenge_id)
                values (?, now(), 'test', now(), 'test', '258841234567', 'MSISDN', 'SMS', 'LOGIN', ?,
                        now() + interval '5 minutes', 'PENDING', ?)""", id, hash(), id);
        return id;
    }

    private static String nuit() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
    }

    private static String hash() {
        return (UUID.randomUUID().toString() + UUID.randomUUID()).replace("-", "");
    }
}
