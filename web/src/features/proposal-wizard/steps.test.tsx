import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { proposalFixture, renderStep, wizardContext } from '../../test/proposals';
import { renderWithAuth } from '../../test/render';
import { DeclarationsStep } from './DeclarationsStep';
import { InspectionStep } from './InspectionStep';
import { SuitabilityWarnings } from './SuitabilityWarnings';

let restore: () => void = () => {};
afterEach(() => restore());

function record() {
  const calls: InternalAxiosRequestConfig[] = [];
  return calls;
}

describe('DeclarationsStep', () => {
  it('"Sim" mostra e exige os seguimentos; "Não" limpa-os', async () => {
    const user = userEvent.setup();
    const calls = record();
    const proposal = proposalFixture();
    const saved = proposalFixture({ version: 4 });
    restore = mockApi((config) => {
      calls.push(config);
      return { status: 200, data: saved };
    });
    const context = wizardContext(proposal, { current: 'declaracoes' });
    renderStep(<DeclarationsStep />, context);

    const claims = screen.getByRole('radiogroup', { name: /acidente ou sinistro/ });
    await user.click(within(claims).getByRole('radio', { name: 'Sim' }));
    const details = screen.getByRole('textbox', { name: /Descreva brevemente/ });
    expect(screen.getByRole('textbox', { name: 'Quantos?' })).toBeInTheDocument();

    for (const group of screen.getAllByRole('radiogroup').slice(1)) {
      await user.click(within(group).getByRole('radio', { name: 'Não' }));
    }
    await user.click(screen.getByRole('checkbox', { name: /Declaro que as respostas/ }));
    await user.click(screen.getByRole('button', { name: 'Continuar' }));

    // Os seguimentos são obrigatórios com "Sim".
    expect(await screen.findAllByText(/^Campo obrigatório\./)).toHaveLength(2);
    expect(calls).toHaveLength(0);

    await user.type(details, 'Choque ligeiro num parque');
    await user.type(screen.getByRole('textbox', { name: 'Quantos?' }), '1');
    await user.click(screen.getByRole('button', { name: 'Continuar' }));
    await waitFor(() => expect(calls).toHaveLength(1));
    const body = JSON.parse(calls[0]!.data as string) as Record<string, unknown>;
    expect(calls[0]!.url).toBe('/api/v1/proposals/p1/declarations');
    expect(body).toMatchObject({ truthfulnessConfirmed: true, version: 3 });
    expect((body.answers as unknown[])[0]).toEqual({
      questionCode: 'CLAIMS_LAST_3_YEARS',
      answer: true,
      claimsCount: 1,
      details: 'Choque ligeiro num parque',
    });
    expect((body.answers as unknown[])[1]).toEqual({
      questionCode: 'PREVIOUS_REFUSAL',
      answer: false,
    });
    expect(context.update).toHaveBeenCalledWith(saved);
    expect(context.next).toHaveBeenCalled();

    // "Não" limpa os seguimentos.
    await user.click(within(claims).getByRole('radio', { name: 'Não' }));
    expect(screen.queryByRole('textbox', { name: 'Quantos?' })).not.toBeInTheDocument();
    await user.click(within(claims).getByRole('radio', { name: 'Sim' }));
    expect(screen.getByRole('textbox', { name: /Descreva brevemente/ })).toHaveValue('');
  });

  it('sem a caixa de veracidade não envia', async () => {
    const user = userEvent.setup();
    const calls = record();
    restore = mockApi((config) => {
      calls.push(config);
      return { status: 200, data: proposalFixture() };
    });
    renderStep(<DeclarationsStep />, wizardContext(proposalFixture()));
    for (const group of screen.getAllByRole('radiogroup')) {
      await user.click(within(group).getByRole('radio', { name: 'Não' }));
    }
    await user.click(screen.getByRole('button', { name: 'Continuar' }));
    expect(
      await screen.findByText('Confirme que as respostas são verdadeiras para continuar.'),
    ).toBeInTheDocument();
    expect(calls).toHaveLength(0);
  });
});

