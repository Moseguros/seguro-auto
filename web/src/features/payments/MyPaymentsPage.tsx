import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { getListMyPaymentsQueryKey, listMyPayments, type IntentView } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { formatDateTimeNumeric } from '../../i18n/format';
import { PAGE_SIZE_PAYMENTS, nextPaymentsPage } from './myPayments';
import { paymentPaths, retryNetworkOnly } from './paymentPaths';
import { paymentStatusColor } from './PaymentIntentPage';

const params = { size: PAGE_SIZE_PAYMENTS, sort: 'createdAt,desc' } as const;

function PaymentCard({ intent }: { intent: IntentView }) {
  const { t, i18n } = useTranslation();
  return (
    <Card variant="outlined" elevation={0}>
      <CardActionArea component={RouterLink} to={paymentPaths.intent(intent.id)}>
        <CardContent>
          <Stack spacing={0.5}>
            <Stack direction="row" justifyContent="space-between" spacing={1} alignItems="center">
              <Typography fontWeight={600} sx={{ wordBreak: 'break-word' }}>
                {intent.target.label}
              </Typography>
              <Chip
                size="small"
                color={paymentStatusColor(intent.status)}
                label={t(`paymentStatus.${intent.status}`)}
              />
            </Stack>
            <Typography variant="body2" color="text.secondary">
              {formatDateTimeNumeric(intent.createdAt)} · {intent.insurer.displayName ?? ''}
            </Typography>
            <Stack direction="row" justifyContent="space-between" spacing={1}>
              <Typography variant="body2">{t(`paymentMethod.${intent.method}`)}</Typography>
              <Typography fontWeight={600} sx={{ fontVariantNumeric: 'tabular-nums' }}>
                {formatMoney(intent.amount, i18n.language)}
              </Typography>
            </Stack>
          </Stack>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}

function PaymentsTable({ items }: { items: IntentView[] }) {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  return (
    <TableContainer component={Card} variant="outlined" elevation={0}>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>{t('payments.list.date')}</TableCell>
            <TableCell>{t('payments.list.target')}</TableCell>
            <TableCell>{t('payments.list.insurer')}</TableCell>
            <TableCell>{t('payments.list.method')}</TableCell>
            <TableCell align="right">{t('payments.list.amount')}</TableCell>
            <TableCell>{t('payments.list.status')}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {items.map((intent) => (
            <TableRow
              key={intent.id}
              hover
              sx={{ cursor: 'pointer' }}
              onClick={() => navigate(paymentPaths.intent(intent.id))}
            >
              <TableCell>{formatDateTimeNumeric(intent.createdAt)}</TableCell>
              <TableCell>
                <RouterLink to={paymentPaths.intent(intent.id)}>{intent.target.label}</RouterLink>
              </TableCell>
              <TableCell>{intent.insurer.displayName ?? ''}</TableCell>
              <TableCell>{t(`paymentMethod.${intent.method}`)}</TableCell>
              <TableCell align="right" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                {formatMoney(intent.amount, i18n.language)}
              </TableCell>
              <TableCell>
                <Chip
                  size="small"
                  color={paymentStatusColor(intent.status)}
                  label={t(`paymentStatus.${intent.status}`)}
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

/** `/cliente/pagamentos` (S5-11 §5): os pagamentos do cliente, os mais recentes primeiro. */
export function MyPaymentsPage() {
  const { t } = useTranslation();
  const wide = useMediaQuery('(min-width:900px)');
  const query = useInfiniteQuery({
    queryKey: [...getListMyPaymentsQueryKey(params), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listMyPayments({ ...params, page: pageParam }, undefined, signal),
    initialPageParam: 0,
    getNextPageParam: nextPaymentsPage,
    retry: retryNetworkOnly,
  });

  let content;
  if (query.isPending) {
    content = <LoadingScreen />;
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
        <Typography color="text.secondary">{t('payments.list.empty')}</Typography>
      ) : (
        <Stack spacing={2}>
          {wide ? (
            <PaymentsTable items={items} />
          ) : (
            <Stack spacing={1.5}>
              {items.map((intent) => (
                <PaymentCard key={intent.id} intent={intent} />
              ))}
            </Stack>
          )}
          {query.hasNextPage && (
            <Button
              variant="outlined"
              onClick={() => void query.fetchNextPage()}
              disabled={query.isFetchingNextPage}
              startIcon={query.isFetchingNextPage ? <CircularProgress size={16} /> : undefined}
              sx={{ alignSelf: 'center' }}
            >
              {t('payments.list.loadMore')}
            </Button>
          )}
        </Stack>
      );
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4" component="h1">
        {t('payments.list.title')}
      </Typography>
      {content}
    </Stack>
  );
}
