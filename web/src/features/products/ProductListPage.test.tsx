import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';

const page = <T,>(content: T[]) => ({ content, page: 0, size: 20, totalElements: content.length });

const product = {
  id: 'p1',
  code: 'RC-AUTO',
  name: 'RC Automóvel Demo',
  type: 'MTPL_ONLY',
  status: 'PUBLISHED',
  currentVersionNo: 1,
  hasDraft: true,
  updatedAt: '2026-11-03T08:00:00Z',
};

describe('ProductListPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('sem product:read vai para /sem-acesso sem chamar a API dos produtos', async () => {
    const calls: string[] = [];
    restore = signInStaff(
      staffMe({ roles: ['FINANCE'], permissions: ['org-user:read'], orgType: 'INSURER' }),
      (c) => {
        calls.push(c.url ?? '');
        return null;
      },
    );
    const { router } = renderRoute('/seguradora/produtos');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'));
    expect(calls.filter((url) => url.startsWith('/api/v1/insurer'))).toEqual([]);
  });

  it('o item "Produtos" do menu só aparece com product:read', async () => {
    restore = signInStaff(
      staffMe({ roles: ['UNDERWRITER'], permissions: ['product:read'], orgType: 'INSURER' }),
      (c) => {
        if (is(c, 'GET', '/api/v1/insurer/products')) return { status: 200, data: page([product]) };
        if (is(c, 'GET', '/api/v1/insurer/branding')) {
          return { status: 200, data: { logoFileId: null } };
        }
        return null;
      },
    );
    renderRoute('/seguradora/produtos');
    // A grelha demora no jsdom quando os testes correm em paralelo.
    expect(
      await screen.findByText('RC Automóvel Demo', undefined, { timeout: 10000 }),
    ).toBeInTheDocument();
    // Sem product:manage não há criação.
    expect(screen.queryByRole('button', { name: 'Novo produto' })).not.toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Abrir menu' }));
    const nav = await screen.findByRole('navigation', { name: 'Seguradora' });
    expect(within(nav).getByRole('link', { name: 'Produtos' })).toBeInTheDocument();
  });

  it('cria um produto e abre o editor da versão 1', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(
      staffMe({
        roles: ['PRODUCT_MANAGER'],
        permissions: ['product:read', 'product:manage'],
        orgType: 'INSURER',
      }),
      (c) => {
        if (is(c, 'GET', '/api/v1/insurer/products')) return { status: 200, data: page([]) };
        if (is(c, 'GET', '/api/v1/insurer/branding')) {
          return { status: 200, data: { logoFileId: null } };
        }
        if (is(c, 'POST', '/api/v1/insurer/products')) {
          const body = JSON.parse(c.data as string) as { code: string };
          bodies.push(body);
          if (body.code === 'RC-AUTO') {
            return { status: 409, data: { code: 'PRODUCT_CODE_ALREADY_EXISTS' } };
          }
          return {
            status: 201,
            data: {
              id: 'p2',
              draftVersion: { id: 'v9', versionNo: 1, status: 'DRAFT' },
              versions: [],
            },
          };
        }
        return null;
      },
    );
    const { router } = renderRoute('/seguradora/produtos');
    expect(await screen.findByText('Ainda não há produtos.')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Novo produto' }));
    const dialog = await screen.findByRole('dialog', { name: 'Novo produto' });
    await userEvent.type(within(dialog).getByLabelText(/Código/), 'rc-auto');
    expect(within(dialog).getByLabelText(/Código/)).toHaveValue('RC-AUTO');
    await userEvent.type(within(dialog).getByLabelText(/Nome/), 'RC Nova');
    await userEvent.selectOptions(within(dialog).getByLabelText(/Tipo/), 'MTPL_ONLY');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Criar produto' }));
    expect(
      await within(dialog).findByText('Já existe um produto com este código.'),
    ).toBeInTheDocument();

    await userEvent.clear(within(dialog).getByLabelText(/Código/));
    await userEvent.type(within(dialog).getByLabelText(/Código/), 'RC-NOVA');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Criar produto' }));
    await waitFor(() =>
      expect(router.state.location.pathname).toBe('/seguradora/produtos/p2/versoes/v9'),
    );
    expect(router.state.location.search).toBe('?passo=dados');
    expect(bodies).toEqual([
      { code: 'RC-AUTO', name: 'RC Nova', type: 'MTPL_ONLY' },
      { code: 'RC-NOVA', name: 'RC Nova', type: 'MTPL_ONLY' },
    ]);
  });
});
