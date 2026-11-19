import { cleanup, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AxiosHeaders, type InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { intentFixture, methodsFixture, referenceIntentFixture } from '../../test/payments';
import { renderRoute } from '../../test/render';
import { is } from '../../test/staff';

let restore: () => void = () => {};
afterEach(() => restore());

const me = customerMe({ permissions: ['self:profile', 'self:payments'] });
const ctaCount = () => document.querySelectorAll('.MuiButton-cta').length;
const keyOf = (c: InternalAxiosRequestConfig) =>
  AxiosHeaders.from(c.headers).get('Idempotency-Key') as string | undefined;

const methodsHandler =
  (data = methodsFixture()) =>
  (c: InternalAxiosRequestConfig) =>
    is(c, 'GET', '/api/v1/proposals/p1/payment-methods') ? { status: 200, data } : null;

describe('ProposalPaymentPage', () => {
  it('mostra o resumo, os métodos pela ordem recebida e exactamente um botão laranja', async () => {
    restore = signInCustomer(
      me,
      methodsHandler(
        methodsFixture({
          methods: [
            { method: 'BANK_REFERENCE', sortOrder: 1 },
            { method: 'MPESA', sortOrder: 2, displayName: 'M-Pesa Vodacom' },
          ],
        }),
      ),
    );
    renderRoute('/cliente/propostas/p1/pagar');
    expect(await screen.findByText('Proposta PR-2027-000123 · Seguradora Demo')).toBeVisible();
    expect(screen.getAllByText(/^2.?999,00 MT$/).length).toBeGreaterThan(0);
    expect(screen.getByText('Pagar até 20/01/2027 12:00')).toBeVisible();
    expect(
      screen.getByText(
        'O pagamento é feito directamente à Seguradora Demo. A MoSeguros não recebe dinheiro.',
      ),
    ).toBeVisible();
    const radios = screen.getAllByRole('radio');
    expect(radios.map((r) => r.getAttribute('aria-label'))).toEqual([
      'Entidade e referência',
      'M-Pesa',
    ]);
    expect(screen.getByText('M-Pesa Vodacom')).toBeVisible();
    // Nada escolhido ao abrir (há dois métodos): o botão está desactivado.
    const cta = screen.getByRole('button', { name: 'Adquirir apólice' });
    expect(cta).toHaveClass('MuiButton-cta');
    expect(cta).toBeDisabled();
    expect(ctaCount()).toBe(1);

    const user = userEvent.setup();
    await user.click(screen.getByRole('radio', { name: 'Entidade e referência' }));
    expect(screen.getByText(/Recebe uma entidade e uma referência/)).toBeVisible();
    expect(screen.queryByRole('textbox', { name: /Número M-Pesa/ })).not.toBeInTheDocument();
    expect(cta).toBeEnabled();

    await user.click(screen.getByRole('radio', { name: 'M-Pesa' }));
    // Preenchido com o telemóvel da conta (84…).
    expect(screen.getByRole('textbox', { name: /Número M-Pesa/ })).toHaveValue('841234567');
    expect(screen.queryByText(/Recebe uma entidade e uma referência/)).not.toBeInTheDocument();
  });

  it('valida o número M-Pesa (84 ou 85) e escolhe o único método', async () => {
    restore = signInCustomer(
      customerMe({ msisdn: '+258821234567' }),
      methodsHandler(methodsFixture({ methods: [{ method: 'MPESA', sortOrder: 1 }] })),
    );
    renderRoute('/cliente/propostas/p1/pagar');
    const field = await screen.findByRole('textbox', { name: /Número M-Pesa/ });
    expect(screen.getByRole('radio', { name: 'M-Pesa' })).toBeChecked();
    // O telemóvel da conta não é M-Pesa: o campo fica vazio.
    expect(field).toHaveValue('');
    const user = userEvent.setup();
    await user.type(field, '821234567');
    await user.tab();
    expect(await screen.findByText('Use um número Vodacom (começado por 84 ou 85).')).toBeVisible();
    expect(screen.getByRole('button', { name: 'Adquirir apólice' })).toBeDisabled();
    await user.clear(field);
    await user.type(field, '85 123 4567');
    expect(field).toHaveValue('851234567');
    expect(screen.getByRole('button', { name: 'Adquirir apólice' })).toBeEnabled();
  });

  it('repete a chave depois de um erro de rede e muda-a quando o método muda', async () => {
    const keys: (string | undefined)[] = [];
    const bodies: unknown[] = [];
    restore = signInCustomer(me, methodsHandler(), (c) => {
      if (!is(c, 'POST', '/api/v1/proposals/p1/payment-intents')) return null;
      keys.push(keyOf(c));
      bodies.push(JSON.parse(c.data as string));
      return keys.length < 3
        ? 'network-error'
        : { status: 202, data: referenceIntentFixture({ id: 'i-9' }) };
    });
    const { router } = renderRoute('/cliente/propostas/p1/pagar');
    const user = userEvent.setup();
    await user.click(await screen.findByRole('radio', { name: 'M-Pesa' }));
    const cta = screen.getByRole('button', { name: 'Adquirir apólice' });
    await user.click(cta);
    expect(await screen.findByRole('alert')).toBeVisible();
    await waitFor(() => expect(cta).toBeEnabled());
    await user.click(cta);
    await waitFor(() => expect(keys).toHaveLength(2));
    expect(keys[0]).toMatch(/^[0-9a-f-]{36}$/);
    expect(keys[1]).toBe(keys[0]);
    expect(bodies[0]).toEqual({ method: 'MPESA', channel: 'WEB', payerMsisdn: '+258841234567' });

    await waitFor(() => expect(cta).toBeEnabled());
    await user.click(screen.getByRole('radio', { name: 'Entidade e referência' }));
    await user.click(cta);
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/pagamentos/i-9'));
    expect(keys[2]).not.toBe(keys[0]);
    expect(bodies[2]).toEqual({ method: 'BANK_REFERENCE', channel: 'WEB' });
    // Depois da resposta 2xx a chave é apagada.
    expect(sessionStorage.getItem('moseguros.pay.p1')).toBeNull();
  });

  it('PAYMENT_IN_PROGRESS vai para o pagamento intentId; ?origem=mediador envia BROKER_LINK', async () => {
    let body: unknown;
    restore = signInCustomer(
      me,
      methodsHandler(),
      (c) => {
        if (!is(c, 'POST', '/api/v1/proposals/p1/payment-intents')) return null;
        body = JSON.parse(c.data as string);
        return { status: 409, data: { code: 'PAYMENT_IN_PROGRESS', intentId: 'i-7' } };
      },
      (c) =>
        is(c, 'GET', '/api/v1/payment-intents/i-7')
          ? { status: 200, data: intentFixture({ id: 'i-7' }) }
          : null,
    );
    const { router } = renderRoute('/cliente/propostas/p1/pagar?origem=mediador');
    const user = userEvent.setup();
    await user.click(await screen.findByRole('radio', { name: 'M-Pesa' }));
    await user.click(screen.getByRole('button', { name: 'Adquirir apólice' }));
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/pagamentos/i-7'));
    expect(body).toMatchObject({ channel: 'BROKER_LINK' });
  });

  it('outros erros do envio e estados da proposta', async () => {
    restore = signInCustomer(me, methodsHandler(), (c) =>
      is(c, 'POST', '/api/v1/proposals/p1/payment-intents')
        ? { status: 409, data: { code: 'PAYMENT_TARGET_ALREADY_PAID' } }
        : null,
    );
    renderRoute('/cliente/propostas/p1/pagar');
    const user = userEvent.setup();
    await user.click(await screen.findByRole('radio', { name: 'Entidade e referência' }));
    await user.click(screen.getByRole('button', { name: 'Adquirir apólice' }));
    expect(await screen.findByText('Esta proposta já está paga.')).toBeVisible();
    expect(ctaCount()).toBe(0);
    restore();
    cleanup();

    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/proposals/p1/payment-methods')
        ? { status: 409, data: { code: 'PAYMENT_TARGET_NOT_PAYABLE' } }
        : null,
    );
    renderRoute('/cliente/propostas/p1/pagar');
    expect(await screen.findByText('Esta proposta já não pode ser paga.')).toBeVisible();
    expect(screen.getByRole('link', { name: 'Ver a proposta' })).toHaveAttribute(
      'href',
      '/cliente/propostas/p1',
    );
    restore();
    cleanup();

    restore = signInCustomer(me, methodsHandler(methodsFixture({ methods: [] })));
    renderRoute('/cliente/propostas/p1/pagar');
    expect(
      await screen.findByText(
        'A seguradora ainda não tem formas de pagamento disponíveis. Contacte a seguradora.',
      ),
    ).toBeVisible();
  });

  it('com um M-Pesa em curso mostra "Ver pagamento" e desactiva o botão laranja', async () => {
    restore = signInCustomer(
      me,
      methodsHandler(methodsFixture({ activeIntent: intentFixture({ id: 'i-3' }) })),
    );
    renderRoute('/cliente/propostas/p1/pagar');
    expect(await screen.findByText('Tem um pagamento em curso')).toBeVisible();
    const view = screen.getByRole('link', { name: 'Ver pagamento' });
    expect(view).toHaveAttribute('href', '/cliente/pagamentos/i-3');
    expect(view).not.toHaveClass('MuiButton-cta');
    await userEvent.setup().click(screen.getByRole('radio', { name: 'Entidade e referência' }));
    expect(screen.getByRole('button', { name: 'Adquirir apólice' })).toBeDisabled();
  });

  it('PAYMENT_METHOD_NOT_AVAILABLE mostra a mensagem e recarrega os métodos', async () => {
    let loads = 0;
    restore = signInCustomer(
      me,
      (c) => {
        if (!is(c, 'GET', '/api/v1/proposals/p1/payment-methods')) return null;
        loads++;
        return { status: 200, data: methodsFixture() };
      },
      (c) =>
        is(c, 'POST', '/api/v1/proposals/p1/payment-intents')
          ? { status: 409, data: { code: 'PAYMENT_METHOD_NOT_AVAILABLE' } }
          : null,
    );
    renderRoute('/cliente/propostas/p1/pagar');
    const user = userEvent.setup();
    await user.click(await screen.findByRole('radio', { name: 'Entidade e referência' }));
    await user.click(screen.getByRole('button', { name: 'Adquirir apólice' }));
    expect(
      await screen.findByText('Esta forma de pagamento não está disponível agora. Escolha outra.'),
    ).toBeVisible();
    await waitFor(() => expect(loads).toBe(2));
    expect(screen.getByRole('radio', { name: 'Entidade e referência' })).not.toBeChecked();
  });
});

