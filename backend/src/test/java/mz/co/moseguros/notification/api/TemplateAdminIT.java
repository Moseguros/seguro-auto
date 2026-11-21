package mz.co.moseguros.notification.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.organization.OrganizationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Administração dos modelos (S6-04 §8): listar, editar (auditado), cada regra de validação, {@code version}
 * antiga, repor, pré-visualizar, testar; cliente e staff de seguradora → 403.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class TemplateAdminIT {

    static final String BASE = "/api/v1/admin/notification-templates";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    FakeOtpDeliveryChannels channels;

    NotificationTestSupport support;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new NotificationTestSupport(fixtures, identityApi);
        admin = support.platformAdminToken();
    }

    @AfterEach
    void tearDown() {
        channels.reset();
    }

    @Test
    void listsWithFiltersAndShowsTheDefaults() {
        JsonPath page = given().auth().oauth2(admin).queryParam("code", "POLICY_").queryParam("channel", "SMS")
                .queryParam("locale", "pt").get(BASE).then().statusCode(200).extract().jsonPath();
        assertThat(page.getLong("totalElements")).isEqualTo(1);
        assertThat(page.getString("content[0].code")).isEqualTo("POLICY_ISSUED");
        assertThat(page.getString("content[0].channel")).isEqualTo("SMS");
        assertThat(page.getBoolean("content[0].active")).isTrue();
        given().auth().oauth2(admin).queryParam("size", 100).get(BASE).then().statusCode(200)
                .body("totalElements", equalTo(72)).body("content[0].code", equalTo("PAYMENT_FAILED"));

        UUID id = id("POLICY_ISSUED", "EMAIL", "pt");
        JsonPath detail = given().auth().oauth2(admin).get(BASE + "/{id}", id).then().statusCode(200).extract()
                .jsonPath();
        assertThat(detail.getString("subject")).isEqualTo("A sua apólice foi emitida");
        assertThat(detail.getString("defaultSubject")).isEqualTo("A sua apólice foi emitida");
        assertThat(detail.getString("defaultBody")).isEqualTo(detail.getString("body"));
        assertThat(detail.getList("variables", String.class))
                .containsExactly("policyNumber", "insurerName", "plate", "validTo", "link", "siteUrl");
        assertThat(detail.getMap("sampleParams")).containsEntry("plate", "ABC-123-MC");
        assertThat(detail.getBoolean("customized")).isFalse();
        given().auth().oauth2(admin).get(BASE + "/{id}", UUID.randomUUID()).then().statusCode(404)
                .body("code", equalTo("TEMPLATE_NOT_FOUND"));
    }

    @Test
    void editsAuditsChecksTheVersionAndResets() {
        UUID id = id("QUOTE_EXPIRING", "SMS", "en");
        long version = version(id);
        try {
            put(id, Map.of("body", "MoSeguros: quote from {{amount}} until {{validUntil}}. {{siteUrl}}",
                    "version", version)).then().statusCode(200)
                    .body("customized", equalTo(true))
                    .body("body", equalTo("MoSeguros: quote from {{amount}} until {{validUntil}}. {{siteUrl}}"))
                    .body("subject", nullValue());
            Map<String, Object> audit = support.owner().queryForMap("""
                    select actor_type, before_state->>'body' as before_body, after_state->>'body' as after_body
                      from audit.audit_event where action = 'NOTIFICATION_TEMPLATE.UPDATE' and entity_id = ?
                     order by occurred_at desc limit 1""", id.toString());
            assertThat(audit).containsEntry("actor_type", "STAFF")
                    .containsEntry("after_body", "MoSeguros: quote from {{amount}} until {{validUntil}}. {{siteUrl}}");
            assertThat((String) audit.get("before_body")).startsWith("MoSeguros: your quote");

            put(id, Map.of("body", "x {{amount}}", "version", version)).then().statusCode(409)
                    .body("code", equalTo("CONCURRENT_MODIFICATION"));

            given().auth().oauth2(admin).post(BASE + "/{id}/reset", id).then().statusCode(200)
                    .body("customized", equalTo(false))
                    .body("body", equalTo("MoSeguros: your quote (from {{amount}}) is valid until {{validUntil}}. "
                            + "Continue in the app or at {{siteUrl}}."));
            assertThat(support.owner().queryForObject("""
                    select count(*) from audit.audit_event where action = 'NOTIFICATION_TEMPLATE.RESET'
                       and entity_id = ?""", Integer.class, id.toString())).isEqualTo(1);
        } finally {
            support.owner().update("update notification.template set customized = false where id = ?", id);
            given().auth().oauth2(admin).post(BASE + "/{id}/reset", id);
        }
    }

    @Test
    void eachValidationRuleGivesItsCode() {
        UUID sms = id("PROPOSAL_EXPIRED", "SMS", "pt");
        UUID email = id("PROPOSAL_EXPIRED", "EMAIL", "pt");
        UUID push = id("PROPOSAL_EXPIRED", "PUSH", "pt");
        UUID whatsapp = id("PROPOSAL_EXPIRED", "WHATSAPP", "pt");
        expectError(sms, Map.of("body", "Olá {{proposalNumber"), "body", "TEMPLATE_SYNTAX");
        expectError(sms, Map.of("body", "Olá {{plate}}"), "body", "TEMPLATE_UNKNOWN_VARIABLE");
        expectError(sms, Map.of("body", "{{proposalNumber}} " + "x".repeat(150)), "body", "SMS_TOO_LONG");
        expectError(email, Map.of("body", "Olá"), "subject", "REQUIRED");
        expectError(email, Map.of("subject", "s".repeat(201), "body", "Olá"), "subject", "TOO_LONG");
        expectError(email, Map.of("subject", "{{nope}}", "body", "Olá"), "subject", "TEMPLATE_UNKNOWN_VARIABLE");
        expectError(push, Map.of("subject", "t".repeat(66), "body", "Olá"), "subject", "TOO_LONG");
        expectError(push, Map.of("subject", "Título", "body", "p".repeat(241)), "body", "TOO_LONG");
        expectError(whatsapp, Map.of("body", "Olá"), "whatsappTemplateName", "REQUIRED");
        assertThat(support.owner().queryForObject("select customized from notification.template where id = ?",
                Boolean.class, sms)).isFalse();
    }

    @Test
    void previewsWithoutSaving() {
        UUID sms = id("PROPOSAL_EXPIRED", "SMS", "pt");
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("body", "Apólice {{proposalNumber}} — {{siteUrl}}"))
                .post(BASE + "/{id}/preview", sms).then().statusCode(200)
                .body("body", equalTo("Apólice PR-2027-000123 — https://moseguros.co.mz"))
                .body("smsText", equalTo("Apolice PR-2027-000123 - https://moseguros.co.mz"))
                .body("length", equalTo(48)).body("limit", equalTo(160)).body("fits", equalTo(true));
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("body", "{{proposalNumber}}", "params", Map.of("proposalNumber", "PR-X")))
                .post(BASE + "/{id}/preview", sms).then().statusCode(200).body("body", equalTo("PR-X"));
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("body", "{{plate}}"))
                .post(BASE + "/{id}/preview", sms).then().statusCode(400)
                .body("errors[0].code", equalTo("TEMPLATE_UNKNOWN_VARIABLE"));
        UUID email = id("PROPOSAL_EXPIRED", "EMAIL", "pt");
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("subject", "Proposta {{proposalNumber}}", "body", "Olá {{link}}"))
                .post(BASE + "/{id}/preview", email).then().statusCode(200)
                .body("subject", equalTo("Proposta PR-2027-000123")).body("smsText", nullValue())
                .body("fits", nullValue());
    }

    @Test
    void sendsATestMessage() {
        UUID sms = id("POLICY_ISSUED", "SMS", "pt");
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "841110001"))
                .post(BASE + "/{id}/test", sms).then().statusCode(200).body("status", equalTo("SENT"));
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo("+258841110001");
        assertThat(sent.text()).isEqualTo("MoSeguros: apolice AP-2027-000123 (ABC-123-MC) activa ate 10/02/2028 10:31. "
                + "Certificado na app e em https://moseguros.co.mz.");
        assertThat(support.owner().queryForObject("""
                select purpose from notification.outbound_message where correlation_id = ? order by created_at desc
                 limit 1""", String.class, sms)).isEqualTo("NOTIFICATION_TEST");

        UUID whatsapp = id("POLICY_ISSUED", "WHATSAPP", "pt");
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "+258 84 111 0002"))
                .post(BASE + "/{id}/test", whatsapp).then().statusCode(200).body("status", equalTo("SENT"));
        assertThat(channels.lastTo("+258841110002").params()).containsEntry("template", "moseguros_policy_issued")
                .containsEntry("p1", "AP-2027-000123").containsEntry("p2", "ABC-123-MC")
                .containsEntry("p3", "10/02/2028 10:31");

        channels.mode(mz.co.moseguros.shared.domain.Channel.SMS, FakeOtpDeliveryChannels.Mode.FAIL);
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "841110003"))
                .post(BASE + "/{id}/test", sms).then().statusCode(200).body("status", equalTo("FAILED"))
                .body("reason", equalTo("HTTP 503"));

        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "123"))
                .post(BASE + "/{id}/test", sms).then().statusCode(400)
                .body("errors[0].field", equalTo("destination")).body("errors[0].code", equalTo("INVALID_MSISDN"));
        UUID email = id("POLICY_ISSUED", "EMAIL", "pt");
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "nao-e-email"))
                .post(BASE + "/{id}/test", email).then().statusCode(400)
                .body("errors[0].code", equalTo("INVALID_EMAIL"));
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "Ana@Teste.co.mz"))
                .post(BASE + "/{id}/test", email).then().statusCode(200).body("status", equalTo("SENT"));
        assertThat(channels.lastTo("ana@teste.co.mz").subject()).isEqualTo("A sua apólice foi emitida");

        UUID push = id("POLICY_ISSUED", "PUSH", "pt");
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("destination", "fcm-token-test-1"))
                .post(BASE + "/{id}/test", push).then().statusCode(200).body("status", equalTo("SENT"));
        assertThat(channels.pushes()).extracting(FakeOtpDeliveryChannels.Push::title).contains("Apólice emitida");
    }

    @Test
    void onlyThePlatformAdminManagesTemplates() {
        UUID id = id("POLICY_ISSUED", "SMS", "pt");
        given().get(BASE).then().statusCode(401);
        String customer = support.customer().token();
        given().auth().oauth2(customer).get(BASE).then().statusCode(403);
        UUID insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Modelos").getId();
        String insurerAdmin = support.staffToken(insurer, "INSURER_ADMIN");
        given().auth().oauth2(insurerAdmin).get(BASE).then().statusCode(403);
        given().auth().oauth2(insurerAdmin).get(BASE + "/{id}", id).then().statusCode(403);
        given().auth().oauth2(insurerAdmin).contentType(ContentType.JSON).body(Map.of("destination", "841110009"))
                .post(BASE + "/{id}/test", id).then().statusCode(403);
        given().auth().oauth2(admin).get(BASE).then().statusCode(200).body("content.code", hasItem("PAYMENT_FAILED"));
    }

    private void expectError(UUID id, Map<String, Object> body, String field, String code) {
        Map<String, Object> request = new HashMap<>(body);
        request.put("version", version(id));
        put(id, request).then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", hasItem(field)).body("errors.code", hasItem(code));
    }

    private Response put(UUID id, Map<String, Object> body) {
        return given().auth().oauth2(admin).contentType(ContentType.JSON).body(body).put(BASE + "/{id}", id);
    }

    private long version(UUID id) {
        return support.owner().queryForObject("select version from notification.template where id = ?", Long.class,
                id);
    }

    private UUID id(String code, String channel, String locale) {
        List<UUID> ids = support.owner().queryForList("""
                select id from notification.template where code = ? and channel = ? and locale = ?""", UUID.class,
                code, channel, locale);
        assertThat(ids).hasSize(1);
        return ids.get(0);
    }
}
