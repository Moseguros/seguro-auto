import { fireEvent, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { DriverResponse } from '../../api/generated';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { is, type MockResult } from '../../test/staff';
import { driverFixture, referenceHandler } from '../../test/vehicles';
import { DriverForm } from './DriverForm';
import { driverBirthDateError, licenceIssueDateError } from './driverSchema';

type Handler = (config: InternalAxiosRequestConfig) => MockResult | undefined;

let restore: () => void = () => {};
afterEach(() => restore());

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

function renderForm(driver?: DriverResponse, ownerTaken = false) {
  const onSaved = vi.fn();
  const onReload = vi.fn();
  renderWithProviders(
    <DriverForm driver={driver} ownerTaken={ownerTaken} onSaved={onSaved} onReload={onReload} />,
  );
  return { onSaved, onReload };
}

const field = (label: string) => screen.getByLabelText(label) as HTMLInputElement;
const save = () => userEvent.click(screen.getByRole('button', { name: 'Guardar' }));

const profile = {
  complete: true,
  version: 1,
  fullName: 'Ana Maria Mussa',
  birthDate: '1990-05-14',
};

async function fillValid() {
  fireEvent.change(field('Nome completo'), { target: { value: 'João  Carlos Mussa ' } });
  fireEvent.change(field('Data de nascimento'), { target: { value: '1985-02-10' } });
  fireEvent.change(field('N.º da carta'), { target: { value: 'map 123 4567' } });
  await userEvent.click(await screen.findByRole('checkbox', { name: 'B — Ligeiros' }));
  fireEvent.change(field('Data de emissão da carta'), { target: { value: '2005-03-01' } });
}

describe('regras do condutor', () => {
  const today = '2026-11-03';

  it('data de nascimento: no passado e até 100 anos', () => {
    expect(driverBirthDateError('', today)).toBe('REQUIRED');
    expect(driverBirthDateError('2026-11-03', today)).toBe('INVALID_DATE');
    expect(driverBirthDateError('2026-11-02', today)).toBeNull();
    expect(driverBirthDateError('1925-11-04', today)).toBeNull();
    expect(driverBirthDateError('1925-11-03', today)).toBe('INVALID_DATE');
    expect(driverBirthDateError('1990-02-30', today)).toBe('INVALID_DATE');
  });

  it('data de emissão: entre o nascimento e hoje', () => {
    expect(licenceIssueDateError('', '1990-01-01', today)).toBe('REQUIRED');
    expect(licenceIssueDateError('1989-12-31', '1990-01-01', today)).toBe(
      'LICENCE_ISSUE_DATE_INVALID',
    );
    expect(licenceIssueDateError('1990-01-01', '1990-01-01', today)).toBeNull();
    expect(licenceIssueDateError('2026-11-03', '1990-01-01', today)).toBeNull();
    expect(licenceIssueDateError('2026-11-04', '1990-01-01', today)).toBe(
      'LICENCE_ISSUE_DATE_INVALID',
    );
  });
});

describe('DriverForm', () => {
  it('valida no browser', async () => {
    const requests = api();
    renderForm();
    fireEvent.change(field('Nome completo'), { target: { value: 'Ana' } });
    fireEvent.change(field('Data de nascimento'), { target: { value: '1990-01-01' } });
    fireEvent.change(field('N.º da carta'), { target: { value: 'AB*12' } });
    fireEvent.change(field('Data de emissão da carta'), { target: { value: '1980-01-01' } });
    await save();
    expect(await screen.findByText(/Indique o nome completo/)).toBeInTheDocument();
    expect(screen.getByText('N.º da carta inválido.')).toBeInTheDocument();
    expect(screen.getByText('Campo obrigatório.')).toBeInTheDocument();
    expect(
      screen.getByText('A data de emissão tem de ser depois do nascimento e até hoje.'),
    ).toBeInTheDocument();
    expect(requests.some((r) => r.method === 'post')).toBe(false);
  });

  it('regista com os dados normalizados', async () => {
    let body: unknown;
    api((config) => {
      if (!is(config, 'POST', '/api/v1/me/drivers')) return undefined;
      body = JSON.parse(config.data as string);
      return { status: 201, data: driverFixture() };
    });
    const { onSaved } = renderForm();
    await fillValid();
    await save();
    await waitFor(() => expect(onSaved).toHaveBeenCalled());
    expect(body).toEqual({
      fullName: 'João Carlos Mussa',
      birthDate: '1985-02-10',
      licenceNumber: 'MAP1234567',
      licenceCategories: ['B'],
      licenceIssueDate: '2005-03-01',
      isOwner: false,
    });
  });

  it('"Sou eu" preenche o nome e a data de nascimento com o perfil', async () => {
    api((config) =>
      is(config, 'GET', '/api/v1/me/profile') ? { status: 200, data: profile } : undefined,
    );
    renderForm();
    await userEvent.click(screen.getByRole('checkbox', { name: 'Sou eu' }));
    await waitFor(() => expect(field('Nome completo')).toHaveValue('Ana Maria Mussa'));
    expect(field('Data de nascimento')).toHaveValue('1990-05-14');
    // Continuam editáveis.
    expect(field('Nome completo')).not.toHaveAttribute('readonly');
  });

  it('"Sou eu" escondido quando já existe outro condutor "Eu"', () => {
    api();
    renderForm(undefined, true);
    expect(screen.queryByRole('checkbox', { name: 'Sou eu' })).not.toBeInTheDocument();
  });

  it('alterar sem mexer na carta envia licenceNumber: null e a version', async () => {
    let body: Record<string, unknown> = {};
    api((config) => {
      if (!is(config, 'PUT', '/api/v1/me/drivers/d1')) return undefined;
      body = JSON.parse(config.data as string);
      return { status: 200, data: driverFixture({ version: 4 }) };
    });
    const { onSaved } = renderForm(driverFixture({ version: 3 }));
    expect(field('N.º da carta')).toHaveValue('****4567');
    expect(field('N.º da carta')).toHaveAttribute('readonly');
    await screen.findByRole('checkbox', { name: 'B — Ligeiros' });
    await save();
    await waitFor(() => expect(onSaved).toHaveBeenCalled());
    expect(body).toMatchObject({ licenceNumber: null, version: 3, licenceCategories: ['B'] });
  });

  it('"Alterar" a carta abre o campo vazio e obriga a preenchê-lo', async () => {
    api();
    renderForm(driverFixture());
    await userEvent.click(screen.getByRole('button', { name: 'Alterar o n.º da carta' }));
    expect(field('N.º da carta')).toHaveValue('');
    expect(field('N.º da carta')).not.toHaveAttribute('readonly');
    await save();
    await waitFor(() =>
      expect(field('N.º da carta')).toHaveAccessibleDescription('Campo obrigatório.'),
    );
  });

  it('DRIVER_ALREADY_REGISTERED aparece no campo da carta', async () => {
    api((config) =>
      is(config, 'POST', '/api/v1/me/drivers')
        ? { status: 409, data: { status: 409, code: 'DRIVER_ALREADY_REGISTERED' } }
        : undefined,
    );
    renderForm();
    await fillValid();
    await save();
    await waitFor(() =>
      expect(field('N.º da carta')).toHaveAccessibleDescription(
        'Já tem um condutor com esta carta.',
      ),
    );
  });

  it('OWNER_DRIVER_ALREADY_EXISTS aparece no interruptor e DRIVER_LIMIT_REACHED no topo', async () => {
    let code = 'OWNER_DRIVER_ALREADY_EXISTS';
    api((config) =>
      is(config, 'POST', '/api/v1/me/drivers') ? { status: 409, data: { code } } : undefined,
    );
    renderForm();
    await fillValid();
    await save();
    expect(await screen.findByText('Já se registou como condutor.')).toBeInTheDocument();
    code = 'DRIVER_LIMIT_REACHED';
    await save();
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Atingiu o máximo de 10 condutores.',
    );
  });
});
