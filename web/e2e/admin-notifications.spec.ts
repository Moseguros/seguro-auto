import { expect, test, type Page, type Route } from '@playwright/test';

type Json = Record<string, unknown>;

const admin = {
  id: '0192f0c4-0000-7000-8000-000000000001',
  type: 'STAFF',
  roles: ['PLATFORM_ADMIN'],
  permissions: ['notification-template:manage', 'organization:read'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Rita Admin',
  email: 'admin@moseguros.co.mz',
};

const smsId = '0192f0c4-0000-7000-8000-00000000e001';
const defaultBody = 'MoSeguros: apolice {{policyNumber}} emitida. Certificado: {{link}}';

/** Pré-visualização como o backend: valores de exemplo e o texto sem acentos. */
function render(body: string, sample: Record<string, string>) {
  const text = body.replace(/\{\{(\w+)\}\}/g, (_, name: string) => sample[name] ?? '');
  const folded = text.normalize('NFD').replace(/[̀-ͯ]/g, '');
  return { body: text, smsText: folded, length: folded.length, limit: 160, fits: folded.length <= 160 };
}

async function mockApi(p: Page) {
  const sample = { policyNumber: 'AP-2027-000001', plate: 'ABC-123-MC', link: 'https://m.co/v/x' };
  const state = {
    template: {
      id: smsId,
      code: 'POLICY_ISSUED',
      channel: 'SMS',
      locale: 'pt',
      body: 'Apólice {{policyNumber}} emitida.',
      active: true,
      customized: true,
      defaultBody,
      variables: ['policyNumber', 'plate', 'link'],
      sampleParams: sample,
      updatedAt: '2027-02-10T08:31:00Z',
      updatedBy: 'admin@moseguros.co.mz',
      version: 2,
    } as Json,
    puts: [] as Json[],
    previews: [] as Json[],
    resets: 0,
  };
  const summary = () => {
    const { body: _b, variables: _v, sampleParams: _s, defaultBody: _d, version: _n, ...rest } =
      state.template;
    return rest;
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
    const key = `${request.method()} ${url.pathname}`;
    if (key === 'POST /api/v1/auth/login' || key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, admin);
    if (key === 'GET /api/v1/admin/notification-templates') {
      const rows = [
        summary(),
        { ...summary(), id: 'e2', channel: 'EMAIL', subject: 'Apólice emitida', customized: false },
        { ...summary(), id: 'e3', locale: 'en', customized: false },
      ];
      const code = url.searchParams.get('code');
      const content = rows.filter((r) => !code || String(r.code).startsWith(code));
      return json(route, 200, { content, page: 0, size: 50, totalElements: content.length });
    }
    if (key === `GET /api/v1/admin/notification-templates/${smsId}`) {
      return json(route, 200, state.template);
    }
    if (key === `POST /api/v1/admin/notification-templates/${smsId}/preview`) {
      const body = request.postDataJSON() as { body: string };
      state.previews.push(body);
      return json(route, 200, render(body.body, sample));
    }
    if (key === `PUT /api/v1/admin/notification-templates/${smsId}`) {
      const body = request.postDataJSON() as Json;
      state.puts.push(body);
      if (body.version !== state.template.version) {
        return problem(route, 409, 'CONCURRENT_MODIFICATION');
      }
      state.template = {
        ...state.template,
        body: body.body,
        active: body.active,
        customized: true,
        version: (state.template.version as number) + 1,
      };
      return json(route, 200, state.template);
    }
    if (key === `POST /api/v1/admin/notification-templates/${smsId}/reset`) {
      state.resets += 1;
      state.template = {
        ...state.template,
        body: defaultBody,
        customized: false,
        version: (state.template.version as number) + 1,
      };
      return json(route, 200, state.template);
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

test('o administrador abre o SMS, insere uma variável, pré-visualiza, guarda e repõe', async ({
  page: p,
}) => {
  const state = await mockApi(p);
  await p.goto(`/entrar?voltar=${encodeURIComponent('/admin/notificacoes')}`);
  await p.getByLabel('E-mail').fill(admin.email);
  await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
  await p.getByLabel('Código da aplicação de autenticação').fill('123456');
  await p.getByRole('button', { name: 'Entrar' }).click();

  await expect(p.getByRole('heading', { level: 1, name: 'Modelos de notificação' })).toBeVisible();
  await expect(
    p.getByRole('navigation', { name: 'Administração' }).getByRole('link', { name: 'Notificações' }),
  ).toBeVisible();
  // A linha do SMS em português.
  await p
    .getByRole('row')
    .filter({ hasText: 'SMS' })
    .filter({ hasText: 'Português' })
    .first()
    .click();
  await expect(p).toHaveURL(new RegExp(`/admin/notificacoes/${smsId}$`));
  await expect(p.getByRole('heading', { level: 1, name: 'POLICY_ISSUED' })).toBeVisible();

  const body = p.getByRole('textbox', { name: 'Texto' });
  await expect(body).toHaveValue('Apólice {{policyNumber}} emitida.');
  await expect(p.getByRole('button', { name: 'Guardar' })).toBeDisabled();
  // Cursor no fim e insere a matrícula.
  await body.click();
  await body.press('End');
  await body.type(' Matrícula ');
  await p.getByRole('button', { name: 'Inserir plate' }).click();
  await expect(body).toHaveValue('Apólice {{policyNumber}} emitida. Matrícula {{plate}}');

  const preview = p.getByTestId('template-preview');
  await expect(preview.getByTestId('preview-sms')).toHaveText(
    'Apolice AP-2027-000001 emitida. Matricula ABC-123-MC',
  );
  await expect(preview.getByTestId('sms-counter')).toHaveText('52/160');
  await expect(preview.getByTestId('sms-counter')).toHaveAttribute('data-fits', 'true');

  await p.getByRole('button', { name: 'Guardar' }).click();
  await expect(p.getByText('Modelo guardado')).toBeVisible();
  expect(state.puts[0]).toMatchObject({
    body: 'Apólice {{policyNumber}} emitida. Matrícula {{plate}}',
    version: 2,
    active: true,
  });
  await expect(p.getByRole('button', { name: 'Guardar' })).toBeDisabled();

  await p.getByRole('button', { name: 'Repor texto original' }).click();
  const dialog = p.getByRole('dialog');
  await expect(
    dialog.getByText('O texto volta ao original da plataforma. As suas alterações perdem-se.'),
  ).toBeVisible();
  await dialog.getByRole('button', { name: 'Repor texto original' }).click();
  await expect(p.getByText('Texto original reposto')).toBeVisible();
  await expect(body).toHaveValue(defaultBody);
  expect(state.resets).toBe(1);
  // Sem personalização o botão "Repor" desaparece.
  await expect(p.getByRole('button', { name: 'Repor texto original' })).toHaveCount(0);
});
