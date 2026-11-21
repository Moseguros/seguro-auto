import type {
  MyNotificationResponse,
  MyPolicySummaryResponse,
  NotificationPreferencesResponse,
  PolicyDetailResponse,
  PolicyDocument,
} from '../api/generated';

export const verificationUrl = 'https://moseguros.co.mz/v/abc123TOKEN';

export function policySummaryFixture(
  overrides: Partial<MyPolicySummaryResponse> = {},
): MyPolicySummaryResponse {
  return {
    id: 'pol-1',
    number: 'AP-2027-000123',
    status: 'ACTIVE',
    coverStatus: 'COVERED',
    coverageType: 'MTPL_ONLY',
    insurer: { id: 'i-1', name: 'Seguradora Demo' },
    productName: 'RC Auto',
    vehicle: { id: 'v-1', plate: 'AAA-123-MC', make: 'Toyota', model: 'Corolla', year: 2018 },
    startAt: '2027-01-15T00:00:00Z',
    endAt: '2028-01-14T21:59:59Z',
    certificateReady: true,
    ...overrides,
  };
}

export function documentsFixture(status: 'READY' | 'PENDING' = 'READY'): PolicyDocument[] {
  const ready = status === 'READY';
  return [
    {
      type: 'CERTIFICATE',
      status,
      fileId: ready ? 'file-cert' : undefined,
      fileName: ready ? 'certificado-AP-2027-000123.pdf' : undefined,
    },
    {
      type: 'SCHEDULE',
      status,
      fileId: ready ? 'file-schedule' : undefined,
      fileName: ready ? 'condicoes-particulares.pdf' : undefined,
    },
  ];
}

export function policyDetailFixture(
  overrides: Partial<PolicyDetailResponse> = {},
): PolicyDetailResponse {
  return {
    id: 'pol-1',
    number: 'AP-2027-000123',
    status: 'ACTIVE',
    coverStatus: 'COVERED',
    coverageType: 'MTPL_ONLY',
    insurer: { id: 'i-1', name: 'Seguradora Demo' },
    product: { name: 'RC Auto', type: 'MTPL_ONLY', versionId: 'pv-1' },
    vehicle: { id: 'v-1', plate: 'AAA-123-MC', make: 'Toyota', model: 'Corolla', year: 2018 },
    policyholderName: 'Ana Maria Mussa',
    startAt: '2027-01-15T00:00:00Z',
    endAt: '2028-01-14T21:59:59Z',
    issuedAt: '2027-01-10T08:00:00Z',
    paymentPlan: 'FULL',
    renewalType: 'ANNUAL_RENEWABLE',
    totalPremium: { amount: 518200, currency: 'MZN' },
    coverages: [
      {
        code: 'MTPL',
        mandatory: true,
        capital: { amount: 500000000, currency: 'MZN' },
        deductibleType: 'NONE',
        premium: { amount: 518200, currency: 'MZN' },
      },
    ],
    instalments: [
      {
        seq: 1,
        dueDate: '2027-01-15',
        amount: { amount: 518200, currency: 'MZN' },
        status: 'PAID',
        paidAt: '2027-01-10T08:00:00Z',
      },
    ],
    certificate: {
      id: 'cert-1',
      number: 'CT-2027-000123',
      status: 'VALID',
      validFrom: '2027-01-15T00:00:00Z',
      validTo: '2028-01-14T21:59:59Z',
      verificationUrl,
      fileId: 'file-cert',
    },
    documents: documentsFixture('READY'),
    ...overrides,
  };
}

export function notificationFixture(
  overrides: Partial<MyNotificationResponse> = {},
): MyNotificationResponse {
  return {
    id: 'n-1',
    category: 'POLICY',
    templateCode: 'POLICY_ISSUED',
    title: 'Apólice emitida',
    body: 'A sua apólice AP-2027-000123 foi emitida.',
    createdAt: new Date(Date.now() - 5 * 60_000).toISOString(),
    webPath: '/cliente/apolices/pol-1',
    ...overrides,
  };
}

const allChannels = (available: Partial<Record<string, boolean>> = {}) =>
  (['PUSH', 'WHATSAPP', 'SMS', 'EMAIL'] as const).map((channel) => ({
    channel,
    enabled: true,
    available: available[channel] ?? true,
  }));

/** Preferências pela ordem do enum; canais PUSH, WHATSAPP, SMS, EMAIL. */
export function preferencesFixture(
  available: Partial<Record<string, boolean>> = { PUSH: false, EMAIL: false },
): NotificationPreferencesResponse {
  return {
    categories: [
      { category: 'PAYMENT', mandatory: true, channels: allChannels(available) },
      { category: 'POLICY', mandatory: true, channels: allChannels(available) },
      { category: 'LEGAL', mandatory: true, channels: allChannels(available) },
      { category: 'PROPOSAL', mandatory: false, channels: allChannels(available) },
      { category: 'QUOTE', mandatory: false, channels: allChannels(available) },
    ],
  };
}
