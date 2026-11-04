package mz.co.moseguros.rating.domain.tariff;

import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.START;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.driverBornOn;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.fixed;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.input;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.json;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.mtpl;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.product;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.requested;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.simple;
import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.vehicle;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Offered;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Unavailable;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import org.junit.jupiter.api.Test;

/** Semântica do avaliador (S3-01 §8 e §9), caso a caso. */
class TariffEvaluatorTest {

    private static final List<ChargeRule> NO_CHARGES = List.of();
    private static final List<ChargeRule> STAMP = List.of(ChargeRule.percent("STAMP", "Selo", "Stamp",
            new BigDecimal("2.5")));

    private static JsonNode mtplTariff(String baseRows, String factors) {
        return json("{'schemaVersion':'rating-v1','coverages':[{'coverageCode':'MTPL','base':{'type':'AMOUNT','rows':["
                + baseRows + "]}" + (factors == null ? "" : ",'factors':[" + factors + "]") + "}]}");
    }

    private static PremiumBreakdown offered(EvaluationResult result) {
        assertThat(result).isInstanceOf(Offered.class);
        return ((Offered) result).breakdown();
    }

    private static Unavailable unavailable(EvaluationResult result) {
        assertThat(result).isInstanceOf(Unavailable.class);
        return (Unavailable) result;
    }

    private static PremiumBreakdown mtplOnly(JsonNode tariff, RatingInput input, List<ChargeRule> charges) {
        return offered(TariffEvaluator.evaluate(input, product(ProductType.MTPL_ONLY, mtpl()), tariff, charges));
    }

    @Test
    void theFirstMatchingRowWins() {
        JsonNode tariff = mtplTariff("{'when':{'vehicleCategory':{'in':['LIGHT_PASSENGER']}},'value':100000},"
                + "{'when':{'vehicleUsage':{'in':['PRIVATE']}},'value':200000}", null);
        PremiumBreakdown b = mtplOnly(tariff, simple(365), NO_CHARGES);
        assertThat(b.coverages().get(0).base().rowIndex()).isZero();
        assertThat(b.coverages().get(0).premiumAmount()).isEqualTo(100_000);
    }

    @Test
    void anEmptyWhenAlwaysApplies() {
        JsonNode tariff = mtplTariff("{'when':{'vehicleCategory':{'in':['HEAVY']}},'value':100000},"
                + "{'label':'Por defeito','when':{},'value':300000}", "{'code':'ANY','rows':[{'when':{},'value':'1.5'}]}");
        PremiumBreakdown b = mtplOnly(tariff, simple(365), NO_CHARGES);
        assertThat(b.coverages().get(0).base().rowIndex()).isEqualTo(1);
        assertThat(b.coverages().get(0).base().rowLabel()).isEqualTo("Por defeito");
        assertThat(b.coverages().get(0).premiumAmount()).isEqualTo(450_000);
    }

    @Test
    void aConditionOnAnAbsentVariableIsNotMet() {
        JsonNode tariff = mtplTariff("{'when':{'engineCc':{'gte':0}},'value':100000},{'when':{},'value':200000}", null);
        RatingInput noEngine = input(ProductType.MTPL_ONLY, vehicle(null, null, false),
                driverBornOn(LocalDate.of(1980, 1, 1)), 365);
        assertThat(mtplOnly(tariff, noEngine, NO_CHARGES).coverages().get(0).base().rowIndex()).isEqualTo(1);

        JsonNode byDriverAge = mtplTariff("{'when':{},'value':100000}",
                "{'code':'AGE','rows':[{'when':{'driverAgeYears':{'gte':0}},'value':'2'},{'when':{},'value':'1'}]}");
        RatingInput noBirthDate = input(ProductType.MTPL_ONLY, vehicle(1600, null, false),
                new RatedDriver(null, null, null, List.of()), 365);
        PremiumBreakdown b = mtplOnly(byDriverAge, noBirthDate, NO_CHARGES);
        assertThat(b.coverages().get(0).factors().get(0).rowIndex()).isEqualTo(1);
        assertThat(b.variables()).doesNotContainKeys("driverAgeYears", "licenceYears", "vehicleValue");

        JsonNode byTracker = mtplTariff("{'when':{'hasTracker':true},'value':100000},{'when':{},'value':200000}", null);
        assertThat(mtplOnly(byTracker, simple(365), NO_CHARGES).coverages().get(0).premiumAmount()).isEqualTo(200_000);
    }

