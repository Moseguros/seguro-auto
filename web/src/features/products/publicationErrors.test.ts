import { describe, expect, it } from 'vitest';
import { groupByStep, stepForField } from './publicationErrors';

describe('stepForField', () => {
  // Os `field` das regras de publicação (S2-04 §4) e da gravação das coberturas (S2-04 §3).
  it.each([
    ['coverages', 'MTPL_REQUIRED', 'coberturas'],
    ['coverages', 'BORDER_ONLY_MTPL', 'coberturas'],
    ['coverages', 'OD_REQUIRED_FOR_COMPREHENSIVE', 'coberturas'],
    ['coverages[0].mandatory', 'MTPL_MUST_BE_MANDATORY', 'coberturas'],
    ['coverages[0].capital', 'MTPL_CAPITAL_BELOW_MINIMUM', 'coberturas'],
    ['coverages[0].capitalType', 'INVALID_CAPITAL_TYPE', 'coberturas'],
    ['coverages[2].code', 'OD_NOT_ALLOWED_FOR_TYPE', 'coberturas'],
    ['coverages[2].capitalType', 'OD_CAPITAL_MUST_BE_VEHICLE_VALUE', 'coberturas'],
    ['coverages[4].code', 'OD_THEFT_REQUIRES_OD_COVERAGES', 'coberturas'],
    ['coverages[3].requiresCodes', 'REQUIRED_COVERAGE_MISSING', 'coberturas'],
    ['coverages[1].capital', 'VTPL_CAPITAL_TOO_LOW', 'coberturas'],
    ['coverages[1].capitalOptions', 'VTPL_CAPITAL_TOO_LOW', 'coberturas'],
    ['coverages[5].territorialScope', 'CROSS_BORDER_SCOPE_REQUIRED', 'coberturas'],
    ['coverages[0].deductibleAmount', 'REQUIRED', 'franquias'],
    ['coverages[0].deductiblePercent', 'OUT_OF_RANGE', 'franquias'],
    ['coverages[10].deductibleMinAmount', 'NOT_ALLOWED', 'franquias'],
    ['documents.GENERAL_CONDITIONS', 'DOCUMENT_REQUIRED', 'documentos'],
    ['documents.PRE_CONTRACT_INFO', 'DOCUMENT_REQUIRED', 'documentos'],
    ['vehicleCategories', 'REQUIRED', 'dados'],
    ['usages', 'REQUIRED', 'dados'],
  ])('%s (%s) → %s', (field, _code, step) => {
    expect(stepForField(field)).toBe(step);
  });
});

describe('groupByStep', () => {
  it('agrupa pela ordem dos passos', () => {
    const groups = groupByStep([
      { field: 'documents.GENERAL_CONDITIONS', code: 'DOCUMENT_REQUIRED' },
      { field: 'coverages', code: 'MTPL_REQUIRED' },
      { field: 'usages', code: 'REQUIRED' },
      { field: 'documents.PRE_CONTRACT_INFO', code: 'DOCUMENT_REQUIRED' },
      { field: 'vehicleCategories', code: 'REQUIRED' },
    ]);
    expect(groups.map(([step, list]) => [step, list.length])).toEqual([
      ['dados', 2],
      ['coberturas', 1],
      ['documentos', 2],
    ]);
  });
});
