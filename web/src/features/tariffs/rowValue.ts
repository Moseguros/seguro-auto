import { formatMoney } from '../../format/money';
import { formatDecimal, rateToPercent } from './decimal';
import type { TariffRow, ValueKind } from './ratingTypes';

/**
 * Valor de uma linha como se mostra: `AMOUNT` → "4 500,00 MT"; taxa → "2,5 %"; factor →
 * "× 1,30". Sem valor (ou inválido) → `null`.
 */
export function rowValueText(
  value: TariffRow['value'],
  kind: ValueKind,
  language: string,
): string | null {
  if (value === null || value === undefined || value === '') return null;
  if (kind === 'AMOUNT') {
    return typeof value === 'number' ? formatMoney({ amount: value }, language) : null;
  }
  if (typeof value !== 'string') return null;
  if (kind === 'RATE') {
    const percent = rateToPercent(value, language);
    return percent === null ? null : `${percent} %`;
  }
  return `× ${formatDecimal(value, language)}`;
}