describe('PaymentIntentPage', () => {
  const intentHandler =
    (...intents: ReturnType<typeof intentFixture>[]) =>
    (c: InternalAxiosRequestConfig) => {
      if (!is(c, 'GET', '/api/v1/payment-intents/i-1')) return null;
      return { status: 200, data: intents.length > 1 ? intents.shift() : intents[0] };
    };

  it('M-Pesa: passos, contagem até zero e "Estamos a confirmar"; nenhum botão laranja', async () => {
    restore = signInCustomer(
      me,
      intentHandler(
        intentFixture({
          nextAction: {
            type: 'AWAIT_CUSTOMER_CONFIRMATION',
            timeoutAt: new Date(Date.now() + 1500).toISOString(),
          },
        }),
      ),
    );
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByText('Confirme o pagamento no seu telemóvel')).toBeVisible();
    expect(
      screen.getByText('1. Desbloqueie o telemóvel com o número +258*****4567.'),
    ).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Pagamento PG-2027-0001234' })).toBeVisible();
    expect(screen.getByText('Pendente')).toBeVisible();
    expect(screen.getByRole('timer')).toHaveTextContent(/^00:0[0-2]$/);
    expect(
      await screen.findByText(
        'Ainda não recebemos a resposta. Estamos a confirmar.',
        {},
        { timeout: 5000 },
      ),
    ).toBeVisible();
    expect(screen.getByRole('timer')).toHaveTextContent('00:00');
    expect(screen.getByRole('link', { name: 'Voltar à proposta' })).toHaveAttribute(
      'href',
      '/cliente/propostas/p1',
    );
    expect(ctaCount()).toBe(0);
  });

  it('UNKNOWN: "Não volte a pagar" e a próxima verificação, sem botões', async () => {
    restore = signInCustomer(
      me,
      intentHandler(
        intentFixture({
          status: 'UNKNOWN',
          nextAction: { type: 'CHECKING_STATUS', nextCheckAt: '2027-01-10T09:05:00Z' },
        }),
      ),
    );
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByText('Não volte a pagar')).toBeVisible();
    expect(screen.getByText('Próxima verificação às 11:05')).toBeVisible();
    expect(within(screen.getByRole('main')).queryByRole('button')).not.toBeInTheDocument();
    expect(ctaCount()).toBe(0);
  });

  it('entidade e referência: os três dados, copiar sem espaços, SMS e PDF', async () => {
    const writeText = vi.fn().mockResolvedValue(undefined);
    Object.defineProperty(navigator, 'clipboard', { value: { writeText }, configurable: true });
    let smsBody: unknown;
    restore = signInCustomer(me, intentHandler(referenceIntentFixture()), (c) => {
      if (!is(c, 'POST', '/api/v1/payment-intents/i-1/reference-sms')) return null;
      smsBody = JSON.parse(c.data as string);
      return { status: 202, data: { sentTo: '+258*****4567' } };
    });
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByTestId('reference-reference')).toHaveTextContent('005 487 811 08');
    expect(screen.getByTestId('reference-entity')).toHaveTextContent('12345');
    expect(screen.getByTestId('reference-amount')).toHaveTextContent(/^2.?999,00 MT$/);
    expect(
      screen.getByText('Pague exactamente este montante. A rede recusa outro valor.'),
    ).toBeVisible();
    expect(screen.getByText('Válida até 20/01/2027 12:00')).toBeVisible();

    const user = userEvent.setup();
    Object.defineProperty(navigator, 'clipboard', { value: { writeText }, configurable: true });
    await user.click(screen.getByRole('button', { name: 'Copiar Referência' }));
    expect(writeText).toHaveBeenLastCalledWith('00548781108');
    expect(await screen.findByText('Copiado')).toBeVisible();
    await user.click(screen.getByRole('button', { name: 'Copiar Montante' }));
    expect(writeText).toHaveBeenLastCalledWith('2999,00');

    await user.click(screen.getByRole('button', { name: 'Enviar por SMS' }));
    const dialog = await screen.findByRole('dialog');
    const field = within(dialog).getByRole('textbox', { name: /Número de telemóvel/ });
    expect(field).toHaveValue('841234567');
    await user.clear(field);
    await user.type(field, '821112233');
    await user.click(within(dialog).getByRole('button', { name: 'Enviar' }));
    expect(await screen.findByText('SMS enviado para +258*****4567')).toBeVisible();
    expect(smsBody).toEqual({ msisdn: '+258821112233' });
    expect(ctaCount()).toBe(0);
  });

  it('SMS da referência: limite de envios', async () => {
    restore = signInCustomer(me, intentHandler(referenceIntentFixture()), (c) =>
      is(c, 'POST', '/api/v1/payment-intents/i-1/reference-sms')
        ? { status: 429, data: { code: 'REFERENCE_SMS_RATE_LIMITED' } }
        : null,
    );
    renderRoute('/cliente/pagamentos/i-1');
    const user = userEvent.setup();
    await user.click(await screen.findByRole('button', { name: 'Enviar por SMS' }));
    const dialog = await screen.findByRole('dialog');
    await user.click(within(dialog).getByRole('button', { name: 'Enviar' }));
    expect(await within(dialog).findByText('Já enviou esta referência 3 vezes.')).toBeVisible();
  });

  it('resultados finais com o passo seguinte', async () => {
    restore = signInCustomer(
      me,
      intentHandler(intentFixture({ status: 'SUCCEEDED', nextAction: { type: 'NONE' } })),
    );
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByText('Pagamento confirmado')).toBeVisible();
    expect(screen.getByText('A sua apólice vai ser emitida em breve.')).toBeVisible();
    expect(screen.getByRole('link', { name: 'Ver proposta' })).toHaveAttribute(
      'href',
      '/cliente/propostas/p1',
    );
    restore();
    cleanup();

    restore = signInCustomer(
      me,
      intentHandler(
        intentFixture({
          status: 'FAILED',
          failureCode: 'INSUFFICIENT_BALANCE',
          nextAction: { type: 'NONE' },
        }),
      ),
    );
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByText('Saldo insuficiente.')).toBeVisible();
    expect(screen.getByRole('link', { name: 'Escolher outra forma de pagamento' })).toHaveAttribute(
      'href',
      '/cliente/propostas/p1/pagar',
    );
    expect(ctaCount()).toBe(0);
    restore();
    cleanup();

    restore = signInCustomer(
      me,
      intentHandler(intentFixture({ status: 'CANCELLED', nextAction: { type: 'NONE' } })),
    );
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByText('Este pagamento foi substituído por outro.')).toBeVisible();
  });

  it('pagamento de outro cliente → "Pagamento não encontrado."', async () => {
    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/payment-intents/i-1')
        ? { status: 404, data: { code: 'PAYMENT_INTENT_NOT_FOUND' } }
        : null,
    );
    renderRoute('/cliente/pagamentos/i-1');
    expect(await screen.findByText('Pagamento não encontrado.')).toBeVisible();
    expect(screen.getByRole('link', { name: 'Ver os meus pagamentos' })).toHaveAttribute(
      'href',
      '/cliente/pagamentos',
    );
  });
});

