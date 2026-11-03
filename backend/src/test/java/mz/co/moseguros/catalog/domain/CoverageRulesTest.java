package mz.co.moseguros.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/** Uma verificação por linha da tabela {@code CoverageRequest} (S2-04 §3). */
class CoverageRulesTest {

    static Money m(long centavos) {
        return Money.ofCentavos(centavos);
    }

    /** MTPL fixa, sem franquia, só em MZ: válida. */
    static CoverageSpec valid(CoverageCode code) {
        return new CoverageSpec(code, true, List.of(), CapitalType.FIXED, m(300_000_000), null,
                DeductibleType.NONE, null, null, null, List.of("MZ"), null, null);
    }

    static Builder b(CoverageCode code) {
        return new Builder(valid(code));
    }

    private static List<String> codes(List<CoverageSpec> coverages) {
        return CoverageRules.validate(coverages).stream().map(e -> e.field() + ":" + e.code()).toList();
    }

    @Test
    void validListHasNoErrors() {
        assertThat(CoverageRules.validate(List.of(valid(CoverageCode.MTPL),
                b(CoverageCode.OD_COLLISION).capital(CapitalType.VEHICLE_VALUE, null, null)
                        .deductible(DeductibleType.PERCENT, null, new BigDecimal("10.00"), m(2_500_000)).build(),
                b(CoverageCode.VTPL).capital(CapitalType.OPTIONS, null, List.of(m(500_000_000), m(1_000_000_000)))
                        .build(),
                b(CoverageCode.OD_GLASS).capital(CapitalType.VEHICLE_VALUE, null, null)
                        .deductible(DeductibleType.FIXED, m(500_000), null, null).build(),
                b(CoverageCode.OCCUPANTS).capital(CapitalType.PER_PERSON, m(50_000_000), null).build())))
                .isEmpty();
        assertThat(CoverageRules.validate(List.of())).isEmpty();
    }

    @Test
    void listIsRequiredAndHasAtMostTwelveCoverages() {
        assertThat(codes(null)).containsExactly("coverages:REQUIRED");
        List<CoverageSpec> thirteen = new ArrayList<>(Collections.nCopies(13, valid(CoverageCode.MTPL)));
        assertThat(codes(thirteen)).containsExactly("coverages:SIZE");
        assertThat(codes(Arrays.asList(valid(CoverageCode.MTPL), null))).containsExactly("coverages[1]:REQUIRED");
    }

    @Test
    void codeIsRequiredAndCannotRepeat() {
        assertThat(codes(List.of(b(null).build()))).containsExactly("coverages[0].code:REQUIRED");
        assertThat(codes(List.of(valid(CoverageCode.MTPL), valid(CoverageCode.VTPL), valid(CoverageCode.MTPL))))
                .containsExactly("coverages[2].code:DUPLICATED");
    }

