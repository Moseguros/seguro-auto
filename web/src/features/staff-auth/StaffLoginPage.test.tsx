import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { getSession, refreshTokenKeys } from '../../auth/session';
import { mockApi } from '../../test/mockApi';
import { renderRoute } from '../../test/render';
import { staffMe } from '../../test/staff';

const tokens = { accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 };

function api(login: { status: number; data?: unknown }, me = staffMe()) {
  const bodies: unknown[] = [];
  const restore = mockApi((config) => {
    if (config.url === '/api/v1/auth/login') {
      bodies.push(JSON.parse(config.data as string));
      return login;
    }
    if (config.url === '/api/v1/me') return { status: 200, data: me };
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
  return { restore, bodies };
}

async function fill(code = '123456') {
  await userEvent.type(await screen.findByLabelText('E-mail'), ' rita@moseguros.co.mz ');
  await userEvent.type(screen.getByLabelText('Palavra-passe'), 'uma-palavra-passe-longa');
  await userEvent.type(screen.getByLabelText('Código da aplicação de autenticação'), code);
  await userEvent.click(screen.getByRole('button', { name: 'Entrar' }));
}

describe('StaffLoginPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('tem o código com teclado numérico e one-time-code, e a ligação de recuperação', async () => {
    renderRoute('/entrar');
    const code = await screen.findByLabelText('Código da aplicação de autenticação');
    expect(code).toHaveAttribute('inputmode', 'numeric');
    expect(code).toHaveAttribute('autocomplete', 'one-time-code');
    expect(screen.getByRole('link', { name: 'Esqueci-me da palavra-passe' })).toHaveAttribute(
      'href',
      '/recuperar',
    );
  });

  it('administrador entra e vai para /admin; o refresh token fica no sessionStorage', async () => {
    const mock = api({ status: 200, data: tokens });
    restore = mock.restore;
    const { router } = renderRoute('/entrar');
    await fill();
    await waitFor(() => expect(router.state.location.pathname).toBe('/admin'));
    expect(mock.bodies).toEqual([
      { email: 'rita@moseguros.co.mz', password: 'uma-palavra-passe-longa', totpCode: '123456' },
    ]);
    expect(getSession()?.type).toBe('STAFF');
    expect(sessionStorage.getItem(refreshTokenKeys.STAFF)).toBe('r1');
  });

  it('volta à página pedida (voltar)', async () => {
    restore = api(
      { status: 200, data: tokens },
      staffMe({ roles: ['INSURER_ADMIN'], orgType: 'INSURER', permissions: ['org-user:read'] }),
    ).restore;
    const { router } = renderRoute('/entrar?voltar=%2Fseguradora%2Futilizadores');
    await fill();
    await waitFor(() => expect(router.state.location.pathname).toBe('/seguradora/utilizadores'));
  });

  it.each([
    [401, { code: 'INVALID_CREDENTIALS' }, 'E-mail, palavra-passe ou código errados.'],
    [
      403,
      { code: 'ACCOUNT_LOCKED', retryAfterSeconds: 840 },
      'Conta bloqueada. Tente de novo dentro de 14 minutos.',
    ],
    [
      403,
      { code: 'ACCOUNT_DISABLED' },
      'Esta conta está desactivada. Contacte o apoio ao cliente.',
    ],
    [
      403,
      { code: 'ORGANIZATION_SUSPENDED' },
      'A sua organização está suspensa. Contacte a administração da MoSeguros.',
    ],
  ])('%s %o → mensagem própria', async (status, data, text) => {
    restore = api({ status, data }).restore;
    renderRoute('/entrar');
    await fill();
    expect(await screen.findByRole('alert')).toHaveTextContent(text);
  });

  it('valida os campos antes de enviar', async () => {
    const mock = api({ status: 200, data: tokens });
    restore = mock.restore;
    renderRoute('/entrar');
    await userEvent.click(await screen.findByRole('button', { name: 'Entrar' }));
    expect(await screen.findAllByText('Campo obrigatório.')).toHaveLength(3);
    expect(mock.bodies).toEqual([]);
  });
});
