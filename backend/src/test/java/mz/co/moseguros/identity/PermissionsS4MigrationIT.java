package mz.co.moseguros.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

/** Permissões do Sprint 4 (S4-01 §1; migração {@code V202701010100}). */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PermissionsS4MigrationIT {

    private static final List<String> S4 = List.of("proposal:read", "proposal:decide", "kyc:review",
            "inspection:review", "proposal:create-for-client", "self:proposals");

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JwtDecoder jwtDecoder;

    @Test
    void eachRoleHasExactlyTheSprint4Permissions() {
        List<Map<String, Object>> rows = fixtures.owner().queryForList("""
                select role_code, permission, created_by from identity.role_permission
                 where permission in ('proposal:read','proposal:decide','kyc:review','inspection:review',
                                      'proposal:create-for-client','self:proposals')
                 order by role_code, permission""");
        assertThat(rows).extracting(r -> r.get("role_code") + " " + r.get("permission") + " " + r.get("created_by"))
                .containsExactly(
                        "AGENT proposal:create-for-client system",
                        "BROKER proposal:create-for-client system",
                        "CUSTOMER self:proposals system",
                        "INSURER_ADMIN kyc:review system",
                        "INSURER_ADMIN proposal:decide system",
                        "INSURER_ADMIN proposal:read system",
                        "UNDERWRITER inspection:review system",
                        "UNDERWRITER kyc:review system",
                        "UNDERWRITER proposal:decide system",
                        "UNDERWRITER proposal:read system");
    }

    @Test
    void previousPermissionsAreKept() {
        assertThat(fixtures.owner().queryForList("""
                select role_code || ' ' || permission from identity.role_permission
                 where role_code in ('UNDERWRITER','INSURER_ADMIN','CUSTOMER','BROKER')
                   and permission in ('product:read','rating:manage','self:profile','self:quotes',
                                      'quote:create-for-client')""", String.class))
                .contains("UNDERWRITER product:read", "INSURER_ADMIN product:read", "INSURER_ADMIN rating:manage",
                        "CUSTOMER self:profile", "CUSTOMER self:quotes", "BROKER quote:create-for-client");
    }

    @Test
    void tokensCarryTheNewPermissions() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Permissoes S4");
        UserAccount underwriter = fixtures.staff("Subscritor", insurer.getId(), "UNDERWRITER");
        UserAccount admin = fixtures.staff("Administrador", insurer.getId(), "INSURER_ADMIN");
        UserAccount finance = fixtures.staff("Financeiro", insurer.getId(), "FINANCE");
        Organization broker = fixtures.organization(OrganizationType.BROKER, "Corretora Permissoes S4");
        UserAccount brokerStaff = fixtures.staff("Corretor", broker.getId(), "BROKER");
        UserAccount customer = fixtures.customer("Cliente Propostas");

        assertThat(permissions(underwriter))
                .contains("proposal:read", "proposal:decide", "kyc:review", "inspection:review", "product:read");
        assertThat(permissions(admin)).contains("proposal:read", "proposal:decide", "kyc:review")
                .doesNotContain("inspection:review");
        assertThat(permissions(finance)).doesNotContainAnyElementsOf(S4);
        assertThat(permissions(brokerStaff)).contains("proposal:create-for-client", "quote:create-for-client")
                .doesNotContain("proposal:read", "kyc:review");
        assertThat(permissions(customer)).contains("self:proposals", "self:profile")
                .doesNotContain("proposal:create-for-client", "kyc:review");
    }

    private List<String> permissions(UserAccount account) {
        return jwtDecoder.decode(identityApi.issueTokens(account, null).accessToken()).getClaimAsStringList("permissions");
    }
}
