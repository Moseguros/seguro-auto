import { afterEach, describe, expect, it } from 'vitest';
import { getSession, refreshTokenKeys, startSession } from '../auth/session';
import { mockApi } from '../test/mockApi';
import { getMe, getOtpOptions, logoutSession, refreshToken, requestOtp } from './generated';
import { isAuthRequest } from './http';

const me = { id: 'u1', type: 'CUSTOMER', roles: [], permissions: [], preferredLanguage: 'pt' };

function header(headers: unknown, name: string): string | undefined {
  const value = (headers as { get(name: string): unknown }).get(name);
  return typeof value === 'string' ? value : undefined;
}

describe('interceptores de autenticação', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('acrescenta o Bearer nos pedidos à API', async () => {
    startSession({ accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 }, 'CUSTOMER');
    const seen: (string | undefined)[] = [];
    restore = mockApi((config) => {
      seen.push(header(config.headers, 'Authorization'));
      return { status: 200, data: me };
    });
    await getMe();
    expect(seen).toEqual(['Bearer a1']);
  });

  it('nenhum pedido a /api/v1/auth/** leva Authorization', async () => {
    startSession({ accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 }, 'CUSTOMER');
    const seen: Record<string, string | undefined> = {};
    restore = mockApi((config) => {
      seen[config.url ?? ''] = header(config.headers, 'Authorization');
      if (config.url === '/api/v1/auth/refresh') {
        return { status: 200, data: { accessToken: 'a2', refreshToken: 'r2', expiresIn: 900 } };
      }
      return { status: 200, data: {} };
    });
    await getOtpOptions();
    await requestOtp({ identifierType: 'MSISDN', identifier: '+258841234567' });
    await refreshToken({ refreshToken: 'r1' });
    await logoutSession({ refreshToken: 'r2' });
    expect(Object.keys(seen)).toHaveLength(4);
    for (const value of Object.values(seen)) expect(value).toBeUndefined();
  });

  it('reconhece os caminhos de /api/v1/auth', () => {
    expect(isAuthRequest('/api/v1/auth/refresh')).toBe(true);
    expect(isAuthRequest('http://api.local/api/v1/auth/otp/request')).toBe(true);
    expect(isAuthRequest('/api/v1/auth')).toBe(true);
    expect(isAuthRequest('/api/v1/me')).toBe(false);
    expect(isAuthRequest('/api/v1/authors')).toBe(false);
  });

  it('três 401 ao mesmo tempo fazem uma única renovação e repetem os pedidos', async () => {
    startSession({ accessToken: 'old', refreshToken: 'r1', expiresIn: 900 }, 'CUSTOMER');
    let refreshes = 0;
    restore = mockApi((config) => {
      if (config.url === '/api/v1/auth/refresh') {
        refreshes += 1;
        return { status: 200, data: { accessToken: 'new', refreshToken: 'r2', expiresIn: 900 } };
      }
      return header(config.headers, 'Authorization') === 'Bearer new'
        ? { status: 200, data: me }
        : { status: 401, data: { code: 'UNAUTHORIZED', status: 401 } };
    });
    const results = await Promise.all([getMe(), getMe(), getMe()]);
    expect(results).toEqual([me, me, me]);
    expect(refreshes).toBe(1);
    expect(getSession()?.accessToken).toBe('new');
    expect(localStorage.getItem(refreshTokenKeys.CUSTOMER)).toBe('r2');
  });

  it('uma renovação falhada limpa a sessão', async () => {
    startSession({ accessToken: 'old', refreshToken: 'r1', expiresIn: 900 }, 'CUSTOMER');
    restore = mockApi((config) =>
      config.url === '/api/v1/auth/refresh'
        ? { status: 401, data: { code: 'REFRESH_TOKEN_INVALID', status: 401 } }
        : { status: 401, data: { code: 'UNAUTHORIZED', status: 401 } },
    );
    await expect(getMe()).rejects.toMatchObject({ response: { status: 401 } });
    expect(getSession()).toBeNull();
    expect(localStorage.getItem(refreshTokenKeys.CUSTOMER)).toBeNull();
  });

  it('sem sessão, um 401 não tenta renovar', async () => {
    let refreshes = 0;
    restore = mockApi((config) => {
      if (config.url === '/api/v1/auth/refresh') refreshes += 1;
      return { status: 401, data: { code: 'UNAUTHORIZED', status: 401 } };
    });
    await expect(getMe()).rejects.toBeTruthy();
    expect(refreshes).toBe(0);
  });

  it('acrescenta Accept-Language com a língua actual', async () => {
    const seen: (string | undefined)[] = [];
    restore = mockApi((config) => {
      seen.push(header(config.headers, 'Accept-Language'));
      return { status: 200, data: {} };
    });
    await getOtpOptions();
    expect(seen).toEqual(['pt-MZ']);
  });
});

describe('sessão', () => {
  it('o refresh token do staff fica no sessionStorage e substitui o do cliente', () => {
    startSession({ accessToken: 'a', refreshToken: 'rc', expiresIn: 900 }, 'CUSTOMER');
    expect(localStorage.getItem(refreshTokenKeys.CUSTOMER)).toBe('rc');
    startSession({ accessToken: 'b', refreshToken: 'rs', expiresIn: 900 }, 'STAFF');
    expect(sessionStorage.getItem(refreshTokenKeys.STAFF)).toBe('rs');
    expect(localStorage.getItem(refreshTokenKeys.CUSTOMER)).toBeNull();
    expect(getSession()).toMatchObject({ type: 'STAFF', accessToken: 'b' });
  });
});
