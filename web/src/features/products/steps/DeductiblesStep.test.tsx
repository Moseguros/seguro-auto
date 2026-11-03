import { screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useState } from 'react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '../../../test/render';
import { newCoverage, type CoverageDraft } from '../editorModel';
import { DeductiblesStep } from './DeductiblesStep';

function Harness() {
  const [coverages, setCoverages] = useState<CoverageDraft[]>([newCoverage('OD_COLLISION')]);
  return (
    <DeductiblesStep
      coverages={coverages}
      onChange={setCoverages}
      readOnly={false}
      errors={{}}
      onValidityChange={() => {}}
    />
  );
}

describe('DeductiblesStep', () => {
  it('mostra só os campos do tipo de franquia escolhido', async () => {
    renderWithProviders(<Harness />);
    const row = screen.getByRole('region', { name: 'Choque, colisão e capotamento' });
    const fields = () =>
      ['Valor da franquia', 'Percentagem', 'Mínimo'].filter(
        (label) => within(row).queryByLabelText(label) !== null,
      );

    expect(within(row).getByLabelText('Franquia')).toHaveValue('NONE');
    expect(fields()).toEqual([]);

    await userEvent.selectOptions(within(row).getByLabelText('Franquia'), 'FIXED');
    expect(fields()).toEqual(['Valor da franquia']);

    await userEvent.selectOptions(within(row).getByLabelText('Franquia'), 'PERCENT');
    expect(fields()).toEqual(['Percentagem', 'Mínimo']);

    await userEvent.type(within(row).getByLabelText('Percentagem'), '10,123');
    expect(
      within(row).getByText('Indique de 0,01 a 100, com no máximo 2 casas decimais.'),
    ).toBeInTheDocument();

    await userEvent.selectOptions(within(row).getByLabelText('Franquia'), 'NONE');
    expect(fields()).toEqual([]);
  });
});
