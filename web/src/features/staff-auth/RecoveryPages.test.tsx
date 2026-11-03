import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderRoute } from '../../test/render';

describe('recuperação da palavra-passe', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it.each([202, 404])('/recuperar mostra sempre a mesma mensagem (%s)', async (status) => {
    const bodies: unknown[] = [];
    restore = mockApi((config) => {
      bodies.push(JSON.parse(config.data as string));
      return { status, data: status >= 400 ? { code: 'NOT_FOUND' } : undefined };
    });
    renderRoute('/recuperar');
    await userEvent.type(await screen.findByLabelText('E-mail'), 'rita@moseguros.co.mz');
    await userEvent.click(screen.getByRole('button', { name: 'Enviar link' }));
    expect(await screen.findByRole('status')).toHaveTextContent(
      'Se o e-mail estiver registado, vai receber um link nos próximos minutos.',
    );
    expect(bodies).toEqual([{ email: 'rita@moseguros.co.mz' }]);
  });

  it('/recuperar/:token grava e mostra a ligação para entrar', async () => {
    const bodies: unknown[] = [];
    restore = mockApi((config) => {
      bodies.push(JSON.parse(config.data as string));
      return { status: 204 };
    });
    renderRoute('/recuperar/tok-1');
    await userEvent.type(await screen.findByLabelText('Nova palavra-passe'), 'curta');
    await userEvent.type(screen.getByLabelText('Confirmar a palavra-passe'), 'outra');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar palavra-passe' }));
    expect(
      await screen.findByText('A palavra-passe tem de ter pelo menos 12 caracteres.'),
    ).toBeInTheDocument();
    expect(screen.getByText('As palavras-passe não coincidem.')).toBeInTheDocument();
    expect(bodies).toEqual([]);

    await userEvent.clear(screen.getByLabelText('Nova palavra-passe'));
    await userEvent.type(screen.getByLabelText('Nova palavra-passe'), 'uma-palavra-passe-longa');
    await userEvent.clear(screen.getByLabelText('Confirmar a palavra-passe'));
    await userEvent.type(
      screen.getByLabelText('Confirmar a palavra-passe'),
      'uma-palavra-passe-longa',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Guardar palavra-passe' }));
    expect(await screen.findByRole('status')).toHaveTextContent('Palavra-passe alterada');
    expect(screen.getByRole('link', { name: 'Entrar' })).toHaveAttribute('href', '/entrar');
    expect(bodies).toEqual([{ token: 'tok-1', newPassword: 'uma-palavra-passe-longa' }]);
  });

  it('PASSWORD_TOO_WEAK mostra os motivos no campo', async () => {
    restore = mockApi(() => ({
      status: 400,
      data: {
        code: 'PASSWORD_TOO_WEAK',
        errors: [{ field: 'newPassword', code: 'TOO_COMMON' }],
      },
    }));
    renderRoute('/recuperar/tok-1');
    await userEvent.type(await screen.findByLabelText('Nova palavra-passe'), 'password12345');
    await userEvent.type(screen.getByLabelText('Confirmar a palavra-passe'), 'password12345');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar palavra-passe' }));
    expect(
      await screen.findByText('Esta palavra-passe é demasiado comum. Escolha outra.'),
    ).toBeInTheDocument();
  });

  it('RESET_TOKEN_INVALID mostra "Este link já não é válido" com ligação para /recuperar', async () => {
    restore = mockApi(() => ({ status: 400, data: { code: 'RESET_TOKEN_INVALID' } }));
    const { router } = renderRoute('/recuperar/velho');
    await userEvent.type(
      await screen.findByLabelText('Nova palavra-passe'),
      'uma-palavra-passe-longa',
    );
    await userEvent.type(
      screen.getByLabelText('Confirmar a palavra-passe'),
      'uma-palavra-passe-longa',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Guardar palavra-passe' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Este link já não é válido');
    await userEvent.click(screen.getByRole('link', { name: 'Pedir um novo link' }));
    await waitFor(() => expect(router.state.location.pathname).toBe('/recuperar'));
  });
});
