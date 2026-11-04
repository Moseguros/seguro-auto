import { useQuery, useQueryClient } from '@tanstack/react-query';
import { createContext, useCallback, useContext, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getProposal, type ProposalResponse } from '../../api/generated';
import type { Problem } from '../../errors/problem';
import type { ProposalMode, WizardStep } from './wizardSteps';

/** Consulta de uma proposta (cliente e mediador usam o mesmo `getProposal`). */
export const proposalKey = (id: string) => ['proposal', id] as const;

/** Listas das propostas (invalidadas depois de cada escrita). */
export const myProposalsKey = ['my-proposals'] as const;
export const brokerProposalsKey = ['broker-proposals'] as const;

export function useProposal(id: string | undefined) {
  return useQuery({
    queryKey: proposalKey(id ?? ''),
    queryFn: ({ signal }) => getProposal(id ?? '', undefined, signal),
    enabled: !!id,
  });
}

/**
 * Escritas sobre a proposta: guarda a resposta na cache e trata os 409 comuns a todos os passos
 * (`CONCURRENT_MODIFICATION` → aviso e recarrega; `PROPOSAL_NOT_EDITABLE` → recarrega, e o ecrã
 * passa ao estado).
 */
export function useProposalMutations(proposalId: string) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [notice, setNotice] = useState<string | null>(null);

  const reload = useCallback(
    () => queryClient.invalidateQueries({ queryKey: proposalKey(proposalId) }),
    [queryClient, proposalId],
  );

  const update = useCallback(
    (proposal: ProposalResponse) => {
      queryClient.setQueryData(proposalKey(proposal.id), proposal);
      void queryClient.invalidateQueries({ queryKey: myProposalsKey });
      void queryClient.invalidateQueries({ queryKey: brokerProposalsKey });
    },
    [queryClient],
  );

  const handleCommon = useCallback(
    (problem: Problem) => {
      if (problem.code === 'CONCURRENT_MODIFICATION') {
        setNotice(t('proposal.wizard.concurrent'));
        void reload();
        return true;
      }
      if (problem.code === 'PROPOSAL_NOT_EDITABLE') {
        void reload();
        return true;
      }
      return false;
    },
    [reload, t],
  );

  return { update, reload, handleCommon, notice, clearNotice: () => setNotice(null) };
}

/** O que cada passo recebe do `ProposalWizard`. */
export interface WizardContextValue {
  proposal: ProposalResponse;
  mode: ProposalMode;
  current: WizardStep;
  /** Grava a resposta de uma escrita (cache e listas). */
  update(proposal: ProposalResponse): void;
  /** Trata os 409 comuns; `true` se tratou. */
  handleCommon(problem: Problem): boolean;
  /** Recarrega a proposta. */
  reload(): Promise<unknown>;
  goTo(step: WizardStep): void;
  /** Passo seguinte (entre os visíveis). */
  next(): void;
  /** Passo anterior, ou `undefined` no primeiro. */
  back?: () => void;
  /** "Agora não" na identidade. */
  kycSkipped: boolean;
  skipKyc(): void;
}

export const WizardContext = createContext<WizardContextValue | null>(null);

export function useWizard(): WizardContextValue {
  const value = useContext(WizardContext);
  if (!value) throw new Error('useWizard fora do ProposalWizard');
  return value;
}
