import { displayTimeZone } from '../../i18n/format';
import { todayInMaputo } from '../../format/dates';

const parts = new Intl.DateTimeFormat('en-GB', {
  timeZone: displayTimeZone,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  hourCycle: 'h23',
});

/** Data e hora em `Africa/Maputo` como `dd/MM/yyyy HH:mm`, ou `''` sem valor. */
export function formatStamp(value: string | undefined | null): string {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  const get = (type: string) => parts.formatToParts(date).find((p) => p.type === type)?.value;
  return `${get('day')}/${get('month')}/${get('year')} ${get('hour')}:${get('minute')}`;
}

/** Data (`yyyy-MM-dd`) de há `days` dias em `Africa/Maputo`. */
export function daysAgoInMaputo(days: number, now: Date = new Date()): string {
  return todayInMaputo(new Date(now.getTime() - days * 24 * 60 * 60 * 1000));
}
