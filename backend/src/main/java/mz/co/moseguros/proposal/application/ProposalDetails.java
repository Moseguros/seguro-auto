package mz.co.moseguros.proposal.application;

import java.util.List;

import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.Signature;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;

/**
 * Proposta com as respostas, o estado KYC do cliente, a assinatura válida e os documentos do produto,
 * para o {@code ProposalResponse} (S4-02 §10, S4-04 §8).
 *
 * @param declarations       vazia até serem gravadas
 * @param suitabilityAnswers vazia até serem gravadas
 * @param kycState           {@code IdentityApi.kycStatus(customerUserId).state()}
 * @param signature          assinatura válida ({@code current_signature_id}) ou {@code null}
 * @param productDocuments   Condições Gerais e Informação Pré-contratual na língua do tomador (ou pt)
 */
public record ProposalDetails(Proposal proposal, List<Declaration> declarations,
                              List<SuitabilityAnswer> suitabilityAnswers, KycState kycState, Signature signature,
                              List<ProductDocuments.Selected> productDocuments) {
}
