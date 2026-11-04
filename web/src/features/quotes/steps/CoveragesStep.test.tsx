import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from '../../../test/mockApi';
import { catalogProduct, catalogProductB } from '../../../test/quotes';
import { renderWithProviders } from '../../../test/render';
import { page } from '../../../test/vehicles';
import { CoveragesStep } from './CoveragesStep';
import { harnessState, StepHarness } from './stepHarness';
import type { RiskVehicle } from './stepTypes';

const vehicle: RiskVehicle = {
  vehicleId: 'v1',
  category: 'LIGHT_PASSENGER',
  usage: 'PRIVATE',
  commercialValue: { amount: 85000000, currency: 'MZN' },
};

function renderStep(productType = 'COMPREHENSIVE') {
  const state = harnessState();
  renderWithProviders(
    <StepHarness
      initial={{ productType }}
      state={state}
      render={(props) => <CoveragesStep {...props} vehicle={vehicle} />}
    />,
  );
  return state;
}

// Fluxos com vários ecrãs: mais tempo quando a máquina está carregada.
describe('CoveragesStep', { timeout: 60_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('mostra as obrigatórias marcadas e desactivadas e as opcionais com "disponível em"', async () => {
    restore = mockApi(() => ({ status: 200, data: page([catalogProduct(), catalogProductB]) }));
    renderStep();
    const mtpl = await screen.findByRole('checkbox', {
      name: 'Responsabilidade civil obrigatória',
    });
    expect(mtpl).toBeChecked();
    expect(mtpl).toBeDisabled();
    expect(screen.getByText(/^Obrigatória/)).toBeInTheDocument();
    expect(screen.getAllByText(/Disponível em 1 de 2 produtos/)).toHaveLength(2);
  });

  it('em COMPREHENSIVE começam marcadas choque, incêndio e natureza; furto marca as três', async () => {
    restore = mockApi(() => ({ status: 200, data: page([catalogProduct(), catalogProductB]) }));
    const state = renderStep();
    const collision = await screen.findByRole('checkbox', {
      name: 'Choque, colisão e capotamento',
    });
    const fire = screen.getByRole('checkbox', { name: 'Incêndio, raio e explosão' });
    const nature = screen.getByRole('checkbox', { name: 'Fenómenos da natureza' });
    const theft = screen.getByRole('checkbox', { name: 'Furto ou roubo' });
    await waitFor(() => expect(collision).toBeChecked());
    expect(fire).toBeChecked();
    expect(nature).toBeChecked();
    expect(theft).not.toBeChecked();

    await userEvent.click(fire);
    expect(fire).not.toBeChecked();
    await userEvent.click(theft);
    expect(theft).toBeChecked();
    expect(fire).toBeChecked();
    expect(
      screen.getByText(
        'Furto ou roubo inclui obrigatoriamente choque, incêndio e fenómenos da natureza.',
      ),
    ).toBeInTheDocument();

    // Desmarcar uma das três desmarca o furto.
    await userEvent.click(nature);
    expect(nature).not.toBeChecked();
    expect(theft).not.toBeChecked();
    expect(state.draft.coverages.map((c) => c.code).sort()).toEqual(['OD_COLLISION', 'OD_FIRE']);
  });

  it('sem nenhuma OD_* em COMPREHENSIVE: OWN_DAMAGE_REQUIRED e "Continuar" inválido', async () => {
    restore = mockApi(() => ({ status: 200, data: page([catalogProduct()]) }));
    const state = renderStep();
    const collision = await screen.findByRole('checkbox', {
      name: 'Choque, colisão e capotamento',
    });
    await waitFor(() => expect(collision).toBeChecked());
    await waitFor(() => expect(state.valid).toBe(true));
    for (const name of [
      'Choque, colisão e capotamento',
      'Incêndio, raio e explosão',
      'Fenómenos da natureza',
    ]) {
      await userEvent.click(screen.getByRole('checkbox', { name }));
    }
    expect(
      screen.getByText('Escolha pelo menos uma cobertura de danos próprios.'),
    ).toBeInTheDocument();
    expect(state.valid).toBe(false);
  });

  it('capital de OPTIONS: o menor por defeito, com a união dos capitais', async () => {
    restore = mockApi(() => ({ status: 200, data: page([catalogProduct(), catalogProductB]) }));
    const state = renderStep();
    await userEvent.click(
      await screen.findByRole('checkbox', { name: 'Responsabilidade civil facultativa' }),
    );
    const select = screen.getByLabelText(/Capital de Responsabilidade civil facultativa/);
    expect(Array.from((select as HTMLSelectElement).options).map((o) => o.value)).toEqual([
      '250000000',
      '500000000',
      '1000000000',
    ]);
    await userEvent.selectOptions(select, '1000000000');
    expect(state.draft.coverages.find((c) => c.code === 'VTPL')?.capital?.amount).toBe(1000000000);
  });

  it('sem produtos do tipo: aviso e passo inválido', async () => {
    restore = mockApi(() => ({ status: 200, data: page([]) }));
    const state = renderStep('TEMPORARY');
    expect(
      await screen.findByText('De momento não há produtos deste tipo para este veículo.'),
    ).toBeInTheDocument();
    expect(state.valid).toBe(false);
  });
});
