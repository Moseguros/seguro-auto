import type { IntentView } from '../../api/generated';
import { getProblem } from '../../errors/problem';

/** Caminhos da web do cliente para os pagamentos (S5-11 §1). */
export const paymentPaths = {
  list: '/cliente/pagamentos',
  intent: (intentId: string) => `/cliente/pagamentos/${intentId}`,
  pay: (proposalId: string) => `/cliente/propostas/${proposalId}/pagar`,
  proposal: (proposalId: string) => `/cliente/propostas/${proposalId}`,
  proposals: '/cliente/propostas',
};

/** A proposta do pagamento, se o alvo for uma proposta. */
export function proposalIdOf(intent: Pick<IntentView, 'target'>): string | undefined {
  return intent.target.type === 'PROPOSAL' ? intent.target.id : undefined;
}

/** Só se repete um pedido sem resposta do servidor (erro de rede), no máximo duas vezes. */
export function retryNetworkOnly(failureCount: number, error: unknown): boolean {
  return getProblem(error).status === 0 && failureCount < 2;
}
