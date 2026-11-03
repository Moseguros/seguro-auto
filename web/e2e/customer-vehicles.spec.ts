import { expect, test, type Page, type Route } from '@playwright/test';

test.use({ viewport: { width: 360, height: 740 } });

const vehicleId = '0193a000-0000-7000-8000-000000000101';
const driverId = '0193a000-0000-7000-8000-000000000201';
const fileId = '0193a000-0000-7000-8000-000000000301';

const me = {
  id: '0192f0c4-0000-7000-8000-000000000006',
  type: 'CUSTOMER',
  roles: ['CUSTOMER'],
  permissions: ['self:profile', 'self:vehicles'],
  preferredLanguage: 'pt',
  msisdn: '+258840000001',
  msisdnVerified: true,
  emailVerified: false,
  profileComplete: true,
  displayName: 'Ana Maria Mussa',
};

const registry: Record<string, unknown> = {
  plate: 'AAA-100-MC',
  chassisNumber: 'JTDBR32E720012345',
  make: 'Toyota',
  model: 'Corolla',
  year: 2018,
  engineCc: 1598,
  category: 'LIGHT_PASSENGER',
  seats: 5,
  source: 'MOCK',
};

/** JPEG mínimo (1×1) para a miniatura. */
const jpeg = Buffer.from(
  '/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA=',
  'base64',
);

const pageOf = (content: unknown[]) => ({
  content,
  page: 0,
  size: 50,
  totalElements: content.length,
});

