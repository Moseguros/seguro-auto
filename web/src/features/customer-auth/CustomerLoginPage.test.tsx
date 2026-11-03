import { act, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it } from 'vitest';
import type { MeResponse } from '../../api/generated';
import { getSession, refreshTokenKeys, startSession } from '../../auth/session';
import { mockApi } from '../../test/mockApi';
import { renderRoute } from '../../test/render';
import { afterLoginPath, safeReturnPath } from './CustomerLoginPage';

const options = {
  identifierTypes: ['MSISDN', 'EMAIL'],
  channels: { MSISDN: ['SMS'], EMAIL: ['EMAIL'] },
  defaultChannels: { MSISDN: 'SMS', EMAIL: 'EMAIL' },
  customerCanChoose: false,
  codeLength: 6,
  resendAfterSeconds: 60,
};

function customer(overrides: Partial<MeResponse> = {}): MeResponse {
  return {
    id: 'u1',
    type: 'CUSTOMER',
    roles: ['CUSTOMER'],
    permissions: [],
    preferredLanguage: 'pt',
    emailVerified: false,
    msisdn: '+258841234567',
    msisdnVerified: true,
    profileComplete: true,
    displayName: 'Ana Maria Mussa',
    ...overrides,
  };
}

const tokens = { accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 };

type Handler = (config: InternalAxiosRequestConfig) => { status: number; data?: unknown } | null;

