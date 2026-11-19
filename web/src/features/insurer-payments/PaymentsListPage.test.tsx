import { cleanup, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { httpClient } from '../../api/http';
import { financeMe } from '../../test/insurerPayments';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { exportFileName } from './PaymentsListPage';
import { readPaymentFilters } from './PaymentFilters';

const slow = { timeout: 15000 };

function fullUrl(c: InternalAxiosRequestConfig): string {
  return httpClient.getUri({ url: c.url, params: c.params, paramsSerializer: c.paramsSerializer });
}

const payment = {
  id: 'pi1',
  reference: 'PG-2027-000010',
  targetLabel: 'PR-2027-000123',
  method: 'MPESA',
  amount: { amount: 299900, currency: 'MZN' },
  status: 'FAILED',
  failureCode: 'INSUFFICIENT_BALANCE',
  createdAt: '2027-01-10T08:30:00Z',
};

describe('separador "Pagamentos"', { timeout: 45_000 }, () => {
  let restore: () => void = () => {};
  beforeEach(() => {
    URL.createObjectURL = vi.fn(() => 'blob:x');
    URL.revokeObjectURL = vi.fn();
    // O jsdom não navega: o clique na ligação de descarga fica sem efeito.
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  });
  afterEach(() => {
    cleanup();
    restore();
    Reflect.deleteProperty(URL, 'createObjectURL');
    Reflect.deleteProperty(URL, 'revokeObjectURL');
    vi.restoreAllMocks();
  });

  it('por defeito pede os últimos 30 dias', () => {
    const filters = readPaymentFilters(new URLSearchParams(), new Date('2027-01-31T12:00:00Z'));
    expect(filters.from).toBe('2027-01-01');
    expect(filters.to).toBe('2027-01-31');
    expect(readPaymentFilters(new URLSearchParams('from=&to=')).from).toBe('');
    expect(exportFileName({ from: '2027-01-01', to: '2027-01-31' })).toBe(
      'pagamentos-2027-01-01-2027-01-31.csv',
    );
  });

  it('lista com os filtros da query string, abre o detalhe e exporta o CSV', async () => {
    const lists: string[] = [];
    const exports: string[] = [];
    restore = signInStaff(financeMe, (c) => {
      if (is(c, 'GET', '/api/v1/insurer/payments')) {
        lists.push(fullUrl(c));
        return { status: 200, data: { content: [payment], page: 0, size: 20, totalElements: 1 } };
      }
      if (is(c, 'GET', '/api/v1/insurer/payments/export')) {
        exports.push(fullUrl(c));
        if (exports.length === 1) {
          return { status: 400, data: { code: 'EXPORT_TOO_LARGE' } };
        }
        return { status: 200, data: new Blob(['a;b']) };
      }
      if (is(c, 'GET', '/api/v1/insurer/payments/pi1')) {
        return {
          status: 200,
          data: {
            ...payment,
            purpose: 'PROPOSAL',
            expiresAt: '2027-01-10T09:00:00Z',
            events: [],
            exceptionIds: [],
            target: { id: 'p1', label: 'PR-2027-000123', type: 'PROPOSAL' },
          },
        };
      }
      if (is(c, 'GET', '/api/v1/insurer/payment-exceptions')) {
        return { status: 200, data: { content: [], page: 0, size: 1, totalElements: 0 } };
      }
      return null;
    });
    const { router } = renderRoute(
      '/seguradora/pagamentos?status=FAILED&status=EXPIRED&method=MPESA&from=2027-01-01&to=2027-01-31',
    );
    expect(await screen.findByText('PG-2027-000010', undefined, slow)).toBeInTheDocument();
    expect(screen.getByText('Saldo insuficiente.')).toBeInTheDocument();
    expect(lists[0]).toContain('status=FAILED&status=EXPIRED');
    expect(lists[0]).toContain('method=MPESA');
    expect(lists[0]).toContain('from=2027-01-01');
    expect(lists[0]).toContain('sort=createdAt,desc');

    await userEvent.click(screen.getByRole('button', { name: 'Exportar CSV' }));
    expect(
      await screen.findByText('Demasiadas linhas (máximo 10 000). Reduza o período.'),
    ).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Exportar CSV' }));
    await waitFor(() => expect(URL.createObjectURL).toHaveBeenCalled());
    expect(exports[1]).toContain('status=FAILED&status=EXPIRED');
    expect(exports[1]).not.toContain('page=');

    await userEvent.click(screen.getByText('PG-2027-000010'));
    await waitFor(() => expect(router.state.location.pathname).toBe('/seguradora/pagamentos/pi1'));
    expect(
      await screen.findByRole('heading', { level: 1, name: 'PG-2027-000010' }, slow),
    ).toBeInTheDocument();
  });

  it('utilizador de seguradora em /admin/operacoes/pagamentos vai para /sem-acesso', async () => {
    restore = signInStaff(financeMe);
    const { router } = renderRoute('/admin/operacoes/pagamentos');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'), slow);
  });

  it('sem payment:read /seguradora/pagamentos vai para /sem-acesso', async () => {
    restore = signInStaff(
      staffMe({ roles: ['UNDERWRITER'], permissions: ['proposal:read'], orgType: 'INSURER' }),
    );
    const { router } = renderRoute('/seguradora/pagamentos');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'), slow);
  });
});
