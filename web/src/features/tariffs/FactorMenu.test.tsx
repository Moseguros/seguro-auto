import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { FactorMenu } from './FactorMenu';

describe('FactorMenu', () => {
  it('os presets que a cobertura já tem ficam desactivados', async () => {
    const onAdd = vi.fn();
    renderWithProviders(<FactorMenu existingCodes={['DRIVER_AGE', 'TRACKER']} onAdd={onAdd} />);
    await userEvent.click(screen.getByRole('button', { name: 'Acrescentar factor' }));
    expect(screen.getByRole('menuitem', { name: 'Idade do condutor' })).toHaveAttribute(
      'aria-disabled',
      'true',
    );
    expect(screen.getByRole('menuitem', { name: 'Localizador' })).toHaveAttribute(
      'aria-disabled',
      'true',
    );
    await userEvent.click(screen.getByRole('menuitem', { name: 'Província de circulação' }));
    expect(onAdd).toHaveBeenCalledWith({ code: 'PROVINCE', label: 'Província de circulação' });
  });

  it('BONUS_MALUS é recusado no factor personalizado', async () => {
    const onAdd = vi.fn();
    renderWithProviders(<FactorMenu existingCodes={['ZONA']} onAdd={onAdd} />);
    await userEvent.click(screen.getByRole('button', { name: 'Acrescentar factor' }));
    await userEvent.click(screen.getByRole('menuitem', { name: 'Factor personalizado' }));
    await userEvent.type(screen.getByLabelText('Código'), 'bonus_malus');
    await userEvent.type(screen.getByLabelText('Nome'), 'Bónus');
    await userEvent.click(screen.getByRole('button', { name: 'Acrescentar' }));
    expect(screen.getByText('Este código está reservado ao sistema.')).toBeInTheDocument();
    expect(onAdd).not.toHaveBeenCalled();

    await userEvent.clear(screen.getByLabelText('Código'));
    await userEvent.type(screen.getByLabelText('Código'), 'ZONA');
    await userEvent.click(screen.getByRole('button', { name: 'Acrescentar' }));
    expect(screen.getByText('Factor repetido.')).toBeInTheDocument();

    await userEvent.clear(screen.getByLabelText('Código'));
    await userEvent.type(screen.getByLabelText('Código'), 'ZONA_RISCO');
    await userEvent.click(screen.getByRole('button', { name: 'Acrescentar' }));
    expect(onAdd).toHaveBeenCalledWith({ code: 'ZONA_RISCO', label: 'Bónus' });
  });
});
