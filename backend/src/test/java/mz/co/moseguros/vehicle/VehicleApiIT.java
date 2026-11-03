package mz.co.moseguros.vehicle;

import static mz.co.moseguros.vehicle.VehicleTestSupport.newDriver;
import static mz.co.moseguros.vehicle.VehicleTestSupport.newVehicle;
import static mz.co.moseguros.vehicle.VehicleTestSupport.putDrivers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
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
import org.springframework.test.context.ActiveProfiles;

/** {@link VehicleApi} (S2-03 §7): visibilidade pelo âmbito de quem chama e conteúdo dos snapshots. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class VehicleApiIT {

    private static final String LICENCE = "MAP-1234567";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    VehicleApi vehicleApi;

    @Autowired
    ObjectMapper objectMapper;

    UserAccount customer;
    UUID vehicleId;
    UUID driverId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        customer = fixtures.customer("Ana Cliente");
        String token = identityApi.issueTokens(customer, null).accessToken();
        vehicleId = newVehicle(token, "LLL-100-MC");
        driverId = newDriver(token, "Ana Maria Mussa", LICENCE);
        putDrivers(token, vehicleId, 0, driverId, true).then().statusCode(200);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void findOwnedOnlyForTheOwner() {
        assertThat(asCustomer(customer.getId(), () -> vehicleApi.findOwned(vehicleId, customer.getId())))
                .hasValueSatisfying(v -> {
                    assertThat(v.plate()).isEqualTo("LLL-100-MC");
                    assertThat(v.plateFormatCode()).isEqualTo("STANDARD");
                    assertThat(v.habitualDriverId()).isEqualTo(driverId);
                    assertThat(v.status()).isEqualTo("ACTIVE");
                    assertThat(v.version()).isEqualTo(1);
                });
        UUID other = UUID.randomUUID();
        assertThat(asCustomer(customer.getId(), () -> vehicleApi.findOwned(vehicleId, other))).isEmpty();
        // Outro cliente não vê o veículo (RLS), nem pedindo pelo dono certo.
        assertThat(asCustomer(other, () -> vehicleApi.findOwned(vehicleId, customer.getId()))).isEmpty();
        // Em SYSTEM vê, mas só para o dono.
        assertThat(asSystem(() -> vehicleApi.findOwned(vehicleId, customer.getId()))).isPresent();
        assertThat(asSystem(() -> vehicleApi.findOwned(vehicleId, other))).isEmpty();
    }

    @Test
    void snapshotHasTheHabitualDriver() {
        VehicleSnapshot snapshot = asCustomer(customer.getId(), () -> vehicleApi.snapshot(vehicleId));
        assertThat(snapshot.vehicleId()).isEqualTo(vehicleId);
        assertThat(snapshot.ownerUserId()).isEqualTo(customer.getId());
        assertThat(snapshot.plate()).isEqualTo("LLL-100-MC");
        assertThat(snapshot.habitualDriverId()).isEqualTo(driverId);
        assertThat(snapshot.takenAt()).isNotNull();

        assertThatThrownBy(() -> asCustomer(UUID.randomUUID(), () -> vehicleApi.snapshot(vehicleId)))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("VEHICLE_NOT_FOUND"));
        assertThatThrownBy(() -> asSystem(() -> vehicleApi.snapshot(UUID.randomUUID())))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("VEHICLE_NOT_FOUND"));
    }

    @Test
    void driverSnapshotNeverHasTheLicenceNumberInClear() throws Exception {
        DriverSnapshot snapshot = asCustomer(customer.getId(), () -> vehicleApi.driverSnapshot(driverId));
        assertThat(snapshot.fullName()).isEqualTo("Ana Maria Mussa");
        assertThat(snapshot.licenceNumberMasked()).isEqualTo("****4567");
        assertThat(snapshot.licenceNumberHash()).hasSize(64);
        assertThat(snapshot.licenceCategories()).containsExactly("B");

        String json = objectMapper.writeValueAsString(snapshot);
        assertThat(json).doesNotContain("MAP1234567").doesNotContain("MAP-1234567").doesNotContain("1234567");
        assertThat(snapshot.toString()).doesNotContain("MAP1234567");

        assertThatThrownBy(() -> asCustomer(UUID.randomUUID(), () -> vehicleApi.driverSnapshot(driverId)))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("DRIVER_NOT_FOUND"));
    }

    @Test
    void orgScopeSeesNothing() {
        UUID org = UUID.randomUUID();
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), org, org));
        assertThat(vehicleApi.findOwned(vehicleId, customer.getId())).isEmpty();
        assertThatThrownBy(() -> vehicleApi.snapshot(vehicleId)).isInstanceOf(DomainException.class);
    }

    private static <T> T asCustomer(UUID userId, Supplier<T> action) {
        TenantContext.set(TenantSnapshot.customer(userId));
        try {
            return action.get();
        } finally {
            TenantContext.clear();
        }
    }

    private static <T> T asSystem(Supplier<T> action) {
        TenantContext.set(TenantSnapshot.system(null));
        try {
            return action.get();
        } finally {
            TenantContext.clear();
        }
    }
}
