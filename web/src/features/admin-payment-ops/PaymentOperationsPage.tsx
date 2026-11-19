import Refresh from '@mui/icons-material/Refresh';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { getPaymentWorkerStatus, listPaymentFileSources } from '../../api/generated';
import { QueueError, QueueSkeleton } from '../insurer-proposals/common';
import { SourcesTable } from './SourcesTable';
import { WorkerStatusCard } from './WorkerStatusCard';

/** Intervalo da actualização automática. */
export const operationsRefreshMs = 30_000;

/** `/admin/operacoes/pagamentos`: filas do worker e fontes de ficheiros (S5-12 §6). */
export function PaymentOperationsPage() {
  const { t } = useTranslation();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const worker = useQuery({
    queryKey: ['admin-payment-worker-status'],
    queryFn: ({ signal }) => getPaymentWorkerStatus(undefined, signal),
    refetchInterval: operationsRefreshMs,
  });
  const sources = useQuery({
    queryKey: ['admin-payment-file-sources'],
    queryFn: ({ signal }) => listPaymentFileSources(undefined, signal),
    refetchInterval: operationsRefreshMs,
  });

  const refresh = () => {
    void worker.refetch();
    void sources.refetch();
  };

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ xs: 'flex-start', sm: 'center' }}
      >
        <Typography variant="h4" component="h1">
          {t('adminPaymentOps.title')}
        </Typography>
        <Button
          variant="outlined"
          startIcon={<Refresh />}
          onClick={refresh}
          disabled={worker.isFetching || sources.isFetching}
        >
          {t('adminPaymentOps.refresh')}
        </Button>
      </Stack>
      {worker.isLoading ? (
        <QueueSkeleton />
      ) : worker.isError && !worker.data ? (
        <QueueError error={worker.error} onRetry={() => void worker.refetch()} />
      ) : worker.data ? (
        <WorkerStatusCard status={worker.data} narrow={narrow} />
      ) : null}
      {sources.isLoading ? (
        <QueueSkeleton />
      ) : sources.isError && !sources.data ? (
        <QueueError error={sources.error} onRetry={() => void sources.refetch()} />
      ) : sources.data ? (
        <SourcesTable sources={sources.data.sources} narrow={narrow} />
      ) : null}
    </Stack>
  );
}
