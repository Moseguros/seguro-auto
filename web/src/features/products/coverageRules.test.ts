import { describe, expect, it } from 'vitest';
import {
  addableCoverages,
  capitalTypesFor,
  coverageCodes,
  lockedFields,
  type CoverageCode,
} from './coverageRules';
import { coverageRequest, newCoverage } from './editorModel';

const ownDamage: CoverageCode[] = ['OD_COLLISION', 'OD_GLASS', 'OD_FIRE', 'OD_NATURE', 'OD_THEFT'];

describe('menu "Acrescentar cobertura"', () => {
  it('BORDER: só MTPL', () => {
    expect(addableCoverages('BORDER', [])).toEqual(['MTPL']);
    expect(addableCoverages('BORDER', ['MTPL'])).toEqual([]);
  });

  it.each(['MTPL_ONLY', 'TEMPORARY'] as const)('%s: todas menos OD_*', (type) => {
    expect(addableCoverages(type, [])).toEqual(
      coverageCodes.filter((code) => !ownDamage.includes(code)),
    );
  });

  it.each(['COMPREHENSIVE', 'FLEET'] as const)('%s: todas', (type) => {
    expect(addableCoverages(type, [])).toEqual(coverageCodes);
  });

  it('não repete as que já estão na lista', () => {
    expect(addableCoverages('COMPREHENSIVE', ['MTPL', 'OD_FIRE'])).not.toContain('MTPL');
    expect(addableCoverages('COMPREHENSIVE', ['MTPL', 'OD_FIRE'])).not.toContain('OD_FIRE');
  });
});

describe('ajudas das regras', () => {
  it('MTPL: obrigatória e capital FIXED bloqueados', () => {
    expect(lockedFields('MTPL')).toEqual({ mandatory: true, capitalType: true });
    expect(capitalTypesFor('MTPL')).toEqual(['FIXED']);
    const mtpl = newCoverage('MTPL');
    expect(mtpl.mandatory).toBe(true);
    expect(mtpl.capitalType).toBe('FIXED');
  });

  it.each(ownDamage)('%s: capital VEHICLE_VALUE bloqueado', (code) => {
    expect(lockedFields(code)).toEqual({ mandatory: false, capitalType: true });
    expect(capitalTypesFor(code)).toEqual(['VEHICLE_VALUE']);
    expect(newCoverage(code).capitalType).toBe('VEHICLE_VALUE');
  });

  it('VTPL: só FIXED ou OPTIONS', () => {
    expect(capitalTypesFor('VTPL')).toEqual(['FIXED', 'OPTIONS']);
    expect(lockedFields('VTPL')).toEqual({ mandatory: false, capitalType: false });
  });

  it('OD_THEFT preenche as coberturas exigidas', () => {
    expect(newCoverage('OD_THEFT').requiresCodes).toEqual(['OD_COLLISION', 'OD_FIRE', 'OD_NATURE']);
    expect(newCoverage('OD_FIRE').requiresCodes).toEqual([]);
  });

  it('o pedido só leva os campos do tipo de capital e de franquia', () => {
    const request = coverageRequest({
      ...newCoverage('OD_COLLISION'),
      capital: { amount: 5, currency: 'MZN' },
      deductibleType: 'PERCENT',
      deductiblePercent: '12,5',
      deductibleAmount: { amount: 7, currency: 'MZN' },
    });
    expect(request).toMatchObject({
      code: 'OD_COLLISION',
      capitalType: 'VEHICLE_VALUE',
      deductibleType: 'PERCENT',
      deductiblePercent: 12.5,
      territorialScope: ['MZ'],
    });
    expect(request.capital).toBeUndefined();
    expect(request.deductibleAmount).toBeUndefined();
  });
});
