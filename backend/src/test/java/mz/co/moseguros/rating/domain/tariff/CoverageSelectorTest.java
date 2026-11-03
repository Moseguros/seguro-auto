package mz.co.moseguros.rating.domain.tariff;

import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.driverBornOn;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.input;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.mtpl;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.options;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.perPerson;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.product;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.requested;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.simple;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.vehicle;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.vehicleValue;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector.SelectedCoverage;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector.Selection;
import org.junit.jupiter.api.Test;

/** Selecção das coberturas e do capital (S3-01 §9, "Selecção"). */
class CoverageSelectorTest {

    private static final ProductDefinition AUTO = product(ProductType.COMPREHENSIVE, mtpl(),
            options(CoverageCode.VTPL, 500_000_000L, 1_000_000_000L),
            vehicleValue(CoverageCode.OD_COLLISION), vehicleValue(CoverageCode.OD_FIRE),
            vehicleValue(CoverageCode.OD_THEFT, CoverageCode.OD_COLLISION, CoverageCode.OD_FIRE),
            perPerson(CoverageCode.OCCUPANTS, 50_000_000L));

    private static List<String> codes(Selection s) {
        return s.coverages().stream().map(c -> c.coverage().code().name()).toList();
    }

    @Test
    void mandatoryCoveragesAreAddedAndTheOrderIsTheProductOrder() {
        Selection s = CoverageSelector.select(simple(365, requested(CoverageCode.OD_FIRE, null),
                requested(CoverageCode.OD_COLLISION, null), requested(CoverageCode.OD_FIRE, 1L)), AUTO);
        assertThat(s.isAvailable()).isTrue();
        assertThat(codes(s)).containsExactly("MTPL", "OD_COLLISION", "OD_FIRE");
        assertThat(CoverageSelector.select(simple(365), AUTO).coverages()).hasSize(1);
    }

    @Test
    void aRequestedCoverageTheProductDoesNotHaveIsNotOffered() {
        Selection s = CoverageSelector.select(simple(365, requested(CoverageCode.LEGAL, null),
                requested(CoverageCode.OD_FIRE, null), requested(CoverageCode.SRCC, null)), AUTO);
        assertThat(s.isAvailable()).isFalse();
        assertThat(s.unavailable().reason()).isEqualTo(UnavailableReason.COVERAGE_NOT_OFFERED);
        assertThat(s.unavailable().detail()).isEqualTo(Map.of("codes", List.of("LEGAL", "SRCC")));
    }

    @Test
    void requiredCoveragesMustBeChosen() {
        Selection s = CoverageSelector.select(simple(365, requested(CoverageCode.OD_THEFT, null),
                requested(CoverageCode.OD_FIRE, null)), AUTO);
        assertThat(s.unavailable().reason()).isEqualTo(UnavailableReason.COVERAGE_REQUIREMENTS_NOT_MET);
        assertThat(s.unavailable().detail()).isEqualTo(Map.of("coverageCode", "OD_THEFT",
                "missing", List.of("OD_COLLISION")));

        Selection ok = CoverageSelector.select(simple(365, requested(CoverageCode.OD_THEFT, null),
                requested(CoverageCode.OD_FIRE, null), requested(CoverageCode.OD_COLLISION, null)), AUTO);
        assertThat(codes(ok)).containsExactly("MTPL", "OD_COLLISION", "OD_FIRE", "OD_THEFT");
    }

    @Test
    void capitalOfFixedPerPersonAndVehicleValueCoverages() {
        Selection s = CoverageSelector.select(simple(365, requested(CoverageCode.OCCUPANTS, 1L),
                requested(CoverageCode.OD_COLLISION, 2L)), AUTO);
        assertThat(s.coverages()).extracting(SelectedCoverage::capital)
                .containsExactly(300_000_000L, 85_000_000L, 50_000_000L);

        Selection noValue = CoverageSelector.select(input(ProductType.COMPREHENSIVE, vehicle(1600, null, false),
                driverBornOn(LocalDate.of(1980, 1, 1)), 365, requested(CoverageCode.OD_COLLISION, null)), AUTO);
        assertThat(noValue.unavailable().reason()).isEqualTo(UnavailableReason.VEHICLE_VALUE_REQUIRED);
        assertThat(noValue.unavailable().detail()).isEqualTo(Map.of("coverageCode", "OD_COLLISION"));
    }

    @Test
    void optionsCapitalIsTheSmallestOptionAtLeastTheRequestedOne() {
        // sem capital pedido: a menor opção
        assertThat(CoverageSelector.select(simple(365, requested(CoverageCode.VTPL, null)), AUTO)
                .coverages().get(1).capital()).isEqualTo(500_000_000L);
        // pedido 4 000 000 MT com opções de 5 e 10 milhões → 5 milhões
        assertThat(CoverageSelector.select(simple(365, requested(CoverageCode.VTPL, 400_000_000L)), AUTO)
                .coverages().get(1).capital()).isEqualTo(500_000_000L);
        assertThat(CoverageSelector.select(simple(365, requested(CoverageCode.VTPL, 500_000_001L)), AUTO)
                .coverages().get(1).capital()).isEqualTo(1_000_000_000L);
        assertThat(CoverageSelector.select(simple(365, requested(CoverageCode.VTPL, 1_000_000_000L)), AUTO)
                .coverages().get(1).capital()).isEqualTo(1_000_000_000L);

        // pedido 20 milhões → CAPITAL_NOT_OFFERED
        Selection tooMuch = CoverageSelector.select(simple(365, requested(CoverageCode.VTPL, 2_000_000_000L)), AUTO);
        assertThat(tooMuch.unavailable().reason()).isEqualTo(UnavailableReason.CAPITAL_NOT_OFFERED);
        assertThat(tooMuch.unavailable().detail()).isEqualTo(Map.of("coverageCode", "VTPL",
                "requested", 2_000_000_000L, "maxOffered", 1_000_000_000L));
    }
}
