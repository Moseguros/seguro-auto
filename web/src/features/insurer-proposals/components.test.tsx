import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { useAuth } from '../../auth/useAuth';
import { renderWithAuth, renderWithProviders } from '../../test/render';
import { insurerProposalFixture } from '../../test/insurerProposals';
import { is, signInStaff, staffMe } from '../../test/staff';
import { DecisionBar } from './DecisionBar';
import { ProposalsTabs } from './ProposalsTabs';
import { RejectDialog } from './RejectDialog';
import { RequestInfoDialog } from './RequestInfoDialog';
import { SlaChip } from './SlaChip';

/** "pronto" só depois de o `me` chegar (as permissões já se conhecem). */
function Ready() {
  const { me } = useAuth();
  return me ? <span>pronto</span> : null;
}

const underwriter = staffMe({
  roles: ['UNDERWRITER'],
  permissions: ['proposal:read', 'proposal:decide', 'kyc:review', 'inspection:review'],
  orgType: 'INSURER',
});

describe('ProposalsTabs', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('mostra só os separadores com permissão', async () => {
    restore = signInStaff(
      staffMe({ permissions: ['proposal:read', 'inspection:review'], orgType: 'INSURER' }),
    );
    renderWithAuth(<ProposalsTabs />, { path: '/seguradora/propostas' });
    await screen.findByRole('tab', { name: 'Propostas' });
    const tabs = screen.getByRole('tablist');
    expect(within(tabs).getByRole('tab', { name: 'Propostas' })).toBeInTheDocument();
    expect(within(tabs).getByRole('tab', { name: 'Vistorias' })).toBeInTheDocument();
    expect(within(tabs).queryByRole('tab', { name: 'Identidade' })).not.toBeInTheDocument();
  });

  it('com as três permissões mostra os três', async () => {
    restore = signInStaff(underwriter);
    renderWithAuth(<ProposalsTabs />, { path: '/seguradora/propostas/kyc' });
    await screen.findByRole('tab', { name: 'Identidade' });
    const tabs = screen.getByRole('tablist');
    expect(
      within(tabs)
        .getAllByRole('tab')
        .map((tab) => tab.textContent),
    ).toEqual(['Propostas', 'Identidade', 'Vistorias']);
    expect(within(tabs).getByRole('tab', { name: 'Identidade' })).toHaveAttribute(
      'aria-selected',
      'true',
    );
  });
});

describe('SlaChip', () => {
  it.each([
    ['ON_TIME', 'No prazo', 'MuiChip-colorSuccess'],
    ['ALERT', 'Prazo a terminar', 'MuiChip-colorWarning'],
    ['OVERDUE', 'Prazo ultrapassado', 'MuiChip-colorError'],
  ] as const)('%s → "%s"', (status, label, color) => {
    renderWithProviders(<SlaChip status={status} dueAt="2027-01-23T10:00:00Z" />);
    const chip = screen.getByText(label).closest('.MuiChip-root');
    expect(chip).toHaveClass(color);
    expect(chip).toHaveAttribute('title', 'Prazo até 23/01/27, 12:00');
  });

  it('sem estado mostra um traço', () => {
    renderWithProviders(<SlaChip status={null} />);
    expect(screen.getByText('—')).toBeInTheDocument();
  });
});

