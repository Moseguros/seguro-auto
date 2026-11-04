import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import type { QuoteRequestResponse } from '../../api/generated';
import { customerMe, signInCustomer } from '../../test/customer';
import {
  ctaButtons,
  quoteFixture,
  requestFixture,
  secondQuote,
  unavailableQuote,
  money,
} from '../../test/quotes';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';

const third = quoteFixture({
  id: 'q4',
  insurer: { id: 'i4', displayName: 'Seguradora D', shortCode: 'DEMOD', logoFileId: null },
  totalAmount: { amount: 7000000, currency: 'MZN' },
});
const fourth = quoteFixture({
  id: 'q5',
  insurer: { id: 'i5', displayName: 'Seguradora E', shortCode: 'DEMOE', logoFileId: null },
  totalAmount: { amount: 8000000, currency: 'MZN' },
});

function signIn(request: QuoteRequestResponse) {
  return signInCustomer(customerMe(), (config) =>
    is(config, 'GET', `/api/v1/quotes/${request.id}`) ? { status: 200, data: request } : undefined,
  );
}

describe('QuoteResultsPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('cartões pela ordem da API, "Mais barata" só no primeiro e os motivos traduzidos', async () => {
    restore = signIn(requestFixture());
    renderRoute('/cliente/simular/r1');
    const cards = await screen.findAllByTestId('quote-card');
    expect(cards).toHaveLength(2);
    expect(cards[0]).toHaveTextContent('Seguradora Demo');
    expect(cards[0]).toHaveTextContent(money(5226200));
    expect(cards[0]).toHaveTextContent('Mais barata');
    expect(cards[0]).toHaveTextContent(`Inclui ${money(127344)} de encargos`);
    expect(cards[0]).toHaveTextContent(`Franquia 10 % (mín. ${money(2500000)})`);
    expect(cards[1]).toHaveTextContent('Seguradora B');
    expect(cards[1]).not.toHaveTextContent('Mais barata');
    expect(cards[1]).toHaveTextContent(`Franquia ${money(500000)}`);
    expect(screen.getByText('Sem proposta (1)')).toBeInTheDocument();
    expect(
      screen.getByText('Esta seguradora não cobre este veículo ou condutor nestas condições.'),
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        /Esta simulação fica guardada em As minhas cotações e é válida até 25\/01\/27/,
      ),
    ).toBeInTheDocument();
    expect(ctaButtons()).toHaveLength(0);
  });

  it('com uma só proposta não há "Mais barata"', async () => {
    restore = signIn(requestFixture({ quotes: [quoteFixture()] }));
    renderRoute('/cliente/simular/r1');
    const [card] = await screen.findAllByTestId('quote-card');
    expect(card).not.toHaveTextContent('Mais barata');
  });

  it('selecção: barra com um único botão laranja, a quarta é recusada, e vai para a comparação', async () => {
    restore = signIn(
      requestFixture({ quotes: [quoteFixture(), secondQuote, third, fourth, unavailableQuote] }),
    );
    const { router } = renderRoute('/cliente/simular/r1');
    const boxes = await screen.findAllByRole('checkbox', { name: /^Comparar/ });
    expect(boxes).toHaveLength(4);
    await userEvent.click(boxes[0]!);
    expect(screen.getByText('1 seleccionadas (máximo 3)')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Comparar propostas' })).toBeDisabled();
    expect(ctaButtons()).toHaveLength(1);
    await userEvent.click(boxes[1]!);
    await userEvent.click(boxes[2]!);
    await userEvent.click(boxes[3]!);
    expect(boxes[3]).not.toBeChecked();
    expect(await screen.findByText('Pode comparar até 3 propostas')).toBeInTheDocument();
    expect(ctaButtons()).toHaveLength(1);
    await userEvent.click(screen.getByRole('button', { name: 'Comparar propostas' }));
    await waitFor(() =>
      expect(router.state.location.pathname).toBe('/cliente/simular/r1/comparar'),
    );
    expect(router.state.location.search).toBe('?ids=q1,q2,q4');
  });

  it('expirada: aviso, "Simular seguro" laranja e sem caixas "Comparar"', async () => {
    restore = signIn(
      requestFixture({
        quotes: [quoteFixture({ status: 'EXPIRED' }), { ...secondQuote, status: 'EXPIRED' }],
      }),
    );
    renderRoute('/cliente/simular/r1');
    expect(await screen.findByText('Esta simulação expirou em 25/01/27.')).toBeInTheDocument();
    const simulate = screen.getByRole('link', { name: 'Simular seguro' });
    expect(simulate).toHaveAttribute('href', '/cliente/simular?refazer=r1');
    expect(ctaButtons()).toHaveLength(1);
    expect(screen.queryByRole('checkbox', { name: /^Comparar/ })).not.toBeInTheDocument();
    expect(screen.getAllByTestId('quote-card')[0]).toHaveTextContent('Expirada');
  });

  it('usada: etiqueta e sem caixa "Comparar"', async () => {
    restore = signIn(
      requestFixture({ quotes: [quoteFixture(), { ...secondQuote, status: 'USED' }] }),
    );
    renderRoute('/cliente/simular/r1');
    const cards = await screen.findAllByTestId('quote-card');
    expect(cards[1]).toHaveTextContent('Usada numa proposta');
    expect(within(cards[1]!).queryByRole('checkbox')).not.toBeInTheDocument();
    expect(within(cards[0]!).getByRole('checkbox')).toBeInTheDocument();
  });

  it('nenhuma proposta: mensagem e "Alterar simulação" (azul)', async () => {
    restore = signIn(requestFixture({ quotes: [unavailableQuote] }));
    renderRoute('/cliente/simular/r1');
    expect(
      await screen.findByText('Nenhuma seguradora apresentou proposta para estas condições.'),
    ).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Alterar simulação' })).toHaveClass(
      'MuiButton-contained',
    );
    expect(ctaButtons()).toHaveLength(0);
  });

  it('pedido do mediador visto pelo cliente: "Cotação preparada pelo seu mediador."', async () => {
    restore = signIn(requestFixture({ origin: 'BROKER', clientMsisdn: '+258841234567' }));
    renderRoute('/cliente/simular/r1');
    expect(await screen.findByText('Cotação preparada pelo seu mediador.')).toBeInTheDocument();
  });

  it('404 → "Cotação não encontrada" com a ligação para a lista', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/quotes/r9')
        ? { status: 404, data: { code: 'QUOTE_REQUEST_NOT_FOUND' } }
        : undefined,
    );
    renderRoute('/cliente/simular/r9');
    expect(await screen.findByText('Cotação não encontrada')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Ver as cotações' })).toHaveAttribute(
      'href',
      '/cliente/cotacoes',
    );
  });

  it('mediador: valores para o telemóvel do cliente', async () => {
    const request = requestFixture({ origin: 'BROKER', clientMsisdn: '+258841234567' });
    restore = signInStaff(
      staffMe({ roles: ['BROKER'], orgType: 'BROKER', permissions: ['quote:create-for-client'] }),
      (config) =>
        is(config, 'GET', '/api/v1/quotes/r1') ? { status: 200, data: request } : undefined,
    );
    renderRoute('/mediador/cotacoes/r1');
    expect(
      await screen.findByText(
        'Valores para o cliente +258 84 123 4567. A proposta em nome do cliente chega numa próxima versão.',
      ),
    ).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Alterar simulação' })).toHaveAttribute(
      'href',
      '/mediador/cotacoes/nova?refazer=r1',
    );
  });
});
