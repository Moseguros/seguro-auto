import { expect, test, type Page, type Route } from '@playwright/test';

test.use({ viewport: { width: 360, height: 740 } });

const vehicleId = '0193a000-0000-7000-8000-000000000101';
const driverId = '0193a000-0000-7000-8000-000000000201';
const requestId = '0193a000-0000-7000-8000-000000000501';
const preContractFileId = '0193a000-0000-7000-8000-000000000601';

const me = {
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

const vehicle = {
  id: vehicleId,
  plate: 'AAA-100-MC',
  chassisNumber: 'JTDBR32E720012345',
  make: 'Toyota',
  model: 'Corolla',
  year: 2018,
  engineCc: 1598,
  category: 'LIGHT_PASSENGER',
  seats: 5,
  usage: 'PRIVATE',
  provinceOfCirculation: 'MAPUTO_CITY',
  commercialValue: mzn(123456789),
  hasTracker: false,
  inatroVerified: true,
  drivers: [{ driverId, fullName: 'Ana Maria Mussa', habitual: true }],
  version: 3,
  createdAt: '2026-11-03T08:00:00Z',
  updatedAt: '2026-11-03T08:00:00Z',
};

const driver = {
  id: driverId,
  fullName: 'Ana Maria Mussa',
  birthDate: '1991-09-09',
  licenceNumberMasked: '****4567',
  licenceCategories: ['B'],
  licenceIssueDate: '2011-04-04',
  isOwner: true,
  vehicleIds: [vehicleId],
  version: 0,
  createdAt: '2026-11-03T08:00:00Z',
  updatedAt: '2026-11-03T08:00:00Z',
};

const coverage = (code: string, extra: Record<string, unknown> = {}) => ({
  code,
  mandatory: false,
  capitalType: 'VEHICLE_VALUE',
  deductibleType: 'PERCENT',
  deductiblePercent: 10,
  deductibleMinAmount: mzn(2500000),
  ...extra,
});

const catalogProduct = (id: string, insurer: string, name: string) => ({
  productId: `${id}-p`,
  productVersionId: `${id}-v`,
  code: id.toUpperCase(),
  name,
  type: 'COMPREHENSIVE',
  status: 'PUBLISHED',
  insurer: { id: `${id}-i`, displayName: insurer },
  usages: ['PRIVATE'],
  vehicleCategories: ['LIGHT_PASSENGER'],
  validFrom: '2026-01-01',
  versionNo: 1,
  documents: [],
  coverages: [
    coverage('MTPL', {
      mandatory: true,
      capitalType: 'FIXED',
      capital: mzn(300000000),
      deductibleType: 'NONE',
    }),
    coverage('OD_COLLISION'),
    coverage('OD_FIRE'),
    coverage('OD_NATURE'),
  ],
});

const quote = (
  id: string,
  insurer: { id: string; displayName: string; shortCode: string },
  total: number,
  preContractInfoFileId: string,
) => ({
  id,
  status: 'OFFERED',
  insurer: { ...insurer, logoFileId: null },
  product: {
    id: `${id}-p`,
    code: `${insurer.shortCode}-AUTO`,
    name: `Auto ${insurer.shortCode}`,
    type: 'COMPREHENSIVE',
    versionId: `${id}-v`,
    versionNo: 1,
    generalConditionsFileId: `${preContractInfoFileId.slice(0, -1)}9`,
    preContractInfoFileId,
  },
  coverages: [
    {
      code: 'MTPL',
      mandatory: true,
      capital: mzn(300000000),
      deductible: { type: 'NONE' },
      premium: mzn(495000),
    },
    {
      code: 'OD_COLLISION',
      mandatory: false,
      capital: mzn(123456789),
      deductible: { type: 'PERCENT', percent: '10', minAmount: mzn(2500000) },
      premium: mzn(total - 495000 - 127434),
    },
  ],
  commercialPremium: mzn(total - 127434),
  charges: [
    { code: 'STAMP', labelPt: 'Imposto de selo', labelEn: 'Stamp duty', amount: mzn(127344) },
  ],
  roundingAdjustment: mzn(90),
  totalAmount: mzn(total),
  validUntil: '2027-01-25T08:15:00Z',
});

const pageOf = (content: unknown[]) => ({
  content,
  page: 0,
  size: 50,
  totalElements: content.length,
});

/** PDF mínimo para a informação pré-contratual. */
const pdf = Buffer.from('%PDF-1.4\n%%EOF\n');

async function mockApi(page: Page) {
  const state = { created: [] as Record<string, unknown>[], files: [] as string[] };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string) =>
    json(route, status, { type: 'about:blank', title: code, status, code });

  const request = () => ({
    id: requestId,
    origin: 'CUSTOMER',
    clientMsisdn: null,
    createdAt: '2027-01-10T08:15:00Z',
    productType: 'COMPREHENSIVE',
    vehicle: { ...vehicle, vehicleId, id: undefined },
    driver: {
      driverId,
      birthDate: driver.birthDate,
      licenceIssueDate: driver.licenceIssueDate,
      licenceCategories: ['B'],
    },
    requestedCoverages: (state.created[0]?.coverages as unknown[]) ?? [],
    startDate: (state.created[0]?.startDate as string) ?? '2027-01-15',
    endDate: '2028-01-14',
    durationDays: 365,
    quotes: [
      quote(
        'q-demo',
        { id: 'i-demo', displayName: 'Seguradora Demo', shortCode: 'DEMO' },
        5226200,
        preContractFileId,
      ),
      quote(
        'q-demob',
        { id: 'i-demob', displayName: 'Seguradora Demo B', shortCode: 'DEMOB' },
        5890000,
        '0193a000-0000-7000-8000-000000000602',
      ),
    ],
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
    if (key === 'GET /api/v1/catalog/products') {
      return json(
        route,
        200,
        pageOf([
          catalogProduct('demo', 'Seguradora Demo', 'Auto DEMO'),
          catalogProduct('demob', 'Seguradora Demo B', 'Auto DEMOB'),
        ]),
      );
    }
    if (req.headers()['authorization'] !== 'Bearer access-1') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, me);
    if (key === 'GET /api/v1/me/vehicles') return json(route, 200, pageOf([vehicle]));
    if (key === 'GET /api/v1/me/drivers') return json(route, 200, pageOf([driver]));
    if (key === 'GET /api/v1/me/quotes') return json(route, 200, pageOf([]));
    if (key === 'POST /api/v1/quotes') {
      state.created.push(req.postDataJSON() as Record<string, unknown>);
      return json(route, 201, request());
    }
    if (key === `GET /api/v1/quotes/${requestId}`) return json(route, 200, request());
    if (key.startsWith('GET /api/v1/files/')) {
      state.files.push(url.pathname);
      return route.fulfill({ status: 200, contentType: 'application/pdf', body: pdf });
    }
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

/** Dia seguinte em `Africa/Maputo` (`AAAA-MM-DD`). */
function tomorrowInMaputo(): string {
  const today = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'Africa/Maputo',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date());
  const date = new Date(`${today}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() + 1);
  return date.toISOString().slice(0, 10);
}

test('simular RC + danos próprios, comparar e abrir a informação pré-contratual', async ({
  page,
}) => {
  const state = await mockApi(page);
  await page.addInitScript(() => {
    window.localStorage.setItem('moseguros.customer.rt', 'refresh-1');
  });

  await page.goto('/cliente/simular');
  await expect(page.getByRole('heading', { level: 1, name: 'Simular seguro' })).toBeVisible();
  await expect(page.getByText('Passo 1 de 5 · Veículo')).toBeVisible();
  await expectNoHorizontalScroll(page);

  // Veículo: o condutor habitual vem escolhido.
  await page.getByRole('radio', { name: /AAA-100-MC/ }).click();
  await expect(page.getByLabel('Condutor')).toHaveValue(driverId);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Tipo.
  await expect(page.getByText('Passo 2 de 5 · Tipo')).toBeVisible();
  await page.getByRole('radio', { name: /Responsabilidade civil \+ danos próprios/ }).click();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Coberturas por defeito.
  await expect(page.getByText('Passo 3 de 5 · Coberturas')).toBeVisible();
  await expect(page.getByRole('checkbox', { name: 'Choque, colisão e capotamento' })).toBeChecked();
  await expect(page.getByRole('checkbox', { name: 'Incêndio, raio e explosão' })).toBeChecked();
  await expect(page.getByRole('checkbox', { name: 'Fenómenos da natureza' })).toBeChecked();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Data de início.
  await expect(page.getByText('Passo 4 de 5 · Início e duração')).toBeVisible();
  const start = tomorrowInMaputo();
  await page.getByLabel('Data de início').fill(start);
  await expect(page.getByText(/^Termina a /)).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Resumo.
  await expect(page.getByText('Passo 5 de 5 · Resumo')).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Simular seguro' }).click();

  // Resultados.
  await expect(page).toHaveURL(new RegExp(`/cliente/simular/${requestId}$`));
  expect(state.created).toEqual([
    {
      vehicleId,
      driverId,
      productType: 'COMPREHENSIVE',
      coverages: [{ code: 'OD_COLLISION' }, { code: 'OD_FIRE' }, { code: 'OD_NATURE' }],
      startDate: start,
      durationDays: 365,
    },
  ]);
  const cards = page.getByTestId('quote-card');
  await expect(cards).toHaveCount(2);
  const expectedTotal = await page.evaluate(
    () =>
      `${new Intl.NumberFormat('pt-MZ', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(52262)} MT`,
  );
  await expect(cards.first().getByTestId('quote-total')).toHaveText(expectedTotal);
  await expect(cards.first()).toContainText('Mais barata');
  await expect(cards.nth(1)).not.toContainText('Mais barata');
  await expectNoHorizontalScroll(page);

  await page.getByRole('checkbox', { name: /^Comparar Seguradora Demo —/ }).check();
  await page.getByRole('checkbox', { name: /^Comparar Seguradora Demo B/ }).check();
  await expect(page.getByText('2 seleccionadas (máximo 3)')).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Comparar propostas' }).click();

  // Comparação em cartões (360 px).
  await expect(page).toHaveURL(/\/comparar\?ids=q-demo,q-demob$/);
  await expect(page.getByTestId('compare-card')).toHaveCount(2);
  await expect(page.getByTestId('compare-table')).toHaveCount(0);
  await expectNoHorizontalScroll(page);
  await page.getByTestId('compare-card').first().getByRole('link', { name: 'Ver detalhe' }).click();

  // Detalhe e a informação pré-contratual.
  await expect(page).toHaveURL(new RegExp(`/cliente/simular/${requestId}/q-demo$`));
  await expect(page.getByRole('heading', { name: 'Seguradora Demo' })).toBeVisible();
  await expectNoHorizontalScroll(page);
  const fileRequest = page.waitForRequest(`**/api/v1/files/${preContractFileId}`);
  const popup = page.waitForEvent('popup');
  await page.getByRole('button', { name: 'Informação pré-contratual' }).click();
  await fileRequest;
  await (await popup).close();
  expect(state.files).toContain(`/api/v1/files/${preContractFileId}`);
});