    @Test
    void gteIsInclusiveAndLtIsExclusive() {
        JsonNode tariff = mtplTariff("{'when':{},'value':100000}",
                "{'code':'DRIVER_AGE','rows':[{'when':{'driverAgeYears':{'lt':25}},'value':'1.30'},"
                        + "{'when':{'driverAgeYears':{'gte':25}},'value':'1.00'}]}");
        // faz 25 anos no próprio dia de início
        RatingInput turns25 = input(ProductType.MTPL_ONLY, vehicle(1600, null, false),
                driverBornOn(START.minusYears(25)), 365);
        PremiumBreakdown b = mtplOnly(tariff, turns25, NO_CHARGES);
        assertThat(b.variables()).containsEntry("driverAgeYears", 25);
        assertThat(b.coverages().get(0).factors().get(0).rowIndex()).isEqualTo(1);
        assertThat(b.coverages().get(0).premiumAmount()).isEqualTo(100_000);

        RatingInput dayBefore25 = input(ProductType.MTPL_ONLY, vehicle(1600, null, false),
                driverBornOn(START.minusYears(25).plusDays(1)), 365);
        assertThat(mtplOnly(tariff, dayBefore25, NO_CHARGES).coverages().get(0).premiumAmount()).isEqualTo(130_000);
    }

    @Test
    void agesAreCountedOnTheStartDateAndNeverNegative() {
        RatedVehicle future = new RatedVehicle(null, null, null, null, START.getYear() + 1, null,
                VehicleCategory.LIGHT_PASSENGER, VehicleUsage.PRIVATE, null, null, Province.GAZA, null, false);
        RatingVariables v = RatingVariables.of(future, new RatedDriver(null, START.plusDays(3), START.plusDays(3),
                List.of()), START, 30);
        assertThat(v.vehicleAgeYears()).isZero();
        assertThat(v.driverAgeYears()).isZero();
        assertThat(v.licenceYears()).isZero();
        assertThat(RatingVariables.of(future, null, START, 30).driverAgeYears()).isNull();
    }

    @Test
    void halfUpOnExactlyHalfACentavo() {
        // caso C10: 19 500 × 2,5 % = 487,5 → 488
        JsonNode tariff = mtplTariff("{'when':{},'value':19500}", null);
        PremiumBreakdown b = mtplOnly(tariff, simple(365), STAMP);
        assertThat(b.charges().get(0).amount()).isEqualTo(488);
        assertThat(b.subtotalAmount()).isEqualTo(19_988);

        // prémio da cobertura: 101 × 0,5 = 50,5 → 51; 101 × 0,4 = 40,4 → 40
        JsonNode half = mtplTariff("{'when':{},'value':101}", "{'code':'HALF','rows':[{'when':{},'value':'0.5'}]}");
        assertThat(mtplOnly(half, simple(365), NO_CHARGES).coverages().get(0).premiumAmount()).isEqualTo(51);
        JsonNode down = mtplTariff("{'when':{},'value':101}", "{'code':'DOWN','rows':[{'when':{},'value':'0.4'}]}");
        PremiumBreakdown d = mtplOnly(down, simple(365), NO_CHARGES);
        assertThat(d.coverages().get(0).exactPremium()).isEqualByComparingTo("40.4");
        assertThat(d.coverages().get(0).premiumAmount()).isEqualTo(40);
    }

