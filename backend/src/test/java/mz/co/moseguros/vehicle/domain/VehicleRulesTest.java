package mz.co.moseguros.vehicle.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.StaticPlateFormatProvider;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.vehicle.domain.VehicleRules.DriverAssignment;
import mz.co.moseguros.vehicle.domain.VehicleRules.ValidVehicle;
import mz.co.moseguros.vehicle.domain.VehicleRules.VehicleData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Cada linha da tabela do pedido de veículo (S2-03 §2) e a lista de condutores (§3.5). */
class VehicleRulesTest {

    /** 31/12/2026 22:30 UTC = 01/01/2027 00:30 em Maputo: o ano máximo é 2028. */
    static final Instant NEW_YEAR_IN_MAPUTO = Instant.parse("2026-12-31T22:30:00Z");

    private final PlateParser plates = StaticPlateFormatProvider.defaultParser();

    static VehicleData valid() {
        return new VehicleData("AAA-100-MC", "JTDBR32E720012345", "Toyota", "Corolla", 2018, 1598,
                VehicleCategory.LIGHT_PASSENGER, 5, null, VehicleUsage.PRIVATE, Province.MAPUTO_CITY,
                Money.ofCentavos(85_000_000), null);
    }

    private ValidVehicle validate(VehicleData data) {
        return VehicleRules.validate(data, plates, NEW_YEAR_IN_MAPUTO);
    }

    private List<FieldError> errors(VehicleData data) {
        try {
            validate(data);
        } catch (DomainException e) {
            assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
            assertThat(e.getHttpStatus()).isEqualTo(400);
            @SuppressWarnings("unchecked")
            List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
            return errors;
        }
        throw new AssertionError("expected VALIDATION_ERROR");
    }

    private List<String> codes(VehicleData data) {
        return errors(data).stream().map(e -> e.field() + "/" + e.code()).toList();
    }

    private static VehicleData with(UnaryOperator<Builder> change) {
        return change.apply(new Builder(valid())).build();
    }

    @Test
    void validRequestIsNormalised() {
        ValidVehicle v = validate(new VehicleData("aaa 100 mc", " jtdbr32e-7200 12345 ", "  Toyota ", " Corolla  ",
                2018, 1598, VehicleCategory.LIGHT_PASSENGER, 5, null, VehicleUsage.PRIVATE, Province.MAPUTO_CITY,
                null, null));
        assertThat(v.plate().value()).isEqualTo("AAA-100-MC");
        assertThat(v.plate().formatCode()).isEqualTo("STANDARD");
        assertThat(v.chassisNumber()).isEqualTo("JTDBR32E720012345");
        assertThat(v.make()).isEqualTo("Toyota");
        assertThat(v.model()).isEqualTo("Corolla");
        assertThat(v.hasTracker()).isFalse();
        assertThat(v.commercialValue()).isNull();

        assertThat(validate(with(b -> b.hasTracker(true))).hasTracker()).isTrue();
        assertThat(validate(with(b -> b.plate("MLA 12 34"))).plate().value()).isEqualTo("MLA-12-34");
    }

    @Test
    void everyRequiredFieldMissingIsReportedTogether() {
        assertThat(codes(new VehicleData(null, " ", "", null, null, null, null, null, null, null, null, null, null)))
                .containsExactly("plate/REQUIRED", "chassisNumber/REQUIRED", "make/REQUIRED", "model/REQUIRED",
                        "year/REQUIRED", "category/REQUIRED", "seats/REQUIRED", "usage/REQUIRED",
                        "provinceOfCirculation/REQUIRED");
    }

    @Test
    void plate() {
        assertThat(codes(with(b -> b.plate("XYZ")))).containsExactly("plate/INVALID_PLATE");
        assertThat(codes(with(b -> b.plate("AAA-100-MC-AAA-100-MC")))).containsExactly("plate/INVALID_PLATE");
    }

    @ParameterizedTest
    @CsvSource({"ABCD", "JTDBR32E720012345JTDBR32E7200123", "JTD*BR32E72"})
    void chassisInvalid(String chassis) {
        assertThat(codes(with(b -> b.chassis(chassis)))).containsExactly("chassisNumber/INVALID_CHASSIS");
    }

    @Test
    void chassisLimits() {
        assertThat(validate(with(b -> b.chassis("ABCDE"))).chassisNumber()).isEqualTo("ABCDE");
        assertThat(validate(with(b -> b.chassis("A".repeat(30)))).chassisNumber()).hasSize(30);
    }

