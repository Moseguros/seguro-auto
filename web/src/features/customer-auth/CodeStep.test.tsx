import { act, fireEvent, screen } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import type { OtpRequestResponse } from '../../api/generated';
import { problemError } from '../../test/problemError';
import { renderWithProviders } from '../../test/render';
import { CodeStep, type CodeStepProps } from './CodeStep';
import type { SentCode } from './otp';

const response: OtpRequestResponse = {
  challengeId: 'c1',
  channel: 'WHATSAPP',
  expiresAt: '2026-11-02T10:00:00Z',
  fallbackUsed: false,
  resendAfterSeconds: 3,
};

const sent: SentCode = {
  request: { identifierType: 'MSISDN', identifier: '+258841234567', channel: 'WHATSAPP' },
  requestedChannel: 'WHATSAPP',
  response,
};

function renderStep(props: Partial<CodeStepProps> = {}) {
  const all: CodeStepProps = {
    sent,
    codeLength: 6,
    onRequest: vi.fn().mockResolvedValue({ ...response, challengeId: 'c2' }),
    onVerify: vi.fn().mockResolvedValue(undefined),
    onChangeIdentifier: vi.fn(),
    getChallenge: vi.fn().mockResolvedValue({ ...response, deliveryStatus: 'SENT' }),
    ...props,
  };
  renderWithProviders(<CodeStep {...all} />);
  return all;
}

async function advance(ms: number) {
  for (let elapsed = 0; elapsed < ms; elapsed += 1000) {
    await act(async () => {
      vi.advanceTimersByTime(1000);
    });
  }
}

function typeCode(code: string) {
  fireEvent.change(screen.getByLabelText('Dígito 1 de 6'), { target: { value: code } });
}

describe('CodeStep', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });
  afterEach(() => {
    vi.useRealTimers();
  });

  it('mostra o canal e o destino mascarado', () => {
    renderStep();
    expect(
      screen.getByText(/Enviámos um código por WhatsApp para \+258\*\*\*\*\*4567\./),
    ).toBeInTheDocument();
  });

  it('mostra a contagem e activa o reenvio no fim', async () => {
    const props = renderStep();
    expect(screen.getByRole('button', { name: 'Reenviar código (3 s)' })).toBeDisabled();
    await advance(1000);
    expect(screen.getByRole('button', { name: 'Reenviar código (2 s)' })).toBeDisabled();
    await advance(2000);
    const resend = screen.getByRole('button', { name: 'Reenviar código' });
    expect(resend).toBeEnabled();
    await act(async () => {
      fireEvent.click(resend);
    });
    expect(props.onRequest).toHaveBeenCalledWith(sent.request);
    expect(screen.getByRole('button', { name: 'Reenviar código (3 s)' })).toBeDisabled();
  });

  it('submete sozinho quando o código fica completo', async () => {
    const props = renderStep();
    await act(async () => typeCode('123456'));
    expect(props.onVerify).toHaveBeenCalledWith('c1', '123456');
  });

  it('código errado mostra as tentativas restantes', async () => {
    renderStep({
      onVerify: vi
        .fn()
        .mockRejectedValue(problemError(400, { code: 'OTP_INVALID', attemptsRemaining: 2 })),
    });
    await act(async () => typeCode('000000'));
    expect(screen.getByRole('alert')).toHaveTextContent('Código errado. Restam 2 tentativas.');
  });

  it.each(['OTP_EXPIRED', 'OTP_ATTEMPTS_EXCEEDED', 'OTP_SUPERSEDED'])(
    '%s mostra a mensagem e activa logo o reenvio',
    async (code) => {
      renderStep({ onVerify: vi.fn().mockRejectedValue(problemError(400, { code })) });
      await act(async () => typeCode('000000'));
      expect(screen.getByRole('alert')).toHaveTextContent(/Peça um novo código|último código/);
      expect(screen.getByRole('button', { name: 'Reenviar código' })).toBeEnabled();
    },
  );

  it('ACCOUNT_DISABLED pede para contactar o apoio', async () => {
    renderStep({
      onVerify: vi.fn().mockRejectedValue(problemError(403, { code: 'ACCOUNT_DISABLED' })),
    });
    await act(async () => typeCode('123456'));
    expect(screen.getByRole('alert')).toHaveTextContent('Contacte o apoio ao cliente.');
  });

  it('fallbackUsed mostra o aviso', () => {
    renderStep({
      sent: { ...sent, response: { ...response, channel: 'SMS', fallbackUsed: true } },
    });
    expect(
      screen.getByText('Não conseguimos enviar por WhatsApp. Enviámos o código por SMS.'),
    ).toBeInTheDocument();
  });

  it('a consulta do envio actualiza o canal e mostra o aviso', async () => {
    const getChallenge = vi
      .fn()
      .mockResolvedValue({ ...response, channel: 'SMS', fallbackUsed: true });
    renderStep({ getChallenge });
    expect(screen.queryByText(/Não conseguimos enviar/)).not.toBeInTheDocument();
    await advance(5000);
    expect(getChallenge).toHaveBeenCalledWith('c1');
    expect(screen.getByText(/Enviámos um código por SMS para/)).toBeInTheDocument();
    expect(
      screen.getByText('Não conseguimos enviar por WhatsApp. Enviámos o código por SMS.'),
    ).toBeInTheDocument();
  });

  it('pára de consultar o envio ao fim de 30 s', async () => {
    const props = renderStep();
    await advance(60_000);
    expect(props.getChallenge).toHaveBeenCalledTimes(6);
  });
});
