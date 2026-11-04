import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { ConditionSummary } from './ConditionSummary';

describe('ConditionSummary', () => {
  it('sem condições: "Sempre"', () => {
    renderWithProviders(<ConditionSummary when={{}} />);
    expect(screen.getByText('Sempre')).toBeInTheDocument();
  });

  it('os quatro formatos de intervalo', () => {
    renderWithProviders(
      <ConditionSummary
        when={{
          driverAgeYears: { gte: 25, lt: 70 },
          licenceYears: { lt: 2 },
          vehicleAgeYears: { gte: 10 },
          capital: { gte: 500000000, lt: 1000000000 },
        }}
      />,
    );
    expect(screen.getByText('Idade do condutor (anos): 25 a 69')).toBeInTheDocument();
    expect(screen.getByText('Antiguidade da carta (anos): menos de 2')).toBeInTheDocument();
    expect(screen.getByText('Idade do veículo (anos): 10 ou mais')).toBeInTheDocument();
    expect(
      screen.getByText(/^Capital: de 5\s000\s000,00 MT até menos de 10\s000\s000,00 MT$/),
    ).toBeInTheDocument();
  });

  it('conjuntos e localizador', () => {
    renderWithProviders(
      <ConditionSummary
        when={{ vehicleCategory: { in: ['LIGHT_PASSENGER', 'LIGHT_MIXED'] }, hasTracker: true }}
      />,
    );
    expect(
      screen.getByText('Categoria do veículo: Ligeiro de passageiros, Ligeiro misto'),
    ).toBeInTheDocument();
    expect(screen.getByText('Localizador: sim')).toBeInTheDocument();
  });
});
