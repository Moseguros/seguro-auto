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

/** Permissões do Sprint 3 (S3-01 §1; migração {@code V202612150100}). */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PermissionsSeedS3IT {

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JwtDecoder jwtDecoder;

    @Test
    void theFiveRowsExist() {
        List<Map<String, Object>> rows = fixtures.owner().queryForList("""
                select role_code, permission, created_by from identity.role_permission
                 where permission in ('rating:manage','quote:create-for-client','self:quotes')
                 order by role_code, permission""");
        assertThat(rows).extracting(r -> r.get("role_code") + " " + r.get("permission") + " " + r.get("created_by"))
                .containsExactly(
                        "AGENT quote:create-for-client system",
                        "BROKER quote:create-for-client system",
                        "CUSTOMER self:quotes system",
                        "INSURER_ADMIN rating:manage system",
                        "PRODUCT_MANAGER rating:manage system");
    }

    @Test
    void tokensCarryTheNewPermissions() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Permissoes S3");
        UserAccount manager = fixtures.staff("Gestor", insurer.getId(), "PRODUCT_MANAGER");
        UserAccount underwriter = fixtures.staff("Subscritor", insurer.getId(), "UNDERWRITER");
        Organization broker = fixtures.organization(OrganizationType.BROKER, "Corretora Permissoes S3");
        UserAccount brokerStaff = fixtures.staff("Corretor", broker.getId(), "BROKER");
        UserAccount customer = fixtures.customer("Cliente Cotacoes");

        assertThat(permissions(manager)).contains("rating:manage", "product:read");
        assertThat(permissions(underwriter)).contains("product:read").doesNotContain("rating:manage");
        assertThat(permissions(brokerStaff)).contains("quote:create-for-client").doesNotContain("rating:manage");
        assertThat(permissions(customer)).contains("self:quotes").doesNotContain("quote:create-for-client");
    }

    private List<String> permissions(UserAccount account) {
        return jwtDecoder.decode(identityApi.issueTokens(account, null).accessToken()).getClaimAsStringList("permissions");
    }
}
