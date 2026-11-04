package mz.co.moseguros.proposal.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.domain.UnderwritingEvaluator.Evaluation;
import mz.co.moseguros.proposal.domain.UnderwritingEvaluator.Input;
import org.junit.jupiter.api.Test;

/** Os 12 casos de referência do S4-05 §3 (valores por defeito salvo indicação; montantes em centavos). */
class UnderwritingEvaluatorTest {

    static final Instant NOW = Instant.parse("2027-01-06T08:40:00Z");
    static final UnderwritingRules DEFAULTS = UnderwritingRules.DEFAULTS;
    static final List<DeclarationQuestion> ALL_NO = List.of();

    static Evaluation evaluate(UnderwritingRules rules, boolean od, long total, KycState kyc,
                               List<DeclarationQuestion> positive, InspectionStatus inspection, Long vehicleValue) {
        return UnderwritingEvaluator.evaluate(rules, new Input(od, total, kyc, positive, inspection, vehicleValue), NOW);
    }

    static UnderwritingRules with(boolean enabled, boolean liabilityNegative) {
        return new UnderwritingRules(1, enabled, DEFAULTS.simplifiedKycMaxPremium(), liabilityNegative,
                DEFAULTS.ownDamageMaxVehicleValue(), DEFAULTS.ownDamageRequireAllDeclarationsNegative(),
                UnderwritingRules.Source.PRODUCT);
    }

    @Test
    void defaultsAreTheProvisionalValues() {
        assertThat(DEFAULTS.schemaVersion()).isEqualTo(1);
        assertThat(DEFAULTS.enabled()).isTrue();
        assertThat(DEFAULTS.simplifiedKycMaxPremium()).isEqualTo(1_000_000L);
        assertThat(DEFAULTS.liabilityRequireAllDeclarationsNegative()).isFalse();
        assertThat(DEFAULTS.ownDamageMaxVehicleValue()).isEqualTo(150_000_000L);
        assertThat(DEFAULTS.ownDamageRequireAllDeclarationsNegative()).isTrue();
        assertThat(DEFAULTS.source()).isEqualTo(UnderwritingRules.Source.DEFAULT);
    }

    @Test
    void case01MtplBelowTheSimplifiedKycLimitIsAccepted() {
        Evaluation e = evaluate(DEFAULTS, false, 450_000, KycState.NONE, ALL_NO, null, 80_000_000L);
        assertThat(e.outcome()).isEqualTo(UnderwritingOutcome.AUTOMATIC);
        assertThat(e.failures()).isEmpty();
    }

