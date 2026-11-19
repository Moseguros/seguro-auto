package mz.co.moseguros.policy.application;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.vehicle.VehicleTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code PolicyVehicleRemovalGuard} (S6-01 §9.1; D29): um veículo com apólice {@code ACTIVE} ou
 * {@code SUSPENDED} em vigor não pode ser retirado; com a apólice terminada, pode.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyVehicleRemovalGuardIT {

    static final String VEHICLES = "/api/v1/me/vehicles/{id}";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    IntentStateService states;

    @Autowired
    TransactionTemplate tx;

    PolicyTestSupport support;
    UUID insurer;

    @BeforeAll
    void setUpAll() {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.payments().insurer("Seguradora Guard").id();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    record Insured(Customer customer, UUID vehicleId, UUID policyId) {
    }

    Insured insured(String status, Duration endFromNow) {
        Customer c = support.payments().customer();
        String plate = PolicyTestSupport.plate();
        UUID vehicle = VehicleTestSupport.newVehicle(c.token(), plate);
        Instant now = Instant.now();
        UUID policy = support.insert(Row.of(insurer, c.id(), plate, status, now.minus(Duration.ofDays(400)),
                now.plus(endFromNow)).vehicle(vehicle));
        return new Insured(c, vehicle, policy);
    }

    String vehicleStatus(UUID vehicleId) {
        return support.owner().queryForObject("select status from vehicle.vehicle where id = ?", String.class,
                vehicleId);
    }

    @Test
    void aVehicleWithAPolicyInForceCannotBeRemoved() {
        for (String status : new String[] {"ACTIVE", "SUSPENDED"}) {
            Insured i = insured(status, Duration.ofDays(30));
            given().auth().oauth2(i.customer().token()).delete(VEHICLES, i.vehicleId())
                    .then().statusCode(409).body("code", equalTo("VEHICLE_REMOVAL_BLOCKED"));
            assertThat(vehicleStatus(i.vehicleId())).as(status).isEqualTo("ACTIVE");
        }
    }

    @Test
    void aVehicleWhosePolicyEndedCanBeRemoved() {
        Insured expired = insured("ACTIVE", Duration.ofDays(30));
        support.owner().update("update policy.policy set status = 'EXPIRED' where id = ?", expired.policyId());
        given().auth().oauth2(expired.customer().token()).delete(VEHICLES, expired.vehicleId())
                .then().statusCode(204);
        assertThat(vehicleStatus(expired.vehicleId())).isEqualTo("REMOVED");

        Insured past = insured("ACTIVE", Duration.ofDays(-1)); // end_at passado, ainda ACTIVE
        given().auth().oauth2(past.customer().token()).delete(VEHICLES, past.vehicleId()).then().statusCode(204);

        Insured cancelled = insured("CANCELLED", Duration.ofDays(30));
        given().auth().oauth2(cancelled.customer().token()).delete(VEHICLES, cancelled.vehicleId())
                .then().statusCode(204);
    }

    @Test
    void aVehicleWithoutPoliciesCanBeRemoved() {
        Customer c = support.payments().customer();
        UUID vehicle = VehicleTestSupport.newVehicle(c.token(), PolicyTestSupport.plate());
        given().auth().oauth2(c.token()).delete(VEHICLES, vehicle).then().statusCode(204);
    }
}
