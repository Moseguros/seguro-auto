import type {
  CatalogProductResponse,
  CoverageResponse,
  QuoteRequestResponse,
  QuoteRequestSummaryResponse,
  QuoteResponse,
} from '../api/generated';

import { formatMoney } from '../format/money';

/** Dados de teste das cotações (S3-04). */

/** `formatMoney` com espaços normais (como o `toHaveTextContent` compara). */
export function money(amount: number): string {
  return formatMoney({ amount }).replace(/\s/g, ' ');
}

const mzn = (amount: number) => ({ amount, currency: 'MZN' as const });

export function quoteFixture(overrides: Partial<QuoteResponse> = {}): QuoteResponse {
  return {
    id: 'q1',
    status: 'OFFERED',
    insurer: { id: 'i1', displayName: 'Seguradora Demo', shortCode: 'DEMO', logoFileId: null },
    product: {
      id: 'p1',
      code: 'DEMO-AUTO',
      name: 'Auto Demo',
      type: 'COMPREHENSIVE',
      versionId: 'pv1',
      versionNo: 1,
      generalConditionsFileId: 'f-gc-1',
      preContractInfoFileId: 'f-pci-1',
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
        premium: mzn(3549383),
      },
    ],
    commercialPremium: mzn(5093766),
    charges: [
      {
        code: 'DEMO_STAMP',
        labelPt: 'Imposto de selo (provisório)',
        labelEn: 'Stamp duty (provisional)',
        amount: mzn(127344),
      },
    ],
    roundingAdjustment: mzn(90),
    totalAmount: mzn(5226200),
    validUntil: '2027-01-25T08:15:00Z',
    ...overrides,
  };
}

export const secondQuote = quoteFixture({
  id: 'q2',
  insurer: { id: 'i2', displayName: 'Seguradora B', shortCode: 'DEMOB', logoFileId: null },
  product: {
    id: 'p2',
    code: 'B-AUTO',
    name: 'Auto B',
    type: 'COMPREHENSIVE',
    versionId: 'pv2',
    versionNo: 2,
    generalConditionsFileId: 'f-gc-2',
    preContractInfoFileId: 'f-pci-2',
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
      deductible: { type: 'FIXED', amount: mzn(500000) },
      premium: mzn(4000000),
    },
  ],
  commercialPremium: mzn(4495000),
  charges: [],
  roundingAdjustment: mzn(0),
  totalAmount: mzn(6100000),
});

export const unavailableQuote: QuoteResponse = {
  id: 'q3',
  status: 'NOT_AVAILABLE',
  insurer: { id: 'i3', displayName: 'Seguradora C', shortCode: 'DEMOC', logoFileId: null },
  product: {
    id: 'p3',
    code: 'C-AUTO',
    name: 'Auto C',
    type: 'COMPREHENSIVE',
    versionId: 'pv3',
    versionNo: 1,
  },
  unavailableReason: 'NO_MATCHING_ROW',
};

export function requestFixture(
  overrides: Partial<QuoteRequestResponse> = {},
): QuoteRequestResponse {
  return {
    id: 'r1',
    origin: 'CUSTOMER',
    clientMsisdn: null,
    createdAt: '2027-01-10T08:15:00Z',
    productType: 'COMPREHENSIVE',
    vehicle: {
      vehicleId: 'v1',
      plate: 'AAA-100-MC',
      make: 'Toyota',
      model: 'Corolla',
      year: 2018,
      engineCc: 1598,
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      seats: 5,
      grossWeightKg: null,
      provinceOfCirculation: 'MAPUTO_CITY',
      commercialValue: mzn(85000000),
      hasTracker: false,
    },
    driver: {
      driverId: 'd1',
      birthDate: '1990-05-14',
      licenceIssueDate: '2010-06-01',
      licenceCategories: ['B'],
    },
    requestedCoverages: [{ code: 'OD_COLLISION' }],
    startDate: '2027-01-15',
    endDate: '2028-01-14',
    durationDays: 365,
    quotes: [quoteFixture(), secondQuote, unavailableQuote],
    ...overrides,
  };
}

export function summaryFixture(
  overrides: Partial<QuoteRequestSummaryResponse> = {},
): QuoteRequestSummaryResponse {
  return {
    id: 'r1',
    createdAt: '2027-01-10T08:15:00Z',
    origin: 'CUSTOMER',
    productType: 'COMPREHENSIVE',
    vehicle: { plate: 'AAA-100-MC', make: 'Toyota', model: 'Corolla' },
    clientMsisdn: null,
    startDate: '2027-01-15',
    durationDays: 365,
    offeredCount: 2,
    bestTotal: mzn(5226200),
    validUntil: '2027-01-25T08:15:00Z',
    expired: false,
    ...overrides,
  };
}

function coverage(code: CoverageResponse['code'], extra: Partial<CoverageResponse> = {}) {
  return {
    code,
    mandatory: false,
    capitalType: 'VEHICLE_VALUE',
    deductibleType: 'NONE',
    ...extra,
  } as CoverageResponse;
}

export function catalogProduct(
  overrides: Partial<CatalogProductResponse> = {},
): CatalogProductResponse {
  return {
    productId: 'p1',
    productVersionId: 'pv1',
    code: 'DEMO-AUTO',
    name: 'Auto Demo',
    type: 'COMPREHENSIVE',
    status: 'PUBLISHED',
    insurer: { id: 'i1', displayName: 'Seguradora Demo' },
    usages: ['PRIVATE'],
    vehicleCategories: ['LIGHT_PASSENGER'],
    validFrom: '2026-01-01',
    versionNo: 1,
    documents: [],
    coverages: [
      coverage('MTPL', { mandatory: true, capitalType: 'FIXED', capital: mzn(300000000) }),
      coverage('VTPL', {
        capitalType: 'OPTIONS',
        capitalOptions: [mzn(500000000), mzn(1000000000)],
      }),
      coverage('OD_COLLISION'),
      coverage('OD_FIRE'),
      coverage('OD_NATURE'),
      coverage('OD_THEFT'),
    ],
    ...overrides,
  };
}

export const catalogProductB = catalogProduct({
  productId: 'p2',
  productVersionId: 'pv2',
  name: 'Auto B',
  insurer: { id: 'i2', displayName: 'Seguradora B' },
  coverages: [
    coverage('MTPL', { mandatory: true, capitalType: 'FIXED', capital: mzn(300000000) }),
    coverage('VTPL', {
      capitalType: 'OPTIONS',
      capitalOptions: [mzn(250000000), mzn(500000000)],
    }),
    coverage('OD_COLLISION'),
    coverage('OD_FIRE'),
    coverage('OD_NATURE'),
    coverage('OCCUPANTS', { capitalType: 'PER_PERSON', capital: mzn(10000000) }),
  ],
});

export const mtplProduct = catalogProduct({
  productId: 'p9',
  productVersionId: 'pv9',
  name: 'RC Demo',
  type: 'MTPL_ONLY',
  coverages: [coverage('MTPL', { mandatory: true, capitalType: 'FIXED', capital: mzn(300000000) })],
});

/** Botões laranja (`variant="cta"`) no documento. */
export function ctaButtons(): Element[] {
  return Array.from(document.querySelectorAll('.MuiButton-cta'));
}
