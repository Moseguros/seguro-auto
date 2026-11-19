import { QueryClientProvider } from '@tanstack/react-query';
import { act, renderHook, waitFor } from '@testing-library/react';
import type { ReactNode } from 'react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { fileNameFromDisposition } from '../../files/downloadFile';
import { mockApi } from '../../test/mockApi';
import { intentFixture, referenceIntentFixture } from '../../test/payments';
import { testQueryClient } from '../../test/render';
import { formatCountdown } from './AwaitConfirmation';
import { amountCopyValue, formatReference, referenceCopyValue } from './formatReference';
import { nextPaymentsPage } from './myPayments';
import { nationalFromMsisdn, validateMozMsisdn, validateMpesaMsisdn } from './PayerMsisdnField';
import { idempotencyStorageKey, paymentSignature, useIdempotencyKey } from './useIdempotencyKey';
import {
  MPESA_POLL_MS,
  pollInterval,
  REFERENCE_POLL_MS,
  usePaymentIntentPolling,
} from './usePaymentIntentPolling';

let restore: () => void = () => {};
afterEach(() => {
  restore();
  vi.useRealTimers();
});

describe('formatReference', () => {
  it('agrupa os 11 dígitos de 3 em 3 a partir da esquerda e copia sem espaços', () => {
    expect(formatReference('00548781108')).toBe('005 487 811 08');
    expect(referenceCopyValue(formatReference('00548781108'))).toBe('00548781108');
    expect(referenceCopyValue('005 487 811 08')).not.toContain(' ');
  });

  it('o montante copiado tem só os algarismos e a vírgula', () => {
    expect(amountCopyValue({ amount: 299900 })).toBe('2999,00');
    expect(amountCopyValue({ amount: 300000005 })).toBe('3000000,05');
  });
});

describe('telemóveis', () => {
  it('M-Pesa só com 84 ou 85; SMS com qualquer operadora', () => {
    expect(validateMpesaMsisdn('')).toBe('REQUIRED');
    expect(validateMpesaMsisdn('841234567')).toBeNull();
    expect(validateMpesaMsisdn('851234567')).toBeNull();
    expect(validateMpesaMsisdn('821234567')).toBe('MSISDN_NOT_MPESA');
    expect(validateMpesaMsisdn('84123456')).toBe('MSISDN_NOT_MPESA');
    expect(validateMozMsisdn('821234567')).toBeNull();
    expect(validateMozMsisdn('881234567')).toBe('INVALID_MSISDN');
    expect(nationalFromMsisdn('+258841234567')).toBe('841234567');
    expect(nationalFromMsisdn(undefined)).toBe('');
  });
});

describe('outros auxiliares', () => {
  it('contagem mm:ss e página seguinte', () => {
    expect(formatCountdown(125_000)).toBe('02:05');
    expect(formatCountdown(-3)).toBe('00:00');
    expect(nextPaymentsPage({ content: [], page: 0, size: 20, totalElements: 21 })).toBe(1);
    expect(nextPaymentsPage({ content: [], page: 1, size: 20, totalElements: 40 })).toBeUndefined();
  });

  it('lê o nome do Content-Disposition', () => {
    expect(fileNameFromDisposition('attachment; filename="ref.pdf"')).toBe('ref.pdf');
    expect(fileNameFromDisposition("attachment; filename*=UTF-8''refer%C3%AAncia.pdf")).toBe(
      'referência.pdf',
    );
    expect(fileNameFromDisposition(undefined)).toBeUndefined();
  });
});

describe('useIdempotencyKey', () => {
  it('repete a chave do mesmo conjunto, muda com o método e depois de reset', () => {
    const { result } = renderHook(() => useIdempotencyKey('p1'));
    const mpesa = paymentSignature('MPESA', '+258841234567');
    const first = result.current.keyFor(mpesa);
    expect(first).toMatch(/^[0-9a-f-]{36}$/);
    expect(result.current.keyFor(mpesa)).toBe(first);
    expect(sessionStorage.getItem(idempotencyStorageKey('p1'))).toContain(first);

    const reference = result.current.keyFor(paymentSignature('BANK_REFERENCE', undefined));
    expect(reference).not.toBe(first);
    expect(result.current.keyFor(paymentSignature('MPESA', '+258851234567'))).not.toBe(reference);

    const before = result.current.keyFor(mpesa);
    act(() => result.current.reset());
    expect(result.current.keyFor(mpesa)).not.toBe(before);
  });
});

describe('usePaymentIntentPolling', () => {
  it('intervalos: 3 s com M-Pesa, 30 s com referência, nenhum num estado final', () => {
    for (const status of ['CREATED', 'PENDING', 'UNKNOWN'] as const) {
      expect(pollInterval({ method: 'MPESA', status })).toBe(MPESA_POLL_MS);
    }
    expect(MPESA_POLL_MS).toBe(3000);
    expect(pollInterval({ method: 'BANK_REFERENCE', status: 'PENDING' })).toBe(REFERENCE_POLL_MS);
    expect(REFERENCE_POLL_MS).toBe(30000);
    for (const status of ['SUCCEEDED', 'FAILED', 'EXPIRED', 'CANCELLED', 'REFUNDED'] as const) {
      expect(pollInterval({ method: 'MPESA', status })).toBe(false);
      expect(pollInterval({ method: 'BANK_REFERENCE', status })).toBe(false);
    }
    expect(pollInterval(undefined)).toBe(false);
  });

  function wrapper({ children }: { children: ReactNode }) {
    return <QueryClientProvider client={testQueryClient()}>{children}</QueryClientProvider>;
  }

  it('lê de novo a cada 3 s com M-Pesa e pára quando o pagamento termina', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const responses = [
      intentFixture(),
      intentFixture(),
      intentFixture({ status: 'SUCCEEDED', nextAction: { type: 'NONE' } }),
    ];
    let calls = 0;
    restore = mockApi(() => ({ status: 200, data: responses[Math.min(calls++, 2)] }));
    const { result } = renderHook(() => usePaymentIntentPolling('i-1'), { wrapper });
    await waitFor(() => expect(result.current.data?.status).toBe('PENDING'));
    expect(calls).toBe(1);
    await act(() => vi.advanceTimersByTimeAsync(2_900));
    expect(calls).toBe(1);
    await act(() => vi.advanceTimersByTimeAsync(200));
    await waitFor(() => expect(calls).toBe(2));
    await act(() => vi.advanceTimersByTimeAsync(3_100));
    await waitFor(() => expect(result.current.data?.status).toBe('SUCCEEDED'));
    expect(calls).toBe(3);
    await act(() => vi.advanceTimersByTimeAsync(60_000));
    expect(calls).toBe(3);
  });

  it('com entidade e referência lê de 30 em 30 s', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    let calls = 0;
    restore = mockApi(() => {
      calls++;
      return { status: 200, data: referenceIntentFixture() };
    });
    const { result } = renderHook(() => usePaymentIntentPolling('i-1'), { wrapper });
    await waitFor(() => expect(result.current.data?.method).toBe('BANK_REFERENCE'));
    await act(() => vi.advanceTimersByTimeAsync(29_000));
    expect(calls).toBe(1);
    await act(() => vi.advanceTimersByTimeAsync(1_500));
    await waitFor(() => expect(calls).toBe(2));
  });
});
