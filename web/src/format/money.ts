import type { Money } from '../api/generated';
import i18n from '../i18n';

function numberLocale(language: string): string {
  return language === 'en' ? 'en' : 'pt-MZ';
}

/** Só o número em meticais (`3 000 000,00`), sem a moeda. */
export function formatAmount(m: Pick<Money, 'amount'>, language: string = i18n.language): string {
  return new Intl.NumberFormat(numberLocale(language), {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(m.amount / 100);
}

/**
 * Valor em centavos mostrado em meticais: `{ amount: 300000000 }` → `3 000 000,00 MT` (pt) ou
 * `3,000,000.00 MT` (en).
 */
export function formatMoney(m: Pick<Money, 'amount'>, language: string = i18n.language): string {
  return `${formatAmount(m, language)} MT`;
}

/** Centavos → `Money` em meticais. */
export function mzn(amount: number): Money {
  return { amount, currency: 'MZN' };
}

const meticais = /^(\d+)(?:,(\d{1,2}))?$/;

/**
 * Texto escrito pelo utilizador → centavos (inteiro), sem vírgula flutuante. Os espaços
 * (incluindo os não separáveis que o `Intl` usa nos milhares) e os pontos são separadores de
 * milhares; a vírgula é o separador decimal (no máximo 2 casas). Vazio → `null`; outra coisa →
 * `'invalid'`.
 */
export function parseMeticais(text: string): number | null | 'invalid' {
  // `\s` inclui os espaços não separáveis (U+00A0, U+202F) que o `Intl` usa nos milhares.
  const compact = text.replace(/\s/g, '').replace(/\./g, '');
  if (compact === '') return null;
  const match = meticais.exec(compact);
  if (!match) return 'invalid';
  const units = Number(match[1]);
  const cents = Number((match[2] ?? '').padEnd(2, '0'));
  const total = units * 100 + cents;
  return Number.isSafeInteger(total) ? total : 'invalid';
}