    @Test
    void theTotalIsRoundedUpToTheMetical() {
        PremiumBreakdown up = mtplOnly(mtplTariff("{'when':{},'value':450001}", null), simple(365), NO_CHARGES);
        assertThat(up.subtotalAmount()).isEqualTo(450_001);
        assertThat(up.totalAmount()).isEqualTo(450_100);
        assertThat(up.roundingAdjustmentAmount()).isEqualTo(99);

        PremiumBreakdown exact = mtplOnly(mtplTariff("{'when':{},'value':450000}", null), simple(365), NO_CHARGES);
        assertThat(exact.totalAmount()).isEqualTo(450_000);
        assertThat(exact.roundingAdjustmentAmount()).isZero();

        PremiumBreakdown withCharges = mtplOnly(mtplTariff("{'when':{},'value':450000}", null), simple(365),
                List.of(ChargeRule.percent("STAMP", "Selo", "Stamp", new BigDecimal("2.5")),
                        ChargeRule.fixed("FEE", "Taxa", "Fee", 5000)));
        assertThat(withCharges.commercialPremiumAmount()).isEqualTo(450_000);
        assertThat(withCharges.subtotalAmount()).isEqualTo(466_250);
        assertThat(withCharges.roundingAdjustmentAmount()).isEqualTo(50);
        assertThat(withCharges.totalAmount()).isEqualTo(466_300);
    }

    @Test
    void durationWithoutARowIsNotRated() {
        JsonNode temp = ReferenceData.tariff("DEMO-TEMP");
        ((ObjectNode) temp).withArray("durationFactors").remove(1); // retira a linha dos 14 dias
        EvaluationResult result = TariffEvaluator.evaluate(simple(14),
                product(ProductType.TEMPORARY, mtpl()), temp, NO_CHARGES);
        assertThat(unavailable(result).reason()).isEqualTo(UnavailableReason.DURATION_NOT_RATED);
        assertThat(unavailable(result).detail()).containsEntry("durationDays", 14);

        PremiumBreakdown sevenDays = offered(TariffEvaluator.evaluate(simple(7),
                product(ProductType.TEMPORARY, mtpl()), temp, NO_CHARGES));
        assertThat(sevenDays.durationFactor()).isEqualByComparingTo("0.06");
        assertThat(sevenDays.coverages().get(0).premiumAmount()).isEqualTo(27_000);
    }

    @Test
    void a365DayQuoteWithoutDurationFactorsUsesOne() {
        PremiumBreakdown b = mtplOnly(mtplTariff("{'when':{},'value':450000}", null), simple(365), NO_CHARGES);
        assertThat(b.durationFactor()).isEqualByComparingTo(BigDecimal.ONE);

        EvaluationResult thirtyDays = TariffEvaluator.evaluate(simple(30), product(ProductType.MTPL_ONLY, mtpl()),
                mtplTariff("{'when':{},'value':450000}", null), NO_CHARGES);
        assertThat(unavailable(thirtyDays).reason()).isEqualTo(UnavailableReason.DURATION_NOT_RATED);
    }

    @Test
    void withoutRatingTheTariffIsMissing() {
        ProductDefinition p = product(ProductType.MTPL_ONLY, mtpl());
        assertThat(unavailable(TariffEvaluator.evaluate(simple(365), p, (JsonNode) null, NO_CHARGES)).reason())
                .isEqualTo(UnavailableReason.TARIFF_MISSING);
        assertThat(unavailable(TariffEvaluator.evaluate(simple(365), p, NullNode.getInstance(), NO_CHARGES)).reason())
                .isEqualTo(UnavailableReason.TARIFF_MISSING);
        assertThat(unavailable(TariffEvaluator.evaluate(simple(365), p, MissingNode.getInstance(), NO_CHARGES))
                .reason()).isEqualTo(UnavailableReason.TARIFF_MISSING);
        assertThat(unavailable(TariffEvaluator.evaluate(simple(365), p, (RatingRules) null, NO_CHARGES)).reason())
                .isEqualTo(UnavailableReason.TARIFF_MISSING);
    }

