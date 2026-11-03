import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { renderRoute } from '../../test/render';
import { is } from '../../test/staff';
import { page, referenceHandler, vehicleFixture } from '../../test/vehicles';

let restore: () => void = () => {};
afterEach(() => restore());

const withDriver = {
  ...vehicleFixture,
  id: 'v2',
  plate: 'MLA-12-34',
  inatroVerified: false,
  registrationDoc: {
    fileId: 'f1',
    contentType: 'application/pdf',
    sizeBytes: 1000,
    uploadedAt: '2026-11-03T10:00:00Z',
  },
  drivers: [{ driverId: 'd1', fullName: 'Ana Maria Mussa', habitual: true }],
};

describe('lista de veículos', () => {
  it('mostra os cartões com condutor habitual, livrete e registo', async () => {
    restore = signInCustomer(customerMe(), referenceHandler, (config) =>
      is(config, 'GET', '/api/v1/me/vehicles')
        ? { status: 200, data: page([vehicleFixture, withDriver]) }
        : undefined,
    );
    renderRoute('/cliente/veiculos');
    const cards = await screen.findAllByTestId('vehicle-card');
    expect(cards).toHaveLength(2);
    const [first, second] = cards as [HTMLElement, HTMLElement];
    expect(within(first).getByText('AAA-100-MC')).toBeInTheDocument();
    expect(within(first).getByText('Toyota Corolla · 2018')).toBeInTheDocument();
    expect(within(first).getByText('Ligeiro de passageiros · Particular')).toBeInTheDocument();
    expect(within(first).getByText('Sem condutor habitual')).toBeInTheDocument();
    expect(within(first).getByText('Livrete em falta')).toBeInTheDocument();
    expect(within(first).getByText('Confirmado no registo')).toBeInTheDocument();
    expect(within(second).getByText('Condutor habitual: Ana Maria Mussa')).toBeInTheDocument();
    expect(within(second).queryByText('Livrete em falta')).not.toBeInTheDocument();
    expect(within(second).queryByText('Confirmado no registo')).not.toBeInTheDocument();
    expect(within(first).getByRole('link')).toHaveAttribute('href', '/cliente/veiculos/v1');
  });

  it('a carregar mostra 3 esqueletos; vazio mostra o texto e o botão', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/vehicles') ? { status: 200, data: page([]) } : undefined,
    );
    renderRoute('/cliente/veiculos');
    expect(await screen.findAllByTestId('card-skeleton')).toHaveLength(3);
    expect(await screen.findByText('Ainda não registou nenhum veículo.')).toBeInTheDocument();
    expect(screen.getAllByRole('link', { name: 'Acrescentar veículo' })).toHaveLength(2);
  });

  it('erro mostra a mensagem e "Tentar de novo"', async () => {
    let fail = true;
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/vehicles')
        ? fail
          ? { status: 500, data: { code: 'INTERNAL_ERROR' } }
          : { status: 200, data: page([vehicleFixture]) }
        : undefined,
    );
    renderRoute('/cliente/veiculos');
    expect(await screen.findByRole('alert')).toBeInTheDocument();
    fail = false;
    await userEvent.click(screen.getByRole('button', { name: 'Tentar de novo' }));
    expect(await screen.findByTestId('vehicle-card')).toBeInTheDocument();
  });

  it('com mais de 50 veículos mostra "Ver mais"', async () => {
    restore = signInCustomer(customerMe(), (config) => {
      if (!is(config, 'GET', '/api/v1/me/vehicles')) return undefined;
      const pageNo = (config.params as { page: number }).page;
      const content = [{ ...vehicleFixture, id: `v-${pageNo}`, plate: `P-${pageNo}` }];
      return { status: 200, data: { content, page: pageNo, size: 50, totalElements: 51 } };
    });
    renderRoute('/cliente/veiculos');
    await userEvent.click(await screen.findByRole('button', { name: 'Ver mais' }));
    expect(await screen.findByText('P-1')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Ver mais' })).not.toBeInTheDocument();
  });
});

