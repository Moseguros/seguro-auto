import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import type { OtpSettingsResponse } from '../../api/generated';
import { renderWithAuth } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { OtpSettingsPage } from './OtpSettingsPage';
import { moveChannel, toggleChannel, formFrom } from './otpSettingsForm';

const settings: OtpSettingsResponse = {
  enabledChannels: ['SMS', 'WHATSAPP'],
  defaultChannel: 'WHATSAPP',
  customerCanChoose: true,
  fallbackOrder: ['WHATSAPP', 'SMS'],
  codeLength: 6,
  validitySeconds: 300,
  maxAttempts: 5,
  resendAfterSeconds: 60,
  maxRequestsPerHour: 5,
  updatedAt: '2026-11-01T10:00:00Z',
  updatedBy: 'system',
  version: 3,
};

const manager = staffMe({ permissions: ['otp-settings:read', 'otp-settings:manage'] });
const reader = staffMe({ permissions: ['otp-settings:read'] });

function order() {
  const list = screen.getByTestId('fallback-order');
  return within(list)
    .getAllByRole('listitem')
    .map((item) => item.getAttribute('data-channel'));
}

function defaultOptions() {
  const select = screen.getByLabelText('Canal por defeito') as HTMLSelectElement;
  return Array.from(select.options)
    .map((o) => o.value)
    .filter(Boolean);
}

describe('otpSettingsForm', () => {
  it('ligar acrescenta no fim; desligar retira e troca o canal por defeito', () => {
    const form = formFrom(settings);
    const on = toggleChannel(form, 'EMAIL', true);
    expect(on.enabledChannels).toEqual(['SMS', 'WHATSAPP', 'EMAIL']);
    expect(on.fallbackOrder).toEqual(['WHATSAPP', 'SMS', 'EMAIL']);
    const off = toggleChannel(on, 'WHATSAPP', false);
    expect(off.fallbackOrder).toEqual(['SMS', 'EMAIL']);
    expect(off.defaultChannel).toBe('SMS');
  });

  it('move um canal na ordem', () => {
    expect(moveChannel(['SMS', 'WHATSAPP', 'EMAIL'], 2, 0)).toEqual(['EMAIL', 'SMS', 'WHATSAPP']);
    expect(moveChannel(['SMS'], 0, 1)).toEqual(['SMS']);
  });
});

