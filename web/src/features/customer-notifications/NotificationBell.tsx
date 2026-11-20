import NotificationsIcon from '@mui/icons-material/Notifications';
import Badge from '@mui/material/Badge';
import IconButton from '@mui/material/IconButton';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { countMyUnreadNotifications } from '../../api/generated';
import { UNREAD_COUNT_REFRESH_MS, unreadCountKey } from './notificationQueries';

export const notificationsPath = '/cliente/notificacoes';

/**
 * Sino da barra superior da área do cliente (S6-06 §4): a contagem das notificações por ler,
 * lida de minuto a minuto e ao voltar ao separador; abre o centro de notificações.
 */
export function NotificationBell() {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey: unreadCountKey,
    queryFn: ({ signal }) => countMyUnreadNotifications(undefined, signal),
    refetchInterval: UNREAD_COUNT_REFRESH_MS,
    refetchIntervalInBackground: false,
    refetchOnWindowFocus: true,
    retry: false,
  });
  const count = query.data?.count ?? 0;
  return (
    <IconButton
      color="inherit"
      component={RouterLink}
      to={notificationsPath}
      aria-label={t('notifications.bell', { count })}
      data-testid="notification-bell"
    >
      {/* Vermelho: o laranja da marca fica só para as acções de conversão. */}
      <Badge badgeContent={count} max={99} color="error" data-testid="notification-badge">
        <NotificationsIcon />
      </Badge>
    </IconButton>
  );
}
