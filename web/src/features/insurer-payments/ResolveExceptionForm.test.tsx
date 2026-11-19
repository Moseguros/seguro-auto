import { cleanup, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { PaymentExceptionDetail } from '../../api/generated';
import { startSession } from '../../auth/session';
import {
  candidateFixture,
  exceptionFixture,
  financeMe,
  insurerAdminMe,
} from '../../test/insurerPayments';
import { mockApi } from '../../test/mockApi';
import { renderRoute, renderWithProviders } from '../../test/render';
import { is, signInStaff } from '../../test/staff';
import { ResolveExceptionForm } from './ResolveExceptionForm';

const slow = { timeout: 15000 };

describe('ResolveExceptionForm', { timeout: 30_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => {
    cleanup();
    restore();
  });

  function renderForm(exception: PaymentExceptionDetail, onResolved = vi.fn()) {
    startSession({ accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 }, 'STAFF');
    renderWithProviders(
      <ResolveExceptionForm exception={exception} onResolved={onResolved} onStale={() => {}} />,
    );
    return onResolved;
  }

  it('só mostra as resoluções permitidas', () => {
    restore = mockApi(() => ({ status: 404, data: {} }));
    renderForm(exceptionFixture({ allowedResolutions: ['REFUND', 'IGNORED'] }));
    const radios = screen.getAllByRole('radio');
    expect(radios).toHaveLength(2);
    expect(screen.getByLabelText(/Reembolsar fora da plataforma/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Ignorar/)).toBeInTheDocument();
    expect(screen.queryByLabelText(/Associar a um pagamento/)).toBeNull();
    expect(
      screen.getByText('A seguradora devolve o dinheiro ao cliente pelos seus meios.'),
    ).toBeInTheDocument();
  });

  it('em LINKED exige uma linha e a nota, pede confirmação e envia o intentId', async () => {
    const bodies: unknown[] = [];
    restore = mockApi((c) => {
      if (c.url === '/api/v1/insurer/payment-exceptions/ex1/candidates') {
        return { status: 200, data: { candidates: [candidateFixture()] } };
      }
      if (c.url === '/api/v1/insurer/payment-exceptions/ex1/resolve') {
        bodies.push(JSON.parse(c.data as string));
        return {
          status: 200,
          data: exceptionFixture({ status: 'RESOLVED', allowedResolutions: [] }),
        };
      }
      return { status: 404, data: {} };
    });
    const onResolved = renderForm(exceptionFixture());
    await userEvent.click(screen.getByLabelText(/Associar a um pagamento/));
    expect(await screen.findByText('PG-2027-000010')).toBeInTheDocument();
    expect(screen.getByText('Mesmo montante')).toBeInTheDocument();
    expect(screen.queryByText('Mesma referência')).toBeNull();

    await userEvent.click(screen.getByRole('button', { name: 'Resolver' }));
    expect(screen.getByText('Escolha o pagamento a associar.')).toBeInTheDocument();
    expect(screen.getByLabelText(/Nota/)).toHaveAttribute('aria-invalid', 'true');
    expect(bodies).toHaveLength(0);

    await userEvent.click(screen.getByRole('radio', { name: 'Escolher PG-2027-000010' }));
    await userEvent.type(screen.getByLabelText(/Nota/), 'abc');
    await userEvent.click(screen.getByRole('button', { name: 'Resolver' }));
    expect(screen.getByLabelText(/Nota/)).toHaveAttribute('aria-invalid', 'true');
    expect(bodies).toHaveLength(0);

    await userEvent.type(screen.getByLabelText(/Nota/), ' confirmado no extracto');
    await userEvent.click(screen.getByRole('button', { name: 'Resolver' }));
    const dialog = await screen.findByRole('dialog');
    expect(dialog).toHaveTextContent(
      'Isto confirma o pagamento PG-2027-000010 e a proposta PR-2027-000123 passa a paga. Continuar?',
    );
    await userEvent.click(within(dialog).getByRole('button', { name: 'Continuar' }));
    await waitFor(() => expect(onResolved).toHaveBeenCalled());
    expect(bodies[0]).toEqual({
      resolution: 'LINKED',
      note: 'abc confirmado no extracto',
      version: 0,
      intentId: 'pi1',
    });
  });

  it('LINK_AMOUNT_INSUFFICIENT mostra a mensagem', async () => {
    restore = mockApi((c) => {
      if (c.url === '/api/v1/insurer/payment-exceptions/ex1/candidates') {
        return { status: 200, data: { candidates: [candidateFixture({ matchesAmount: false })] } };
      }
      if (c.url === '/api/v1/insurer/payment-exceptions/ex1/resolve') {
        return { status: 422, data: { code: 'LINK_AMOUNT_INSUFFICIENT' } };
      }
      return { status: 404, data: {} };
    });
    renderForm(exceptionFixture());
    await userEvent.click(screen.getByLabelText(/Associar a um pagamento/));
    await userEvent.click(await screen.findByRole('radio', { name: 'Escolher PG-2027-000010' }));
    await userEvent.type(screen.getByLabelText(/Nota/), 'cliente pagou menos');
    await userEvent.click(screen.getByRole('button', { name: 'Resolver' }));
    await userEvent.click(
      within(await screen.findByRole('dialog')).getByRole('button', { name: 'Continuar' }),
    );
    expect(
      await screen.findByText(
        'O montante recebido é menor do que o do pagamento. Escolha reembolsar ou ignorar.',
      ),
    ).toBeInTheDocument();
  });
});

