import { expect, type Page, type Route } from '@playwright/test';

/** API simulada das propostas (S4-08) para os testes Playwright do cliente e do mediador. */

/* eslint-disable @typescript-eslint/no-explicit-any */
type Json = any;

export const requestId = '0193a000-0000-7000-8000-000000000801';
export const quoteId = '0193a000-0000-7000-8000-000000000802';
export const proposalId = '0193a000-0000-7000-8000-000000000803';

export const mzn = (amount: number) => ({ amount, currency: 'MZN' });

/** Hoje em `Africa/Maputo` mais `days` dias (`AAAA-MM-DD`). */
export function maputoDate(days = 0): string {
  const today = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'Africa/Maputo',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date());
  const date = new Date(`${today}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() + days);
  return date.toISOString().slice(0, 10);
}

/** 00:00 de um dia de Maputo em UTC (22:00 do dia anterior). */
function maputoMidnight(date: string): string {
  const day = new Date(`${date}T00:00:00Z`);
  day.setUTCHours(day.getUTCHours() - 2);
  return day.toISOString();
}

export function quoteFixture(comprehensive: boolean) {
  return {
    id: quoteId,
    status: 'OFFERED',
    insurer: { id: 'i-demo', displayName: 'Seguradora Demo', shortCode: 'DEMO', logoFileId: null },
    product: {
      id: 'prod-demo',
      code: comprehensive ? 'AUTO-COMPLETO' : 'RC-AUTO',
      name: comprehensive ? 'Auto Completo' : 'RC Auto',
      type: comprehensive ? 'COMPREHENSIVE' : 'MTPL_ONLY',
      versionId: 'pv-demo',
      versionNo: 1,
      generalConditionsFileId: '0193a000-0000-7000-8000-000000000901',
      preContractInfoFileId: '0193a000-0000-7000-8000-000000000902',
    },
    coverages: [
      {
        code: 'MTPL',
        mandatory: true,
        capital: mzn(300000000),
        deductible: { type: 'NONE' },
        premium: mzn(495000),
      },
      ...(comprehensive
        ? [
            {
              code: 'OD_COLLISION',
              mandatory: false,
              capital: mzn(120000000),
              deductible: { type: 'PERCENT', percent: '10', minAmount: mzn(2500000) },
              premium: mzn(4500000),
            },
          ]
        : []),
    ],
    commercialPremium: mzn(comprehensive ? 4995000 : 495000),
    charges: [
      { code: 'STAMP', labelPt: 'Imposto de selo', labelEn: 'Stamp duty', amount: mzn(23110) },
    ],
    roundingAdjustment: mzn(90),
    totalAmount: mzn(comprehensive ? 5018200 : 518200),
    validUntil: '2030-01-25T08:15:00Z',
  };
}

export function quoteRequestFixture(comprehensive: boolean, origin: 'CUSTOMER' | 'BROKER') {
  return {
    id: requestId,
    origin,
    clientMsisdn: origin === 'BROKER' ? '+258840000002' : null,
    createdAt: '2026-11-04T08:15:00Z',
    productType: comprehensive ? 'COMPREHENSIVE' : 'MTPL_ONLY',
    startDate: maputoDate(1),
    endDate: maputoDate(365),
    durationDays: 365,
    requestedCoverages: [],
    vehicle: {
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      year: 2019,
      provinceOfCirculation: 'MAPUTO_CITY',
      hasTracker: false,
      commercialValue: mzn(120000000),
      plate: 'AAA-100-MC',
      engineCc: 1598,
      seats: 5,
      grossWeightKg: null,
      make: origin === 'CUSTOMER' ? 'Toyota' : null,
      model: origin === 'CUSTOMER' ? 'Corolla' : null,
      vehicleId: null,
    },
    driver: {
      birthDate: '1991-09-09',
      licenceIssueDate: '2011-04-04',
      licenceCategories: ['B'],
      driverId: null,
    },
    quotes: [quoteFixture(comprehensive)],
  };
}

const allSlots = ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'];

export function proposalFixture(options: {
  comprehensive: boolean;
  channel: 'WEB' | 'BROKER';
  policyholderSource: 'PROFILE' | 'BROKER';
  kycState: string;
}): Json {
  const quote = quoteFixture(options.comprehensive);
  const start = maputoDate(1);
  return {
    id: proposalId,
    number: 'PR-2026-000123',
    status: 'DRAFT',
    channel: options.channel,
    broker: options.channel === 'BROKER' ? { id: 'o-broker', displayName: 'Corretora Demo' } : null,
    insurer: { id: 'i-demo', displayName: 'Seguradora Demo' },
    quoteId,
    createdAt: '2026-11-04T09:30:00Z',
    updatedAt: '2026-11-04T09:30:00Z',
    submittedAt: null,
    startAt: maputoMidnight(start),
    endAt: maputoMidnight(maputoDate(366)),
    endDate: maputoDate(365),
    durationDays: 365,
    version: 1,
    checklist: {
      declarations: 'MISSING',
      suitability: 'MISSING',
      inspection: options.comprehensive ? 'MISSING' : 'NOT_REQUIRED',
      signature: 'MISSING',
    },
    product: {
      id: quote.product.id,
      name: quote.product.name,
      type: quote.product.type,
      versionId: quote.product.versionId,
      documents: [
        { type: 'GENERAL_CONDITIONS', fileId: quote.product.generalConditionsFileId },
        { type: 'PRE_CONTRACT_INFO', fileId: quote.product.preContractInfoFileId },
      ],
    },
    policyholder: {
      fullName: options.channel === 'BROKER' ? 'João Carlos Sitoe' : 'Ana Maria Mussa',
      birthDate: '1991-09-09',
      idDocType: 'BI',
      idDocNumberMasked: '*********432B',
      nuitMasked: '*****6789',
      province: 'MAPUTO',
      msisdnMasked: '+25884****002',
    },
    policyholderSource: options.policyholderSource,
    vehicle: {
      id: 'v1',
      plate: 'AAA-100-MC',
      make: 'Toyota',
      model: 'Corolla',
      year: 2019,
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      commercialValue: mzn(120000000),
    },
    driver: {
      id: 'd1',
      fullName: 'João Carlos Sitoe',
      licenceIssueDate: '2011-04-04',
      licenceNumberMasked: '****3456',
    },
    coverages: quote.coverages,
    premium: {
      commercial: quote.commercialPremium,
      charges: quote.charges,
      roundingAdjustment: quote.roundingAdjustment,
      total: quote.totalAmount,
    },
    inspection: options.comprehensive
      ? {
          id: 'insp1',
          required: true,
          mode: 'SELF_PHOTOS',
          status: 'PENDING',
          maxExtraPhotos: 6,
          missingSlots: [...allSlots],
          photos: [],
          version: 0,
        }
      : { required: false },
    kyc: { state: options.kycState, requiredForAutomaticAcceptance: true },
    declarations: null,
    suitability: null,
    signature: null,
    decision: null,
    expiresAt: '2026-11-19T09:30:00Z',
  };
}

/** PNG de 1×1. */
const png = Buffer.from(
  'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
  'base64',
);

export interface ProposalApi {
  proposal: Json;
  calls: string[];
  bodies: Record<string, Json[]>;
  /** Estado da proposta depois de `submitProposal`. */
  submitAs: 'UNDER_REVIEW' | 'ACCEPTED';
  suitability: 'SUITABLE' | 'WARNED';
}

export async function mockProposalApi(
  page: Page,
  options: {
    me: Json;
    quoteRequest: Json;
    proposal: Json;
    submitAs: 'UNDER_REVIEW' | 'ACCEPTED';
    suitability: 'SUITABLE' | 'WARNED';
    /** `POST /broker/proposals`: resposta conforme o corpo. */
    createForClient?: (body: Json) => { status: number; body: Json };
  },
): Promise<ProposalApi> {
  const state: ProposalApi = {
    proposal: options.proposal,
    calls: [],
    bodies: {},
    submitAs: options.submitAs,
    suitability: options.suitability,
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string, extra: Json = {}) =>
    json(route, status, { type: 'about:blank', title: code, status, code, ...extra });
  const changed = () => {
    const p = state.proposal;
    p.version += 1;
    p.signature = null;
    p.checklist.signature = 'MISSING';
  };
  const base = `/api/v1/proposals/${proposalId}`;

  await page.route('**/api/v1/**', async (route) => {
    const req = route.request();
    const url = new URL(req.url());
    const key = `${req.method()} ${url.pathname}`;
    state.calls.push(key);
    const body = () => {
      const data = req.postDataJSON() as Json;
      (state.bodies[key] ??= []).push(data);
      return data;
    };
    const p = state.proposal;

    if (key === 'POST /api/v1/auth/refresh') {
      return json(route, 200, {
        accessToken: 'access-1',
        refreshToken: 'refresh-1',
        expiresIn: 900,
      });
    }
    if (key === 'GET /api/v1/auth/otp/options') {
      return json(route, 200, {
        codeLength: 6,
        customerCanChoose: false,
        channels: { MSISDN: ['SMS'] },
        defaultChannels: { MSISDN: 'SMS' },
        identifierTypes: ['MSISDN'],
        resendAfterSeconds: 60,
      });
    }
    if (req.headers()['authorization'] !== 'Bearer access-1') {
      return problem(route, 401, 'UNAUTHORIZED');
    }
    if (key === 'GET /api/v1/me') return json(route, 200, options.me);
    if (key === 'GET /api/v1/me/profile') {
      return json(route, 200, {
        complete: true,
        fullName: 'Ana Maria Mussa',
        birthDate: '1991-09-09',
        idDocType: 'BI',
        idDocNumberMasked: '*********432B',
        nuitMasked: '*****6789',
        province: 'MAPUTO',
        version: 1,
      });
    }
    if (key === 'GET /api/v1/me/kyc') return json(route, 200, { state: p.kyc.state });
    if (key === `GET /api/v1/quotes/${requestId}`) return json(route, 200, options.quoteRequest);
    if (key.startsWith('GET /api/v1/files/')) {
      return route.fulfill({ status: 200, contentType: 'image/png', body: png });
    }
    if (key === 'POST /api/v1/proposals') {
      body();
      return json(route, 201, p);
    }
    if (key === 'POST /api/v1/broker/proposals' && options.createForClient) {
      const result = options.createForClient(body());
      return json(route, result.status, result.body);
    }
    if (key === `GET ${base}`) return json(route, 200, p);
    if (key === `PUT ${base}` || key === `PUT /api/v1/broker/proposals/${proposalId}`) {
      body();
      changed();
      return json(route, 200, p);
    }
    if (key === `POST ${base}/declarations`) {
      const data = body();
      p.declarations = { answers: data.answers, confirmedAt: '2026-11-04T10:00:00Z' };
      p.checklist.declarations = 'DONE';
      changed();
      return json(route, 200, p);
    }
    if (key === `POST ${base}/suitability`) {
      const data = body();
      const warned = state.suitability === 'WARNED';
      p.suitability = {
        answers: data.answers,
        answeredAt: '2026-11-04T10:05:00Z',
        result: state.suitability,
        reasons: warned ? ['DEDUCTIBLE_NOT_WANTED'] : [],
        acknowledgedAt: null,
      };
      p.checklist.suitability = warned ? 'ACK_REQUIRED' : 'DONE';
      changed();
      return json(route, 200, p);
    }
    if (key === `POST ${base}/suitability/acknowledge`) {
      body();
      p.suitability.acknowledgedAt = '2026-11-04T10:06:00Z';
      p.checklist.suitability = 'DONE';
      changed();
      return json(route, 200, p);
    }
    if (key === `POST ${base}/inspection/photos`) {
      const raw = req.postDataBuffer()?.toString('latin1') ?? '';
      const slot = /name="slot"\r\n\r\n(\w+)/.exec(raw)?.[1] ?? 'EXTRA';
      (state.bodies[key] ??= []).push({
        slot,
        source: /name="source"\r\n\r\n(\w+)/.exec(raw)?.[1],
      });
      const inspection = p.inspection;
      const photo = {
        id: `ph-${slot}-${inspection.photos.length}`,
        slot,
        fileId: `0193a000-0000-7000-8000-0000000010${String(inspection.photos.length).padStart(2, '0')}`,
        source: 'UPLOAD',
        uploadedAt: '2026-11-04T10:10:00Z',
      };
      inspection.photos = [
        ...inspection.photos.filter((ph: Json) => slot === 'EXTRA' || ph.slot !== slot),
        photo,
      ];
      inspection.missingSlots = inspection.missingSlots.filter((s: string) => s !== slot);
      if (inspection.status === 'REJECTED') inspection.status = 'PENDING';
      changed();
      return json(route, 201, photo);
    }
    if (key === `POST ${base}/inspection/submit`) {
      const data = body();
      if (p.inspection.missingSlots.length > 0) {
        return problem(route, 409, 'INSPECTION_PHOTOS_MISSING', {
          missingSlots: p.inspection.missingSlots,
        });
      }
      p.inspection.status = 'SUBMITTED';
      p.inspection.odometerKm = data.odometerKm;
      p.checklist.inspection = 'DONE';
      changed();
      return json(route, 200, p);
    }
    if (key === `POST ${base}/sign/request`) {
      const data = body();
      expect(data.documentsAcknowledged).toBe(true);
      return json(route, 200, {
        challengeId: 'ch-1',
        channel: 'SMS',
        destinationMasked: '+25884****002',
        documentFileId: '0193a000-0000-7000-8000-000000000903',
        documentHash: 'a'.repeat(64),
        expiresAt: '2026-11-04T10:20:00Z',
        fallbackUsed: false,
        resendAfterSeconds: 60,
      });
    }
    if (key === `POST ${base}/sign/confirm`) {
      const data = body();
      if (data.code !== '123456')
        return problem(route, 400, 'OTP_INVALID', { attemptsRemaining: 2 });
      p.version += 1;
      p.checklist.signature = 'DONE';
      p.signature = {
        assisted: options.me.type === 'STAFF',
        channel: 'SMS',
        destinationMasked: '+25884****002',
        documentFileId: '0193a000-0000-7000-8000-000000000903',
        documentHash: 'a'.repeat(64),
        signedAt: '2026-11-04T10:15:00Z',
      };
      return json(route, 200, p);
    }
    if (key === `POST ${base}/submit`) {
      body();
      p.version += 1;
      p.status = state.submitAs;
      p.submittedAt = '2026-11-04T10:16:00Z';
      p.infoRequest = undefined;
      p.decision =
        state.submitAs === 'ACCEPTED'
          ? { mode: 'AUTOMATIC', decidedAt: '2026-11-04T10:16:00Z' }
          : { insurerDueAt: '2026-11-19T10:16:00Z' };
      p.expiresAt = state.submitAs === 'ACCEPTED' ? '2026-11-11T10:16:00Z' : null;
      return json(route, 200, p);
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

export async function expectNoHorizontalScroll(page: Page) {
  const overflow = await page.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

/** Abre um caminho; sob carga repete uma vez antes de falhar. */
export async function open(page: Page, path: string) {
  try {
    await page.goto(path);
  } catch {
    await page.goto(path);
  }
}

/** Responde "Não" às cinco declarações (com a caixa de veracidade) e continua. */
export async function answerDeclarations(page: Page) {
  await expect(page.getByText(/· Declarações$/)).toBeVisible();
  for (const group of await page.getByRole('radiogroup').all()) {
    await group.getByRole('radio', { name: 'Não' }).check();
  }
  await page.getByRole('checkbox', { name: /Declaro que as respostas/ }).check();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();
}

/** Responde à adequação (a primeira opção de cada pergunta) e continua. */
export async function answerSuitability(page: Page) {
  await expect(page.getByText(/· Adequação$/)).toBeVisible();
  for (const group of await page.getByRole('radiogroup').all()) {
    await group.getByRole('radio').first().check();
  }
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();
}

/** No resumo: confirma os documentos, assina com o código e envia. */
export async function signAndSubmit(page: Page, sentText: RegExp | string) {
  await expect(page.getByText(/· Resumo e assinatura$/)).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page
    .getByRole('checkbox', {
      name: 'Recebi e li as Condições Gerais e a Informação Pré-contratual.',
    })
    .check();
  await page.getByRole('button', { name: 'Assinar e enviar à seguradora' }).click();
  const dialog = page.getByRole('dialog');
  await expect(dialog.getByTestId('sign-sent-to')).toContainText(sentText);
  await expectNoHorizontalScroll(page);
  await dialog.locator('input[autocomplete="one-time-code"]').fill('123456');
}
