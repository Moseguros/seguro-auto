import { expect, test, type Page, type Route } from '@playwright/test';
import { expectNoHorizontalScroll, mzn, open } from './proposalMocks';

/** Apólices, certificado, notificações e preferências do cliente (S6-06), com a API simulada. */

/* eslint-disable @typescript-eslint/no-explicit-any */
type Json = any;

test.use({ viewport: { width: 360, height: 740 } });

const policyId = '0193a000-0000-7000-8000-000000000c01';
const certificateFileId = '0193a000-0000-7000-8000-000000000c02';
const verificationUrl = 'https://moseguros.co.mz/v/Q7mK2xTOKEN';

const me = {
  id: '0192f0c4-0000-7000-8000-000000000006',
  type: 'CUSTOMER',
  roles: ['CUSTOMER'],
  permissions: ['self:profile', 'self:proposals', 'self:payments', 'self:policies'],
  preferredLanguage: 'pt',
  msisdn: '+258841234567',
  msisdnVerified: true,
  emailVerified: false,
  profileComplete: true,
  displayName: 'Ana Maria Mussa',
};

const vehicle = { id: 'v-1', plate: 'AAA-123-MC', make: 'Toyota', model: 'Corolla', year: 2018 };
const insurer = { id: 'i-demo', name: 'Seguradora Demo' };

const summary = {
  id: policyId,
  number: 'AP-2026-000123',
  status: 'ACTIVE',
  coverStatus: 'COVERED',
  coverageType: 'MTPL_ONLY',
  insurer,
  productName: 'RC Auto',
  vehicle,
  startAt: '2026-11-19T22:00:00Z',
  endAt: '2027-11-19T21:59:59Z',
  certificateReady: true,
};

const detail = {
  ...summary,
  product: { name: 'RC Auto', type: 'MTPL_ONLY', versionId: 'pv-1' },
  policyholderName: 'Ana Maria Mussa',
  issuedAt: '2026-11-19T10:00:00Z',
  paymentPlan: 'FULL',
  renewalType: 'ANNUAL_RENEWABLE',
  totalPremium: mzn(518200),
  coverages: [
    {
      code: 'MTPL',
      mandatory: true,
      capital: mzn(500000000),
      deductibleType: 'NONE',
      premium: mzn(518200),
    },
    {
      code: 'ASSISTANCE',
      mandatory: false,
      deductibleType: 'FIXED',
      deductibleAmount: mzn(250000),
      premium: mzn(0),
    },
  ],
  instalments: [
    {
      seq: 1,
      dueDate: '2026-11-20',
      amount: mzn(518200),
      status: 'PAID',
      paidAt: '2026-11-19T10:00:00Z',
    },
  ],
  certificate: {
    id: 'cert-1',
    number: 'CT-2026-000123',
    status: 'VALID',
    validFrom: '2026-11-19T22:00:00Z',
    validTo: '2027-11-19T21:59:59Z',
    verificationUrl,
    fileId: certificateFileId,
  },
  documents: [
    {
      type: 'CERTIFICATE',
      status: 'READY',
      fileId: certificateFileId,
      fileName: 'certificado-AP-2026-000123.pdf',
    },
    { type: 'SCHEDULE', status: 'READY', fileId: 'file-2', fileName: 'condicoes.pdf' },
    { type: 'GENERAL_CONDITIONS', status: 'READY', fileId: 'file-3', fileName: 'gerais.pdf' },
  ],
};

function notifications(readIds: Set<string>): Json[] {
  const minutesAgo = (m: number) => new Date(Date.now() - m * 60_000).toISOString();
  return [
    {
      id: 'n-2',
      category: 'POLICY',
      templateCode: 'POLICY_ISSUED',
      title: 'Apólice emitida',
      body: 'A sua apólice AP-2026-000123 foi emitida. O certificado já está disponível.',
      createdAt: minutesAgo(2),
      webPath: `/cliente/apolices/${policyId}`,
    },
    {
      id: 'n-1',
      category: 'PAYMENT',
      templateCode: 'PAYMENT_SUCCEEDED',
      title: 'Pagamento recebido',
      body: 'Recebemos o pagamento de 5 182,00 MT da proposta PR-2026-000123.',
      createdAt: minutesAgo(5),
      webPath: '/cliente/pagamentos/0193a000-0000-7000-8000-000000000a01',
    },
  ].map((n) => (readIds.has(n.id) ? { ...n, readAt: new Date().toISOString() } : n));
}

