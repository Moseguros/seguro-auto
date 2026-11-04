package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resultado do questionário de adequação ({@code suitability_result}, S4-02 §8): {@link #WARNED}
 * com pelo menos um {@link SuitabilityReason}; o cliente confirma conscientemente os avisos
 * ({@code acknowledgeProposalSuitability}) ou desiste.
 */
@Schema(enumAsRef = true)
public enum SuitabilityResult {
    SUITABLE,
    WARNED
}
