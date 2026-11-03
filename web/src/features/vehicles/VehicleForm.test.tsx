import { fireEvent, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { is, type MockResult } from '../../test/staff';
import { referenceHandler, registryFixture, vehicleFixture } from '../../test/vehicles';
import { VehicleForm } from './VehicleForm';

type Handler = (config: InternalAxiosRequestConfig) => MockResult | undefined;

let restore: () => void = () => {};
afterEach(() => restore());

/** API simulada: listas públicas mais os `handlers`; regista os pedidos. */
function api(...handlers: Handler[]) {
  const requests: InternalAxiosRequestConfig[] = [];
  restore = mockApi((config) => {
    requests.push(config);
    for (const handler of [referenceHandler, ...handlers]) {
      const result = handler(config);
      if (result) return result;
    }
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
  return requests;
}

function renderForm(vehicle = undefined as typeof vehicleFixture | undefined) {
  const onSaved = vi.fn();
  const onReload = vi.fn();
  renderWithProviders(<VehicleForm vehicle={vehicle} onSaved={onSaved} onReload={onReload} />);
  return { onSaved, onReload };
}

const field = (label: string) =>
  (label === 'Marca' || label === 'Modelo'
    ? screen.getByRole('combobox', { name: label })
    : screen.getByLabelText(label)) as HTMLInputElement;
const lookup = () => userEvent.click(screen.getByRole('button', { name: 'Procurar no registo' }));
const save = () => userEvent.click(screen.getByRole('button', { name: 'Guardar' }));

function fillValid() {
  fireEvent.change(field('Matrícula'), { target: { value: 'aaa-100-mc' } });
  fireEvent.change(field('N.º de chassis'), { target: { value: 'JTDBR32E720012345' } });
  fireEvent.change(field('Marca'), { target: { value: 'Toyota' } });
  fireEvent.change(field('Modelo'), { target: { value: 'Corolla' } });
  fireEvent.change(field('Ano'), { target: { value: '2018' } });
  fireEvent.change(field('Categoria'), { target: { value: 'LIGHT_PASSENGER' } });
  fireEvent.change(field('Cilindrada (cm³)'), { target: { value: '1598' } });
  fireEvent.change(field('Lugares'), { target: { value: '5' } });
  fireEvent.change(field('Uso'), { target: { value: 'PRIVATE' } });
  fireEvent.change(field('Província de circulação'), { target: { value: 'MAPUTO_CITY' } });
}

describe('VehicleForm', () => {
  it('mostra os exemplos de matrícula e põe a matrícula em maiúsculas', async () => {
    api();
    renderForm();
    expect(await screen.findByText('Ex.: AAA-100-MC ou MLA-12-34')).toBeInTheDocument();
    await userEvent.type(field('Matrícula'), 'aaa-100-mc');
    expect(field('Matrícula')).toHaveValue('AAA-100-MC');
  });

  it('"Procurar no registo" com 200 preenche os campos e mostra o aviso', async () => {
    const requests = api((config) =>
      is(config, 'GET', '/api/v1/me/vehicles/registry-lookup')
        ? { status: 200, data: registryFixture }
        : undefined,
    );
    renderForm();
    fireEvent.change(field('N.º de chassis'), { target: { value: 'OUTRO12345' } });
    fireEvent.change(field('Matrícula'), { target: { value: 'AAA-100-MC' } });
    await lookup();
    expect(
      await screen.findByText(
        'Preenchemos os dados a partir do registo automóvel. Confirme-os com o seu livrete.',
      ),
    ).toBeInTheDocument();
    const lookupRequest = requests.find((r) => r.url === '/api/v1/me/vehicles/registry-lookup');
    expect(lookupRequest?.params).toEqual({ plate: 'AAA-100-MC' });
    expect(field('N.º de chassis')).toHaveValue('JTDBR32E720012345');
    expect(field('Marca')).toHaveValue('Toyota');
    expect(field('Modelo')).toHaveValue('Corolla');
    expect(field('Ano')).toHaveValue('2018');
    expect(field('Categoria')).toHaveValue('LIGHT_PASSENGER');
    expect(field('Cilindrada (cm³)')).toHaveValue('1598');
    expect(field('Lugares')).toHaveValue('5');
    expect(field('Peso bruto (kg)')).toHaveValue('');
  });

  it.each([
    [404, 'VEHICLE_REGISTRY_NOT_FOUND', 'Não encontrámos esta matrícula no registo.'],
    [503, 'VEHICLE_REGISTRY_UNAVAILABLE', 'O registo automóvel não está disponível.'],
  ])('"Procurar no registo" com %i mostra o aviso e não apaga nada', async (status, code, text) => {
    api((config) =>
      is(config, 'GET', '/api/v1/me/vehicles/registry-lookup')
        ? { status, data: { status, code } }
        : undefined,
    );
    renderForm();
    fireEvent.change(field('Matrícula'), { target: { value: 'AAA-999-MC' } });
    fireEvent.change(field('N.º de chassis'), { target: { value: 'ABC12345' } });
    fireEvent.change(field('Marca'), { target: { value: 'Nissan' } });
    await lookup();
    expect(await screen.findByText(new RegExp(text))).toBeInTheDocument();
    expect(field('N.º de chassis')).toHaveValue('ABC12345');
    expect(field('Marca')).toHaveValue('Nissan');
    expect(field('Matrícula')).toHaveValue('AAA-999-MC');
    expect(field('Matrícula')).not.toBeDisabled();
  });

  it('"Procurar no registo" com 400 põe o erro na matrícula', async () => {
    api((config) =>
      is(config, 'GET', '/api/v1/me/vehicles/registry-lookup')
        ? {
            status: 400,
            data: { code: 'VALIDATION_ERROR', errors: [{ field: 'plate', code: 'INVALID_PLATE' }] },
          }
        : undefined,
    );
    renderForm();
    fireEvent.change(field('Matrícula'), { target: { value: 'X1' } });
    await lookup();
    await waitFor(() =>
      expect(field('Matrícula')).toHaveAccessibleDescription('Matrícula inválida.'),
    );
  });

  it('reboque esconde a cilindrada e fixa 0 lugares', async () => {
    let body: Record<string, unknown> = {};
    api((config) => {
      if (!is(config, 'POST', '/api/v1/me/vehicles')) return undefined;
      body = JSON.parse(config.data as string);
      return { status: 201, data: vehicleFixture };
    });
    const { onSaved } = renderForm();
    fillValid();
    fireEvent.change(field('Categoria'), { target: { value: 'TRAILER' } });
    await waitFor(() =>
      expect(screen.queryByLabelText('Cilindrada (cm³)')).not.toBeInTheDocument(),
    );
    expect(field('Lugares')).toHaveValue('0');
    expect(field('Lugares')).toBeDisabled();
    await save();
    expect(await screen.findByText('Campo obrigatório.')).toBeInTheDocument();
    expect(field('Peso bruto (kg)')).toHaveAccessibleDescription('Campo obrigatório.');
    fireEvent.change(field('Peso bruto (kg)'), { target: { value: '1200' } });
    await save();
    await waitFor(() => expect(onSaved).toHaveBeenCalled());
    expect(body).not.toHaveProperty('engineCc');
    expect(body).toMatchObject({ category: 'TRAILER', seats: 0, grossWeightKg: 1200 });
  });

  it('pesado sem peso bruto dá erro no browser', async () => {
    const requests = api();
    renderForm();
    fillValid();
    fireEvent.change(field('Categoria'), { target: { value: 'HEAVY' } });
    await save();
    await waitFor(() =>
      expect(field('Peso bruto (kg)')).toHaveAccessibleDescription('Campo obrigatório.'),
    );
    expect(requests.some((r) => r.method === 'post')).toBe(false);
  });

  it('VEHICLE_ALREADY_REGISTERED aparece no campo matrícula', async () => {
    api((config) =>
      is(config, 'POST', '/api/v1/me/vehicles')
        ? { status: 409, data: { status: 409, code: 'VEHICLE_ALREADY_REGISTERED' } }
        : undefined,
    );
    const { onSaved } = renderForm();
    fillValid();
    await save();
    await waitFor(() =>
      expect(field('Matrícula')).toHaveAccessibleDescription(
        'Já tem um veículo com esta matrícula.',
      ),
    );
    expect(onSaved).not.toHaveBeenCalled();
  });

  it('VEHICLE_LIMIT_REACHED aparece no topo', async () => {
    api((config) =>
      is(config, 'POST', '/api/v1/me/vehicles')
        ? { status: 409, data: { status: 409, code: 'VEHICLE_LIMIT_REACHED' } }
        : undefined,
    );
    renderForm();
    fillValid();
    await save();
    expect(await screen.findByRole('alert')).toHaveTextContent('Atingiu o máximo de 20 veículos.');
  });

  it('alterar envia a version e CONCURRENT_MODIFICATION pede para recarregar', async () => {
    let body: Record<string, unknown> = {};
    api((config) => {
      if (!is(config, 'PUT', '/api/v1/me/vehicles/v1')) return undefined;
      body = JSON.parse(config.data as string);
      return { status: 409, data: { status: 409, code: 'CONCURRENT_MODIFICATION' } };
    });
    const { onReload } = renderForm(vehicleFixture);
    expect(field('Matrícula')).toHaveValue('AAA-100-MC');
    await save();
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Este veículo foi alterado noutro dispositivo',
    );
    expect(body).toMatchObject({
      version: 2,
      plate: 'AAA-100-MC',
      commercialValue: { amount: 85000000, currency: 'MZN' },
    });
    await userEvent.click(screen.getByRole('button', { name: 'Recarregar' }));
    expect(onReload).toHaveBeenCalled();
  });

  it('erros de campo do servidor aparecem nos campos', async () => {
    api((config) =>
      is(config, 'POST', '/api/v1/me/vehicles')
        ? {
            status: 400,
            data: {
              code: 'VALIDATION_ERROR',
              errors: [{ field: 'plate', code: 'INVALID_PLATE' }],
            },
          }
        : undefined,
    );
    renderForm();
    fillValid();
    await save();
    await waitFor(() =>
      expect(field('Matrícula')).toHaveAccessibleDescription('Matrícula inválida.'),
    );
  });

  it('valor comercial com mais de 2 casas decimais é inválido', async () => {
    api();
    renderForm();
    fireEvent.change(field('Valor comercial'), { target: { value: '1,234' } });
    expect(
      await screen.findByText('Valor inválido. Use no máximo 2 casas decimais.'),
    ).toBeInTheDocument();
  });
});
