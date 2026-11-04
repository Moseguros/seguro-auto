import { describe, expect, it } from 'vitest';
import { locate } from './pointer';

describe('locate', () => {
  it('duração', () => {
    expect(locate('/durationFactors/2/factor')).toEqual({ kind: 'duration', index: 2 });
    expect(locate('/durationFactors/0')).toEqual({ kind: 'duration', index: 0 });
  });

  it('coberturas e topo', () => {
    expect(locate('/coverages')).toEqual({ kind: 'top' });
    expect(locate('/coverages/1')).toEqual({ kind: 'coverage', coverage: 1 });
    expect(locate('/coverages/1/coverageCode')).toEqual({ kind: 'coverage', coverage: 1 });
  });

  it('base e linha da base', () => {
    expect(locate('/coverages/0/base/type')).toEqual({ kind: 'base', coverage: 0 });
    expect(locate('/coverages/0/base/rows')).toEqual({ kind: 'base', coverage: 0 });
    expect(locate('/coverages/0/base/rows/3/value')).toEqual({
      kind: 'base',
      coverage: 0,
      row: 3,
    });
    expect(locate('/coverages/2/base/rows/0/when/driverAgeYears')).toEqual({
      kind: 'base',
      coverage: 2,
      row: 0,
    });
  });

  it('factor e linha do factor', () => {
    expect(locate('/coverages/0/factors/1/code')).toEqual({
      kind: 'factor',
      coverage: 0,
      factor: 1,
    });
    expect(locate('/coverages/0/factors/1/rows/2/when/driverAgeYears')).toEqual({
      kind: 'factor',
      coverage: 0,
      factor: 1,
      row: 2,
    });
  });

  it('ponteiro que não corresponde a nada', () => {
    expect(locate('')).toBeNull();
    expect(locate('coverages[0]')).toBeNull();
    expect(locate('/schemaVersion')).toBeNull();
    expect(locate('/coverages/x')).toBeNull();
    expect(locate('/durationFactors')).toBeNull();
  });
});
