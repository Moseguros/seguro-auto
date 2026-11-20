import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from '../test/mockApi';
import { renderRoute } from '../test/render';
import { signInStaff, staffMe } from '../test/staff';
import { menus } from './menus';

const slow = { timeout: 10_000 };

describe('menus e layouts (S6-05)', { timeout: 30_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('o menu admin tem "Notificações" com a permissão do modelo', () => {
    const item = menus.admin.find((i) => i.key === 'notifications');
    expect(item).toMatchObject({
      path: '/admin/notificacoes',
      permission: 'notification-template:manage',
    });
    const keys = menus.admin.map((i) => i.key);
    expect(keys.indexOf('notifications')).toBe(keys.indexOf('auth') + 1);
    expect(menus.seguradora.find((i) => i.key === 'policies')?.permission).toBe('policy:read');
  });

  it('sem notification-template:manage o item não aparece; com ela aparece', async () => {
    restore = signInStaff(staffMe({ permissions: ['organization:read'] }));
    const first = renderRoute('/admin');
    await userEvent.click(await screen.findByRole('button', { name: 'Abrir menu' }, slow));
    let nav = await screen.findByRole('navigation', { name: 'Administração' });
    expect(within(nav).queryByRole('link', { name: 'Notificações' })).not.toBeInTheDocument();
    first.unmount();
    restore();

    restore = signInStaff(staffMe({ permissions: ['notification-template:manage'] }));
    renderRoute('/admin');
    await userEvent.click(await screen.findByRole('button', { name: 'Abrir menu' }, slow));
    nav = await screen.findByRole('navigation', { name: 'Administração' });
    expect(within(nav).getByRole('link', { name: 'Notificações' })).toHaveAttribute(
      'href',
      '/admin/notificacoes',
    );
  });

  it('seguradora sem policy:read não vê "Apólices" e a página vai para /sem-acesso', async () => {
    restore = signInStaff(
      staffMe({ roles: ['UNDERWRITER'], permissions: ['proposal:read'], orgType: 'INSURER' }),
    );
    const { router } = renderRoute('/seguradora');
    await userEvent.click(await screen.findByRole('button', { name: 'Abrir menu' }, slow));
    const nav = await screen.findByRole('navigation', { name: 'Seguradora' });
    expect(within(nav).getByRole('link', { name: 'Propostas' })).toBeInTheDocument();
    expect(within(nav).queryByRole('link', { name: 'Apólices' })).not.toBeInTheDocument();
    await router.navigate('/seguradora/apolices');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
  });

  it('PublicLayout com sessão mostra o menu do utilizador', async () => {
    restore = signInStaff(
      staffMe({ roles: ['POLICE_OFFICER'], permissions: [], orgType: 'POLICE' }),
    );
    renderRoute('/sem-acesso');
    await userEvent.click(await screen.findByRole('button', { name: 'Menu da conta' }, slow));
    expect(await screen.findByTestId('user-menu-name')).toHaveTextContent('Rita Admin');
    expect(screen.getByRole('menuitem', { name: 'Terminar sessão' })).toBeInTheDocument();
  });

  it('PublicLayout sem sessão não mostra o menu do utilizador', async () => {
    restore = mockApi(() => ({ status: 200, data: { version: '1', commit: 'x' } }));
    renderRoute('/');
    expect(await screen.findByRole('heading', { level: 1, name: 'MoSeguros' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Menu da conta' })).not.toBeInTheDocument();
  });
});
