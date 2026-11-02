import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { problemError } from '../../test/problemError';
import { renderWithProviders } from '../../test/render';
import { OrganizationForm } from './OrganizationForm';
import { toCreateRequest, type OrganizationFormValues } from './organizationSchema';

const ombudsmen = {
  content: [
    {
      id: 'omb-1',
      displayName: 'Provedor Nacional',
      type: 'OMBUDSMAN',
      status: 'ACTIVE',
      nuit: '400000001',
      createdAt: '2026-10-01T00:00:00Z',
    },
  ],
  page: 0,
  size: 100,
  totalElements: 1,
};

function renderForm(
  onSubmit = vi.fn<(values: OrganizationFormValues) => Promise<void>>(async () => {}),
) {
  renderWithProviders(
    <OrganizationForm mode="create" submitLabel="Criar organização" onSubmit={onSubmit} />,
  );
  return onSubmit;
}

const label = (text: string) => new RegExp(`^${text}`);

async function fillCommon(type: string) {
  await userEvent.selectOptions(screen.getByLabelText(label('Tipo')), type);
  await userEvent.type(screen.getByLabelText(label('Nome legal')), 'Seguradora Índico, SA');
  await userEvent.type(screen.getByLabelText(label('NUIT')), '400 123 456');
  await userEvent.type(screen.getByLabelText(label('E-mail de contacto')), 'geral@indico.co.mz');
  await userEvent.type(screen.getByLabelText(/^Nome$|^Nome \*/), 'Maria Gestora');
  await userEvent.type(screen.getByLabelText(/^E-mail$|^E-mail \*/), 'maria@indico.co.mz');
}

describe('OrganizationForm', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('o perfil da seguradora só aparece para Seguradora, com os provedores activos', async () => {
    const requests: unknown[] = [];
    restore = mockApi((config) => {
      requests.push(config.params);
      return { status: 200, data: ombudsmen };
    });
    renderForm();
    expect(screen.queryByTestId('insurer-profile')).not.toBeInTheDocument();
    await userEvent.selectOptions(screen.getByLabelText(label('Tipo')), 'BROKER');
    expect(screen.queryByTestId('insurer-profile')).not.toBeInTheDocument();
    await userEvent.selectOptions(screen.getByLabelText(label('Tipo')), 'INSURER');
    expect(screen.getByTestId('insurer-profile')).toBeInTheDocument();
    expect(screen.getByLabelText(label('Código curto'))).toBeInTheDocument();
    expect(await screen.findByRole('option', { name: 'Provedor Nacional' })).toBeInTheDocument();
    expect(requests).toContainEqual({ type: 'OMBUDSMAN', status: 'ACTIVE', size: 100 });
  });

  it.each(['INSURER', 'BROKER', 'AGENT'])('o n.º ASFPM é obrigatório para %s', async (type) => {
    restore = mockApi(() => ({ status: 200, data: ombudsmen }));
    const onSubmit = renderForm();
    await fillCommon(type);
    await userEvent.click(screen.getByRole('button', { name: 'Criar organização' }));
    expect(
      await screen.findByText('Indique o número de autorização da ASFPM.'),
    ).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('o n.º ASFPM é opcional para os outros tipos', async () => {
    const onSubmit = renderForm();
    await fillCommon('WORKSHOP');
    await userEvent.click(screen.getByRole('button', { name: 'Criar organização' }));
    await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));
    const values = onSubmit.mock.calls[0]![0];
    expect(toCreateRequest(values)).toEqual({
      type: 'WORKSHOP',
      legalName: 'Seguradora Índico, SA',
      nuit: '400123456',
      contactEmail: 'geral@indico.co.mz',
      tradeName: undefined,
      licenseNumber: undefined,
      contactPhone: undefined,
      address: undefined,
      province: undefined,
      insurerProfile: undefined,
    });
  });

  it('valida o perfil da seguradora e envia-o', async () => {
    restore = mockApi(() => ({ status: 200, data: ombudsmen }));
    const onSubmit = renderForm();
    await fillCommon('INSURER');
    await userEvent.type(screen.getByLabelText(label('N.º de autorização ASFPM')), 'ASF-001');
    await userEvent.click(screen.getByRole('button', { name: 'Criar organização' }));
    expect(await screen.findAllByText('Campo obrigatório.')).toHaveLength(2);
    expect(onSubmit).not.toHaveBeenCalled();

    await userEvent.type(screen.getByLabelText(label('Código curto')), 'idc');
    await userEvent.type(screen.getByLabelText(label('E-mail de reclamações')), 'rec@indico.co.mz');
    await userEvent.selectOptions(
      await screen.findByLabelText(label('Provedor do cliente')),
      'omb-1',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Criar organização' }));
    await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));
    expect(toCreateRequest(onSubmit.mock.calls[0]![0]).insurerProfile).toEqual({
      shortCode: 'IDC',
      complaintsEmail: 'rec@indico.co.mz',
      ombudsmanOrganizationId: 'omb-1',
    });
  });

  it('os erros do servidor aparecem nos campos', async () => {
    const onSubmit = renderForm(
      vi.fn(async () => {
        throw problemError(409, { code: 'NUIT_ALREADY_IN_USE' });
      }),
    );
    await fillCommon('WORKSHOP');
    await userEvent.click(screen.getByRole('button', { name: 'Criar organização' }));
    expect(await screen.findByText('Já existe uma organização com este NUIT.')).toBeInTheDocument();
    expect(screen.getByLabelText(label('NUIT'))).toHaveAttribute('aria-invalid', 'true');
    expect(onSubmit).toHaveBeenCalled();
  });
});
