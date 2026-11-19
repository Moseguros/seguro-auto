import { useCallback } from 'react';

/** Chave do `sessionStorage` com a chave de idempotência do pagamento de uma proposta. */
export function idempotencyStorageKey(proposalId: string): string {
  return `moseguros.pay.${proposalId}`;
}

interface StoredKey {
  /** O conjunto `(método, número)` a que a chave pertence. */
  signature: string;
  key: string;
}

/** Fora do `sessionStorage` (modo privado ou bloqueado) a chave vive só em memória. */
const memory = new Map<string, StoredKey>();

function read(storageKey: string): StoredKey | undefined {
  try {
    const raw = sessionStorage.getItem(storageKey);
    if (raw) {
      const parsed = JSON.parse(raw) as Partial<StoredKey>;
      if (typeof parsed.signature === 'string' && typeof parsed.key === 'string') {
        return { signature: parsed.signature, key: parsed.key };
      }
    }
    return undefined;
  } catch {
    return memory.get(storageKey);
  }
}

function write(storageKey: string, value: StoredKey | undefined): void {
  if (value) memory.set(storageKey, value);
  else memory.delete(storageKey);
  try {
    if (value) sessionStorage.setItem(storageKey, JSON.stringify(value));
    else sessionStorage.removeItem(storageKey);
  } catch {
    // Sem armazenamento: fica a cópia em memória.
  }
}

function newKey(): string {
  return crypto.randomUUID();
}

/** O conjunto de dados que identifica um pedido de pagamento. */
export function paymentSignature(method: string, msisdn: string | undefined): string {
  return `${method}|${msisdn ?? ''}`;
}

/**
 * Chave de idempotência do pagamento de uma proposta (S5-11 §3): a mesma enquanto o conjunto
 * `(método, número)` não mudar (um pedido que falhou por rede ou *timeout* repete-se com ela);
 * nova quando o conjunto muda ou depois de uma resposta 2xx ({@link reset}). Fica no
 * `sessionStorage` (`moseguros.pay.<proposalId>`) para sobreviver a um recarregar da página.
 */
export function useIdempotencyKey(proposalId: string) {
  const storageKey = idempotencyStorageKey(proposalId);

  const keyFor = useCallback(
    (signature: string): string => {
      const stored = read(storageKey);
      if (stored && stored.signature === signature) return stored.key;
      const key = newKey();
      write(storageKey, { signature, key });
      return key;
    },
    [storageKey],
  );

  const reset = useCallback(() => write(storageKey, undefined), [storageKey]);

  return { keyFor, reset };
}
