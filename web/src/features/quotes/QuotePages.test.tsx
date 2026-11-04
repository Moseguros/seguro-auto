import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { ctaButtons, requestFixture, summaryFixture } from '../../test/quotes';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { page } from '../../test/vehicles';

const broker = staffMe({
  roles: ['BROKER'],
  orgType: 'BROKER',
  permissions: ['quote:create-for-client'],
});

// Fluxos com vários ecrãs: mais tempo quando a máquina está carregada.
describe('detalhe e listas das cotações', { timeout: 60_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('detalhe: coberturas, encargos, total, documentos e sem botão laranja', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/quotes/r1') ? { status: 200, data: requestFixture() } : undefined,
    );
    renderRoute('/cliente/simular/r1/q1');
    expect(await screen.findByRole('heading', { name: 'Seguradora Demo' })).toBeInTheDocument();
    expect(screen.getByText('Auto Demo · versão 1')).toBeInTheDocument();
    expect(screen.getByText('Imposto de selo (provisório)')).toBeInTheDocument();
    expect(screen.getByText('Ajuste de arredondamento')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Informação pré-contratual' })).toBeEnabled();
    expect(
      screen.getByText('Em breve vai poder avançar daqui para a proposta.'),
    ).toBeInTheDocument();
    expect(ctaButtons()).toHaveLength(0);
  });

  it('detalhe de uma sem proposta: só o motivo', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/quotes/r1') ? { status: 200, data: requestFixture() } : undefined,
    );
    renderRoute('/cliente/simular/r1/q3');
    expect(
      await screen.findByText(
        'Esta seguradora não cobre este veículo ou condutor nestas condições.',
      ),
    ).toBeInTheDocument();
    expect(screen.queryByText('Prémio total')).not.toBeInTheDocument();
  });

  it('as minhas cotações: cartões com "Pelo mediador" e "Expirada"; um único laranja no vazio', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/quotes')
        ? {
            status: 200,
            data: page([
              summaryFixture(),
              summaryFixture({
                id: 'r2',
                origin: 'BROKER',
                expired: true,
                offeredCount: 0,
                bestTotal: undefined,
              }),
            ]),
          }
        : undefined,
    );
    renderRoute('/cliente/cotacoes');
    const cards = await screen.findAllByTestId('quote-summary-card');
    expect(cards[0]).toHaveTextContent('2 propostas');
    expect(cards[0]).toHaveTextContent('Válida até 25/01/27');
    expect(cards[1]).toHaveTextContent('Pelo mediador');
    expect(cards[1]).toHaveTextContent('Expirada');
    expect(ctaButtons()).toHaveLength(0);
  });

  it('as minhas cotações vazia: "Ainda não fez simulações." e "Simular seguro"', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/quotes') ? { status: 200, data: page([]) } : undefined,
    );
    renderRoute('/cliente/cotacoes');
    expect(await screen.findByText('Ainda não fez simulações.')).toBeInTheDocument();
    expect(ctaButtons()).toHaveLength(1);
    expect(screen.getByRole('link', { name: 'Simular seguro' })).toHaveAttribute(
      'href',
      '/cliente/simular',
    );
  });

  it('início do cliente: cartão "As minhas cotações" e um só botão laranja', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/quotes')
        ? { status: 200, data: page([summaryFixture()]) }
        : undefined,
    );
    renderRoute('/cliente');
    expect(await screen.findByTestId('recent-quotes')).toHaveTextContent('As minhas cotações');
    expect(screen.getByRole('link', { name: 'Ver todas' })).toHaveClass('MuiButton-outlined');
    expect(ctaButtons()).toHaveLength(1);
  });

  it('mediador: lista filtrada pelo telemóvel; "Nova cotação" azul; vazio', async () => {
    const calls: unknown[] = [];
    restore = signInStaff(broker, (config) => {
      if (!is(config, 'GET', '/api/v1/broker/quotes')) return undefined;
      const params = config.params as { clientMsisdn?: string };
      calls.push(params.clientMsisdn);
      return {
        status: 200,
        data: page(
          params.clientMsisdn === '+258841234567'
            ? [summaryFixture({ origin: 'BROKER', clientMsisdn: '+258841234567' })]
            : [],
        ),
      };
    });
    renderRoute('/mediador/cotacoes');
    expect(await screen.findByText('Ainda não há cotações.')).toBeInTheDocument();
    expect(screen.getAllByRole('link', { name: 'Nova cotação' })[0]).toHaveClass(
      'MuiButton-contained',
    );
    await userEvent.type(screen.getByLabelText('Telemóvel do cliente'), '841234567');
    await userEvent.click(screen.getByRole('button', { name: 'Filtrar' }));
    const card = await screen.findByTestId('quote-summary-card');
    expect(card).toHaveTextContent('+258 84 123 4567');
    expect(calls).toEqual([undefined, '+258841234567']);
    expect(ctaButtons()).toHaveLength(0);
  });

  it('o item "Cotações" do mediador só aparece com quote:create-for-client', async () => {
    restore = signInStaff(staffMe({ roles: ['BROKER'], orgType: 'BROKER', permissions: [] }));
    const { unmount } = renderRoute('/mediador');
    await userEvent.click(await screen.findByRole('button', { name: 'Abrir menu' }));
    const nav = await screen.findByRole('navigation', { name: 'Mediador' });
    expect(within(nav).queryByRole('link', { name: 'Cotações' })).not.toBeInTheDocument();
    unmount();
    restore();

    restore = signInStaff(broker);
    renderRoute('/mediador');
    await userEvent.click(await screen.findByRole('button', { name: 'Abrir menu' }));
    const allowed = await screen.findByRole('navigation', { name: 'Mediador' });
    expect(within(allowed).getByRole('link', { name: 'Cotações' })).toBeInTheDocument();
  });

  it('cliente que abre /mediador/cotacoes vai para /sem-acesso', async () => {
    restore = signInCustomer(customerMe());
    const { router } = renderRoute('/mediador/cotacoes');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
  });
});