    @Test
    void makeAndModel() {
        assertThat(codes(with(b -> b.make("   ").model(null)))).containsExactly("make/REQUIRED", "model/REQUIRED");
        assertThat(codes(with(b -> b.make("M".repeat(61)).model("X".repeat(61)))))
                .containsExactly("make/OUT_OF_RANGE", "model/OUT_OF_RANGE");
        assertThat(validate(with(b -> b.make("M".repeat(60)).model("X"))).make()).hasSize(60);
    }

    @Test
    void yearUsesTheYearInMaputo() {
        assertThat(validate(with(b -> b.year(2028))).year()).isEqualTo(2028);
        assertThat(codes(with(b -> b.year(2029)))).containsExactly("year/INVALID_YEAR");
        assertThat(validate(with(b -> b.year(1900))).year()).isEqualTo(1900);
        assertThat(codes(with(b -> b.year(1899)))).containsExactly("year/INVALID_YEAR");
        // Em UTC ainda é 2026: com o ano UTC, 2028 seria recusado.
        assertThat(codes(new Builder(valid()).year(2028).build(), Instant.parse("2026-12-31T21:59:00Z")))
                .containsExactly("year/INVALID_YEAR");
    }

    private List<String> codes(VehicleData data, Instant now) {
        try {
            VehicleRules.validate(data, plates, now);
        } catch (DomainException e) {
            @SuppressWarnings("unchecked")
            List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
            return errors.stream().map(f -> f.field() + "/" + f.code()).toList();
        }
        return List.of();
    }

    @Test
    void engineCc() {
        assertThat(codes(with(b -> b.engineCc(null)))).containsExactly("engineCc/REQUIRED");
        assertThat(codes(with(b -> b.engineCc(0)))).containsExactly("engineCc/OUT_OF_RANGE");
        assertThat(codes(with(b -> b.engineCc(30_001)))).containsExactly("engineCc/OUT_OF_RANGE");
        assertThat(validate(with(b -> b.engineCc(1))).engineCc()).isEqualTo(1);
        assertThat(validate(with(b -> b.engineCc(30_000))).engineCc()).isEqualTo(30_000);
    }

    @Test
    void trailerHasNoEngineNoSeatsAndNeedsTheWeight() {
        VehicleData trailer = with(b -> b.category(VehicleCategory.TRAILER).engineCc(null).seats(0).weight(3500));
        assertThat(validate(trailer).category()).isEqualTo(VehicleCategory.TRAILER);
        assertThat(codes(with(b -> b.category(VehicleCategory.TRAILER).engineCc(1200).seats(0).weight(3500))))
                .containsExactly("engineCc/NOT_ALLOWED");
        assertThat(codes(with(b -> b.category(VehicleCategory.TRAILER).engineCc(null).seats(1).weight(3500))))
                .containsExactly("seats/OUT_OF_RANGE");
        assertThat(codes(with(b -> b.category(VehicleCategory.TRAILER).engineCc(null).seats(0).weight(null))))
                .containsExactly("grossWeightKg/REQUIRED");
    }

    @Test
    void heavyNeedsTheWeight() {
        assertThat(codes(with(b -> b.category(VehicleCategory.HEAVY).seats(2).weight(null))))
                .containsExactly("grossWeightKg/REQUIRED");
        assertThat(validate(with(b -> b.category(VehicleCategory.HEAVY).seats(2).weight(40_000))).grossWeightKg())
                .isEqualTo(40_000);
    }

    @Test
    void grossWeightIsOptionalForTheOthersButInRange() {
        assertThat(validate(with(b -> b.weight(null))).grossWeightKg()).isNull();
        assertThat(validate(with(b -> b.weight(1))).grossWeightKg()).isEqualTo(1);
        assertThat(validate(with(b -> b.weight(100_000))).grossWeightKg()).isEqualTo(100_000);
        assertThat(codes(with(b -> b.weight(0)))).containsExactly("grossWeightKg/OUT_OF_RANGE");
        assertThat(codes(with(b -> b.weight(100_001)))).containsExactly("grossWeightKg/OUT_OF_RANGE");
    }

    @Test
    void seatsDependOnTheCategory() {
        assertThat(validate(with(b -> b.category(VehicleCategory.MOTORCYCLE).seats(3))).seats()).isEqualTo(3);
        assertThat(codes(with(b -> b.category(VehicleCategory.MOTORCYCLE).seats(4))))
                .containsExactly("seats/OUT_OF_RANGE");
        assertThat(codes(with(b -> b.category(VehicleCategory.MOTORCYCLE).seats(0))))
                .containsExactly("seats/OUT_OF_RANGE");
        assertThat(validate(with(b -> b.category(VehicleCategory.BUS).seats(100))).seats()).isEqualTo(100);
        assertThat(codes(with(b -> b.category(VehicleCategory.BUS).seats(101)))).containsExactly("seats/OUT_OF_RANGE");
        assertThat(codes(with(b -> b.seats(0)))).containsExactly("seats/OUT_OF_RANGE");
    }

