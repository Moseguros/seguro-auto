import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '../../../test/render';
import { harnessState, StepHarness } from './stepHarness';
import type { RiskVehicle } from './stepTypes';
import { TypeStep } from './TypeStep';

function renderStep(vehicle: RiskVehicle, mode: 'customer' | 'broker' = 'customer') {
  const state = harnessState();
  renderWithProviders(
    <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <StepHarness
        initial={{}}
        state={state}
        render={(props) => <TypeStep {...props} mode={mode} vehicle={vehicle} />}
      />
    </MemoryRouter>,
  );
  return state;
}

describe('TypeStep', () => {
  it('três cartões; escolher um torna o passo válido', async () => {
    const state = renderStep({ vehicleId: 'v1', commercialValue: { amount: 1, currency: 'MZN' } });
    expect(screen.getAllByRole('radio')).toHaveLength(3);
    await userEvent.click(screen.getByRole('radio', { name: /Só responsabilidade civil/ }));
    expect(state.draft.productType).toBe('MTPL_ONLY');
    expect(state.valid).toBe(true);
    await userEvent.click(screen.getByRole('radio', { name: /Seguro temporário/ }));
    expect(state.draft.productType).toBe('TEMPORARY');
  });

  it('sem valor comercial COMPREHENSIVE fica desactivado, com "Indicar valor" no cliente', () => {
    renderStep({ vehicleId: 'v1', commercialValue: null });
    const card = screen.getByRole('radio', { name: /Responsabilidade civil \+ danos próprios/ });
    expect(card).toHaveAttribute('aria-disabled', 'true');
    expect(screen.getByText('Precisa do valor comercial do veículo')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Indicar valor' })).toHaveAttribute(
      'href',
      '/cliente/veiculos/v1/editar?voltar=%2Fcliente%2Fsimular',
    );
  });

  it('no mediador sem valor: "Indique o valor no passo Veículo"', () => {
    renderStep({ commercialValue: null }, 'broker');
    expect(screen.getByText('Indique o valor no passo Veículo')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Indicar valor' })).not.toBeInTheDocument();
  });
});
