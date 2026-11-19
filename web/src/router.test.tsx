import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import type { MeOrganizationType, MeResponse } from './api/generated';
import { startSession } from './auth/session';
import { mockApi } from './test/mockApi';
import { renderRoute } from './test/render';

function staff(orgType: MeOrganizationType | null, roles: string[], permissions: string[] = []) {
  const me: MeResponse = {
    id: 'u1',
    type: 'STAFF',
    roles,
    permissions,
    preferredLanguage: 'pt',
    emailVerified: true,
    msisdnVerified: false,
    displayName: 'Rita Staff',
    email: 'rita@exemplo.co.mz',
    organization: orgType ? { id: 'o1', displayName: 'Org', type: orgType } : undefined,
  };
  return me;
}

/** Abre uma sessão de staff com o `me` dado. */
function signIn(me: MeResponse) {
  startSession({ accessToken: 'a1', refreshToken: 'r1', expiresIn: 900 }, 'STAFF');
  return mockApi((config) =>
    config.url === '/api/v1/me'
      ? { status: 200, data: me }
      : { status: 404, data: { code: 'NOT_FOUND' } },
  );
}

describe('rotas', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('/ mostra o nome, a frase e o estado do sistema', async () => {
    restore = mockApi(() => ({ status: 200, data: { version: '9.9.9', commit: 'deadbee' } }));
    renderRoute('/');
    expect(screen.getByRole('heading', { level: 1, name: 'MoSeguros' })).toBeInTheDocument();
    expect(
      screen.getByText('Compare, compre e gira o seu seguro automóvel num só lugar.'),
    ).toBeInTheDocument();
    expect(await screen.findByText('9.9.9')).toBeInTheDocument();
  });

  it('/seguradora mostra o menu da área (com as permissões) e a página em construção', async () => {
    restore = signIn(
      staff(
        'INSURER',
        ['INSURER_ADMIN'],
        ['org-user:read', 'product:read', 'payment:read', 'payment:configure'],
      ),
    );
    renderRoute('/seguradora/apolices');
    expect(await screen.findByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
    // Sem matchMedia (jsdom) o layout é o de ecrã estreito: o menu abre-se como gaveta.
    await userEvent.click(screen.getByRole('button', { name: 'Abrir menu' }));
    const nav = await screen.findByRole('navigation', { name: 'Seguradora' });
    expect(nav).toHaveTextContent('Tarifas');
    expect(nav).toHaveTextContent('Configuração');
    expect(within(nav).getByRole('link', { name: 'Pagamentos' })).toBeInTheDocument();
    expect(within(nav).getByRole('link', { name: 'Utilizadores' })).toBeInTheDocument();
  });

  it('sem a permissão o item "Utilizadores" não aparece e a página vai para /sem-acesso', async () => {
    restore = signIn(staff('INSURER', ['UNDERWRITER']));
    const { router } = renderRoute('/seguradora');
    expect(await screen.findByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Abrir menu' }));
    const nav = await screen.findByRole('navigation', { name: 'Seguradora' });
    expect(within(nav).queryByRole('link', { name: 'Utilizadores' })).not.toBeInTheDocument();

    await router.navigate('/seguradora/utilizadores');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
  });

  it('/frota/x/y com sessão da frota mostra a página em construção', async () => {
    restore = signIn(staff('CORPORATE_CLIENT', ['FLEET_MANAGER']));
    renderRoute('/frota/x/y');
    expect(await screen.findByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
  });

  it('área de staff sem sessão vai para /entrar com voltar', async () => {
    const { router } = renderRoute('/admin/organizacoes');
    await waitFor(() => expect(router.state.location.pathname).toBe('/entrar'));
    expect(router.state.location.search).toBe('?voltar=%2Fadmin%2Forganizacoes');
    expect(await screen.findByRole('heading', { name: 'Entrar na MoSeguros' })).toBeInTheDocument();
  });

  it('utilizador de seguradora a abrir /admin vai para /sem-acesso', async () => {
    restore = signIn(staff('INSURER', ['INSURER_ADMIN'], ['org-user:read']));
    const { router } = renderRoute('/admin');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
  });

  it.each(['/verificar', '/v/abc'])('%s mostra a página em construção', (path) => {
    renderRoute(path);
    expect(screen.getByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
  });

  it('um caminho desconhecido mostra 404', () => {
    renderRoute('/xyz');
    expect(screen.getByRole('heading', { name: 'Página não encontrada' })).toBeInTheDocument();
  });
});
