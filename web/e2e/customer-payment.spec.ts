import { expect, test, type Page, type Route } from '@playwright/test';
import { expectNoHorizontalScroll, mzn, open, proposalFixture, proposalId } from './proposalMocks';

/** Pagamento de uma proposta aceite pelo cliente (S5-11), com a API simulada. */

/* eslint-disable @typescript-eslint/no-explicit-any */
type Json = any;

test.use({ viewport: { width: 360, height: 740 } });

const intentId = '0193a000-0000-7000-8000-000000000a01';

const me = {
  id: '0192f0c4-0000-7000-8000-000000000006',
  type: 'CUSTOMER',
  roles: ['CUSTOMER'],
  permissions: ['self:profile', 'self:proposals', 'self:payments'],
  preferredLanguage: 'pt',
  msisdn: '+258841234567',
  msisdnVerified: true,
  emailVerified: false,
  profileComplete: true,
  displayName: 'Ana Maria Mussa',
};

function acceptedProposal(): Json {
  const p = proposalFixture({
    comprehensive: false,
    channel: 'WEB',
    policyholderSource: 'PROFILE',
    kycState: 'VERIFIED',
  });
  p.status = 'ACCEPTED';
  p.expiresAt = '2030-01-20T10:00:00Z';
  return p;
}

function intent(method: 'MPESA' | 'BANK_REFERENCE', overrides: Json = {}): Json {
  return {
    id: intentId,
    reference: 'PG-2026-0001234',
    status: 'PENDING',
    method,
    purpose: 'NEW_POLICY',
    target: { type: 'PROPOSAL', id: proposalId, label: 'PR-2026-000123' },
    insurer: { id: 'i-demo', displayName: 'Seguradora Demo' },
    payerUserId: me.id,
    amount: mzn(518200),
    payerMsisdnMasked: method === 'MPESA' ? '+258*****4567' : undefined,
    expiresAt: '2030-01-20T10:00:00Z',
    createdAt: '2026-11-18T09:00:00Z',
    nextAction:
      method === 'MPESA'
        ? {
            type: 'AWAIT_CUSTOMER_CONFIRMATION',
            timeoutAt: new Date(Date.now() + 90_000).toISOString(),
          }
        : {
            type: 'PAY_BY_REFERENCE',
            entity: '12345',
            reference: '00548781108',
            amount: mzn(518200),
            validUntil: '2030-01-20T10:00:00Z',
          },
    ...overrides,
  };
}

interface PaymentApi {
  calls: string[];
  bodies: Record<string, Json[]>;
  /** Respostas de `GET /payment-intents/{id}` por ordem; a última repete-se. */
  reads: Json[];
  created: Json;
}

async function mockPaymentApi(page: Page, api: PaymentApi) {
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  await page.addInitScript(() => {
    window.localStorage.setItem('moseguros.customer.rt', 'refresh-1');
  });
  await page.route('**/api/v1/**', async (route) => {
    const req = route.request();
    const url = new URL(req.url());
    const key = `${req.method()} ${url.pathname}`;
    api.calls.push(key);
    if (req.method() === 'POST' && req.postData()) {
      (api.bodies[key] ??= []).push(req.postDataJSON());
    }
    if (key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, {
        accessToken: 'access-1',
        refreshToken: 'refresh-1',
        expiresIn: 900,
      });
    }
    if (req.headers()['authorization'] !== 'Bearer access-1') {
      return json(route, 401, { code: 'UNAUTHORIZED', status: 401, title: 'Unauthorized' });
    }
    if (key === 'GET /api/v1/me') return json(route, 200, me);
    if (key === `GET /api/v1/proposals/${proposalId}`) return json(route, 200, acceptedProposal());
    if (key === `GET /api/v1/proposals/${proposalId}/payment-methods`) {
      return json(route, 200, {
        proposalId,
        proposalNumber: 'PR-2026-000123',
        insurer: { id: 'i-demo', displayName: 'Seguradora Demo' },
        amount: mzn(518200),
        expiresAt: '2030-01-20T10:00:00Z',
        paid: false,
        methods: [
          { method: 'MPESA', sortOrder: 1 },
          { method: 'BANK_REFERENCE', sortOrder: 2 },
        ],
      });
    }
    if (key === `POST /api/v1/proposals/${proposalId}/payment-intents`) {
      if (!req.headers()['idempotency-key'])
        return json(route, 400, { code: 'IDEMPOTENCY_KEY_REQUIRED' });
      return json(route, 202, api.created);
    }
    if (key === `GET /api/v1/payment-intents/${intentId}`) {
      const next = api.reads.length > 1 ? api.reads.shift() : api.reads[0];
      return json(route, 200, next);
    }
    if (key === `POST /api/v1/payment-intents/${intentId}/reference-sms`) {
      return json(route, 202, { sentTo: '+258*****4567' });
    }
    return json(route, 404, { code: 'NOT_FOUND', status: 404, title: 'Not Found' });
  });
}

function newApi(created: Json, reads: Json[]): PaymentApi {
  return { calls: [], bodies: {}, created, reads };
}

/** Da proposta aceite ao ecrã de pagamento, com o método escolhido. */
async function choose(page: Page, method: 'M-Pesa' | 'Entidade e referência') {
  await open(page, `/cliente/propostas/${proposalId}`);
  const buy = page.getByRole('link', { name: 'Adquirir apólice' });
  await expect(buy).toBeVisible();
  await buy.click();
  await expect(page).toHaveURL(new RegExp(`/cliente/propostas/${proposalId}/pagar$`));
  await expect(
    page.getByText(
      'O pagamento é feito directamente à Seguradora Demo. A MoSeguros não recebe dinheiro.',
    ),
  ).toBeVisible();
  await page.getByRole('radio', { name: method }).check();
  await expectNoHorizontalScroll(page);
}

