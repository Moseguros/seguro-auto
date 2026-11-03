import type { Money } from '../api/generated';
import i18n from '../i18n';

/** `3 000 000,00 MT` (pt) ou `3,000,000.00 MT` (en); o valor chega em centavos. */
export function formatMoney(
  money: Pick<Money, 'amount'>,
  language: string = i18n.language,
): string {
  const formatter = new Intl.NumberFormat(language === 'en' ? 'en' : 'pt-MZ', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
  return `${formatter.format(money.amount / 100)} MT`;
}

const moneyPattern = /^(\d+)(?:,(\d{1,2}))?$/;

/**
 * Texto em meticais → centavos (inteiro), sem aritmética de vírgula flutuante. Ignora espaços
 * (incluindo os não separáveis) e pontos de milhares; a vírgula separa as casas decimais (no
 * máximo 2). Vazio → `null`; outra coisa → `'invalid'`.
 */
export function parseMeticais(text: string): number | null | 'invalid' {
  // `\s` inclui os espaços não separáveis (U+00A0, U+202F) que o `Intl` usa nos milhares.
  const cleaned = text.replace(/\s/g, '').replace(/\./g, '');
  if (cleaned === '') return null;
  const match = moneyPattern.exec(cleaned);
  if (!match) return 'invalid';
  const units = Number(match[1]);
  const cents = Number((match[2] ?? '').padEnd(2, '0'));
  const amount = units * 100 + cents;
  return Number.isSafeInteger(amount) ? amount : 'invalid';
}
