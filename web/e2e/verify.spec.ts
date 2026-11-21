import { expect, test, type Page, type Route } from '@playwright/test';

const token = 'DEMOvalid0000000000001';

const police = {
  id: '0192f0c4-0000-7000-8000-000000000011',
  type: 'STAFF',
  roles: ['POLICE_OFFICER'],
  permissions: ['verification:police'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Agente Silva',
  email: 'agente@prm.gov.mz',
  organization: { id: '0192f0c4-0000-7000-8000-0000000000c1', displayName: 'PRM', type: 'POLICE' },
};

const valid = {
  status: 'VALID',
  checkedAt: '2027-02-10T08:31:00Z',
  plateMasked: 'ABC-***-MC',
  insurerName: 'Seguradora Demo',
  validFrom: '2027-02-10T08:31:00Z',
  validTo: '2028-02-09T21:59:00Z',
  coverageType: 'MTPL_ONLY',
};

/** API simulada da verificação; `plates` decide a resposta de cada matrícula. */
async function mockApi(p: Page) {
  const state = {
    publicQueries: [] as { plate: string | null; captcha: string | undefined }[],
    policeQueries: [] as string[],
  };
  const json = (route: Route, status: number, body?: unknown, headers?: Record<string, string>) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      headers,
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string, extra: object = {}) =>
    json(route, status, { type: 'about:blank', title: code, status, code, ...extra });

  await p.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const url = new URL(request.url());
    const key = `${request.method()} ${url.pathname}`;

    if (key === 'GET /api/v1/public/verifications/config') {
      return json(route, 200, { captchaProvider: 'NONE' });
    }
    if (key === 'GET /api/v1/public/verifications') {
      const plate = url.searchParams.get('plate');
      state.publicQueries.push({ plate, captcha: request.headers()['x-captcha-token'] });
      if (plate === 'XYZ-999-MP')
        return json(route, 200, { status: 'NOT_FOUND', checkedAt: valid.checkedAt });
      if (plate === 'AAA-000-MC') {
        return problem(route, 429, 'RATE_LIMITED', { retryAfterSeconds: 30 });
      }
      return json(route, 200, valid);
    }
    if (key === `GET /api/v1/public/verifications/${token}`) {
      return json(route, 200, { ...valid, certificateStatus: 'CURRENT' });
    }

    if (key === 'POST /api/v1/auth/login' || key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, police);
    if (key === 'GET /api/v1/police/verifications') {
      state.policeQueries.push(url.search);
      return json(route, 200, {
        ...valid,
        policyNumber: 'AP-2027-900001',
        certificateNumber: 'CT-2027-000001',
        vehicleMake: 'Toyota',
        vehicleModel: 'Hilux',
      });
    }
    if (key === 'GET /api/v1/police/check-history') {
      return json(route, 200, {
        content: [
          {
            id: 'c1',
            checkedAt: '2027-02-10T08:31:00Z',
            subjectType: 'PLATE',
            plateMasked: 'ABC-***-MC',
            result: 'VALID',
          },
        ],
        page: 0,
        size: 20,
        totalElements: 1,
      });
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

async function expectNoHorizontalScroll(p: Page) {
  const overflow = await p.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

test.describe('verificação do seguro (360 px)', () => {
  test.use({ viewport: { width: 360, height: 740 } });
  test.setTimeout(90_000);

  test('verifica uma matrícula com CAPTCHA NONE e vê "Seguro válido"', async ({ page: p }) => {
    const state = await mockApi(p);
    await p.goto('/verificar');
    await expect(p.getByRole('heading', { name: 'Verificar seguro automóvel' })).toBeVisible();
    await p.getByLabel('Matrícula').fill('abc-123-mc');
    await expect(p.getByLabel('Matrícula')).toHaveValue('ABC-123-MC');
    await p.getByRole('button', { name: 'Verificar' }).click();
    await expect(p.getByRole('heading', { name: 'Seguro válido' })).toBeVisible();
    await expect(p.getByText('Válido de 10/02/2027 10:31 a 09/02/2028 23:59')).toBeVisible();
    expect(state.publicQueries[0]).toEqual({ plate: 'ABC-123-MC', captcha: undefined });
    await expectNoHorizontalScroll(p);

    await p.getByRole('button', { name: 'Nova verificação' }).click();
    await p.getByLabel('Matrícula').fill('XYZ-999-MP');
    await p.getByRole('button', { name: 'Verificar' }).click();
    await expect(
      p.getByRole('heading', { name: 'Sem seguro registado na MoSeguros' }),
    ).toBeVisible();
    await expect(
      p.getByText(/Não encontrámos nenhum seguro para esta matrícula na MoSeguros/),
    ).toBeVisible();
  });

  test('RATE_LIMITED mostra a contagem', async ({ page: p }) => {
    await mockApi(p);
    await p.goto('/verificar');
    await p.getByLabel('Matrícula').fill('AAA-000-MC');
    await p.getByRole('button', { name: 'Verificar' }).click();
    await expect(
      p.getByText(/Demasiados pedidos\. Tente de novo dentro de \d+ segundos\./),
    ).toBeVisible();
    await expect(p.getByRole('button', { name: 'Verificar' })).toBeDisabled();
  });

  test('/v/<token> mostra o resultado', async ({ page: p }) => {
    await mockApi(p);
    await p.goto(`/v/${token}`);
    await expect(p.getByRole('heading', { name: 'Seguro válido' })).toBeVisible();
    await expect(p.getByText('Seguradora Demo')).toBeVisible();
    await expect(p.getByRole('link', { name: 'Verificar outra matrícula' })).toHaveAttribute(
      'href',
      '/verificar',
    );
    await expectNoHorizontalScroll(p);
  });

  test('o agente da polícia vê os três separadores e pesquisa sem CAPTCHA', async ({ page: p }) => {
    const state = await mockApi(p);
    await p.goto(`/entrar?voltar=${encodeURIComponent('/verificar')}`);
    await p.getByLabel('E-mail').fill(police.email);
    await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
    await p.getByLabel('Código da aplicação de autenticação').fill('123456');
    await p.getByRole('button', { name: 'Entrar' }).click();
    await expect(p).toHaveURL(/\/verificar$/);
    await expect(p.getByRole('tab', { name: 'Matrícula' })).toBeVisible();
    await expect(p.getByRole('tab', { name: 'Ler QR' })).toBeVisible();
    await expect(p.getByRole('tab', { name: 'Histórico' })).toBeVisible();
    await expect(p.getByRole('button', { name: 'Menu da conta' })).toBeVisible();
    await p.getByLabel('Matrícula').fill('ABC-123-MC');
    await p.getByRole('button', { name: 'Verificar' }).click();
    await expect(p.getByText('AP-2027-900001')).toBeVisible();
    await expect(p.getByText('Toyota Hilux')).toBeVisible();
    expect(state.policeQueries).toEqual(['?plate=ABC-123-MC']);
    expect(state.publicQueries).toEqual([]);

    await p.getByRole('tab', { name: 'Histórico' }).click();
    await p.getByRole('link', { name: 'Ver o meu histórico de verificações' }).click();
    await expect(p).toHaveURL(/\/verificar\/historico$/);
    await expect(p.getByText('Matrícula · ABC-***-MC')).toBeVisible();
    await expectNoHorizontalScroll(p);
  });

  test('/verificar/historico sem sessão vai para /entrar?voltar=…', async ({ page: p }) => {
    await mockApi(p);
    await p.goto('/verificar/historico');
    await expect(p).toHaveURL(/\/entrar\?voltar=%2Fverificar%2Fhistorico$/);
  });
});
