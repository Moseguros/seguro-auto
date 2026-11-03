import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import type { ProductVersionResponse } from '../../api/generated';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe, type StaffHandler } from '../../test/staff';
import { draftVersion, productWith, publishedVersion } from './testData';

const manager = staffMe({
  roles: ['PRODUCT_MANAGER'],
  permissions: ['product:read', 'product:manage'],
  orgType: 'INSURER',
});
const reader = staffMe({
  roles: ['UNDERWRITER'],
  permissions: ['product:read'],
  orgType: 'INSURER',
});

function api(
  version: ProductVersionResponse,
  me = manager,
  calls: string[] = [],
  extra: StaffHandler = () => null,
) {
  return signInStaff(me, extra, (c) => {
    calls.push(`${c.method?.toUpperCase()} ${c.url}`);
    if (is(c, 'GET', `/api/v1/insurer/product-versions/${version.id}`)) {
      return { status: 200, data: version };
    }
    if (is(c, 'GET', `/api/v1/insurer/products/${version.productId}`)) {
      return { status: 200, data: productWith(version) };
    }
    if (is(c, 'GET', '/api/v1/platform-settings')) {
      return {
        status: 200,
        data: { settings: [{ key: 'MTPL_MIN_CAPITAL', value: '300000000', valueType: 'MONEY' }] },
      };
    }
    return null;
  });
}

const path = (version: ProductVersionResponse, step = 'dados') =>
  `/seguradora/produtos/${version.productId}/versoes/${version.id}?passo=${step}`;

describe('ProductVersionEditorPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('versão PUBLISHED: só de leitura, com o aviso e "Criar nova versão"', async () => {
    restore = api(publishedVersion);
    renderRoute(path(publishedVersion));
    expect(await screen.findByTestId('read-only-notice')).toHaveTextContent(
      'Versão 1 publicada em 03/11/26. Uma versão publicada não pode ser alterada.',
    );
    expect(await screen.findByRole('button', { name: 'Criar nova versão' })).toBeInTheDocument();
    expect(screen.getByRole('checkbox', { name: 'Ligeiro de passageiros' })).toBeDisabled();
    expect(screen.getByRole('checkbox', { name: 'Ligeiro de passageiros' })).toBeChecked();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();

    // Os outros passos também não se alteram.
    await userEvent.click(screen.getByRole('button', { name: /Seguinte/ }));
    expect(await screen.findByRole('heading', { name: 'Coberturas' })).toBeInTheDocument();
    expect(screen.getByLabelText('Capital')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Acrescentar cobertura' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });

  it('o passo 5 de uma versão publicada só mostra a pré-visualização', async () => {
    const calls: string[] = [];
    restore = api(publishedVersion, manager, calls);
    renderRoute(path(publishedVersion, 'revisao'));
    expect(await screen.findByTestId('catalog-preview')).toHaveTextContent(
      'Responsabilidade civil obrigatória',
    );
    expect(screen.queryByRole('button', { name: 'Publicar versão' })).not.toBeInTheDocument();
    expect(calls.some((call) => call.endsWith('/validate'))).toBe(false);
  });

  it('sem product:manage a versão em preparação é só de leitura', async () => {
    restore = api(draftVersion, reader);
    renderRoute(path(draftVersion));
    expect(await screen.findByTestId('read-only-notice')).toHaveTextContent(
      'Pode consultar esta versão, mas não alterá-la.',
    );
    expect(screen.getByRole('checkbox', { name: 'Particular' })).toBeDisabled();
    expect(screen.getByLabelText('Notas da versão')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Criar nova versão' })).not.toBeInTheDocument();
  });

  it('com product:manage a versão em preparação grava os dados gerais com a version', async () => {
    const bodies: unknown[] = [];
    restore = api(draftVersion, manager, [], (c) => {
      if (is(c, 'PUT', `/api/v1/insurer/product-versions/${draftVersion.id}`)) {
        bodies.push(JSON.parse(c.data as string));
        return { status: 200, data: { ...draftVersion, usages: ['PRIVATE'], version: 1 } };
      }
      return null;
    });
    const { router } = renderRoute(path(draftVersion));
    await userEvent.click(await screen.findByRole('checkbox', { name: 'Particular' }));
    await userEvent.click(screen.getByRole('button', { name: 'Guardar e continuar' }));
    await waitFor(() =>
      expect(bodies).toEqual([{ vehicleCategories: [], usages: ['PRIVATE'], version: 0 }]),
    );
    await waitFor(() => expect(router.state.location.search).toBe('?passo=coberturas'));
  });
});
