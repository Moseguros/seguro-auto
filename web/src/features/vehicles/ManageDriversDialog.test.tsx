import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { DriverResponse } from '../../api/generated';
import { customerMe, signInCustomer } from '../../test/customer';
import { renderWithAuth } from '../../test/render';
import { is } from '../../test/staff';
import { driverFixture, page, vehicleFixture } from '../../test/vehicles';
import { ManageDriversDialog } from './ManageDriversDialog';

const ana = driverFixture({ id: 'd1', fullName: 'Ana Maria Mussa', isOwner: true });
const joao = driverFixture({ id: 'd2', fullName: 'João Carlos Mussa' });

let restore: () => void = () => {};
afterEach(() => restore());

function renderDialog(drivers: DriverResponse[], vehicle = vehicleFixture) {
  let body: Record<string, unknown> | undefined;
  restore = signInCustomer(customerMe(), (config) => {
    if (is(config, 'GET', '/api/v1/me/drivers')) return { status: 200, data: page(drivers) };
    if (is(config, 'PUT', '/api/v1/me/vehicles/v1/drivers')) {
      body = JSON.parse(config.data as string);
      return { status: 200, data: { ...vehicle, version: vehicle.version + 1 } };
    }
    return undefined;
  });
  const onSaved = vi.fn();
  const onClose = vi.fn();
  renderWithAuth(<ManageDriversDialog vehicle={vehicle} onClose={onClose} onSaved={onSaved} />);
  return { onSaved, onClose, body: () => body };
}

const box = (name: string) => screen.findByRole('checkbox', { name });
const habitual = (name: string) =>
  screen.getByRole('radio', { name: `${name} é o condutor habitual` });
const saveButton = () => screen.getByRole('button', { name: 'Guardar' });

describe('ManageDriversDialog', () => {
  it('não grava sem habitual', async () => {
    renderDialog([ana, joao]);
    await userEvent.click(await box('Ana Maria Mussa'));
    expect(saveButton()).toBeDisabled();
    expect(screen.getByText('Escolha o condutor habitual.')).toBeInTheDocument();
    await userEvent.click(habitual('Ana Maria Mussa'));
    expect(saveButton()).toBeEnabled();
    expect(screen.queryByText('Escolha o condutor habitual.')).not.toBeInTheDocument();
  });

  it('só um habitual; envia a lista com a version do veículo', async () => {
    const { onSaved, body } = renderDialog([ana, joao]);
    await userEvent.click(await box('Ana Maria Mussa'));
    await userEvent.click(await box('João Carlos Mussa'));
    await userEvent.click(habitual('Ana Maria Mussa'));
    await userEvent.click(habitual('João Carlos Mussa'));
    expect(habitual('Ana Maria Mussa')).not.toBeChecked();
    expect(habitual('João Carlos Mussa')).toBeChecked();
    await userEvent.click(saveButton());
    await waitFor(() => expect(onSaved).toHaveBeenCalled());
    expect(body()).toEqual({
      drivers: [
        { driverId: 'd1', habitual: false },
        { driverId: 'd2', habitual: true },
      ],
      version: 2,
    });
  });

  it('desmarcar o habitual obriga a escolher outro', async () => {
    renderDialog([ana, joao], {
      ...vehicleFixture,
      drivers: [
        { driverId: 'd1', fullName: 'Ana Maria Mussa', habitual: true },
        { driverId: 'd2', fullName: 'João Carlos Mussa', habitual: false },
      ],
    });
    expect(await box('Ana Maria Mussa')).toBeChecked();
    expect(habitual('Ana Maria Mussa')).toBeChecked();
    await userEvent.click(await box('Ana Maria Mussa'));
    expect(saveButton()).toBeDisabled();
  });

  it('lista vazia pode ser gravada (veículo sem condutores)', async () => {
    const { onSaved, body } = renderDialog([ana]);
    await box('Ana Maria Mussa');
    await userEvent.click(saveButton());
    await waitFor(() => expect(onSaved).toHaveBeenCalled());
    expect(body()).toEqual({ drivers: [], version: 2 });
  });

  it('no máximo 5 condutores', async () => {
    const drivers = Array.from({ length: 6 }, (_, i) =>
      driverFixture({ id: `d${i}`, fullName: `Condutor Número${'a'.repeat(i + 1)}` }),
    );
    renderDialog(drivers);
    for (const driver of drivers.slice(0, 5)) await userEvent.click(await box(driver.fullName));
    expect(await box(drivers[5]!.fullName)).toBeDisabled();
  });

  it('sem condutores mostra "Registar condutor" com o veículo', async () => {
    renderDialog([]);
    const dialog = await screen.findByRole('dialog');
    expect(await within(dialog).findByText('Ainda não registou condutores.')).toBeInTheDocument();
    expect(within(dialog).getByRole('link', { name: 'Registar condutor' })).toHaveAttribute(
      'href',
      '/cliente/condutores/novo?veiculo=v1',
    );
    expect(within(dialog).queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
});
