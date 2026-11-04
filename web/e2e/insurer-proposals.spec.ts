import { expect, test, type Page, type Route } from '@playwright/test';

const orgId = '0192f0c4-0000-7000-8000-0000000000a1';
const proposalId = '0192f0c4-0000-7000-8000-000000000501';
const inspectionId = '0192f0c4-0000-7000-8000-000000000601';
const kycId = '0192f0c4-0000-7000-8000-000000000701';

const underwriter = {
  id: '0192f0c4-0000-7000-8000-000000000004',
  type: 'STAFF',
  roles: ['UNDERWRITER'],
  permissions: ['proposal:read', 'proposal:decide', 'kyc:review', 'inspection:review'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Sara Subscritora',
  email: 'subscritor@demo.local',
  organization: { id: orgId, displayName: 'Seguradora Demo', type: 'INSURER' },
};

const finance = {
  ...underwriter,
  id: '0192f0c4-0000-7000-8000-000000000005',
  roles: ['FINANCE'],
  permissions: ['payment:read'],
  displayName: 'Filipe Finanças',
  email: 'financas@demo.local',
};

/** PNG de 1×1 para as fotografias e os documentos. */
const png = Buffer.from(
  'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==',
  'base64',
);

const mzn = (amount: number) => ({ amount, currency: 'MZN' });
const slots = ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'];

type Json = Record<string, unknown>;

/** API simulada com estado: a proposta, a vistoria e o KYC mudam com as decisões. */
async function mockApi(p: Page, me: Json = underwriter) {
  const state = {
    proposalStatus: 'UNDER_REVIEW',
    proposalVersion: 7,
    inspectionStatus: 'SUBMITTED',
    inspectionVersion: 3,
    kycState: 'PENDING',
    kycStatus: 'PENDING',
    listQueries: [] as string[],
    approvals: [] as unknown[],
    verifications: [] as unknown[],
    accepts: [] as unknown[],
    rejects: [] as unknown[],
    infoRequests: [] as unknown[],
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string) =>
    json(route, status, { type: 'about:blank', title: code, status, code });

  const inspection = () => ({
    id: inspectionId,
    required: true,
    mode: 'SELF_PHOTOS',
    status: state.inspectionStatus,
    odometerKm: 45210,
    submittedAt: '2027-01-08T09:00:00Z',
    version: state.inspectionVersion,
    missingSlots: [],
    photos: slots.map((slot, i) => ({
      id: `ph-${slot}`,
      slot,
      fileId: `file-${slot}`,
      source: 'CAMERA',
      uploadedAt: '2027-01-08T08:00:00Z',
      takenAt: `2027-01-08T07:5${i}:00Z`,
      ...(slot === 'FRONT' ? { latitude: -25.9655, longitude: 32.5832 } : {}),
    })),
    ...(state.inspectionStatus === 'APPROVED'
      ? { review: { reviewedAt: '2027-01-09T08:00:00Z', retakeSlots: [] } }
      : {}),
  });
  const proposal = () => ({
    id: proposalId,
    number: 'PR-2027-000123',
    status: state.proposalStatus,
    channel: 'WEB',
    broker: null,
    insurer: { id: orgId, displayName: 'Seguradora Demo' },
    quoteId: 'q1',
    createdAt: '2027-01-05T09:30:00Z',
    updatedAt: '2027-01-08T09:00:00Z',
    submittedAt: '2027-01-08T10:00:00Z',
    startAt: '2027-01-14T22:00:00Z',
    endAt: '2028-01-14T22:00:00Z',
    endDate: '2028-01-14',
    durationDays: 365,
    version: state.proposalVersion,
    checklist: { declarations: 'DONE', suitability: 'DONE', inspection: 'DONE', signature: 'DONE' },
    product: {
      id: 'prod1',
      name: 'Auto Completo',
      type: 'COMPREHENSIVE',
      versionId: 'pv1',
      documents: [],
    },
    policyholder: {
      fullName: 'Ana Maria Cossa',
      birthDate: '1990-04-12',
      idDocType: 'BI',
      idDocNumberMasked: '********123A',
      nuitMasked: '*****6789',
      province: 'MAPUTO_CITY',
      msisdnMasked: '+25884****001',
    },
    vehicle: {
      id: 'v1',
      plate: 'AAA-100-MC',
      make: 'Toyota',
      model: 'Corolla',
      year: 2019,
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      chassisNumber: 'JTDBR32E720123456',
      seats: 5,
      commercialValue: mzn(95000000),
    },
    driver: {
      id: 'd1',
      fullName: 'Ana Maria Cossa',
      birthDate: '1990-04-12',
      licenceNumberMasked: '****1234',
      licenceIssueDate: '2012-06-01',
      licenceCategories: ['B'],
    },
    coverages: [
      {
        code: 'MTPL',
        mandatory: true,
        capital: mzn(300000000),
        deductible: { type: 'NONE' },
        premium: mzn(450000),
      },
      {
        code: 'OD_COLLISION',
        mandatory: false,
        capital: mzn(95000000),
        deductible: { type: 'PERCENT', percent: '10', minAmount: mzn(2500000) },
        premium: mzn(1050000),
      },
    ],
    premium: {
      commercial: mzn(1500000),
      charges: [
        { code: 'STAMP', labelPt: 'Imposto de selo', labelEn: 'Stamp duty', amount: mzn(30000) },
      ],
      roundingAdjustment: mzn(0),
      total: mzn(1530000),
    },
    declarations: {
      confirmedAt: '2027-01-06T10:00:00Z',
      answers: [
        {
          questionCode: 'CLAIMS_LAST_3_YEARS',
          answer: true,
          claimsCount: 1,
          details: 'Pequeno toque num parque em 2025, sem feridos.',
        },
        { questionCode: 'PREVIOUS_REFUSAL', answer: false },
        { questionCode: 'USAGE_DIFFERENT', answer: false },
        { questionCode: 'VEHICLE_MODIFIED', answer: false },
        { questionCode: 'NOVICE_DRIVERS', answer: false },
      ],
    },
    suitability: {
      answeredAt: '2027-01-06T10:05:00Z',
      result: 'SUITABLE',
      reasons: [],
      acknowledgedAt: null,
      answers: [
        { questionCode: 'SUIT_NEED', answer: 'OWN_DAMAGE' },
        { questionCode: 'SUIT_FINANCED', answer: 'NO' },
        { questionCode: 'SUIT_CAN_AFFORD_REPAIR', answer: 'NO' },
        { questionCode: 'SUIT_CROSS_BORDER', answer: 'NO' },
        { questionCode: 'SUIT_DEDUCTIBLE', answer: 'YES' },
      ],
    },
    inspection: inspection(),
    kyc: {
      state: state.kycState,
      checkId: kycId,
      verifiedAt: state.kycState === 'VERIFIED' ? '2027-01-09T08:30:00Z' : null,
      verifiedByThisInsurer: state.kycState === 'VERIFIED',
      requiredForAutomaticAcceptance: true,
    },
    signature: {
      assisted: false,
      channel: 'SMS',
      destinationMasked: '+25884****001',
      documentFileId: 'file-pdf',
      documentHash: '9f2c0000000000000000000000000000000000000000000000000000000000e1',
      signedAt: '2027-01-08T09:50:00Z',
    },
    signatures: [
      {
        assisted: false,
        channel: 'SMS',
        destinationMasked: '+25884****001',
        documentFileId: 'file-pdf',
        documentHash: '9f2c0000000000000000000000000000000000000000000000000000000000e1',
        signedAt: '2027-01-08T09:50:00Z',
        invalidatedAt: null,
        invalidatedReason: null,
      },
    ],
    sla:
      state.proposalStatus === 'UNDER_REVIEW'
        ? {
            status: 'ALERT',
            reviewStartedAt: '2027-01-08T10:00:00Z',
            alertAt: '2027-01-18T10:00:00Z',
            dueAt: '2027-01-23T10:00:00Z',
          }
        : {},
    underwriting: {
      evaluatedAt: '2027-01-08T10:00:00Z',
      outcome: 'MANUAL_REVIEW',
      rulesSchemaVersion: 1,
      rulesSource: 'DEFAULT',
      failures: ['KYC_REQUIRED', 'DECLARATION_POSITIVE', 'INSPECTION_NOT_APPROVED'],
      inputs: {
        hasOwnDamage: true,
        kycState: 'PENDING',
        inspectionStatus: 'SUBMITTED',
        maxVehicleValue: 150000000,
        positiveDeclarations: ['CLAIMS_LAST_3_YEARS'],
        simplifiedKycMaxPremium: 1000000,
        totalAmount: 1530000,
        vehicleValue: 95000000,
      },
    },
    decision: null,
  });
  const summary = () => ({
    id: proposalId,
    number: 'PR-2027-000123',
    customerName: 'Ana Maria Cossa',
    productName: 'Auto Completo',
    productType: 'COMPREHENSIVE',
    vehiclePlate: 'AAA-100-MC',
    total: mzn(1530000),
    channel: 'WEB',
    brokerName: null,
    status: state.proposalStatus,
    submittedAt: '2027-01-08T10:00:00Z',
    slaDueAt: '2027-01-23T10:00:00Z',
    slaStatus: 'ALERT',
    kycState: state.kycState,
    inspectionStatus: state.inspectionStatus,
  });
  const kycCheck = () => ({
    id: kycId,
    status: state.kycStatus,
    submittedAt: '2027-01-07T08:00:00Z',
    version: state.kycStatus === 'PENDING' ? 0 : 1,
    customer: {
      userId: 'c1',
      fullName: 'Ana Maria Cossa',
      birthDate: '1990-04-12',
      idDocType: 'BI',
      idDocNumber: '110100123456A',
      msisdnMasked: '+25884****001',
    },
    files: {
      docFrontFileId: 'file-doc-front',
      docBackFileId: 'file-doc-back',
      selfieFileId: 'file-selfie',
    },
    previousChecks: [],
  });
  const decide = (route: Route, body: Json, apply: () => void) => {
    if (state.proposalStatus !== 'UNDER_REVIEW')
      return problem(route, 409, 'PROPOSAL_NOT_UNDER_REVIEW');
    if (body.version !== state.proposalVersion)
      return problem(route, 409, 'CONCURRENT_MODIFICATION');
    apply();
    state.proposalVersion += 1;
    return json(route, 200, proposal());
  };

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
    if (key === 'GET /api/v1/me') return json(route, 200, me);
    if (pathname.startsWith('/api/v1/files/')) {
      return route.fulfill({ status: 200, contentType: 'image/png', body: png });
    }
    if (key === 'GET /api/v1/insurer/proposals') {
      state.listQueries.push(url.search);
      const statuses = url.searchParams.getAll('status');
      const content =
        statuses.length === 0 || statuses.includes(state.proposalStatus) ? [summary()] : [];
      return json(route, 200, { content, page: 0, size: 20, totalElements: content.length });
    }
    if (key === `GET /api/v1/insurer/proposals/${proposalId}`) return json(route, 200, proposal());
    if (key === `POST /api/v1/insurer/proposals/${proposalId}/accept`) {
      const body = request.postDataJSON() as Json;
      state.accepts.push(body);
      if (state.kycState !== 'VERIFIED') return problem(route, 409, 'KYC_NOT_VERIFIED');
      return decide(route, body, () => (state.proposalStatus = 'ACCEPTED'));
    }
    if (key === `POST /api/v1/insurer/proposals/${proposalId}/reject`) {
      const body = request.postDataJSON() as Json;
      state.rejects.push(body);
      return decide(route, body, () => (state.proposalStatus = 'REJECTED'));
    }
    if (key === `POST /api/v1/insurer/proposals/${proposalId}/request-info`) {
      const body = request.postDataJSON() as Json;
      state.infoRequests.push(body);
      return decide(route, body, () => (state.proposalStatus = 'INFO_REQUESTED'));
    }
    if (key === `POST /api/v1/insurer/inspections/${inspectionId}/approve`) {
      const body = request.postDataJSON() as Json;
      state.approvals.push(body);
      if (body.version !== state.inspectionVersion) {
        return problem(route, 409, 'CONCURRENT_MODIFICATION');
      }
      state.inspectionStatus = 'APPROVED';
      state.inspectionVersion += 1;
      return json(route, 200, inspection());
    }
    if (key === 'GET /api/v1/insurer/kyc') {
      const content =
        state.kycStatus === 'PENDING'
          ? [
              {
                id: kycId,
                fullName: 'Ana Maria Cossa',
                idDocType: 'BI',
                status: 'PENDING',
                submittedAt: '2027-01-07T08:00:00Z',
              },
            ]
          : [];
      return json(route, 200, { content, page: 0, size: 20, totalElements: content.length });
    }
    if (key === `GET /api/v1/insurer/kyc/${kycId}`) return json(route, 200, kycCheck());
    if (key === `POST /api/v1/insurer/kyc/${kycId}/verify`) {
      state.verifications.push(request.postDataJSON());
      state.kycStatus = 'VERIFIED';
      state.kycState = 'VERIFIED';
      return json(route, 200, kycCheck());
    }
    if (key === 'GET /api/v1/insurer/inspections') {
      return json(route, 200, { content: [], page: 0, size: 20, totalElements: 0 });
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

async function signIn(p: Page, email = underwriter.email) {
  await p.goto('/entrar?voltar=%2Fseguradora%2Fpropostas');
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

async function openProposal(p: Page) {
  await signIn(p);
  await expect(p).toHaveURL(/\/seguradora\/propostas$/);
  await p.getByText('PR-2027-000123').first().click();
  await expect(p).toHaveURL(new RegExp(`/seguradora/propostas/${proposalId}$`));
  await expect(p.getByRole('heading', { level: 1, name: 'PR-2027-000123' })).toBeVisible();
}

test.describe('propostas da seguradora', () => {
  test('aprova a vistoria, verifica o KYC e aceita', async ({ page: p }) => {
    const state = await mockApi(p);
    await signIn(p);
    await expect(p).toHaveURL(/\/seguradora\/propostas$/);
    // Filtro por defeito "Em análise", pela mais antiga.
    await expect(p.getByRole('combobox', { name: 'Estado' })).toHaveText('Em análise');
    await expect(p.getByRole('grid').getByText('PR-2027-000123')).toBeVisible();
    expect(state.listQueries[0]).toContain('status=UNDER_REVIEW');
    expect(state.listQueries[0]).toContain('sort=submittedAt,asc');

    await p.getByRole('grid').getByText('PR-2027-000123').click();
    await expect(p.getByRole('heading', { level: 1, name: 'PR-2027-000123' })).toBeVisible();
    await expect(p.getByRole('button', { name: 'Aceitar' })).toBeDisabled();

    // Galeria da vistoria.
    const gallery = p.getByRole('list', { name: 'Vistoria' });
    await expect(gallery.getByRole('img', { name: 'Frente' })).toBeVisible();
    await expect(gallery.getByRole('img')).toHaveCount(6);
    await p.getByRole('button', { name: 'Ampliar Traseira' }).click();
    const lightbox = p.getByRole('dialog', { name: 'Traseira' });
    await expect(lightbox).toBeVisible();
    await p.keyboard.press('ArrowRight');
    await expect(p.getByRole('dialog', { name: 'Lado esquerdo' })).toBeVisible();
    await p.keyboard.press('Escape');

    await p.getByRole('button', { name: 'Aprovar vistoria' }).click();
    await p
      .getByRole('dialog', { name: 'Aprovar a vistoria' })
      .getByRole('button', { name: 'Aprovar' })
      .click();
    await expect(p.getByText('Vistoria aprovada.')).toBeVisible();
    expect(state.approvals).toEqual([{ version: 3 }]);

    // KYC pela secção "Identidade".
    await p.getByRole('button', { name: 'Rever identidade' }).click();
    await expect(p).toHaveURL(new RegExp(`/seguradora/propostas/kyc/${kycId}$`));
    await expect(p.getByText('110100123456A')).toBeVisible();
    await expect(p.getByRole('img', { name: 'Selfie' })).toBeVisible();
    await p.getByRole('button', { name: 'Confirmar identidade' }).click();
    await p.getByRole('dialog').getByRole('button', { name: 'Confirmar identidade' }).click();
    await expect(p).toHaveURL(new RegExp(`/seguradora/propostas/${proposalId}$`));
    await expect(p.getByText('Identidade confirmada')).toBeVisible();
    expect(state.verifications).toEqual([{ version: 0 }]);

    await expect(p.getByRole('button', { name: 'Aceitar' })).toBeEnabled();
    await p.getByRole('button', { name: 'Aceitar' }).click();
    const accept = p.getByRole('dialog', { name: 'Aceitar a proposta PR-2027-000123' });
    await accept.getByLabel('Nota (opcional)').fill('Tudo conforme.');
    await accept.getByRole('button', { name: 'Aceitar' }).click();
    await expect(p).toHaveURL(/\/seguradora\/propostas$/);
    await expect(p.getByText('Proposta aceite')).toBeVisible();
    expect(state.accepts).toEqual([{ note: 'Tudo conforme.', version: 7 }]);
  });

  test('recusa com motivo', async ({ page: p }) => {
    const state = await mockApi(p);
    await openProposal(p);
    await p.getByRole('button', { name: 'Recusar', exact: true }).click();
    const dialog = p.getByRole('dialog', { name: 'Recusar a proposta PR-2027-000123' });
    await dialog.getByRole('combobox', { name: /Motivo/ }).click();
    await p.getByRole('option', { name: 'Declarações incorrectas' }).click();
    await dialog
      .getByLabel(/Explicação para o cliente/)
      .fill('Os sinistros declarados não batem certo.');
    await dialog.getByRole('button', { name: 'Recusar' }).click();
    await expect(p).toHaveURL(/\/seguradora\/propostas$/);
    await expect(p.getByText('Proposta recusada')).toBeVisible();
    expect(state.rejects).toEqual([
      {
        reasonCode: 'FALSE_DECLARATION',
        reason: 'Os sinistros declarados não batem certo.',
        version: 7,
      },
    ]);
  });

  test('pedido de informação com o item Vistoria', async ({ page: p }) => {
    const state = await mockApi(p);
    await openProposal(p);
    await p.getByRole('button', { name: 'Pedir informação' }).click();
    const dialog = p.getByRole('dialog', { name: 'Pedir informação ao cliente' });
    await expect(
      dialog.getByText('A assinatura do cliente deixa de valer e ele terá de assinar de novo.'),
    ).toBeVisible();
    await dialog.getByRole('checkbox', { name: 'Vistoria' }).check();
    await dialog.getByLabel(/Mensagem para o cliente/).fill('Repita a fotografia do chassis.');
    await dialog.getByRole('button', { name: 'Enviar pedido' }).click();
    await expect(p).toHaveURL(/\/seguradora\/propostas$/);
    await expect(p.getByText('Pedido de informação enviado')).toBeVisible();
    expect(state.infoRequests).toEqual([
      { items: ['INSPECTION'], message: 'Repita a fotografia do chassis.', version: 7 },
    ]);
  });

  test('FINANCE (sem proposal:read) vai para /sem-acesso', async ({ page: p }) => {
    await mockApi(p, finance);
    await signIn(p, finance.email);
    await expect(p).toHaveURL(/\/sem-acesso$/);
  });

  test.describe('a 360×740', () => {
    test.use({ viewport: { width: 360, height: 740 } });

    test('fila em cartões, filtros na gaveta e barra de decisão no fundo', async ({ page: p }) => {
      await mockApi(p);
      await signIn(p);
      await expect(p).toHaveURL(/\/seguradora\/propostas$/);
      const card = p.getByTestId('proposal-card');
      await expect(card).toContainText('PR-2027-000123');
      await expect(card).toContainText('Ana Maria Cossa');
      await expect(card).toContainText('Prazo a terminar');
      await expect(p.getByRole('grid')).toHaveCount(0);
      await expectNoHorizontalScroll(p);

      await p.getByRole('button', { name: 'Filtros' }).click();
      const drawer = p.getByRole('region', { name: 'Filtros' });
      await expect(drawer.getByRole('combobox', { name: 'Estado' })).toBeVisible();
      await drawer.getByRole('button', { name: 'Fechar' }).click();
      await expect(drawer).toBeHidden();

      await card.click();
      await expect(p.getByRole('heading', { level: 1, name: 'PR-2027-000123' })).toBeVisible();
      const bar = p.getByTestId('decision-bar');
      await expect(bar).toBeVisible();
      const box = await bar.boundingBox();
      expect(box).not.toBeNull();
      expect(Math.round(box!.y + box!.height)).toBe(740);
      expect(await bar.evaluate((el) => getComputedStyle(el).position)).toBe('fixed');
      await expectNoHorizontalScroll(p);

      // Só o resumo está aberto; a vistoria abre-se com o toque.
      await p.getByRole('button', { name: 'Vistoria' }).click();
      await expect(p.getByRole('img', { name: 'Frente' })).toBeVisible();
      await expectNoHorizontalScroll(p);
    });
  });
});
