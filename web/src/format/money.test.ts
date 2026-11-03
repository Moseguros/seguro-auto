import { describe, expect, it } from 'vitest';
import i18n from '../i18n';
import { formatMoney, parseMeticais } from './money';

/** O `Intl` usa espaços não separáveis nos milhares: compara-se com espaços normais. */
const plain = (text: string) => text.replace(/\s/g, ' ');

describe('parseMeticais', () => {
  it.each([
    ['3 000 000', 300000000],
    ['3.000.000,50', 300000050],
    ['3000000,5', 300000050],
    ['0,01', 1],
    ['1,234', 'invalid'],
    ['abc', 'invalid'],
    ['', null],
  ] as const)('%j → %j', (text, expected) => {
    expect(parseMeticais(text)).toBe(expected);
  });

  it('aceita os espaços não separáveis que o próprio formato escreve', () => {
    expect(parseMeticais('3 000 000,00')).toBe(300000000);
    expect(parseMeticais('-5')).toBe('invalid');
    expect(parseMeticais('12,')).toBe('invalid');
  });
});

describe('formatMoney', () => {
  it('em pt usa o espaço nos milhares, a vírgula nas casas decimais e "MT"', async () => {
    await i18n.changeLanguage('pt-MZ');
    expect(plain(formatMoney({ amount: 300000000 }))).toBe('3 000 000,00 MT');
    expect(plain(formatMoney({ amount: 1 }))).toBe('0,01 MT');
  });

  it('em en usa a vírgula nos milhares e o ponto nas casas decimais', async () => {
    await i18n.changeLanguage('en');
    expect(formatMoney({ amount: 300000050 })).toBe('3,000,000.50 MT');
  });
});
