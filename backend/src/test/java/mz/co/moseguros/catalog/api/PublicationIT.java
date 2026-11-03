package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.PRODUCTS;
import static mz.co.moseguros.catalog.CatalogTestSupport.VERSIONS;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static mz.co.moseguros.catalog.CatalogTestSupport.ownDamage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionPublished;
import mz.co.moseguros.catalog.application.PublicationService;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.shared.security.JwtClaimsConverter;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.PublishedEvents;
import org.springframework.modulith.test.PublishedEventsExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Publicação de versões (S2-04 §4 e §5): todas as falhas na mesma resposta, publicação com
 * sucesso (estados, evento e auditoria), substituição da versão em vigor e capital mínimo lido no
 * momento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(PublishedEventsExtension.class)
class PublicationIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    PublicationService publicationService;

    @Autowired
    JwtDecoder jwtDecoder;

    CatalogTestSupport support;
    Insurer insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seg Publicação " + UUID.randomUUID().toString().substring(0, 4));
    }

    /** Os testes partilham a BD: o capital mínimo é reposto. */
    @AfterEach
    void restore() {
        fixtures.owner().update("update catalog.platform_setting set value = '300000000' where key = 'MTPL_MIN_CAPITAL'");
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @Test
    void anEmptyVersionIsRefusedWithAllTheFailuresTogether() {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");

        support.publish(t, p.versionId()).then().statusCode(400)
                .body("code", equalTo("PRODUCT_VERSION_NOT_PUBLISHABLE"))
                .body("errors.collect { it.field + ':' + it.code }", contains("coverages:MTPL_REQUIRED",
                        "documents.GENERAL_CONDITIONS:DOCUMENT_REQUIRED", "documents.PRE_CONTRACT_INFO:DOCUMENT_REQUIRED",
                        "vehicleCategories:REQUIRED", "usages:REQUIRED"));
        given().auth().oauth2(insurer.underwriter()).post(VERSIONS + "/{id}/validate", p.versionId()).then()
                .statusCode(200)
                .body("publishable", equalTo(false))
                .body("errors.size()", equalTo(5))
                .body("errors[0].field", equalTo("coverages"))
                .body("errors[0].code", equalTo("MTPL_REQUIRED"));
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then().body("status", equalTo("DRAFT"));
    }

    @Test
    void refusedWhenMtplIsMissing() {
        String t = insurer.manager();
        Created p = support.product(t, "COMPREHENSIVE");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                List.of(ownDamage("OD_COLLISION")));

        support.publish(t, p.versionId()).then().statusCode(400)
                .body("code", equalTo("PRODUCT_VERSION_NOT_PUBLISHABLE"))
                .body("errors.collect { it.field + ':' + it.code }", contains("coverages:MTPL_REQUIRED"));
    }

    @Test
    void refusedWhenDocumentsAreMissing() {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        support.updateEligibility(t, p.versionId(), List.of("MOTORCYCLE"), List.of("PRIVATE")).then().statusCode(200);
        support.replaceCoverages(t, p.versionId(), List.of(mtpl(MIN_CAPITAL))).then().statusCode(200);
        // em inglês não conta
        given().auth().oauth2(t).multiPart("file", "en.pdf", support.pdf(), "application/pdf")
                .multiPart("docType", "GENERAL_CONDITIONS").multiPart("language", "en")
                .post(VERSIONS + "/{id}/documents", p.versionId()).then().statusCode(201);

        support.publish(t, p.versionId()).then().statusCode(400)
                .body("errors.collect { it.field + ':' + it.code }", containsInAnyOrder(
                        "documents.GENERAL_CONDITIONS:DOCUMENT_REQUIRED",
                        "documents.PRE_CONTRACT_INFO:DOCUMENT_REQUIRED"));

        support.uploadPdf(t, p.versionId(), "GENERAL_CONDITIONS").then().statusCode(201);
        support.publish(t, p.versionId()).then().statusCode(400)
                .body("errors.collect { it.field + ':' + it.code }",
                        contains("documents.PRE_CONTRACT_INFO:DOCUMENT_REQUIRED"));
    }

    @Test
    void successfulPublicationPublishesTheVersionAndTheProductAndEmitsTheEvent(PublishedEvents events) {
        UserAccount manager = fixtures.staff("Gestor Publicação", insurer.id(), "PRODUCT_MANAGER");
        String t = support.token(manager);
        Created p = support.product(t, "MTPL_ONLY");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        given().auth().oauth2(t).post(VERSIONS + "/{id}/validate", p.versionId()).then().statusCode(200)
                .body("publishable", equalTo(true)).body("errors.size()", equalTo(0));
        long version = support.version(t, p.versionId());

        // chamada directa ao serviço (na thread do teste), para o PublishedEvents ver o evento
        SecurityContextHolder.getContext().setAuthentication(new JwtClaimsConverter().convert(jwtDecoder.decode(t)));
        TenantContext.set(TenantSnapshot.org(manager.getId(), insurer.id(), insurer.id()));
        publicationService.publish(p.versionId(), version);
        TenantContext.clear();
        SecurityContextHolder.clearContext();

        assertThat(events.ofType(ProductVersionPublished.class)
                .matching(e -> e.productVersionId().equals(p.versionId()))).hasSize(1);
        ProductVersionPublished event = events.ofType(ProductVersionPublished.class).iterator().next();
        assertThat(event.insurerId()).isEqualTo(insurer.id());
        assertThat(event.productId()).isEqualTo(p.productId());
        assertThat(event.versionNo()).isEqualTo(1);
        assertThat(event.productType()).isEqualTo(ProductType.MTPL_ONLY);
        assertThat(event.eventId()).isNotNull();

        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then().statusCode(200)
                .body("status", equalTo("PUBLISHED"))
                .body("validFrom", equalTo(today()))
                .body("publishedAt", notNullValue())
                .body("publishedBy", equalTo(manager.getId().toString()))
                .body("editable", equalTo(false))
                .body("version", equalTo((int) version + 1));
        given().auth().oauth2(t).get(PRODUCTS + "/{id}", p.productId()).then().statusCode(200)
                .body("status", equalTo("PUBLISHED"))
                .body("currentVersion.id", equalTo(p.versionId().toString()));
        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'PRODUCT_VERSION.PUBLISH' and entity_id = ?
                   and insurer_id = ? and actor_type = 'STAFF' and actor_id = ?
                   and before_state ->> 'status' = 'DRAFT' and after_state ->> 'status' = 'PUBLISHED'""",
                Integer.class, p.versionId().toString(), insurer.id(), manager.getId().toString())).isEqualTo(1);
    }

    @Test
    void publishingVersionTwoSupersedesVersionOne() {
        String t = insurer.manager();
        Created p = support.publishedMtplOnly(t);
        String v2 = given().auth().oauth2(t).post(PRODUCTS + "/{id}/versions", p.productId()).then().statusCode(201)
                .extract().path("id");
        support.replaceCoverages(t, UUID.fromString(v2), List.of(mtpl(MIN_CAPITAL + 1_000_000))).then().statusCode(200);

        support.publish(t, UUID.fromString(v2)).then().statusCode(200)
                .body("status", equalTo("PUBLISHED")).body("versionNo", equalTo(2));
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then()
                .body("status", equalTo("SUPERSEDED"))
                .body("validTo", equalTo(today()));
        given().auth().oauth2(t).get(PRODUCTS + "/{id}", p.productId()).then()
                .body("currentVersion.versionNo", equalTo(2))
                .body("versions.status", contains("PUBLISHED", "SUPERSEDED"));
    }

    @Test
    void theMinimumCapitalIsReadAtPublicationTime() {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        given().auth().oauth2(t).post(VERSIONS + "/{id}/validate", p.versionId()).then()
                .body("publishable", equalTo(true));

        fixtures.owner().update("update catalog.platform_setting set value = '400000000' where key = 'MTPL_MIN_CAPITAL'");

        given().auth().oauth2(t).post(VERSIONS + "/{id}/validate", p.versionId()).then()
                .body("publishable", equalTo(false))
                .body("errors[0].code", equalTo("MTPL_CAPITAL_BELOW_MINIMUM"))
                .body("errors[0].field", equalTo("coverages[0].capital"));
        support.publish(t, p.versionId()).then().statusCode(400)
                .body("code", equalTo("PRODUCT_VERSION_NOT_PUBLISHABLE"))
                .body("errors[0].code", equalTo("MTPL_CAPITAL_BELOW_MINIMUM"));
    }

    @Test
    void publishChecksVersionAndPermissions() {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        long version = support.version(t, p.versionId());
        given().auth().oauth2(t).contentType(ContentType.JSON).body(Map.of("version", version - 1))
                .post(VERSIONS + "/{id}/publish", p.versionId()).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        given().auth().oauth2(insurer.underwriter()).contentType(ContentType.JSON).body(Map.of("version", version))
                .post(VERSIONS + "/{id}/publish", p.versionId()).then().statusCode(403);

        // produto retirado: a DRAFT fica, mas não se publica
        given().auth().oauth2(t).post(PRODUCTS + "/{id}/retire", p.productId()).then().statusCode(200);
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then()
                .body("status", equalTo("DRAFT")).body("editable", equalTo(false));
        given().auth().oauth2(t).contentType(ContentType.JSON).body(Map.of("version", version))
                .post(VERSIONS + "/{id}/publish", p.versionId()).then().statusCode(409)
                .body("code", equalTo("PRODUCT_RETIRED"));
    }

    static String today() {
        return LocalDate.now(ZoneId.of("Africa/Maputo")).toString();
    }
}
