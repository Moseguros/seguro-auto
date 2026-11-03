/** As datas chegam em UTC (ISO 8601) e mostram-se na hora de Maputo. */
export const displayTimeZone = 'Africa/Maputo';

function locale(language: string): string {
  return language === 'en' ? 'en-GB' : 'pt-PT';
}

/** Data e hora (`02/11/2026, 14:05`), ou `''` sem valor. */
export function formatDateTime(value: string | undefined, language: string): string {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return new Intl.DateTimeFormat(locale(language), {
    timeZone: displayTimeZone,
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(date);
}

/** Só a data (`02/11/2026`), ou `''` sem valor. */
export function formatDate(value: string | undefined, language: string): string {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return new Intl.DateTimeFormat(locale(language), {
    timeZone: displayTimeZone,
    dateStyle: 'short',
  }).format(date);
}
