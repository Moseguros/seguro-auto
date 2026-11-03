import Decimal from 'decimal.js';

/**
 * Taxas e factores em texto, sem `Number` (nunca há vírgula flutuante). O dinheiro usa
 * `formatMoney` e `parseMeticais` de `format/money.ts`.
 */

const written = /^\d+(?:[.,]\d+)?$/;

/** Formato dos decimais na tarifa (`rating-v1.json`): até 6 inteiros e 6 casas. */
export const tariffDecimalPattern = /^(0|[1-9][0-9]{0,5})(\.[0-9]{1,6})?$/;

/**
 * Texto escrito pelo utilizador (vírgula ou ponto decimal, sem milhares) → texto com ponto,
 * mantendo os zeros: `"1,30"` → `"1.30"`. Inválido → `null`.
 */
export function normalizeDecimal(text: string): string | null {
  const compact = text.trim();
  if (!written.test(compact)) return null;
  return compact.replace(',', '.');
}

/** Decimal em texto (`"1.30"`) → `Decimal`, ou `null` se não for um número. */
export function toDecimal(text: string | number | null | undefined): Decimal | null {
  if (text === null || text === undefined) return null;
  if (typeof text === 'number') return Number.isFinite(text) ? new Decimal(text) : null;
  const normalized = normalizeDecimal(text);
  return normalized === null ? null : new Decimal(normalized);
}

/** Percentagem escrita (`"2,5"`) → taxa decimal em texto (`"0.025"`). Inválida → `null`. */
export function percentToRate(text: string): string | null {
  const percent = toDecimal(text);
  return percent === null ? null : percent.div(100).toFixed();
}

/** Taxa decimal (`"0.0015"`) → percentagem com vírgula (`"0,15"`). Inválida → `null`. */
export function rateToPercent(rate: string, language = 'pt-MZ'): string | null {
  const value = toDecimal(rate);
  return value === null ? null : formatDecimal(value.times(100).toFixed(), language);
}

/** Decimal em texto com o separador da língua: `"1.30"` → `"1,30"` (pt) ou `"1.30"` (en). */
export function formatDecimal(text: string, language = 'pt-MZ'): string {
  return language === 'en' ? text : text.replace('.', ',');
}

/** `0 < valor ≤ max`, com `max` em texto (comparação exacta). */
export function isPositiveUpTo(value: Decimal, max: string | number): boolean {
  return value.greaterThan(0) && value.lessThanOrEqualTo(max);
}

/** Número de casas decimais de um decimal escrito (`"2,55"` → 2). */
export function decimalPlaces(text: string): number {
  const normalized = normalizeDecimal(text);
  if (normalized === null) return 0;
  const dot = normalized.indexOf('.');
  return dot < 0 ? 0 : normalized.length - dot - 1;
}

/** Centavos → meticais com `casas` casas decimais (`"35493826838"` → `"354938,2684"`). */
export function centsToMeticais(cents: string | number, places: number, language = 'pt-MZ') {
  const value = toDecimal(typeof cents === 'number' ? String(cents) : cents);
  if (value === null) return '';
  return formatDecimal(value.div(100).toFixed(places), language);
}
