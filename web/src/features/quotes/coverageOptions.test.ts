import { describe, expect, it } from 'vitest';
import { catalogProduct, catalogProductB, mtplProduct } from '../../test/quotes';
import { buildCoverageOptions, defaultCoverages, toggleCoverage } from './coverageOptions';

describe('coverageOptions', () => {
  const options = buildCoverageOptions(
    [catalogProduct(), catalogProductB, mtplProduct],
    'COMPREHENSIVE',
  );

  it('só conta os produtos do tipo escolhido', () => {
    expect(options.total).toBe(2);
    expect(buildCoverageOptions([catalogProduct(), mtplProduct], 'MTPL_ONLY').total).toBe(1);
  });

  it('junta as obrigatórias e a união das opcionais com "disponível em"', () => {
    expect(options.mandatory.map((o) => o.code)).toEqual(['MTPL']);
    expect(options.optional.map((o) => [o.code, o.availableIn])).toEqual([
      ['VTPL', 2],
      ['OD_COLLISION', 2],
      ['OD_FIRE', 2],
      ['OD_NATURE', 2],
      ['OD_THEFT', 1],
      ['OCCUPANTS', 1],
    ]);
  });

  it('junta os capitais de OPTIONS por ordem crescente, sem repetidos', () => {
    const vtpl = options.optional.find((o) => o.code === 'VTPL');
    expect(vtpl?.capitalOptions.map((m) => m.amount)).toEqual([250000000, 500000000, 1000000000]);
  });

  it('por defeito em COMPREHENSIVE: choque, incêndio e natureza; com o menor capital', () => {
    expect(defaultCoverages('COMPREHENSIVE', options.optional).map((c) => c.code)).toEqual([
      'OD_COLLISION',
      'OD_FIRE',
      'OD_NATURE',
    ]);
    expect(defaultCoverages('MTPL_ONLY', options.optional)).toEqual([]);
    const vtpl = options.optional.find((o) => o.code === 'VTPL')!;
    expect(toggleCoverage([], vtpl, true, options.optional).selected).toEqual([
      { code: 'VTPL', capital: { amount: 250000000, currency: 'MZN' } },
    ]);
  });

  it('furto marca as três exigidas; desmarcar uma delas desmarca o furto', () => {
    const theft = options.optional.find((o) => o.code === 'OD_THEFT')!;
    const fire = options.optional.find((o) => o.code === 'OD_FIRE')!;
    const checked = toggleCoverage([], theft, true, options.optional);
    expect(checked.theftNotice).toBe(true);
    expect(checked.selected.map((c) => c.code).sort()).toEqual(
      ['OD_COLLISION', 'OD_FIRE', 'OD_NATURE', 'OD_THEFT'].sort(),
    );
    const unchecked = toggleCoverage(checked.selected, fire, false, options.optional);
    expect(unchecked.selected.map((c) => c.code).sort()).toEqual(['OD_COLLISION', 'OD_NATURE']);
  });
});
