import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Radio from '@mui/material/Radio';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import {
  listPaymentExceptionCandidates,
  type PaymentExceptionCandidate,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import { QueueError } from '../insurer-proposals/common';
import { formatReference } from '../payments/formatReference';
import { paymentExceptionCandidatesKey, PaymentStatusChip } from './common';
import { formatStamp } from './format';

/**
 * Pagamentos que podem ser associados a uma excepção (`LINKED`, S5-12 §4): escolhe-se uma linha.
 * As etiquetas dizem se a referência e o montante coincidem com o dinheiro recebido.
 */
export function CandidatesTable({
  exceptionId,
  selected,
  onSelect,
  error,
}: {
  exceptionId: string;
  selected: string | undefined;
  onSelect(candidate: PaymentExceptionCandidate): void;
  /** Texto de erro (ex.: "Escolha o pagamento a associar."). */
  error?: string;
}) {
  const { t, i18n } = useTranslation();
  const query = useQuery({
    queryKey: paymentExceptionCandidatesKey(exceptionId),
    queryFn: ({ signal }) => listPaymentExceptionCandidates(exceptionId, undefined, signal),
  });

  if (query.isLoading) return <Skeleton variant="rectangular" height={88} />;
  if (query.isError) {
    return <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  }
  const candidates = query.data?.candidates ?? [];
  if (candidates.length === 0) {
    return <Alert severity="info">{t('paymentExceptions.candidates.empty')}</Alert>;
  }

  return (
    <Stack spacing={1}>
      <TableContainer
        sx={{
          maxWidth: '100%',
          overflowX: 'auto',
          border: 1,
          borderColor: 'divider',
          borderRadius: 1,
        }}
      >
        <Table size="small" aria-label={t('paymentExceptions.candidates.title')}>
          <TableHead>
            <TableRow>
              <TableCell padding="checkbox" />
              <TableCell>{t('paymentExceptions.candidates.reference')}</TableCell>
              <TableCell>{t('paymentExceptions.candidates.proposal')}</TableCell>
              <TableCell align="right">{t('paymentExceptions.candidates.amount')}</TableCell>
              <TableCell>{t('paymentExceptions.candidates.status')}</TableCell>
              <TableCell>{t('paymentExceptions.candidates.bankReference')}</TableCell>
              <TableCell>{t('paymentExceptions.candidates.createdAt')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {candidates.map((c) => (
              <TableRow
                key={c.id}
                hover
                selected={selected === c.id}
                onClick={() => onSelect(c)}
                sx={{ cursor: 'pointer' }}
                data-testid="candidate-row"
              >
                <TableCell padding="checkbox">
                  <Radio
                    checked={selected === c.id}
                    onChange={() => onSelect(c)}
                    slotProps={{
                      input: {
                        'aria-label': t('paymentExceptions.candidates.select', {
                          reference: c.reference,
                        }),
                      },
                    }}
                  />
                </TableCell>
                <TableCell>
                  <Stack spacing={0.5} alignItems="flex-start">
                    <span>{c.reference}</span>
                    {c.matchesReference && (
                      <Chip
                        size="small"
                        color="success"
                        variant="outlined"
                        label={t('paymentExceptions.candidates.sameReference')}
                      />
                    )}
                  </Stack>
                </TableCell>
                <TableCell>{c.targetLabel}</TableCell>
                <TableCell align="right">
                  <Stack spacing={0.5} alignItems="flex-end">
                    <Box
                      component="span"
                      sx={{ fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' }}
                    >
                      {formatMoney(c.amount, i18n.language)}
                    </Box>
                    {c.matchesAmount && (
                      <Chip
                        size="small"
                        color="success"
                        variant="outlined"
                        label={t('paymentExceptions.candidates.sameAmount')}
                      />
                    )}
                  </Stack>
                </TableCell>
                <TableCell>
                  <PaymentStatusChip status={c.status} />
                </TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>
                  {c.bankReference ? formatReference(c.bankReference) : '—'}
                </TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatStamp(c.createdAt)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {error && (
        <Alert severity="error" role="alert">
          {error}
        </Alert>
      )}
    </Stack>
  );
}
