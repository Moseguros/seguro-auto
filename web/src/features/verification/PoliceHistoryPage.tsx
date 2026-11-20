import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Link from '@mui/material/Link';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { listMyPoliceChecks } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { todayInMaputo } from '../../format/dates';
import { formatDateTime } from '../../i18n/format';
import { VerificationStatusLabel } from './VerificationResultCard';
import { VerifyColumn } from './common';

const pageSize = 20;

/** `yyyy-MM-dd` de `days` dias antes do dia de hoje em Maputo. */
export function daysBefore(today: string, days: number): string {
  const date = new Date(`${today}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() - days);
  return date.toISOString().slice(0, 10);
}

/** `/verificar/historico` (S6-05 §5): as verificações do próprio agente, por período. */
export function PoliceHistoryPage() {
  const { t, i18n } = useTranslation();
  const today = todayInMaputo();
  const [from, setFrom] = useState(() => daysBefore(today, 30));
  const [to, setTo] = useState(today);

  const query = useInfiniteQuery({
    queryKey: ['police-check-history', from, to],
    queryFn: ({ pageParam, signal }) =>
      listMyPoliceChecks(
        {
          from: from || undefined,
          to: to || undefined,
          page: pageParam,
          size: pageSize,
          sort: 'checkedAt,desc',
        },
        undefined,
        signal,
      ),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
  });
  const rows = query.data?.pages.flatMap((p) => p.content) ?? [];

  let body;
  if (query.isLoading) {
    body = (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress aria-label={t('auth.loading')} />
      </Box>
    );
  } else if (query.isError && !query.data) {
    body = (
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
  } else if (rows.length === 0) {
    body = (
      <Paper variant="outlined" sx={{ p: 3, textAlign: 'center' }}>
        <Typography>{t('police.history.empty')}</Typography>
      </Paper>
    );
  } else {
    body = (
      <Paper variant="outlined">
        <List aria-label={t('police.history.title')} disablePadding>
          {rows.map((row) => (
            <ListItem key={row.id} divider data-testid="police-check">
              <Stack spacing={0.5} sx={{ minWidth: 0, width: '100%' }}>
                <Stack direction="row" justifyContent="space-between" spacing={1} flexWrap="wrap">
                  <Typography variant="body2" fontWeight={600}>
                    {formatDateTime(row.checkedAt, i18n.language)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {t(`subjectType.${row.subjectType}`)}
                    {row.plateMasked ? ` · ${row.plateMasked}` : ''}
                  </Typography>
                </Stack>
                <VerificationStatusLabel status={row.result} />
              </Stack>
            </ListItem>
          ))}
        </List>
      </Paper>
    );
  }

  return (
    <VerifyColumn>
      <Stack spacing={2}>
        <Typography variant="h5" component="h1">
          {t('police.history.title')}
        </Typography>
        <Stack direction="row" spacing={2}>
          <TextField
            type="date"
            size="small"
            label={t('police.history.from')}
            value={from}
            onChange={(event) => setFrom(event.target.value)}
            slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: to || undefined } }}
            fullWidth
          />
          <TextField
            type="date"
            size="small"
            label={t('police.history.to')}
            value={to}
            onChange={(event) => setTo(event.target.value)}
            slotProps={{ inputLabel: { shrink: true }, htmlInput: { min: from || undefined } }}
            fullWidth
          />
        </Stack>
        {body}
        {query.hasNextPage && (
          <Button
            variant="outlined"
            onClick={() => void query.fetchNextPage()}
            disabled={query.isFetchingNextPage}
            sx={{ alignSelf: 'center' }}
          >
            {t('police.history.loadMore')}
          </Button>
        )}
        <Link component={RouterLink} to="/verificar">
          {t('police.history.back')}
        </Link>
      </Stack>
    </VerifyColumn>
  );
}
