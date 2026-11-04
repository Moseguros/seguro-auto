import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import '../i18n';
import { DemoBanner } from './DemoBanner';

describe('DemoBanner', () => {
  it('mostra a faixa quando está ligada', () => {
    render(<DemoBanner enabled />);
    expect(screen.getByRole('note')).toHaveTextContent('DADOS DE DEMONSTRAÇÃO');
  });

  it('não mostra nada quando está desligada', () => {
    render(<DemoBanner enabled={false} />);
    expect(screen.queryByTestId('demo-banner')).not.toBeInTheDocument();
  });
});
