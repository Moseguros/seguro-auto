package mz.co.moseguros.notification.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.notification.NotificationTestSupport.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/** Preferências do cliente (S6-04 §8): canal seguinte, categoria obrigatória e {@code available}. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PreferencesIT {

    static final String BASE = "/api/v1/me/notification-preferences";

    @LocalServerPort
    int port;

    @Autowired
    NotificationApi notifications;

    @Autowired
    FakeOtpDeliveryChannels channels;

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

    @AfterEach
    void tearDown() {
        channels.reset();
    }

    @Test
    void listsAllCategoriesWithChannelsInOrderAndAvailability() {
        Client c = support.customer();
        JsonPath body = given().auth().oauth2(c.token()).get(BASE).then().statusCode(200).extract().jsonPath();
        assertThat(body.getList("categories.category", String.class))
                .containsExactly("PAYMENT", "POLICY", "LEGAL", "PROPOSAL", "QUOTE");
        assertThat(body.getList("categories.mandatory", Boolean.class)).containsExactly(true, true, true, false, false);
        assertThat(body.getList("categories[0].channels.channel", String.class))
                .containsExactly("PUSH", "WHATSAPP", "SMS", "EMAIL");
        assertThat(body.getList("categories[0].channels.enabled", Boolean.class)).containsOnly(true);
        assertThat(body.getList("categories[0].channels.available", Boolean.class))
                .containsExactly(false, true, true, false);

        support.verifiedEmail(c.id());
        support.device(c.id(), "token-" + UUID.randomUUID());
        given().auth().oauth2(c.token()).get(BASE).then().statusCode(200)
                .body("categories[3].channels.available", contains(true, true, true, true));
    }

    @Test
    void disablingSmsMakesTheNextSendUseTheNextChannel() {
        Client c = support.customer();
        String email = support.verifiedEmail(c.id());
        update(c, "PROPOSAL", Map.of("WHATSAPP", false, "SMS", false)).then().statusCode(200)
                .body("categories.find { it.category == 'PROPOSAL' }.channels.enabled",
                        contains(true, false, false, true))
                .body("categories.find { it.category == 'QUOTE' }.channels.enabled", contains(true, true, true, true));

        UUID id = tx.execute(s -> notifications.notify(c.id(), "PROPOSAL_EXPIRED",
                Map.of("proposalNumber", "PR-2027-000010"), NotificationCategory.PROPOSAL));
        assertThat(support.external(id)).extracting(r -> r.get("channel")).containsExactly("EMAIL");
        await(() -> !channels.sentTo(email).isEmpty());
        assertThat(channels.sentTo(c.phone())).isEmpty();

        update(c, "PROPOSAL", Map.of("SMS", true)).then().statusCode(200)
                .body("categories.find { it.category == 'PROPOSAL' }.channels.enabled",
                        contains(true, false, true, true));
    }

    @Test
    void mandatoryCategoryNeedsSmsWhatsAppOrEmail() {
        Client c = support.customer();
        update(c, "PAYMENT", Map.of("SMS", false, "WHATSAPP", false)).then().statusCode(200);
        given().auth().oauth2(c.token()).contentType(ContentType.JSON)
                .body(Map.of("categories", List.of(
                        Map.of("category", "PROPOSAL", "channels", List.of(Map.of("channel", "PUSH", "enabled", false))),
                        Map.of("category", "PAYMENT", "channels", List.of(Map.of("channel", "EMAIL", "enabled", false),
                                Map.of("channel", "PUSH", "enabled", true))))))
                .put(BASE).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("categories[1].channels"))
                .body("errors[0].code", equalTo("MANDATORY_CATEGORY_NEEDS_CHANNEL"));
        // nada foi gravado
        given().auth().oauth2(c.token()).get(BASE).then()
                .body("categories.find { it.category == 'PROPOSAL' }.channels.enabled", contains(true, true, true, true))
                .body("categories.find { it.category == 'PAYMENT' }.channels.enabled",
                        contains(true, false, false, true));
    }

    @Test
    void staffIsForbidden() {
        given().get(BASE).then().statusCode(401);
        given().auth().oauth2(support.platformAdminToken()).get(BASE).then().statusCode(403);
    }

    private io.restassured.response.Response update(Client c, String category, Map<String, Boolean> channelsEnabled) {
        List<Map<String, Object>> list = channelsEnabled.entrySet().stream()
                .map(e -> Map.<String, Object>of("channel", e.getKey(), "enabled", e.getValue())).toList();
        return given().auth().oauth2(c.token()).contentType(ContentType.JSON)
                .body(Map.of("categories", List.of(Map.of("category", category, "channels", list))))
                .put(BASE);
    }
}