describe('separador "Excepções"', { timeout: 45_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => {
    cleanup();
    restore();
  });

  const list = {
    content: [
      {
        id: 'ex1',
        type: 'UNKNOWN_REFERENCE',
        status: 'OPEN',
        reference: '00548781108',
        amount: { amount: 299900, currency: 'MZN' },
        createdAt: '2027-01-11T10:00:00Z',
        fileName: 'MEPS20270111.txt',
        lineNo: 4,
      },
    ],
    page: 0,
    size: 20,
    totalElements: 1,
  };

  it('o gestor vê a excepção só em leitura', async () => {
    restore = signInStaff(insurerAdminMe, (c) => {
      if (is(c, 'GET', '/api/v1/insurer/payment-exceptions')) return { status: 200, data: list };
      if (is(c, 'GET', '/api/v1/insurer/payment-exceptions/ex1')) {
        return { status: 200, data: exceptionFixture() };
      }
      return null;
    });
    renderRoute('/seguradora/pagamentos/excepcoes?excepcao=ex1');
    expect(
      await screen.findByText('Só o financeiro pode resolver excepções.', undefined, slow),
    ).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Resolver' })).toBeNull();
    expect(screen.getByText('Referência não emitida pela plataforma')).toBeInTheDocument();
  });

  it('o financeiro resolve com REFUND, a lista e o contador actualizam', async () => {
    let open = true;
    restore = signInStaff(financeMe, (c) => {
      if (is(c, 'GET', '/api/v1/insurer/payment-exceptions')) {
        const params = c.params as { status?: string; size?: number };
        const data = open ? list : { ...list, content: [], totalElements: 0 };
        return { status: 200, data: params.size === 1 ? { ...data, size: 1 } : data };
      }
      if (is(c, 'GET', '/api/v1/insurer/payment-exceptions/ex1')) {
        return { status: 200, data: exceptionFixture() };
      }
      if (is(c, 'POST', '/api/v1/insurer/payment-exceptions/ex1/resolve')) {
        open = false;
        return {
          status: 200,
          data: exceptionFixture({ status: 'RESOLVED', resolution: 'REFUND' }),
        };
      }
      return null;
    });
    const { router } = renderRoute('/seguradora/pagamentos/excepcoes');
    expect(await screen.findByTestId('open-exceptions-count', undefined, slow)).toHaveTextContent(
      '1',
    );
    await userEvent.click(await screen.findByText('Referência desconhecida', undefined, slow));
    await userEvent.click(await screen.findByLabelText(/Reembolsar fora da plataforma/));
    await userEvent.type(
      screen.getByLabelText(/Nota/),
      'Devolvido pela seguradora fora da plataforma',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Resolver' }));
    expect(await screen.findByText('Excepção resolvida')).toBeInTheDocument();
    await waitFor(() => expect(screen.queryByTestId('open-exceptions-count')).toBeNull());
    expect(router.state.location.search).not.toContain('excepcao');
    expect(await screen.findByText('Não há excepções com estes filtros.')).toBeInTheDocument();
  });
});
