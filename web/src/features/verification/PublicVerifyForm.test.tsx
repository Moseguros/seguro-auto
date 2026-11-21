import { act, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { PublicVerifyForm } from './PublicVerifyForm';
import type { TurnstileApi } from './TurnstileWidget';

const valid = {
  status: 'VALID',
  checkedAt: '2027-02-10T08:31:00Z',
  plateMasked: 'ABC-***-MC',
  insurerName: 'Seguradora Demo',
  validFrom: '2027-02-10T08:31:00Z',
  validTo: '2028-02-09T21:59:00Z',
  coverageType: 'MTPL_ONLY',
};

type Callbacks = { callback?: (token: string) => void };

/** `window.turnstile` falso: guarda o `callback` para o teste "resolver" o desafio. */
function fakeTurnstile() {
  const state: { options?: Callbacks } = {};
  const api: TurnstileApi = {
    render: vi.fn((_el: HTMLElement, options: Callbacks) => {
      state.options = options;
      return 'w1';
    }),
    reset: vi.fn(),
    remove: vi.fn(),
  };
  window.turnstile = api;
  return {
    api,
    solve(token: string) {
      act(() => state.options?.callback?.(token));
    },
  };
}

const turnstileConfig = { captchaProvider: 'TURNSTILE' as const, captchaSiteKey: '1x00000000' };

describe('PublicVerifyForm', { timeout: 30_000 }, () => {
  let restore: () => void = () => {};
  let requests: InternalAxiosRequestConfig[] = [];
  beforeEach(() => {
    requests = [];
  });
  afterEach(() => {
    restore();
    delete window.turnstile;
  });

  it('com TURNSTILE o botão fica desactivado até haver token e envia X-Captcha-Token', async () => {
    const turnstile = fakeTurnstile();
    restore = mockApi((config) => {
      requests.push(config);
      return { status: 200, data: valid };
    });
    renderWithProviders(<PublicVerifyForm config={turnstileConfig} />);
    const button = screen.getByRole('button', { name: 'Verificar' });
    await userEvent.type(screen.getByLabelText('Matrícula'), 'abc-123-mc');
    expect(screen.getByLabelText('Matrícula')).toHaveValue('ABC-123-MC');
    expect(button).toBeDisabled();
    await waitFor(() => expect(turnstile.api.render).toHaveBeenCalled());
    expect(vi.mocked(turnstile.api.render).mock.calls[0]?.[1]).toMatchObject({
      sitekey: '1x00000000',
      language: 'pt',
      theme: 'auto',
    });
    turnstile.solve('tok-1');
    expect(button).toBeEnabled();
    await userEvent.click(button);
    expect(await screen.findByRole('heading', { name: 'Seguro válido' })).toBeInTheDocument();
    expect(requests[0]?.url).toBe('/api/v1/public/verifications');
    expect(requests[0]?.params).toEqual({ plate: 'ABC-123-MC' });
    expect(requests[0]?.headers.get('X-Captcha-Token')).toBe('tok-1');
    // O token só serve uma vez.
    expect(turnstile.api.reset).toHaveBeenCalledWith('w1');
  });

  it('com NONE o botão está activo e não há CAPTCHA', async () => {
    restore = mockApi((config) => {
      requests.push(config);
      return { status: 200, data: { status: 'NOT_FOUND', checkedAt: valid.checkedAt } };
    });
    renderWithProviders(<PublicVerifyForm config={{ captchaProvider: 'NONE' }} />);
    expect(screen.queryByTestId('turnstile')).not.toBeInTheDocument();
    const button = screen.getByRole('button', { name: 'Verificar' });
    expect(button).toBeEnabled();
    await userEvent.type(screen.getByLabelText('Matrícula'), 'XYZ 999 MP');
    await userEvent.click(button);
    expect(
      await screen.findByRole('heading', { name: 'Sem seguro registado na MoSeguros' }),
    ).toBeInTheDocument();
    expect(requests[0]?.headers.get('X-Captcha-Token')).toBeFalsy();
    await userEvent.click(screen.getByRole('button', { name: 'Nova verificação' }));
    expect(screen.getByLabelText('Matrícula')).toHaveValue('');
  });

  it('valida a matrícula localmente', async () => {
    restore = mockApi((config) => {
      requests.push(config);
      return { status: 200, data: valid };
    });
    renderWithProviders(<PublicVerifyForm config={{ captchaProvider: 'NONE' }} />);
    await userEvent.type(screen.getByLabelText('Matrícula'), 'AB$');
    await userEvent.click(screen.getByRole('button', { name: 'Verificar' }));
    expect(await screen.findByText('Matrícula inválida.')).toBeInTheDocument();
    expect(requests).toHaveLength(0);
  });

  it('CAPTCHA_INVALID mostra a mensagem e repõe o widget', async () => {
    const turnstile = fakeTurnstile();
    restore = mockApi(() => ({ status: 400, data: { code: 'CAPTCHA_INVALID' } }));
    renderWithProviders(<PublicVerifyForm config={turnstileConfig} />);
    await userEvent.type(screen.getByLabelText('Matrícula'), 'ABC-123-MC');
    await waitFor(() => expect(turnstile.api.render).toHaveBeenCalled());
    turnstile.solve('tok-1');
    await userEvent.click(screen.getByRole('button', { name: 'Verificar' }));
    expect(
      await screen.findByText('A confirmação expirou ou falhou. Tente de novo.'),
    ).toBeInTheDocument();
    expect(turnstile.api.reset).toHaveBeenCalledWith('w1');
    // Sem token novo o botão volta a ficar desactivado.
    expect(screen.getByRole('button', { name: 'Verificar' })).toBeDisabled();
  });

  it('INVALID_PLATE do servidor aparece no campo', async () => {
    restore = mockApi(() => ({
      status: 400,
      data: { code: 'VALIDATION_ERROR', errors: [{ field: 'plate', code: 'INVALID_PLATE' }] },
    }));
    renderWithProviders(<PublicVerifyForm config={{ captchaProvider: 'NONE' }} />);
    await userEvent.type(screen.getByLabelText('Matrícula'), 'ZZZZ-0000');
    await userEvent.click(screen.getByRole('button', { name: 'Verificar' }));
    expect(await screen.findByText('Matrícula inválida.')).toBeInTheDocument();
  });

  it('RATE_LIMITED mostra a contagem e reactiva o botão no fim', async () => {
    restore = mockApi(() => ({
      status: 429,
      data: { code: 'RATE_LIMITED', retryAfterSeconds: 2 },
    }));
    renderWithProviders(<PublicVerifyForm config={{ captchaProvider: 'NONE' }} />);
    await userEvent.type(screen.getByLabelText('Matrícula'), 'ABC-123-MC');
    await userEvent.click(screen.getByRole('button', { name: 'Verificar' }));
    expect(
      await screen.findByText('Demasiados pedidos. Tente de novo dentro de 2 segundos.'),
    ).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Verificar' })).toBeDisabled();
    expect(
      await screen.findByText('Demasiados pedidos. Tente de novo dentro de 1 segundos.'),
    ).toBeInTheDocument();
    await waitFor(() => expect(screen.getByRole('button', { name: 'Verificar' })).toBeEnabled(), {
      timeout: 4000,
    });
    expect(screen.queryByText(/Demasiados pedidos/)).not.toBeInTheDocument();
  });
});
