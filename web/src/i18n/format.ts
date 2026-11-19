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

function maputoParts(value: string | undefined): Record<string, string> | undefined {
  if (!value) return undefined;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return undefined;
  const parts = new Intl.DateTimeFormat('en-GB', {
    timeZone: displayTimeZone,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hourCycle: 'h23',
  }).formatToParts(date);
  return Object.fromEntries(parts.map((p) => [p.type, p.value]));
}

/** `dd/MM/yyyy HH:mm` em Maputo (S5-11), igual nas duas línguas, ou `''` sem valor. */
export function formatDateTimeNumeric(value: string | undefined): string {
  const p = maputoParts(value);
  return p ? `${p.day}/${p.month}/${p.year} ${p.hour}:${p.minute}` : '';
}

/** Só a hora (`HH:mm`) em Maputo, ou `''` sem valor. */
export function formatTime(value: string | undefined): string {
  const p = maputoParts(value);
  return p ? `${p.hour}:${p.minute}` : '';
}