describe('DecisionBar', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('não aparece sem proposal:decide', async () => {
    restore = signInStaff(staffMe({ permissions: ['proposal:read'], orgType: 'INSURER' }));
    renderWithAuth(
      <>
        <Ready />
        <DecisionBar proposal={insurerProposalFixture()} onOutcome={() => {}} />
      </>,
    );
    expect(await screen.findByText('pronto')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Recusar' })).not.toBeInTheDocument();
  });

  it('não aparece fora de UNDER_REVIEW', async () => {
    restore = signInStaff(underwriter);
    renderWithAuth(
      <>
        <Ready />
        <DecisionBar
          proposal={insurerProposalFixture({ status: 'ACCEPTED' })}
          onOutcome={() => {}}
        />
      </>,
    );
    expect(await screen.findByText('pronto')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Recusar' })).not.toBeInTheDocument();
  });

  it('"Aceitar" desactivado com KYC por verificar e requiredForAutomaticAcceptance', async () => {
    restore = signInStaff(underwriter);
    const proposal = insurerProposalFixture({
      inspection: { ...insurerProposalFixture().inspection, status: 'APPROVED' },
    });
    renderWithAuth(<DecisionBar proposal={proposal} onOutcome={() => {}} />);
    expect(await screen.findByRole('button', { name: 'Aceitar' })).toBeDisabled();
    expect(
      screen.getByText('A identidade do cliente ainda não foi verificada.'),
    ).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Pedir informação' })).toBeEnabled();
    expect(screen.getByRole('button', { name: 'Recusar' })).toBeEnabled();
  });

  it('"Aceitar" activo com o KYC verificado e a vistoria aprovada', async () => {
    restore = signInStaff(underwriter);
    const base = insurerProposalFixture();
    const proposal = insurerProposalFixture({
      inspection: { ...base.inspection, status: 'APPROVED' },
      kyc: { ...base.kyc, state: 'VERIFIED' },
    });
    renderWithAuth(<DecisionBar proposal={proposal} onOutcome={() => {}} />);
    expect(await screen.findByRole('button', { name: 'Aceitar' })).toBeEnabled();
  });

  it('"Aceitar" activo sem KYC quando o KYC simplificado basta', async () => {
    restore = signInStaff(underwriter);
    const base = insurerProposalFixture();
    const proposal = insurerProposalFixture({
      inspection: { ...base.inspection, status: 'APPROVED' },
      kyc: { ...base.kyc, requiredForAutomaticAcceptance: false },
    });
    renderWithAuth(<DecisionBar proposal={proposal} onOutcome={() => {}} />);
    expect(await screen.findByRole('button', { name: 'Aceitar' })).toBeEnabled();
  });
});

describe('RejectDialog', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('não envia sem motivo nem com texto de menos de 10 caracteres', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(underwriter, (c) => {
      if (is(c, 'POST', '/api/v1/insurer/proposals/p1/reject')) {
        bodies.push(JSON.parse(c.data as string));
        return { status: 200, data: insurerProposalFixture({ status: 'REJECTED' }) };
      }
      return null;
    });
    const onOutcome = vi.fn();
    renderWithAuth(
      <RejectDialog proposal={insurerProposalFixture()} onClose={() => {}} onOutcome={onOutcome} />,
    );
    const confirm = await screen.findByRole('button', { name: 'Recusar' });
    expect(
      screen.getByText('Esta decisão é comunicada ao cliente e não pode ser desfeita.'),
    ).toBeInTheDocument();

    await userEvent.click(confirm);
    expect(bodies).toHaveLength(0);
    expect(screen.getByText('Obrigatório.')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('combobox', { name: /Motivo/ }));
    await userEvent.click(await screen.findByRole('option', { name: 'Risco não aceite' }));
    await userEvent.type(screen.getByRole('textbox', { name: /Explicação/ }), 'Curto');
    await userEvent.click(confirm);
    expect(bodies).toHaveLength(0);

    await userEvent.type(
      screen.getByRole('textbox', { name: /Explicação/ }),
      ' de mais, agora sim.',
    );
    await userEvent.click(confirm);
    await waitFor(() => expect(onOutcome).toHaveBeenCalled());
    expect(bodies).toEqual([
      { reasonCode: 'RISK_NOT_ACCEPTED', reason: 'Curto de mais, agora sim.', version: 7 },
    ]);
    expect(onOutcome).toHaveBeenCalledWith({ kind: 'done', notice: 'Proposta recusada' });
  });

  it('409 PROPOSAL_NOT_UNDER_REVIEW manda recarregar', async () => {
    restore = signInStaff(underwriter, (c) =>
      is(c, 'POST', '/api/v1/insurer/proposals/p1/reject')
        ? { status: 409, data: { code: 'PROPOSAL_NOT_UNDER_REVIEW' } }
        : null,
    );
    const onOutcome = vi.fn();
    renderWithAuth(
      <RejectDialog proposal={insurerProposalFixture()} onClose={() => {}} onOutcome={onOutcome} />,
    );
    await userEvent.click(await screen.findByRole('combobox', { name: /Motivo/ }));
    await userEvent.click(await screen.findByRole('option', { name: 'Outro motivo' }));
    await userEvent.type(
      screen.getByRole('textbox', { name: /Explicação/ }),
      'Um texto suficiente.',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Recusar' }));
    await waitFor(() =>
      expect(onOutcome).toHaveBeenCalledWith({
        kind: 'reload',
        message: 'A proposta não está em análise.',
      }),
    );
  });
});

describe('RequestInfoDialog', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('exige pelo menos um item', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(underwriter, (c) => {
      if (is(c, 'POST', '/api/v1/insurer/proposals/p1/request-info')) {
        bodies.push(JSON.parse(c.data as string));
        return { status: 200, data: insurerProposalFixture({ status: 'INFO_REQUESTED' }) };
      }
      return null;
    });
    const onOutcome = vi.fn();
    renderWithAuth(
      <RequestInfoDialog
        proposal={insurerProposalFixture()}
        onClose={() => {}}
        onOutcome={onOutcome}
      />,
    );
    expect(
      await screen.findByText(
        'A assinatura do cliente deixa de valer e ele terá de assinar de novo.',
      ),
    ).toBeInTheDocument();
    await userEvent.type(
      screen.getByRole('textbox', { name: /Mensagem/ }),
      'Repita as fotografias da frente.',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Enviar pedido' }));
    expect(bodies).toHaveLength(0);
    expect(screen.getByText('Escolha pelo menos um item.')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('checkbox', { name: 'Vistoria' }));
    await userEvent.click(screen.getByRole('button', { name: 'Enviar pedido' }));
    await waitFor(() => expect(onOutcome).toHaveBeenCalled());
    expect(bodies).toEqual([
      { items: ['INSPECTION'], message: 'Repita as fotografias da frente.', version: 7 },
    ]);
  });
});
