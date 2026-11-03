import type { MeResponse } from '../api/generated';
import { startSession } from '../auth/session';
import { mockApi } from './mockApi';
import { is, type StaffHandler } from './staff';

/** `me` de um cliente para os testes. */
export function customerMe(overrides: Partial<MeResponse> = {}): MeResponse {
  return {
    id: 'c1',
    type: 'CUSTOMER',
    roles: ['CUSTOMER'],
    permissions: ['self:profile', 'self:vehicles'],
    preferredLanguage: 'pt',
    emailVerified: false,
    msisdnVerified: true,
    msisdn: '+258841234567',
    displayName: 'Ana Maria Mussa',
    profileComplete: true,
    ...overrides,
  };
}

/**
 * Abre uma sessão de cliente e simula a API: `GET /me` devolve `me`, o resto vai aos `handlers`
 * (o primeiro que responder) ou dá 404.
 */
export function signInCustomer(me: MeResponse, ...handlers: StaffHandler[]): () => void {
  startSession({ accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 }, 'CUSTOMER');
  return mockApi((config) => {
    if (is(config, 'GET', '/api/v1/me')) return { status: 200, data: me };
    for (const handler of handlers) {
      const result = handler(config);
      if (result) return result;
    }
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
}
