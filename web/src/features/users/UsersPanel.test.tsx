import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import type { StaffUserResponse } from '../../api/generated';
import { renderWithAuth } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { availableActions } from './usersApi';
import { UsersPanel } from './UsersPanel';

const orgId = 'org-1';

function user(id: string, status: StaffUserResponse['status'], roles = ['UNDERWRITER']) {
  return {
    id,
    email: `${id}@indico.co.mz`,
    displayName: `Pessoa ${id}`,
    status,
    roles,
    createdAt: '2026-10-01T00:00:00Z',
    version: 1,
  } satisfies StaffUserResponse;
}

const users = [
  user('u1', 'ACTIVE', ['INSURER_ADMIN']),
  user('u2', 'ACTIVE'),
  user('u3', 'LOCKED'),
  user('u4', 'DISABLED'),
  user('u5', 'PENDING_ACTIVATION'),
];

const invitations = [
  {
    id: 'i1',
    email: 'nova@indico.co.mz',
    fullName: 'Nova Pessoa',
    roleCodes: ['FINANCE'],
    status: 'PENDING',
    createdAt: '2026-11-01T00:00:00Z',
    expiresAt: '2026-11-08T00:00:00Z',
    organizationId: orgId,
  },
  {
    id: 'i2',
    email: 'velha@indico.co.mz',
    fullName: 'Velha Pessoa',
    roleCodes: ['FINANCE'],
    status: 'ACCEPTED',
    createdAt: '2026-10-01T00:00:00Z',
    expiresAt: '2026-10-08T00:00:00Z',
    organizationId: orgId,
  },
];

const page = <T,>(content: T[]) => ({ content, page: 0, size: 20, totalElements: content.length });

const me = staffMe({
  id: 'u1',
  roles: ['INSURER_ADMIN'],
  permissions: ['org-user:read', 'org-user:manage'],
  orgType: 'INSURER',
});

function api(extra: Parameters<typeof signInStaff>[1] = () => null, current = me) {
  return signInStaff(current, extra, (c) => {
    if (is(c, 'GET', `/api/v1/organizations/${orgId}/users`)) {
      return { status: 200, data: page(users) };
    }
    if (is(c, 'GET', `/api/v1/organizations/${orgId}/invitations`)) {
      return { status: 200, data: page(invitations) };
    }
    if (is(c, 'GET', '/api/v1/roles')) {
      return {
        status: 200,
        data: {
          roles: ['INSURER_ADMIN', 'UNDERWRITER', 'FINANCE'],
          managerRoles: ['INSURER_ADMIN'],
          managerInviteRoles: ['INSURER_ADMIN'],
        },
      };
    }
    return null;
  });
}

async function menuItems(row: HTMLElement) {
  await userEvent.click(within(row).getByRole('button', { name: /^Acções para/ }));
  const menu = await screen.findByRole('menu');
  const items = within(menu)
    .getAllByRole('menuitem')
    .map((item) => item.textContent);
  await userEvent.keyboard('{Escape}');
  await waitFor(() => expect(screen.queryByRole('menu')).not.toBeInTheDocument());
  return items;
}

describe('availableActions', () => {
  it('depende do estado e nunca há acções sobre o próprio', () => {
    expect(availableActions({ id: 'a', status: 'ACTIVE' }, 'a')).toEqual([]);
    expect(availableActions({ id: 'b', status: 'ACTIVE' }, 'a')).toEqual([
      'roles',
      'disable',
      'resetAccess',
    ]);
    expect(availableActions({ id: 'b', status: 'PENDING_ACTIVATION' }, 'a')).toEqual([]);
  });
});

