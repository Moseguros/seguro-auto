import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';

/** O leitor de QR falso: chama o `callback` com o texto de `scan.text` ou falha a câmara. */
const scan = vi.hoisted(() => ({
  texts: [] as string[],
  cameraFails: false,
  stop: () => {},
}));
vi.mock('@zxing/browser', () => ({
  BrowserQRCodeReader: class {
    decodeFromConstraints(
      _constraints: unknown,
      _video: unknown,
      callback: (
        result: { getText(): string } | undefined,
        error: unknown,
        controls: { stop(): void },
      ) => void,
    ) {
      if (scan.cameraFails) return Promise.reject(new Error('NotAllowedError'));
      const controls = { stop: scan.stop };
      setTimeout(() => {
        for (const text of scan.texts) callback({ getText: () => text }, undefined, controls);
      }, 10);
      return Promise.resolve(controls);
    }
  },
}));

const police = staffMe({
  roles: ['POLICE_OFFICER'],
  permissions: ['verification:police'],
  orgType: 'POLICE',
  displayName: 'Agente Silva',
});

const result = {
  status: 'VALID',
  checkedAt: '2027-02-10T08:31:00Z',
  plateMasked: 'ABC-***-MC',
  insurerName: 'Seguradora Demo',
  validFrom: '2027-02-10T08:31:00Z',
  validTo: '2028-02-09T21:59:00Z',
  coverageType: 'MTPL_ONLY',
  policyNumber: 'AP-2027-900001',
  certificateNumber: 'CT-2027-000001',
  vehicleMake: 'Toyota',
  vehicleModel: 'Hilux',
};

const slow = { timeout: 10_000 };

describe('área da polícia', { timeout: 30_000 }, () => {
  let restore: () => void = () => {};
  let requests: InternalAxiosRequestConfig[] = [];
  beforeEach(() => {
    requests = [];
    scan.texts = [];
    scan.cameraFails = false;
    scan.stop = vi.fn();
  });
  afterEach(() => restore());

  const api = (c: InternalAxiosRequestConfig) => {
    requests.push(c);
    if (is(c, 'GET', '/api/v1/police/verifications')) return { status: 200, data: result };
    if (c.url?.startsWith('/api/v1/police/verifications/')) {
      return { status: 200, data: { ...result, certificateStatus: 'CURRENT' } };
    }
    if (is(c, 'GET', '/api/v1/police/check-history')) {
      return {
        status: 200,
        data: {
          content: [
            {
              id: 'c1',
              checkedAt: '2027-02-10T08:31:00Z',
              subjectType: 'PLATE',
              plateMasked: 'ABC-***-MC',
              result: 'VALID',
            },
            {
              id: 'c2',
              checkedAt: '2027-02-09T08:00:00Z',
              subjectType: 'TOKEN',
              result: 'EXPIRED',
            },
          ],
          page: 0,
          size: 20,
          totalElements: 2,
        },
      };
    }
    return null;
  };

  it('vê os três separadores e pesquisa a matrícula sem CAPTCHA', async () => {
    restore = signInStaff(police, api);
    renderRoute('/verificar');
    expect(await screen.findByRole('tab', { name: 'Matrícula' }, slow)).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Ler QR' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Histórico' })).toBeInTheDocument();
    expect(screen.queryByTestId('turnstile')).not.toBeInTheDocument();
    await userEvent.type(screen.getByLabelText('Matrícula'), 'abc-123-mc');
    await userEvent.click(screen.getByRole('button', { name: 'Verificar' }));
    expect(await screen.findByText('AP-2027-900001')).toBeInTheDocument();
    const call = requests.find((c) => c.url === '/api/v1/police/verifications');
    expect(call?.params).toEqual({ plate: 'ABC-123-MC' });
    expect(requests.some((c) => c.url?.startsWith('/api/v1/public/'))).toBe(false);
    // O menu do utilizador está no layout público.
    expect(screen.getByRole('button', { name: 'Menu da conta' })).toBeInTheDocument();
  });

  it('lê o QR: ignora outro texto, pára a câmara e verifica o código', async () => {
    scan.texts = ['olá', 'https://verificar.moseguros.co.mz/v/DEMOvalid0000000000001'];
    restore = signInStaff(police, api);
    renderRoute('/verificar');
    await userEvent.click(await screen.findByRole('tab', { name: 'Ler QR' }, slow));
    expect(await screen.findByText('AP-2027-900001', undefined, slow)).toBeInTheDocument();
    expect(scan.stop).toHaveBeenCalled();
    expect(
      requests.some((c) => c.url === '/api/v1/police/verifications/DEMOvalid0000000000001'),
    ).toBe(true);
  });

  it('sem câmara mostra o aviso e volta ao separador Matrícula', async () => {
    scan.cameraFails = true;
    restore = signInStaff(police, api);
    renderRoute('/verificar');
    await userEvent.click(await screen.findByRole('tab', { name: 'Ler QR' }, slow));
    expect(
      await screen.findByText(
        'Não foi possível usar a câmara. Verifique as permissões do browser ou pesquise pela matrícula.',
        undefined,
        slow,
      ),
    ).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Matrícula' })).toHaveAttribute('aria-selected', 'true');
  });

  it('o histórico lista as consultas dos últimos 30 dias com a data em Maputo', async () => {
    restore = signInStaff(police, api);
    renderRoute('/verificar/historico');
    const list = await screen.findByRole('list', { name: 'Histórico de verificações' }, slow);
    expect(within(list).getByText('10/02/27, 10:31')).toBeInTheDocument();
    expect(within(list).getByText('Matrícula · ABC-***-MC')).toBeInTheDocument();
    expect(within(list).getByText('QR')).toBeInTheDocument();
    expect(within(list).getByText('Seguro expirado')).toBeInTheDocument();
    const call = requests.find((c) => c.url === '/api/v1/police/check-history');
    const from = new Date(`${call?.params.to}T00:00:00Z`);
    from.setUTCDate(from.getUTCDate() - 30);
    expect(call?.params.from).toBe(from.toISOString().slice(0, 10));
  });

  it('o histórico sem verification:police vai para /sem-acesso', async () => {
    restore = signInStaff(staffMe({ permissions: [] }), api);
    const { router } = renderRoute('/verificar/historico');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
  });
});
