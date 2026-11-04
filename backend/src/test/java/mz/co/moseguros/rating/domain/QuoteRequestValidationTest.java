package mz.co.moseguros.rating.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.CoverageInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.DriverInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.Errors;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.Terms;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.TermsInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.VehicleInput;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.StaticPlateFormatProvider;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Regras do pedido de cotação (S3-02 §3) e dos dados escritos à mão (§6 e §12). */
class QuoteRequestValidationTest {

    static final LocalDate TODAY = LocalDate.of(2027, 1, 10);
    static final Instant NOW = Instant.parse("2027-01-10T08:15:00Z");
    static final int WINDOW = 60;
    static final ReferenceValueLookup LICENCES = (domain, code) ->
            ReferenceValueLookup.LICENCE_CATEGORY.equals(domain) && Set.of("A", "B", "C").contains(code);

    static CoverageInput c(CoverageCode code) {
        return new CoverageInput(code, null);
    }

    static TermsInput terms(ProductType type, Integer days, LocalDate start, CoverageInput... coverages) {
        return new TermsInput(type, days, start, Arrays.asList(coverages));
    }

    static List<FieldError> errorsOf(TermsInput input) {
        Errors errors = new Errors();
        Terms terms = QuoteRequestValidator.validateTerms(input, TODAY, WINDOW, true, errors);
        if (errors.isEmpty()) {
            assertThat(terms).isNotNull();
        } else {
            assertThat(terms).isNull();
        }
        return errors.list();
    }

    static Terms valid(TermsInput input) {
        Errors errors = new Errors();
        Terms terms = QuoteRequestValidator.validateTerms(input, TODAY, WINDOW, true, errors);
        assertThat(errors.list()).isEmpty();
        return terms;
    }

    static void assertError(List<FieldError> errors, String field, String code) {
        assertThat(errors).extracting(FieldError::field, FieldError::code).contains(org.assertj.core.groups.Tuple
                .tuple(field, code));
    }

    static final CoverageInput[] FULL_OD = {c(CoverageCode.OD_COLLISION), c(CoverageCode.OD_FIRE),
            c(CoverageCode.OD_NATURE)};

    @Nested
    class Rule1ProductType {

        @ParameterizedTest
        @ValueSource(strings = {"MTPL_ONLY", "TEMPORARY"})
        void quotableTypesWithoutOwnDamage(String type) {
            valid(terms(ProductType.valueOf(type), 365, TODAY));
        }

        @Test
        void comprehensiveIsQuotable() {
            valid(terms(ProductType.COMPREHENSIVE, 365, TODAY, FULL_OD));
        }

        @ParameterizedTest
        @ValueSource(strings = {"BORDER", "FLEET"})
        void borderAndFleetAreNotAllowed(String type) {
            assertError(errorsOf(terms(ProductType.valueOf(type), 365, TODAY)), "productType", "NOT_ALLOWED");
        }

        @Test
        void requiredFields() {
            List<FieldError> errors = errorsOf(new TermsInput(null, null, null, null));
            assertError(errors, "productType", "REQUIRED");
            assertError(errors, "durationDays", "REQUIRED");
            assertError(errors, "startDate", "REQUIRED");
        }
    }

    @Nested
    class Rule2Duration {

        @ParameterizedTest
        @ValueSource(ints = {7, 14, 30, 60, 365})
        void temporaryDurations(int days) {
            assertThat(valid(terms(ProductType.TEMPORARY, days, TODAY)).durationDays()).isEqualTo(days);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 15, 90, 366})
        void otherTemporaryDurationsAreRejected(int days) {
            assertError(errorsOf(terms(ProductType.TEMPORARY, days, TODAY)), "durationDays", "DURATION_NOT_ALLOWED");
        }

