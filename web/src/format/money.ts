import type { Money } from '../api/generated';
import i18n from '../i18n';

function numberLocale(): string {
  return i18n.language === 'en' ? 'en' : 'pt-MZ';
}

/** Só o número em meticais (`3 000 000,00`), sem a moeda. */
export function formatAmount(m: Pick<Money, 'amount'>): string {
  return new Intl.NumberFormat(numberLocale(), {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(m.amount / 100);
}

/** Valor em centavos mostrado em meticais: `{ amount: 300000000 }` → `3 000 000,00 MT`. */
export function formatMoney(m: Pick<Money, 'amount'>): string {
  return `${formatAmount(m)} MT`;
}

/** Centavos → `Money` em meticais. */
export function mzn(amount: number): Money {
  return { amount, currency: 'MZN' };
}

const meticais = /^(\d+)(?:,(\d{1,2}))?$/;

/**
 * Texto escrito pelo utilizador → centavos (inteiro), sem vírgula flutuante. Os espaços e os
 * pontos são separadores de milhares; a vírgula é o separador decimal (no máximo 2 casas).
 * Vazio → `null`; outra coisa → `'invalid'`.
 */
export function parseMeticais(text: string): number | null | 'invalid' {
  const compact = text.replace(/[\s\u00a0\u202f]/g, '').replace(/\./g, '');
  if (compact === '') return null;
  const match = meticais.exec(compact);
  if (!match) return 'invalid';
  const units = Number(match[1]);
  const cents = Number((match[2] ?? '').padEnd(2, '0'));
  const total = units * 100 + cents;
  return Number.isSafeInteger(total) ? total : 'invalid';
}
