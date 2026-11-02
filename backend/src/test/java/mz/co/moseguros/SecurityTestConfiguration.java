package mz.co.moseguros;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.identity.domain.UserRole;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Apoio aos testes de segurança: um relógio que os testes podem adiantar ({@link MutableClock},
 * {@code @Primary}) e a criação de contas, perfis e organizações ({@link IdentityFixtures}).
 * Usar sempre com {@link TestcontainersConfiguration}, para os testes partilharem o contexto.
 */
@TestConfiguration(proxyBeanMethods = false)
public class SecurityTestConfiguration {

    @Bean
    @Primary
    MutableClock mutableClock() {
        return new MutableClock();
    }

    @Bean
    IdentityFixtures identityFixtures(TransactionTemplate tx, UserAccountRepository accounts,
                                      CustomerProfileRepository profiles, UserRoleRepository userRoles,
                                      OrganizationRepository organizations, PostgreSQLContainer<?> postgres,
                                      PasswordEncoder passwordEncoder) {
        return new IdentityFixtures(tx, accounts, profiles, userRoles, organizations,
                TestcontainersConfiguration.ownerJdbc(postgres), passwordEncoder);
    }

    /** Relógio do sistema (UTC) com um avanço controlado pelo teste. */
    public static class MutableClock extends Clock {

        private final AtomicReference<Duration> offset = new AtomicReference<>(Duration.ZERO);

        public void advance(Duration duration) {
            offset.updateAndGet(current -> current.plus(duration));
        }

        public void reset() {
            offset.set(Duration.ZERO);
        }

        @Override
        public Instant instant() {
            return Instant.now().plus(offset.get());
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            throw new UnsupportedOperationException();
        }
    }

    /** Cria dados de identidade em âmbito {@code SYSTEM}; muda estados como dono das tabelas. */
    public static class IdentityFixtures {

        private final TransactionTemplate tx;
        private final UserAccountRepository accounts;
        private final CustomerProfileRepository profiles;
        private final UserRoleRepository userRoles;
        private final OrganizationRepository organizations;
        private final JdbcTemplate owner;
        private final PasswordEncoder passwordEncoder;

        IdentityFixtures(TransactionTemplate tx, UserAccountRepository accounts, CustomerProfileRepository profiles,
                         UserRoleRepository userRoles, OrganizationRepository organizations, JdbcTemplate owner,
                         PasswordEncoder passwordEncoder) {
            this.tx = tx;
            this.accounts = accounts;
            this.profiles = profiles;
            this.userRoles = userRoles;
            this.organizations = organizations;
            this.owner = owner;
            this.passwordEncoder = passwordEncoder;
        }

        /** Cliente activo com o perfil {@code CUSTOMER} e o nome indicado no perfil. */
        public UserAccount customer(String fullName) {
            UserAccount account = UserAccount.newCustomer(Msisdn.of("84" + digits(7)));
            CustomerProfile profile = CustomerProfile.emptyFor(account);
            profile.update(fullName, null, null, null, null, null, null);
            return inSystem(() -> {
                accounts.save(account);
                profiles.save(profile);
                userRoles.save(new UserRole(account.getId(), "CUSTOMER"));
                return account;
            });
        }

        /** Organização activa do tipo indicado. */
        public Organization organization(OrganizationType type, String tradeName) {
            Organization organization = Organization.create(type, tradeName + ", SA", tradeName, Nuit.of(digits(9)),
                    type.requiresLicense() ? "ASFPM-" + digits(4) : null, "geral" + digits(6) + "@teste.co.mz", null,
                    null, null);
            return inSystem(() -> organizations.save(organization));
        }

        /** Staff {@code ACTIVE} com os perfis indicados; {@code organizationId} {@code null} = plataforma. */
        public UserAccount staff(String displayName, UUID organizationId, String... roles) {
            UserAccount account = UserAccount.newStaff("staff" + digits(8) + "@teste.co.mz", displayName,
                    organizationId);
            inSystem(() -> {
                accounts.save(account);
                for (String role : roles) {
                    userRoles.save(new UserRole(account.getId(), role));
                }
                return account;
            });
            setAccountStatus(account.getId(), "ACTIVE");
            return account;
        }

        /**
         * Staff {@code ACTIVE} com palavra-passe e segredo TOTP (S1-05), como depois da activação;
         * {@code organizationId} {@code null} = plataforma. Nenhum passo TOTP foi usado ainda.
         */
        public UserAccount activeStaff(String displayName, UUID organizationId, String password, String totpSecret,
                                       String... roles) {
            UserAccount account = UserAccount.newStaff("staff" + digits(8) + "@teste.co.mz", displayName,
                    organizationId);
            account.setCredentials(displayName, passwordEncoder.encode(password), totpSecret);
            account.activate(0L);
            return inSystem(() -> {
                accounts.save(account);
                for (String role : roles) {
                    userRoles.save(new UserRole(account.getId(), role));
                }
                return account;
            });
        }

        public void setAccountStatus(UUID accountId, String status) {
            owner.update("update identity.user_account set status = ? where id = ?", status, accountId);
        }

        public void suspendOrganization(UUID organizationId) {
            owner.update("update organization.organization set status = 'SUSPENDED' where id = ?", organizationId);
        }

        public JdbcTemplate owner() {
            return owner;
        }

        private <T> T inSystem(Supplier<T> work) {
            TenantSnapshot previous = TenantContext.current();
            TenantContext.set(TenantSnapshot.system(null));
            try {
                return tx.execute(status -> work.get());
            } finally {
                TenantContext.set(previous);
            }
        }

        private static String digits(int count) {
            StringBuilder sb = new StringBuilder();
            sb.append(ThreadLocalRandom.current().nextInt(1, 10));
            for (int i = 1; i < count; i++) {
                sb.append(ThreadLocalRandom.current().nextInt(10));
            }
            return sb.toString();
        }
    }
}