    @Test
    void mandatoryIsRequired() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).mandatory(null).build())))
                .containsExactly("coverages[0].mandatory:REQUIRED");
    }

    @Test
    void requiresCodesHaveNoRepeatsAndNotTheOwnCode() {
        assertThat(codes(List.of(b(CoverageCode.OD_THEFT).requires(CoverageCode.OD_FIRE, CoverageCode.OD_FIRE)
                .build()))).containsExactly("coverages[0].requiresCodes:DUPLICATED");
        assertThat(codes(List.of(b(CoverageCode.OD_THEFT).requires(CoverageCode.OD_THEFT).build())))
                .containsExactly("coverages[0].requiresCodes:INVALID_REQUIREMENT");
        // a existência é verificada na publicação
        assertThat(codes(List.of(b(CoverageCode.OD_THEFT).requires(CoverageCode.OD_FIRE).build()))).isEmpty();
        assertThat(codes(List.of(b(CoverageCode.MTPL).requiresNull().build()))).isEmpty();
    }

    @Test
    void capitalTypeIsRequired() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).capital(null, null, null).build())))
                .containsExactly("coverages[0].capitalType:REQUIRED");
    }

    @Test
    void capitalIsRequiredForFixedAndPerPersonForbiddenOtherwiseAndPositive() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).capital(CapitalType.FIXED, null, null).build())))
                .containsExactly("coverages[0].capital:REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.OCCUPANTS).capital(CapitalType.PER_PERSON, null, null).build())))
                .containsExactly("coverages[0].capital:REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.OD_FIRE).capital(CapitalType.VEHICLE_VALUE, m(1), null).build())))
                .containsExactly("coverages[0].capital:NOT_ALLOWED");
        assertThat(codes(List.of(b(CoverageCode.VTPL).capital(CapitalType.OPTIONS, m(1), List.of(m(1), m(2)))
                .build()))).containsExactly("coverages[0].capital:NOT_ALLOWED");
        assertThat(codes(List.of(b(CoverageCode.MTPL).capital(CapitalType.FIXED, m(0), null).build())))
                .containsExactly("coverages[0].capital:OUT_OF_RANGE");
        assertThat(codes(List.of(b(CoverageCode.MTPL).capital(CapitalType.FIXED, m(-5), null).build())))
                .containsExactly("coverages[0].capital:OUT_OF_RANGE");
    }

    @Test
    void capitalOptionsAreRequiredForOptionsForbiddenOtherwiseAndStrictlyIncreasing() {
        assertThat(codes(List.of(b(CoverageCode.VTPL).capital(CapitalType.OPTIONS, null, null).build())))
                .containsExactly("coverages[0].capitalOptions:REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.MTPL).capital(CapitalType.FIXED, m(1), List.of(m(1), m(2)))
                .build()))).containsExactly("coverages[0].capitalOptions:NOT_ALLOWED");
        for (List<Money> invalid : List.of(List.of(m(1)), List.of(m(2), m(1)), List.of(m(1), m(1)),
                List.of(m(0), m(1)), List.of(m(-1), m(1)), Collections.nCopies(11, m(1)), List.<Money>of())) {
            assertThat(codes(List.of(b(CoverageCode.VTPL).capital(CapitalType.OPTIONS, null, invalid).build())))
                    .as(invalid.toString()).containsExactly("coverages[0].capitalOptions:CAPITAL_OPTIONS_INVALID");
        }
        List<Money> ten = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ten.add(m(i * 100L));
        }
        assertThat(codes(List.of(b(CoverageCode.VTPL).capital(CapitalType.OPTIONS, null, ten).build()))).isEmpty();
    }

    @Test
    void deductibleTypeIsRequired() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).deductible(null, null, null, null).build())))
                .containsExactly("coverages[0].deductibleType:REQUIRED");
    }

    @Test
    void deductibleAmountIsRequiredForFixedForbiddenOtherwiseAndPositive() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).deductible(DeductibleType.FIXED, null, null, null).build())))
                .containsExactly("coverages[0].deductibleAmount:REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.MTPL).deductible(DeductibleType.FIXED, m(0), null, null).build())))
                .containsExactly("coverages[0].deductibleAmount:OUT_OF_RANGE");
        assertThat(codes(List.of(b(CoverageCode.MTPL).deductible(DeductibleType.NONE, m(10), null, null).build())))
                .containsExactly("coverages[0].deductibleAmount:NOT_ALLOWED");
        assertThat(codes(List.of(b(CoverageCode.MTPL)
                .deductible(DeductibleType.PERCENT, m(10), new BigDecimal("5"), null).build())))
                .containsExactly("coverages[0].deductibleAmount:NOT_ALLOWED");
    }

    @Test
    void deductiblePercentIsRequiredForPercentForbiddenOtherwiseAndInRange() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).deductible(DeductibleType.PERCENT, null, null, null).build())))
                .containsExactly("coverages[0].deductiblePercent:REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.MTPL)
                .deductible(DeductibleType.NONE, null, new BigDecimal("10"), null).build())))
                .containsExactly("coverages[0].deductiblePercent:NOT_ALLOWED");
        for (String invalid : List.of("0", "-1", "100.01", "150", "10.005")) {
            assertThat(codes(List.of(b(CoverageCode.MTPL)
                    .deductible(DeductibleType.PERCENT, null, new BigDecimal(invalid), null).build())))
                    .as(invalid).containsExactly("coverages[0].deductiblePercent:OUT_OF_RANGE");
        }
        for (String valid : List.of("0.01", "10.5", "100", "100.00", "12.340")) {
            assertThat(codes(List.of(b(CoverageCode.MTPL)
                    .deductible(DeductibleType.PERCENT, null, new BigDecimal(valid), null).build()))).as(valid).isEmpty();
        }
    }

    @Test
    void deductibleMinAmountIsOptionalForPercentForbiddenOtherwiseAndPositive() {
        assertThat(codes(List.of(b(CoverageCode.MTPL)
                .deductible(DeductibleType.PERCENT, null, BigDecimal.TEN, m(0)).build())))
                .containsExactly("coverages[0].deductibleMinAmount:OUT_OF_RANGE");
        assertThat(codes(List.of(b(CoverageCode.MTPL)
                .deductible(DeductibleType.FIXED, m(10), null, m(5)).build())))
                .containsExactly("coverages[0].deductibleMinAmount:NOT_ALLOWED");
        assertThat(codes(List.of(b(CoverageCode.MTPL)
                .deductible(DeductibleType.PERCENT, null, BigDecimal.TEN, m(5)).build()))).isEmpty();
    }

    @Test
    void territorialScopeRules() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).scope((List<String>) null).build())))
                .containsExactly("coverages[0].territorialScope:REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.MTPL).scope(List.of()).build())))
                .containsExactly("coverages[0].territorialScope:SIZE");
        List<String> many = new ArrayList<>(List.of("MZ"));
        for (char c = 'A'; many.size() < 21; c++) {
            many.add("Z" + c);
        }
        assertThat(codes(List.of(b(CoverageCode.MTPL).scope(many).build())))
                .containsExactly("coverages[0].territorialScope:SIZE");
        for (String invalid : List.of("mz", "MOZ", "M1", "")) {
            assertThat(codes(List.of(b(CoverageCode.MTPL).scope(List.of("MZ", invalid)).build())))
                    .as(invalid).containsExactly("coverages[0].territorialScope:INVALID_COUNTRY_CODE");
        }
        assertThat(codes(List.of(b(CoverageCode.MTPL).scope(List.of("MZ", "ZA", "MZ")).build())))
                .containsExactly("coverages[0].territorialScope:DUPLICATED");
        assertThat(codes(List.of(b(CoverageCode.MTPL).scope(List.of("ZA")).build())))
                .containsExactly("coverages[0].territorialScope:MZ_SCOPE_REQUIRED");
        assertThat(codes(List.of(b(CoverageCode.CROSS_BORDER).scope(List.of("ZA", "MZ", "SZ")).build()))).isEmpty();
    }

    @Test
    void descriptionAndExclusionsHaveMaximumLengths() {
        assertThat(codes(List.of(b(CoverageCode.MTPL).texts("a".repeat(1001), "b".repeat(4001)).build())))
                .containsExactly("coverages[0].description:SIZE", "coverages[0].exclusions:SIZE");
        assertThat(codes(List.of(b(CoverageCode.MTPL).texts("a".repeat(1000), "b".repeat(4000)).build()))).isEmpty();
    }

    @Test
    void severalErrorsAreReportedTogether() {
        List<FieldError> errors = CoverageRules.validate(List.of(
                b(CoverageCode.MTPL).mandatory(null).capital(CapitalType.FIXED, null, null).build(),
                b(CoverageCode.MTPL).scope(List.of("ZA")).build()));
        assertThat(errors).extracting(e -> e.field() + ":" + e.code()).containsExactly(
                "coverages[0].mandatory:REQUIRED", "coverages[0].capital:REQUIRED",
                "coverages[1].code:DUPLICATED", "coverages[1].territorialScope:MZ_SCOPE_REQUIRED");
        assertThatThrownBy(() -> CoverageRules.require(List.of(b(null).build())))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
                    assertThat(e.getHttpStatus()).isEqualTo(400);
                    assertThat(e.getProperties()).containsKey("errors");
                });
    }

    /** Altera um campo de cada vez a partir de uma cobertura válida. */
    static final class Builder {
        private CoverageCode code;
        private Boolean mandatory;
        private List<CoverageCode> requires;
        private CapitalType capitalType;
        private Money capital;
        private List<Money> options;
        private DeductibleType deductibleType;
        private Money deductibleAmount;
        private BigDecimal percent;
        private Money minAmount;
        private List<String> scope;
        private String description;
        private String exclusions;

        Builder(CoverageSpec s) {
            code = s.code();
            mandatory = s.mandatory();
            requires = s.requiresCodes();
            capitalType = s.capitalType();
            capital = s.capital();
            options = s.capitalOptions();
            deductibleType = s.deductibleType();
            deductibleAmount = s.deductibleAmount();
            percent = s.deductiblePercent();
            minAmount = s.deductibleMinAmount();
            scope = s.territorialScope();
        }

        Builder code(CoverageCode value) {
            code = value;
            return this;
        }

        Builder mandatory(Boolean value) {
            mandatory = value;
            return this;
        }

        Builder requires(CoverageCode... values) {
            requires = Arrays.asList(values);
            return this;
        }

        Builder requiresNull() {
            requires = null;
            return this;
        }

        Builder capital(CapitalType type, Money amount, List<Money> optionList) {
            capitalType = type;
            capital = amount;
            options = optionList;
            return this;
        }

        Builder deductible(DeductibleType type, Money amount, BigDecimal pct, Money min) {
            deductibleType = type;
            deductibleAmount = amount;
            percent = pct;
            minAmount = min;
            return this;
        }

        Builder scope(List<String> value) {
            scope = value;
            return this;
        }

        Builder texts(String desc, String excl) {
            description = desc;
            exclusions = excl;
            return this;
        }

        CoverageSpec build() {
            return new CoverageSpec(code, mandatory, requires, capitalType, capital, options, deductibleType,
                    deductibleAmount, percent, minAmount, scope, description, exclusions);
        }
    }
}