describe('SuitabilityWarnings', () => {
  it('mostra um aviso por motivo e chama acknowledgeProposalSuitability', async () => {
    const user = userEvent.setup();
    const calls = record();
    const acknowledged = proposalFixture({ version: 5 });
    restore = mockApi((config) => {
      calls.push(config);
      return { status: 200, data: acknowledged };
    });
    const onAcknowledged = vi.fn();
    renderWithAuth(
      <SuitabilityWarnings
        proposalId="p1"
        version={4}
        reasons={['CANNOT_AFFORD_REPAIR', 'DEDUCTIBLE_NOT_WANTED']}
        quotesPath="/cliente/simular"
        onAcknowledged={onAcknowledged}
      />,
    );
    const warnings = screen.getByTestId('suitability-warnings');
    const alerts = within(warnings).getAllByRole('alert');
    expect(alerts).toHaveLength(2);
    expect(alerts[0]).toHaveTextContent(/não conseguiria pagar a reparação/);
    expect(alerts[1]).toHaveTextContent(/não quer franquia/);
    expect(screen.getByRole('link', { name: 'Pedir outra cotação' })).toHaveAttribute(
      'href',
      '/cliente/simular',
    );

    await user.click(
      screen.getByRole('button', { name: 'Li os avisos e quero continuar com esta proposta' }),
    );
    await waitFor(() => expect(onAcknowledged).toHaveBeenCalledWith(acknowledged));
    expect(calls[0]!.url).toBe('/api/v1/proposals/p1/suitability/acknowledge');
    expect(JSON.parse(calls[0]!.data as string)).toEqual({ version: 4 });
  });
});

describe('InspectionStep', () => {
  it('"Enviar vistoria" fica desactivado com ângulos em falta', () => {
    restore = mockApi(() => ({ status: 404, data: { code: 'NOT_FOUND' } }));
    renderStep(<InspectionStep />, wizardContext(proposalFixture(), { current: 'vistoria' }));
    expect(screen.getByRole('button', { name: 'Enviar vistoria' })).toBeDisabled();
    expect(screen.getByTestId('photo-slot-FRONT')).toHaveAttribute('data-missing', 'true');
    expect(
      within(screen.getByTestId('photo-slot-CHASSIS')).getByText(/número do chassis gravado/),
    ).toBeInTheDocument();
  });

  it('um erro INSPECTION_PHOTOS_MISSING marca os cartões', async () => {
    const user = userEvent.setup();
    const calls = record();
    restore = mockApi((config) => {
      calls.push(config);
      if (config.url?.startsWith('/api/v1/files/')) return { status: 404, data: {} };
      return {
        status: 409,
        data: { code: 'INSPECTION_PHOTOS_MISSING', missingSlots: ['REAR', 'CHASSIS'] },
      };
    });
    const proposal = proposalFixture();
    const photos = (['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'] as const).map(
      (slot, i) => ({
        id: `ph${i}`,
        slot,
        fileId: `f${i}`,
        source: 'UPLOAD' as const,
        uploadedAt: '2027-01-08T08:00:00Z',
      }),
    );
    renderStep(
      <InspectionStep />,
      wizardContext({
        ...proposal,
        inspection: { ...proposal.inspection, missingSlots: [], photos },
      }),
    );
    expect(screen.getByTestId('photo-slot-REAR')).not.toHaveAttribute('data-missing');
    await user.type(screen.getByRole('textbox', { name: 'Quilometragem actual' }), '45210');
    await user.click(screen.getByRole('button', { name: 'Enviar vistoria' }));

    await waitFor(() =>
      expect(screen.getByTestId('photo-slot-REAR')).toHaveAttribute('data-missing', 'true'),
    );
    expect(screen.getByTestId('photo-slot-CHASSIS')).toHaveAttribute('data-missing', 'true');
    expect(screen.getByTestId('photo-slot-FRONT')).not.toHaveAttribute('data-missing');
    expect(screen.getByText('Faltam fotografias obrigatórias.')).toBeInTheDocument();
    const submit = calls.find((c) => c.url === '/api/v1/proposals/p1/inspection/submit');
    expect(JSON.parse(submit!.data as string)).toEqual({ odometerKm: 45210, version: 3 });
  });
});