describe('MyPaymentsPage', () => {
  it('lista com "Carregar mais" e lista vazia', async () => {
    const urls: string[] = [];
    restore = signInCustomer(me, (c) => {
      if (!is(c, 'GET', '/api/v1/me/payments')) return null;
      const params = c.params as { page: number; size: number; sort: string };
      urls.push(`${params.page}:${params.size}:${params.sort}`);
      return {
        status: 200,
        data: {
          content: [intentFixture({ id: `i-${params.page}`, status: 'SUCCEEDED' })],
          page: params.page,
          size: 1,
          totalElements: 2,
        },
      };
    });
    renderRoute('/cliente/pagamentos');
    expect(await screen.findAllByText('PR-2027-000123')).toHaveLength(1);
    expect(screen.getByText('Pago')).toBeVisible();
    expect(screen.getByText('10/01/2027 11:00 · Seguradora Demo')).toBeVisible();
    await userEvent.setup().click(screen.getByRole('button', { name: 'Carregar mais' }));
    await waitFor(() => expect(screen.getAllByText('PR-2027-000123')).toHaveLength(2));
    expect(urls).toEqual(['0:20:createdAt,desc', '1:20:createdAt,desc']);
    expect(screen.queryByRole('button', { name: 'Carregar mais' })).not.toBeInTheDocument();
    expect(screen.getAllByRole('link')[0]).toBeDefined();
    restore();
    cleanup();

    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/me/payments')
        ? { status: 200, data: { content: [], page: 0, size: 20, totalElements: 0 } }
        : null,
    );
    renderRoute('/cliente/pagamentos');
    expect(await screen.findByText('Ainda não tem pagamentos.')).toBeVisible();
  });
});
