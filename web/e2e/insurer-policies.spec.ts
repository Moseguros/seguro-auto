import { expect, test, type Page, type Route } from '@playwright/test';

const orgId = '0192f0c4-0000-7000-8000-0000000000a1';
const policyId = '0192f0c4-0000-7000-8000-000000000a01';
const fileId = '0192f0c4-0000-7000-8000-000000000f01';

type Json = Record<string, unknown>;

const insurerStaff = (permissions: string[], email: string) => ({
  id: `0192f0c4-0000-7000-8000-${email.length.toString().padStart(12, '0')}`,
  type: 'STAFF',
  roles: ['INSURER_ADMIN'],
  permissions,
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Gina Gestora',
  email,
  organization: { id: orgId, displayName: 'Seguradora Demo', type: 'INSURER' },
});

const mzn = (amount: number) => ({ amount, currency: 'MZN' });

const summary = {
  id: policyId,
  number: 'AP-2027-000001',
  status: 'ACTIVE',
  coverStatus: 'COVERED',
  policyholderName: 'Ana Tomador',
  plate: 'ABC-123-MC',
  productName: 'RC Auto',
  coverageType: 'MTPL_ONLY',
  startAt: '2027-02-10T08:31:00Z',
  endAt: '2028-02-09T21:59:59Z',
  totalPremium: mzn(299900),
  issuedAt: '2027-02-10T08:31:00Z',
};

const detail = {
  ...summary,
  insurer: { id: orgId, name: 'Seguradora Demo' },
  product: { name: 'RC Auto', type: 'MTPL_ONLY', versionId: 'v1' },
  proposal: { id: 'pr1', number: 'PR-2027-000123' },
  vehicle: { id: 'v1', plate: 'ABC-123-MC', make: 'Toyota', model: 'Hilux', year: 2020 },
  paymentPlan: 'FULL',
  renewalType: 'ANNUAL_RENEWABLE',
  coverages: [
    {
      code: 'MTPL',
      mandatory: true,
      capital: mzn(300000000),
      deductibleType: 'NONE',
      premium: mzn(299900),
    },
  ],
  instalments: [
    {
      seq: 1,
      dueDate: '2027-02-10',
      amount: mzn(299900),
      status: 'PAID',
      paidAt: '2027-02-10T08:31:00Z',
    },
  ],
  documents: [
    { type: 'CERTIFICATE', status: 'READY', fileId, fileName: 'certificado-AP-2027-000001.pdf' },
    { type: 'SCHEDULE', status: 'PENDING' },
  ],
  events: [{ type: 'ISSUED', occurredAt: '2027-02-10T08:31:00Z', actorType: 'SYSTEM' }],
};

async function mockApi(p: Page, me: Json) {
  const state = { listQueries: [] as string[], fileAuth: [] as (string | undefined)[] };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string) =>
    json(route, status, { type: 'about:blank', title: code, status, code });

  await p.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const url = new URL(request.url());
    const key = `${request.method()} ${url.pathname}`;
    if (key === 'POST /api/v1/auth/login' || key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (key === `GET /api/v1/files/${fileId}`) {
      state.fileAuth.push(request.headers()['authorization']);
    }
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, me);
    if (key === 'GET /api/v1/insurer/policies') {
      state.listQueries.push(url.search);
      return json(route, 200, { content: [summary], page: 0, size: 20, totalElements: 1 });
    }
    if (key === `GET /api/v1/insurer/policies/${policyId}`) return json(route, 200, detail);
    if (key === `GET /api/v1/files/${fileId}`) {
      return route.fulfill({
        status: 200,
        contentType: 'application/pdf',
        headers: {
          'content-disposition': 'attachment; filename="certificado-AP-2027-000001.pdf"',
        },
        body: '%PDF-1.4\n%%EOF\n',
      });
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

async function signIn(p: Page, email: string, to: string) {
  await p.goto(`/entrar?voltar=${encodeURIComponent(to)}`);
  await p.getByLabel('E-mail').fill(email);
  await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
  await p.getByLabel('Código da aplicação de autenticação').fill('123456');
  await p.getByRole('button', { name: 'Entrar' }).click();
}

test.describe('apólices da seguradora', () => {
  // Login, lista e detalhe carregados à parte: sob carga passam dos 30 s por defeito.
  test.setTimeout(90_000);

  test('lista com filtros → detalhe → documentos → descarregar o certificado', async ({
    page: p,
  }) => {
    const me = insurerStaff(['policy:read', 'proposal:read'], 'gestor@demo.local');
    const state = await mockApi(p, me);
    await signIn(p, me.email, '/seguradora/apolices');
    await expect(p.getByRole('heading', { name: 'Apólices', level: 1 })).toBeVisible();
    await expect(p.getByRole('gridcell', { name: 'AP-2027-000001' })).toBeVisible();

    // Estado: "Activa".
    await p.getByRole('combobox', { name: 'Estado' }).click();
    await p.getByRole('option', { name: 'Activa' }).click();
    await p.keyboard.press('Escape');
    await expect(p).toHaveURL(/status=ACTIVE/);
    await p.getByLabel('N.º da apólice, matrícula ou nome').fill('ABC-123');
    await expect(p).toHaveURL(/status=ACTIVE&q=ABC-123/);
    await expect
      .poll(() =>
        state.listQueries.some((q) => q.includes('status=ACTIVE') && q.includes('q=ABC-123')),
      )
      .toBe(true);

    await p.getByRole('gridcell', { name: 'AP-2027-000001' }).click();
    await expect(p).toHaveURL(new RegExp(`/seguradora/apolices/${policyId}$`));
    await expect(p.getByRole('heading', { level: 1, name: 'AP-2027-000001' })).toBeVisible();
    await expect(p.getByRole('link', { name: 'Ver proposta PR-2027-000123' })).toBeVisible();

    await p.getByRole('tab', { name: 'Documentos' }).click();
    await expect(p.getByText('A preparar…')).toBeVisible();
    const download = p.waitForEvent('download');
    await p.getByRole('button', { name: 'Descarregar: Certificado de seguro' }).click();
    expect((await download).suggestedFilename()).toBe('certificado-AP-2027-000001.pdf');
    expect(state.fileAuth).toEqual(['Bearer access']);
  });

  test('utilizador sem policy:read não vê o item "Apólices"', async ({ page: p }) => {
    const me = insurerStaff(['payment:read'], 'financas@demo.local');
    await mockApi(p, me);
    await signIn(p, me.email, '/seguradora');
    const nav = p.getByRole('navigation', { name: 'Seguradora' });
    await expect(nav.getByRole('link', { name: 'Pagamentos' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Apólices' })).toHaveCount(0);
  });
});
