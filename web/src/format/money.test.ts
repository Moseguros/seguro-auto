import { describe, expect, it } from 'vitest';
import { formatMoney, parseMeticais } from './money';

describe('parseMeticais', () => {
  it.each([
    ['3 000 000', 300000000],
    ['3.000.000,50', 300000050],
    ['3000000,5', 300000050],
    ['0,01', 1],
    [`3${String.fromCharCode(0x202f)}000${String.fromCharCode(0xa0)}000`, 300000000],
    ['1,234', 'invalid'],
    ['abc', 'invalid'],
    ['-5', 'invalid'],
    ['', null],
    ['  ', null],
  ] as const)('"%s" → %s', (text, expected) => {
    expect(parseMeticais(text)).toBe(expected);
  });
});

describe('formatMoney', () => {
  const normalize = (text: string) => text.replace(/\s/g, ' ');

  it('pt: espaço nos milhares e vírgula decimal', () => {
    expect(normalize(formatMoney({ amount: 300000000 }, 'pt-MZ'))).toBe('3 000 000,00 MT');
    expect(normalize(formatMoney({ amount: 85000050 }, 'pt-MZ'))).toBe('850 000,50 MT');
  });

  it('en: vírgula nos milhares e ponto decimal', () => {
    expect(formatMoney({ amount: 300000000 }, 'en')).toBe('3,000,000.00 MT');
  });
});
