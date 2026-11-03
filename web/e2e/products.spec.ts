import { expect, test, type Page, type Route } from '@playwright/test';

const orgId = '0192f0c4-0000-7000-8000-0000000000a1';
const productId = '0192f0c4-0000-7000-8000-000000000201';
const versionId = '0192f0c4-0000-7000-8000-000000000211';
const minCapital = 300000000;

const manager = {
  id: '0192f0c4-0000-7000-8000-000000000003',
  type: 'STAFF',
  roles: ['PRODUCT_MANAGER'],
  permissions: ['product:read', 'product:manage'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Gestor de Produto',
  email: 'gestor@demo.local',
  organization: { id: orgId, displayName: 'Seguradora Demo', type: 'INSURER' },
};

type Json = Record<string, unknown>;

/** API simulada com estado: o produto, a versão e os documentos criados durante o teste. */
async function mockApi(p: Page) {
  const state = {
    product: null as Json | null,
    version: null as Json | null,
    documents: [] as Json[],
    coverageBodies: [] as unknown[],
    published: false,
  };
  let lock = 0;
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string, extra = {}) =>
    json(route, status, { type: 'about:blank', title: code, status, code, ...extra });

  const versionRef = () => {
    const v = state.version!;
    return {
      id: v.id,
      versionNo: v.versionNo,
      status: v.status,
      validFrom: v.validFrom,
      publishedAt: v.publishedAt,
    };
  };
  const productResponse = () => ({
    ...state.product!,
    status: state.published ? 'PUBLISHED' : 'DRAFT',
    currentVersion: state.published ? versionRef() : undefined,
    draftVersion: state.published ? undefined : versionRef(),
    versions: [versionRef()],
  });
  const versionResponse = () => ({ ...state.version!, documents: state.documents });
  const ruleErrors = () => {
    const v = state.version!;
    const errors: { field: string; code: string }[] = [];
    const coverages = v.coverages as Json[];
    const mtpl = coverages.findIndex((c) => c.code === 'MTPL');
    if (mtpl < 0) errors.push({ field: 'coverages', code: 'MTPL_REQUIRED' });
    else if (((coverages[mtpl]!.capital as Json | undefined)?.amount as number) < minCapital) {
      errors.push({ field: `coverages[${mtpl}].capital`, code: 'MTPL_CAPITAL_BELOW_MINIMUM' });
    }
    for (const docType of ['GENERAL_CONDITIONS', 'PRE_CONTRACT_INFO']) {
      if (!state.documents.some((d) => d.docType === docType && d.language === 'pt')) {
        errors.push({ field: `documents.${docType}`, code: 'DOCUMENT_REQUIRED' });
      }
    }
    if (!(v.vehicleCategories as unknown[]).length) {
      errors.push({ field: 'vehicleCategories', code: 'REQUIRED' });
    }
    if (!(v.usages as unknown[]).length) errors.push({ field: 'usages', code: 'REQUIRED' });
    return errors;
  };
  const checkVersion = (route: Route, body: Json) => {
    if (body.version !== state.version!.version) {
      void problem(route, 409, 'CONCURRENT_MODIFICATION');
      return false;
    }
    return true;
  };
  const bump = () => {
    state.version!.version = (state.version!.version as number) + 1;
  };

  await p.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const { pathname } = new URL(request.url());
    const method = request.method();
    const key = `${method} ${pathname}`;

    if (key === 'POST /api/v1/auth/login') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, { accessToken: 'access', refreshToken: 'refresh', expiresIn: 900 });
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (request.headers()['authorization'] !== 'Bearer access') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, manager);
    if (key === 'GET /api/v1/platform-settings') {
      return json(route, 200, {
        settings: [{ key: 'MTPL_MIN_CAPITAL', value: String(minCapital), valueType: 'MONEY' }],
      });
    }
    if (key === 'GET /api/v1/insurer/branding') return json(route, 200, { logoFileId: null });
    if (key === 'GET /api/v1/insurer/products') {
      const content = state.product
        ? [
            {
              id: productId,
              code: state.product.code,
              name: state.product.name,
              type: state.product.type,
              status: state.published ? 'PUBLISHED' : 'DRAFT',
              currentVersionNo: state.published ? 1 : undefined,
              hasDraft: !state.published,
              updatedAt: '2026-11-03T08:00:00Z',
            },
          ]
        : [];
      return json(route, 200, { content, page: 0, size: 20, totalElements: content.length });
    }
    if (key === 'POST /api/v1/insurer/products') {
      const body = request.postDataJSON() as Json;
      state.product = {
        ...body,
        id: productId,
        version: 0,
        createdAt: '2026-11-03T08:00:00Z',
        updatedAt: '2026-11-03T08:00:00Z',
      };
      state.version = {
        id: versionId,
        productId,
        productCode: body.code,
        productName: body.name,
        productType: body.type,
        versionNo: 1,
        status: 'DRAFT',
        vehicleCategories: [],
        usages: [],
        coverages: [],
        editable: true,
        version: 0,
        createdAt: '2026-11-03T08:00:00Z',
        updatedAt: '2026-11-03T08:00:00Z',
      };
      return json(route, 201, productResponse());
    }
    if (key === `GET /api/v1/insurer/products/${productId}`) {
      return json(route, 200, productResponse());
    }
    const base = `/api/v1/insurer/product-versions/${versionId}`;
    if (key === `GET ${base}`) return json(route, 200, versionResponse());
    if (key === `PUT ${base}`) {
      const body = request.postDataJSON() as Json;
      if (!checkVersion(route, body)) return;
      Object.assign(state.version!, {
        vehicleCategories: body.vehicleCategories,
        usages: body.usages,
        changeNotes: body.changeNotes,
      });
      bump();
      return json(route, 200, versionResponse());
    }
    if (key === `PUT ${base}/coverages`) {
      const body = request.postDataJSON() as Json;
      if (!checkVersion(route, body)) return;
      state.coverageBodies.push(body.coverages);
      state.version!.coverages = body.coverages;
      bump();
      return json(route, 200, versionResponse());
    }
    if (key === `POST ${base}/documents`) {
      const raw = request.postDataBuffer()?.toString('latin1') ?? '';
      const part = (name: string) =>
        new RegExp(`name="${name}"\\r\\n\\r\\n([^\\r]*)`).exec(raw)?.[1];
      const document = {
        id: `0192f0c4-0000-7000-8000-00000000030${state.documents.length}`,
        docType: part('docType'),
        language: part('language') ?? 'pt',
        fileId: `0192f0c4-0000-7000-8000-00000000040${state.documents.length}`,
      };
      state.documents = state.documents
        .filter((d) => !(d.docType === document.docType && d.language === document.language))
        .concat(document);
      bump();
      lock += 1;
      return json(route, 201, document);
    }
    if (key === `POST ${base}/validate`) {
      const errors = ruleErrors();
      return json(route, 200, { publishable: errors.length === 0, errors });
    }
    if (key === `POST ${base}/publish`) {
      const body = request.postDataJSON() as Json;
      if (!checkVersion(route, body)) return;
      const errors = ruleErrors();
      if (errors.length) return problem(route, 400, 'PRODUCT_VERSION_NOT_PUBLISHABLE', { errors });
      Object.assign(state.version!, {
        status: 'PUBLISHED',
        editable: false,
        validFrom: '2026-11-03',
        publishedAt: '2026-11-03T09:00:00Z',
        publishedBy: manager.id,
      });
      state.published = true;
      bump();
      return json(route, 200, versionResponse());
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return { state, uploads: () => lock };
}

