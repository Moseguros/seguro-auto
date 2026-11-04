package mz.co.moseguros.proposal.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.identity.KycState;

/**
 * Avaliação das regras de aceitação automática de uma proposta (S4-05 §3). Função pura: avalia
 * <strong>todas</strong> as condições, pela ordem da tabela da ficha, e junta as falhas; com
 * {@code enabled = false} só há {@link UnderwritingFailure#AUTO_ACCEPT_DISABLED}. {@code OUTDATED},
 * {@code PENDING}, {@code REJECTED} e {@code NONE} contam como "não {@code VERIFIED}".
 */
public final class UnderwritingEvaluator {

    private UnderwritingEvaluator() {
    }

    /**
     * Factos da proposta.
     *
     * @param hasOwnDamage         alguma cobertura {@code OD_*}
     * @param totalAmount          {@code total_amount} (centavos)
     * @param positiveDeclarations perguntas com resposta "sim", pela ordem das perguntas
     * @param inspectionStatus     estado da vistoria; {@code null} sem vistoria
     * @param vehicleValue         {@code vehicle_snapshot.commercialValue.amount}; {@code null} sem valor
     */
    public record Input(boolean hasOwnDamage, long totalAmount, KycState kycState,
                        List<DeclarationQuestion> positiveDeclarations, InspectionStatus inspectionStatus,
                        Long vehicleValue) {

        public Input {
            Objects.requireNonNull(kycState, "kycState");
            positiveDeclarations = List.copyOf(positiveDeclarations);
        }
    }

    /** Resultado, com as regras e os factos usados (para o {@code underwriting_result}). */
    public record Evaluation(UnderwritingOutcome outcome, List<UnderwritingFailure> failures, UnderwritingRules rules,
                             Input input, Instant evaluatedAt) {

        public Evaluation {
            failures = List.copyOf(failures);
        }

        public boolean automatic() {
            return outcome == UnderwritingOutcome.AUTOMATIC;
        }

        /** JSON gravado em {@code underwriting_result} (S4-05 §3). */
        public JsonNode toJson() {
            JsonNodeFactory f = JsonNodeFactory.instance;
            ObjectNode root = f.objectNode();
            root.put("rulesSchemaVersion", rules.schemaVersion());
            root.put("rulesSource", rules.source().name());
            root.put("evaluatedAt", evaluatedAt.toString());
            root.put("outcome", outcome.name());
            ArrayNode list = root.putArray("failures");
            failures.forEach(failure -> list.add(failure.name()));
            ObjectNode inputs = root.putObject("inputs");
            inputs.put("hasOwnDamage", input.hasOwnDamage());
            inputs.put("totalAmount", input.totalAmount());
            inputs.put("kycState", input.kycState().name());
            inputs.put("simplifiedKycMaxPremium", rules.simplifiedKycMaxPremium());
            ArrayNode positive = inputs.putArray("positiveDeclarations");
            input.positiveDeclarations().forEach(q -> positive.add(q.name()));
            if (input.inspectionStatus() == null) {
                inputs.putNull("inspectionStatus");
            } else {
                inputs.put("inspectionStatus", input.inspectionStatus().name());
            }
            if (input.vehicleValue() == null) {
                inputs.putNull("vehicleValue");
            } else {
                inputs.put("vehicleValue", input.vehicleValue());
            }
            inputs.put("maxVehicleValue", rules.ownDamageMaxVehicleValue());
            return root;
        }
    }

    /** Avalia as regras com os factos da proposta. */
    public static Evaluation evaluate(UnderwritingRules rules, Input input, Instant now) {
        Objects.requireNonNull(rules, "rules");
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(now, "now");
        List<UnderwritingFailure> failures = new ArrayList<>();
        if (!rules.enabled()) {
            failures.add(UnderwritingFailure.AUTO_ACCEPT_DISABLED);
        } else {
            if (rules.kycRequired(input.kycState() == KycState.VERIFIED, input.totalAmount())) {
                failures.add(UnderwritingFailure.KYC_REQUIRED);
            }
            boolean requireNegative = input.hasOwnDamage() ? rules.ownDamageRequireAllDeclarationsNegative()
                    : rules.liabilityRequireAllDeclarationsNegative();
            if (requireNegative && !input.positiveDeclarations().isEmpty()) {
                failures.add(UnderwritingFailure.DECLARATION_POSITIVE);
            }
            if (input.hasOwnDamage()) {
                if (input.inspectionStatus() != InspectionStatus.APPROVED) {
                    failures.add(UnderwritingFailure.INSPECTION_NOT_APPROVED);
                }
                if (input.vehicleValue() == null) {
                    failures.add(UnderwritingFailure.VEHICLE_VALUE_MISSING);
                } else if (input.vehicleValue() > rules.ownDamageMaxVehicleValue()) {
                    failures.add(UnderwritingFailure.VEHICLE_VALUE_ABOVE_LIMIT);
                }
            }
        }
        UnderwritingOutcome outcome = failures.isEmpty() ? UnderwritingOutcome.AUTOMATIC
                : UnderwritingOutcome.MANUAL_REVIEW;
        return new Evaluation(outcome, failures, rules, input, now);
    }
}
