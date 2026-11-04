import type { TFunction } from 'i18next';
import type {
  Money,
  QuoteDeductible,
  QuoteResponse,
  QuoteSummaryVehicle,
  QuotedVehicle,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import i18n from '../../i18n';

/** Textos partilhados pelos ecrãs das cotações. */

/** Percentagem na língua actual (`"2.5"` → `2,5`). */
function formatPercent(value: string | undefined): string {
  const number = Number(value);
  if (!Number.isFinite(number)) return value ?? '';
  return new Intl.NumberFormat(i18n.language === 'en' ? 'en' : 'pt-MZ', {
    maximumFractionDigits: 2,
  }).format(number);
}

/** Valor da franquia (`10 % (mín. 25 000,00 MT)`, `5 000,00 MT`), ou `null` sem franquia. */
export function deductibleValue(t: TFunction, deductible: QuoteDeductible | undefined) {
  if (!deductible || deductible.type === 'NONE') return null;
  if (deductible.type === 'FIXED') {
    return deductible.amount ? formatMoney(deductible.amount) : null;
  }
  const percent = formatPercent(deductible.percent);
  return deductible.minAmount
    ? t('quote.deductible.percentMin', { percent, min: formatMoney(deductible.minAmount) })
    : t('quote.deductible.percent', { percent });
}

/** "Franquia …" de uma cobertura, ou `null`. */
export function deductibleText(t: TFunction, deductible: QuoteDeductible | undefined) {
  const value = deductibleValue(t, deductible);
  return value ? t('quote.deductible.label', { value }) : null;
}

/** Soma dos encargos de uma cotação. */
export function chargesTotal(quote: QuoteResponse): Money {
  const amount = (quote.charges ?? []).reduce((sum, charge) => sum + charge.amount.amount, 0);
  return { amount, currency: 'MZN' };
}

/** Rótulo de um encargo na língua actual. */
export function chargeLabel(charge: { labelPt: string; labelEn: string }): string {
  return i18n.language === 'en' ? charge.labelEn : charge.labelPt;
}

/** "AAA-100-MC · Toyota Corolla" (o que houver). */
export function vehicleLabel(
  t: TFunction,
  vehicle: Pick<QuotedVehicle | QuoteSummaryVehicle, 'plate' | 'make' | 'model'> & {
    category?: string;
  },
): string {
  const name = [vehicle.make, vehicle.model].filter(Boolean).join(' ');
  const parts = [vehicle.plate, name].filter(Boolean);
  if (parts.length > 0) return parts.join(' · ');
  return vehicle.category ? t(`vehicleCategory.${vehicle.category}`) : t('quote.vehicle');
}

/** Iniciais da seguradora para o avatar ("Seguradora Demo" → "SD"). */
export function initials(name: string): string {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((word) => word[0]?.toUpperCase() ?? '')
    .join('');
}

/** "{n} propostas". */
export function offeredText(t: TFunction, count: number): string {
  return t('quote.offeredCount', { count });
}

/** Duração ("1 ano" ou "30 dias"). */
export function durationText(t: TFunction, days: number): string {
  return days === 365 ? t('quote.oneYear') : t('quote.days', { count: days });
}

/** Telemóvel `+258841234567` → `+258 84 123 4567`. */
export function formatMsisdn(value: string | null | undefined): string {
  if (!value) return '';
  const match = /^\+?258(\d{2})(\d{3})(\d{4})$/.exec(value);
  return match ? `+258 ${match[1]} ${match[2]} ${match[3]}` : value;
}
