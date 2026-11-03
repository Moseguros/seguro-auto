import { displayTimeZone } from '../i18n/format';

const formatter = new Intl.DateTimeFormat('en-CA', {
  timeZone: displayTimeZone,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
});

/** Data de hoje (`AAAA-MM-DD`) em `Africa/Maputo`. */
export function todayInMaputo(now: Date = new Date()): string {
  return formatter.format(now);
}

/** Ano actual em `Africa/Maputo` (pode já ser o seguinte quando em UTC ainda não o é). */
export function currentYearInMaputo(now: Date = new Date()): number {
  return Number(todayInMaputo(now).slice(0, 4));
}