    @Test
    void anUnreadableTariffIsInvalid() {
        ProductDefinition p = product(ProductType.MTPL_ONLY, mtpl());
        for (String bad : new String[] {"[]", "{'schemaVersion':'rating-v2','coverages':[]}",
                "{'schemaVersion':'rating-v1','coverages':[{'coverageCode':'MTPL','base':{'type':'AMOUNT','rows':"
                        + "[{'when':{},'value':'abc'}]}}]}",
                "{'schemaVersion':'rating-v1','coverages':[{'coverageCode':'XYZ','base':{'type':'AMOUNT','rows':[]}}]}"}) {
            assertThat(unavailable(TariffEvaluator.evaluate(simple(365), p, json(bad), NO_CHARGES)).reason())
                    .as(bad).isEqualTo(UnavailableReason.TARIFF_INVALID);
        }
        // a tarifa não tem a cobertura obrigatória (não passou na validação)
        JsonNode noMtpl = json("{'schemaVersion':'rating-v1','coverages':[{'coverageCode':'VTPL','base':"
                + "{'type':'AMOUNT','rows':[{'when':{},'value':1}]}}]}");
        Unavailable missing = unavailable(TariffEvaluator.evaluate(simple(365), p, noMtpl, NO_CHARGES));
        assertThat(missing.reason()).isEqualTo(UnavailableReason.TARIFF_INVALID);
        assertThat(missing.detail()).containsEntry("coverageCode", "MTPL");
    }

    @Test
    void aFactorWithoutAMatchingRowMakesTheQuoteUnavailable() {
        JsonNode tariff = mtplTariff("{'when':{},'value':100000}",
                "{'code':'PROVINCE','rows':[{'when':{'province':{'in':['NIASSA']}},'value':'1.1'}]}");
        Unavailable u = unavailable(TariffEvaluator.evaluate(simple(365), product(ProductType.MTPL_ONLY, mtpl()),
                tariff, NO_CHARGES));
        assertThat(u.reason()).isEqualTo(UnavailableReason.NO_MATCHING_ROW);
        assertThat(u.detail()).isEqualTo(Map.of("coverageCode", "MTPL", "table", "PROVINCE"));
    }

    @Test
    void selectionProblemsComeFirst() {
        EvaluationResult r = TariffEvaluator.evaluate(simple(365, requested(CoverageCode.OD_FIRE, null)),
                product(ProductType.MTPL_ONLY, mtpl()), (JsonNode) null, NO_CHARGES);
        assertThat(unavailable(r).reason()).isEqualTo(UnavailableReason.COVERAGE_NOT_OFFERED);
        EvaluationResult rr = TariffEvaluator.evaluate(simple(365, requested(CoverageCode.OD_FIRE, null)),
                product(ProductType.MTPL_ONLY, mtpl()), (RatingRules) null, NO_CHARGES);
        assertThat(unavailable(rr).reason()).isEqualTo(UnavailableReason.COVERAGE_NOT_OFFERED);
    }

    @Test
    void ratesWithoutTheValueOrCapitalTheyNeedAreUnavailable() {
        ProductDefinition p = product(ProductType.MTPL_ONLY, mtpl());
        JsonNode onVehicleValue = json("{'schemaVersion':'rating-v1','coverages':[{'coverageCode':'MTPL','base':"
                + "{'type':'RATE_OF_VEHICLE_VALUE','rows':[{'when':{},'value':'0.01'}]}}]}");
        RatingInput noValue = input(ProductType.MTPL_ONLY, vehicle(1600, null, false),
                driverBornOn(LocalDate.of(1980, 1, 1)), 365);
        assertThat(unavailable(TariffEvaluator.evaluate(noValue, p, onVehicleValue, NO_CHARGES)).reason())
                .isEqualTo(UnavailableReason.VEHICLE_VALUE_REQUIRED);

        ProductDefinition withoutCapital = product(ProductType.MTPL_ONLY,
                new CoverageDefinition(CoverageCode.MTPL, true, List.of(), mz.co.moseguros.catalog.CapitalType.FIXED,
                        null, null, null, null, null, null));
        JsonNode onCapital = json("{'schemaVersion':'rating-v1','coverages':[{'coverageCode':'MTPL','base':"
                + "{'type':'RATE_OF_CAPITAL','rows':[{'when':{},'value':'0.01'}]}}]}");
        assertThat(unavailable(TariffEvaluator.evaluate(simple(365), withoutCapital, onCapital, NO_CHARGES)).reason())
                .isEqualTo(UnavailableReason.TARIFF_INVALID);

        PremiumBreakdown b = offered(TariffEvaluator.evaluate(simple(365), p, onCapital, NO_CHARGES));
        assertThat(b.coverages().get(0).premiumAmount()).isEqualTo(3_000_000);
        assertThat(b.coverages().get(0).base().amount()).isEqualByComparingTo("3000000");
    }

