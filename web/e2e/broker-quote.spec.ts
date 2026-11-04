import { expect, test, type Page, type Route } from '@playwright/test';

test.use({ viewport: { width: 360, height: 740 } });

const requestId = '0193a000-0000-7000-8000-000000000701';

const broker = {
  id: '0192f0c4-0000-7000-8000-000000000010',
  type: 'STAFF',
  roles: ['BROKER'],
  permissions: ['quote:create-for-client'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Carlos Corretor',
  email: 'carlos@corretor.co.mz',
  organization: { id: 'o-broker', displayName: 'Corretora Demo', type: 'BROKER' },
};

const customer = {
  id: '0192f0c4-0000-7000-8000-000000000006',
  type: 'CUSTOMER',
  roles: ['CUSTOMER'],
  permissions: ['self:profile', 'self:vehicles', 'self:quotes'],
  preferredLanguage: 'pt',
  msisdn: '+258840000001',
  msisdnVerified: true,
  emailVerified: false,
  profileComplete: true,
  displayName: 'Ana Maria Mussa',
};

const mzn = (amount: number) => ({ amount, currency: 'MZN' });

const pageOf = (content: unknown[]) => ({
  content,
  page: 0,
  size: 20,
  totalElements: content.length,
});

const mtplProduct = {
  productId: 'p-rc',
  productVersionId: 'pv-rc',
  code: 'DEMO-RC',
  name: 'RC Demo',
  type: 'MTPL_ONLY',
  status: 'PUBLISHED',
  insurer: { id: 'i-demo', displayName: 'Seguradora Demo' },
  usages: ['PRIVATE'],
  vehicleCategories: ['LIGHT_PASSENGER'],
  validFrom: '2026-01-01',
  versionNo: 1,
  documents: [],
  coverages: [
    {
      code: 'MTPL',
      mandatory: true,
      capitalType: 'FIXED',
      capital: mzn(300000000),
      deductibleType: 'NONE',
    },
  ],
};

async function mockApi(page: Page, me: Record<string, unknown>) {
  const state = {
    created: [] as Record<string, unknown>[],
    filters: [] as (string | null)[],
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string) =>
    json(route, status, { type: 'about:blank', title: code, status, code });

  const request = () => {
    const body = state.created[0] ?? {};
    return {
      id: requestId,
      origin: 'BROKER',
      clientMsisdn: body.clientMsisdn,
      createdAt: '2027-01-10T08:15:00Z',
      productType: 'MTPL_ONLY',
      vehicle: { ...(body.vehicle as object), vehicleId: null },
      driver: { driverId: null, licenceCategories: [], ...(body.driver as object) },
      requestedCoverages: [],
      startDate: body.startDate,
      endDate: '2028-01-14',
      durationDays: 365,
      quotes: [
        {
          id: 'q-rc',
          status: 'OFFERED',
          insurer: {
            id: 'i-demo',
            displayName: 'Seguradora Demo',
            shortCode: 'DEMO',
            logoFileId: null,
          },
          product: {
            id: 'p-rc',
            code: 'DEMO-RC',
            name: 'RC Demo',
            type: 'MTPL_ONLY',
            versionId: 'pv-rc',
            versionNo: 1,
          },
          coverages: [
            {
              code: 'MTPL',
              mandatory: true,
              capital: mzn(300000000),
              deductible: { type: 'NONE' },
              premium: mzn(495000),
            },
          ],
          commercialPremium: mzn(495000),
          charges: [],
          roundingAdjustment: mzn(0),
          totalAmount: mzn(495000),
          validUntil: '2027-01-25T08:15:00Z',
        },
      ],
    };
  };
  const summary = () => ({
    id: requestId,
    createdAt: '2027-01-10T08:15:00Z',
    origin: 'BROKER',
    productType: 'MTPL_ONLY',
    vehicle: { plate: null, make: null, model: null },
    clientMsisdn: state.created[0]?.clientMsisdn,
    startDate: state.created[0]?.startDate,
    durationDays: 365,
    offeredCount: 1,
    bestTotal: mzn(495000),
    validUntil: '2027-01-25T08:15:00Z',
    expired: false,
  });

  await page.route('**/api/v1/**', async (route) => {
    const req = route.request();
    const url = new URL(req.url());
    const key = `${req.method()} ${url.pathname}`;

    if (key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, {
        accessToken: 'access-1',
        refreshToken: 'refresh-1',
        expiresIn: 900,
      });
    }
    if (key === 'GET /api/v1/catalog/products') return json(route, 200, pageOf([mtplProduct]));
    if (key === 'GET /api/v1/reference-domains/LICENCE_CATEGORY/values') {
      return json(route, 200, {
        values: [
          { code: 'A', labelPt: 'A — Motociclos' },
          { code: 'B', labelPt: 'B — Ligeiros' },
        ],
      });
    }
    if (req.headers()['authorization'] !== 'Bearer access-1') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, me);
    if (key === 'GET /api/v1/me/quotes') return json(route, 200, pageOf([]));
    if (key === 'GET /api/v1/broker/quotes') {
      const filter = url.searchParams.get('clientMsisdn');
      state.filters.push(filter);
      const all = state.created.length > 0 ? [summary()] : [];
      return json(route, 200, pageOf(filter ? all.filter((s) => s.clientMsisdn === filter) : all));
    }
    if (key === 'POST /api/v1/broker/quotes') {
      state.created.push(req.postDataJSON() as Record<string, unknown>);
      return json(route, 201, request());
    }
    if (key === `GET /api/v1/quotes/${requestId}`) return json(route, 200, request());
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

async function expectNoHorizontalScroll(page: Page) {
  const overflow = await page.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

test('corretor cota RC para o telemóvel de um cliente e encontra o pedido na lista', async ({
  page,
}) => {
  const state = await mockApi(page, broker);
  await page.addInitScript(() => sessionStorage.setItem('moseguros.staff.rt', 'refresh-1'));

  await page.goto('/mediador/cotacoes');
  await expect(page.getByText('Ainda não há cotações.')).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('link', { name: 'Nova cotação' }).first().click();

  // Cliente.
  await expect(page).toHaveURL(/\/mediador\/cotacoes\/nova$/);
  await expect(page.getByText('Passo 1 de 7 · Cliente')).toBeVisible();
  await page.getByLabel('Telemóvel do cliente').fill('841234567');
  await expect(
    page.getByText(
      'A cotação fica associada a este número. O cliente não recebe nenhuma mensagem nesta fase.',
    ),
  ).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Veículo à mão.
  await expect(page.getByText('Passo 2 de 7 · Veículo')).toBeVisible();
  await page.getByLabel('Categoria').selectOption('LIGHT_PASSENGER');
  await page.getByLabel('Uso').selectOption('PRIVATE');
  await page.getByLabel('Ano').fill('2024');
  await page.getByLabel('Província de circulação').selectOption('MAPUTO_CITY');
  await page.getByLabel('Lugares').fill('5');
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Condutor à mão.
  await expect(page.getByText('Passo 3 de 7 · Condutor')).toBeVisible();
  await page.getByLabel('Data de nascimento').fill('1991-09-09');
  await page.getByLabel('Data da carta').fill('2011-04-04');
  await page.getByLabel('B — Ligeiros').check();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Tipo, coberturas e período.
  await expect(page.getByText('Passo 4 de 7 · Tipo')).toBeVisible();
  await page.getByRole('radio', { name: /Só responsabilidade civil/ }).click();
  await page.getByRole('button', { name: 'Continuar' }).click();
  await expect(page.getByText('Passo 5 de 7 · Coberturas')).toBeVisible();
  await expect(
    page.getByRole('checkbox', { name: 'Responsabilidade civil obrigatória' }),
  ).toBeDisabled();
  await page.getByRole('button', { name: 'Continuar' }).click();
  await expect(page.getByText('Passo 6 de 7 · Início e duração')).toBeVisible();
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Resumo e "Simular seguro".
  await expect(page.getByText('Passo 7 de 7 · Resumo')).toBeVisible();
  await expect(page.getByText('+258 84 123 4567')).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Simular seguro' }).click();

  await expect(page).toHaveURL(new RegExp(`/mediador/cotacoes/${requestId}$`));
  await expect(
    page.getByText(
      'Valores para o cliente +258 84 123 4567. A proposta em nome do cliente chega numa próxima versão.',
    ),
  ).toBeVisible();
  await expect(page.getByTestId('quote-card')).toHaveCount(1);
  await expectNoHorizontalScroll(page);
  expect(state.created).toHaveLength(1);
  expect(state.created[0]).toMatchObject({
    clientMsisdn: '+258841234567',
    vehicle: {
      year: 2024,
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      seats: 5,
      provinceOfCirculation: 'MAPUTO_CITY',
      hasTracker: false,
    },
    driver: { birthDate: '1991-09-09', licenceIssueDate: '2011-04-04', licenceCategories: ['B'] },
    productType: 'MTPL_ONLY',
    coverages: [],
    durationDays: 365,
  });

  // A lista filtrada pelo telemóvel mostra o pedido.
  await page.goto('/mediador/cotacoes');
  await page.getByLabel('Telemóvel do cliente').fill('841234567');
  await page.getByRole('button', { name: 'Filtrar' }).click();
  await expect(page.getByTestId('quote-summary-card')).toHaveCount(1);
  await expect(page.getByTestId('quote-summary-card')).toContainText('+258 84 123 4567');
  expect(state.filters).toContain('+258841234567');
  await expectNoHorizontalScroll(page);
});

test('um cliente que abre /mediador/cotacoes vai para /sem-acesso', async ({ page }) => {
  await mockApi(page, customer);
  await page.addInitScript(() => {
    window.localStorage.setItem('moseguros.customer.rt', 'refresh-1');
  });
  await page.goto('/mediador/cotacoes');
  await expect(page).toHaveURL(/\/sem-acesso$/);
});