describe('OtpSettingsPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('ligar e desligar canais actualiza a ordem de alternativa e o canal por defeito', async () => {
    restore = signInStaff(manager, (c) =>
      is(c, 'GET', '/api/v1/admin/otp-settings') ? { status: 200, data: settings } : null,
    );
    renderWithAuth(<OtpSettingsPage />);
    const email = await screen.findByRole('switch', { name: 'E-mail' });
    expect(order()).toEqual(['WHATSAPP', 'SMS']);
    expect(defaultOptions()).toEqual(['WHATSAPP', 'SMS']);
    expect(screen.getByText('Não há alternativa entre telemóvel e e-mail.')).toBeInTheDocument();

    await userEvent.click(email);
    expect(order()).toEqual(['WHATSAPP', 'SMS', 'EMAIL']);
    expect(defaultOptions()).toEqual(['WHATSAPP', 'SMS', 'EMAIL']);

    await userEvent.click(screen.getByRole('switch', { name: 'WhatsApp' }));
    expect(order()).toEqual(['SMS', 'EMAIL']);
    // O canal por defeito só lista canais activos.
    expect(defaultOptions()).toEqual(['SMS', 'EMAIL']);
    expect(screen.getByLabelText('Canal por defeito')).toHaveValue('SMS');

    // Alternativa por teclado aos arrastos: botões subir/descer.
    await userEvent.click(screen.getByRole('button', { name: 'Subir E-mail' }));
    expect(order()).toEqual(['EMAIL', 'SMS']);
  });

  it('sem canais mostra CHANNELS_EMPTY e não grava', async () => {
    let puts = 0;
    restore = signInStaff(manager, (c) => {
      if (is(c, 'GET', '/api/v1/admin/otp-settings')) return { status: 200, data: settings };
      if (is(c, 'PUT', '/api/v1/admin/otp-settings')) puts += 1;
      return null;
    });
    renderWithAuth(<OtpSettingsPage />);
    await userEvent.click(await screen.findByRole('switch', { name: 'SMS' }));
    await userEvent.click(screen.getByRole('switch', { name: 'WhatsApp' }));
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Escolha pelo menos um canal.')).toBeInTheDocument();
    expect(puts).toBe(0);
  });

  it('grava com a versão e a validade em segundos; os erros do servidor aparecem nos campos', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(manager, (c) => {
      if (is(c, 'GET', '/api/v1/admin/otp-settings')) return { status: 200, data: settings };
      if (is(c, 'PUT', '/api/v1/admin/otp-settings')) {
        bodies.push(JSON.parse(c.data as string));
        return {
          status: 400,
          data: {
            code: 'VALIDATION_ERROR',
            errors: [
              { field: 'validitySeconds', code: 'OUT_OF_RANGE' },
              { field: 'defaultChannel', code: 'DEFAULT_CHANNEL_NOT_ENABLED' },
            ],
          },
        };
      }
      return null;
    });
    renderWithAuth(<OtpSettingsPage />);
    const validity = await screen.findByLabelText('Validade (minutos)');
    await userEvent.clear(validity);
    await userEvent.type(validity, '10');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]).toMatchObject({ validitySeconds: 600, version: 3, codeLength: 6 });
    expect(await screen.findByText(/Valor fora dos limites permitidos\./)).toBeInTheDocument();
    expect(screen.getByText('O canal por defeito tem de estar activo.')).toBeInTheDocument();
    expect(screen.getByLabelText('Validade (minutos)')).toHaveAttribute('aria-invalid', 'true');
  });

  it('409 mostra o aviso com "Recarregar"', async () => {
    let gets = 0;
    restore = signInStaff(manager, (c) => {
      if (is(c, 'GET', '/api/v1/admin/otp-settings')) {
        gets += 1;
        return { status: 200, data: settings };
      }
      if (is(c, 'PUT', '/api/v1/admin/otp-settings')) {
        return { status: 409, data: { code: 'CONCURRENT_MODIFICATION' } };
      }
      return null;
    });
    renderWithAuth(<OtpSettingsPage />);
    await userEvent.click(await screen.findByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Outra pessoa alterou esta configuração')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Recarregar' }));
    await waitFor(() => expect(gets).toBe(2));
  });

  it('sem otp-settings:manage é só de leitura e sem botões', async () => {
    restore = signInStaff(reader, (c) =>
      is(c, 'GET', '/api/v1/admin/otp-settings') ? { status: 200, data: settings } : null,
    );
    renderWithAuth(<OtpSettingsPage />);
    expect(await screen.findByText('Só pode consultar esta configuração.')).toBeInTheDocument();
    for (const name of ['SMS', 'WhatsApp', 'E-mail']) {
      expect(screen.getByRole('switch', { name })).toBeDisabled();
    }
    expect(screen.getByLabelText('Tamanho do código')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(
      screen.queryByRole('button', { name: 'Enviar código de teste' }),
    ).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /^Subir/ })).not.toBeInTheDocument();
  });

  it('o envio de teste lista os canais compatíveis (mesmo inactivos) e mostra o resultado', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(manager, (c) => {
      if (is(c, 'GET', '/api/v1/admin/otp-settings')) {
        return {
          status: 200,
          data: {
            ...settings,
            enabledChannels: ['SMS'],
            fallbackOrder: ['SMS'],
            defaultChannel: 'SMS',
          },
        };
      }
      if (is(c, 'POST', '/api/v1/admin/otp-settings/test')) {
        bodies.push(JSON.parse(c.data as string));
        return {
          status: 200,
          data: { channel: 'WHATSAPP', deliveryStatus: 'SENT', messageId: 'm1' },
        };
      }
      return null;
    });
    renderWithAuth(<OtpSettingsPage />);
    await userEvent.click(await screen.findByRole('button', { name: 'Enviar código de teste' }));
    const dialog = await screen.findByRole('dialog', { name: 'Enviar código de teste' });
    const channel = within(dialog).getByLabelText('Canal') as HTMLSelectElement;
    expect(Array.from(channel.options).map((o) => o.textContent)).toEqual([
      'SMS',
      'WhatsApp (inactivo)',
    ]);
    await userEvent.type(within(dialog).getByLabelText('Identificador'), '+258841234567');
    await userEvent.selectOptions(channel, 'WHATSAPP');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Enviar' }));
    expect(await within(dialog).findByTestId('test-delivery-result')).toHaveTextContent(
      'Resultado: Enviado (WhatsApp).',
    );
    expect(bodies).toEqual([
      { identifierType: 'MSISDN', identifier: '+258841234567', channel: 'WHATSAPP' },
    ]);
  });
});
