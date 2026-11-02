import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { OtpOptionsResponse, OtpRequestResponse } from '../../api/generated';
import { problemError } from '../../test/problemError';
import { renderWithProviders } from '../../test/render';
import { IdentifierStep } from './IdentifierStep';

const baseOptions: OtpOptionsResponse = {
  identifierTypes: ['MSISDN', 'EMAIL'],
  channels: { MSISDN: ['SMS', 'WHATSAPP'], EMAIL: ['EMAIL'] },
  defaultChannels: { MSISDN: 'WHATSAPP', EMAIL: 'EMAIL' },
  customerCanChoose: true,
  codeLength: 6,
  resendAfterSeconds: 60,
};

const sentResponse: OtpRequestResponse = {
  challengeId: 'c1',
  channel: 'SMS',
  expiresAt: '2026-11-02T10:00:00Z',
  fallbackUsed: false,
  resendAfterSeconds: 60,
};

describe('IdentifierStep', () => {
  it('mostra a escolha de canal quando é permitida, com o canal por defeito', () => {
    renderWithProviders(
      <IdentifierStep options={baseOptions} onRequest={vi.fn()} onSent={vi.fn()} />,
    );
    expect(screen.getByRole('radiogroup', { name: 'Receber o código por' })).toBeInTheDocument();
    expect(screen.getByRole('radio', { name: 'WhatsApp' })).toBeChecked();
    expect(screen.getByRole('radio', { name: 'SMS' })).not.toBeChecked();
  });

  it('não mostra a escolha de canal se o cliente não pode escolher', () => {
    renderWithProviders(
      <IdentifierStep
        options={{ ...baseOptions, customerCanChoose: false }}
        onRequest={vi.fn()}
        onSent={vi.fn()}
      />,
    );
    expect(screen.queryByRole('radiogroup')).not.toBeInTheDocument();
  });

  it('não mostra a escolha de canal com um só canal para o tipo', async () => {
    renderWithProviders(
      <IdentifierStep options={baseOptions} onRequest={vi.fn()} onSent={vi.fn()} />,
    );
    await userEvent.click(screen.getByRole('button', { name: 'E-mail' }));
    expect(screen.queryByRole('radiogroup')).not.toBeInTheDocument();
  });

  it('com um só tipo de identificador não mostra o selector', () => {
    renderWithProviders(
      <IdentifierStep
        options={{ ...baseOptions, identifierTypes: ['MSISDN'] }}
        onRequest={vi.fn()}
        onSent={vi.fn()}
      />,
    );
    expect(screen.queryByRole('group', { name: 'Entrar com' })).not.toBeInTheDocument();
    expect(screen.getByLabelText('Número de telemóvel')).toBeInTheDocument();
  });

  it('valida o telemóvel antes de pedir o código', async () => {
    const onRequest = vi.fn();
    renderWithProviders(
      <IdentifierStep options={baseOptions} onRequest={onRequest} onSent={vi.fn()} />,
    );
    await userEvent.type(screen.getByLabelText('Número de telemóvel'), '811234567');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    expect(screen.getByText('Número de telemóvel inválido (ex.: +258 84 123 4567).')).toBeVisible();
    expect(onRequest).not.toHaveBeenCalled();
  });

  it('pede o código com +258 e o canal escolhido', async () => {
    const onRequest = vi.fn().mockResolvedValue(sentResponse);
    const onSent = vi.fn();
    renderWithProviders(
      <IdentifierStep options={baseOptions} onRequest={onRequest} onSent={onSent} />,
    );
    await userEvent.type(screen.getByLabelText('Número de telemóvel'), '841234567');
    await userEvent.click(screen.getByRole('radio', { name: 'SMS' }));
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    const request = { identifierType: 'MSISDN', identifier: '+258841234567', channel: 'SMS' };
    expect(onRequest).toHaveBeenCalledWith(request);
    expect(onSent).toHaveBeenCalledWith({
      request,
      requestedChannel: 'SMS',
      response: sentResponse,
    });
  });

  it('sem escolha permitida não envia o canal', async () => {
    const onRequest = vi.fn().mockResolvedValue(sentResponse);
    renderWithProviders(
      <IdentifierStep
        options={{ ...baseOptions, customerCanChoose: false }}
        onRequest={onRequest}
        onSent={vi.fn()}
      />,
    );
    await userEvent.type(screen.getByLabelText('Número de telemóvel'), '841234567');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    expect(onRequest).toHaveBeenCalledWith({
      identifierType: 'MSISDN',
      identifier: '+258841234567',
    });
  });

  it('OTP_CHANNEL_NOT_AVAILABLE mostra a mensagem e muda para o outro tipo', async () => {
    const onRequest = vi
      .fn()
      .mockRejectedValue(problemError(400, { code: 'OTP_CHANNEL_NOT_AVAILABLE' }));
    renderWithProviders(
      <IdentifierStep options={baseOptions} onRequest={onRequest} onSent={vi.fn()} />,
    );
    await userEvent.type(screen.getByLabelText('Número de telemóvel'), '841234567');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Este canal não está disponível');
    expect(screen.getByLabelText('E-mail')).toBeInTheDocument();
  });

  it('429 mostra a contagem e desactiva o botão', async () => {
    const onRequest = vi
      .fn()
      .mockRejectedValue(problemError(429, { code: 'OTP_RATE_LIMITED', retryAfterSeconds: 90 }));
    renderWithProviders(
      <IdentifierStep options={baseOptions} onRequest={onRequest} onSent={vi.fn()} />,
    );
    await userEvent.type(screen.getByLabelText('Número de telemóvel'), '841234567');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    const alert = await screen.findByRole('alert');
    expect(alert).toHaveTextContent('Pediu demasiados códigos');
    expect(alert).toHaveTextContent('Tente de novo dentro de 90 s.');
    await waitFor(() =>
      expect(screen.getByRole('button', { name: 'Receber código' })).toBeDisabled(),
    );
  });
});
