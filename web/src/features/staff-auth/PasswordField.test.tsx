import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useState } from 'react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { PasswordField, passwordRuleStatus } from './PasswordField';

function Field(props: { email?: string; errorCodes?: string[] }) {
  const [value, setValue] = useState('');
  return (
    <PasswordField
      label="Nova palavra-passe"
      name="password"
      value={value}
      onChange={(event) => setValue(event.target.value)}
      showRules
      {...props}
    />
  );
}

describe('PasswordField', () => {
  it('mostra cada regra e marca as cumpridas', async () => {
    renderWithProviders(<Field email="ana.silva@indico.co.mz" />);
    expect(screen.getByText('Ter pelo menos 12 caracteres')).toBeInTheDocument();
    expect(screen.getByText('Não conter o seu e-mail')).toBeInTheDocument();
    expect(screen.getByText('Não ser uma palavra-passe comum')).toBeInTheDocument();
    expect(screen.getByTestId('rule-minLength')).toHaveAttribute('data-met', 'false');

    await userEvent.type(screen.getByLabelText('Nova palavra-passe'), 'ana.silva-2026!');
    expect(screen.getByTestId('rule-minLength')).toHaveAttribute('data-met', 'true');
    expect(screen.getByTestId('rule-noEmail')).toHaveAttribute('data-met', 'false');
  });

  it('mostra os erros validation.* do servidor', () => {
    renderWithProviders(<Field errorCodes={['TOO_SHORT', 'TOO_COMMON']} />);
    expect(
      screen.getByText(
        'A palavra-passe tem de ter pelo menos 12 caracteres. Esta palavra-passe é demasiado comum. Escolha outra.',
      ),
    ).toBeInTheDocument();
    expect(screen.getByLabelText('Nova palavra-passe')).toHaveAttribute('aria-invalid', 'true');
  });

  it('mostrar/ocultar troca o tipo do campo', async () => {
    renderWithProviders(<Field />);
    const input = screen.getByLabelText('Nova palavra-passe');
    expect(input).toHaveAttribute('type', 'password');
    await userEvent.click(screen.getByRole('button', { name: 'Mostrar a palavra-passe' }));
    expect(input).toHaveAttribute('type', 'text');
    await userEvent.click(screen.getByRole('button', { name: 'Ocultar a palavra-passe' }));
    expect(input).toHaveAttribute('type', 'password');
  });

  it('passwordRuleStatus usa a parte local do e-mail', () => {
    expect(passwordRuleStatus('xpto-ANA-1234567', 'ana@x.mz')).toEqual({
      minLength: true,
      noEmail: false,
      notCommon: null,
    });
    expect(passwordRuleStatus('curta').noEmail).toBeNull();
  });
});
