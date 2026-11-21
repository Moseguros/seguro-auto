import { useEffect, useRef, useState } from 'react';
import { useGetMyPolicy, type PolicyDetailResponse } from '../../api/generated';

export const DOCUMENTS_POLL_MS = 5_000;
export const DOCUMENTS_POLL_LIMIT_MS = 120_000;

/** Há documentos ainda por gerar (`PENDING`). */
export function hasPendingDocuments(policy: Pick<PolicyDetailResponse, 'documents'> | undefined) {
  return !!policy?.documents.some((d) => d.status === 'PENDING');
}

/**
 * Lê a apólice do cliente e, enquanto houver documentos `PENDING`, lê-a de novo a cada 5 s, até
 * 2 minutos depois de abrir a página; depois `timedOut` fica `true` (a página mostra o aviso).
 */
export function usePolicyDocumentsPolling(id: string) {
  const startedAt = useRef(Date.now());
  const [timedOut, setTimedOut] = useState(false);
  const query = useGetMyPolicy(id, {
    query: {
      retry: false,
      refetchInterval: (q) =>
        hasPendingDocuments(q.state.data) &&
        Date.now() - startedAt.current < DOCUMENTS_POLL_LIMIT_MS
          ? DOCUMENTS_POLL_MS
          : false,
      refetchIntervalInBackground: false,
    },
  });
  const pending = hasPendingDocuments(query.data);

  useEffect(() => {
    if (!pending) return;
    const remaining = DOCUMENTS_POLL_LIMIT_MS - (Date.now() - startedAt.current);
    if (remaining <= 0) {
      setTimedOut(true);
      return;
    }
    const timer = setTimeout(() => setTimedOut(true), remaining);
    return () => clearTimeout(timer);
  }, [pending]);

  return { query, documentsTimedOut: pending && timedOut };
}
