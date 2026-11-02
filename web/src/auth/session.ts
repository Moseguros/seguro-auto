import { isAxiosError } from 'axios';
import { refreshToken as refreshTokenRequest } from '../api/generated';
import { configureAuth } from '../api/http';

export type SessionType = 'CUSTOMER' | 'STAFF';

/** Uma sessão de cada vez. O token de acesso fica só em memória. */
export interface Session {
  type: SessionType;
  accessToken: string;
  refreshToken: string;
  /** Fim da validade do token de acesso (ms desde a época). */
  expiresAt: number;
}

export interface Tokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

/** Porque terminou a última sessão: pelo utilizador ou por falha da renovação. */
export type SessionEndReason = 'logout' | 'expired';

/**
 * O refresh token do cliente fica no `localStorage` (sobrevive ao fecho do browser); o do staff
 * no `sessionStorage`, para o staff perder a sessão ao fechar o separador.
 */
export const refreshTokenKeys: Record<SessionType, string> = {
  CUSTOMER: 'moseguros.customer.rt',
  STAFF: 'moseguros.staff.rt',
};

function storageFor(type: SessionType): Storage | undefined {
  try {
    return type === 'CUSTOMER' ? globalThis.localStorage : globalThis.sessionStorage;
  } catch {
    return undefined;
  }
}

function writeStored(type: SessionType, value: string | null): void {
  try {
    const storage = storageFor(type);
    if (value === null) storage?.removeItem(refreshTokenKeys[type]);
    else storage?.setItem(refreshTokenKeys[type], value);
  } catch {
    // Sem armazenamento (modo privado): a sessão vale só enquanto a página estiver aberta.
  }
}

/** Refresh token guardado de uma sessão anterior (o do cliente tem prioridade). */
export function storedRefreshToken(): { type: SessionType; refreshToken: string } | null {
  for (const type of ['CUSTOMER', 'STAFF'] as const) {
    try {
      const value = storageFor(type)?.getItem(refreshTokenKeys[type]);
      if (value) return { type, refreshToken: value };
    } catch {
      // ignora
    }
  }
  return null;
}

let current: Session | null = null;
let lastEndReason: SessionEndReason | null = null;
let renewing: Promise<string> | null = null;
const listeners = new Set<() => void>();
const endListeners = new Set<(reason: SessionEndReason, type: SessionType) => void>();

function emit(): void {
  listeners.forEach((listener) => listener());
}

export function getSession(): Session | null {
  return current;
}

export function getLastEndReason(): SessionEndReason | null {
  return lastEndReason;
}

export function subscribeSession(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

/** Avisado quando a sessão termina (logout ou renovação falhada). */
export function onSessionEnded(
  listener: (reason: SessionEndReason, type: SessionType) => void,
): () => void {
  endListeners.add(listener);
  return () => endListeners.delete(listener);
}

export function sessionFromTokens(tokens: Tokens, type: SessionType, now = Date.now()): Session {
  return {
    type,
    accessToken: tokens.accessToken,
    refreshToken: tokens.refreshToken,
    expiresAt: now + tokens.expiresIn * 1000,
  };
}

/** Abre (ou substitui) a sessão e guarda o refresh token no armazenamento do seu tipo. */
export function startSession(tokens: Tokens, type: SessionType): Session {
  current = sessionFromTokens(tokens, type);
  lastEndReason = null;
  writeStored(type, tokens.refreshToken);
  writeStored(type === 'CUSTOMER' ? 'STAFF' : 'CUSTOMER', null);
  emit();
  return current;
}

/** Termina a sessão: limpa a memória e os dois armazenamentos e avisa quem escuta. */
export function endSession(reason: SessionEndReason): void {
  const type = current?.type ?? storedRefreshToken()?.type ?? 'CUSTOMER';
  current = null;
  lastEndReason = reason;
  writeStored('CUSTOMER', null);
  writeStored('STAFF', null);
  emit();
  endListeners.forEach((listener) => listener(reason, type));
}

/**
 * Troca o refresh token (da sessão activa ou guardado) por um par novo. Chamadas simultâneas
 * partilham a mesma renovação. Se o servidor recusar, a sessão termina (`expired`); sem resposta
 * (rede), a sessão em memória mantém-se e o refresh token guardado também.
 */
export function renewSession(): Promise<string> {
  if (renewing) return renewing;
  const source = current
    ? { type: current.type, refreshToken: current.refreshToken }
    : storedRefreshToken();
  if (!source) return Promise.reject(new Error('No session to renew'));
  renewing = (async () => {
    try {
      const tokens = await refreshTokenRequest({ refreshToken: source.refreshToken });
      return startSession(tokens, source.type).accessToken;
    } catch (error) {
      if (!isAxiosError(error) || error.response) endSession('expired');
      throw error;
    }
  })().finally(() => {
    renewing = null;
  });
  return renewing;
}

/** Só para testes: volta ao estado inicial sem avisar ninguém. */
export function resetSessionForTests(): void {
  current = null;
  lastEndReason = null;
  renewing = null;
  writeStored('CUSTOMER', null);
  writeStored('STAFF', null);
  emit();
}

configureAuth({
  accessToken: () => current?.accessToken,
  renew: renewSession,
});
