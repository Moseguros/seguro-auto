import Settings from '@mui/icons-material/Settings';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CircularProgress from '@mui/material/CircularProgress';
import List from '@mui/material/List';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import {
  countMyUnreadNotifications,
  getListMyNotificationsQueryKey,
  listMyNotifications,
  markAllMyNotificationsRead,
  type ListMyNotificationsParams,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import {
  NOTIFICATIONS_PAGE_SIZE,
  UNREAD_COUNT_REFRESH_MS,
  nextNotificationsPage,
  notificationsListKey,
  unreadCountKey,
} from './notificationQueries';
import { NotificationItem } from './NotificationItem';

export const preferencesPath = '/cliente/notificacoes/preferencias';

type Filter = 'all' | 'unread';

function NotificationSkeletons() {
  const { t } = useTranslation();
  return (
    <Stack spacing={1} aria-label={t('auth.loading')}>
      {[0, 1, 2].map((index) => (
        <Stack key={index} spacing={0.5} sx={{ p: 2 }} data-testid="notification-skeleton">
          <Skeleton variant="text" width="60%" />
          <Skeleton variant="text" width="90%" />
        </Stack>
      ))}
    </Stack>
  );
}

/** `/cliente/notificacoes` (S6-06 §4): o centro de notificações do cliente. */
export function NotificationsPage() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [search, setSearch] = useSearchParams();
  const filter: Filter = search.get('ver') === 'por-ler' ? 'unread' : 'all';
  const params: ListMyNotificationsParams = {
    ...(filter === 'unread' ? { unreadOnly: true } : {}),
    size: NOTIFICATIONS_PAGE_SIZE,
  };
  const query = useInfiniteQuery({
    queryKey: [...getListMyNotificationsQueryKey(params), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listMyNotifications({ ...params, page: pageParam }, undefined, signal),
    initialPageParam: 0,
    getNextPageParam: nextNotificationsPage,
  });
  // A mesma consulta do sino (partilham a cache).
  const unread = useQuery({
    queryKey: unreadCountKey,
    queryFn: ({ signal }) => countMyUnreadNotifications(undefined, signal),
    refetchInterval: UNREAD_COUNT_REFRESH_MS,
    retry: false,
  });
  const markAll = useMutation({
    mutationFn: () => markAllMyNotificationsRead(),
    onSettled: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: unreadCountKey }),
        queryClient.invalidateQueries({ queryKey: notificationsListKey }),
      ]);
    },
  });

  let content: ReactNode;
  if (query.isPending) {
    content = <NotificationSkeletons />;
  } else if (query.isError) {
    content = (
      <Alert
        severity="error"
        role="alert"
        action={
          <Button color="inherit" size="small" onClick={() => void query.refetch()}>
            {t('common.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(query.error))}
      </Alert>
    );
  } else {
    const items = query.data.pages.flatMap((p) => p.content);
    content =
      items.length === 0 ? (
        <Typography color="text.secondary" sx={{ py: 4, textAlign: 'center' }}>
          {t('notifications.empty')}
        </Typography>
      ) : (
        <Stack spacing={2}>
          <Card variant="outlined" elevation={0}>
            <List disablePadding sx={{ p: 0.5 }}>
              {items.map((notification) => (
                <NotificationItem key={notification.id} notification={notification} />
              ))}
            </List>
          </Card>
          {query.hasNextPage && (
            <Button
              variant="outlined"
              onClick={() => void query.fetchNextPage()}
              disabled={query.isFetchingNextPage}
              startIcon={query.isFetchingNextPage ? <CircularProgress size={16} /> : undefined}
              sx={{ alignSelf: 'center' }}
            >
              {t('notifications.loadMore')}
            </Button>
          )}
        </Stack>
      );
  }

  return (
    <Stack spacing={2}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" gap={1}>
        <Typography variant="h4" component="h1">
          {t('notifications.title')}
        </Typography>
        <Button component={RouterLink} to={preferencesPath} startIcon={<Settings />}>
          {t('notifications.preferences')}
        </Button>
      </Stack>
      <Stack
        direction="row"
        alignItems="center"
        justifyContent="space-between"
        gap={1}
        flexWrap="wrap"
      >
        <Tabs
          value={filter}
          onChange={(_, value: Filter) =>
            setSearch(value === 'unread' ? { ver: 'por-ler' } : {}, { replace: true })
          }
        >
          <Tab value="all" label={t('notifications.tabs.all')} />
          <Tab value="unread" label={t('notifications.tabs.unread')} />
        </Tabs>
        {(unread.data?.count ?? 0) > 0 && (
          <Button
            variant="text"
            onClick={() => markAll.mutate()}
            disabled={markAll.isPending}
            startIcon={markAll.isPending ? <CircularProgress size={16} /> : undefined}
          >
            {t('notifications.markAllRead')}
          </Button>
        )}
      </Stack>
      {markAll.isError && (
        <Alert severity="error" role="alert">
          {problemMessage(t, getProblem(markAll.error))}
        </Alert>
      )}
      {content}
    </Stack>
  );
}
