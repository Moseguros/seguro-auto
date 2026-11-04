import { todayInMaputo } from '../../format/dates';
import { ONE_YEAR_DAYS, QUOTE_MAX_START_DAYS_AHEAD } from './config';

/** Datas `AAAA-MM-DD` (sem hora), contadas em UTC para não depender do fuso do browser. */

const datePattern = /^(\d{4})-(\d{2})-(\d{2})$/;

export function parseIsoDate(value: string): Date | null {
  const match = datePattern.exec(value);
  if (!match) return null;
  const [y, m, d] = [Number(match[1]), Number(match[2]), Number(match[3])];
  const date = new Date(Date.UTC(y, m - 1, d));
  if (date.getUTCFullYear() !== y || date.getUTCMonth() !== m - 1 || date.getUTCDate() !== d) {
    return null;
  }
  return date;
}

function toIso(date: Date): string {
  return date.toISOString().slice(0, 10);
}

export function addDays(value: string, days: number): string {
  const date = parseIsoDate(value);
  if (!date) return '';
  date.setUTCDate(date.getUTCDate() + days);
  return toIso(date);
}

/** Como `LocalDate.plusYears`: 29 de Fevereiro passa a 28 num ano comum. */
export function addYears(value: string, years: number): string {
  const date = parseIsoDate(value);
  if (!date) return '';
  const year = date.getUTCFullYear() + years;
  const month = date.getUTCMonth();
  const lastDay = new Date(Date.UTC(year, month + 1, 0)).getUTCDate();
  return toIso(new Date(Date.UTC(year, month, Math.min(date.getUTCDate(), lastDay))));
}

/**
 * Último dia (inclusivo) do seguro, com a regra do S3-02 §3.4: 1 ano → mais um ano menos um dia;
 * senão início + dias − 1.
 */
export function endDateOf(startDate: string, durationDays: number): string {
  if (!parseIsoDate(startDate)) return '';
  if (durationDays === ONE_YEAR_DAYS) return addDays(addYears(startDate, 1), -1);
  return addDays(startDate, durationDays - 1);
}

/** Primeira e última data de início aceites (hoje em `Africa/Maputo` até hoje + 60 dias). */
export function startDateRange(today: string = todayInMaputo()): { min: string; max: string } {
  return { min: today, max: addDays(today, QUOTE_MAX_START_DAYS_AHEAD) };
}

/** Anos completos entre duas datas (idade). */
export function yearsBetween(from: string, to: string): number | null {
  const a = parseIsoDate(from);
  const b = parseIsoDate(to);
  if (!a || !b) return null;
  let years = b.getUTCFullYear() - a.getUTCFullYear();
  if (
    b.getUTCMonth() < a.getUTCMonth() ||
    (b.getUTCMonth() === a.getUTCMonth() && b.getUTCDate() < a.getUTCDate())
  ) {
    years -= 1;
  }
  return years;
}