    @Test
    void conditionsOnCapitalUseTheResolvedCapitalOfTheCoverage() {
        ProductDefinition p = product(ProductType.MTPL_ONLY, mtpl(),
                TariffFixtures.options(CoverageCode.VTPL, 500_000_000L, 1_000_000_000L));
        JsonNode tariff = json("{'schemaVersion':'rating-v1','coverages':["
                + "{'coverageCode':'MTPL','base':{'type':'AMOUNT','rows':[{'when':{'capital':{'gte':400000000}},"
                + "'value':1},{'when':{},'value':2}]}},"
                + "{'coverageCode':'VTPL','base':{'type':'RATE_OF_CAPITAL','rows':[{'when':{'capital':{'lt':600000000}},"
                + "'value':'0.0004'},{'when':{},'value':'0.0003'}]}}]}");
        PremiumBreakdown small = offered(TariffEvaluator.evaluate(
                simple(365, requested(CoverageCode.VTPL, 400_000_000L)), p, tariff, NO_CHARGES));
        assertThat(small.coverages().get(0).premiumAmount()).isEqualTo(2);
        assertThat(small.coverages().get(1).capitalAmount()).isEqualTo(500_000_000L);
        assertThat(small.coverages().get(1).premiumAmount()).isEqualTo(200_000);
        PremiumBreakdown big = offered(TariffEvaluator.evaluate(
                simple(365, requested(CoverageCode.VTPL, 700_000_000L)), p, tariff, NO_CHARGES));
        assertThat(big.coverages().get(1).capitalAmount()).isEqualTo(1_000_000_000L);
        assertThat(big.coverages().get(1).premiumAmount()).isEqualTo(300_000);
    }

    @Test
    void allConditionKindsAreChecked() {
        JsonNode tariff = mtplTariff("{'when':{'vehicleCategory':{'in':['LIGHT_PASSENGER']},'vehicleUsage':"
                + "{'in':['PRIVATE']},'province':{'in':['MAPUTO_CITY']},'hasTracker':false,'vehicleAgeYears':{'gte':7,"
                + "'lt':8},'engineCc':{'gte':1000,'lt':2000},'seats':{'lt':6},'driverAgeYears':{'gte':40},"
                + "'licenceYears':{'gte':17},'durationDays':{'gte':365},'capital':{'gte':300000000},"
                + "'vehicleValue':{'gte':1,'lt':85000001}},'value':1},{'when':{},'value':2}", null);
        PremiumBreakdown b = mtplOnly(tariff, simple(365), NO_CHARGES);
        assertThat(b.coverages().get(0).base().rowIndex()).isZero();

        JsonNode weight = mtplTariff("{'when':{'grossWeightKg':{'gte':0}},'value':1},{'when':{},'value':2}", null);
        assertThat(mtplOnly(weight, simple(365), NO_CHARGES).coverages().get(0).base().rowIndex()).isEqualTo(1);
    }

