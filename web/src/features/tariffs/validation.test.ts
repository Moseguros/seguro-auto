import { describe, expect, it } from 'vitest';
import type { RatingCoverage } from '../../api/generated';
import tariffDp from './fixtures/tariff-dp.json';
import tariffTemp from './fixtures/tariff-temp.json';
import { initialDraft } from './initialDraft';
import type { Rating } from './ratingTypes';
import { validateRating } from './validation';

const mtpl: RatingCoverage = {
  code: 'MTPL',
  mandatory: true,
  capitalType: 'FIXED',
  capital: { amount: 300000000, currency: 'MZN' },
};
const autoCoverages: RatingCoverage[] = [
  mtpl,
  {
    code: 'VTPL',
    mandatory: false,
    capitalType: 'OPTIONS',
    capitalOptions: [
      { amount: 500000000, currency: 'MZN' },
      { amount: 1000000000, currency: 'MZN' },
    ],
  },
  ...(['OD_COLLISION', 'OD_FIRE', 'OD_NATURE', 'OD_THEFT'] as const).map(
    (code): RatingCoverage => ({ code, mandatory: false, capitalType: 'VEHICLE_VALUE' }),
  ),
];

const clone = <T>(value: T): T => structuredClone(value);

describe('validateRating', () => {
  it('as tarifas de referência são válidas', () => {
    expect(
      validateRating(tariffDp as unknown as Rating, {
        coverages: autoCoverages,
        productType: 'COMPREHENSIVE',
      }),
    ).toEqual([]);
    expect(
      validateRating(tariffTemp as unknown as Rating, {
        coverages: [mtpl],
        productType: 'TEMPORARY',
      }),
    ).toEqual([]);
  });

  it('o rascunho inicial é inválido até ter valores', () => {
    const draft = initialDraft({ coverages: [mtpl], productType: 'TEMPORARY' });
    expect(draft.durationFactors).toEqual([{ days: 365, factor: '1' }]);
    expect(draft.coverages[0]!.base.type).toBe('AMOUNT');
    expect(validateRating(draft, { coverages: [mtpl], productType: 'TEMPORARY' })).toEqual([
      { field: '/coverages/0/base/rows/0/value', code: 'REQUIRED' },
    ]);
  });

  it('erros com os ponteiros do servidor', () => {
    const rating = clone(tariffDp as unknown as Rating);
    rating.coverages[0]!.factors![0]!.rows[1]!.when.driverAgeYears = { gte: 70, lt: 25 };
    rating.coverages[0]!.factors![1]!.code = 'DRIVER_AGE';
    rating.coverages[0]!.factors![2]!.code = 'BONUS_MALUS';
    rating.coverages[2]!.base.rows[0]!.value = '1.5';
    rating.coverages[0]!.base.type = 'RATE_OF_VEHICLE_VALUE';
    rating.coverages[0]!.base.rows = rating.coverages[0]!.base.rows.map((r) => ({
      ...r,
      value: '0.01',
    }));
    rating.coverages.pop();
    rating.durationFactors = [{ days: 30, factor: '0.2' }];
    expect(
      validateRating(rating, { coverages: autoCoverages, productType: 'COMPREHENSIVE' }),
    ).toEqual([
      { field: '/coverages', code: 'COVERAGE_TARIFF_MISSING' },
      { field: '/coverages/0/base/type', code: 'BASE_TYPE_NOT_ALLOWED' },
      { field: '/coverages/0/factors/0/rows/1/when/driverAgeYears', code: 'INVALID_RANGE' },
      { field: '/coverages/0/factors/1/code', code: 'DUPLICATE_FACTOR' },
      { field: '/coverages/0/factors/2/code', code: 'RESERVED_FACTOR_CODE' },
      { field: '/coverages/2/base/rows/0/value', code: 'VALUE_OUT_OF_RANGE' },
      { field: '/durationFactors/0/days', code: 'DURATION_NOT_ALLOWED' },
    ]);
  });

  it('duração: obrigatória nos temporários e 365 dias = 1', () => {
    const rating = clone(tariffTemp as unknown as Rating);
    rating.durationFactors = [
      { days: 365, factor: '0.9' },
      { days: 365, factor: '1' },
    ];
    expect(validateRating(rating, { coverages: [mtpl], productType: 'TEMPORARY' })).toEqual([
      { field: '/durationFactors/0/factor', code: 'DURATION_365_NOT_ONE' },
      { field: '/durationFactors/1/days', code: 'DUPLICATE_DURATION' },
    ]);
    rating.durationFactors = [];
    expect(validateRating(rating, { coverages: [mtpl], productType: 'TEMPORARY' })).toEqual([
      { field: '/durationFactors', code: 'DURATION_FACTORS_REQUIRED' },
    ]);
  });
});
