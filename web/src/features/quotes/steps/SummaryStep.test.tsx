import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { todayInMaputo } from '../../../format/dates';
import { customerMe, signInCustomer } from '../../../test/customer';
import { catalogProduct } from '../../../test/quotes';
import { renderRoute } from '../../../test/render';
import { is, type MockResult } from '../../../test/staff';
import { driverFixture, page, vehicleFixture } from '../../../test/vehicles';
import { draftStorageKeys } from '../config';
import { emptyDraft } from '../useQuoteDraft';

function openSummary(postResult: MockResult) {
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
      startDate: todayInMaputo(),
    }),
  );
  const restore = signInCustomer(customerMe(), (config) => {
    if (is(config, 'POST', '/api/v1/quotes')) return postResult;
    if (is(config, 'GET', '/api/v1/me/vehicles'))
      return { status: 200, data: page([vehicleFixture]) };
    if (is(config, 'GET', '/api/v1/me/drivers')) {
      return { status: 200, data: page([driverFixture({ isOwner: true })]) };
    }
    if (is(config, 'GET', '/api/v1/catalog/products')) {
      return { status: 200, data: page([catalogProduct()]) };
    }
    return undefined;
  });
  return { restore, ...renderRoute('/cliente/simular') };
}

// Fluxos com vários ecrãs: mais tempo quando a máquina está carregada.
describe('SummaryStep', { timeout: 60_000 }, () => {
  let restore: () => void = () => {};
  beforeEach(() => sessionStorage.clear());
  afterEach(() => restore());

  it('QUOTE_RATE_LIMITED mostra os minutos a partir de retryAfterSeconds', async () => {
    ({ restore } = openSummary({
      status: 429,
      data: { code: 'QUOTE_RATE_LIMITED', retryAfterSeconds: 125 },
    }));
    await userEvent.click(await screen.findByRole('button', { name: 'Simular seguro' }));
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Fez muitas simulações na última hora. Tente mais tarde. Pode tentar de novo daqui a 3 minutos.',
    );
  });

  it('VALIDATION_ERROR volta ao passo do campo e mostra o erro no campo', async () => {
    ({ restore } = openSummary({
      status: 400,
      data: {
        code: 'VALIDATION_ERROR',
        errors: [{ field: 'startDate', code: 'START_DATE_OUT_OF_RANGE', message: 'x' }],
      },
    }));
    await userEvent.click(await screen.findByRole('button', { name: 'Simular seguro' }));
    await waitFor(() =>
      expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Início e duração'),
    );
    expect(
      screen.getByText('A data de início tem de ser entre hoje e daqui a 60 dias.'),
    ).toBeInTheDocument();
  });

  it('VEHICLE_VALUE_REQUIRED volta ao passo do veículo com a mensagem', async () => {
    ({ restore } = openSummary({ status: 400, data: { code: 'VEHICLE_VALUE_REQUIRED' } }));
    await userEvent.click(await screen.findByRole('button', { name: 'Simular seguro' }));
    await waitFor(() => expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Veículo'));
    expect(
      screen.getByText('Para danos próprios, indique o valor comercial do veículo.'),
    ).toBeInTheDocument();
  });

  it('"Alterar" volta ao passo do bloco', async () => {
    ({ restore } = openSummary({ status: 500, data: {} }));
    await userEvent.click(await screen.findByRole('button', { name: 'Alterar: Tipo de seguro' }));
    expect(screen.getByTestId('quote-step-of')).toHaveTextContent('Passo 2 de 5 · Tipo');
  });
});
