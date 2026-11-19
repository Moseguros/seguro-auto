import type { IntentView, ProposalPaymentMethodsResponse } from '../api/generated';

/** Dados de teste dos pagamentos do cliente (S5-11). */

const mzn = (amount: number) => ({ amount, currency: 'MZN' as const });

export function methodsFixture(
  overrides: Partial<ProposalPaymentMethodsResponse> = {},
): ProposalPaymentMethodsResponse {
  return {
    proposalId: 'p1',
    proposalNumber: 'PR-2027-000123',
    insurer: { id: 'i1', displayName: 'Seguradora Demo' },
    amount: mzn(299900),
    expiresAt: '2027-01-20T10:00:00Z',
    paid: false,
    methods: [
      { method: 'MPESA', sortOrder: 1, displayName: 'M-Pesa Vodacom' },
      { method: 'BANK_REFERENCE', sortOrder: 2 },
    ],
    ...overrides,
  };
}

export function intentFixture(overrides: Partial<IntentView> = {}): IntentView {
  return {
    id: 'i-1',
    reference: 'PG-2027-0001234',
    status: 'PENDING',
    method: 'MPESA',
    purpose: 'NEW_POLICY',
    target: { type: 'PROPOSAL', id: 'p1', label: 'PR-2027-000123' },
    insurer: { id: 'i1', displayName: 'Seguradora Demo' },
    payerUserId: 'c1',
    amount: mzn(299900),
    payerMsisdnMasked: '+258*****4567',
    expiresAt: '2027-01-20T10:00:00Z',
    createdAt: '2027-01-10T09:00:00Z',
    nextAction: {
      type: 'AWAIT_CUSTOMER_CONFIRMATION',
      timeoutAt: new Date(Date.now() + 90_000).toISOString(),
    },
    ...overrides,
  };
}

export function referenceIntentFixture(overrides: Partial<IntentView> = {}): IntentView {
  return intentFixture({
    method: 'BANK_REFERENCE',
    payerMsisdnMasked: undefined,
    nextAction: {
      type: 'PAY_BY_REFERENCE',
      entity: '12345',
      reference: '00548781108',
      amount: mzn(299900),
      validUntil: '2027-01-20T10:00:00Z',
    },
    ...overrides,
  });
}
