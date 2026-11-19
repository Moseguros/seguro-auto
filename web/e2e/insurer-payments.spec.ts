import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { expect, test, type Page, type Route } from '@playwright/test';

const orgId = '0192f0c4-0000-7000-8000-0000000000a1';
const exceptionId = '0192f0c4-0000-7000-8000-000000000901';
const candidateId = '0192f0c4-0000-7000-8000-000000000801';
const paymentId = '0192f0c4-0000-7000-8000-000000000802';

type Json = Record<string, unknown>;

const insurerStaff = (roles: string[], permissions: string[], name: string, email: string) => ({
  id: `0192f0c4-0000-7000-8000-${email.length.toString().padStart(12, '0')}`,
  type: 'STAFF',
  roles,
  permissions,
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: name,
  email,
  organization: { id: orgId, displayName: 'Seguradora Demo', type: 'INSURER' },
});

const finance = insurerStaff(
  ['FINANCE'],
  ['payment:configure', 'payment:read', 'payment:resolve-exception'],
  'Filipe Finanças',
  'financas@demo.local',
);
const insurerAdmin = insurerStaff(
  ['INSURER_ADMIN'],
  ['payment:configure', 'payment:read', 'org-user:read'],
  'Gina Gestora',
  'gestor@demo.local',
);
const platformAdmin = {
  id: '0192f0c4-0000-7000-8000-000000000001',
  type: 'STAFF',
  roles: ['PLATFORM_ADMIN'],
  permissions: ['platform:operations', 'organization:read'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Rita Admin',
  email: 'admin@moseguros.co.mz',
};

const schema = (name: string) =>
  JSON.parse(
    readFileSync(
      fileURLToPath(
        new URL(
          `../../backend/src/main/resources/schemas/payment-method-${name}.json`,
          import.meta.url,
        ),
      ),
      'utf-8',
    ),
  ) as Json;

const mzn = (amount: number) => ({ amount, currency: 'MZN' });

/** API simulada com estado: o M-Pesa, a excepção e os pedidos feitos. */
async function mockApi(p: Page, me: Json) {
  const state = {
    mpesa: {
      method: 'MPESA',
      configured: false,
      enabled: false,
      environment: 'SANDBOX',
      displayName: null,
      settings: {
        ports: { c2b: 18352, query: 18353, reversal: 18354 },
        c2bTimeoutSeconds: 90,
      },
      settingsSchema: schema('mpesa'),
      secrets: {
        apiKey: { configured: false, required: true, updatedAt: null },
        publicKey: { configured: false, required: true, updatedAt: null },
        securityCredential: { configured: false, required: false, updatedAt: null },
      },
      intentValidityMinutes: 30,
      sortOrder: 0,
      version: null as number | null,
      updatedAt: null,
    } as Json,
    exceptionOpen: true,
    puts: [] as Json[],
    resolutions: [] as Json[],
    paymentQueries: [] as string[],
    exportQueries: [] as string[],
  };

  const bankReference = {
    method: 'BANK_REFERENCE',
    configured: true,
    enabled: true,
    environment: 'PRODUCTION',
    displayName: null,
    settings: {
      entity: '11223',
      fileFormat: 'MEPS',
      sftp: {
        host: 'sftp',
        port: 22,
        username: 'demo',
        authType: 'PASSWORD',
        inboundDir: '/in',
        moveAfterProcessing: true,
        processedDir: '/done',
        errorDir: '/err',
        filePattern: '*',
      },
    },
    settingsSchema: schema('bank_reference'),
    secrets: {
      'sftp.password': { configured: true, required: true, updatedAt: '2027-01-02T08:00:00Z' },
      'sftp.privateKey': { configured: false, required: false, updatedAt: null },
      'sftp.passphrase': { configured: false, required: false, updatedAt: null },
    },
    intentValidityMinutes: 30,
    sortOrder: 1,
    version: 2,
    updatedAt: '2027-01-02T08:00:00Z',
  };

  const exceptionSummary = () => ({
    id: exceptionId,
    type: 'UNKNOWN_REFERENCE',
    status: state.exceptionOpen ? 'OPEN' : 'RESOLVED',
    ...(state.exceptionOpen ? {} : { resolution: 'LINKED' }),
    entity: '11223',
    reference: '00548781108',
    amount: mzn(299900),
    paidAt: '2027-01-11T09:00:00Z',
    fileName: 'MEPS20270111.txt',
    lineNo: 4,
    createdAt: '2027-01-11T10:00:00Z',
  });
  const exceptionDetail = () => ({
    ...exceptionSummary(),
    detail: 'NOT_ISSUED',
    allowedResolutions: state.exceptionOpen ? ['LINKED', 'REFUND', 'IGNORED'] : [],
    version: state.exceptionOpen ? 0 : 1,
  });
  const paymentSummary = {
    id: paymentId,
    reference: 'PG-2027-000011',
    targetLabel: 'PR-2027-000124',
    method: 'MPESA',
    amount: mzn(450000),
    status: 'SUCCEEDED',
    providerTxId: 'MPX123',
    createdAt: '2027-01-10T08:30:00Z',
    succeededAt: '2027-01-10T08:31:00Z',
  };

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
    const { pathname } = url;
    const key = `${request.method()} ${pathname}`;

    if (key === 'POST /api/v1/auth/login' || key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, me);

    // Formas de pagamento
    if (key === 'GET /api/v1/insurer/payment-methods') {
      return json(route, 200, { methods: [state.mpesa, bankReference] });
    }
    if (key === 'PUT /api/v1/insurer/payment-methods/MPESA') {
      const body = request.postDataJSON() as Json;
      state.puts.push(body);
      const secrets = { ...(state.mpesa.secrets as Record<string, Json>) };
      for (const field of Object.keys((body.secrets as Json | undefined) ?? {})) {
        secrets[field] = { ...secrets[field], configured: true, updatedAt: '2027-01-12T08:00:00Z' };
      }
      state.mpesa = {
        ...state.mpesa,
        configured: true,
        environment: body.environment,
        settings: body.settings,
        secrets,
        version: ((state.mpesa.version as number | null) ?? -1) + 1,
        updatedAt: '2027-01-12T08:00:00Z',
      };
      return json(route, 200, state.mpesa);
    }
    if (key === 'POST /api/v1/insurer/payment-methods/MPESA/test') {
      const testedAt = '2027-01-12T08:05:00Z';
      state.mpesa = {
        ...state.mpesa,
        lastTest: { at: testedAt, result: 'OK', message: null },
        version: (state.mpesa.version as number) + 1,
      };
      return json(route, 200, { result: 'OK', message: null, testedAt });
    }
    if (key === 'POST /api/v1/insurer/payment-methods/MPESA/enable') {
      const body = request.postDataJSON() as Json;
      if (body.version !== state.mpesa.version)
        return problem(route, 409, 'CONCURRENT_MODIFICATION');
      state.mpesa = { ...state.mpesa, enabled: true, version: (state.mpesa.version as number) + 1 };
      return json(route, 200, state.mpesa);
    }
    if (key === 'GET /api/v1/insurer/payment-file-source') {
      return json(route, 200, {
        configured: true,
        enabled: true,
        entity: '11223',
        fileFormat: 'MEPS',
        lastPollAt: '2027-01-12T07:59:00Z',
        lastPollOutcome: 'OK',
        lastSuccessAt: '2027-01-12T07:59:00Z',
        staleAfterMinutes: 60,
      });
    }

    // Pagamentos
    if (key === 'GET /api/v1/insurer/payments') {
      state.paymentQueries.push(url.search);
      return json(route, 200, { content: [paymentSummary], page: 0, size: 20, totalElements: 1 });
    }
    if (key === 'GET /api/v1/insurer/payments/export') {
      state.exportQueries.push(url.search);
      return route.fulfill({
        status: 200,
        contentType: 'text/csv',
        headers: { 'content-disposition': 'attachment; filename="pagamentos.csv"' },
        body: 'referencia;montante\nPG-2027-000011;4500,00\n',
      });
    }

    // Excepções
    if (key === 'GET /api/v1/insurer/payment-exceptions') {
      const status = url.searchParams.get('status');
      const all = [exceptionSummary()];
      const content = all.filter((e) => !status || e.status === status);
      return json(route, 200, {
        content,
        page: 0,
        size: Number(url.searchParams.get('size') ?? 20),
        totalElements: content.length,
      });
    }
    if (key === `GET /api/v1/insurer/payment-exceptions/${exceptionId}`) {
      return json(route, 200, exceptionDetail());
    }
    if (key === `GET /api/v1/insurer/payment-exceptions/${exceptionId}/candidates`) {
      return json(route, 200, {
        candidates: [
          {
            id: candidateId,
            reference: 'PG-2027-000010',
            targetLabel: 'PR-2027-000123',
            amount: mzn(299900),
            status: 'EXPIRED',
            bankReference: '00548781109',
            createdAt: '2027-01-05T10:00:00Z',
            matchesAmount: true,
            matchesReference: false,
          },
        ],
      });
    }
    if (key === `POST /api/v1/insurer/payment-exceptions/${exceptionId}/resolve`) {
      const body = request.postDataJSON() as Json;
      state.resolutions.push(body);
      if (!state.exceptionOpen) return problem(route, 409, 'INVALID_STATE_TRANSITION');
      state.exceptionOpen = false;
      return json(route, 200, exceptionDetail());
    }
    if (key === 'GET /api/v1/insurer/payment-files') {
      return json(route, 200, {
        content: [
          {
            id: 'f1',
            fileName: 'MEPS20270111.txt',
            fileFormat: 'MEPS',
            formatFileId: '202701111',
            status: 'PROCESSED',
            recordCount: 5,
            totalAmount: mzn(1499500),
            totalFees: mzn(1500),
            confirmedCount: 4,
            exceptionCount: 1,
            occurredAt: '2027-01-11T09:30:00Z',
            sha256: 'abc',
          },
        ],
        page: 0,
        size: 20,
        totalElements: 1,
      });
    }

    // Administração
    if (key === 'GET /api/v1/admin/payment-worker-status') {
      return json(route, 200, {
        brokerReachable: true,
        lastMessageAt: '2027-01-12T08:00:00Z',
        queues: [
          { name: 'core.reference-payments', messages: 0, consumers: 1 },
          { name: 'core.reference-payments.dlq', messages: 0, consumers: 0 },
        ],
      });
    }
    if (key === 'GET /api/v1/admin/payment-file-sources') {
      return json(route, 200, {
        sources: [
          {
            sourceId: 's1',
            insurer: { id: orgId, displayName: 'Seguradora Demo' },
            entity: '11223',
            fileFormat: 'MEPS',
            enabled: true,
            lastPollAt: '2027-01-12T07:59:00Z',
            lastPollOutcome: 'OK',
            lastSuccessAt: '2027-01-12T07:59:00Z',
            filesLast24h: 3,
            rejectedLast24h: 0,
          },
          {
            sourceId: 's2',
            insurer: { id: 'o2', displayName: 'Seguradora Demo B' },
            entity: '44556',
            fileFormat: 'BMEPS',
            enabled: true,
            lastPollAt: '2027-01-12T07:59:00Z',
            lastPollOutcome: 'ERROR',
            lastPollError: 'SFTP_AUTH_FAILED',
            lastSuccessAt: '2027-01-12T05:00:00Z',
            staleSince: '2027-01-12T06:00:00Z',
            filesLast24h: 0,
            rejectedLast24h: 0,
          },
        ],
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

async function expectNoHorizontalScroll(p: Page) {
  const overflow = await p.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

test.describe('pagamentos da seguradora', () => {
  test('o financeiro configura o M-Pesa, grava, testa e activa', async ({ page: p }) => {
    const state = await mockApi(p, finance);
    await signIn(p, finance.email, '/seguradora/configuracao');
    await p.getByRole('link', { name: 'Abrir formas de pagamento' }).click();
    await expect(p).toHaveURL(/\/seguradora\/configuracao\/pagamentos$/);
    const mpesa = p.getByTestId('payment-method-MPESA');
    await expect(mpesa.getByTestId('method-state')).toHaveText('Não configurado');
    // Valores por defeito do schema.
    await expect(p.locator('#MPESA-ports-c2b')).toHaveValue('18352');
    await expect(mpesa.getByRole('button', { name: 'Testar ligação' })).toBeDisabled();
    await expect(mpesa.getByRole('button', { name: 'Activar' })).toBeDisabled();

    await p.locator('#MPESA-serviceProviderCode').fill('171717');
    await p.locator('#MPESA-host').fill('http://mpesa-mock');
    await p.locator('#MPESA-origin').fill('developer.mpesa.vm.co.mz');
    await p.locator('#MPESA-secret-apiKey').fill('chave-da-api');
    await p.locator('#MPESA-secret-publicKey').fill('MIIBIjANBgkq');
    await mpesa.getByRole('button', { name: 'Guardar' }).click();
    await expect(p.getByText('Guardado')).toBeVisible();
    expect(state.puts[0]).toMatchObject({
      environment: 'SANDBOX',
      version: null,
      secrets: { apiKey: 'chave-da-api', publicKey: 'MIIBIjANBgkq' },
      settings: {
        serviceProviderCode: '171717',
        host: 'http://mpesa-mock',
        ports: { c2b: 18352, query: 18353, reversal: 18354 },
      },
    });
    // Os segredos voltam a ficar vazios e dizem que estão configurados.
    await expect(p.locator('#MPESA-secret-apiKey')).toHaveValue('');
    await expect(mpesa.getByText(/Configurado em 12\/01\/2027/).first()).toBeVisible();
    await expect(mpesa.getByTestId('method-state')).toHaveText('Inactivo');

    await mpesa.getByRole('button', { name: 'Testar ligação' }).click();
    await expect(mpesa.getByTestId('test-result')).toContainText('Ligação testada com sucesso');

    await mpesa.getByRole('button', { name: 'Activar' }).click();
    const dialog = p.getByRole('dialog');
    await expect(dialog).toContainText('Os clientes passam a ver esta forma de pagamento.');
    await dialog.getByRole('button', { name: 'Activar' }).click();
    await expect(mpesa.getByTestId('method-state')).toHaveText('Activo');
    await expect(mpesa.getByRole('button', { name: 'Desactivar' })).toBeEnabled();
  });

  test('lista os pagamentos com os filtros na query string e exporta o CSV', async ({
    page: p,
  }) => {
    const state = await mockApi(p, finance);
    await signIn(
      p,
      finance.email,
      '/seguradora/pagamentos?status=SUCCEEDED&from=2027-01-01&to=2027-01-31',
    );
    await expect(p.getByRole('grid').getByText('PG-2027-000011')).toBeVisible();
    expect(state.paymentQueries.at(-1)).toContain('status=SUCCEEDED');
    expect(state.paymentQueries.at(-1)).toContain('from=2027-01-01');

    await p.getByRole('combobox', { name: 'Método' }).click();
    await p.getByRole('option', { name: 'M-Pesa' }).click();
    await expect(p).toHaveURL(/method=MPESA/);
    await expect(p).toHaveURL(/status=SUCCEEDED/);
    await expect.poll(() => state.paymentQueries.at(-1)).toContain('method=MPESA');

    const download = p.waitForEvent('download');
    await p.getByRole('button', { name: 'Exportar CSV' }).click();
    expect((await download).suggestedFilename()).toBe('pagamentos-2027-01-01-2027-01-31.csv');
    expect(state.exportQueries[0]).toContain('method=MPESA');
    expect(state.exportQueries[0]).toContain('status=SUCCEEDED');

    await p.getByRole('grid').getByText('PG-2027-000011').click();
    await expect(p).toHaveURL(new RegExp(`/seguradora/pagamentos/${paymentId}$`));
  });

  test('o financeiro associa uma referência desconhecida a um pagamento', async ({ page: p }) => {
    const state = await mockApi(p, finance);
    await signIn(p, finance.email, '/seguradora/pagamentos/excepcoes');
    await expect(p.getByTestId('open-exceptions-count')).toHaveText('1');
    await p.getByRole('grid').getByText('Referência desconhecida').click();
    const drawer = p.getByRole('dialog');
    await expect(drawer).toContainText('Referência não emitida pela plataforma');
    await drawer.getByLabel(/Associar a um pagamento/).check();
    await drawer.getByRole('radio', { name: 'Escolher PG-2027-000010' }).check();
    await drawer.getByLabel(/Nota/).fill('Cliente pagou com a referência de outra proposta.');
    await drawer.getByRole('button', { name: 'Resolver' }).click();
    const confirm = p.getByRole('dialog').filter({ hasText: 'Continuar?' });
    await expect(confirm).toContainText(
      'Isto confirma o pagamento PG-2027-000010 e a proposta PR-2027-000123 passa a paga.',
    );
    await confirm.getByRole('button', { name: 'Continuar' }).click();
    await expect(p.getByText('Excepção resolvida')).toBeVisible();
    await expect(p.getByTestId('open-exceptions-count')).toHaveCount(0);
    await expect(p.getByText('Não há excepções com estes filtros.')).toBeVisible();
    expect(state.resolutions).toEqual([
      {
        resolution: 'LINKED',
        note: 'Cliente pagou com a referência de outra proposta.',
        version: 0,
        intentId: candidateId,
      },
    ]);
  });

  test('o gestor vê a excepção só em leitura e os ficheiros', async ({ page: p }) => {
    await mockApi(p, insurerAdmin);
    await signIn(p, insurerAdmin.email, '/seguradora/pagamentos/ficheiros');
    await expect(p.getByRole('grid').getByText('MEPS20270111.txt')).toBeVisible();
    await expect(p.getByRole('grid').getByText('202701111')).toBeVisible();
    await p.getByRole('link', { name: 'Ver as excepções de MEPS20270111.txt' }).click();
    await expect(p).toHaveURL(/excepcoes\?status=&q=MEPS20270111\.txt/);
    await p.getByRole('grid').getByText('Referência desconhecida').click();
    const drawer = p.getByRole('dialog');
    await expect(drawer).toContainText('Só o financeiro pode resolver excepções.');
    await expect(drawer.getByRole('button', { name: 'Resolver' })).toHaveCount(0);
  });

  test('a 360×740 a excepção abre em ecrã inteiro e a resolução funciona', async ({ page: p }) => {
    await p.setViewportSize({ width: 360, height: 740 });
    const state = await mockApi(p, finance);
    await signIn(p, finance.email, '/seguradora/pagamentos/excepcoes');
    await p.getByTestId('exception-card').click();
    const dialog = p.getByRole('dialog');
    await expect(dialog).toBeVisible();
    const box = await dialog.boundingBox();
    expect(box?.width).toBe(360);
    await dialog.getByLabel(/Reembolsar fora da plataforma/).check();
    await dialog.getByLabel(/Nota/).fill('Devolução feita pela seguradora fora da plataforma.');
    await dialog.getByRole('button', { name: 'Resolver' }).click();
    await expect(p.getByText('Excepção resolvida')).toBeVisible();
    expect(state.resolutions[0]).toMatchObject({ resolution: 'REFUND' });
    await expectNoHorizontalScroll(p);
  });
});

test.describe('a 360 px', () => {
  test('as páginas dos pagamentos não têm scroll horizontal', async ({ page: p }) => {
    await p.setViewportSize({ width: 360, height: 740 });
    await mockApi(p, finance);
    await signIn(p, finance.email, '/seguradora/configuracao/pagamentos');
    // A menos de 900 px os métodos são painéis que se abrem um de cada vez.
    await p.getByRole('button', { name: /M-Pesa/ }).click();
    await expect(p.locator('#MPESA-serviceProviderCode')).toBeVisible();
    await expectNoHorizontalScroll(p);
    await p.getByRole('button', { name: /Entidade e referência/ }).click();
    await expect(p.locator('#BANK_REFERENCE-entity')).toBeVisible();
    await expect(p.locator('#MPESA-serviceProviderCode')).toBeHidden();
    await expectNoHorizontalScroll(p);
    for (const path of ['/seguradora/pagamentos', '/seguradora/pagamentos/ficheiros']) {
      await p.goto(path);
      await expect(p.getByRole('tab', { name: /Ficheiros/ })).toBeVisible();
      await expect(p.getByRole('button', { name: 'Filtros' })).toBeVisible();
      await expectNoHorizontalScroll(p);
    }
    await expect(p.getByTestId('file-card')).toContainText('MEPS20270111.txt');
  });
});

test.describe('operações de pagamento', () => {
  test('o administrador vê uma fonte parada destacada', async ({ page: p }) => {
    await mockApi(p, platformAdmin);
    await signIn(p, platformAdmin.email, '/admin/operacoes/pagamentos');
    await expect(
      p.getByRole('heading', { level: 1, name: 'Operações de pagamento' }),
    ).toBeVisible();
    await expect(p.getByText('Broker ligado')).toBeVisible();
    const stale = p.locator('[data-testid="source"][data-stale="true"]');
    await expect(stale).toHaveCount(1);
    await expect(stale).toContainText('Seguradora Demo B');
    await expect(stale).toContainText('Autenticação SFTP recusada');
    // As paradas vêm primeiro.
    await expect(p.getByTestId('source').first()).toContainText('Seguradora Demo B');
  });

  test('um utilizador de seguradora não entra nas operações', async ({ page: p }) => {
    await mockApi(p, finance);
    await signIn(p, finance.email, '/seguradora/pagamentos');
    await expect(p.getByRole('grid')).toBeVisible();
    await p.goto('/admin/operacoes/pagamentos');
    await expect(p).toHaveURL(/\/sem-acesso$/);
  });
});
