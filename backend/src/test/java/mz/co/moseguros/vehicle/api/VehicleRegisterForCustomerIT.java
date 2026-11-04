package mz.co.moseguros.vehicle.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.JwtClaimsConverter;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import mz.co.moseguros.vehicle.NewDriver;
import mz.co.moseguros.vehicle.NewVehicle;
import mz.co.moseguros.vehicle.RegisteredVehicle;
import mz.co.moseguros.vehicle.VehicleApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code VehicleApi.registerForCustomer} (S4-06 §3) chamado no âmbito {@code ORG} de um mediador, numa
 * transacção como a da proposta: cria o veículo e o condutor habitual na conta do cliente, reutiliza-os
 * com a mesma matrícula e o mesmo chassis, recusa outro chassis e um chassis inválido, desfaz-se com a
 * transacção de quem chama, e o cliente vê o veículo em {@code GET /me/vehicles}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleRegisterForCustomerIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    VehicleApi vehicleApi;

    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    PlatformTransactionManager transactionManager;

    UUID brokerOrg;
    UserAccount brokerUser;
    String brokerToken;

    @BeforeAll
    void createBroker() {
        brokerOrg = fixtures.organization(OrganizationType.BROKER, "Corretora Veículos").getId();
        brokerUser = fixtures.staff("Mediador Veículos", brokerOrg, "BROKER");
        brokerToken = identityApi.issueTokens(brokerUser, null).accessToken();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
        clock.reset();
    }

    JdbcTemplate owner() {
        return fixtures.owner();
    }

    /** Corre como o mediador (o que o TenantContextFilter e o conversor fariam num pedido), numa transacção. */
    <T> T asBroker(Supplier<T> work, boolean rollback) {
        TenantContext.set(TenantSnapshot.org(brokerUser.getId(), brokerOrg, null));
        SecurityContextHolder.getContext().setAuthentication(
                new JwtClaimsConverter().convert(jwtDecoder.decode(brokerToken)));
        try {
            return new TransactionTemplate(transactionManager).execute(status -> {
                T result = work.get();
                if (rollback) {
                    status.setRollbackOnly();
                }
                return result;
            });
        } finally {
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    static NewVehicle vehicle(String plate, String chassis) {
        return new NewVehicle(plate, chassis, "Toyota", "Corolla", 2024, 1600, VehicleCategory.LIGHT_PASSENGER, 5,
                null, VehicleUsage.PRIVATE, Province.MAPUTO_CITY, Money.ofCentavos(120_000_000L), false);
    }

    static NewDriver driver(String licence) {
        return new NewDriver("João Carlos Sitoe", LocalDate.of(1991, 9, 9), licence, List.of("B"),
                LocalDate.of(2011, 4, 4), true);
    }

    int count(String sql, Object... args) {
        Integer n = owner().queryForObject(sql, Integer.class, args);
        return n == null ? 0 : n;
    }

    @Test
    void createsReusesAndRefuses() {
        UserAccount customer = fixtures.customer("Cliente Mediado");
        String plate = QuoteTestSupport.randomPlate();
        String licence = "MAP-" + QuoteTestSupport.digits(6);

        RegisteredVehicle first = asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(plate.toLowerCase().replace('-', ' '), "JTD-BR32E720012345"), driver(licence)), false);
        assertThat(first.vehicleCreated()).isTrue();
        assertThat(first.driverCreated()).isTrue();
        assertThat(first.vehicle().ownerUserId()).isEqualTo(customer.getId());
        assertThat(first.vehicle().plate()).isEqualTo(plate);
        assertThat(first.vehicle().chassisNumber()).isEqualTo("JTDBR32E720012345");
        assertThat(first.vehicle().habitualDriverId()).isEqualTo(first.driver().driverId());
        assertThat(first.driver().licenceNumberMasked()).isEqualTo("****" + licence.substring(licence.length() - 4));
        assertThat(count("select count(*) from vehicle.vehicle_driver where vehicle_id = ? and driver_id = ? and is_habitual",
                first.vehicle().vehicleId(), first.driver().driverId())).isEqualTo(1);
        assertThat(count("""
                select count(*) from audit.audit_event where actor_type = 'STAFF' and actor_id = ?
                   and action in ('VEHICLE.CREATE', 'DRIVER.CREATE', 'VEHICLE.DRIVERS_UPDATE')""",
                brokerUser.getId().toString())).isEqualTo(3);
        assertThat(owner().queryForObject("""
                select after_state::text from audit.audit_event where action = 'DRIVER.CREATE' and entity_id = ?""",
                String.class, first.driver().driverId().toString())).doesNotContain(licence);

        // mesma matrícula e mesmo chassis, mesma carta: reutiliza os dois sem os alterar
        RegisteredVehicle again = asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(plate, "JTDBR32E720012345"), driver(licence)), false);
        assertThat(again.vehicleCreated()).isFalse();
        assertThat(again.driverCreated()).isFalse();
        assertThat(again.vehicle().vehicleId()).isEqualTo(first.vehicle().vehicleId());
        assertThat(again.driver().driverId()).isEqualTo(first.driver().driverId());
        assertThat(count("select count(*) from vehicle.vehicle where owner_user_id = ?", customer.getId())).isEqualTo(1);
        assertThat(count("select count(*) from vehicle.driver where owner_user_id = ?", customer.getId())).isEqualTo(1);

        // mesma matrícula, outro chassis → 409
        assertThatThrownBy(() -> asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(plate, "OTHERCHASSIS123"), driver(licence)), false))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("VEHICLE_ALREADY_REGISTERED"));

        // chassis inválido → VALIDATION_ERROR com o campo prefixado
        assertThatThrownBy(() -> asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(QuoteTestSupport.randomPlate(), "X!"), driver("12")), false))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
                    @SuppressWarnings("unchecked")
                    List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
                    assertThat(errors).extracting(FieldError::field, FieldError::code)
                            .contains(org.assertj.core.groups.Tuple.tuple("vehicle.chassisNumber", "INVALID_CHASSIS"),
                                    org.assertj.core.groups.Tuple.tuple("driver.licenceNumber",
                                            "INVALID_LICENCE_NUMBER"));
                });

        // um segundo veículo com outro condutor: o condutor fica habitual do novo veículo
        String plate2 = QuoteTestSupport.randomPlate();
        String licence2 = "MAP-" + QuoteTestSupport.digits(6);
        NewDriver other = new NewDriver("Maria José Sitoe", LocalDate.of(1993, 1, 2), licence2, List.of("B"),
                LocalDate.of(2015, 6, 1), false);
        RegisteredVehicle second = asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(plate2, "JTDBR32E720099999"), other), false);
        // e o primeiro condutor numa segunda viatura fica não habitual
        RegisteredVehicle third = asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(plate2, "JTDBR32E720099999"), driver(licence)), false);
        assertThat(third.vehicle().vehicleId()).isEqualTo(second.vehicle().vehicleId());
        assertThat(third.vehicle().habitualDriverId()).isEqualTo(second.driver().driverId());
        assertThat(count("""
                select count(*) from vehicle.vehicle_driver where vehicle_id = ? and driver_id = ? and not is_habitual""",
                second.vehicle().vehicleId(), first.driver().driverId())).isEqualTo(1);

        // o cliente vê os veículos
        String customerToken = identityApi.issueTokens(customer, null).accessToken();
        given().auth().oauth2(customerToken).get("/api/v1/me/vehicles").then().statusCode(200)
                .body("totalElements", equalTo(2));
        given().auth().oauth2(customerToken).get("/api/v1/me/drivers").then().statusCode(200)
                .body("totalElements", equalTo(2));
    }

    @Test
    void itRunsInTheCallersTransaction() {
        UserAccount customer = fixtures.customer("Cliente Desfeito");
        RegisteredVehicle rolledBack = asBroker(() -> vehicleApi.registerForCustomer(customer.getId(),
                vehicle(QuoteTestSupport.randomPlate(), "JTDBR32E720012345"),
                driver("MAP-" + QuoteTestSupport.digits(6))), true);
        assertThat(rolledBack.vehicleCreated()).isTrue();
        assertThat(count("select count(*) from vehicle.vehicle where owner_user_id = ?", customer.getId())).isZero();
        assertThat(count("select count(*) from vehicle.driver where owner_user_id = ?", customer.getId())).isZero();
        assertThat(count("select count(*) from audit.audit_event where entity_id = ?",
                rolledBack.vehicle().vehicleId().toString())).isZero();
    }
}
