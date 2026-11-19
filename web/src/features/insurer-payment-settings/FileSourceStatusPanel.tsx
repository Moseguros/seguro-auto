import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { getPaymentFileSourceStatus } from '../../api/generated';
import { Field, QueueError } from '../insurer-proposals/common';
import {
  filesPath,
  paymentFileSourceKey,
  paymentSettingsPath,
  useCodeText,
} from '../insurer-payments/common';
import { formatStamp } from '../insurer-payments/format';

/**
 * Estado da leitura dos ficheiros do banco (S5-12 §2): no cartão `BANK_REFERENCE` da configuração
 * (`placement="settings"`, com a ligação "Ver ficheiros") e no topo do separador "Ficheiros".
 */
export function FileSourceStatusPanel({ placement }: { placement: 'settings' | 'files' }) {
  const { t } = useTranslation();
  const codeText = useCodeText();
  const query = useQuery({
    queryKey: paymentFileSourceKey,
    queryFn: ({ signal }) => getPaymentFileSourceStatus(undefined, signal),
  });

  if (query.isLoading) return <Skeleton variant="rectangular" height={96} />;
  if (query.isError) {
    return <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  }
  const source = query.data;
  if (!source) return null;
  if (!source.configured) {
    if (placement === 'settings') return null;
    return (
      <Alert severity="info">
        {t('paymentSettingsPage.fileSource.notConfigured')}{' '}
        <Link component={RouterLink} to={paymentSettingsPath}>
          {t('paymentSettingsPage.fileSource.configure')}
        </Link>
      </Alert>
    );
  }

  const outcome = source.lastPollOutcome;
  return (
    <Paper
      variant="outlined"
      component="section"
      aria-label={t('paymentSettingsPage.fileSource.title')}
      sx={{ p: 2, minWidth: 0 }}
    >
      <Stack spacing={1.5}>
        <Typography variant="subtitle1" component="h3" fontWeight={600}>
          {t('paymentSettingsPage.fileSource.title')}
        </Typography>
        {source.staleSince && (
          <Alert severity="warning">
            {t('paymentSettingsPage.fileSource.stale', { date: formatStamp(source.staleSince) })}
          </Alert>
        )}
        <Box
          component="dl"
          sx={{
            display: 'grid',
            gap: 1.5,
            gridTemplateColumns: { xs: 'minmax(0, 1fr)', sm: 'repeat(3, minmax(0, 1fr))' },
            m: 0,
          }}
        >
          <Field label={t('paymentSettingsPage.fileSource.lastPoll')}>
            {source.lastPollAt ? (
              <Stack direction="row" spacing={1} alignItems="center">
                <span>{formatStamp(source.lastPollAt)}</span>
                {outcome && (
                  <Chip
                    size="small"
                    variant="outlined"
                    color={outcome === 'OK' ? 'success' : 'error'}
                    label={t(`paymentSettingsPage.fileSource.outcome.${outcome}`)}
                  />
                )}
              </Stack>
            ) : (
              t('paymentSettingsPage.fileSource.never')
            )}
          </Field>
          {source.lastPollError && (
            <Field label={t('paymentSettingsPage.fileSource.error')}>
              {codeText('pollErrorCode', source.lastPollError)}
            </Field>
          )}
          <Field label={t('paymentSettingsPage.fileSource.lastSuccess')}>
            {formatStamp(source.lastSuccessAt)}
          </Field>
          <Field label={t('paymentSettingsPage.fileSource.format')}>{source.fileFormat}</Field>
          <Field label={t('paymentSettingsPage.fileSource.entity')}>{source.entity}</Field>
        </Box>
        {source.staleAfterMinutes !== undefined && (
          <Typography variant="body2" color="text.secondary">
            {t('paymentSettingsPage.fileSource.note', { minutes: source.staleAfterMinutes })}
          </Typography>
        )}
        {placement === 'settings' && (
          <Link component={RouterLink} to={filesPath} sx={{ alignSelf: 'flex-start' }}>
            {t('paymentSettingsPage.fileSource.viewFiles')}
          </Link>
        )}
      </Stack>
    </Paper>
  );
}
