package mz.co.moseguros.proposal.application;

import java.util.List;

import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;

/**
 * Proposta com as respostas e o estado KYC do cliente, para o {@code ProposalResponse} (S4-02 §10).
 *
 * @param declarations       vazia até serem gravadas
 * @param suitabilityAnswers vazia até serem gravadas
 * @param kycState           {@code IdentityApi.kycStatus(customerUserId).state()}
 */
public record ProposalDetails(Proposal proposal, List<Declaration> declarations,
                              List<SuitabilityAnswer> suitabilityAnswers, KycState kycState) {
}
