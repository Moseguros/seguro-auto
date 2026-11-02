import { fireEvent, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { CustomerProfileResponse } from '../../api/generated';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { ProfileForm } from './ProfileForm';
import { birthDateError, isValidName, profileSchema } from './profileSchema';

const empty: CustomerProfileResponse = { complete: false, version: 0 };
const existing: CustomerProfileResponse = {
  complete: true,
  version: 3,
  fullName: 'Ana Maria Mussa',
  birthDate: '1990-05-14',
  idDocType: 'BI',
  idDocNumberMasked: '*********345A',
  nuitMasked: '*****6789',
  province: 'MAPUTO_CITY',
};

function renderForm(profile: CustomerProfileResponse = empty) {
  const onSaved = vi.fn();
  const onConflict = vi.fn();
  renderWithProviders(<ProfileForm profile={profile} onSaved={onSaved} onConflict={onConflict} />);
  return { onSaved, onConflict };
}

function field(label: string) {
  return screen.getByLabelText(label) as HTMLInputElement;
}

async function fillValid() {
  await userEvent.type(field('Nome completo'), 'Ana Maria Mussa');
  fireEvent.change(field('Data de nascimento'), { target: { value: '1990-05-14' } });
  await userEvent.selectOptions(field('Tipo de documento'), 'BI');
  await userEvent.type(field('Número do documento'), '110100123345a');
  await userEvent.type(field('NUIT'), '123456789');
  await userEvent.selectOptions(field('Província'), 'MAPUTO_CITY');
}

const save = () => userEvent.click(screen.getByRole('button', { name: 'Guardar' }));

describe('regras do perfil', () => {
  const today = '2026-11-02';

  it('nome: duas palavras, só letras, espaços, apóstrofo e hífen', () => {
    expect(isValidName('Ana Mussa')).toBe(true);
    expect(isValidName("  Zé   D'Almeida-Côrte ")).toBe(true);
    expect(isValidName('Ana')).toBe(false);
    expect(isValidName('Ana -')).toBe(false);
    expect(isValidName('Ana 123')).toBe(false);
    expect(isValidName(`Ana ${'a'.repeat(120)}`)).toBe(false);
  });

  it('data de nascimento: 18 a 120 anos em Africa/Maputo', () => {
    expect(birthDateError('', today)).toBe('REQUIRED');
    expect(birthDateError('2008-11-02', today)).toBeNull();
    expect(birthDateError('2008-11-03', today)).toBe('UNDERAGE');
    expect(birthDateError('2027-01-01', today)).toBe('UNDERAGE');
    expect(birthDateError('1906-11-02', today)).toBeNull();
    expect(birthDateError('1905-11-01', today)).toBe('INVALID_DATE');
    expect(birthDateError('1990-02-30', today)).toBe('INVALID_DATE');
  });

  it('documento e NUIT obrigatórios só quando ainda não existem', () => {
    const values = {
      fullName: 'Ana Mussa',
      birthDate: '1990-01-01',
      idDocType: 'BI',
      idDocNumber: '',
      nuit: '',
      address: '',
      province: 'GAZA',
    };
    const required = profileSchema({ documentRequired: true, nuitRequired: true, today });
    const issues = required.safeParse(values).error?.issues.map((i) => [i.path[0], i.message]);
    expect(issues).toEqual([
      ['idDocNumber', 'REQUIRED'],
      ['nuit', 'REQUIRED'],
    ]);
    const optional = profileSchema({ documentRequired: false, nuitRequired: false, today });
    expect(optional.safeParse(values).success).toBe(true);
  });
});

describe('ProfileForm', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('campos obrigatórios em falta', async () => {
    renderForm();
    await save();
    expect(await screen.findAllByText('Campo obrigatório.')).toHaveLength(6);
  });

  it('valida cada regra', async () => {
    renderForm();
    await userEvent.type(field('Nome completo'), 'Ana');
    fireEvent.change(field('Data de nascimento'), { target: { value: '2020-01-01' } });
    await userEvent.type(field('Número do documento'), 'AB-123');
    await userEvent.type(field('NUIT'), '12345');
    fireEvent.change(field('Morada'), { target: { value: 'x'.repeat(301) } });
    await save();
    expect(await screen.findByText(/Indique o nome completo/)).toBeVisible();
    expect(screen.getByText('Tem de ter pelo menos 18 anos.')).toBeVisible();
    expect(screen.getByText(/Número de documento inválido/)).toBeVisible();
    expect(screen.getByText('O NUIT tem 9 dígitos.')).toBeVisible();
    expect(screen.getByText('Comprimento fora dos limites permitidos.')).toBeVisible();
  });

  it('data com mais de 120 anos é inválida', async () => {
    renderForm();
    fireEvent.change(field('Data de nascimento'), { target: { value: '1890-01-01' } });
    await save();
    expect(await screen.findByText('Data inválida.')).toBeVisible();
  });

  it('grava os dados normalizados', async () => {
    let body: unknown;
    restore = mockApi((config) => {
      body = JSON.parse(config.data as string);
      return { status: 200, data: { ...existing, version: 1 } };
    });
    const { onSaved } = renderForm();
    await fillValid();
    await save();
    await waitFor(() => expect(onSaved).toHaveBeenCalled(), { timeout: 3000 });
    expect(body).toEqual({
      fullName: 'Ana Maria Mussa',
      birthDate: '1990-05-14',
      idDocType: 'BI',
      idDocNumber: '110100123345A',
      nuit: '123456789',
      address: null,
      province: 'MAPUTO_CITY',
      version: 0,
    });
  });

  it('documento e NUIT existentes aparecem mascarados e vão a null se não mudarem', async () => {
    let body: Record<string, unknown> = {};
    restore = mockApi((config) => {
      body = JSON.parse(config.data as string);
      return { status: 200, data: existing };
    });
    const { onSaved } = renderForm(existing);
    expect(field('Número do documento')).toHaveValue('*********345A');
    expect(field('Número do documento')).toHaveAttribute('readonly');
    expect(field('NUIT')).toHaveValue('*****6789');
    await save();
    await waitFor(() => expect(onSaved).toHaveBeenCalled());
    expect(body.idDocNumber).toBeNull();
    expect(body.nuit).toBeNull();
    expect(body.version).toBe(3);
  });

  it('"Alterar" abre o campo vazio', async () => {
    renderForm(existing);
    await userEvent.click(screen.getByRole('button', { name: 'Alterar o NUIT' }));
    expect(field('NUIT')).toHaveValue('');
    expect(field('NUIT')).not.toHaveAttribute('readonly');
    expect(field('NUIT')).toHaveAttribute('inputmode', 'numeric');
  });

  it('mudar o tipo de documento obriga a indicar o número', async () => {
    renderForm(existing);
    await userEvent.selectOptions(field('Tipo de documento'), 'PASSPORT');
    expect(field('Número do documento')).toHaveValue('');
    await save();
    expect(await screen.findByText('Campo obrigatório.')).toBeVisible();
  });

  it('erros de campo da API aparecem nos campos', async () => {
    restore = mockApi(() => ({
      status: 400,
      data: { code: 'VALIDATION_ERROR', errors: [{ field: 'nuit', code: 'INVALID_NUIT' }] },
    }));
    renderForm();
    await fillValid();
    await save();
    expect(await screen.findByText('O NUIT tem 9 dígitos.')).toBeVisible();
  });

  it('409 CONCURRENT_MODIFICATION mostra a mensagem e pede para recarregar', async () => {
    restore = mockApi(() => ({ status: 409, data: { code: 'CONCURRENT_MODIFICATION' } }));
    const { onConflict } = renderForm(existing);
    await save();
    expect(await screen.findByRole('alert')).toHaveTextContent('Outra pessoa alterou estes dados');
    expect(onConflict).toHaveBeenCalled();
  });
});