function preferences(): Json {
  const channels = () => [
    { channel: 'PUSH', enabled: true, available: false },
    { channel: 'WHATSAPP', enabled: true, available: true },
    { channel: 'SMS', enabled: true, available: true },
    { channel: 'EMAIL', enabled: true, available: false },
  ];
  return {
    categories: [
      { category: 'PAYMENT', mandatory: true, channels: channels() },
      { category: 'POLICY', mandatory: true, channels: channels() },
      { category: 'LEGAL', mandatory: true, channels: channels() },
      { category: 'PROPOSAL', mandatory: false, channels: channels() },
      { category: 'QUOTE', mandatory: false, channels: channels() },
    ],
  };
}

interface Api {
  calls: string[];
  bodies: Record<string, Json[]>;
  fileAuthorization: string[];
  policies: Json[];
  read: Set<string>;
}

async function mockApi(page: Page, policies: Json[] = [summary]): Promise<Api> {
  const api: Api = { calls: [], bodies: {}, fileAuthorization: [], policies, read: new Set() };
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
    api.calls.push(`${key}${url.search}`);
    if (['POST', 'PUT'].includes(req.method()) && req.postData()) {
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
    if (key === 'GET /api/v1/me/policies') {
      return json(route, 200, {
        content: api.policies,
        page: 0,
        size: 20,
        totalElements: api.policies.length,
      });
    }
    if (key === `GET /api/v1/me/policies/${policyId}`) return json(route, 200, detail);
    if (key === `GET /api/v1/files/${certificateFileId}`) {
      api.fileAuthorization.push(req.headers()['authorization'] ?? '');
      return route.fulfill({
        status: 200,
        contentType: 'application/pdf',
        headers: {
          'Content-Disposition': 'attachment; filename="certificado-AP-2026-000123.pdf"',
          'Access-Control-Expose-Headers': 'Content-Disposition',
        },
        body: '%PDF-1.7\n%%EOF\n',
      });
    }
    if (key === 'GET /api/v1/me/notifications/unread-count') {
      return json(route, 200, {
        count: notifications(api.read).filter((n: Json) => !n.readAt).length,
      });
    }
    if (key === 'GET /api/v1/me/notifications') {
      const all = notifications(api.read);
      const content =
        url.searchParams.get('unreadOnly') === 'true' ? all.filter((n: Json) => !n.readAt) : all;
      return json(route, 200, { content, page: 0, size: 20, totalElements: content.length });
    }
    const read = /^POST \/api\/v1\/me\/notifications\/([^/]+)\/read$/.exec(key);
    if (read) {
      api.read.add(read[1]!);
      return route.fulfill({ status: 204 });
    }
    if (key === 'GET /api/v1/me/notification-preferences') return json(route, 200, preferences());
    if (key === 'PUT /api/v1/me/notification-preferences') {
      const result = preferences();
      for (const change of req.postDataJSON().categories as Json[]) {
        const category = result.categories.find((c: Json) => c.category === change.category);
        for (const ch of change.channels as Json[]) {
          category.channels.find((c: Json) => c.channel === ch.channel).enabled = ch.enabled;
        }
      }
      return json(route, 200, result);
    }
    return json(route, 404, { code: 'NOT_FOUND', status: 404, title: 'Not Found' });
  });
  return api;
}

test('lista → detalhe → certificado em ecrã inteiro, que fecha com Esc', async ({ page }) => {
  await mockApi(page);
  await open(page, '/cliente/apolices');
  const card = page.getByTestId('policy-card');
  await expect(card).toContainText('AAA-123-MC');
  await expect(card).toContainText('Seguradora Demo · RC Auto');
  await expect(card).toContainText('Em vigor');
  await expect(card).toContainText('Apólice AP-2026-000123');
  await expectNoHorizontalScroll(page);

  await card.click();
  await expect(page).toHaveURL(`/cliente/apolices/${policyId}`);
  await expect(page.getByRole('heading', { name: 'AAA-123-MC' })).toBeVisible();
  await expect(page.getByText('Certificado n.º CT-2026-000123')).toBeVisible();
  await expect(page.getByTestId('certificate-qr')).toHaveAttribute('data-value', verificationUrl);
  await expect(page.getByText('Responsabilidade civil obrigatória')).toBeVisible();
  await expect(page.getByText('Condições particulares')).toBeVisible();
  await expectNoHorizontalScroll(page);

  await page.getByRole('button', { name: 'Mostrar em ecrã inteiro' }).click();
  const dialog = page.getByRole('dialog');
  await expect(dialog.getByTestId('certificate-fullscreen-qr')).toHaveAttribute(
    'data-value',
    verificationUrl,
  );
  await expect(
    dialog.getByRole('img', { name: 'Código QR para verificar o seguro' }),
  ).toBeVisible();
  await expect(dialog.getByText('AAA-123-MC')).toBeVisible();
  await expect(
    dialog.getByText('O agente pode ler este código para confirmar o seguro.'),
  ).toBeVisible();
  const box = await dialog.getByTestId('certificate-fullscreen-qr').boundingBox();
  expect(box!.width).toBeGreaterThan(300);
  await expectNoHorizontalScroll(page);
  await page.keyboard.press('Escape');
  await expect(dialog).toBeHidden();
});

