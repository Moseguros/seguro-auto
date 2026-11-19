import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { PaymentQueueStatus, PaymentWorkerStatus } from '../../api/generated';
import { formatStamp } from '../insurer-payments/format';

/** Fila de mensagens que falharam o processamento. */
export function isDeadLetter(queue: Pick<PaymentQueueStatus, 'name'>): boolean {
  return queue.name.endsWith('.dlq');
}

/** Problema a destacar numa fila: `.dlq` com mensagens, ou fila principal sem consumidores. */
export function queueProblem(queue: PaymentQueueStatus): 'dlq' | 'noConsumers' | null {
  if (isDeadLetter(queue)) return queue.messages > 0 ? 'dlq' : null;
  return queue.consumers === 0 ? 'noConsumers' : null;
}

/** Estado do broker e das filas do worker (S5-12 §6). */
export function WorkerStatusCard({
  status,
  narrow,
}: {
  status: PaymentWorkerStatus;
  narrow: boolean;
}) {
  const { t } = useTranslation();
  const count = (value: number) => (value < 0 ? t('adminPaymentOps.worker.missing') : value);
  const problemText = (queue: PaymentQueueStatus) => {
    const problem = queueProblem(queue);
    if (problem === 'dlq') return t('adminPaymentOps.worker.dlqAlert');
    if (problem === 'noConsumers') return t('adminPaymentOps.worker.noConsumers');
    return undefined;
  };

  let queues;
  if (status.queues.length === 0) {
    queues = <Typography variant="body2">{t('adminPaymentOps.worker.noQueues')}</Typography>;
  } else if (narrow) {
    queues = (
      <Stack spacing={1.5} component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {status.queues.map((queue) => {
          const problem = queueProblem(queue);
          const text = problemText(queue);
          return (
            <li key={queue.name}>
              <Card
                variant="outlined"
                data-testid="queue"
                data-problem={problem ?? undefined}
                sx={
                  problem ? { borderColor: problem === 'dlq' ? 'error.main' : 'warning.main' } : {}
                }
              >
                <CardContent>
                  <Typography fontWeight={600} sx={{ wordBreak: 'break-all' }}>
                    {queue.name}
                  </Typography>
                  <Typography variant="body2">
                    {t('adminPaymentOps.worker.messages')}: {count(queue.messages)} ·{' '}
                    {t('adminPaymentOps.worker.consumers')}: {count(queue.consumers)}
                  </Typography>
                  {text && (
                    <Typography
                      variant="body2"
                      color={problem === 'dlq' ? 'error' : 'warning.main'}
                    >
                      {text}
                    </Typography>
                  )}
                </CardContent>
              </Card>
            </li>
          );
        })}
      </Stack>
    );
  } else {
    queues = (
      <TableContainer sx={{ maxWidth: '100%', overflowX: 'auto' }}>
        <Table size="small" aria-label={t('adminPaymentOps.worker.title')}>
          <TableHead>
            <TableRow>
              <TableCell>{t('adminPaymentOps.worker.queue')}</TableCell>
              <TableCell align="right">{t('adminPaymentOps.worker.messages')}</TableCell>
              <TableCell align="right">{t('adminPaymentOps.worker.consumers')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {status.queues.map((queue) => {
              const problem = queueProblem(queue);
              const text = problemText(queue);
              const color = problem === 'dlq' ? 'error.main' : problem ? 'warning.dark' : undefined;
              return (
                <TableRow
                  key={queue.name}
                  data-testid="queue"
                  data-problem={problem ?? undefined}
                  sx={
                    problem
                      ? {
                          bgcolor: (theme) =>
                            problem === 'dlq'
                              ? `${theme.palette.error.main}14`
                              : `${theme.palette.warning.main}14`,
                        }
                      : undefined
                  }
                >
                  <TableCell sx={{ color }}>
                    <Box component="span" sx={{ fontWeight: problem ? 600 : 400 }}>
                      {queue.name}
                    </Box>
                    {text && (
                      <Typography variant="body2" sx={{ color }}>
                        {text}
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell align="right" sx={{ color, fontVariantNumeric: 'tabular-nums' }}>
                    {count(queue.messages)}
                  </TableCell>
                  <TableCell align="right" sx={{ color, fontVariantNumeric: 'tabular-nums' }}>
                    {count(queue.consumers)}
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
    );
  }

  return (
    <Paper
      variant="outlined"
      component="section"
      aria-label={t('adminPaymentOps.worker.title')}
      sx={{ p: 2, minWidth: 0 }}
    >
      <Stack spacing={2}>
        <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
          <Typography variant="h6" component="h2" sx={{ mr: 1 }}>
            {t('adminPaymentOps.worker.title')}
          </Typography>
          <Chip
            size="small"
            color={status.brokerReachable ? 'success' : 'error'}
            label={t(
              status.brokerReachable
                ? 'adminPaymentOps.worker.brokerUp'
                : 'adminPaymentOps.worker.brokerDown',
            )}
          />
        </Stack>
        {queues}
        <Typography variant="body2" color="text.secondary">
          {status.lastMessageAt
            ? t('adminPaymentOps.worker.lastMessage', { date: formatStamp(status.lastMessageAt) })
            : t('adminPaymentOps.worker.noMessages')}
        </Typography>
      </Stack>
    </Paper>
  );
}
