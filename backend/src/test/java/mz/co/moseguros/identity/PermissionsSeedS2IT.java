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

/** Permissões do Sprint 2 (S2-01 §8; migração {@code V202612010101}). */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PermissionsSeedS2IT {

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JwtDecoder jwtDecoder;

    @Test
    void theSevenRowsExist() {
        List<Map<String, Object>> rows = fixtures.owner().queryForList("""
                select role_code, permission from identity.role_permission
                 where permission in ('reference:manage','product:read','product:manage','self:vehicles')
                 order by role_code, permission""");
        assertThat(rows).extracting(r -> r.get("role_code") + " " + r.get("permission")).containsExactly(
                "CUSTOMER self:vehicles",
                "INSURER_ADMIN product:manage",
                "INSURER_ADMIN product:read",
                "PLATFORM_ADMIN reference:manage",
                "PRODUCT_MANAGER product:manage",
                "PRODUCT_MANAGER product:read",
                "UNDERWRITER product:read");
        assertThat(fixtures.owner().queryForObject("""
                select count(*) from identity.role_permission
                 where permission in ('reference:manage','product:read','product:manage','self:vehicles')
                   and created_by = 'system'""", Integer.class)).isEqualTo(7);
    }

    @Test
    void underwriterTokenHasProductReadButNotProductManage() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Permissoes");
        UserAccount underwriter = fixtures.staff("Subscritor", insurer.getId(), "UNDERWRITER");

        List<String> permissions = jwtDecoder.decode(identityApi.issueTokens(underwriter, null).accessToken())
                .getClaimAsStringList("permissions");

        assertThat(permissions).contains("product:read").doesNotContain("product:manage");
    }

    @Test
    void customerTokenHasSelfVehicles() {
        UserAccount customer = fixtures.customer("Cliente Veiculos");
        List<String> permissions = jwtDecoder.decode(identityApi.issueTokens(customer, null).accessToken())
                .getClaimAsStringList("permissions");
        assertThat(permissions).contains("self:profile", "self:vehicles");
    }
}