async function expectNoHorizontalScroll(p: Page) {
  const overflow = await p.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

async function signIn(p: Page) {
  await p.goto('/entrar?voltar=%2Fseguradora%2Fprodutos');
  await p.getByLabel('E-mail').fill(manager.email);
  await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
  await p.getByLabel('Código da aplicação de autenticação').fill('123456');
  await p.getByRole('button', { name: 'Entrar' }).click();
  await expect(p).toHaveURL(/\/seguradora\/produtos$/);
}

const steps = ['dados', 'coberturas', 'franquias', 'documentos', 'revisao'];

/** Abre o passo: no ecrã largo pelo `Stepper`, no estreito com "Seguinte". */
async function goToStep(p: Page, name: string, passo: string) {
  const heading = p.getByRole('heading', { level: 2, name, exact: true });
  const stepButton = p.getByRole('button', { name: new RegExp(`^\\d?\\s*${name}$`) });
  if ((await stepButton.count()) > 0 && (await stepButton.first().isVisible())) {
    await stepButton.first().click();
  } else {
    const current = new URL(p.url()).searchParams.get('passo') ?? 'dados';
    for (let i = steps.indexOf(current) + 1; i <= steps.indexOf(passo); i += 1) {
      await p.getByRole('button', { name: /Seguinte/ }).click();
      await expect(p).toHaveURL(new RegExp(`passo=${steps[i]}$`));
    }
  }
  await expect(p).toHaveURL(new RegExp(`passo=${passo}$`));
  await expect(heading).toBeVisible();
}

async function uploadPdf(p: Page, region: string, name: string) {
  const section = p.getByRole('region', { name: region, exact: true });
  const chooser = p.waitForEvent('filechooser');
  await section.getByRole('button', { name: 'Enviar PDF' }).click();
  await (
    await chooser
  ).setFiles({ name, mimeType: 'application/pdf', buffer: Buffer.from('%PDF-1.4\n%%EOF\n') });
  await expect(section.getByText('Enviado', { exact: true })).toBeVisible();
}

async function productFlow(p: Page, narrow: boolean) {
  const api = await mockApi(p);
  await signIn(p);
  await expect(p.getByRole('heading', { level: 1, name: 'Produtos' })).toBeVisible();
  await expect(p.getByText('Ainda não há produtos.')).toBeVisible();
  if (narrow) await expectNoHorizontalScroll(p);

  // Criar o produto.
  await p.getByRole('button', { name: 'Novo produto' }).click();
  const dialog = p.getByRole('dialog', { name: 'Novo produto' });
  await dialog.getByLabel('Código').fill('rc-nova');
  await expect(dialog.getByLabel('Código')).toHaveValue('RC-NOVA');
  await dialog.getByLabel('Nome').fill('RC Nova');
  await dialog.getByLabel('Tipo').selectOption('MTPL_ONLY');
  await dialog.getByRole('button', { name: 'Criar produto' }).click();
  await expect(p).toHaveURL(new RegExp(`/versoes/${versionId}\\?passo=dados$`));

  // Passo 1: elegibilidade.
  await expect(p.getByRole('heading', { level: 2, name: 'Dados gerais' })).toBeVisible();
  await p.getByRole('checkbox', { name: 'Ligeiro de passageiros' }).check();
  await p.getByRole('checkbox', { name: 'Particular' }).check();
  if (narrow) await expectNoHorizontalScroll(p);
  await p.getByRole('button', { name: 'Guardar e continuar' }).click();

  // Passo 2: MTPL com 3 000 000 MT.
  await expect(p.getByRole('heading', { level: 2, name: 'Coberturas' })).toBeVisible();
  await p.getByRole('button', { name: 'Acrescentar cobertura' }).click();
  await p.getByRole('menuitem', { name: 'Responsabilidade civil obrigatória' }).click();
  const card = p.getByRole('region', { name: 'Responsabilidade civil obrigatória' });
  await expect(card.getByRole('switch', { name: 'Obrigatória' })).toBeChecked();
  await expect(card.getByRole('switch', { name: 'Obrigatória' })).toBeDisabled();
  await expect(card.getByLabel('Tipo de capital')).toBeDisabled();
  await expect(card.getByText(/Mínimo legal: 3\s000\s000,00 MT/)).toBeVisible();
  await card.getByLabel('Capital', { exact: true }).fill('3 000 000');
  if (narrow) await expectNoHorizontalScroll(p);
  await p.getByRole('button', { name: 'Guardar e continuar' }).click();
  await expect(p.getByRole('heading', { level: 2, name: 'Franquias' })).toBeVisible();
  expect(api.state.coverageBodies).toEqual([
    [
      expect.objectContaining({
        code: 'MTPL',
        mandatory: true,
        capitalType: 'FIXED',
        capital: { amount: 300000000, currency: 'MZN' },
        deductibleType: 'NONE',
        territorialScope: ['MZ'],
      }),
    ],
  ]);

  // Passo 5: faltam os dois documentos.
  await goToStep(p, 'Revisão e publicação', 'revisao');
  const errors = p.getByTestId('publication-error');
  await expect(errors).toHaveCount(2);
  await expect(errors.nth(0)).toContainText('Condições gerais: Falta este documento em português.');
  await expect(errors.nth(1)).toContainText(
    'Informação pré-contratual: Falta este documento em português.',
  );
  await expect(p.getByRole('button', { name: 'Publicar versão' })).toBeDisabled();
  if (narrow) await expectNoHorizontalScroll(p);
  await errors.nth(0).getByRole('button', { name: 'Corrigir' }).click();

  // Passo 4: enviar os PDFs.
  await expect(p.getByRole('heading', { level: 2, name: 'Documentos' })).toBeVisible();
  await expect(p).toHaveURL(/passo=documentos$/);
  await uploadPdf(p, 'Condições gerais', 'condicoes-gerais.pdf');
  await uploadPdf(p, 'Informação pré-contratual', 'informacao-pre-contratual.pdf');
  expect(api.uploads()).toBe(2);
  if (narrow) await expectNoHorizontalScroll(p);

  // Passo 5 sem erros: publicar.
  await goToStep(p, 'Revisão e publicação', 'revisao');
  await expect(p.getByText('A versão cumpre as regras de publicação.')).toBeVisible();
  await expect(p.getByTestId('publication-error')).toHaveCount(0);
  await expect(p.getByTestId('catalog-preview')).toContainText('Seguradora Demo');
  await expect(p.getByTestId('catalog-preview')).toContainText(/3\s000\s000,00 MT/);
  await p.getByRole('button', { name: 'Publicar versão' }).click();
  const publish = p.getByRole('dialog', { name: 'Publicar a versão 1?' });
  await expect(publish).toContainText(
    'Depois de publicada, esta versão não pode ser alterada e passa a estar à venda hoje.',
  );
  await publish.getByRole('button', { name: 'Publicar' }).click();

  // Detalhe com o aviso; a versão abre só de leitura.
  await expect(p).toHaveURL(new RegExp(`/seguradora/produtos/${productId}$`));
  await expect(p.getByText('Versão 1 publicada', { exact: true })).toBeVisible();
  if (narrow) await expectNoHorizontalScroll(p);
  await p.getByRole('link', { name: 'Versão 1' }).click();
  await expect(p.getByTestId('read-only-notice')).toContainText(
    'Uma versão publicada não pode ser alterada.',
  );
  await expect(p.getByRole('checkbox', { name: 'Particular' })).toBeDisabled();
  await expect(p.getByRole('button', { name: 'Guardar' })).toHaveCount(0);
  if (narrow) await expectNoHorizontalScroll(p);
}

test.describe('produtos', () => {
  test('criar, preencher, publicar e ver a versão só de leitura', async ({ page: p }) => {
    await productFlow(p, false);
  });

  test.describe('a 360×740', () => {
    test.use({ viewport: { width: 360, height: 740 } });

    test('o mesmo fluxo é utilizável', async ({ page: p }) => {
      await productFlow(p, true);
    });
  });
});