    @Test
    void case02TheSimplifiedKycLimitIsStrict() {
        Evaluation e = evaluate(DEFAULTS, false, 1_000_000, KycState.NONE, ALL_NO, null, null);
        assertThat(e.outcome()).isEqualTo(UnderwritingOutcome.MANUAL_REVIEW);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.KYC_REQUIRED);
    }

    @Test
    void case03VerifiedKycAboveTheLimitIsAccepted() {
        assertThat(evaluate(DEFAULTS, false, 1_200_000, KycState.VERIFIED, ALL_NO, null, null).outcome())
                .isEqualTo(UnderwritingOutcome.AUTOMATIC);
    }

    @Test
    void case04OutdatedKycIsNotVerified() {
        Evaluation e = evaluate(DEFAULTS, false, 1_200_000, KycState.OUTDATED, ALL_NO, null, null);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.KYC_REQUIRED);
        for (KycState state : List.of(KycState.PENDING, KycState.REJECTED, KycState.NONE)) {
            assertThat(evaluate(DEFAULTS, false, 1_200_000, state, ALL_NO, null, null).failures())
                    .containsExactly(UnderwritingFailure.KYC_REQUIRED);
        }
    }

    @Test
    void case05PositiveDeclarationIsAllowedForLiabilityByDefault() {
        assertThat(evaluate(DEFAULTS, false, 450_000, KycState.NONE, List.of(DeclarationQuestion.PREVIOUS_REFUSAL),
                null, null).outcome()).isEqualTo(UnderwritingOutcome.AUTOMATIC);
    }

    @Test
    void case06PositiveDeclarationWithLiabilityRequiringAllNegative() {
        Evaluation e = evaluate(with(true, true), false, 450_000, KycState.NONE,
                List.of(DeclarationQuestion.PREVIOUS_REFUSAL), null, null);
        assertThat(e.outcome()).isEqualTo(UnderwritingOutcome.MANUAL_REVIEW);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.DECLARATION_POSITIVE);
    }

    @Test
    void case07TemporaryIsAccepted() {
        assertThat(evaluate(DEFAULTS, false, 90_000, KycState.NONE, ALL_NO, null, null).outcome())
                .isEqualTo(UnderwritingOutcome.AUTOMATIC);
    }

    @Test
    void case08OwnDamageWithTheInspectionNotApproved() {
        Evaluation e = evaluate(DEFAULTS, true, 3_500_000, KycState.VERIFIED, ALL_NO, InspectionStatus.SUBMITTED,
                80_000_000L);
        assertThat(e.outcome()).isEqualTo(UnderwritingOutcome.MANUAL_REVIEW);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.INSPECTION_NOT_APPROVED);
    }

    @Test
    void case09OwnDamageWithTheInspectionApproved() {
        assertThat(evaluate(DEFAULTS, true, 3_500_000, KycState.VERIFIED, ALL_NO, InspectionStatus.APPROVED,
                80_000_000L).outcome()).isEqualTo(UnderwritingOutcome.AUTOMATIC);
    }

    @Test
    void case10VehicleValueAboveTheLimit() {
        Evaluation e = evaluate(DEFAULTS, true, 3_500_000, KycState.VERIFIED, ALL_NO, InspectionStatus.APPROVED,
                160_000_000L);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.VEHICLE_VALUE_ABOVE_LIMIT);
        // O limite é inclusivo: só acima dele falha.
        assertThat(evaluate(DEFAULTS, true, 3_500_000, KycState.VERIFIED, ALL_NO, InspectionStatus.APPROVED,
                150_000_000L).outcome()).isEqualTo(UnderwritingOutcome.AUTOMATIC);
    }

    @Test
    void case11AllFailuresAreCollectedInOrder() {
        Evaluation e = evaluate(DEFAULTS, true, 3_500_000, KycState.PENDING, ALL_NO, InspectionStatus.APPROVED, null);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.KYC_REQUIRED,
                UnderwritingFailure.VEHICLE_VALUE_MISSING);
    }

    @Test
    void case12DisabledSendsEverythingToReviewWithoutEvaluatingTheRest() {
        Evaluation e = evaluate(with(false, false), true, 3_500_000, KycState.NONE,
                List.of(DeclarationQuestion.CLAIMS_LAST_3_YEARS), InspectionStatus.PENDING, null);
        assertThat(e.outcome()).isEqualTo(UnderwritingOutcome.MANUAL_REVIEW);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.AUTO_ACCEPT_DISABLED);
        assertThat(evaluate(with(false, false), false, 90_000, KycState.VERIFIED, ALL_NO, null, null).failures())
                .containsExactly(UnderwritingFailure.AUTO_ACCEPT_DISABLED);
    }

    @Test
    void ownDamageDeclarationsUseTheOwnDamageRule() {
        Evaluation e = evaluate(DEFAULTS, true, 3_500_000, KycState.VERIFIED,
                List.of(DeclarationQuestion.CLAIMS_LAST_3_YEARS), InspectionStatus.APPROVED, 80_000_000L);
        assertThat(e.failures()).containsExactly(UnderwritingFailure.DECLARATION_POSITIVE);
    }

    @Test
    void theRecordHasTheRulesAndTheInputs() {
        JsonNode json = evaluate(DEFAULTS, false, 1_200_000, KycState.PENDING, ALL_NO, null, 80_000_000L).toJson();
        assertThat(json.get("rulesSchemaVersion").asInt()).isEqualTo(1);
        assertThat(json.get("rulesSource").asText()).isEqualTo("DEFAULT");
        assertThat(json.get("evaluatedAt").asText()).isEqualTo("2027-01-06T08:40:00Z");
        assertThat(json.get("outcome").asText()).isEqualTo("MANUAL_REVIEW");
        assertThat(json.get("failures").get(0).asText()).isEqualTo("KYC_REQUIRED");
        JsonNode inputs = json.get("inputs");
        assertThat(inputs.get("hasOwnDamage").asBoolean()).isFalse();
        assertThat(inputs.get("totalAmount").asLong()).isEqualTo(1_200_000L);
        assertThat(inputs.get("kycState").asText()).isEqualTo("PENDING");
        assertThat(inputs.get("simplifiedKycMaxPremium").asLong()).isEqualTo(1_000_000L);
        assertThat(inputs.get("positiveDeclarations")).isEmpty();
        assertThat(inputs.get("inspectionStatus").isNull()).isTrue();
        assertThat(inputs.get("vehicleValue").asLong()).isEqualTo(80_000_000L);
        assertThat(inputs.get("maxVehicleValue").asLong()).isEqualTo(150_000_000L);
    }
}
