import { screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { canCompare, CompareBar, toggleSelection } from './CompareBar';

describe('CompareBar', () => {
  it('não aparece sem selecção', () => {
    renderWithProviders(<CompareBar count={0} onCompare={vi.fn()} />);
    expect(screen.queryByRole('button', { name: 'Comparar propostas' })).not.toBeInTheDocument();
  });

  it('"Comparar propostas" só activo com 2 ou 3', () => {
    const { rerender } = renderWithProviders(<CompareBar count={1} onCompare={vi.fn()} />);
    expect(screen.getByText('1 seleccionadas (máximo 3)')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Comparar propostas' })).toBeDisabled();
    for (const count of [2, 3]) {
      rerender(<CompareBar count={count} onCompare={vi.fn()} />);
      expect(screen.getByRole('button', { name: 'Comparar propostas' })).toBeEnabled();
    }
    expect(canCompare(4)).toBe(false);
  });

  it('a quarta caixa é recusada', () => {
    const three = ['a', 'b', 'c'];
    expect(toggleSelection(three, 'd', true)).toEqual({ selected: three, refused: true });
    expect(toggleSelection(['a', 'b'], 'c', true)).toEqual({ selected: three, refused: false });
    expect(toggleSelection(three, 'b', false)).toEqual({ selected: ['a', 'c'], refused: false });
  });
});
