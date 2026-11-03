package mz.co.moseguros.rating.domain.tariff;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/** Peças pequenas para os testes do avaliador, do selector e do validador. */
final class TariffFixtures {

    static final LocalDate START = LocalDate.of(2027, 1, 15);

    private TariffFixtures() {
    }

    static JsonNode json(String text) {
        try {
            return ReferenceData.MAPPER.readTree(text.replace('\'', '"'));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static CoverageDefinition fixed(CoverageCode code, boolean mandatory, long capital) {
        return new CoverageDefinition(code, mandatory, List.of(), CapitalType.FIXED, capital, null,
                DeductibleType.NONE, null, null, null);
    }

    static CoverageDefinition mtpl() {
        return fixed(CoverageCode.MTPL, true, 300_000_000L);
    }

    static CoverageDefinition options(CoverageCode code, Long... options) {
        return new CoverageDefinition(code, false, List.of(), CapitalType.OPTIONS, null, List.of(options),
                DeductibleType.NONE, null, null, null);
    }

    static CoverageDefinition vehicleValue(CoverageCode code, CoverageCode... requires) {
        return new CoverageDefinition(code, false, List.of(requires), CapitalType.VEHICLE_VALUE, null, null,
                DeductibleType.FIXED, 500_000L, null, null);
    }

    static CoverageDefinition perPerson(CoverageCode code, long capital) {
        return new CoverageDefinition(code, false, List.of(), CapitalType.PER_PERSON, capital, null,
                DeductibleType.NONE, null, null, null);
    }

    static ProductDefinition product(ProductType type, CoverageDefinition... coverages) {
        return new ProductDefinition(type, List.of(coverages));
    }

    static RatedVehicle vehicle(Integer engineCc, Long commercialValue, boolean tracker) {
        return new RatedVehicle(null, "AAA-100-MC", "Toyota", "Corolla", 2020, engineCc, VehicleCategory.LIGHT_PASSENGER,
                VehicleUsage.PRIVATE, 5, null, Province.MAPUTO_CITY, commercialValue, tracker);
    }

    static RatedDriver driverBornOn(LocalDate birthDate) {
        return new RatedDriver(null, birthDate, LocalDate.of(2010, 1, 1), List.of("B"));
    }

    static RatingInput input(ProductType type, RatedVehicle vehicle, RatedDriver driver, int days,
                             RequestedCoverage... requested) {
        return new RatingInput(type, List.of(requested), vehicle, driver, START, days);
    }

    static RatingInput simple(int days, RequestedCoverage... requested) {
        return input(ProductType.MTPL_ONLY, vehicle(1600, 85_000_000L, false), driverBornOn(LocalDate.of(1980, 5, 5)),
                days, requested);
    }

    static RequestedCoverage requested(CoverageCode code, Long capital) {
        return new RequestedCoverage(code, capital);
    }
}
