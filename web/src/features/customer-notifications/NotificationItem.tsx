import Assignment from '@mui/icons-material/Assignment';
import Calculate from '@mui/icons-material/Calculate';
import Description from '@mui/icons-material/Description';
import Gavel from '@mui/icons-material/Gavel';
import Notifications from '@mui/icons-material/Notifications';
import Payments from '@mui/icons-material/Payments';
import type { SvgIconComponent } from '@mui/icons-material';
import Box from '@mui/material/Box';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { markMyNotificationRead, type MyNotificationResponse } from '../../api/generated';
import {
  customerWebPath,
  notificationsListKey,
  relativeTime,
  unreadCountKey,
} from './notificationQueries';

const categoryIcons: Record<string, SvgIconComponent> = {
  PAYMENT: Payments,
  POLICY: Description,
  PROPOSAL: Assignment,
  LEGAL: Gavel,
  QUOTE: Calculate,
};

/** Ícone da categoria; as categorias dos sprints seguintes usam o sino. */
export function categoryIcon(category: string): SvgIconComponent {
  return categoryIcons[category] ?? Notifications;
}

/**
 * Uma notificação do centro (S6-06 §4). Clicar marca-a como lida (sem esperar pela resposta) e
 * abre o `webPath` quando é da área do cliente; senão fica na lista.
 */
export function NotificationItem({ notification }: { notification: MyNotificationResponse }) {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const unread = !notification.readAt;
  const Icon = categoryIcon(notification.category);

  function open() {
    if (unread) {
      void markMyNotificationRead(notification.id)
        .catch(() => undefined)
        .finally(() => {
          void queryClient.invalidateQueries({ queryKey: unreadCountKey });
          void queryClient.invalidateQueries({ queryKey: notificationsListKey });
        });
    }
    const target = customerWebPath(notification.webPath);
    if (target) navigate(target);
  }

  return (
    <ListItemButton
      onClick={open}
      alignItems="flex-start"
      data-testid="notification-item"
      data-unread={unread ? 'true' : 'false'}
      sx={{
        borderRadius: 1,
        gap: 1,
        bgcolor: unread ? 'action.hover' : undefined,
      }}
    >
      <ListItemIcon sx={{ minWidth: 36, mt: 0.5 }}>
        <Icon color={unread ? 'primary' : 'action'} />
      </ListItemIcon>
      <Stack spacing={0.25} sx={{ flex: 1, minWidth: 0 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="baseline" gap={1}>
          <Typography fontWeight={unread ? 700 : 500} sx={{ wordBreak: 'break-word', minWidth: 0 }}>
            {notification.title}
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ whiteSpace: 'nowrap' }}>
            {relativeTime(notification.createdAt, i18n.language)}
          </Typography>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ wordBreak: 'break-word' }}>
          {notification.body}
        </Typography>
      </Stack>
      {unread && (
        <Box
          role="img"
          aria-label={t('notifications.unread')}
          sx={{
            width: 10,
            height: 10,
            borderRadius: '50%',
            bgcolor: 'primary.main',
            flexShrink: 0,
            mt: 1,
          }}
        />
      )}
    </ListItemButton>
  );
}
