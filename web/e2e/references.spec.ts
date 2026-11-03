import { expect, test, type Page, type Route } from '@playwright/test';

const admin = {
  id: '0192f0c4-0000-7000-8000-000000000001',
  type: 'STAFF',
  roles: ['PLATFORM_ADMIN'],
  permissions: ['organization:read', 'reference:manage'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Rita Admin',
  email: 'admin@moseguros.co.mz',
};

type Json = Record<string, unknown>;

/** API simulada com estado: valores de referência, formatos de matrícula e parâmetros. */
async function mockApi(p: Page) {
  let next = 1;
  const id = () => `0192f0c4-0000-7000-8000-${String(next++).padStart(12, '0')}`;
  const state = {
    values: [
      {
        id: id(),
        domain: 'VEHICLE_MAKE',
        code: 'TOYOTA',
        labelPt: 'Toyota',
        sortOrder: 100,
        active: true,
        version: 0,
        updatedAt: '2026-11-01T08:00:00Z',
      },
    ] as Json[],
    formats: [
      {
        id: id(),
        code: 'MZ_STANDARD',
        label: 'Moçambique (actual)',
        pattern: '^(?<l1>[A-Z]{3})(?<n>\\d{3})(?<l2>[A-Z]{2})$',
        displayTemplate: '{l1}-{n}-{l2}',
        example: 'AAA-123-MC',
        sortOrder: 10,
        active: true,
        version: 0,
      },
    ] as Json[],
    setting: {
      key: 'MTPL_MIN_CAPITAL',
      value: '300000000',
      valueType: 'MONEY',
      version: 1,
      updatedAt: '2026-11-01T08:00:00Z',
      updatedBy: 'admin@moseguros.co.mz',
    },
    created: [] as unknown[],
    tests: [] as unknown[],
    settingUpdates: [] as unknown[],
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string, extra = {}) =>
    json(route, status, { type: 'about:blank', title: code, status, code, ...extra });

  await p.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const url = new URL(request.url());
    const { pathname } = url;
    const method = request.method();
    const key = `${method} ${pathname}`;

    if (key === 'POST /api/v1/auth/login' || key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, admin);

    const values =
      /^\/api\/v1\/admin\/reference-domains\/([A-Z_]+)\/values(?:\/([^/]+))?(?:\/(activate|deactivate))?$/.exec(
        pathname,
      );
    if (values) {
      const [, domain, valueId, action] = values;
      if (method === 'GET' && !valueId) {
        const parent = url.searchParams.get('parentCode');
        const rows = state.values.filter(
          (v) => v.domain === domain && (!parent || v.parentCode === parent),
        );
        return json(route, 200, { content: rows, page: 0, size: 20, totalElements: rows.length });
      }
      if (method === 'POST' && !valueId) {
        const body = request.postDataJSON() as Json;
        state.created.push({ domain, ...body });
        const value = {
          ...body,
          id: id(),
          domain,
          active: true,
          version: 0,
          updatedAt: '2026-11-03T08:00:00Z',
        };
        state.values.push(value);
        return json(route, 201, value);
      }
      const value = state.values.find((v) => v.id === valueId);
      if (!value) return problem(route, 404, 'REFERENCE_VALUE_NOT_FOUND');
      if (action) {
        value.active = action === 'activate';
        return json(route, 200, value);
      }
    }

    if (key === 'GET /api/v1/admin/plate-formats') {
      return json(route, 200, { formats: state.formats });
    }
    if (key === 'POST /api/v1/admin/plate-formats/test') {
      const body = request.postDataJSON() as {
        pattern: string;
        displayTemplate: string;
        value: string;
      };
      state.tests.push(body);
      const normalized = body.value.toUpperCase().replace(/[\s-]/g, '');
      // O padrão de Java com grupos com nome também é válido em JavaScript.
      const match = new RegExp(body.pattern).exec(normalized);
      if (!match) return json(route, 200, { matches: false });
      const formatted = body.displayTemplate.replace(
        /\{(\w+)\}/g,
        (_, group: string) => match.groups?.[group] ?? '',
      );
      return json(route, 200, { matches: true, formatted });
    }
    if (key === 'POST /api/v1/admin/plate-formats') {
      const body = request.postDataJSON() as Json;
      state.created.push(body);
      const format = { ...body, id: id(), active: true, version: 0 };
      state.formats.push(format);
      return json(route, 201, format);
    }
    const formatAction = /^\/api\/v1\/admin\/plate-formats\/([^/]+)\/(activate|deactivate)$/.exec(
      pathname,
    );
    if (formatAction && method === 'POST') {
      const format = state.formats.find((f) => f.id === formatAction[1]);
      if (!format) return problem(route, 404, 'PLATE_FORMAT_NOT_FOUND');
      if (formatAction[2] === 'deactivate') {
        if (state.formats.filter((f) => f.active).length <= 1) {
          return problem(route, 409, 'LAST_ACTIVE_PLATE_FORMAT');
        }
        format.active = false;
      } else {
        format.active = true;
      }
      return json(route, 200, format);
    }

    if (key === 'GET /api/v1/admin/platform-settings') {
      return json(route, 200, { settings: [state.setting] });
    }
    if (key === 'PUT /api/v1/admin/platform-settings/MTPL_MIN_CAPITAL') {
      const body = request.postDataJSON() as { value: string; version: number };
      state.settingUpdates.push(body);
      if (body.version !== state.setting.version)
        return problem(route, 409, 'CONCURRENT_MODIFICATION');
      state.setting = {
        ...state.setting,
        value: body.value,
        version: state.setting.version + 1,
        updatedAt: '2026-11-03T09:00:00Z',
      };
      return json(route, 200, state.setting);
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

async function signIn(p: Page) {
  await p.goto('/entrar?voltar=%2Fadmin%2Freferencias');
  await p.getByLabel('E-mail').fill(admin.email);
  await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
  await p.getByLabel('Código da aplicação de autenticação').fill('123456');
  await p.getByRole('button', { name: 'Entrar' }).click();
  await expect(p).toHaveURL(/\/admin\/referencias$/);
  await expect(p.getByRole('heading', { level: 1, name: 'Tabelas de referência' })).toBeVisible();
}

test.describe('tabelas de referência', () => {
  test('marcas, modelos, formatos de matrícula e parâmetros', async ({ page: p }) => {
    const state = await mockApi(p);
    await signIn(p);

    // Marca TATA.
    await expect(p.getByRole('tab', { name: 'Marcas' })).toHaveAttribute('aria-selected', 'true');
    await p.getByRole('button', { name: 'Novo valor' }).click();
    let dialog = p.getByRole('dialog', { name: 'Novo valor' });
    await dialog.getByLabel('Código').fill('tata');
    await dialog.getByLabel('Nome (pt)').fill('Tata');
    await dialog.getByRole('button', { name: 'Guardar' }).click();
    await expect(dialog).toBeHidden();
    await expect(p.getByRole('gridcell', { name: 'TATA', exact: true })).toBeVisible();

    // Modelo TATA_ACE da marca TATA.
    await p.getByRole('tab', { name: 'Modelos' }).click();
    await expect(p).toHaveURL(/separador=modelos$/);
    await p.getByRole('button', { name: 'Novo valor' }).click();
    dialog = p.getByRole('dialog', { name: 'Novo valor' });
    await dialog.getByLabel('Código').fill('TATA_ACE');
    await dialog.getByLabel('Marca').selectOption({ label: 'Tata' });
    await dialog.getByLabel('Nome (pt)').fill('Ace');
    await dialog.getByRole('button', { name: 'Guardar' }).click();
    await expect(dialog).toBeHidden();
    await expect(p.getByRole('gridcell', { name: 'TATA_ACE', exact: true })).toBeVisible();
    expect(state.created).toEqual([
      { domain: 'VEHICLE_MAKE', code: 'TATA', labelPt: 'Tata', sortOrder: 100 },
      {
        domain: 'VEHICLE_MODEL',
        code: 'TATA_ACE',
        labelPt: 'Ace',
        sortOrder: 100,
        parentCode: 'TATA',
      },
    ]);

    // Formato de matrícula com teste.
    await p.getByRole('tab', { name: 'Formatos de matrícula' }).click();
    await p.getByRole('button', { name: 'Novo formato' }).click();
    dialog = p.getByRole('dialog', { name: 'Novo formato de matrícula' });
    await dialog.getByLabel('Código').fill('TEST2');
    await dialog.getByLabel('Nome').fill('Teste de duas letras');
    await dialog.getByLabel('Padrão').fill('^(?<l>[A-Z]{2})(?<n>\\d{4})$');
    await dialog.getByLabel('Modelo de apresentação').fill('{l}-{n}');
    await dialog.getByLabel('Exemplo').fill('AB-1234');
    await dialog.getByLabel('Matrícula').pressSequentially('ab 1234');
    await expect(dialog.getByText('Corresponde: AB-1234')).toBeVisible();
    expect(state.tests).toHaveLength(1);
    await dialog.getByRole('button', { name: 'Guardar' }).click();
    await expect(dialog).toBeHidden();
    await expect(p.getByRole('gridcell', { name: 'TEST2', exact: true })).toBeVisible();

    // Desactivar o novo e depois o último activo.
    await p.getByRole('button', { name: 'Acções para TEST2' }).click();
    await p.getByRole('menuitem', { name: 'Desactivar' }).click();
    await expect(p.getByRole('row').filter({ hasText: 'TEST2' })).toContainText('Desactivado');
    await p.getByRole('button', { name: 'Acções para MZ_STANDARD' }).click();
    await p.getByRole('menuitem', { name: 'Desactivar' }).click();
    await expect(
      p.getByRole('alert').filter({ hasText: 'Tem de ficar pelo menos um formato' }),
    ).toHaveText('Tem de ficar pelo menos um formato de matrícula activo.');

    // MTPL_MIN_CAPITAL.
    await p.getByRole('tab', { name: 'Parâmetros' }).click();
    const setting = p.getByRole('region', {
      name: 'Capital mínimo da responsabilidade civil obrigatória',
    });
    await expect(setting).toContainText('Os produtos já publicados não mudam.');
    await setting.getByLabel('Valor').fill('3 500 000');
    await setting.getByRole('button', { name: 'Guardar' }).click();
    await expect.poll(() => state.settingUpdates).toEqual([{ value: '350000000', version: 1 }]);
  });

  test.describe('a 360×740', () => {
    test.use({ viewport: { width: 360, height: 740 } });

    test('os separadores cabem no ecrã', async ({ page: p }) => {
      await mockApi(p);
      await signIn(p);
      for (const tab of [
        'Marcas',
        'Modelos',
        'Categorias de carta',
        'Formatos de matrícula',
        'Parâmetros',
      ]) {
        await p.getByRole('tab', { name: tab }).click();
        await expect(p.getByRole('tab', { name: tab })).toHaveAttribute('aria-selected', 'true');
        await expectNoHorizontalScroll(p);
      }
    });
  });
});
