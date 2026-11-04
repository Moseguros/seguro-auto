import { screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { RowDialog } from './RowDialog';

describe('RowDialog', () => {
  it('gte ≥ lt mostra validation.INVALID_RANGE e não aplica', async () => {
    const onApply = vi.fn();
    renderWithProviders(
      <RowDialog row={null} kind="FACTOR" onClose={() => {}} onApply={onApply} />,
    );
    const group = screen.getByRole('group', { name: 'Idade do condutor (anos)' });
    await userEvent.click(within(group).getByRole('checkbox'));
    await userEvent.type(within(group).getByLabelText('A partir de (≥)'), '70');
    await userEvent.type(within(group).getByLabelText('Menos de (<)'), '25');
    await userEvent.type(screen.getByLabelText('Factor'), '1,15');
    await userEvent.click(screen.getByRole('button', { name: 'Aplicar' }));
    expect(
      within(group).getByText('O limite inferior tem de ser menor que o superior.'),
    ).toBeInTheDocument();
    expect(onApply).not.toHaveBeenCalled();
  });

  it('uma taxa de 150 % é recusada; 2,5 % passa a "0.025"', async () => {
    const onApply = vi.fn();
    renderWithProviders(<RowDialog row={null} kind="RATE" onClose={() => {}} onApply={onApply} />);
    const rate = screen.getByLabelText('Taxa (%)');
    await userEvent.type(rate, '150');
    await userEvent.click(screen.getByRole('button', { name: 'Aplicar' }));
    expect(screen.getByText('Valor fora dos limites permitidos.')).toBeInTheDocument();
    expect(onApply).not.toHaveBeenCalled();

    await userEvent.clear(rate);
    await userEvent.type(rate, '2,5');
    await userEvent.click(screen.getByRole('button', { name: 'Aplicar' }));
    expect(onApply).toHaveBeenCalledWith({ when: {}, value: '0.025' });
  });

  it('a variável sugerida começa ligada numa linha nova', async () => {
    const onApply = vi.fn();
    renderWithProviders(
      <RowDialog
        row={null}
        kind="FACTOR"
        suggested="driverAgeYears"
        onClose={() => {}}
        onApply={onApply}
      />,
    );
    const group = screen.getByRole('group', { name: 'Idade do condutor (anos)' });
    await userEvent.type(within(group).getByLabelText('Menos de (<)'), '25');
    await userEvent.type(screen.getByLabelText('Factor'), '1,30');
    await userEvent.click(screen.getByRole('button', { name: 'Aplicar' }));
    expect(onApply).toHaveBeenCalledWith({ when: { driverAgeYears: { lt: 25 } }, value: '1.30' });
  });

  it('condição usada sem limites é obrigatória', async () => {
    const onApply = vi.fn();
    renderWithProviders(
      <RowDialog
        row={{ when: {}, value: 450000 }}
        rowNumber={2}
        kind="AMOUNT"
        onClose={() => {}}
        onApply={onApply}
      />,
    );
    expect(screen.getByText('Editar a linha 2')).toBeInTheDocument();
    const group = screen.getByRole('group', { name: 'Lugares' });
    await userEvent.click(within(group).getByRole('checkbox'));
    await userEvent.click(screen.getByRole('button', { name: 'Aplicar' }));
    expect(within(group).getByText(/obrigatório/i)).toBeInTheDocument();
    expect(onApply).not.toHaveBeenCalled();
  });
});
