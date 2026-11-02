package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.application.CustomerProfileService;
import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.CustomerProfileRules;
import mz.co.moseguros.identity.domain.CustomerProfileRules.Changes;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code GET/PUT /me/profile} (S1-06 §2): completo ou não, valores mascarados, cifra na BD,
 * validação, concorrência, acesso de staff e isolamento entre clientes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class CustomerProfileIT {

    private static final String DOC = "110100123345A";
    private static final String NUIT = "123456789";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    CustomerProfileService profileService;

    @Autowired
    CustomerProfileRepository profiles;

    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    TransactionTemplate tx;

    JdbcTemplate owner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    private String tokenOf(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private static Map<String, Object> fullProfile(long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", "Ana Maria Mussa");
        body.put("birthDate", "1990-05-14");
        body.put("idDocType", "BI");
        body.put("idDocNumber", DOC);
        body.put("nuit", NUIT);
        body.put("address", "Av. Julius Nyerere, 1234");
        body.put("province", "MAPUTO_CITY");
        body.put("version", version);
        return body;
    }

    private static Response put(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/profile");
    }

    @Test
    void newCustomerIsIncomplete() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));

        given().auth().oauth2(token).get("/api/v1/me/profile")
                .then().statusCode(200)
                .body("complete", equalTo(false))
                .body("fullName", equalTo("Ana Mussa"))
                .body("$", not(hasKey("idDocNumberMasked")))
                .body("$", not(hasKey("nuitMasked")))
                .body("$", not(hasKey("birthDate")))
                .body("$", not(hasKey("province")))
                .body("version", equalTo(0));
    }

    @Test
    void fillingEverythingMakesItCompleteWithMaskedValuesAndEncryptedColumns() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = tokenOf(customer);

        put(token, fullProfile(0)).then().statusCode(200)
                .body("complete", equalTo(true))
                .body("fullName", equalTo("Ana Maria Mussa"))
                .body("birthDate", equalTo("1990-05-14"))
                .body("idDocType", equalTo("BI"))
                .body("idDocNumberMasked", equalTo("*********345A"))
                .body("nuitMasked", equalTo("*****6789"))
                .body("address", equalTo("Av. Julius Nyerere, 1234"))
                .body("province", equalTo("MAPUTO_CITY"))
                .body("version", equalTo(1))
                .body("$", not(hasKey("idDocNumber")))
                .body("$", not(hasKey("nuit")));

        String body = given().auth().oauth2(token).get("/api/v1/me/profile")
                .then().statusCode(200)
                .body("complete", equalTo(true))
                .body("idDocNumberMasked", equalTo("*********345A"))
                .body("nuitMasked", equalTo("*****6789"))
                .extract().asString();
        assertThat(body).doesNotContain(DOC).doesNotContain(NUIT);

        given().auth().oauth2(token).get("/api/v1/me")
                .then().statusCode(200)
                .body("profileComplete", equalTo(true))
                .body("displayName", equalTo("Ana Maria Mussa"));

        Map<String, Object> row = owner.queryForMap(
                "select nuit_enc, id_doc_number_enc from identity.customer_profile where user_id = ?",
                customer.getId());
        String nuitEnc = new String((byte[]) row.get("nuit_enc"), StandardCharsets.ISO_8859_1);
        String docEnc = new String((byte[]) row.get("id_doc_number_enc"), StandardCharsets.ISO_8859_1);
        assertThat(nuitEnc).doesNotContain(NUIT).doesNotContain("6789");
        assertThat(docEnc).doesNotContain(DOC).doesNotContain("345A");

        Map<String, Object> audit = owner.queryForMap("""
                select actor_type, actor_id, before_state::text as before, after_state::text as after
                  from audit.audit_event where action = 'CUSTOMER.PROFILE_UPDATE' and actor_id = ?""",
                customer.getId().toString());
        assertThat(audit.get("actor_type")).isEqualTo("CUSTOMER");
        assertThat((String) audit.get("after")).contains("*********345A").contains("*****6789")
                .doesNotContain(DOC).doesNotContain(NUIT);
        assertThat((String) audit.get("before")).doesNotContain(DOC).doesNotContain(NUIT);
    }

    @Test
    void documentAndNuitCanBeKeptOrReplaced() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        put(token, fullProfile(0)).then().statusCode(200);

        Map<String, Object> keep = fullProfile(1);
        keep.remove("idDocNumber");
        keep.put("nuit", null);
        keep.put("province", "GAZA");
        put(token, keep).then().statusCode(200)
                .body("idDocNumberMasked", equalTo("*********345A"))
                .body("nuitMasked", equalTo("*****6789"))
                .body("province", equalTo("GAZA"))
                .body("version", equalTo(2));

        Map<String, Object> changeType = fullProfile(2);
        changeType.put("idDocType", "PASSPORT");
        changeType.remove("idDocNumber");
        put(token, changeType).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("idDocNumber"))
                .body("errors[0].code", equalTo("REQUIRED"));

        changeType.put("idDocNumber", "n 1234567");
        changeType.put("nuit", "987654321");
        put(token, changeType).then().statusCode(200)
                .body("idDocType", equalTo("PASSPORT"))
                .body("idDocNumberMasked", equalTo("****4567"))
                .body("nuitMasked", equalTo("*****4321"));
    }

    @Test
    void underageIsRejected() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        Map<String, Object> body = fullProfile(0);
        body.put("birthDate", LocalDate.now(CustomerProfileRules.ZONE).minusYears(17).toString());

        put(token, body).then().statusCode(400)
                .contentType("application/problem+json")
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.size()", equalTo(1))
                .body("errors[0].field", equalTo("birthDate"))
                .body("errors[0].code", equalTo("UNDERAGE"));
    }

    @Test
    void allValidationErrorsComeTogether() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", "Ana");
        body.put("idDocNumber", "ab");
        body.put("nuit", "123");
        body.put("version", 0);

        put(token, body).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", equalTo(List.of("fullName", "birthDate", "idDocType", "idDocNumber", "nuit",
                        "province")))
                .body("errors.code", equalTo(List.of("INVALID_NAME", "REQUIRED", "REQUIRED",
                        "INVALID_DOCUMENT_NUMBER", "INVALID_NUIT", "REQUIRED")));
    }

    @Test
    void staleVersionIsAConflict() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        put(token, fullProfile(0)).then().statusCode(200);

        put(token, fullProfile(0)).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void missingVersionIsAValidationError() {
        String token = tokenOf(fixtures.customer("Ana Mussa"));
        Map<String, Object> body = fullProfile(0);
        body.remove("version");
        put(token, body).then().statusCode(400)
                .body("errors[0].field", equalTo("version"));
    }

    @Test
    void staffIsForbidden() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Perfil");
        String token = tokenOf(fixtures.staff("Bruno Staff", insurer.getId(), "INSURER_ADMIN"));

        given().auth().oauth2(token).get("/api/v1/me/profile")
                .then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        put(token, fullProfile(0)).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
    }

    @Test
    void withoutTokenIsUnauthorized() {
        given().get("/api/v1/me/profile").then().statusCode(401);
    }

    /**
     * Isolamento pelo RLS, directamente no serviço: com o âmbito {@code CUSTOMER} de A, o pedido
     * com a identidade de B não encontra o perfil de B (nem o lê nem o altera).
     */
    @Test
    void customerScopeOfAHidesTheProfileOfB() {
        UserAccount a = fixtures.customer("Ana Mussa");
        UserAccount b = fixtures.customer("Bela Cossa");
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(
                jwtDecoder.decode(tokenOf(b)), List.of(new SimpleGrantedAuthority("self:profile"))));
        TenantContext.set(TenantSnapshot.customer(a.getId()));

        assertThatThrownBy(() -> profileService.getMyProfile())
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("NOT_FOUND"));
        Changes changes = new Changes("Intrusa Silva", LocalDate.of(1990, 1, 1), IdDocType.BI, DOC, NUIT, null,
                Province.GAZA);
        assertThatThrownBy(() -> profileService.updateMyProfile(changes, 0))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("NOT_FOUND"));
        Optional<CustomerProfile> ofB = tx.execute(status -> profiles.findByUserId(b.getId()));
        Optional<CustomerProfile> ofA = tx.execute(status -> profiles.findByUserId(a.getId()));
        assertThat(ofB).isEmpty();
        assertThat(ofA).isPresent();

        TenantContext.clear();
        assertThat(owner.queryForObject("select full_name from identity.customer_profile where user_id = ?",
                String.class, b.getId())).isEqualTo("Bela Cossa");
    }
}
