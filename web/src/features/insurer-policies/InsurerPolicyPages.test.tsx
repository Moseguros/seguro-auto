import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { PolicyDetailResponse, PolicyDocument } from '../../api/generated';
import { renderRoute, renderWithProviders } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { DocumentsList } from '../policies/common/DocumentsList';
import { PolicyTimeline } from './PolicyTimeline';
import { readPolicyFilters } from './PolicyFilters';

const download = vi.hoisted(() => vi.fn(async () => {}));
vi.mock('../../files/downloadFile', () => ({ downloadFile: download }));

const mzn = (amount: number) => ({ amount, currency: 'MZN' });
const slow = { timeout: 10_000 };

const policy: PolicyDetailResponse = {
  id: 'p1',
  number: 'AP-2027-000001',
  status: 'ACTIVE',
  coverStatus: 'COVERED',
  coverageType: 'MTPL_ONLY',
  policyholderName: 'Ana Tomador',
  insurer: { id: 'o1', name: 'Seguradora Demo' },
  product: { name: 'RC Auto', type: 'MTPL_ONLY', versionId: 'v1' },
  proposal: { id: 'pr1', number: 'PR-2027-000123' },
  vehicle: { id: 'v1', plate: 'ABC-123-MC', make: 'Toyota', model: 'Hilux', year: 2020 },
  startAt: '2027-02-10T08:31:00Z',
  endAt: '2028-02-09T21:59:59Z',
  issuedAt: '2027-02-10T08:31:00Z',
  paymentPlan: 'FULL',
  renewalType: 'ANNUAL_RENEWABLE',
  totalPremium: mzn(299900),
  coverages: [
    {
      code: 'MTPL',
      mandatory: true,
      capital: mzn(300000000),
      deductibleType: 'NONE',
      premium: mzn(299900),
    },
  ],
  instalments: [
    {
      seq: 1,
      dueDate: '2027-02-10',
      amount: mzn(299900),
      status: 'PAID',
      paidAt: '2027-02-10T08:31:00Z',
    },
  ],
  documents: [
    { type: 'CERTIFICATE', status: 'READY', fileId: 'f1', fileName: 'certificado.pdf' },
    { type: 'SCHEDULE', status: 'PENDING' },
  ],
  events: [{ type: 'ISSUED', occurredAt: '2027-02-10T08:31:00Z', actorType: 'SYSTEM' }],
};

function insurer(permissions: string[]) {
  return staffMe({
    roles: ['CLAIMS_HANDLER'],
    permissions,
    orgType: 'INSURER',
    displayName: 'Carla Sinistros',
  });
}

const api = (c: InternalAxiosRequestConfig) => {
  if (is(c, 'GET', '/api/v1/insurer/policies/p1')) return { status: 200, data: policy };
  if (is(c, 'GET', '/api/v1/insurer/policies/nada')) {
    return { status: 404, data: { code: 'POLICY_NOT_FOUND' } };
  }
  return null;
};

describe('apólices da seguradora', { timeout: 30_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => {
    restore();
    download.mockClear();
  });

  it('os filtros vêm da URL', () => {
    const filters = readPolicyFilters(
      new URLSearchParams('status=ACTIVE&status=XX&productType=FLEET&q=ABC&sort=foo,asc'),
    );
    expect(filters).toMatchObject({
      status: ['ACTIVE'],
      productType: 'FLEET',
      q: 'ABC',
      sort: 'issuedAt,desc',
    });
  });

  it('sem proposal:read o número da proposta aparece sem ligação', async () => {
    restore = signInStaff(insurer(['policy:read']), api);
    renderRoute('/seguradora/apolices/p1');
    const proposal = await screen.findByTestId('policy-proposal', undefined, slow);
    expect(proposal).toHaveTextContent('Proposta PR-2027-000123');
    expect(proposal.tagName).not.toBe('A');
    expect(screen.queryByRole('link', { name: /PR-2027-000123/ })).not.toBeInTheDocument();
  });

  it('com proposal:read liga à proposta e mostra os separadores', async () => {
    restore = signInStaff(insurer(['policy:read', 'proposal:read']), api);
    renderRoute('/seguradora/apolices/p1');
    expect(
      await screen.findByRole('link', { name: 'Ver proposta PR-2027-000123' }, slow),
    ).toHaveAttribute('href', '/seguradora/propostas/pr1');
    expect(screen.getByRole('heading', { level: 1, name: 'AP-2027-000001' })).toBeInTheDocument();
    expect(screen.getByText('Responsabilidade civil obrigatória')).toBeInTheDocument();
    expect(screen.getAllByText(/^2\s?999,00 MT$/).length).toBeGreaterThan(0);

    await userEvent.click(screen.getByRole('tab', { name: 'Prestações' }));
    expect(screen.getByText('Paga')).toBeInTheDocument();
    expect(screen.getByText('10/02/27, 10:31')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('tab', { name: 'Histórico' }));
    expect(screen.getByText('Apólice emitida')).toBeInTheDocument();
  });

  it('404 mostra "Apólice não encontrada" com ligação para a lista', async () => {
    restore = signInStaff(insurer(['policy:read']), api);
    renderRoute('/seguradora/apolices/nada');
    expect(
      await screen.findByRole('heading', { name: 'Apólice não encontrada' }, slow),
    ).toBeInTheDocument();
    expect(screen.getAllByRole('link', { name: 'Voltar às apólices' })[0]).toHaveAttribute(
      'href',
      '/seguradora/apolices',
    );
  });

  it('a linha temporal usa formatDateTime em Maputo', () => {
    renderWithProviders(
      <PolicyTimeline
        events={[
          { type: 'DOCUMENTS_READY', occurredAt: '2027-02-10T08:31:00Z', actorType: 'STAFF' },
        ]}
      />,
    );
    expect(screen.getByText('Documentos disponíveis')).toBeInTheDocument();
    expect(screen.getByText('10/02/27, 10:31 · Por: Colaborador')).toBeInTheDocument();
  });
});

describe('DocumentsList', () => {
  const documents: PolicyDocument[] = [
    { type: 'CERTIFICATE', status: 'READY', fileId: 'f1', fileName: 'certificado.pdf' },
    { type: 'SCHEDULE', status: 'PENDING' },
  ];
  afterEach(() => download.mockClear());

  it('PENDING sem botão; READY chama downloadFile', async () => {
    renderWithProviders(<DocumentsList documents={documents} />);
    const pending = screen.getByTestId('document-SCHEDULE');
    expect(within(pending).getByText('A preparar…')).toBeInTheDocument();
    expect(within(pending).queryByRole('button')).not.toBeInTheDocument();

    const ready = screen.getByTestId('document-CERTIFICATE');
    await userEvent.click(within(ready).getByRole('button', { name: /Descarregar/ }));
    await waitFor(() => expect(download).toHaveBeenCalledWith('f1', 'certificado.pdf'));
  });

  it('um erro do download aparece', async () => {
    download.mockRejectedValueOnce(new Error('x'));
    renderWithProviders(<DocumentsList documents={documents} />);
    await userEvent.click(screen.getByRole('button', { name: /Descarregar/ }));
    expect(await screen.findByRole('alert')).toBeInTheDocument();
  });
});
