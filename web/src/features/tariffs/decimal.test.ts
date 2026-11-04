import { describe, expect, it } from 'vitest';
import { centsToMeticais, normalizeDecimal, percentToRate, rateToPercent } from './decimal';

describe('decimal', () => {
  it('percentagem → taxa', () => {
    expect(percentToRate('2,5')).toBe('0.025');
    expect(percentToRate('0,15')).toBe('0.0015');
    expect(percentToRate('2.5')).toBe('0.025');
    expect(percentToRate('100')).toBe('1');
    expect(percentToRate('abc')).toBeNull();
  });

  it('sem arredondamentos de vírgula flutuante', () => {
    expect(percentToRate('0,1')).toBe('0.001');
    expect(percentToRate('0,3')).toBe('0.003');
    expect(percentToRate('0,0001')).toBe('0.000001');
  });

  it('taxa → percentagem com vírgula', () => {
    expect(rateToPercent('0.025')).toBe('2,5');
    expect(rateToPercent('0.0015')).toBe('0,15');
    expect(rateToPercent('0.030')).toBe('3');
    expect(rateToPercent('0.025', 'en')).toBe('2.5');
  });

  it('normaliza decimais mantendo os zeros', () => {
    expect(normalizeDecimal('1,30')).toBe('1.30');
    expect(normalizeDecimal(' 0.90 ')).toBe('0.90');
    expect(normalizeDecimal('1,3,0')).toBeNull();
    expect(normalizeDecimal('')).toBeNull();
  });

  it('centavos exactos em meticais com 4 casas', () => {
    expect(centsToMeticais('3549382.683750000', 4)).toBe('35493,8268');
    expect(centsToMeticais(450000, 2)).toBe('4500,00');
  });
});