/** API simulada com estado: veículos e condutores criados durante o teste. */
async function mockApi(page: Page) {
  const state = {
    vehicles: [] as Record<string, unknown>[],
    drivers: [] as Record<string, unknown>[],
    created: [] as unknown[],
    driverAssignments: [] as unknown[],
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string) =>
    json(route, status, { type: 'about:blank', title: code, status, code });
  const findVehicle = (id: string) => state.vehicles.find((v) => v.id === id);

  await page.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const url = new URL(request.url());
    const { pathname } = url;
    const method = request.method();
    const key = `${method} ${pathname}`;

    if (key === 'POST /api/v1/auth/refresh') {
      const body = request.postDataJSON() as { refreshToken: string };
      if (body.refreshToken !== 'refresh-1') return problem(route, 401, 'REFRESH_TOKEN_INVALID');
      return json(route, 200, {
        accessToken: 'access-1',
        refreshToken: 'refresh-1',
        expiresIn: 900,
      });
    }
    if (key === 'GET /api/v1/plate-formats') {
      return json(route, 200, {
        formats: [
          { code: 'STANDARD', label: 'Actual', example: 'AAA-100-MC' },
          { code: 'LEGACY', label: 'Antiga', example: 'MLA-12-34' },
        ],
      });
    }
    if (key === 'GET /api/v1/reference-domains/VEHICLE_MAKE/values') {
      return json(route, 200, { values: [{ code: 'TOYOTA', labelPt: 'Toyota' }] });
    }
    if (key === 'GET /api/v1/reference-domains/VEHICLE_MODEL/values') {
      return json(route, 200, {
        values: [{ code: 'TOYOTA_COROLLA', labelPt: 'Corolla', parentCode: 'TOYOTA' }],
      });
    }
    if (key === 'GET /api/v1/reference-domains/LICENCE_CATEGORY/values') {
      return json(route, 200, {
        values: [
          { code: 'A', labelPt: 'A — Motociclos' },
          { code: 'B', labelPt: 'B — Ligeiros' },
        ],
      });
    }

    // Daqui para baixo, só com sessão.
    if (request.headers()['authorization'] !== 'Bearer access-1') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, me);
    if (key === 'GET /api/v1/me/profile') {
      return json(route, 200, {
        complete: true,
        version: 1,
        fullName: 'Ana Maria Mussa',
        birthDate: '1990-05-14',
        province: 'MAPUTO_CITY',
      });
    }
    if (key === 'GET /api/v1/me/vehicles/registry-lookup') {
      return url.searchParams.get('plate') === 'AAA-100-MC'
        ? json(route, 200, registry)
        : problem(route, 404, 'VEHICLE_REGISTRY_NOT_FOUND');
    }
    if (key === 'GET /api/v1/me/vehicles') return json(route, 200, pageOf(state.vehicles));
    if (key === 'POST /api/v1/me/vehicles') {
      const body = request.postDataJSON() as Record<string, unknown>;
      state.created.push(body);
      const vehicle = {
        ...body,
        id: vehicleId,
        plate: 'AAA-100-MC',
        inatroVerified: true,
        drivers: [],
        version: 0,
        createdAt: '2026-11-03T08:00:00Z',
        updatedAt: '2026-11-03T08:00:00Z',
      };
      state.vehicles.push(vehicle);
      return json(route, 201, vehicle);
    }
    if (key === `GET /api/v1/me/vehicles/${vehicleId}`) {
      const vehicle = findVehicle(vehicleId);
      return vehicle ? json(route, 200, vehicle) : problem(route, 404, 'VEHICLE_NOT_FOUND');
    }
    if (key === `POST /api/v1/me/vehicles/${vehicleId}/registration-doc`) {
      const vehicle = findVehicle(vehicleId)!;
      vehicle.registrationDoc = {
        fileId,
        contentType: 'image/jpeg',
        sizeBytes: jpeg.length,
        uploadedAt: '2026-11-03T08:05:00Z',
      };
      vehicle.version = (vehicle.version as number) + 1;
      return json(route, 200, vehicle);
    }
    if (key === `PUT /api/v1/me/vehicles/${vehicleId}/drivers`) {
      const vehicle = findVehicle(vehicleId)!;
      const body = request.postDataJSON() as {
        drivers: { driverId: string; habitual: boolean }[];
        version: number;
      };
      state.driverAssignments.push(body);
      if (body.version !== vehicle.version) return problem(route, 409, 'CONCURRENT_MODIFICATION');
      vehicle.drivers = body.drivers.map((assignment) => ({
        driverId: assignment.driverId,
        fullName: state.drivers.find((d) => d.id === assignment.driverId)?.fullName,
        habitual: assignment.habitual,
      }));
      vehicle.version = (vehicle.version as number) + 1;
      for (const driver of state.drivers) {
        driver.vehicleIds = body.drivers.some((d) => d.driverId === driver.id) ? [vehicleId] : [];
      }
      return json(route, 200, vehicle);
    }
    if (key === `GET /api/v1/files/${fileId}`) {
      return route.fulfill({ status: 200, contentType: 'image/jpeg', body: jpeg });
    }
    if (key === 'GET /api/v1/me/drivers') return json(route, 200, pageOf(state.drivers));
    if (key === 'POST /api/v1/me/drivers') {
      const body = request.postDataJSON() as Record<string, unknown> & { licenceNumber: string };
      const { licenceNumber, ...rest } = body;
      const driver = {
        ...rest,
        id: driverId,
        licenceNumberMasked: `****${licenceNumber.slice(-4)}`,
        vehicleIds: [],
        version: 0,
        createdAt: '2026-11-03T08:10:00Z',
        updatedAt: '2026-11-03T08:10:00Z',
      };
      state.drivers.push(driver);
      return json(route, 201, driver);
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

/** Sessão de cliente guardada: a aplicação renova-a ao abrir. */
async function signIn(page: Page) {
  await page.addInitScript(() => {
    window.localStorage.setItem('moseguros.customer.rt', 'refresh-1');
  });
}

async function expectNoHorizontalScroll(page: Page) {
  const overflow = await page.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

test('acrescentar um veículo pelo registo, enviar o livrete e escolher o condutor habitual', async ({
  page,
}) => {
  const state = await mockApi(page);
  await signIn(page);

  // Lista vazia.
  await page.goto('/cliente/veiculos');
  await expect(page.getByText('Ainda não registou nenhum veículo.')).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('link', { name: 'Acrescentar veículo' }).first().click();

  // Formulário com "Procurar no registo".
  await expect(page).toHaveURL(/\/cliente\/veiculos\/novo$/);
  await expect(page.getByText('Ex.: AAA-100-MC ou MLA-12-34')).toBeVisible();
  await page.getByLabel('Matrícula').fill('aaa-100-mc');
  await page.getByRole('button', { name: 'Procurar no registo' }).click();
  await expect(
    page.getByText(
      'Preenchemos os dados a partir do registo automóvel. Confirme-os com o seu livrete.',
    ),
  ).toBeVisible();
  await expect(page.getByLabel('N.º de chassis')).toHaveValue('JTDBR32E720012345');
  await expect(page.getByRole('combobox', { name: 'Marca' })).toHaveValue('Toyota');
  await expect(page.getByLabel('Categoria')).toHaveValue('LIGHT_PASSENGER');
  await page.getByLabel('Uso').selectOption('PRIVATE');
  await page.getByLabel('Província de circulação').selectOption('MAPUTO_CITY');
  await page.getByLabel('Valor comercial').fill('850 000');
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Guardar' }).click();

  // Detalhe: enviar a foto do livrete.
  await expect(page).toHaveURL(new RegExp(`/cliente/veiculos/${vehicleId}$`));
  expect(state.created).toEqual([
    {
      plate: 'AAA-100-MC',
      chassisNumber: 'JTDBR32E720012345',
      make: 'Toyota',
      model: 'Corolla',
      year: 2018,
      category: 'LIGHT_PASSENGER',
      engineCc: 1598,
      seats: 5,
      usage: 'PRIVATE',
      provinceOfCirculation: 'MAPUTO_CITY',
      commercialValue: { amount: 85000000, currency: 'MZN' },
      hasTracker: false,
    },
  ]);
  await expect(
    page.getByText('Veículo acrescentado. Envie agora a foto do livrete.'),
  ).toBeVisible();
  await expect(page.getByText('Ainda não enviou o livrete.')).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByTestId('registration-doc-camera').setInputFiles({
    name: 'livrete.jpg',
    mimeType: 'image/jpeg',
    buffer: jpeg,
  });
  await expect(page.getByText('Livrete guardado')).toBeVisible();
  await expect(page.getByRole('img', { name: 'Foto do livrete' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Abrir', exact: true })).toBeEnabled();

  // Ainda sem condutores: registar "Sou eu" a partir do diálogo.
  await page.getByRole('button', { name: 'Gerir condutores' }).click();
  await expect(page.getByText('Ainda não registou condutores.')).toBeVisible();
  await page.getByRole('link', { name: 'Registar condutor' }).click();
  await expect(page).toHaveURL(new RegExp(`/cliente/condutores/novo\\?veiculo=${vehicleId}$`));
  await page.getByLabel('Sou eu').check();
  await expect(page.getByLabel('Nome completo')).toHaveValue('Ana Maria Mussa');
  await expect(page.getByLabel('Data de nascimento')).toHaveValue('1990-05-14');
  await page.getByLabel('N.º da carta').fill('map 123 4567');
  await page.getByLabel('B — Ligeiros').check();
  await page.getByLabel('Data de emissão da carta').fill('2010-06-01');
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Guardar' }).click();

  // Volta ao veículo com o diálogo aberto: associar como habitual.
  await expect(page).toHaveURL(new RegExp(`/cliente/veiculos/${vehicleId}$`));
  const dialog = page.getByRole('dialog', { name: 'Condutores do veículo' });
  await expect(dialog).toBeVisible();
  await dialog.getByLabel('Ana Maria Mussa', { exact: true }).check();
  await expect(dialog.getByRole('button', { name: 'Guardar' })).toBeDisabled();
  await dialog.getByRole('radio', { name: 'Ana Maria Mussa é o condutor habitual' }).check();
  await dialog.getByRole('button', { name: 'Guardar' }).click();
  await expect(dialog).toBeHidden();
  expect(state.driverAssignments).toEqual([
    { drivers: [{ driverId, habitual: true }], version: 1 },
  ]);
  await expect(page.getByTestId('vehicle-driver')).toContainText('Habitual');
  await expectNoHorizontalScroll(page);

  // A lista mostra o veículo com o condutor habitual e sem "Livrete em falta".
  await page.getByRole('button', { name: 'Abrir menu' }).click();
  await page
    .getByRole('navigation', { name: 'Área do cliente' })
    .getByRole('link', { name: 'Veículos' })
    .click();
  await expect(page).toHaveURL(/\/cliente\/veiculos$/);
  const card = page.getByTestId('vehicle-card');
  await expect(card).toContainText('AAA-100-MC');
  await expect(card).toContainText('Condutor habitual: Ana Maria Mussa');
  await expect(card).not.toContainText('Livrete em falta');
  await expectNoHorizontalScroll(page);

  // Condutores: o mesmo item do menu continua marcado.
  await page.getByRole('tab', { name: 'Condutores' }).click();
  await expect(page).toHaveURL(/\/cliente\/condutores$/);
  await expect(page.getByTestId('driver-card')).toContainText('Carta ****4567');
  await expect(page.getByTestId('driver-card')).toContainText('1 veículo');
  await expectNoHorizontalScroll(page);
});

test('/cliente/veiculos sem sessão vai para a entrada', async ({ page }) => {
  await mockApi(page);
  await page.goto('/cliente/veiculos');
  await expect(page).toHaveURL(/\/cliente\/entrar\?voltar=%2Fcliente%2Fveiculos$/);
  await expect(page.getByRole('heading', { name: 'Entrar na área do cliente' })).toBeVisible();
});
