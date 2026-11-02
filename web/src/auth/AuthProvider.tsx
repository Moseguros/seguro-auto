import { useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createContext,
  useCallback,
  useEffect,
  useMemo,
  useState,
  useSyncExternalStore,
  type ReactNode,
} from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { getMe, logoutSession, type MeResponse } from '../api/generated';
import {
  endSession,
  getSession,
  onSessionEnded,
  renewSession,
  startSession,
  storedRefreshToken,
  subscribeSession,
  type Session,
  type SessionType,
  type Tokens,
} from './session';

export type AuthStatus = 'loading' | 'anonymous' | 'authenticated';

export interface AuthContextValue {
  session: Session | null;
  /** Resultado de `getMe` (chave `['me']` no TanStack Query). */
  me: MeResponse | undefined;
  status: AuthStatus;
  /** Abre a sessão com os tokens recebidos e devolve o utilizador autenticado. */
  login(tokens: Tokens, type: SessionType): Promise<MeResponse>;
  /** Termina a sessão e vai para a página de entrada do tipo da sessão. */
  logout(): Promise<void>;
}

export const meQueryKey = ['me'] as const;

export const AuthContext = createContext<AuthContextValue | null>(null);

export function loginPathFor(type: SessionType): string {
  return type === 'STAFF' ? '/entrar' : '/cliente/entrar';
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const session = useSyncExternalStore(subscribeSession, getSession, getSession);
  // Ao arrancar com um refresh token guardado, obtém-se um token de acesso antes de decidir.
  const [booting, setBooting] = useState(() => !getSession() && storedRefreshToken() !== null);

  useEffect(() => {
    if (!booting) return;
    let active = true;
    renewSession()
      .catch(() => undefined)
      .finally(() => {
        if (active) setBooting(false);
      });
    return () => {
      active = false;
    };
  }, [booting]);

  const meQuery = useQuery({
    queryKey: meQueryKey,
    queryFn: ({ signal }) => getMe(undefined, signal),
    enabled: session !== null,
    staleTime: 60_000,
  });

  // Renovação falhada (nos interceptores): limpa a cache e volta à página de entrada.
  useEffect(
    () =>
      onSessionEnded((reason, type) => {
        if (reason !== 'expired') return;
        queryClient.clear();
        navigate(loginPathFor(type), { replace: true });
      }),
    [navigate, queryClient],
  );

  const login = useCallback(
    async (tokens: Tokens, type: SessionType) => {
      queryClient.removeQueries({ queryKey: meQueryKey });
      startSession(tokens, type);
      return queryClient.fetchQuery({ queryKey: meQueryKey, queryFn: () => getMe() });
    },
    [queryClient],
  );

  const logout = useCallback(async () => {
    const current = getSession();
    const stored = storedRefreshToken();
    const type = current?.type ?? stored?.type ?? 'CUSTOMER';
    const refreshToken = current?.refreshToken ?? stored?.refreshToken;
    const revoke = refreshToken
      ? logoutSession({ refreshToken }).catch(() => undefined)
      : Promise.resolve();
    endSession('logout');
    queryClient.clear();
    navigate(loginPathFor(type), { replace: true });
    await revoke;
  }, [navigate, queryClient]);

  let status: AuthStatus;
  if (booting || (session !== null && meQuery.isPending)) status = 'loading';
  else if (session !== null) status = 'authenticated';
  else status = 'anonymous';

  const value = useMemo<AuthContextValue>(
    () => ({ session, me: meQuery.data, status, login, logout }),
    [session, meQuery.data, status, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/** Elemento da rota raiz: a sessão precisa do router para navegar no logout. */
export function AuthRoot() {
  return (
    <AuthProvider>
      <Outlet />
    </AuthProvider>
  );
}