test('M-Pesa: proposta aceite → pagar → pendente → confirmado após duas leituras → "Ver proposta"', async ({
  page,
}) => {
  const api = newApi(intent('MPESA'), [
    intent('MPESA'),
    intent('MPESA'),
    intent('MPESA', { status: 'SUCCEEDED', nextAction: { type: 'NONE' } }),
  ]);
  await mockPaymentApi(page, api);
  await choose(page, 'M-Pesa');
  await expect(page.getByRole('textbox', { name: /Número M-Pesa/ })).toHaveValue('841234567');
  await page.getByRole('button', { name: 'Adquirir apólice' }).click();

  await expect(page).toHaveURL(new RegExp(`/cliente/pagamentos/${intentId}$`));
  await expect(page.getByText('Confirme o pagamento no seu telemóvel')).toBeVisible();
  await expect(page.getByText('Pendente', { exact: true })).toBeVisible();
  await expect(page.getByRole('timer')).toBeVisible();
  await expectNoHorizontalScroll(page);
  expect(await page.locator('.MuiButton-cta').count()).toBe(0);

  await expect(page.getByText('Pagamento confirmado')).toBeVisible({ timeout: 15_000 });
  expect(api.bodies[`POST /api/v1/proposals/${proposalId}/payment-intents`]).toEqual([
    { method: 'MPESA', channel: 'WEB', payerMsisdn: '+258841234567' },
  ]);
  const reads = api.calls.filter((c) => c === `GET /api/v1/payment-intents/${intentId}`);
  expect(reads.length).toBeGreaterThanOrEqual(2);
  await page.getByRole('link', { name: 'Ver proposta' }).click();
  await expect(page).toHaveURL(new RegExp(`/cliente/propostas/${proposalId}$`));
});

test('entidade e referência: os três dados, copiar a referência e enviar por SMS', async ({
  page,
  context,
}) => {
  await context.grantPermissions(['clipboard-read', 'clipboard-write']);
  const api = newApi(intent('BANK_REFERENCE'), [intent('BANK_REFERENCE')]);
  await mockPaymentApi(page, api);
  await choose(page, 'Entidade e referência');
  await page.getByRole('button', { name: 'Adquirir apólice' }).click();

  await expect(page.getByTestId('reference-entity')).toHaveText('12345');
  await expect(page.getByTestId('reference-reference')).toHaveText('005 487 811 08');
  await expect(page.getByTestId('reference-amount')).toHaveText(/5.?182,00 MT/);
  await expect(
    page.getByText('Pague exactamente este montante. A rede recusa outro valor.'),
  ).toBeVisible();
  await expectNoHorizontalScroll(page);

  await page.getByRole('button', { name: 'Copiar Referência' }).click();
  await expect(page.getByText('Copiado')).toBeVisible();
  expect(await page.evaluate(() => navigator.clipboard.readText())).toBe('00548781108');

  await page.getByRole('button', { name: 'Enviar por SMS' }).click();
  const dialog = page.getByRole('dialog');
  await expect(dialog.getByRole('textbox', { name: /Número de telemóvel/ })).toHaveValue(
    '841234567',
  );
  await dialog.getByRole('button', { name: 'Enviar' }).click();
  await expect(page.getByText('SMS enviado para +258*****4567')).toBeVisible();
  expect(api.bodies[`POST /api/v1/payment-intents/${intentId}/reference-sms`]).toEqual([
    { msisdn: '+258841234567' },
  ]);
  expect(api.bodies[`POST /api/v1/proposals/${proposalId}/payment-intents`]).toEqual([
    { method: 'BANK_REFERENCE', channel: 'WEB' },
  ]);
});

test('UNKNOWN: "Não volte a pagar" e nenhum botão de pagamento', async ({ page }) => {
  const unknown = intent('MPESA', {
    status: 'UNKNOWN',
    nextAction: { type: 'CHECKING_STATUS', nextCheckAt: '2030-01-10T09:05:00Z' },
  });
  await mockPaymentApi(page, newApi(unknown, [unknown]));
  await open(page, `/cliente/pagamentos/${intentId}`);
  await expect(page.getByText('Não volte a pagar')).toBeVisible();
  await expect(page.getByText('A confirmar', { exact: true })).toBeVisible();
  await expect(page.getByRole('main').getByRole('button')).toHaveCount(0);
  await expect(page.getByRole('main').getByRole('link')).toHaveCount(0);
  expect(await page.locator('.MuiButton-cta').count()).toBe(0);
});

test('FAILED (INSUFFICIENT_BALANCE) → "Escolher outra forma de pagamento" volta ao ecrã de pagamento', async ({
  page,
}) => {
  const failed = intent('MPESA', {
    status: 'FAILED',
    failureCode: 'INSUFFICIENT_BALANCE',
    nextAction: { type: 'NONE' },
  });
  await mockPaymentApi(page, newApi(failed, [failed]));
  await open(page, `/cliente/pagamentos/${intentId}`);
  await expect(page.getByText('Saldo insuficiente.')).toBeVisible();
  await page.getByRole('link', { name: 'Escolher outra forma de pagamento' }).click();
  await expect(page).toHaveURL(new RegExp(`/cliente/propostas/${proposalId}/pagar$`));
  await expect(page.getByRole('radio', { name: 'M-Pesa' })).toBeVisible();
  await expect(page.locator('.MuiButton-cta')).toHaveCount(1);
});
