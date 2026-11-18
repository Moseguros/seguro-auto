package mz.co.moseguros.identity;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

/** Permissões do Sprint 5 (S5-01 §1; migração {@code V202701150100}). */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PermissionsS5MigrationIT {

    private static final List<String> S5 = List.of("payment:configure", "payment:read", "payment:resolve-exception",
            "payment:link-for-client", "self:payments", "platform:operations");

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JwtDecoder jwtDecoder;

    @Test
    void theMigrationInsertsTheNineRows() {
        List<Map<String, Object>> rows = fixtures.owner().queryForList("""
                select role_code, permission, created_by from identity.role_permission
                 where permission in ('payment:configure','payment:read','payment:resolve-exception',
                                      'payment:link-for-client','self:payments','platform:operations')
                 order by role_code, permission""");
        assertThat(rows).extracting(r -> r.get("role_code") + " " + r.get("permission") + " " + r.get("created_by"))
                .containsExactly(
                        "AGENT payment:link-for-client system",
                        "BROKER payment:link-for-client system",
                        "CUSTOMER self:payments system",
                        "FINANCE payment:configure system",
                        "FINANCE payment:read system",
                        "FINANCE payment:resolve-exception system",
                        "INSURER_ADMIN payment:configure system",
                        "INSURER_ADMIN payment:read system",
                        "PLATFORM_ADMIN platform:operations system");
    }

    @Test
    void tokensAndMeCarryTheNewPermissions() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Permissoes S5");
        UserAccount finance = fixtures.staff("Financeiro", insurer.getId(), "FINANCE");
        UserAccount admin = fixtures.staff("Administrador", insurer.getId(), "INSURER_ADMIN");
        UserAccount underwriter = fixtures.staff("Subscritor", insurer.getId(), "UNDERWRITER");
        Organization agent = fixtures.organization(OrganizationType.AGENT, "Agente Permissoes S5");
        UserAccount agentStaff = fixtures.staff("Agente", agent.getId(), "AGENT");
        UserAccount customer = fixtures.customer("Cliente Pagamentos");
        UserAccount platform = fixtures.staff("Plataforma", null, "PLATFORM_ADMIN");

        assertThat(permissions(finance)).contains("payment:configure", "payment:read", "payment:resolve-exception");
        assertThat(permissions(admin)).contains("payment:configure", "payment:read")
                .doesNotContain("payment:resolve-exception");
        assertThat(permissions(underwriter)).doesNotContainAnyElementsOf(S5);
        assertThat(permissions(agentStaff)).contains("payment:link-for-client").doesNotContain("payment:read");
        assertThat(permissions(customer)).contains("self:payments").doesNotContain("payment:read");
        assertThat(permissions(platform)).contains("platform:operations").doesNotContain("payment:configure");

        RestAssured.port = port;
        List<String> me = given().auth().oauth2(identityApi.issueTokens(finance, null).accessToken())
                .get("/api/v1/me").then().statusCode(200).extract().jsonPath().getList("permissions", String.class);
        assertThat(me).contains("payment:configure", "payment:read", "payment:resolve-exception");
    }

    private List<String> permissions(UserAccount account) {
        return jwtDecoder.decode(identityApi.issueTokens(account, null).accessToken()).getClaimAsStringList("permissions");
    }
}
