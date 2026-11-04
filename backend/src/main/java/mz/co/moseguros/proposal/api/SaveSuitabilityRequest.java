package mz.co.moseguros.proposal.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.application.SuitabilityService.AnswerInput;

/**
 * Questionário de adequação ({@code saveProposalSuitability}, S4-02 §8 e §9). Erros juntos em
 * {@code errors[]}: {@code ANSWERS_INCOMPLETE}, {@code ANSWER_DUPLICATED}, {@code UNKNOWN_QUESTION},
 * {@code INVALID_ANSWER}.
 */
public record SaveSuitabilityRequest(
        @ArraySchema(arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Uma resposta por pergunta (as cinco de api/proposal-questions.json)"))
        List<SuitabilityAnswerRequest> answers,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {

    /** Resposta a uma pergunta de adequação. */
    public record SuitabilityAnswerRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "SUIT_NEED",
                    description = "Código de api/proposal-questions.json (suitability)")
            String questionCode,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "OWN_DAMAGE",
                    description = "Uma das options da pergunta")
            String answer) {
    }

    List<AnswerInput> toInputs() {
        return answers == null ? null : answers.stream()
                .map(a -> a == null ? null : new AnswerInput(a.questionCode(), a.answer()))
                .toList();
    }
}