function api(...handlers: Handler[]) {
  return mockApi((config) => {
    for (const handler of handlers) {
      const result = handler(config);
      if (result) return result;
    }
    if (config.url?.startsWith('/api/v1/auth/otp/challenges/')) {
      return { status: 200, data: { channel: 'SMS', deliveryStatus: 'SENT', fallbackUsed: false } };
    }
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
}

const route =
  (method: string, url: string, status: number, data?: unknown): Handler =>
  (config) =>
    config.method?.toUpperCase() === method && config.url === url ? { status, data } : null;

describe('destino depois de entrar', () => {
  it('perfil incompleto ou conta nova → completar o perfil', () => {
    expect(afterLoginPath(true, true, '/cliente/apolices')).toBe('/cliente/perfil?completar=1');
    expect(afterLoginPath(false, false, null)).toBe('/cliente/perfil?completar=1');
  });

  it('senão volta ao pedido ou ao início, só para caminhos internos', () => {
    expect(afterLoginPath(false, true, '/cliente/apolices')).toBe('/cliente/apolices');
    expect(afterLoginPath(false, true, null)).toBe('/cliente');
    expect(safeReturnPath('//evil.example')).toBeNull();
    expect(safeReturnPath('https://evil.example')).toBeNull();
  });
});

describe('área do cliente', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('/cliente/perfil sem sessão vai para a entrada com voltar', async () => {
    restore = api(route('GET', '/api/v1/auth/otp/options', 200, options));
    const { router } = renderRoute('/cliente/perfil');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/entrar'));
    expect(router.state.location.search).toBe('?voltar=%2Fcliente%2Fperfil');
    expect(
      await screen.findByRole('heading', { name: 'Entrar na área do cliente' }),
    ).toBeInTheDocument();
  });

  it('entra com o código e volta à página pedida', async () => {
    restore = api(
      route('GET', '/api/v1/auth/otp/options', 200, options),
      route('POST', '/api/v1/auth/otp/request', 202, {
        challengeId: 'c1',
        channel: 'SMS',
        expiresAt: '2026-11-02T10:00:00Z',
        fallbackUsed: false,
        resendAfterSeconds: 60,
      }),
      route('POST', '/api/v1/auth/otp/verify', 200, { ...tokens, isNewUser: false }),
      route('GET', '/api/v1/me', 200, customer()),
    );
    const { router } = renderRoute('/cliente/entrar?voltar=%2Fcliente%2Fapolices');
    await userEvent.type(await screen.findByLabelText('Número de telemóvel'), '841234567');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    await userEvent.click(await screen.findByLabelText('Dígito 1 de 6'));
    await userEvent.paste('123456');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/apolices'));
    expect(getSession()).toMatchObject({ type: 'CUSTOMER', accessToken: 'a1' });
    expect(localStorage.getItem(refreshTokenKeys.CUSTOMER)).toBe('r1');
  });

  it('conta nova vai completar o perfil', async () => {
    restore = api(
      route('GET', '/api/v1/auth/otp/options', 200, options),
      route('POST', '/api/v1/auth/otp/request', 202, {
        challengeId: 'c1',
        channel: 'SMS',
        expiresAt: '2026-11-02T10:00:00Z',
        fallbackUsed: false,
        resendAfterSeconds: 60,
      }),
      route('POST', '/api/v1/auth/otp/verify', 200, { ...tokens, isNewUser: true }),
      route('GET', '/api/v1/me', 200, customer({ profileComplete: false, displayName: undefined })),
      route('GET', '/api/v1/me/profile', 200, { complete: false, version: 0 }),
      route('GET', '/api/v1/me/contacts', 200, { contacts: [] }),
    );
    const { router } = renderRoute('/cliente/entrar');
    await userEvent.type(await screen.findByLabelText('Número de telemóvel'), '841234567');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    await userEvent.click(await screen.findByLabelText('Dígito 1 de 6'));
    await userEvent.paste('123456');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/perfil'));
    expect(router.state.location.search).toBe('?completar=1');
    expect(
      await screen.findByText('Precisamos destes dados para emitir as suas apólices.'),
    ).toBeInTheDocument();
  });

  it('ao arrancar com um refresh token guardado renova e mostra o início', async () => {
    localStorage.setItem(refreshTokenKeys.CUSTOMER, 'stored');
    let refreshBody: unknown;
    restore = api(
      (config) => {
        if (config.url !== '/api/v1/auth/refresh') return null;
        refreshBody = JSON.parse(config.data as string);
        return { status: 200, data: tokens };
      },
      route('GET', '/api/v1/me', 200, customer({ profileComplete: false })),
    );
    renderRoute('/cliente');
    expect(await screen.findByRole('heading', { name: 'Olá, Ana' })).toBeInTheDocument();
    expect(refreshBody).toEqual({ refreshToken: 'stored' });
    expect(
      screen.getByText('Complete o seu perfil para poder comprar um seguro.'),
    ).toBeInTheDocument();
    const quote = screen.getByRole('link', { name: 'Simular seguro' });
    expect(quote).toHaveAttribute('href', '/cliente/simular');
    expect(quote).toHaveClass('MuiButton-cta');
    expect(screen.getByRole('link', { name: 'Completar perfil' })).toHaveClass(
      'MuiButton-contained',
    );
  });

  it('sessão de staff em /cliente mostra "sem acesso"', async () => {
    startSession(tokens, 'STAFF');
    restore = api(
      route('GET', '/api/v1/me', 200, {
        ...customer(),
        type: 'STAFF',
        roles: ['PLATFORM_ADMIN'],
      }),
    );
    const { router } = renderRoute('/cliente');
    expect(
      await screen.findByRole('heading', { name: 'Não tem acesso a esta área' }),
    ).toBeInTheDocument();
    expect(router.state.location.pathname).toBe('/sem-acesso');
    expect(screen.getByRole('link', { name: 'Ir para a minha área' })).toHaveAttribute(
      'href',
      '/admin',
    );
  });

  it('"Terminar sessão" revoga a sessão, limpa tudo e vai para a entrada', async () => {
    startSession(tokens, 'CUSTOMER');
    let logoutBody: unknown;
    restore = api(
      route('GET', '/api/v1/me', 200, customer()),
      route('GET', '/api/v1/auth/otp/options', 200, options),
      (config) => {
        if (config.url !== '/api/v1/auth/logout') return null;
        logoutBody = JSON.parse(config.data as string);
        return { status: 500, data: { code: 'INTERNAL_ERROR' } };
      },
    );
    const { router } = renderRoute('/cliente');
    await userEvent.click(await screen.findByRole('button', { name: 'Menu da conta' }));
    expect(screen.getByTestId('user-menu-name')).toHaveTextContent('Ana Maria Mussa');
    await act(async () => {
      await userEvent.click(screen.getByRole('menuitem', { name: 'Terminar sessão' }));
    });
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/entrar'));
    expect(router.state.location.search).toBe('');
    expect(logoutBody).toEqual({ refreshToken: 'r1' });
    expect(getSession()).toBeNull();
    expect(localStorage.getItem(refreshTokenKeys.CUSTOMER)).toBeNull();
  });
});
