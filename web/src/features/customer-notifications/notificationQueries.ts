import {
  getCountMyUnreadNotificationsQueryKey,
  getListMyNotificationsQueryKey,
  type PageResponseMyNotificationResponse,
} from '../../api/generated';
import { formatDate } from '../../i18n/format';

export const NOTIFICATIONS_PAGE_SIZE = 20;
export const UNREAD_COUNT_REFRESH_MS = 60_000;

/** Prefixo das chaves da lista (todas as variantes: todas, por ler, páginas). */
export const notificationsListKey = getListMyNotificationsQueryKey();
export const unreadCountKey = getCountMyUnreadNotificationsQueryKey();

/** A página seguinte, ou `undefined` na última. */
export function nextNotificationsPage(
  last: PageResponseMyNotificationResponse,
): number | undefined {
  return (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined;
}

/** Só se navega para caminhos da área do cliente (`/cliente/…`). */
export function customerWebPath(webPath: string | undefined): string | undefined {
  return webPath && webPath.startsWith('/cliente/') ? webPath : undefined;
}

const WEEK_MS = 7 * 24 * 60 * 60 * 1000;

/**
 * Data relativa ("há 5 min", `Intl.RelativeTimeFormat`); mais de 7 dias → `formatDate`.
 */
export function relativeTime(value: string, language: string, now: number = Date.now()): string {
  const time = new Date(value).getTime();
  if (Number.isNaN(time)) return '';
  const diff = time - now;
  if (Math.abs(diff) > WEEK_MS) return formatDate(value, language);
  const rtf = new Intl.RelativeTimeFormat(language === 'en' ? 'en-GB' : 'pt-PT', {
    numeric: 'auto',
    style: 'short',
  });
  const seconds = Math.round(diff / 1000);
  if (Math.abs(seconds) < 60) return rtf.format(0, 'second');
  const minutes = Math.round(seconds / 60);
  if (Math.abs(minutes) < 60) return rtf.format(minutes, 'minute');
  const hours = Math.round(minutes / 60);
  if (Math.abs(hours) < 24) return rtf.format(hours, 'hour');
  return rtf.format(Math.round(hours / 24), 'day');
}
