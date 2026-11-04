import { render } from '@testing-library/react';
import type { ReactElement } from 'react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { vi } from 'vitest';
import type { ProposalResponse, QuoteRequestResponse } from '../api/generated';
import { AppProviders } from '../App';
import {
  WizardContext,
  type WizardContextValue,
} from '../features/proposal-wizard/useProposalMutations';
import { routerFutureFlags } from '../router';
import { testQueryClient } from './render';

/** Dados de teste das propostas do cliente e do mediador (S4-08). */

const mzn = (amount: number) => ({ amount, currency: 'MZN' as const });

export function proposalFixture(overrides: Partial<ProposalResponse> = {}): ProposalResponse {
  return {
    id: 'p1',
    number: 'PR-2027-000123',
    status: 'DRAFT',
    channel: 'WEB',
    broker: null,
    insurer: { id: 'i1', displayName: 'Seguradora Demo' },
    quoteId: 'q1',
    createdAt: '2027-01-05T09:30:00Z',
    updatedAt: '2027-01-05T09:30:00Z',
    submittedAt: null,
    startAt: '2027-01-14T22:00:00Z',
    endAt: '2028-01-14T22:00:00Z',
    endDate: '2028-01-14',
    durationDays: 365,
    version: 3,
    checklist: {
      declarations: 'MISSING',
      suitability: 'MISSING',
      inspection: 'MISSING',
      signature: 'MISSING',
    },
    product: {
      id: 'prod1',
      name: 'Auto Completo',
      type: 'COMPREHENSIVE',
      versionId: 'pv1',
      documents: [
        { type: 'GENERAL_CONDITIONS', fileId: 'f-gc' },
        { type: 'PRE_CONTRACT_INFO', fileId: 'f-pci' },
      ],
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
    policyholderSource: 'PROFILE',
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
      fullName: 'Ana Maria Cossa',
      licenceIssueDate: '2011-04-04',
      licenceNumberMasked: '****4567',
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
        capital: mzn(120000000),
        deductible: { type: 'PERCENT', percent: '10', minAmount: mzn(2500000) },
        premium: mzn(4500000),
      },
    ],
    premium: {
      commercial: mzn(4995000),
      charges: [
        { code: 'STAMP', labelPt: 'Imposto de selo', labelEn: 'Stamp duty', amount: mzn(231110) },
      ],
      roundingAdjustment: mzn(90),
      total: mzn(5226200),
    },
    inspection: {
      id: 'insp1',
      required: true,
      mode: 'SELF_PHOTOS',
      status: 'PENDING',
      maxExtraPhotos: 6,
      missingSlots: ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'],
      photos: [],
      version: 0,
    },
    kyc: { state: 'NONE', requiredForAutomaticAcceptance: true },
    declarations: null,
    suitability: null,
    signature: null,
    decision: null,
    expiresAt: '2027-01-20T09:30:00Z',
    ...overrides,
  };
}

/** Pedido de cotação do mediador (S3-02) com uma cotação. */
export function brokerQuoteRequestFixture(
  overrides: Partial<QuoteRequestResponse> = {},
): QuoteRequestResponse {
  return {
    id: 'req1',
    origin: 'BROKER',
    clientMsisdn: '+258840000002',
    createdAt: '2027-01-10T08:15:00Z',
    productType: 'COMPREHENSIVE',
    startDate: '2027-01-15',
    endDate: '2028-01-14',
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
      make: null,
      model: null,
      vehicleId: null,
    },
    driver: {
      birthDate: '1991-09-09',
      licenceIssueDate: '2011-04-04',
      licenceCategories: ['B'],
      driverId: null,
    },
    quotes: [],
    ...overrides,
  };
}

/** Contexto falso dos passos (as funções são `vi.fn()`). */
export function wizardContext(
  proposal: ProposalResponse,
  overrides: Partial<WizardContextValue> = {},
): WizardContextValue {
  return {
    proposal,
    mode: 'customer',
    current: 'tomador',
    update: vi.fn(),
    handleCommon: vi.fn(() => false),
    reload: vi.fn(() => Promise.resolve()),
    goTo: vi.fn(),
    next: vi.fn(),
    back: vi.fn(),
    kycSkipped: false,
    skipKyc: vi.fn(),
    ...overrides,
  };
}

/** Mostra um passo dentro do `WizardContext`, de um router de memória e dos fornecedores. */
export function renderStep(ui: ReactElement, context: WizardContextValue) {
  const router = createMemoryRouter(
    [
      {
        path: '*',
        element: <WizardContext.Provider value={context}>{ui}</WizardContext.Provider>,
      },
    ],
    { initialEntries: ['/cliente/propostas/p1'], future: routerFutureFlags },
  );
  return render(
    <AppProviders queryClient={testQueryClient()}>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </AppProviders>,
  );
}
