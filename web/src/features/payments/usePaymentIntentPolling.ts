import { retryNetworkOnly } from './paymentPaths';
import { useGetPaymentIntent, type IntentView, type PaymentStatus } from '../../api/generated';

export const MPESA_POLL_MS = 3_000;
export const REFERENCE_POLL_MS = 30_000;

const finalStatuses: readonly PaymentStatus[] = [
  'SUCCEEDED',
  'FAILED',
  'EXPIRED',
  'CANCELLED',
  'REFUNDED',
];

export function isFinalStatus(status: PaymentStatus): boolean {
  return finalStatuses.includes(status);
}

/** Uma intenção M-Pesa ainda por terminar (o botão de pagar fica desactivado). */
export function isMpesaInProgress(intent: Pick<IntentView, 'method' | 'status'>): boolean {
  return intent.method === 'MPESA' && !isFinalStatus(intent.status);
}

/**
 * Intervalo entre leituras (S5-11 §4): 3 s com M-Pesa por terminar, 30 s com entidade e
 * referência pendente, nenhum num estado final (ou sem dados).
 */
export function pollInterval(
  intent: Pick<IntentView, 'method' | 'status'> | undefined,
): number | false {
  if (!intent || isFinalStatus(intent.status)) return false;
  return intent.method === 'MPESA' ? MPESA_POLL_MS : REFERENCE_POLL_MS;
}

/**
 * Lê a intenção e repete pelo {@link pollInterval}; com o separador em segundo plano não repete
 * (o TanStack Query lê de novo quando o separador volta a estar visível).
 */
export function usePaymentIntentPolling(intentId: string) {
  return useGetPaymentIntent(intentId, {
    query: {
      retry: retryNetworkOnly,
      refetchInterval: (query) => pollInterval(query.state.data),
      refetchIntervalInBackground: false,
      refetchOnWindowFocus: true,
    },
  });
}
