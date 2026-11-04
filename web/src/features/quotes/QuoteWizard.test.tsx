import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { catalogProduct, ctaButtons, requestFixture, summaryFixture } from '../../test/quotes';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe, type MockResult } from '../../test/staff';
import { driverFixture, page, vehicleFixture } from '../../test/vehicles';
import { draftStorageKeys } from './config';
import { emptyDraft } from './useQuoteDraft';

const owner = driverFixture({ isOwner: true });

function customerApi(extra?: (config: InternalAxiosRequestConfig) => MockResult | undefined) {
  return signInCustomer(customerMe(), (config) => {
    const result = extra?.(config);
    if (result) return result;
    if (is(config, 'GET', '/api/v1/me/vehicles'))
      return { status: 200, data: page([vehicleFixture]) };
    if (is(config, 'GET', '/api/v1/me/drivers')) return { status: 200, data: page([owner]) };
    if (is(config, 'GET', '/api/v1/me/quotes'))
      return { status: 200, data: page([summaryFixture()]) };
    if (is(config, 'GET', '/api/v1/catalog/products')) {
      return { status: 200, data: page([catalogProduct()]) };
    }
    return undefined;
  });
}

const next = () => userEvent.click(screen.getByRole('button', { name: 'Continuar' }));

describe('QuoteWizard', () => {
  let restore: () => void = () => {};
  beforeEach(() => sessionStorage.clear());
  afterEach(() => restore());

  it('/cliente/simular abre o assistente; cada passo tem no máximo um botão laranja', async () => {
    restore = customerApi();
    renderRoute('/cliente/simular');
    expect(
      await screen.findByRole('heading', { level: 1, name: 'Simular seguro' }),
    ).toBeInTheDocument();
    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Passo 1 de 5 · Veículo');
    // Simulações recentes por baixo do passo.
    expect(await screen.findByTestId('recent-quotes')).toHaveTextContent('Ver todas');
    expect(screen.getByRole('button', { name: 'Continuar' })).toBeDisabled();
    expect(ctaButtons()).toHaveLength(0);

    await userEvent.click(screen.getByRole('radio', { name: /AAA-100-MC/ }));
    // Condutor por defeito: o próprio cliente.
    expect(screen.getByLabelText('Condutor')).toHaveValue('d1');
    await next();

    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Passo 2 de 5 · Tipo');
    expect(ctaButtons()).toHaveLength(0);
    await userEvent.click(
      screen.getByRole('radio', { name: /Responsabilidade civil \+ danos próprios/ }),
    );
    await next();

    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Passo 3 de 5 · Coberturas');
    await waitFor(() =>
      expect(screen.getByRole('checkbox', { name: 'Choque, colisão e capotamento' })).toBeChecked(),
    );
    expect(ctaButtons()).toHaveLength(0);
    await next();

    expect(screen.getByTestId('quote-step-of')).toHaveTextContent(
      'Passo 4 de 5 · Início e duração',
    );
    expect(screen.getByText('1 ano')).toBeInTheDocument();
    expect(screen.getByTestId('quote-end-date')).toHaveTextContent(/^Termina a /);
    expect(ctaButtons()).toHaveLength(0);
    await next();

    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Passo 5 de 5 · Resumo');
    expect(screen.queryByRole('button', { name: 'Continuar' })).not.toBeInTheDocument();
    expect(ctaButtons()).toHaveLength(1);
    expect(ctaButtons()[0]).toHaveTextContent('Simular seguro');
    expect(screen.getByText(/^Ana Maria Mussa · \d+ anos, carta há \d+ anos$/)).toBeInTheDocument();
  });

  it('/cliente/cotar não abre o assistente (cai na página em construção)', async () => {
    restore = customerApi();
    renderRoute('/cliente/cotar');
    expect(await screen.findByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
    expect(screen.queryByText(/Passo 1 de/)).not.toBeInTheDocument();
  });

  it('cria a simulação, apaga o rascunho e vai para os resultados', async () => {
    const posted: unknown[] = [];
    restore = customerApi((config) => {
      if (is(config, 'POST', '/api/v1/quotes')) {
        posted.push(JSON.parse(config.data as string));
        return { status: 201, data: requestFixture() };
      }
      if (is(config, 'GET', '/api/v1/quotes/r1')) return { status: 200, data: requestFixture() };
      return undefined;
    });
    sessionStorage.setItem(
      draftStorageKeys.customer,
      JSON.stringify({
        ...emptyDraft(),
        step: 4,
        vehicleId: 'v1',
        driverId: 'd1',
        productType: 'COMPREHENSIVE',
        coverages: [{ code: 'OD_COLLISION' }],
        coveragesFor: 'COMPREHENSIVE',
        startDate: '2027-01-15',
      }),
    );
    const { router } = renderRoute('/cliente/simular');
    await userEvent.click(await screen.findByRole('button', { name: 'Simular seguro' }));
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/simular/r1'));
    expect(posted).toEqual([
      {
        vehicleId: 'v1',
        driverId: 'd1',
        productType: 'COMPREHENSIVE',
        coverages: [{ code: 'OD_COLLISION' }],
        startDate: '2027-01-15',
        durationDays: 365,
      },
    ]);
    expect(sessionStorage.getItem(draftStorageKeys.customer)).toBeNull();
  });

  it('?refazer= preenche o rascunho e actualiza uma data de início passada', async () => {
    restore = customerApi((config) =>
      is(config, 'GET', '/api/v1/quotes/r1')
        ? { status: 200, data: requestFixture({ startDate: '2020-01-15' }) }
        : undefined,
    );
    renderRoute('/cliente/simular?refazer=r1');
    expect(await screen.findByText('Actualizámos a data de início para hoje.')).toBeInTheDocument();
    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Resumo');
    expect(screen.getByText(/Choque, colisão e capotamento/)).toBeInTheDocument();
  });

  it('mediador sem quote:create-for-client vai para /sem-acesso', async () => {
    restore = signInStaff(staffMe({ roles: ['BROKER'], orgType: 'BROKER', permissions: [] }));
    const { router } = renderRoute('/mediador/cotacoes/nova');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
  });

  it('mediador com a permissão: passos Cliente → … → Resumo', async () => {
    restore = signInStaff(
      staffMe({ roles: ['BROKER'], orgType: 'BROKER', permissions: ['quote:create-for-client'] }),
    );
    renderRoute('/mediador/cotacoes/nova');
    expect(
      await screen.findByRole('heading', { level: 1, name: 'Nova cotação' }),
    ).toBeInTheDocument();
    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Passo 1 de 7 · Cliente');
    const msisdn = screen.getByLabelText('Telemóvel do cliente');
    await userEvent.type(msisdn, '881234567');
    expect(screen.getByText(/^Número de telemóvel inválido/)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Continuar' })).toBeDisabled();
    await userEvent.clear(msisdn);
    await userEvent.type(msisdn, '841234567');
    expect(screen.getByRole('button', { name: 'Continuar' })).toBeEnabled();
    expect(ctaButtons()).toHaveLength(0);
  });
});
