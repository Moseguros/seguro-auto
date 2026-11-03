package mz.co.moseguros.catalog;

import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static mz.co.moseguros.catalog.CatalogTestSupport.ownDamage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@link CatalogApi} (S2-04 §6): lê as versões publicadas de todas as seguradoras em qualquer
 * âmbito (aqui {@code CUSTOMER}) e nunca devolve rascunhos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class CatalogApiIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    CatalogApi catalogApi;

    CatalogTestSupport support;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
    }

    @Test
    void findPublishedVersionsSeesBothInsurersInCustomerScope() {
        Insurer a = support.insurer("Api Seg A " + UUID.randomUUID().toString().substring(0, 4));
        Insurer b = support.insurer("Api Seg B " + UUID.randomUUID().toString().substring(0, 4));
        Created pa = support.published(a.manager(), "COMPREHENSIVE", List.of("BUS"), List.of("PASSENGER_TRANSPORT"),
                List.of(mtpl(MIN_CAPITAL), ownDamage("OD_COLLISION")));
        Created pb = support.published(b.manager(), "MTPL_ONLY", List.of("BUS"), List.of("PASSENGER_TRANSPORT"),
                List.of(mtpl(MIN_CAPITAL)));
        Created draft = support.product(a.manager(), "MTPL_ONLY");

        List<ProductVersionView> found = asCustomer(() -> catalogApi.findPublishedVersions(VehicleCategory.BUS,
                VehicleUsage.PASSENGER_TRANSPORT, null));
        List<ProductVersionView> ours = found.stream()
                .filter(v -> v.insurerId().equals(a.id()) || v.insurerId().equals(b.id())).toList();
        assertThat(ours).extracting(ProductVersionView::id)
                .containsExactlyInAnyOrder(pa.versionId(), pb.versionId());

        ProductVersionView va = ours.stream().filter(v -> v.id().equals(pa.versionId())).findFirst().orElseThrow();
        assertThat(va.productId()).isEqualTo(pa.productId());
        assertThat(va.productType()).isEqualTo(ProductType.COMPREHENSIVE);
        assertThat(va.status()).isEqualTo("PUBLISHED");
        assertThat(va.versionNo()).isEqualTo(1);
        assertThat(va.validFrom()).isNotNull();
        assertThat(va.publishedAt()).isNotNull();
        assertThat(va.vehicleCategories()).containsExactly(VehicleCategory.BUS);
        assertThat(va.coverages()).extracting(CoverageView::code)
                .containsExactly(CoverageCode.MTPL, CoverageCode.OD_COLLISION);
        assertThat(va.coverages().get(0).capital()).isEqualTo(Money.ofCentavos(MIN_CAPITAL));
        assertThat(va.coverages().get(1).deductibleMinAmount()).isEqualTo(Money.ofCentavos(2_500_000));
        assertThat(va.documents()).hasSize(2);
        assertThat(va.generalConditionsFileId()).isNotNull();
        assertThat(va.preContractInfoFileId()).isNotNull().isNotEqualTo(va.generalConditionsFileId());
        assertThat(va.rules().isObject()).isTrue();

        assertThat(asCustomer(() -> catalogApi.findPublishedVersions(VehicleCategory.BUS, null, ProductType.MTPL_ONLY)))
                .extracting(ProductVersionView::id).contains(pb.versionId()).doesNotContain(pa.versionId());

        assertThat(asCustomer(() -> catalogApi.version(pb.versionId())).productCode()).isNotBlank();
        assertThatThrownBy(() -> asCustomer(() -> catalogApi.version(draft.versionId())))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("PRODUCT_VERSION_NOT_FOUND"));
        assertThatThrownBy(() -> catalogApi.version(UUID.randomUUID()))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("PRODUCT_VERSION_NOT_FOUND"));
        // o âmbito de quem chama volta como estava
        assertThat(asCustomer(() -> TenantContext.current().scope().name())).isEqualTo("CUSTOMER");
    }

    private static <T> T asCustomer(Supplier<T> work) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.customer(UUID.randomUUID()));
        try {
            return work.get();
        } finally {
            TenantContext.set(previous);
        }
    }
}
