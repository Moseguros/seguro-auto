package mz.co.moseguros.notification.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/** Centro de notificações do cliente (S6-04 §8): só as suas, não lidas, marcar como lida; staff → 403. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class MyNotificationsIT {

    static final String BASE = "/api/v1/me/notifications";

    @LocalServerPort
    int port;

    @Autowired
    NotificationApi notifications;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    TransactionTemplate tx;

    NotificationTestSupport support;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new NotificationTestSupport(fixtures, identityApi);
    }

    @Test
    void listsOnlyTheCustomersOwnNotificationsAndMarksThemRead() {
        Client a = support.customer();
        Client b = support.customer();
        UUID proposalId = UUID.randomUUID();
        UUID first = notify(a, "PR-2027-000001");
        UUID second = tx.execute(s -> notifications.notify(a.id(), "PROPOSAL_ACCEPTED",
                NotificationTestSupport.accepted(proposalId),
                NotificationCategory.PROPOSAL));
        notify(b, "PR-2027-000003");

        JsonPath page = given().auth().oauth2(a.token()).get(BASE).then().statusCode(200).extract().jsonPath();
        assertThat(page.getLong("totalElements")).isEqualTo(2);
        UUID secondInbox = inboxId(second);
        UUID firstInbox = inboxId(first);
        assertThat(page.getList("content.id", String.class)).containsExactly(secondInbox.toString(),
                firstInbox.toString());
        Map<String, Object> latest = page.getMap("content[0]");
        assertThat(latest).containsEntry("templateCode", "PROPOSAL_ACCEPTED").containsEntry("category", "PROPOSAL")
                .containsEntry("title", "Proposta aceite")
                .containsEntry("deepLink", "moseguros://proposal/" + proposalId)
                .containsEntry("webPath", "/cliente/propostas/" + proposalId)
                .containsKey("createdAt").doesNotContainKey("readAt");
        assertThat(page.getString("content[1].body")).isEqualTo("A proposta PR-2027-000001 expirou. Pode fazer uma "
                + "nova simulação.");

        given().auth().oauth2(a.token()).get(BASE + "/unread-count").then().statusCode(200).body("count", equalTo(2));
        given().auth().oauth2(a.token()).post(BASE + "/{id}/read", firstInbox).then().statusCode(204);
        given().auth().oauth2(a.token()).post(BASE + "/{id}/read", firstInbox).then().statusCode(204);
        given().auth().oauth2(a.token()).get(BASE + "/unread-count").then().statusCode(200).body("count", equalTo(1));
        given().auth().oauth2(a.token()).queryParam("unreadOnly", true).get(BASE).then().statusCode(200)
                .body("totalElements", equalTo(1)).body("content[0].id", equalTo(secondInbox.toString()));
        given().auth().oauth2(a.token()).get(BASE).then().statusCode(200)
                .body("content[1].readAt", notNullValue()).body("content[0].readAt", nullValue());

        // de outro cliente ou inexistente → 404
        given().auth().oauth2(b.token()).post(BASE + "/{id}/read", secondInbox).then().statusCode(404)
                .body("code", equalTo("NOTIFICATION_NOT_FOUND"));
        given().auth().oauth2(a.token()).post(BASE + "/{id}/read", UUID.randomUUID()).then().statusCode(404)
                .body("code", equalTo("NOTIFICATION_NOT_FOUND"));
        // linha que não é INBOX → 404
        UUID external = (UUID) support.external(first).get(0).get("id");
        given().auth().oauth2(a.token()).post(BASE + "/{id}/read", external).then().statusCode(404);

        given().auth().oauth2(a.token()).post(BASE + "/read-all").then().statusCode(204);
        given().auth().oauth2(a.token()).get(BASE + "/unread-count").then().body("count", equalTo(0));
        given().auth().oauth2(b.token()).get(BASE + "/unread-count").then().body("count", equalTo(1));
    }

    @Test
    void staffIsForbiddenAndAnonymousIsUnauthorized() {
        given().get(BASE).then().statusCode(401);
        given().get(BASE + "/unread-count").then().statusCode(401);
        String staff = support.platformAdminToken();
        given().auth().oauth2(staff).get(BASE).then().statusCode(403);
        given().auth().oauth2(staff).post(BASE + "/read-all").then().statusCode(403);
        given().auth().oauth2(staff).post(BASE + "/{id}/read", UUID.randomUUID()).then().statusCode(403);
    }

    private UUID notify(Client c, String number) {
        return tx.execute(s -> notifications.notify(c.id(), "PROPOSAL_EXPIRED", Map.of("proposalNumber", number),
                NotificationCategory.PROPOSAL));
    }

    private UUID inboxId(UUID notificationId) {
        return (UUID) support.inbox(notificationId).get("id");
    }
}
