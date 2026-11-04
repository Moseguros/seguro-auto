package mz.co.moseguros.proposal.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.application.DeclarationService.AnswerInput;

/**
 * Declarações de risco ({@code saveProposalDeclarations}, S4-02 §7 e §9): as cinco perguntas, os
 * campos de seguimento com "sim" e a confirmação de veracidade. Erros juntos em {@code errors[]}:
 * {@code ANSWERS_INCOMPLETE}, {@code ANSWER_DUPLICATED}, {@code UNKNOWN_QUESTION}, {@code REQUIRED},
 * {@code TOO_SHORT}, {@code TOO_LONG}, {@code OUT_OF_RANGE}, {@code NOT_ALLOWED}, {@code MUST_BE_TRUE}.
 */
public record SaveDeclarationsRequest(
        @ArraySchema(arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Uma resposta por pergunta (as cinco de api/proposal-questions.json)"))
        List<DeclarationAnswerRequest> answers,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Tem de ser true")
        Boolean truthfulnessConfirmed,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {

    /** Resposta a uma declaração. */
    public record DeclarationAnswerRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "CLAIMS_LAST_3_YEARS",
                    description = "Código de api/proposal-questions.json (declarations)")
            String questionCode,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Boolean answer,
            @Schema(description = "1–20; só em CLAIMS_LAST_3_YEARS com resposta sim", example = "1")
            Integer claimsCount,
            @Schema(description = "10–500 caracteres; obrigatório com resposta sim, proibido com não")
            String details) {
    }

    List<AnswerInput> toInputs() {
        return answers == null ? null : answers.stream()
                .map(a -> a == null ? null : new AnswerInput(a.questionCode(), a.answer(), a.claimsCount(), a.details()))
                .toList();
    }
}
