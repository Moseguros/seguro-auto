package mz.co.moseguros.shared.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * O {@code TenantContext} de cada pedido vem do token ({@link SecurityProbeController}, só no
 * perfil {@code test}), e as permissões do token chegam ao {@code @PreAuthorize}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class TenantContextFilterIT {

    static final String TENANT = "/api/v1/test-probes/tenant";
    static final String ORGANIZATION_MANAGE = "/api/v1/test-probes/organization-manage";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void customerScope() {
        UserAccount customer = fixtures.customer("Ana");

        given().auth().oauth2(token(customer)).get(TENANT)
                .then().statusCode(200)
                .body("scope", equalTo("CUSTOMER"))
                .body("userId", equalTo(customer.getId().toString()))
                .body("orgId", nullValue())
                .body("insurerId", nullValue());
    }

    @Test
    void platformScope() {
        UserAccount admin = fixtures.staff("Admin", null, "PLATFORM_ADMIN");

        given().auth().oauth2(token(admin)).get(TENANT)
                .then().statusCode(200)
                .body("scope", equalTo("PLATFORM"))
                .body("userId", equalTo(admin.getId().toString()))
                .body("orgId", nullValue());
    }

    @Test
    void orgScopeWithInsurer() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Tenant");
        UserAccount staff = fixtures.staff("Staff", insurer.getId(), "UNDERWRITER");

        given().auth().oauth2(token(staff)).get(TENANT)
                .then().statusCode(200)
                .body("scope", equalTo("ORG"))
                .body("userId", equalTo(staff.getId().toString()))
                .body("orgId", equalTo(insurer.getId().toString()))
                .body("insurerId", equalTo(insurer.getId().toString()));
    }

    @Test
    void orgScopeWithoutInsurerForOtherOrganizations() {
        Organization workshop = fixtures.organization(OrganizationType.WORKSHOP, "Oficina Tenant");
        UserAccount staff = fixtures.staff("Mecânico", workshop.getId(), "WORKSHOP");

        given().auth().oauth2(token(staff)).get(TENANT)
                .then().statusCode(200)
                .body("scope", equalTo("ORG"))
                .body("orgId", equalTo(workshop.getId().toString()))
                .body("insurerId", nullValue());
    }

    @Test
    void staffWithoutOrganizationAndWithoutPlatformAdminHasNoScope() {
        UserAccount staff = fixtures.staff("Sem âmbito", null);

        given().auth().oauth2(token(staff)).get(TENANT)
                .then().statusCode(200)
                .body("scope", equalTo("NONE"))
                .body("userId", nullValue());
    }

    @Test
    void probeRequiresAuthentication() {
        given().get(TENANT).then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
    }

    @Test
    void permissionReachesPreAuthorize() {
        UserAccount customer = fixtures.customer("Cliente");
        UserAccount admin = fixtures.staff("Admin", null, "PLATFORM_ADMIN");

        given().auth().oauth2(token(customer)).get(ORGANIZATION_MANAGE)
                .then().statusCode(403)
                .contentType("application/problem+json")
                .body("code", equalTo("FORBIDDEN"));
        given().auth().oauth2(token(admin)).get(ORGANIZATION_MANAGE)
                .then().statusCode(200)
                .body("result", equalTo("ok"));
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }
}
