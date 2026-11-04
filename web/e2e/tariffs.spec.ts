import { readFileSync } from 'node:fs';
import { expect, test, type Page, type Route } from '@playwright/test';

const orgId = '0192f0c4-0000-7000-8000-0000000000a1';
const productId = '0192f0c4-0000-7000-8000-000000000201';
const versionId = '0192f0c4-0000-7000-8000-000000000211';

type Json = Record<string, unknown>;

const tariffDp = JSON.parse(
  readFileSync('src/features/tariffs/fixtures/tariff-dp.json', 'utf8'),
) as { coverages: Json[] };
const [mtplDp, vtplDp] = tariffDp.coverages as [Json, Json];

/** Tarifa inicial: a do `DEMO-RC`, com a MTPL sem factores. */
function initialRating(): Json {
  return {
    schemaVersion: 'rating-v1',
    coverages: [{ coverageCode: 'MTPL', base: mtplDp.base }, vtplDp],
  };
}

const manager = {
  id: '0192f0c4-0000-7000-8000-000000000003',
  type: 'STAFF',
  roles: ['PRODUCT_MANAGER'],
  permissions: ['product:read', 'product:manage', 'rating:manage'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Gestor de Produto',
  email: 'gestor@demo.local',
  organization: { id: orgId, displayName: 'Seguradora Demo', type: 'INSURER' },
};

const version = {
  id: versionId,
  productId,
  productCode: 'DEMO-RC',
  productName: 'RC Demo',
  productType: 'MTPL_ONLY',
  versionNo: 2,
  status: 'DRAFT',
  vehicleCategories: ['LIGHT_PASSENGER'],
  usages: ['PRIVATE'],
  coverages: [],
  documents: [],
  editable: true,
  version: 4,
  createdAt: '2026-11-03T08:00:00Z',
  updatedAt: '2026-11-03T08:00:00Z',
};

const ratingCoverages = [
  {
    code: 'MTPL',
    mandatory: true,
    capitalType: 'FIXED',
    capital: { amount: 300000000, currency: 'MZN' },
    capitalOptions: null,
  },
  {
    code: 'VTPL',
    mandatory: false,
    capitalType: 'OPTIONS',
    capital: null,
    capitalOptions: [
      { amount: 500000000, currency: 'MZN' },
      { amount: 1000000000, currency: 'MZN' },
    ],
  },
];

/** Resposta do simulador para o caso C01 de `S3-rating-cases.md`. */
const c01 = {
  status: 'OFFERED',
  unavailableReason: null,
  unavailableDetail: null,
  breakdown: {
    provider: 'INTERNAL',
    variables: {
      vehicleCategory: 'LIGHT_PASSENGER',
      vehicleUsage: 'PRIVATE',
      province: 'GAZA',
      hasTracker: false,
      vehicleAgeYears: 9,
      driverAgeYears: 40,
      licenceYears: 10,
      durationDays: 365,
    },
    durationFactor: '1',
    coverages: [
      {
        code: 'MTPL',
        capitalAmount: 300000000,
        deductible: { type: 'NONE' },
        base: {
          type: 'AMOUNT',
          rowIndex: 0,
          rowLabel: 'Ligeiro de passageiros, particular',
          value: 450000,
          amount: '450000',
        },
        factors: [
          {
            code: 'DRIVER_AGE',
            label: 'Idade do condutor',
            rowIndex: 1,
            rowLabel: null,
            value: '1.00',
          },
          {
            code: 'LICENCE_YEARS',
            label: 'Antiguidade da carta',
            rowIndex: 1,
            rowLabel: null,
            value: '1.00',
          },
          {
            code: 'PROVINCE',
            label: 'Província de circulação',
            rowIndex: 1,
            rowLabel: null,
            value: '1.00',
          },
        ],
        bonusMalusFactor: '1',
        exactPremium: '450000.000000',
        premiumAmount: 450000,
      },
    ],
    commercialPremiumAmount: 450000,
    charges: [
      {
        code: 'DEMO_STAMP',
        labelPt: 'Imposto de selo (provisório)',
        labelEn: 'Stamp duty (provisional)',
        type: 'PERCENT',
        value: '2.5',
        amount: 11250,
      },
      {
        code: 'DEMO_FEE',
        labelPt: 'Taxa fixa (provisória)',
        labelEn: 'Fixed fee (provisional)',
        type: 'FIXED',
        value: 5000,
        amount: 5000,
      },
    ],
    subtotalAmount: 466250,
    roundingAdjustmentAmount: 50,
    totalAmount: 466300,
  },
};

interface Options {
  /** Erros que o `PUT` da tarifa devolve (400). */
  putErrors?: { field: string; code: string }[];
}

/** API simulada: produto, versão, tarifa e simulador. */
async function mockApi(p: Page, options: Options = {}) {
  const state = {
    rating: initialRating(),
    ratingVersion: 7,
    puts: [] as Json[],
    simulations: [] as Json[],
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string, extra = {}) =>
    json(route, status, { type: 'about:blank', title: code, status, code, ...extra });
  const rules = () => ({
    productVersionId: versionId,
    productType: 'MTPL_ONLY',
    editable: true,
    configured: true,
    rating: state.rating,
    coverages: ratingCoverages,
    version: state.ratingVersion,
  });
  const ref = { id: versionId, versionNo: 2, status: 'DRAFT' };

  await p.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const { pathname } = new URL(request.url());
    const key = `${request.method()} ${pathname}`;

    if (key === 'POST /api/v1/auth/login' || key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, manager);
    if (key === 'GET /api/v1/insurer/rating-settings') {
      return json(route, 200, { ratingMode: 'INTERNAL', charges: [], version: 1 });
    }
    if (key === 'GET /api/v1/insurer/products') {
      const content = [
        {
          id: productId,
          code: 'DEMO-RC',
          name: 'RC Demo',
          type: 'MTPL_ONLY',
          status: 'PUBLISHED',
          currentVersionNo: 1,
          hasDraft: true,
          updatedAt: '2026-11-03T08:00:00Z',
        },
      ];
      return json(route, 200, { content, page: 0, size: 100, totalElements: 1 });
    }
    if (key === `GET /api/v1/insurer/products/${productId}`) {
      return json(route, 200, {
        id: productId,
        code: 'DEMO-RC',
        name: 'RC Demo',
        type: 'MTPL_ONLY',
        status: 'PUBLISHED',
        draftVersion: ref,
        versions: [ref],
        version: 1,
        createdAt: '2026-11-03T08:00:00Z',
        updatedAt: '2026-11-03T08:00:00Z',
      });
    }
    if (key === `GET /api/v1/insurer/product-versions/${versionId}`) {
      return json(route, 200, version);
    }
    if (key === `GET /api/v1/insurer/product-versions/${versionId}/rating`) {
      return json(route, 200, rules());
    }
    if (key === `PUT /api/v1/insurer/product-versions/${versionId}/rating`) {
      const body = request.postDataJSON() as Json;
      state.puts.push(body);
      if (options.putErrors) {
        return problem(route, 400, 'VALIDATION_ERROR', { errors: options.putErrors });
      }
      state.rating = body.rating as Json;
      state.ratingVersion += 1;
      return json(route, 200, rules());
    }
    if (key === `POST /api/v1/insurer/product-versions/${versionId}/rating/validate`) {
      return json(route, 200, { valid: true, errors: [] });
    }
    if (key === `POST /api/v1/insurer/product-versions/${versionId}/rating/simulate`) {
      state.simulations.push(request.postDataJSON() as Json);
      return json(route, 200, c01);
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

async function signIn(p: Page, path: string) {
  await p.goto(`/entrar?voltar=${encodeURIComponent(path)}`);
  await p.getByLabel('E-mail').fill(manager.email);
  await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
  await p.getByLabel('Código da aplicação de autenticação').fill('123456');
  await p.getByRole('button', { name: 'Entrar' }).click();
  await expect(p).toHaveURL(new RegExp(`${path.replace(/[/?]/g, '\\$&')}$`));
}

async function expectNoHorizontalScroll(p: Page) {
  const overflow = await p.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

async function runC01(p: Page) {
  await p.getByRole('tab', { name: 'Simulador' }).click();
  await p.getByRole('combobox', { name: 'Província de circulação' }).click();
  await p.getByRole('option', { name: 'Gaza' }).click();
  await p.getByLabel('Ano').fill('2018');
  await p.getByLabel('Data de nascimento').fill('1986-06-01');
  await p.getByLabel('Data da carta').fill('2016-03-01');
  await p.getByLabel('Início').fill('2027-01-15');
  await p.getByRole('button', { name: 'Calcular' }).click();
}

test.describe('tarifas', () => {
  test('acrescentar o factor "Idade do condutor" à MTPL e guardar', async ({ page: p }) => {
    const state = await mockApi(p);
    await signIn(p, '/seguradora/tarifas');
    await p.getByRole('button', { name: /RC Demo/ }).click();
    await p.getByRole('link', { name: 'Editar tarifa' }).click();
    await expect(p).toHaveURL(new RegExp(`/seguradora/tarifas/${versionId}$`));
    await expect(
      p.getByRole('heading', { level: 2, name: 'Responsabilidade civil obrigatória' }),
    ).toBeVisible();

    await p.getByRole('button', { name: 'Acrescentar factor' }).click();
    await p.getByRole('menuitem', { name: 'Idade do condutor' }).click();
    const factor = p.getByRole('region', { name: 'Idade do condutor' });
    await factor.getByRole('button', { name: 'Acrescentar linha' }).click();
    const dialog = p.getByRole('dialog', { name: 'Nova linha' });
    await dialog
      .getByRole('group', { name: 'Idade do condutor (anos)' })
      .getByLabel('Menos de (<)')
      .fill('25');
    await dialog.getByLabel('Factor').fill('1,30');
    await dialog.getByRole('button', { name: 'Aplicar' }).click();
    await expect(factor.getByText('Idade do condutor (anos): menos de 25')).toBeVisible();
    await expect(factor.getByText('× 1,30')).toBeVisible();

    await p.getByRole('button', { name: 'Guardar' }).click();
    await expect(p.getByText('Tarifa guardada')).toBeVisible();
    expect(state.puts).toHaveLength(1);
    const body = state.puts[0] as { rating: { coverages: Json[] }; version: number };
    expect(body.version).toBe(7);
    expect(body.rating.coverages[0]!.factors).toEqual([
      {
        code: 'DRIVER_AGE',
        label: 'Idade do condutor',
        rows: [
          { when: { driverAgeYears: { lt: 25 } }, value: '1.30' },
          { when: {}, value: '1' },
        ],
      },
    ]);
  });

  test('erro 400 do servidor destaca a linha 1 da base da MTPL', async ({ page: p }) => {
    await mockApi(p, {
      putErrors: [{ field: '/coverages/0/base/rows/0/value', code: 'VALUE_OUT_OF_RANGE' }],
    });
    await signIn(p, `/seguradora/tarifas/${versionId}`);
    await p.getByRole('button', { name: 'Guardar' }).click();
    const panel = p.getByTestId('tariff-errors');
    await expect(panel).toContainText('Valor fora dos limites permitidos. (MTPL › Base › linha 1)');
    const row = p.getByTestId('row-0-base-0');
    await expect(row).toHaveAttribute('data-highlighted', 'true');
    await expect(row).toHaveAttribute('data-error', 'true');
    await expect(p.getByTestId('row-0-base-1')).not.toHaveAttribute('data-error', 'true');
  });

  test('simulador com o caso C01: total e decomposição', async ({ page: p }) => {
    const state = await mockApi(p);
    await signIn(p, `/seguradora/tarifas/${versionId}`);
    await runC01(p);
    const total = await p.evaluate(
      () =>
        `${new Intl.NumberFormat('pt-MZ', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(4663)} MT`,
    );
    await expect(p.getByTestId('simulation-total')).toContainText(total);
    const breakdown = p.getByTestId('breakdown');
    await expect(breakdown).toContainText('Idade do condutor: 40 anos');
    await expect(breakdown).toContainText('Imposto de selo (provisório)');
    await breakdown.getByRole('button', { name: /Responsabilidade civil obrigatória/ }).click();
    await expect(p.getByTestId('breakdown-MTPL')).toContainText(
      'Base · linha 1 · Ligeiro de passageiros, particular',
    );
    await expect(p.getByTestId('breakdown-MTPL')).toContainText('Prémio exacto');

    expect(state.simulations).toHaveLength(1);
    const request = state.simulations[0] as Json;
    expect(request).toMatchObject({
      vehicle: {
        category: 'LIGHT_PASSENGER',
        usage: 'PRIVATE',
        provinceOfCirculation: 'GAZA',
        year: 2018,
        hasTracker: false,
      },
      driver: { birthDate: '1986-06-01', licenceIssueDate: '2016-03-01' },
      coverages: [],
      startDate: '2027-01-15',
      durationDays: 365,
      charges: null,
    });
    expect(request.rating).toEqual(state.rating);
  });

  test('CSV com uma categoria inválida: erro com a linha e a tabela fica igual', async ({
    page: p,
  }) => {
    await mockApi(p);
    await signIn(p, `/seguradora/tarifas/${versionId}`);
    const base = p.getByTestId('table-0-base');
    await expect(base.getByTestId(/^row-0-base-\d+$/)).toHaveCount(5);
    const header = 'label;vehicleCategory;value';
    await p.getByTestId('csv-input-0-base').setInputFiles({
      name: 'base.csv',
      mimeType: 'text/csv',
      buffer: Buffer.from(`${header}\r\nCarro;LIGHT_PASSENGER;4500\r\nOutro;CARRO;3000\r\n`),
    });
    await expect(p.getByTestId('csv-errors')).toContainText(
      'Linha 3, coluna vehicleCategory: código desconhecido "CARRO".',
    );
    await p.getByRole('button', { name: 'Fechar' }).click();
    await expect(base.getByTestId(/^row-0-base-\d+$/)).toHaveCount(5);
    await expect(base).toContainText('Ligeiro de passageiros, particular');
  });

  test.describe('ecrã de 360 px', () => {
    test.use({ viewport: { width: 360, height: 740 } });

    test('o editor e o simulador são utilizáveis sem scroll horizontal', async ({ page: p }) => {
      await mockApi(p);
      await signIn(p, `/seguradora/tarifas/${versionId}`);
      await expect(p.getByRole('combobox', { name: 'Cobertura' })).toBeVisible();
      await expect(p.getByTestId('row-0-base-0')).toBeVisible();
      await expectNoHorizontalScroll(p);

      await p.getByRole('button', { name: 'Acções da linha 1' }).click();
      await p.getByRole('menuitem', { name: 'Editar' }).click();
      await expect(p.getByRole('dialog', { name: 'Editar a linha 1' })).toBeVisible();
      await p.getByRole('button', { name: 'Cancelar' }).click();

      await runC01(p);
      await expect(p.getByTestId('simulation-total')).toBeVisible();
      await expectNoHorizontalScroll(p);
    });
  });
});
