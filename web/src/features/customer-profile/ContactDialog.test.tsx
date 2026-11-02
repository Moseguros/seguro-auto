import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { ContactDialog } from './ContactDialog';

const options = {
  identifierTypes: ['MSISDN'],
  channels: { MSISDN: ['SMS'], EMAIL: ['EMAIL'] },
  defaultChannels: { MSISDN: 'SMS', EMAIL: 'EMAIL' },
  customerCanChoose: false,
  codeLength: 6,
  resendAfterSeconds: 0,
};

const otpResponse = {
  challengeId: 'c1',
  channel: 'EMAIL',
  expiresAt: '2026-11-02T10:00:00Z',
  fallbackUsed: false,
  resendAfterSeconds: 0,
};

describe('ContactDialog', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('pede, reenvia com startContactVerification e confirma com verifyContact', async () => {
    const calls: string[] = [];
    const bodies: unknown[] = [];
    restore = mockApi((config: InternalAxiosRequestConfig) => {
      calls.push(`${config.method?.toUpperCase()} ${config.url}`);
      if (config.url === '/api/v1/auth/otp/options') return { status: 200, data: options };
      if (config.url === '/api/v1/me/contacts') {
        bodies.push(JSON.parse(config.data as string));
        return { status: 202, data: otpResponse };
      }
      if (config.url === '/api/v1/me/contacts/verify') {
        return {
          status: 200,
          data: { contacts: [{ type: 'EMAIL', value: 'ana@exemplo.co.mz', verified: true }] },
        };
      }
      return { status: 404, data: { code: 'NOT_FOUND' } };
    });
    const onVerified = vi.fn();
    renderWithProviders(
      <ContactDialog type="EMAIL" replacing={false} onClose={vi.fn()} onVerified={onVerified} />,
    );

    expect(screen.getByRole('heading', { name: 'Acrescentar e-mail' })).toBeInTheDocument();
    // Só o tipo do contacto: sem selector, mesmo que o login seja só por telemóvel.
    await userEvent.type(await screen.findByLabelText('E-mail'), 'ana@exemplo.co.mz');
    await userEvent.click(screen.getByRole('button', { name: 'Receber código' }));
    expect(
      await screen.findByText(/Enviámos um código por E-mail para a\*\*\*@exemplo\.co\.mz/),
    ).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Reenviar código' }));
    await waitFor(() =>
      expect(calls.filter((call) => call === 'POST /api/v1/me/contacts')).toHaveLength(2),
    );
    expect(calls.some((call) => call.includes('/auth/otp/request'))).toBe(false);
    expect(bodies[1]).toEqual({ type: 'EMAIL', value: 'ana@exemplo.co.mz' });

    await userEvent.click(screen.getByLabelText('Dígito 1 de 6'));
    await userEvent.paste('123456');
    await waitFor(() => expect(onVerified).toHaveBeenCalled());
    expect(calls).toContain('POST /api/v1/me/contacts/verify');
  });
});
