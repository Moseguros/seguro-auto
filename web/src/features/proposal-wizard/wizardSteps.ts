import type { InfoRequestItem, ProposalResponse, ProposalStatus } from '../../api/generated';

/** Passos da proposta (`?passo=`), pela ordem do S4-08 §3. */
export const wizardSteps = [
  'tomador',
  'declaracoes',
  'adequacao',
  'vistoria',
  'identidade',
  'resumo',
] as const;

export type WizardStep = (typeof wizardSteps)[number];

/** Quem usa os passos: o cliente (`/cliente`) ou o mediador em nome dele (`/mediador`). */
export type ProposalMode = 'customer' | 'broker';

/** Estados em que a proposta se altera (os passos); nos outros mostra-se o estado. */
export const editableStatuses: readonly ProposalStatus[] = ['DRAFT', 'INFO_REQUESTED'];

export function isEditable(status: ProposalStatus): boolean {
  return editableStatuses.includes(status);
}

export function isWizardStep(value: string | null | undefined): value is WizardStep {
  return !!value && (wizardSteps as readonly string[]).includes(value);
}

/** Proposta com o que os passos precisam de ler. */
export type StepProposal = Pick<ProposalResponse, 'checklist' | 'inspection' | 'kyc'>;

/** Passos mostrados: a vistoria só com `inspection.required`. */
export function visibleSteps(proposal: Pick<ProposalResponse, 'inspection'>): WizardStep[] {
  return wizardSteps.filter((step) => step !== 'vistoria' || proposal.inspection.required);
}

/**
 * O passo está concluído? `kycSkipped`: o utilizador escolheu "Agora não" na identidade.
 * O resumo nunca está concluído (é onde se assina).
 */
export function isStepDone(step: WizardStep, proposal: StepProposal, kycSkipped = false): boolean {
  switch (step) {
    case 'tomador':
      return true;
    case 'declaracoes':
      return proposal.checklist.declarations === 'DONE';
    case 'adequacao':
      return proposal.checklist.suitability === 'DONE';
    case 'vistoria':
      return !proposal.inspection.required || proposal.checklist.inspection === 'DONE';
    case 'identidade':
      return kycSkipped || proposal.kyc.state === 'VERIFIED' || proposal.kyc.state === 'PENDING';
    case 'resumo':
      return false;
  }
}

/** Primeiro passo por concluir (o resumo se estiver tudo feito). */
export function firstPendingStep(proposal: StepProposal, kycSkipped = false): WizardStep {
  return visibleSteps(proposal).find((step) => !isStepDone(step, proposal, kycSkipped)) ?? 'resumo';
}

/** Passo a seguir a `step` entre os visíveis (ou o próprio, se for o último). */
export function nextStep(step: WizardStep, proposal: Pick<ProposalResponse, 'inspection'>) {
  const steps = visibleSteps(proposal);
  const index = steps.indexOf(step);
  return steps[Math.min(index + 1, steps.length - 1)] ?? 'resumo';
}

/** Passo antes de `step` entre os visíveis, ou `null` no primeiro. */
export function previousStep(
  step: WizardStep,
  proposal: Pick<ProposalResponse, 'inspection'>,
): WizardStep | null {
  const steps = visibleSteps(proposal);
  const index = steps.indexOf(step);
  return index > 0 ? (steps[index - 1] ?? null) : null;
}

/** Passo de cada item de um pedido de informação da seguradora. */
export const infoRequestItemStep: Record<InfoRequestItem, WizardStep> = {
  DECLARATIONS: 'declaracoes',
  SUITABILITY: 'adequacao',
  INSPECTION: 'vistoria',
  KYC: 'identidade',
  POLICYHOLDER: 'tomador',
  OTHER: 'resumo',
};

/** Passo de cada item de `PROPOSAL_INCOMPLETE.missing` (S4-04/S4-05). */
export const missingItemStep: Record<string, WizardStep> = {
  DECLARATIONS: 'declaracoes',
  SUITABILITY: 'adequacao',
  SUITABILITY_ACKNOWLEDGEMENT: 'adequacao',
  INSPECTION: 'vistoria',
  START_DATE: 'tomador',
  SIGNATURE: 'resumo',
};

export interface ProposalPaths {
  /** Lista e base do detalhe (`<list>/:id`). */
  list: string;
  /** Onde se faz uma nova simulação (o botão "Simular seguro"). */
  quotes: string;
}

export const proposalPaths: Record<ProposalMode, ProposalPaths> = {
  customer: { list: '/cliente/propostas', quotes: '/cliente/simular' },
  broker: { list: '/mediador/propostas', quotes: '/mediador/cotacoes' },
};

/** Caminho de um passo da proposta. */
export function stepPath(mode: ProposalMode, proposalId: string, step?: WizardStep): string {
  const base = `${proposalPaths[mode].list}/${proposalId}`;
  return step ? `${base}?passo=${step}` : base;
}

/** O que falta, lido do `checklist` (os mesmos códigos de `PROPOSAL_INCOMPLETE.missing`). */
export function missingFromChecklist(proposal: StepProposal): string[] {
  const missing: string[] = [];
  if (proposal.checklist.declarations !== 'DONE') missing.push('DECLARATIONS');
  if (proposal.checklist.suitability === 'MISSING') missing.push('SUITABILITY');
  if (proposal.checklist.suitability === 'ACK_REQUIRED') {
    missing.push('SUITABILITY_ACKNOWLEDGEMENT');
  }
  if (
    proposal.inspection.required &&
    (proposal.checklist.inspection === 'MISSING' || proposal.checklist.inspection === 'REJECTED')
  ) {
    missing.push('INSPECTION');
  }
  return missing;
}
