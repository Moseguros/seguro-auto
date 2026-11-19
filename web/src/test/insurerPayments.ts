import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import type {
  PaymentExceptionCandidate,
  PaymentExceptionDetail,
  PaymentMethodResponse,
} from '../api/generated';
import type { JsonSchema } from '../features/insurer-payment-settings/schemaPaths';
import { staffMe } from './staff';

/** JSON Schema verdadeiro do método (`backend/src/main/resources/schemas`). */
export function methodSchema(method: 'mpesa' | 'bank_reference'): JsonSchema {
  return JSON.parse(
    readFileSync(
      resolve(
        __dirname,
        `../../../backend/src/main/resources/schemas/payment-method-${method}.json`,
      ),
      'utf-8',
    ),
  ) as JsonSchema;
}

const secret = (configured: boolean, required: boolean) => ({
  configured,
  required,
  updatedAt: configured ? '2027-01-10T08:00:00Z' : null,
});

export function mpesaMethodFixture(
  overrides: Partial<PaymentMethodResponse> = {},
): PaymentMethodResponse {
  return {
    method: 'MPESA',
    configured: true,
    enabled: false,
    environment: 'SANDBOX',
    displayName: 'M-Pesa',
    settings: {
      serviceProviderCode: '171717',
      host: 'http://mpesa-mock',
      ports: { c2b: 18352, query: 18353, reversal: 18354 },
      origin: 'developer.mpesa.vm.co.mz',
      c2bTimeoutSeconds: 90,
    },
    settingsSchema: methodSchema('mpesa') as PaymentMethodResponse['settingsSchema'],
    secrets: {
      apiKey: secret(true, true),
      publicKey: secret(true, true),
      securityCredential: secret(false, false),
    },
    intentValidityMinutes: 30,
    sortOrder: 1,
    version: 3,
    updatedAt: '2027-01-10T08:00:00Z',
    ...overrides,
  };
}

export function bankReferenceMethodFixture(
  overrides: Partial<PaymentMethodResponse> = {},
): PaymentMethodResponse {
  return {
    method: 'BANK_REFERENCE',
    configured: true,
    enabled: true,
    environment: 'PRODUCTION',
    displayName: null,
    settings: {
      entity: '11223',
      fileFormat: 'MEPS',
      sftp: {
        host: 'sftp.banco.co.mz',
        port: 22,
        username: 'seguradora',
        authType: 'PASSWORD',
        inboundDir: '/in',
        moveAfterProcessing: true,
        processedDir: '/done',
        errorDir: '/err',
        filePattern: '*',
      },
    },
    settingsSchema: methodSchema('bank_reference') as PaymentMethodResponse['settingsSchema'],
    secrets: {
      'sftp.password': secret(true, true),
      'sftp.privateKey': secret(false, false),
      'sftp.passphrase': secret(false, false),
    },
    intentValidityMinutes: 30,
    sortOrder: 2,
    version: 5,
    updatedAt: '2027-01-10T08:00:00Z',
    ...overrides,
  };
}

export const financeMe = staffMe({
  roles: ['FINANCE'],
  permissions: ['payment:configure', 'payment:read', 'payment:resolve-exception'],
  orgType: 'INSURER',
});

export const insurerAdminMe = staffMe({
  roles: ['INSURER_ADMIN'],
  permissions: ['payment:configure', 'payment:read'],
  orgType: 'INSURER',
});

export function exceptionFixture(
  overrides: Partial<PaymentExceptionDetail> = {},
): PaymentExceptionDetail {
  return {
    id: 'ex1',
    type: 'UNKNOWN_REFERENCE',
    status: 'OPEN',
    allowedResolutions: ['LINKED', 'REFUND', 'IGNORED'],
    amount: { amount: 299900, currency: 'MZN' },
    entity: '11223',
    reference: '00548781108',
    paidAt: '2027-01-11T09:00:00Z',
    fileName: 'MEPS20270111.txt',
    lineNo: 4,
    detail: 'NOT_ISSUED',
    createdAt: '2027-01-11T10:00:00Z',
    version: 0,
    ...overrides,
  };
}

export function candidateFixture(
  overrides: Partial<PaymentExceptionCandidate> = {},
): PaymentExceptionCandidate {
  return {
    id: 'pi1',
    reference: 'PG-2027-000010',
    targetLabel: 'PR-2027-000123',
    amount: { amount: 299900, currency: 'MZN' },
    status: 'EXPIRED',
    bankReference: '00548781109',
    createdAt: '2027-01-05T10:00:00Z',
    matchesAmount: true,
    matchesReference: false,
    ...overrides,
  };
}