        @ParameterizedTest
        @ValueSource(ints = {7, 30, 60})
        void annualTypesOnlyAccept365(int days) {
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, days, TODAY)), "durationDays", "DURATION_NOT_ALLOWED");
            assertError(errorsOf(terms(ProductType.COMPREHENSIVE, days, TODAY, FULL_OD)), "durationDays",
                    "DURATION_NOT_ALLOWED");
        }
    }

    @Nested
    class Rule3StartDate {

        @Test
        void fromTodayToTodayPlusTheWindow() {
            valid(terms(ProductType.MTPL_ONLY, 365, TODAY));
            valid(terms(ProductType.MTPL_ONLY, 365, TODAY.plusDays(WINDOW)));
        }

        @Test
        void yesterdayAndAfterTheWindowAreRejected() {
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, 365, TODAY.minusDays(1))), "startDate",
                    "START_DATE_OUT_OF_RANGE");
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, 365, TODAY.plusDays(WINDOW + 1))), "startDate",
                    "START_DATE_OUT_OF_RANGE");
        }

        @Test
        void theSimulatorAcceptsAnyStartDate() {
            Errors errors = new Errors();
            Terms terms = QuoteRequestValidator.validateTerms(terms(ProductType.MTPL_ONLY, 365,
                    LocalDate.of(2020, 1, 1)), TODAY, 0, false, errors);
            assertThat(errors.list()).isEmpty();
            assertThat(terms.startDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        }
    }

    @Nested
    class Rule4EndDate {

        @Test
        void aYearEndsTheDayBeforeTheAnniversaryEvenInALeapYear() {
            assertThat(QuoteRequestValidator.endDate(LocalDate.of(2027, 3, 1), 365)).isEqualTo(LocalDate.of(2028, 2, 29));
            assertThat(QuoteRequestValidator.endDate(LocalDate.of(2027, 1, 15), 365))
                    .isEqualTo(LocalDate.of(2028, 1, 14));
        }

        @Test
        void otherDurationsCountTheStartDay() {
            assertThat(QuoteRequestValidator.endDate(LocalDate.of(2027, 1, 15), 30)).isEqualTo(LocalDate.of(2027, 2, 13));
            assertThat(QuoteRequestValidator.endDate(LocalDate.of(2027, 1, 15), 7)).isEqualTo(LocalDate.of(2027, 1, 21));
        }

        @Test
        void theTermsCarryTheEndDate() {
            assertThat(valid(terms(ProductType.TEMPORARY, 30, LocalDate.of(2027, 1, 15))).endDate())
                    .isEqualTo(LocalDate.of(2027, 2, 13));
        }
    }

    @Nested
    class Rule5Coverages {

        @Test
        void emptyOrMissingListIsValid() {
            assertThat(valid(new TermsInput(ProductType.MTPL_ONLY, 365, TODAY, null)).coverages()).isEmpty();
            assertThat(valid(terms(ProductType.MTPL_ONLY, 365, TODAY)).coverages()).isEmpty();
        }

        @Test
        void mtplMayComeOrNot() {
            assertThat(valid(terms(ProductType.MTPL_ONLY, 365, TODAY, c(CoverageCode.MTPL))).coverages())
                    .extracting(RequestedCoverage::code).containsExactly(CoverageCode.MTPL);
        }

        @Test
        void repeatedCode() {
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, 365, TODAY, c(CoverageCode.VTPL), c(CoverageCode.VTPL))),
                    "coverages[1].code", "DUPLICATE_COVERAGE");
        }

        @Test
        void missingCode() {
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, 365, TODAY, new CoverageInput(null, null))),
                    "coverages[0].code", "REQUIRED");
        }

        @Test
        void ownDamageOnlyInComprehensive() {
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, 365, TODAY, c(CoverageCode.OD_FIRE))),
                    "coverages[0].code", "COVERAGE_NOT_ALLOWED_FOR_TYPE");
            assertError(errorsOf(terms(ProductType.TEMPORARY, 30, TODAY, c(CoverageCode.OD_COLLISION))),
                    "coverages[0].code", "COVERAGE_NOT_ALLOWED_FOR_TYPE");
        }

        @Test
        void comprehensiveNeedsOwnDamage() {
            assertError(errorsOf(terms(ProductType.COMPREHENSIVE, 365, TODAY, c(CoverageCode.VTPL))), "coverages",
                    "OWN_DAMAGE_REQUIRED");
            assertError(errorsOf(new TermsInput(ProductType.COMPREHENSIVE, 365, TODAY, null)), "coverages",
                    "OWN_DAMAGE_REQUIRED");
            valid(terms(ProductType.COMPREHENSIVE, 365, TODAY, c(CoverageCode.OD_GLASS)));
        }

        @Test
        void theftRequiresCollisionFireAndNature() {
            assertError(errorsOf(terms(ProductType.COMPREHENSIVE, 365, TODAY, c(CoverageCode.OD_COLLISION),
                    c(CoverageCode.OD_THEFT))), "coverages[1].code", "COVERAGE_REQUIRES");
            List<CoverageInput> all = new ArrayList<>(Arrays.asList(FULL_OD));
            all.add(c(CoverageCode.OD_THEFT));
            valid(new TermsInput(ProductType.COMPREHENSIVE, 365, TODAY, all));
        }

        @Test
        void capitalMustBePositiveAndIsKeptInCentavos() {
            assertError(errorsOf(terms(ProductType.MTPL_ONLY, 365, TODAY,
                    new CoverageInput(CoverageCode.VTPL, Money.ofCentavos(0)))), "coverages[0].capital", "OUT_OF_RANGE");
            assertThat(valid(terms(ProductType.MTPL_ONLY, 365, TODAY,
                    new CoverageInput(CoverageCode.VTPL, Money.ofCentavos(500_000_000)))).coverages())
                    .containsExactly(new RequestedCoverage(CoverageCode.VTPL, 500_000_000L));
        }

        @Test
        void tooManyCoverages() {
            List<CoverageInput> many = new ArrayList<>();
            for (int i = 0; i < 13; i++) {
                many.add(c(CoverageCode.VTPL));
            }
            assertError(errorsOf(new TermsInput(ProductType.MTPL_ONLY, 365, TODAY, many)), "coverages", "OUT_OF_RANGE");
        }
    }

    @Nested
    class Rule6VehicleValue {

        @Test
        void ownDamageWithoutValueIsRejected() {
            Terms terms = valid(terms(ProductType.COMPREHENSIVE, 365, TODAY, FULL_OD));
            assertThatThrownBy(() -> QuoteRequestValidator.requireVehicleValue(terms, null))
                    .isInstanceOf(DomainException.class)
                    .satisfies(e -> {
                        assertThat(((DomainException) e).getCode()).isEqualTo("VEHICLE_VALUE_REQUIRED");
                        assertThat(((DomainException) e).getHttpStatus()).isEqualTo(400);
                    });
        }

        @Test
        void withValueOrWithoutOwnDamageIsAccepted() {
            QuoteRequestValidator.requireVehicleValue(valid(terms(ProductType.COMPREHENSIVE, 365, TODAY, FULL_OD)),
                    120_000_000L);
            QuoteRequestValidator.requireVehicleValue(valid(terms(ProductType.MTPL_ONLY, 365, TODAY)), null);
        }
    }

    @Test
    void throwIfAnyCarriesAllTheErrors() {
        Errors errors = new Errors();
        QuoteRequestValidator.validateTerms(new TermsInput(null, null, null, null), TODAY, WINDOW, true, errors);
        assertThatThrownBy(errors::throwIfAny).isInstanceOf(DomainException.class)
                .satisfies(e -> {
                    DomainException d = (DomainException) e;
                    assertThat(d.getCode()).isEqualTo("VALIDATION_ERROR");
                    assertThat((List<?>) d.getProperties().get("errors")).hasSize(3);
                });
    }

    // --- Dados escritos à mão ---

    static final PlateParser PLATES = new PlateParser(StaticPlateFormatProvider.defaults());

    static VehicleInput vehicle() {
        return new VehicleInput("abc 123 mc", " Toyota ", "Corolla", 2024, 1600, VehicleCategory.LIGHT_PASSENGER,
                VehicleUsage.PRIVATE, 5, null, Province.MAPUTO_CITY, Money.ofCentavos(120_000_000), null);
    }

    static VehicleInput with(VehicleInput v, String field, Object value) {
        return new VehicleInput(
                field.equals("plate") ? (String) value : v.plate(),
                field.equals("make") ? (String) value : v.make(),
                field.equals("model") ? (String) value : v.model(),
                field.equals("year") ? (Integer) value : v.year(),
                field.equals("engineCc") ? (Integer) value : v.engineCc(),
                field.equals("category") ? (VehicleCategory) value : v.category(),
                field.equals("usage") ? (VehicleUsage) value : v.usage(),
                field.equals("seats") ? (Integer) value : v.seats(),
                field.equals("grossWeightKg") ? (Integer) value : v.grossWeightKg(),
                field.equals("province") ? (Province) value : v.provinceOfCirculation(),
                field.equals("commercialValue") ? (Money) value : v.commercialValue(),
                field.equals("hasTracker") ? (Boolean) value : v.hasTracker());
    }

    static List<FieldError> vehicleErrors(VehicleInput input, boolean partial) {
        Errors errors = new Errors();
        RatedVehicle v = QuoteRequestValidator.validateVehicle(input, PLATES, NOW, partial, errors);
        assertThat(v == null).isEqualTo(!errors.isEmpty());
        return errors.list();
    }

    @Nested
    class ManualVehicle {

        @Test
        void validVehicleIsNormalized() {
            Errors errors = new Errors();
            RatedVehicle v = QuoteRequestValidator.validateVehicle(vehicle(), PLATES, NOW, false, errors);
            assertThat(errors.list()).isEmpty();
            assertThat(v.vehicleId()).isNull();
            assertThat(v.plate()).isEqualTo("ABC-123-MC");
            assertThat(v.make()).isEqualTo("Toyota");
            assertThat(v.commercialValue()).isEqualTo(120_000_000L);
            assertThat(v.hasTracker()).isFalse();
        }

        @Test
        void plateMakeModelAndEngineAreOptional() {
            VehicleInput v = with(with(with(with(vehicle(), "plate", null), "make", " "), "model", null), "engineCc", null);
            assertThat(vehicleErrors(v, false)).isEmpty();
        }

        @Test
        void invalidPlate() {
            assertError(vehicleErrors(with(vehicle(), "plate", "XX"), false), "vehicle.plate", "INVALID_PLATE");
        }

        @Test
        void textLimits() {
            assertError(vehicleErrors(with(vehicle(), "make", "x".repeat(61)), false), "vehicle.make", "OUT_OF_RANGE");
        }

        @Test
        void year() {
            assertError(vehicleErrors(with(vehicle(), "year", null), false), "vehicle.year", "REQUIRED");
            assertError(vehicleErrors(with(vehicle(), "year", 1899), false), "vehicle.year", "INVALID_YEAR");
            assertError(vehicleErrors(with(vehicle(), "year", 2029), false), "vehicle.year", "INVALID_YEAR");
            assertThat(vehicleErrors(with(vehicle(), "year", 2028), false)).isEmpty();
        }

        @Test
        void engine() {
            assertError(vehicleErrors(with(vehicle(), "engineCc", 0), false), "vehicle.engineCc", "OUT_OF_RANGE");
            VehicleInput trailer = with(with(with(vehicle(), "category", VehicleCategory.TRAILER), "seats", 0),
                    "grossWeightKg", 2000);
            assertError(vehicleErrors(trailer, false), "vehicle.engineCc", "NOT_ALLOWED");
            assertThat(vehicleErrors(with(trailer, "engineCc", null), false)).isEmpty();
        }

        @Test
        void requiredEnums() {
            List<FieldError> errors = vehicleErrors(with(with(with(vehicle(), "category", null), "usage", null),
                    "province", null), false);
            assertError(errors, "vehicle.category", "REQUIRED");
            assertError(errors, "vehicle.usage", "REQUIRED");
            assertError(errors, "vehicle.provinceOfCirculation", "REQUIRED");
        }

        @Test
        void seats() {
            assertError(vehicleErrors(with(vehicle(), "seats", null), false), "vehicle.seats", "REQUIRED");
            assertError(vehicleErrors(with(vehicle(), "seats", 0), false), "vehicle.seats", "OUT_OF_RANGE");
            assertError(vehicleErrors(with(with(vehicle(), "category", VehicleCategory.MOTORCYCLE), "seats", 4), false),
                    "vehicle.seats", "OUT_OF_RANGE");
            assertError(vehicleErrors(with(with(vehicle(), "category", null), "seats", 101), false), "vehicle.seats",
                    "OUT_OF_RANGE");
        }

        @Test
        void grossWeight() {
            assertError(vehicleErrors(with(vehicle(), "category", VehicleCategory.HEAVY), false),
                    "vehicle.grossWeightKg", "REQUIRED");
            assertError(vehicleErrors(with(vehicle(), "grossWeightKg", 100_001), false), "vehicle.grossWeightKg",
                    "OUT_OF_RANGE");
        }

        @Test
        void commercialValue() {
            assertError(vehicleErrors(with(vehicle(), "commercialValue", Money.ofCentavos(0)), false),
                    "vehicle.commercialValue", "OUT_OF_RANGE");
            assertThat(vehicleErrors(with(vehicle(), "commercialValue", null), false)).isEmpty();
        }

        @Test
        void theSimulatorNeedsOnlyYearCategoryUsageAndProvince() {
            VehicleInput partial = new VehicleInput(null, null, null, 2018, null, VehicleCategory.HEAVY,
                    VehicleUsage.GOODS, null, null, Province.GAZA, null, null);
            assertThat(vehicleErrors(partial, true)).isEmpty();
            assertThat(vehicleErrors(partial, false)).extracting(FieldError::field)
                    .contains("vehicle.seats", "vehicle.grossWeightKg");
        }

        @Test
        void missingVehicle() {
            assertError(vehicleErrors(null, false), "vehicle", "REQUIRED");
        }
    }

    static List<FieldError> driverErrors(DriverInput input) {
        Errors errors = new Errors();
        RatedDriver d = QuoteRequestValidator.validateDriver(input, LICENCES, TODAY, errors);
        assertThat(d == null).isEqualTo(!errors.isEmpty());
        return errors.list();
    }

    @Nested
    class ManualDriver {

        @Test
        void validDriver() {
            Errors errors = new Errors();
            RatedDriver d = QuoteRequestValidator.validateDriver(new DriverInput(LocalDate.of(1991, 9, 9),
                    LocalDate.of(2011, 4, 4), List.of(" b ", "C")), LICENCES, TODAY, errors);
            assertThat(errors.list()).isEmpty();
            assertThat(d.driverId()).isNull();
            assertThat(d.licenceCategories()).containsExactly("B", "C");
        }

        @Test
        void onlyTheBirthDateIsRequired() {
            assertThat(driverErrors(new DriverInput(LocalDate.of(1991, 9, 9), null, null))).isEmpty();
            assertError(driverErrors(new DriverInput(null, null, null)), "driver.birthDate", "REQUIRED");
            assertError(driverErrors(null), "driver", "REQUIRED");
        }

        @Test
        void birthDateInThePastAndAtMost100Years() {
            assertError(driverErrors(new DriverInput(TODAY, null, null)), "driver.birthDate", "INVALID_DATE");
            assertError(driverErrors(new DriverInput(TODAY.minusYears(101), null, null)), "driver.birthDate",
                    "INVALID_DATE");
        }

        @Test
        void licenceIssueDate() {
            assertError(driverErrors(new DriverInput(LocalDate.of(1991, 9, 9), LocalDate.of(1990, 1, 1), null)),
                    "driver.licenceIssueDate", "LICENCE_ISSUE_DATE_INVALID");
            assertError(driverErrors(new DriverInput(LocalDate.of(1991, 9, 9), TODAY.plusDays(1), null)),
                    "driver.licenceIssueDate", "LICENCE_ISSUE_DATE_INVALID");
        }

        @Test
        void licenceCategories() {
            assertError(driverErrors(new DriverInput(LocalDate.of(1991, 9, 9), null, List.of("Z"))),
                    "driver.licenceCategories[0]", "LICENCE_CATEGORY_UNKNOWN");
            assertError(driverErrors(new DriverInput(LocalDate.of(1991, 9, 9), null, List.of("B", "b"))),
                    "driver.licenceCategories[1]", "DUPLICATED");
            assertError(driverErrors(new DriverInput(LocalDate.of(1991, 9, 9), null,
                    List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"))),
                    "driver.licenceCategories", "OUT_OF_RANGE");
        }
    }
}
