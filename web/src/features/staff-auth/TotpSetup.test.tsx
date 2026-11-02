import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { problemError } from '../../test/problemError';
import { renderWithProviders } from '../../test/render';
import { groupSecret, TotpSetup } from './TotpSetup';

const setup = {
  otpauthUri:
    'otpauth://totp/MoSeguros:ana%40indico.co.mz?secret=JBSWY3DPEHPK3PXPJBSW&issuer=MoSeguros',
  totpSecret: 'JBSWY3DPEHPK3PXPJBSW',
};

describe('TotpSetup', () => {
  it('agrupa o segredo de 4 em 4', () => {
    expect(groupSecret('JBSWY3DPEHPK3PXPJB')).toBe('JBSW Y3DP EHPK 3PXP JB');
  });

  it('mostra o QR e o segredo agrupado', () => {
    renderWithProviders(<TotpSetup setup={setup} onActivate={vi.fn()} />);
    expect(
      screen.getByText('Abra a aplicação de autenticação e leia o código QR.'),
    ).toBeInTheDocument();
    const qr = screen.getByRole('img', { name: 'Código QR para a aplicação de autenticação' });
    expect(qr.tagName.toLowerCase()).toBe('svg');
    expect(screen.getByTestId('totp-secret')).toHaveTextContent('JBSW Y3DP EHPK 3PXP JBSW');
    expect(screen.getByRole('button', { name: 'Copiar o segredo' })).toBeInTheDocument();
  });

  it('activa com o código e TOTP_INVALID mostra a mensagem', async () => {
    const onActivate = vi.fn(async () => {
      throw problemError(400, { code: 'TOTP_INVALID' });
    });
    renderWithProviders(<TotpSetup setup={setup} onActivate={onActivate} />);
    const code = screen.getByLabelText('Código de 6 dígitos');
    expect(code).toHaveAttribute('autocomplete', 'one-time-code');
    expect(code).toHaveAttribute('inputmode', 'numeric');
    await userEvent.type(code, '123456');
    await userEvent.click(screen.getByRole('button', { name: 'Activar a conta' }));
    await waitFor(() => expect(onActivate).toHaveBeenCalledWith('123456'));
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Código errado. Confirme a hora do telemóvel.',
    );
  });
});