test('descarregar o certificado pede /api/v1/files/{id} com Authorization', async ({ page }) => {
  const api = await mockApi(page);
  await open(page, `/cliente/apolices/${policyId}`);
  const downloadPromise = page.waitForEvent('download');
  await page.getByRole('button', { name: 'Descarregar certificado' }).click();
  const download = await downloadPromise;
  expect(download.suggestedFilename()).toBe('certificado-AP-2026-000123.pdf');
  expect(api.fileAuthorization).toEqual(['Bearer access-1']);
  expect(api.calls.some((c) => c.includes('access_token') || c.includes('token='))).toBe(false);
});

test('o sino mostra 2 por ler, o centro abre a apólice e a contagem desce', async ({ page }) => {
  const api = await mockApi(page);
  await open(page, '/cliente');
  const bell = page.getByRole('link', { name: 'Notificações, 2 por ler' });
  await expect(bell).toBeVisible();
  await expectNoHorizontalScroll(page);
  await bell.click();
  await expect(page).toHaveURL('/cliente/notificacoes');
  await expect(page.getByRole('heading', { name: 'Notificações' })).toBeVisible();
  await expect(page.getByText('Pagamento recebido')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Marcar todas como lidas' })).toBeVisible();
  await expectNoHorizontalScroll(page);

  await page.getByText('Apólice emitida').click();
  await expect(page).toHaveURL(`/cliente/apolices/${policyId}`);
  await expect(page.getByRole('link', { name: 'Notificações, 1 por ler' })).toBeVisible();
  expect(api.calls).toContain('POST /api/v1/me/notifications/n-2/read');
});

test('preferências: desligar SMS em "Propostas" e guardar', async ({ page }) => {
  const api = await mockApi(page);
  await open(page, '/cliente/notificacoes/preferencias');
  const proposals = page.getByTestId('category-PROPOSAL');
  await expect(proposals.getByRole('heading', { name: 'Propostas' })).toBeVisible();
  await expect(
    page.getByTestId('category-PAYMENT').getByText('Obrigatório', { exact: true }),
  ).toBeVisible();
  await expect(proposals.getByRole('switch', { name: 'E-mail' })).toBeDisabled();
  await expectNoHorizontalScroll(page);

  await proposals.getByRole('switch', { name: 'SMS' }).click();
  await page.getByRole('button', { name: 'Guardar' }).click();
  await expect(page.getByText('Preferências guardadas')).toBeVisible();
  expect(api.bodies['PUT /api/v1/me/notification-preferences']).toEqual([
    { categories: [{ category: 'PROPOSAL', channels: [{ channel: 'SMS', enabled: false }] }] },
  ]);
  await expect(proposals.getByRole('switch', { name: 'SMS' })).not.toBeChecked();
  await expect(page.getByRole('button', { name: 'Guardar' })).toBeDisabled();
});

test('sem apólices → "Simular seguro"', async ({ page }) => {
  await mockApi(page, []);
  await open(page, '/cliente/apolices?estado=todas');
  await expect(page.getByText('Ainda não tem apólices.')).toBeVisible();
  const cta = page.getByRole('link', { name: 'Simular seguro' });
  await expect(cta).toHaveClass(/MuiButton-cta/);
  await expectNoHorizontalScroll(page);
  await cta.click();
  await expect(page).toHaveURL('/cliente/simular');
});

test('o perfil leva às preferências', async ({ page }) => {
  await mockApi(page);
  await page.route('**/api/v1/me/profile', (route) =>
    route.fulfill({ status: 500, contentType: 'application/problem+json', body: '{}' }),
  );
  await open(page, '/cliente/perfil');
  await page.getByRole('link', { name: 'Escolher como recebo os avisos' }).click();
  await expect(page).toHaveURL('/cliente/notificacoes/preferencias');
});