describe('UsersPanel', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('não há acções na própria linha; as acções dependem do estado', async () => {
    restore = api();
    renderWithAuth(<UsersPanel scope={{ kind: 'organization', organizationId: orgId }} />);
    const self = await screen.findByTestId('user-row-u1@indico.co.mz');
    await screen.findByRole('button', { name: 'Acções para Pessoa u2' });
    expect(within(self).queryByRole('button')).not.toBeInTheDocument();
    expect(within(self).getByText('(você)')).toBeInTheDocument();
    expect(within(self).getByText('Gestor da seguradora')).toBeInTheDocument();

    expect(await menuItems(screen.getByTestId('user-row-u2@indico.co.mz'))).toEqual([
      'Alterar perfis',
      'Desactivar',
      'Repor acesso',
    ]);
    expect(await menuItems(screen.getByTestId('user-row-u3@indico.co.mz'))).toEqual([
      'Alterar perfis',
      'Desbloquear',
      'Desactivar',
      'Repor acesso',
    ]);
    expect(await menuItems(screen.getByTestId('user-row-u4@indico.co.mz'))).toEqual(['Activar']);
    expect(
      within(screen.getByTestId('user-row-u5@indico.co.mz')).queryByRole('button'),
    ).not.toBeInTheDocument();

    // Convites: só os pendentes ou expirados têm acções.
    expect(await menuItems(screen.getByTestId('invitation-row-nova@indico.co.mz'))).toEqual([
      'Reenviar',
      'Revogar',
    ]);
    expect(
      within(screen.getByTestId('invitation-row-velha@indico.co.mz')).queryByRole('button'),
    ).not.toBeInTheDocument();
  });

  it('"Repor acesso" pede confirmação e LAST_MANAGER mostra a mensagem própria', async () => {
    const calls: string[] = [];
    restore = api((c) => {
      if (c.method?.toUpperCase() === 'POST' && c.url?.startsWith('/api/v1/users/')) {
        calls.push(c.url);
        return c.url.endsWith('/disable')
          ? { status: 400, data: { code: 'LAST_MANAGER' } }
          : { status: 200, data: invitations[0] };
      }
      return null;
    });
    renderWithAuth(<UsersPanel scope={{ kind: 'organization', organizationId: orgId }} />);
    const row = await screen.findByTestId('user-row-u2@indico.co.mz');
    await userEvent.click(await within(row).findByRole('button', { name: /^Acções para/ }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Repor acesso' }));
    const dialog = await screen.findByRole('dialog', { name: 'Repor o acesso?' });
    expect(dialog).toHaveTextContent(
      'A pessoa recebe um novo convite e terá de configurar de novo a palavra-passe e o código.',
    );
    await userEvent.click(within(dialog).getByRole('button', { name: 'Repor acesso' }));
    await waitFor(() => expect(calls).toEqual(['/api/v1/users/u2/reset-access']));

    await userEvent.click(within(row).getByRole('button', { name: /^Acções para/ }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Desactivar' }));
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'A organização tem de ficar com pelo menos um gestor activo.',
    );
  });

  it('sem org-user:manage não há botão de convite nem acções', async () => {
    restore = api(() => null, { ...me, permissions: ['org-user:read'] });
    renderWithAuth(<UsersPanel scope={{ kind: 'organization', organizationId: orgId }} />);
    await screen.findByTestId('user-row-u2@indico.co.mz');
    expect(screen.queryByRole('button', { name: 'Convidar utilizador' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /^Acções para/ })).not.toBeInTheDocument();
  });

  it('convida com nome, e-mail e perfis', async () => {
    const bodies: unknown[] = [];
    restore = api((c) => {
      if (is(c, 'POST', `/api/v1/organizations/${orgId}/users`)) {
        bodies.push(JSON.parse(c.data as string));
        return { status: 201, data: invitations[0] };
      }
      return null;
    });
    renderWithAuth(<UsersPanel scope={{ kind: 'organization', organizationId: orgId }} />);
    const invite = await screen.findByRole('button', { name: 'Convidar utilizador' });
    await waitFor(() => expect(invite).toBeEnabled());
    await userEvent.click(invite);
    const dialog = await screen.findByRole('dialog', { name: 'Convidar utilizador' });
    await userEvent.type(within(dialog).getByLabelText('Nome'), 'Nova Pessoa');
    await userEvent.type(within(dialog).getByLabelText('E-mail'), 'nova@indico.co.mz');
    await userEvent.click(within(dialog).getByRole('checkbox', { name: 'Subscritor' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Enviar convite' }));
    await waitFor(() =>
      expect(bodies).toEqual([
        { fullName: 'Nova Pessoa', email: 'nova@indico.co.mz', roleCodes: ['UNDERWRITER'] },
      ]),
    );
    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument());
  });
});
