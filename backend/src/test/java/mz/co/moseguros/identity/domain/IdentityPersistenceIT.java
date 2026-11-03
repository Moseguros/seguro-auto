package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpChallenge.Purpose;
import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.InsurerProfileRepository;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * As entidades de identidade e organizações gravam e lêem contra as migrações reais
 * ({@code ddl-auto: validate}), com a aplicação ligada como {@code moseguros_app_user}.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class IdentityPersistenceIT {

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    UserAccountRepository accounts;

    @Autowired
    CustomerProfileRepository profiles;

    @Autowired
    RoleRepository roles;

    @Autowired
    UserRoleRepository userRoles;

    @Autowired
    OtpSettingsRepository otpSettings;

    @Autowired
    OtpChallengeRepository challenges;

    @Autowired
    RefreshTokenRepository refreshTokens;

    @Autowired
    InvitationRepository invitations;

    @Autowired
    PasswordResetRepository passwordResets;

    @Autowired
    OrganizationRepository organizations;

    @Autowired
    InsurerProfileRepository insurerProfiles;

    @AfterEach
    void clearContext() {
        TenantContext.clear();
    }

    @Test
    void customerNuitAndDocumentAreEncryptedInTheDatabase() {
        String nuit = nineDigits();
        UserAccount customer = UserAccount.newCustomer(randomMsisdn());
        CustomerProfile profile = CustomerProfile.emptyFor(customer);
        profile.update("Ana Silva", LocalDate.of(1990, 5, 1), IdDocType.BI, "110100123456B", nuit,
                "Av. 24 de Julho", Province.MAPUTO_CITY);
        inScope(TenantSnapshot.system(null), () -> {
            accounts.save(customer);
            return profiles.save(profile);
        });

        // Consulta SQL directa (como dono): só bytes cifrados.
        JdbcTemplate owner = TestcontainersConfiguration.ownerJdbc(postgres);
        byte[] nuitEnc = owner.queryForObject("select nuit_enc from identity.customer_profile where user_id = ?",
                byte[].class, customer.getId());
        byte[] docEnc = owner.queryForObject(
                "select id_doc_number_enc from identity.customer_profile where user_id = ?",
                byte[].class, customer.getId());
        assertThat(new String(nuitEnc, StandardCharsets.ISO_8859_1)).doesNotContain(nuit);
        assertThat(new String(docEnc, StandardCharsets.ISO_8859_1)).doesNotContain("110100123456B");
        assertThat(nuitEnc[0]).isEqualTo((byte) 1);
        assertThat(owner.queryForObject("""
                select position(convert_to(?, 'UTF8') in nuit_enc) from identity.customer_profile where user_id = ?""",
                Integer.class, nuit, customer.getId())).isZero();

        CustomerProfile read = inScope(TenantSnapshot.customer(customer.getId()),
                () -> profiles.findByUserId(customer.getId()).orElseThrow());
        assertThat(read.getNuit()).isEqualTo(nuit);
        assertThat(read.getIdDocNumber()).isEqualTo("110100123456B");
        assertThat(read.isComplete()).isTrue();
    }

    @Test
    void staffAccountWithEncryptedTotpSecretAndRoles() {
        UserAccount staff = UserAccount.newStaff("staff" + nineDigits() + "@moseguros.co.mz", "Staff", null);
        org.springframework.test.util.ReflectionTestUtils.setField(staff, "totpSecret", "JBSWY3DPEHPK3PXP");
        inScope(TenantSnapshot.platform(null), () -> {
            accounts.save(staff);
            return userRoles.save(new UserRole(staff.getId(), "PLATFORM_ADMIN"));
        });

        JdbcTemplate owner = TestcontainersConfiguration.ownerJdbc(postgres);
        byte[] secret = owner.queryForObject("select totp_secret_enc from identity.user_account where id = ?",
                byte[].class, staff.getId());
        assertThat(new String(secret, StandardCharsets.ISO_8859_1)).doesNotContain("JBSWY3DPEHPK3PXP");
        assertThat(owner.queryForObject("select created_by from identity.user_role where user_id = ?",
                String.class, staff.getId())).isEqualTo("system");

        inScope(TenantSnapshot.platform(null), () -> {
            UserAccount read = accounts.findByTypeAndEmail(AccountType.STAFF, staff.getEmail()).orElseThrow();
            assertThat(read.getTotpSecret()).isEqualTo("JBSWY3DPEHPK3PXP");
            assertThat(read.getStatus()).isEqualTo(AccountStatus.PENDING_ACTIVATION);
            List<UserRole> assigned = userRoles.findByUserId(staff.getId());
            assertThat(assigned).extracting(UserRole::getRoleCode).containsExactly("PLATFORM_ADMIN");
            // A aplicação pode apagar perfis atribuídos (única excepção ao "sem delete").
            userRoles.deleteAll(assigned);
            userRoles.flush();
            assertThat(userRoles.findByUserId(staff.getId())).isEmpty();
            return null;
        });
    }

    @Test
    void seededRolesAndPermissions() {
        inScope(TenantSnapshot.none(), () -> {
            // Tabelas de referência sem RLS: visíveis mesmo sem âmbito.
            assertThat(roles.count()).isEqualTo(18);
            assertThat(roles.findByCode("PLATFORM_ADMIN").orElseThrow().getOrgTypes()).isNull();
            assertThat(roles.findByCode("ORG_ADMIN").orElseThrow().getOrgTypes())
                    .containsExactly("BROKER", "AGENT", "WORKSHOP", "ASSESSOR", "ASSISTANCE", "OMBUDSMAN",
                            "REGULATOR", "POLICE", "CORPORATE_CLIENT");
            assertThat(roles.findByCode("INSURER_ADMIN").orElseThrow().isAssignableIn("INSURER")).isTrue();
            assertThat(roles.findByCode("INSURER_ADMIN").orElseThrow().isAssignableIn("BROKER")).isFalse();
            assertThat(roles.findPermissionsByRoleCodes(List.of("PLATFORM_ADMIN"))).containsExactlyInAnyOrder(
                    "otp-settings:read", "otp-settings:manage", "organization:read", "organization:manage",
                    "org-user:read", "org-user:manage", "reference:manage"); // reference:manage: S2-01
            assertThat(roles.findPermissionsByRoleCodes(List.of("CUSTOMER")))
                    .containsExactlyInAnyOrder("self:profile", "self:vehicles");
            assertThat(roles.findPermissionsByRoleCodes(List.of("UNDERWRITER"))).containsExactly("product:read");
            return null;
        });
    }

    @Test
    void otpSettingsSeedAndUpdate() {
        inScope(TenantSnapshot.platform(null), () -> {
            OtpSettings settings = otpSettings.findFirstBy().orElseThrow();
            assertThat(settings.getEnabledChannels()).containsExactly(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL);
            assertThat(settings.getDefaultChannel()).isEqualTo(Channel.SMS);
            assertThat(settings.isCustomerCanChoose()).isTrue();
            assertThat(settings.getFallbackOrder()).containsExactly(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL);
            assertThat(settings.getCodeLength()).isEqualTo(6);
            assertThat(settings.getValiditySeconds()).isEqualTo(300);
            assertThat(settings.getMaxAttempts()).isEqualTo(3);
            assertThat(settings.getResendAfterSeconds()).isEqualTo(60);
            assertThat(settings.getMaxRequestsPerHour()).isEqualTo(5);
            assertThat(settings.getCreatedBy()).isEqualTo("system");

            settings.update(List.of(Channel.SMS, Channel.EMAIL), Channel.EMAIL, false,
                    List.of(Channel.EMAIL, Channel.SMS), 6, 300, 3, 60, 5);
            otpSettings.flush();
            OtpSettings reread = otpSettings.findFirstBy().orElseThrow();
            assertThat(reread.getFallbackOrder()).containsExactly(Channel.EMAIL, Channel.SMS);

            // Repor os valores iniciais para os outros testes.
            reread.update(List.of(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL), Channel.SMS, true,
                    List.of(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL), 6, 300, 3, 60, 5);
            return null;
        });
    }

    @Test
    void organizationsAndInsurerProfile() {
        Organization insurer = Organization.create(OrganizationType.INSURER, "Seguradora Teste, SA", "Seg Teste",
                Nuit.of(nineDigits()), "ASFPM-1", "geral@segteste.mz", "+258 21 000 000", "Maputo", Province.MAPUTO);
        Organization ombudsman = Organization.create(OrganizationType.OMBUDSMAN, "Provedor", null,
                Nuit.of(nineDigits()), null, "provedor@exemplo.mz", null, null, null);
        String shortCode = "S" + nineDigits().substring(0, 6);
        inScope(TenantSnapshot.platform(null), () -> {
            organizations.save(insurer);
            organizations.save(ombudsman);
            return insurerProfiles.save(InsurerProfile.create(insurer, shortCode, "rec@segteste.mz", ombudsman));
        });

        inScope(TenantSnapshot.org(null, insurer.getId(), insurer.getId()), () -> {
            Organization read = organizations.findByNuit(insurer.getNuit()).orElseThrow();
            assertThat(read.getContactPhone()).isEqualTo("+25821000000");
            assertThat(read.getVersion()).isZero();
            read.suspend();
            organizations.flush();
            assertThat(insurerProfiles.findByOrganizationId(insurer.getId()).orElseThrow().getOmbudsmanOrgId())
                    .isEqualTo(ombudsman.getId());
            // O provedor é outra organização: invisível em âmbito ORG.
            assertThat(organizations.findById(ombudsman.getId())).isEmpty();
            return null;
        });
        assertThat(TestcontainersConfiguration.ownerJdbc(postgres).queryForObject(
                "select status from organization.organization where id = ?", String.class, insurer.getId()))
                .isEqualTo("SUSPENDED");
    }

    @Test
    void systemOnlyTablesRoundTrip() {
        UserAccount customer = UserAccount.newCustomer("cliente" + nineDigits() + "@exemplo.mz");
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        String identifier = customer.getEmail();
        UUID rootId = inScope(TenantSnapshot.system(null), () -> {
            accounts.save(customer);
            OtpChallenge first = challenges.save(OtpChallenge.issue(identifier, IdentifierType.EMAIL, Channel.EMAIL,
                    Purpose.LOGIN, null, hash(), now.plusSeconds(300), null));
            challenges.save(OtpChallenge.issue(identifier, IdentifierType.EMAIL, Channel.EMAIL, Purpose.LOGIN, null,
                    hash(), now.plusSeconds(300), first));
            RefreshToken token = RefreshToken.issue(customer.getId(), UUID.randomUUID(), hash(), now.plusSeconds(60),
                    null, "JUnit");
            token.revoke(RevokedReason.LOGOUT, now, null);
            refreshTokens.save(token);
            passwordResets.save(PasswordReset.create(customer.getId(), hash(), now.plusSeconds(1800)));
            return first.getId();
        });

        inScope(TenantSnapshot.system(null), () -> {
            List<OtpChallenge> chain = challenges.findByIdentifierOrderByCreatedAtDesc(identifier);
            assertThat(chain).hasSize(2);
            assertThat(chain).extracting(OtpChallenge::getRootChallengeId).containsOnly(rootId);
            assertThat(challenges.findByRootChallengeId(rootId)).hasSize(2);
            assertThat(refreshTokens.findByUserIdAndRevokedAtIsNull(customer.getId())).isEmpty();
            assertThat(passwordResets.findByUserIdAndUsedAtIsNullAndInvalidatedAtIsNull(customer.getId())).hasSize(1);
            return null;
        });
        // Fora de SYSTEM, nada disto é visível.
        inScope(TenantSnapshot.platform(null), () -> {
            assertThat(challenges.findByIdentifierOrderByCreatedAtDesc(identifier)).isEmpty();
            assertThat(passwordResets.findByUserIdAndUsedAtIsNullAndInvalidatedAtIsNull(customer.getId())).isEmpty();
            return null;
        });
    }

    @Test
    void invitationWithRoleArray() {
        UUID orgId = UUID.randomUUID();
        Invitation invitation = Invitation.create("convite" + nineDigits() + "@exemplo.mz", "Convidado", orgId,
                List.of("ORG_ADMIN", "BROKER"), hash(), Instant.now().plusSeconds(3600), null);
        inScope(TenantSnapshot.org(null, orgId, null), () -> invitations.save(invitation));
        inScope(TenantSnapshot.org(null, orgId, null), () -> {
            assertThat(invitations.findByTokenHash(invitation.getTokenHash()).orElseThrow().getRoleCodes())
                    .containsExactly("ORG_ADMIN", "BROKER");
            return null;
        });
    }

    @Test
    void databaseConstraintsBackTheDomain() {
        // Cliente sem contacto
        assertThatThrownBy(() -> jdbc.update("""
                insert into identity.user_account (id, created_at, created_by, updated_at, updated_by, type, status)
                values (gen_random_uuid(), now(), 'x', now(), 'x', 'CUSTOMER', 'ACTIVE')"""))
                .isInstanceOf(DataAccessException.class);
        // Staff com telemóvel
        JdbcTemplate owner = TestcontainersConfiguration.ownerJdbc(postgres);
        assertThatThrownBy(() -> owner.update("""
                insert into identity.user_account (id, created_at, created_by, updated_at, updated_by, type, email,
                  msisdn, status)
                values (gen_random_uuid(), now(), 'x', now(), 'x', 'STAFF', 'a@b.mz', '258841234567', 'ACTIVE')"""))
                .hasMessageContaining("check");
        // Canal por defeito fora dos activos
        assertThatThrownBy(() -> owner.update(
                "update identity.otp_settings set default_channel = 'EMAIL', enabled_channels = '{SMS}', "
                        + "fallback_order = '{SMS}'"))
                .hasMessageContaining("check");
    }

    private <T> T inScope(TenantSnapshot snapshot, Supplier<T> work) {
        TenantContext.set(snapshot);
        try {
            return tx.execute(status -> work.get());
        } finally {
            TenantContext.clear();
        }
    }

    private static Msisdn randomMsisdn() {
        return Msisdn.of("84" + nineDigits().substring(2));
    }

    private static String nineDigits() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
    }

    private static String hash() {
        return (UUID.randomUUID().toString() + UUID.randomUUID()).replace("-", "");
    }
}
