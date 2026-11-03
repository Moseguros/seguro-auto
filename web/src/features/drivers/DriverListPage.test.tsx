import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { renderRoute } from '../../test/render';
import { is } from '../../test/staff';
import { driverFixture, page, referenceHandler } from '../../test/vehicles';

let restore: () => void = () => {};
afterEach(() => restore());

const ana = driverFixture({
  id: 'd1',
  fullName: 'Ana Maria Mussa',
  isOwner: true,
  licenceCategories: ['B', 'Z'],
  vehicleIds: ['v1', 'v2'],
});

describe('lista de condutores', () => {
  it('mostra "Eu", as categorias, a carta mascarada e os veículos', async () => {
    restore = signInCustomer(customerMe(), referenceHandler, (config) =>
      is(config, 'GET', '/api/v1/me/drivers') ? { status: 200, data: page([ana]) } : undefined,
    );
    renderRoute('/cliente/condutores');
    const card = await screen.findByTestId('driver-card');
    expect(within(card).getByText('Ana Maria Mussa')).toBeInTheDocument();
    expect(within(card).getByText('Eu')).toBeInTheDocument();
    expect(await within(card).findByText('Categorias: B — Ligeiros, Z')).toBeInTheDocument();
    expect(within(card).getByText('Carta ****4567')).toBeInTheDocument();
    expect(within(card).getByText('2 veículos')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Registar condutor' })).toHaveAttribute(
      'href',
      '/cliente/condutores/novo',
    );
  });

  it('vazio', async () => {
    restore = signInCustomer(customerMe(), referenceHandler, (config) =>
      is(config, 'GET', '/api/v1/me/drivers') ? { status: 200, data: page([]) } : undefined,
    );
    renderRoute('/cliente/condutores');
    expect(await screen.findByText('Ainda não registou condutores.')).toBeInTheDocument();
  });

  it('DRIVER_IS_HABITUAL aparece na confirmação e o condutor fica', async () => {
    let deletes = 0;
    restore = signInCustomer(customerMe(), referenceHandler, (config) => {
      if (is(config, 'GET', '/api/v1/me/drivers')) return { status: 200, data: page([ana]) };
      if (is(config, 'DELETE', '/api/v1/me/drivers/d1')) {
        deletes += 1;
        return { status: 409, data: { status: 409, code: 'DRIVER_IS_HABITUAL' } };
      }
      return undefined;
    });
    renderRoute('/cliente/condutores');
    await userEvent.click(await screen.findByRole('button', { name: 'Acções de Ana Maria Mussa' }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Retirar' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Retirar' }));
    expect(await within(dialog).findByRole('alert')).toHaveTextContent(
      'Este condutor é o habitual de um veículo. Escolha outro habitual primeiro.',
    );
    expect(deletes).toBe(1);
    expect(screen.getByTestId('driver-card')).toBeInTheDocument();
  });

  it('retirar um condutor actualiza a lista', async () => {
    let removed = false;
    restore = signInCustomer(customerMe(), referenceHandler, (config) => {
      if (is(config, 'GET', '/api/v1/me/drivers')) {
        return { status: 200, data: page(removed ? [] : [ana]) };
      }
      if (is(config, 'DELETE', '/api/v1/me/drivers/d1')) {
        removed = true;
        return { status: 204 };
      }
      return undefined;
    });
    renderRoute('/cliente/condutores');
    await userEvent.click(await screen.findByRole('button', { name: 'Acções de Ana Maria Mussa' }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Retirar' }));
    await userEvent.click(
      within(await screen.findByRole('dialog')).getByRole('button', { name: 'Retirar' }),
    );
    expect(await screen.findByText('Condutor retirado')).toBeInTheDocument();
    await waitFor(() => expect(screen.queryByTestId('driver-card')).not.toBeInTheDocument());
  });
});