describe('detalhe do veículo', () => {
  it('404 VEHICLE_NOT_FOUND mostra a mensagem e a ligação para a lista', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/vehicles/x')
        ? { status: 404, data: { status: 404, code: 'VEHICLE_NOT_FOUND' } }
        : undefined,
    );
    renderRoute('/cliente/veiculos/x');
    expect(await screen.findByRole('alert')).toHaveTextContent('Veículo não encontrado.');
    expect(screen.getByRole('link', { name: 'Voltar aos veículos' })).toHaveAttribute(
      'href',
      '/cliente/veiculos',
    );
  });

  it('mostra os dados, o livrete e os condutores', async () => {
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/me/vehicles/v2') ? { status: 200, data: withDriver } : undefined,
    );
    renderRoute('/cliente/veiculos/v2');
    expect(await screen.findByRole('heading', { name: 'MLA-12-34' })).toBeInTheDocument();
    expect(screen.getByText('Não confirmado no registo')).toBeInTheDocument();
    expect(screen.getByText(/850\s000,00 MT/)).toBeInTheDocument();
    expect(screen.getByText('Livrete em PDF')).toBeInTheDocument();
    const driver = screen.getByTestId('vehicle-driver');
    expect(driver).toHaveTextContent('Ana Maria Mussa');
    expect(driver).toHaveTextContent('Habitual');
  });

  it('VEHICLE_REMOVAL_BLOCKED aparece na confirmação e o veículo fica', async () => {
    restore = signInCustomer(customerMe(), (config) => {
      if (is(config, 'GET', '/api/v1/me/vehicles/v1')) {
        return { status: 200, data: vehicleFixture };
      }
      if (is(config, 'DELETE', '/api/v1/me/vehicles/v1')) {
        return { status: 409, data: { status: 409, code: 'VEHICLE_REMOVAL_BLOCKED' } };
      }
      return undefined;
    });
    const { router } = renderRoute('/cliente/veiculos/v1');
    await userEvent.click(await screen.findByRole('button', { name: 'Retirar veículo' }));
    const dialog = await screen.findByRole('dialog');
    expect(dialog).toHaveTextContent('O veículo deixa de aparecer na sua conta.');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Retirar' }));
    expect(await within(dialog).findByRole('alert')).toHaveTextContent(
      'Este veículo tem uma apólice em vigor e não pode ser retirado.',
    );
    expect(router.state.location.pathname).toBe('/cliente/veiculos/v1');
  });

  it('retirar volta à lista com o aviso "Veículo retirado"', async () => {
    let removed = false;
    restore = signInCustomer(customerMe(), (config) => {
      if (is(config, 'GET', '/api/v1/me/vehicles/v1')) {
        return { status: 200, data: vehicleFixture };
      }
      if (is(config, 'DELETE', '/api/v1/me/vehicles/v1')) {
        removed = true;
        return { status: 204 };
      }
      if (is(config, 'GET', '/api/v1/me/vehicles')) {
        return { status: 200, data: page(removed ? [] : [vehicleFixture]) };
      }
      return undefined;
    });
    const { router } = renderRoute('/cliente/veiculos/v1');
    await userEvent.click(await screen.findByRole('button', { name: 'Retirar veículo' }));
    await userEvent.click(
      within(await screen.findByRole('dialog')).getByRole('button', { name: 'Retirar' }),
    );
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/veiculos'));
    expect(await screen.findByText('Veículo retirado')).toBeInTheDocument();
  });
});

describe('menu', () => {
  it('o item "Veículos" fica activo em /cliente/condutores', async () => {
    restore = signInCustomer(customerMe(), referenceHandler, (config) =>
      is(config, 'GET', '/api/v1/me/drivers') ? { status: 200, data: page([]) } : undefined,
    );
    renderRoute('/cliente/condutores');
    expect(await screen.findByRole('heading', { name: 'Condutores' })).toBeInTheDocument();
    // O separador "Condutores" fica marcado.
    expect(screen.getByRole('tab', { name: 'Condutores' })).toHaveAttribute(
      'aria-selected',
      'true',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Abrir menu' }));
    const nav = await screen.findByRole('navigation', { name: 'Área do cliente' });
    expect(within(nav).getByRole('link', { name: 'Veículos' })).toHaveClass('Mui-selected');
    expect(within(nav).getByRole('link', { name: 'Perfil' })).not.toHaveClass('Mui-selected');
  });
});