    @Test
    void commercialValue() {
        assertThat(validate(with(b -> b.value(Money.ofCentavos(100_000_000_000L)))).commercialValue().amount())
                .isEqualTo(100_000_000_000L);
        assertThat(codes(with(b -> b.value(Money.ofCentavos(0))))).containsExactly("commercialValue/OUT_OF_RANGE");
        assertThat(codes(with(b -> b.value(Money.ofCentavos(100_000_000_001L)))))
                .containsExactly("commercialValue/OUT_OF_RANGE");
        // A moeda é validada pelo Money ao ler o pedido.
        assertThatThrownBy(() -> new Money(1, "USD"))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("INVALID_CURRENCY"));
    }

    @Test
    void driversListMayBeEmpty() {
        assertThat(VehicleRules.validateDrivers(List.of())).isEmpty();
    }

    @Test
    void driversListNeedsExactlyOneHabitual() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        assertThat(VehicleRules.validateDrivers(List.of(new DriverAssignment(a, true), new DriverAssignment(b, null))))
                .containsExactly(new DriverAssignment(a, true), new DriverAssignment(b, false));
        assertThat(driverCodes(List.of(new DriverAssignment(a, false), new DriverAssignment(b, false))))
                .containsExactly("drivers/HABITUAL_DRIVER_REQUIRED");
        assertThat(driverCodes(List.of(new DriverAssignment(a, true), new DriverAssignment(b, true))))
                .containsExactly("drivers/MULTIPLE_HABITUAL_DRIVERS");
    }

    @Test
    void driversListLimitsDuplicatesAndMissingIds() {
        UUID a = UUID.randomUUID();
        List<DriverAssignment> six = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            six.add(new DriverAssignment(UUID.randomUUID(), i == 0));
        }
        assertThat(driverCodes(six)).containsExactly("drivers/OUT_OF_RANGE");
        assertThat(VehicleRules.validateDrivers(six.subList(0, 5))).hasSize(5);
        assertThat(driverCodes(List.of(new DriverAssignment(a, true), new DriverAssignment(a, false))))
                .containsExactly("drivers[1].driverId/DUPLICATED");
        assertThat(driverCodes(Arrays.asList(new DriverAssignment(null, true))))
                .containsExactly("drivers[0].driverId/REQUIRED");
        assertThat(driverCodes(null)).containsExactly("drivers/REQUIRED");
    }

    private static List<String> driverCodes(List<DriverAssignment> drivers) {
        try {
            VehicleRules.validateDrivers(drivers);
        } catch (DomainException e) {
            @SuppressWarnings("unchecked")
            List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
            return errors.stream().map(f -> f.field() + "/" + f.code()).toList();
        }
        throw new AssertionError("expected VALIDATION_ERROR");
    }

    /** Altera um campo de cada vez a partir de um pedido válido. */
    static final class Builder {
        private String plate;
        private String chassis;
        private String make;
        private String model;
        private Integer year;
        private Integer engineCc;
        private VehicleCategory category;
        private Integer seats;
        private Integer weight;
        private VehicleUsage usage;
        private Province province;
        private Money value;
        private Boolean hasTracker;

        Builder(VehicleData d) {
            plate = d.plate();
            chassis = d.chassisNumber();
            make = d.make();
            model = d.model();
            year = d.year();
            engineCc = d.engineCc();
            category = d.category();
            seats = d.seats();
            weight = d.grossWeightKg();
            usage = d.usage();
            province = d.provinceOfCirculation();
            value = d.commercialValue();
            hasTracker = d.hasTracker();
        }

        Builder plate(String v) { plate = v; return this; }
        Builder chassis(String v) { chassis = v; return this; }
        Builder make(String v) { make = v; return this; }
        Builder model(String v) { model = v; return this; }
        Builder year(Integer v) { year = v; return this; }
        Builder engineCc(Integer v) { engineCc = v; return this; }
        Builder category(VehicleCategory v) { category = v; return this; }
        Builder seats(Integer v) { seats = v; return this; }
        Builder weight(Integer v) { weight = v; return this; }
        Builder value(Money v) { value = v; return this; }
        Builder hasTracker(Boolean v) { hasTracker = v; return this; }

        VehicleData build() {
            return new VehicleData(plate, chassis, make, model, year, engineCc, category, seats, weight, usage,
                    province, value, hasTracker);
        }
    }
}