    @Test
    void theBreakdownIsSerialisedWithDecimalsAsText() throws Exception {
        JsonNode c05 = null;
        for (JsonNode c : ReferenceData.json("cases.json")) {
            if (c.get("id").asText().equals("C05")) {
                c05 = c;
            }
        }
        PremiumBreakdown b = offered(TariffEvaluator.evaluate(ReferenceData.input(c05),
                ReferenceData.product("DEMO-AUTO"), ReferenceData.tariff("DEMO-AUTO"), ReferenceData.charges()));
        JsonNode json = json(b.toJson().toString());
        assertThat(json.get("provider").asText()).isEqualTo("INTERNAL");
        assertThat(json.get("variables")).isEqualTo(json("{'vehicleCategory':'LIGHT_PASSENGER','vehicleUsage':'PRIVATE',"
                + "'province':'MAPUTO_CITY','hasTracker':false,'vehicleAgeYears':3,'driverAgeYears':35,'licenceYears':15,"
                + "'durationDays':365,'vehicleValue':123456789}"));
        assertThat(json.get("durationFactor").asText()).isEqualTo("1");
        JsonNode collision = json.get("coverages").get(1);
        assertThat(collision).isEqualTo(json("{'code':'OD_COLLISION','capitalAmount':123456789,"
                + "'deductible':{'type':'PERCENT','percent':'10','minAmount':2500000},"
                + "'base':{'type':'RATE_OF_VEHICLE_VALUE','rowIndex':0,'rowLabel':'Até 4 anos','value':'0.025',"
                + "'amount':'3086419.725'},"
                + "'factors':[{'code':'DRIVER_AGE','label':'Idade do condutor','rowIndex':1,'rowLabel':null,'value':'1.00'},"
                + "{'code':'TRACKER','label':'Localizador','rowIndex':1,'rowLabel':null,'value':'1.00'},"
                + "{'code':'PROVINCE','label':'Província de circulação','rowIndex':0,'rowLabel':null,'value':'1.15'}],"
                + "'bonusMalusFactor':'1','exactPremium':'3549382.683750000','premiumAmount':3549383}"));
        JsonNode mtpl = json.get("coverages").get(0);
        assertThat(mtpl.get("deductible")).isEqualTo(json("{'type':'NONE'}"));
        assertThat(mtpl.get("base").get("value").isIntegralNumber()).isTrue();
        assertThat(mtpl.get("base").get("rowLabel").asText()).isEqualTo("Ligeiro de passageiros, particular");
        assertThat(json.get("commercialPremiumAmount").asLong()).isEqualTo(5_093_766);
        assertThat(json.get("charges").get(0)).isEqualTo(json("{'code':'DEMO_STAMP','labelPt':'Imposto de selo "
                + "(provisório)','labelEn':'Stamp duty (provisional)','type':'PERCENT','value':'2.5','amount':127344}"));
        assertThat(json.get("charges").get(1).get("value").asLong()).isEqualTo(5000);
        assertThat(json.get("subtotalAmount").asLong()).isEqualTo(5_226_110);
        assertThat(json.get("roundingAdjustmentAmount").asLong()).isEqualTo(90);
        assertThat(json.get("totalAmount").asLong()).isEqualTo(5_226_200);

        PremiumBreakdown.Deductible fixedDeductible = PremiumBreakdown.Deductible.of(
                TariffFixtures.vehicleValue(CoverageCode.OD_GLASS));
        assertThat(fixedDeductible.amount()).isEqualTo(500_000L);
    }

    @Test
    void theBreakdownOfAFixedDeductibleHasTheAmount() {
        ProductDefinition p = product(ProductType.COMPREHENSIVE, mtpl(), TariffFixtures.vehicleValue(CoverageCode.OD_GLASS),
                fixed(CoverageCode.ASSISTANCE, false, 2_500_000L));
        JsonNode tariff = json("{'schemaVersion':'rating-v1','coverages':["
                + "{'coverageCode':'MTPL','base':{'type':'AMOUNT','rows':[{'when':{},'value':1000}]}},"
                + "{'coverageCode':'OD_GLASS','base':{'type':'RATE_OF_VEHICLE_VALUE','rows':[{'when':{},'value':'0.001'}]}},"
                + "{'coverageCode':'ASSISTANCE','base':{'type':'AMOUNT','rows':[{'when':{},'value':120000}]}}]}");
        PremiumBreakdown b = offered(TariffEvaluator.evaluate(simple(365, requested(CoverageCode.OD_GLASS, null)),
                p, tariff, NO_CHARGES));
        assertThat(b.coverages()).extracting(PremiumBreakdown.CoverageLine::code).containsExactly("MTPL", "OD_GLASS");
        JsonNode glass = json(b.toJson().toString()).get("coverages").get(1);
        assertThat(glass.get("deductible")).isEqualTo(json("{'type':'FIXED','amount':500000}"));
        assertThat(glass.get("premiumAmount").asLong()).isEqualTo(85_000);
    }
}
