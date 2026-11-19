import type { InternalAxiosRequestConfig } from 'axios';
import type { MeOrganizationType, MeResponse } from '../api/generated';
import { startSession } from '../auth/session';
import { mockApi } from './mockApi';

/** `me` de um utilizador de staff para os testes. */
export function staffMe(
  overrides: Partial<MeResponse> & { orgType?: MeOrganizationType } = {},
): MeResponse {
  const { orgType, ...rest } = overrides;
  return {
    id: 'u1',
    type: 'STAFF',
    roles: ['PLATFORM_ADMIN'],
    permissions: [],
    preferredLanguage: 'pt',
    emailVerified: true,
    msisdnVerified: false,
    displayName: 'Rita Admin',
    email: 'rita@moseguros.co.mz',
    organization: orgType ? { id: 'o1', displayName: 'Org', type: orgType } : undefined,
    ...rest,
  };
}

export type MockResult = { status: number; data?: unknown };
/** `'network-error'` simula um pedido sem resposta do servidor. */
export type StaffHandler = (
  config: InternalAxiosRequestConfig,
) => MockResult | 'network-error' | null | undefined;

/** Pedido `METHOD url` (sem os parâmetros de pesquisa). */
export function is(config: InternalAxiosRequestConfig, method: string, url: string): boolean {
  return config.method?.toUpperCase() === method && config.url === url;
}

/**
 * Abre uma sessão de staff e simula a API: `GET /me` devolve `me`, o resto vai aos `handlers`
 * (o primeiro que responder) ou dá 404.
 */
export function signInStaff(me: MeResponse, ...handlers: StaffHandler[]): () => void {
  startSession({ accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 }, 'STAFF');
  return mockApi((config) => {
    if (is(config, 'GET', '/api/v1/me')) return { status: 200, data: me };
    for (const handler of handlers) {
      const result = handler(config);
      if (result) return result;
    }
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
}
