import type { TFunction } from 'i18next';
import type { InsurerProposalResponse } from '../../api/generated';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';

/** Códigos 409 que obrigam a recarregar o detalhe (a proposta mudou entretanto). */
export const reloadCodes = ['CONCURRENT_MODIFICATION', 'PROPOSAL_NOT_UNDER_REVIEW'];

/** Resultado de uma decisão, entregue à página. */
export type DecisionOutcome =
  { kind: 'done'; notice: string } | { kind: 'reload'; message: string };

/**
 * Trata o erro de uma decisão: os 409 de concorrência ou de estado vão para a página (que
 * recarrega o detalhe); os outros ficam no diálogo (devolve-se a mensagem e o problema).
 */
export function handleDecisionError(
  t: TFunction,
  error: unknown,
  onOutcome: (outcome: DecisionOutcome) => void,
): { message: string; problem: Problem } | null {
  const problem = getProblem(error);
  const message = problemMessage(t, problem);
  if (problem.status === 409 && reloadCodes.includes(problem.code)) {
    onOutcome({ kind: 'reload', message });
    return null;
  }
  return { message, problem };
}

/** Motivo para "Aceitar" estar desactivado (`errors.<código>`), ou `null`. */
export function acceptBlocker(proposal: InsurerProposalResponse): string | null {
  const inspection = proposal.inspection;
  if (inspection.required && inspection.status !== 'APPROVED') return 'INSPECTION_NOT_APPROVED';
  if (proposal.kyc.requiredForAutomaticAcceptance && proposal.kyc.state !== 'VERIFIED') {
    return 'KYC_NOT_VERIFIED';
  }
  return null;
}
